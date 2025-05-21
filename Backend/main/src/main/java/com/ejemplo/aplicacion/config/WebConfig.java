package com.ejemplo.aplicacion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica CORS a todas las rutas
                .allowedOrigins("http://localhost:3000") // Cambia según el puerto donde corre tu frontend React
                .allowedMethods("*") // Permite todos los métodos (GET, POST, etc.)
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
