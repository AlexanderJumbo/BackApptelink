package com.reto.inventario_inteligente.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfiguration {
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); //"GET","POST"
        configuration.setAllowedHeaders(Arrays.asList("*"));//"Origin", "X-Requested-With","Accept","Authorization", "Cache-Control", "Content-Type")); // no incluye el header de autorización por ejemplo
        configuration.setAllowCredentials(true); //acepta cookies como sessionID, el header de autorizathion bearer token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);// indica para todos los controladores
        return source;
    }
}
