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
                        // 👉 Archivos públicos del frontend
                        .requestMatchers(
                                "/index.html", "/login.html", "/selector.html", "/formulario.html", "/firma.html", "/resumen.html",
                                "/css/**", "/js/**", "/images/**", "/", "/favicon.ico"
                        ).permitAll()

                        // 👉 APIs abiertas
                        .requestMatchers("/api/auth/login", "/api/usuarios/crear").permitAll()

                        // 👉 APIs restringidas
                        .requestMatchers("/api/contratos/crear").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers("/api/contratos/**").hasRole("ADMIN")

                        // 👉 Todo lo demás necesita autenticación
                        .anyRequest().authenticated()
                );

        return http.build();
    }


}
