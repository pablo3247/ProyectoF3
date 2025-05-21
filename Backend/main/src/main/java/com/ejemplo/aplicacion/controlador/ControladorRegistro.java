package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registro")
public class ControladorRegistro {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder codificadorContrasena;

    @PostMapping
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        if (repositorioUsuario.findByCorreo(nuevoUsuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado.");
        }

        nuevoUsuario.setContrasenaHash(codificadorContrasena.encode(nuevoUsuario.getContrasenaHash()));
        repositorioUsuario.save(nuevoUsuario);
        return ResponseEntity.ok("Usuario registrado con éxito.");
    }
}

