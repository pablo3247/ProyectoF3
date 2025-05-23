package controlador;

import modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repositorio.RepositorioUsuario;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*")
public class ControladorAutenticacion {

    @RestController
    @RequestMapping("/api/auth")
    @CrossOrigin(origins = "*") // o especifica tu frontend
    public class AuthController {

        @Autowired
        private RepositorioUsuario usuarioRepository;

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Optional<Usuario> user = usuarioRepository.findByEmailAndPassword(email, password);

            if (user.isPresent()) {
                return ResponseEntity.ok("Login exitoso");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
            }
        }
    }

}

