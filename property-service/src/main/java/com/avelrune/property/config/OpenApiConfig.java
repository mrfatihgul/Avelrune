package com.avelrune.property.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .openapi("3.0.0")
                .info(new Info()
                        .title("Avelrune Property API")
                        .version("1.0.0")
                        .description("İlan, danışman ve admin uçları."))
                .servers(List.of(
                        new Server().url("http://localhost").description("Traefik HTTP"),
                        new Server().url("https://localhost").description("Traefik HTTPS")))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerJwt()))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OpenApiCustomizer postmanCompatibleSpec() {
        return openApi -> {
            openApi.setOpenapi("3.0.0");
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((path, item) -> {
                if (item == null) {
                    return;
                }
                item.readOperations().forEach(operation -> {
                    if (operation.getParameters() != null) {
                        operation.setParameters(operation.getParameters().stream()
                                .filter(parameter -> parameter.getName() == null
                                        || !parameter.getName().startsWith("X-Authenticated-"))
                                .toList());
                    }
                });
            });
        };
    }

    private static SecurityScheme bearerJwt() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
