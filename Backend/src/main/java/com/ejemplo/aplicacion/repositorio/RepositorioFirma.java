
package com.ejemplo.aplicacion.repositorio;

import com.ejemplo.aplicacion.modelo.Firma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioFirma extends JpaRepository<Firma, Long> {
}
