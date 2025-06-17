package com.ejemplo.aplicacion.repositorio;

import com.ejemplo.aplicacion.dto.ContratoResumen;
import com.ejemplo.aplicacion.modelo.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    @Query("SELECT new com.ejemplo.aplicacion.dto.ContratoResumen(c.id, c.nombre, c.estado) " +
            "FROM Contrato c WHERE c.dni = :dni")
    List<ContratoResumen> findContratosResumenPorDni(@Param("dni") String dni);
}