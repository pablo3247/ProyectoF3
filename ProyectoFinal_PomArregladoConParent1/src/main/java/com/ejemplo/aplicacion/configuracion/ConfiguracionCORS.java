package com.ejemplo.aplicacion.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class ConfiguracionCORS {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (frontend)
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (puedes restringir si quieres)
        config.setAllowedHeaders(Arrays.asList("*"));

        // Permitir enviar credenciales (cookies, auth headers)
        config.setAllowCredentials(true);

        // Exponer headers que puedan necesitar ser accesibles en el cliente
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);

        // Prueba
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        return source;
    }
}
