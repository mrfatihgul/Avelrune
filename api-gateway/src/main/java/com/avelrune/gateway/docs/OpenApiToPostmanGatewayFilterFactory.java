package com.avelrune.gateway.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OpenApiToPostmanGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBody;
    private final ObjectMapper objectMapper;

    public OpenApiToPostmanGatewayFilterFactory(
            ModifyResponseBodyGatewayFilterFactory modifyResponseBody,
            ObjectMapper objectMapper
    ) {
        super(Object.class);
        this.modifyResponseBody = modifyResponseBody;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Object config) {
        ModifyResponseBodyGatewayFilterFactory.Config bodyConfig = new ModifyResponseBodyGatewayFilterFactory.Config();
        bodyConfig.setInClass(String.class);
        bodyConfig.setOutClass(String.class);
        bodyConfig.setNewContentType("application/json;charset=UTF-8");
        bodyConfig.setRewriteFunction(rewrite());
        return modifyResponseBody.apply(bodyConfig);
    }

    private RewriteFunction<String, String> rewrite() {
        return (exchange, body) -> {
            if (body == null || body.isBlank() || !body.contains("\"openapi\"")) {
                return Mono.just(body == null ? "" : body);
            }
            try {
                JsonNode spec = objectMapper.readTree(body);
                return Mono.just(objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(PostmanCollectionConverter.fromOpenApi(spec, objectMapper)));
            } catch (Exception ex) {
                return Mono.just(body);
            }
        };
    }
}
