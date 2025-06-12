
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


}
