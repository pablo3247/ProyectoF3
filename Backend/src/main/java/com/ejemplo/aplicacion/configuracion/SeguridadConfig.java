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
                        // Permitir acceso a login y a recursos estáticos (ajusta según tu estructura)
                        .requestMatchers("/Fronted/public/**", "/login.html").permitAll()
                        // Permitir crear usuario sin autenticar
                        .requestMatchers("/api/usuarios/crear").permitAll()
                        // Cualquier otra petición requiere autenticación
                        .requestMatchers("/api/auth/login").permitAll()

                        //Quitar esto y poner solo las paginas a las que accede!!!!
                        //Se ve que no permite a todas las rutas xd
                        .requestMatchers("*").permitAll()

                        // Permitir acceso libre a contratos
                        .requestMatchers("/api/contratos/**").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
