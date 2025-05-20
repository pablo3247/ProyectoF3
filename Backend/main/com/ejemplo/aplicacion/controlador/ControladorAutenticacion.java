import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*")
public class ControladorAutenticacion {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder codificadorContrasena;

    @Autowired
    private ServicioJwt servicioJwt;

    @PostMapping
    public ResponseEntity<RespuestaLogin> login(@RequestBody PeticionLogin peticion) {
        Usuario usuario = repositorioUsuario.findByCorreo(peticion.getIdentificador())
                .or(() -> repositorioUsuario.findByTelefono(peticion.getIdentificador()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!codificadorContrasena.matches(peticion.getContrasena(), usuario.getContrasenaHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        String token = servicioJwt.generarToken(usuario);

        return ResponseEntity.ok(new RespuestaLogin(token, usuario.getRol()));
    }
}

