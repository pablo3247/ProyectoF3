package controlador;

import modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repositorio.RepositorioUsuario;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Esto permite llamadas desde React (localhost:3000)
public class ControladorUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario nuevoUsuario) {
        return repositorioUsuario.save(nuevoUsuario);
    }
}
