package com.ejemplo.aplicacion.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SeguridadConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SeguridadConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 💡 Rutas públicas (puedes añadir más si tienes .js, .css, etc.)
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/formulario.html",
                                "/selector.html",
                                "/css/**",
                                "/js/**",
                                "/api/auth/login",
                                "/api/usuarios/crear"
                        ).permitAll()
                        // 🔐 Rutas protegidas
                        .requestMatchers("/api/contratos/crear").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers("/api/contratos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

