package com.ejemplo.aplicacion.configuracion;

import com.ejemplo.aplicacion.seguridad.FiltroJwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SeguridadConfig {

    private final FiltroJwt filtroJwt;

    public SeguridadConfig(FiltroJwt filtroJwt) {
        this.filtroJwt = filtroJwt;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())  // 🔁 Usa la configuración ya definida en ConfiguracionCORS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/formulario.html", "/selector.html",
                                "/firma.html", "/resumen.html", "/verContratos.html", "/panelRoot.html",
                                "/css/**", "/js/**",
                                "/api/auth/login", "/api/usuarios/crear"
                        ).permitAll()
                        .requestMatchers("/api/contratos/crear").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/contratos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
