package com.avelrune.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private static final String USER_HEADER = "X-Authenticated-User";
    private static final String ROLE_HEADER = "X-Authenticated-Role";
    private static final String USER_ID_HEADER = "X-Authenticated-User-Id";

    private final JwtPublicKeyService publicKeyService;
    private final ObjectMapper objectMapper;

    public JwtGatewayFilter(JwtPublicKeyService publicKeyService, ObjectMapper objectMapper) {
        this.publicKeyService = publicKeyService;
        this.objectMapper = objectMapper;
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
            Claims claims = parseClaims(header);
            String email = claims.getSubject();
            if (email == null || email.isBlank()) {
                return unauthorized(exchange, "Oturum geçersiz.");
            }
            String role = claims.get("role", String.class);
            if (requiresAdmin(method, path) && !"ADMIN".equals(role)) {
                return forbidden(exchange);
            }
            attachIdentity(builder, claims);
            return chain.filter(exchange.mutate().request(builder.build()).build());
        } catch (JwtException | IllegalArgumentException ex) {
            return unauthorized(exchange, "Oturum geçersiz veya süresi doldu.");
        }
    }

    private boolean isPublic(HttpMethod method, String path) {
        if (path.startsWith("/api/docs/") || path.startsWith("/actuator")) {
            return true;
        }
        if (method == HttpMethod.OPTIONS) {
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
        return false;
    }

    private boolean requiresAdmin(HttpMethod method, String path) {
        if (path.startsWith("/api/admin") || path.startsWith("/api/auth/users")) {
            return true;
        }
        if (pathEquals(path, "/api/inquiries/stats")) {
            return true;
        }
        return method == HttpMethod.GET && pathEquals(path, "/api/inquiries");
    }

    private boolean isOptionalJwt(HttpMethod method, String path) {
        return method == HttpMethod.POST && pathEquals(path, "/api/inquiries");
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
            attachIdentity(builder, parseClaims(header));
        } catch (JwtException | IllegalArgumentException ex) {
            // keep guest request
        }
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    private Claims parseClaims(String authorization) {
        return Jwts.parser()
                .verifyWith(publicKeyService.getPublicKey())
                .build()
                .parseSignedClaims(authorization.substring(7))
                .getPayload();
    }

    private void attachIdentity(ServerHttpRequest.Builder builder, Claims claims) {
        builder.headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION));
        String email = claims.getSubject();
        String role = claims.get("role", String.class);
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
    }

    private boolean pathEquals(String path, String expected) {
        return path.equals(expected) || path.equals(expected + "/");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeJson(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        return writeJson(exchange, HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz yok.");
    }

    private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", status.value());
        payload.put("error", status.getReasonPhrase());
        payload.put("message", message);
        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(payload);
        } catch (JsonProcessingException ex) {
            body = "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Yanıt üretilemedi.\"}"
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
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
