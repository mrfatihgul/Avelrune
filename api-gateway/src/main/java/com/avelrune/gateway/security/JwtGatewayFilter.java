package com.avelrune.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private static final String USER_HEADER = "X-Authenticated-User";
    private static final String ROLE_HEADER = "X-Authenticated-Role";
    private static final String USER_ID_HEADER = "X-Authenticated-User-Id";

    private final JwtPublicKeyService publicKeyService;

    public JwtGatewayFilter(JwtPublicKeyService publicKeyService) {
        this.publicKeyService = publicKeyService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        String path = request.getURI().getPath();

        ServerHttpRequest.Builder builder = request.mutate().headers(headers -> {
            headers.remove(USER_HEADER);
            headers.remove(ROLE_HEADER);
            headers.remove(USER_ID_HEADER);
        });

        if (isPublic(method, path)) {
            if (isOptionalJwt(method, path)) {
                return attachOptionalJwt(exchange, chain, builder, request);
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return unauthorized(exchange, "Giriş yapmanız gerekiyor.");
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKeyService.getPublicKey())
                    .build()
                    .parseSignedClaims(header.substring(7))
                    .getPayload();
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            if (email == null || email.isBlank()) {
                return unauthorized(exchange, "Oturum geçersiz.");
            }
            if (requiresAdmin(method, path) && !"ADMIN".equals(role)) {
                return forbidden(exchange);
            }
            builder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
            builder.header(USER_HEADER, email);
            if (role != null && !role.isBlank()) {
                builder.header(ROLE_HEADER, role);
            }
            Object uid = claims.get("uid");
            if (uid != null) {
                builder.header(USER_ID_HEADER, String.valueOf(uid));
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        } catch (JwtException | IllegalArgumentException ex) {
            return unauthorized(exchange, "Oturum geçersiz veya süresi doldu.");
        }
    }

    private boolean isPublic(HttpMethod method, String path) {
        if (method == HttpMethod.OPTIONS) {
            return true;
        }
        if (path.startsWith("/actuator")) {
            return true;
        }
        if (method == HttpMethod.POST && (pathEquals(path, "/api/auth/login") || pathEquals(path, "/api/auth/register"))) {
            return true;
        }
        if (pathEquals(path, "/api/properties/mine") || path.startsWith("/api/properties/owned")) {
            return false;
        }
        if (isOptionalJwt(method, path)) {
            return true;
        }
        if (method == HttpMethod.GET && (pathEquals(path, "/api/properties") || path.startsWith("/api/properties/"))) {
            return true;
        }
        if (method == HttpMethod.GET && (pathEquals(path, "/api/agents") || path.startsWith("/api/agents/"))) {
            return true;
        }
        return method == HttpMethod.POST && pathEquals(path, "/api/requests");
    }

    private boolean requiresAdmin(HttpMethod method, String path) {
        if (path.startsWith("/api/admin")) {
            return true;
        }
        if (path.startsWith("/api/auth/users")) {
            return true;
        }
        if (path.startsWith("/api/requests/property") || pathEquals(path, "/api/requests/summary")) {
            return false;
        }
        if (method == HttpMethod.PATCH && path.matches("/api/requests/\\d+/read/?")) {
            return false;
        }
        if (pathEquals(path, "/api/requests") || path.startsWith("/api/requests/")) {
            return method != HttpMethod.POST || !pathEquals(path, "/api/requests");
        }
        return false;
    }

    private boolean isOptionalJwt(HttpMethod method, String path) {
        return method == HttpMethod.POST && pathEquals(path, "/api/requests");
    }

    private Mono<Void> attachOptionalJwt(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            ServerHttpRequest.Builder builder,
            ServerHttpRequest request
    ) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKeyService.getPublicKey())
                    .build()
                    .parseSignedClaims(header.substring(7))
                    .getPayload();
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            builder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
            if (email != null && !email.isBlank()) {
                builder.header(USER_HEADER, email);
            }
            if (role != null && !role.isBlank()) {
                builder.header(ROLE_HEADER, role);
            }
            Object uid = claims.get("uid");
            if (uid != null) {
                builder.header(USER_ID_HEADER, String.valueOf(uid));
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        } catch (JwtException | IllegalArgumentException ex) {
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
    }

    private boolean pathEquals(String path, String expected) {
        if (path.equals(expected)) {
            return true;
        }
        return path.equals(expected + "/");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeJson(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        return writeJson(exchange, HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz yok.");
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, String message) {
        byte[] body = """
                {"status":%d,"error":"%s","message":"%s"}
                """.formatted(status.value(), status.getReasonPhrase(), message)
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(body))
        );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
