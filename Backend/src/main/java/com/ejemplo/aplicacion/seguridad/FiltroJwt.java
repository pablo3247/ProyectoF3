package com.ejemplo.aplicacion.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        String ruta = request.getServletPath();

        if (esRutaPublica(ruta)) {
            chain.doFilter(request, response);
            return;
        }

        String cabecera = request.getHeader("Authorization");

        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            String token = cabecera.substring(7);

            try {
                if (servicioJwt.validarToken(token)) {
                    String identificador = servicioJwt.extraerIdentificador(token);
                    String rolExtraido = servicioJwt.extraerRol(token);

                    if (rolExtraido == null || rolExtraido.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Rol no encontrado en token");
                        response.getWriter().flush();
                        return;
                    }

                    // Añadir prefijo ROLE_ si no lo tiene
                    String rol = rolExtraido.startsWith("ROLE_") ? rolExtraido : "ROLE_" + rolExtraido.toUpperCase();

                    User usuarioSpring = new User(
                            identificador,
                            "",
                            Collections.singleton(new SimpleGrantedAuthority(rol))
                    );

                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    usuarioSpring, null, usuarioSpring.getAuthorities()
                            );

                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                    chain.doFilter(request, response);
                    return;
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token inválido");
                    response.getWriter().flush();
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Error al procesar el token: " + e.getMessage());
                response.getWriter().flush();
                return;
            }

        } else {
            // No hay token -> bloquea acceso
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No se encontró token de autorización");
            response.getWriter().flush();
        }
    }

    private boolean esRutaPublica(String ruta) {
        return ruta.equals("/") ||
                ruta.equals("/index.html") ||
                ruta.equals("/selector.html") ||
                ruta.equals("/gestionarContratos.html") ||
                ruta.equals("/nuevoContrato.html") ||
                ruta.equals("/verContratos.html") ||
                ruta.equals("/resumen.html") ||
                ruta.equals("/firma.html") ||
                ruta.equals("/crearContratos.html") ||
                ruta.startsWith("/css/") ||
                ruta.startsWith("/js/") ||
                ruta.startsWith("/imagenes/") ||
                ruta.startsWith("/fonts/") ||
                ruta.equals("/favicon.ico") ||
                ruta.startsWith("/api/auth/login") ||
                ruta.equals("/formulario.html") ||
                ruta.startsWith("/api/usuarios/crear");
    }
}
