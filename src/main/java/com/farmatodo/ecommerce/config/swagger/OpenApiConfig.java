package com.farmatodo.ecommerce.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                .packagesToScan("com.farmatodo.ecommerce")
                .pathsToMatch("/**")
                .pathsToExclude("/error")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ecommerce - Tokenization API")
                        .version("v1")
                        .description("APIs para tokenización de tarjetas, clientes, productos y pedidos.")
                        .contact(new Contact().name("Equipo Ecommerce").email("dev@empresa.com")))
                .components(new Components().addSecuritySchemes("apiKeyAuth",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("X-API-KEY")))
                .addSecurityItem(new SecurityRequirement().addList("apiKeyAuth"));
    }
}
