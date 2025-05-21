package com.ejemplo.aplicacion.repositorio;

import java.util.Optional;

import com.ejemplo.aplicacion.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByTelefono(String telefono);
}
