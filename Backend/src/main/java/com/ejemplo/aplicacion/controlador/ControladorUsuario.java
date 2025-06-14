
package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return repositorioUsuario.findAll();
    }

    @GetMapping("/actual")
    public ResponseEntity<Usuario> obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(username);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

