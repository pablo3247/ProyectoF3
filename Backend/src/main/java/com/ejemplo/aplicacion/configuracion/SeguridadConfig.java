package com.ejemplo.aplicacion.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // Usamos el bean ya definido en ConfiguracionCORS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/formulario.html", "/selector.html",
                                "/css/**", "/js/**",
                                "/api/auth/login", "/api/usuarios/crear"
                        ).permitAll()
                        .requestMatchers("/api/contratos/crear").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers("/api/contratos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
