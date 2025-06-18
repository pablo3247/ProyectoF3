
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

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return repositorioUsuario.findAll();
    }

    @GetMapping("/solo-usuarios")
    public List<Usuario> obtenerUsuariosNormales() {
        return repositorioUsuario.findAll().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRol()))
                .toList();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.of(repositorioUsuario.findByEmail(email));
    }

}
