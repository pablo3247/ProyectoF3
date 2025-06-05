package com.ejemplo.aplicacion.repositorio;

import com.ejemplo.aplicacion.modelo.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepositorio extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByDniContainingIgnoreCaseAndNombreClienteContainingIgnoreCaseAndFechaFirmaBetween(
            String dni,
            String nombre,
            java.time.LocalDateTime desde,
            java.time.LocalDateTime hasta,
            Pageable pageable
    );
}
