package com.ejemplo.aplicacion.repositorio;

import com.ejemplo.aplicacion.dto.ContratoResumen;
import com.ejemplo.aplicacion.modelo.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {

    @Query("SELECT new com.ejemplo.aplicacion.dto.ContratoResumen(c.id, c.nombre, c.estado) " +
            "FROM Contrato c WHERE c.dni = :dni")
    List<ContratoResumen> findContratosResumenPorDni(@Param("dni") String dni);
    @Query("SELECT DISTINCT c.nombre FROM Contrato c WHERE LOWER(c.nombre) LIKE :prefijo")
    List<String> findNombresContratos(@Param("prefijo") String prefijo);

    @Query("SELECT DISTINCT c.dni FROM Contrato c WHERE LOWER(c.dni) LIKE CONCAT(:query, '%')")
    List<String> findDniStartingWith(@Param("query") String query);

    @Query("SELECT DISTINCT LOWER(c.usuario.nombre) FROM Contrato c WHERE LOWER(c.usuario.nombre) LIKE :prefijo")
    List<String> findDistinctUsuarioNombreStartingWith(@Param("prefijo") String prefijo);


}