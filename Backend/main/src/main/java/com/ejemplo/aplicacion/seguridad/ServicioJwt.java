import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class ServicioJwt {

    private final String claveSecreta = "secreto-super-seguro"; // Reemplaza con una cadena segura

    // Generar token
    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getCorreo() != null ? usuario.getCorreo() : usuario.getTelefono())
                .claim("usuarioId", usuario.getId())
                .claim("rol", usuario.getRol())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hora
                .signWith(SignatureAlgorithm.HS256, claveSecreta)
                .compact();
    }

    // Validar token
    public boolean validarToken(String token) {
        try {
            Jwts.parser().setSigningKey(claveSecreta).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extraer identificador (correo o teléfono)
    public String extraerIdentificador(String token) {
        return extraerReclamo(token, Claims::getSubject);
    }

    // Extraer rol
    public String extraerRol(String token) {
        return extraerTodosReclamos(token).get("rol", String.class);
    }

    // Métodos auxiliares
    private Claims extraerTodosReclamos(String token) {
        return Jwts.parser().setSigningKey(claveSecreta).parseClaimsJws(token).getBody();
    }

    private <T> T extraerReclamo(String token, Function<Claims, T> extractor) {
        final Claims claims = extraerTodosReclamos(token);
        return extractor.apply(claims);
    }
}

