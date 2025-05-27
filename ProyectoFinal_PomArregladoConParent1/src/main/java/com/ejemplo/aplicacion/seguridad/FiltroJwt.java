package com.ejemplo.aplicacion.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FiltroJwt extends OncePerRequestFilter {

    @Autowired
    private ServicioJwt servicioJwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Excluir rutas públicas del filtro, como /api/auth/login
        String ruta = request.getServletPath();
        if (ruta.equals("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String cabecera = request.getHeader("Authorization");

        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            String token = cabecera.substring(7);

            if (servicioJwt.validarToken(token)) {
                String identificador = servicioJwt.extraerIdentificador(token);
                String rol = servicioJwt.extraerRol(token);

                User usuarioSpring = new User(
                        identificador,
                        "", // No se necesita contraseña en este contexto
                        Collections.singleton(() -> rol)
                );

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                usuarioSpring, null, usuarioSpring.getAuthorities()
                        );

                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            }
        }

        // Continuar con el siguiente filtro de la cadena
        chain.doFilter(request, response);
    }
}
