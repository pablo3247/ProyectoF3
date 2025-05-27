
package com.ejemplo.aplicacion.repositorio;

import com.ejemplo.aplicacion.modelo.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorioContrato extends JpaRepository<Contrato, Long> {
    List<Contrato> findByFirmadoFalse();
}
