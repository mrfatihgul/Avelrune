package com.avelrune.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                        .title("Avelrune Auth API")
                        .version("1.0.0")
                        .description("Kimlik doğrulama ve kullanıcı uçları."))
                .servers(List.of(new Server().url("/").description("Traefik")))
                .components(new Components().addSecuritySchemes("bearer-jwt", bearerJwt()))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    private static SecurityScheme bearerJwt() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
