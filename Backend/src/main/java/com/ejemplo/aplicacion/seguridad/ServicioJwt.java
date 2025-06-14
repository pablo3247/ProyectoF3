package com.ejemplo.aplicacion.seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import com.ejemplo.aplicacion.modelo.Usuario;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class ServicioJwt {

    // Clave secreta convertida a Key para uso con JJWT (asegúrate que tenga al menos 32 bytes)
    private final Key claveSecreta = Keys.hmacShaKeyFor("una_clave_muy_larga_y_segura_de_mas_de_32_bytes!".getBytes(StandardCharsets.UTF_8));

    // Generar token
    public String generarToken(Usuario usuario) {
        String subject = usuario.getEmail() != null ? usuario.getEmail() : usuario.getTelefono();
        String rol = usuario.getRol();  // Puede ser null si no tiene rol asignado

        return Jwts.builder()
                .setSubject(subject)
                .claim("usuarioId", usuario.getId())
                .claim("rol", rol)  // Incluimos el rol en el token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hora
                .signWith(claveSecreta, SignatureAlgorithm.HS256)
                .compact();
    }


    // Validar token
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(claveSecreta)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extraer identificador (correo o teléfono)
    public String extraerIdentificador(String token) {
        return extraerReclamo(token, Claims::getSubject);
    }

    // Extraer rol (asegúrate de que el token tenga este claim)
    public String extraerRol(String token) {
        return extraerTodosReclamos(token).get("rol", String.class);
    }

    // Métodos auxiliares
    private Claims extraerTodosReclamos(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(claveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extraerReclamo(String token, Function<Claims, T> extractor) {
        final Claims claims = extraerTodosReclamos(token);
        return extractor.apply(claims);
    }
}
