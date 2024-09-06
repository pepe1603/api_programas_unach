package com.unach.api_pp_sc_rp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//colocar aqui la direccion del Frontend
                .allowedOrigins("*")//rUTA de api , en este caso aceptamos todas las direcciones
                .allowedMethods("*");
    }
}
