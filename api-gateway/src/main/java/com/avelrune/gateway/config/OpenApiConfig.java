package com.avelrune.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Avelrune API Gateway")
                        .version("1.0.0")
                        .description("Gateway OpenAPI tanımı. Swagger UI bu serviste yoktur."))
                .servers(List.of(new Server().url("/").description("Traefik")));
    }
}
