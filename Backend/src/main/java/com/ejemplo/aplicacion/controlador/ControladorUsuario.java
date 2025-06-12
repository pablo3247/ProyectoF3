
package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/usuarios")
public class ControladorUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario guardado = repositorioUsuario.save(usuario);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String contrasena = credenciales.get("contrasena");

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Login exitoso");
        respuesta.put("rol", usuario.getRol());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return repositorioUsuario.findAll();
    }


}
