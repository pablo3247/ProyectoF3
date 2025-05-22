public class JWT {
    // ==========================
// MODELO: Usuario.java
// ==========================

import jakarta.persistence.*;

    @Entity
    public class Usuario {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nombre;
        private String dnnni;
        private String correo;
        private String contrasenaHash;
        private String certificado;
        private String rol; // "cliente" o "root"

        // Getters y Setters
        // ...
    }

// ==========================
// DTO: PeticionLogin.java
// ==========================

    public class PeticionLogin {
        private String correo;
        private String contrasena;

        // Getters y Setters
        // ...
    }

// ==========================
// DTO: RespuestaLogin.java
// ==========================

    public class RespuestaLogin {
        private String token;
        private String rol;

        public RespuestaLogin(String token, String rol) {
            this.token = token;
            this.rol = rol;
        }

        // Getters y Setters
        // ...
    }

// ==========================
// REPOSITORIO: RepositorioUsuario.java
// ==========================

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
        Optional<Usuario> findByCorreo(String correo);
    }

// ==========================
// SERVICIO: ServicioJwt.java
// ==========================

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.function.Function;

    @Service
    public class ServicioJwt {
        private final String claveSecreta = "secreto-super-seguro";

        public String generarToken(Usuario usuario) {
            return Jwts.builder()
                    .setSubject(usuario.getCorreo())
                    .claim("rol", usuario.getRol())
                    .claim("usuarioId", usuario.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                    .signWith(SignatureAlgorithm.HS256, claveSecreta)
                    .compact();
        }

        public boolean validarToken(String token) {
            try {
                Jwts.parser().setSigningKey(claveSecreta).parseClaimsJws(token);
                return true;
            } catch (JwtException e) {
                return false;
            }
        }

        public String extraerCorreo(String token) {
            return extraerReclamo(token, Claims::getSubject);
        }

        public String extraerRol(String token) {
            return extraerTodosReclamos(token).get("rol", String.class);
        }

        private Claims extraerTodosReclamos(String token) {
            return Jwts.parser().setSigningKey(claveSecreta).parseClaimsJws(token).getBody();
        }

        private <T> T extraerReclamo(String token, Function<Claims, T> extractor) {
            final Claims claims = extraerTodosReclamos(token);
            return extractor.apply(claims);
        }
    }

// ==========================
// CONTROLADOR: ControladorAutenticacion.java
// ==========================

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

    @RestController
    @RequestMapping("/login")
    public class ControladorAutenticacion {

        @Autowired
        private RepositorioUsuario repositorioUsuario;

        @Autowired
        private PasswordEncoder codificadorContrasena;

        @Autowired
        private ServicioJwt servicioJwt;

        @PostMapping
        public ResponseEntity<?> login(@RequestBody PeticionLogin peticion) {
            Usuario usuario = repositorioUsuario.findByCorreo(peticion.getCorreo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

            if (!codificadorContrasena.matches(peticion.getContrasena(), usuario.getContrasenaHash())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña inválida");
            }

            String jwt = servicioJwt.generarToken(usuario);

            return ResponseEntity.ok(new RespuestaLogin(jwt, usuario.getRol()));
        }
    }

// ==========================
// CONFIGURACIÓN: ConfiguracionSeguridad.java
// ==========================

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

    @Configuration
    public class ConfiguracionSeguridad {

        @Bean
        public PasswordEncoder codificadorContrasena() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager administradorAutenticacion(AuthenticationConfiguration configuracion) throws Exception {
            return configuracion.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain cadenaFiltros(HttpSecurity http, FiltroJwt filtroJwt) throws Exception {
            http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/login").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }

// ==========================
// FILTRO: FiltroJwt.java
// ==========================

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
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String cabeceraAutorizacion = request.getHeader("Authorization");

            if (cabeceraAutorizacion != null && cabeceraAutorizacion.startsWith("Bearer ")) {
                String token = cabeceraAutorizacion.substring(7);

                if (servicioJwt.validarToken(token)) {
                    String correo = servicioJwt.extraerCorreo(token);
                    String rol = servicioJwt.extraerRol(token);

                    User principal = new User(correo, "", Collections.singleton(() -> rol));

                    UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());

                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }

            filterChain.doFilter(request, response);
        }
    }

}
