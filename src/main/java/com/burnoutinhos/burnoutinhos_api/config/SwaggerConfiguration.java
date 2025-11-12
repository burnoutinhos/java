package com.burnoutinhos.burnoutinhos_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    OpenAPI configurarSwagger() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Burnoutinhos")
                    .description("Toma cuidado com sua saúde meu!")
                    .summary(
                        "API para organização pessoa com inteligência artificial"
                    )
                    .version("v1.0.0")
                    .license(
                        new License()
                            .url("https://github.com/gsnimbus/java")
                            .name("Licença - GsNimbus - v1.0.0")
                    )
                    .termsOfService("Termos de Serviço")
            )
            .addSecurityItem(
                new SecurityRequirement().addList("Bearer Authentication")
            )
            .components(
                new Components().addSecuritySchemes(
                    "Bearer Authentication",
                    createAPIKeyScheme()
                )
            );
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer");
    }
}
