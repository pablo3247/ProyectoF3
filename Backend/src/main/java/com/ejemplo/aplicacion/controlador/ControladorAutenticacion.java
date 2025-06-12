package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.dto.LoginDTO;
import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ControladorAutenticacion {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO credenciales) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(credenciales.getEmail());

        Map<String, Object> respuesta = new HashMap<>();

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getContrasena().equals(credenciales.getContrasena())) {
                respuesta.put("mensaje", "Login exitoso");
                respuesta.put("rol", usuario.getRol());
                return ResponseEntity.ok(respuesta);
            } else {
                respuesta.put("mensaje", "Contraseña incorrecta");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
            }
        } else {
            respuesta.put("mensaje", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }
    }

}

