package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.dto.LoginDTO;
import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ControladorAutenticacion {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO credenciales) {
        Optional<Usuario> usuario = repositorioUsuario.findByEmail(credenciales.getEmail());
        if (usuario.isPresent()) {
            System.out.println("Usuario encontrado: " + usuario.get().getEmail());
            if (usuario.get().getContrasena().equals(credenciales.getContrasena())) {
                System.out.println("Contraseña válida");
                return "Login exitoso";
            } else {
                System.out.println("Contraseña inválida");
                return "Credenciales inválidas";
            }
        } else {
            System.out.println("Usuario no encontrado");
            return "Credenciales inválidas";
        }
    }

}
