package com.unach.api_pp_sc_rp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Controller API") // Nombre del grupo para la documentación
                .packagesToScan("com.unach.api_pp_sc_rp.controller") // Paquete a escanear
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestion de Programas UANCH-CIV")
                        .version("1.0.0")
                        .description("API para la administración de Tareas Usando Spring Como BackEnd")
                        .termsOfService("https://example.com/terms") // Enlace a los términos del servicio
                        .contact(new Contact()
                                .name("Jose Colombio GOPE")
                                .url("https://example.com/contact")
                                .email("jose_000316@josegmail.com"))
                        .license(new License()
                                .name("Licencia de la API")
                                .url("https://unach.com/license")));
    }
}
