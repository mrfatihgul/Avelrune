package com.avelrune.gateway.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PrivateNetworkAccessFilter implements WebFilter {

    static final String ALLOW_PRIVATE_NETWORK = "Access-Control-Allow-Private-Network";
    static final String ALLOW_LOCAL_NETWORK = "Access-Control-Allow-Local-Network";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        response.beforeCommit(() -> {
            HttpHeaders headers = response.getHeaders();
            headers.set(ALLOW_PRIVATE_NETWORK, "true");
            headers.set(ALLOW_LOCAL_NETWORK, "true");
            MediaType type = headers.getContentType();
            if (type != null && type.getCharset() == null && needsUtf8Charset(type)) {
                headers.setContentType(new MediaType(type, StandardCharsets.UTF_8));
            }
            return Mono.empty();
        });
        return chain.filter(exchange);
    }

    private static boolean needsUtf8Charset(MediaType type) {
        String subtype = type.getSubtype();
        return MediaType.APPLICATION_JSON.includes(type)
                || "yaml".equalsIgnoreCase(subtype)
                || "x-yaml".equalsIgnoreCase(subtype)
                || subtype.endsWith("+json")
                || subtype.endsWith("+yaml");
    }
}
