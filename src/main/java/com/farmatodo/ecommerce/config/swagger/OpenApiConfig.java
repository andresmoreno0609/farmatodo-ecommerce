package com.farmatodo.ecommerce.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi ecommerceApi() {
        return GroupedOpenApi.builder()
                .group("ecommerce")
                .packagesToScan("com.farmatodo.ecommerce") // muy importante
                .pathsToMatch("/**")
                .pathsToExclude("/error")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("apiKeyAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                ))
                // Hace que por defecto los endpoints pidan API Key;
                // /ping no quedará afectado porque está fuera de seguridad por config de Spring Security.
                .addSecurityItem(new SecurityRequirement().addList("apiKeyAuth"));
    }
}
