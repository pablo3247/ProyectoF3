package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.AuditLog;
import com.ejemplo.aplicacion.repositorio.AuditLogRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/root/contratos")
public class AuditLogControlador {

    @Autowired
    private AuditLogRepositorio auditLogRepositorio;

    @GetMapping
    public Page<AuditLog> listarContratosFirmados(
            @RequestParam(required = false, defaultValue = "") String dni,
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaFirma").descending());

        if (desde == null) desde = LocalDateTime.MIN;
        if (hasta == null) hasta = LocalDateTime.now();

        return auditLogRepositorio.findByDniContainingIgnoreCaseAndNombreClienteContainingIgnoreCaseAndFechaFirmaBetween(
                dni, nombre, desde, hasta, pageable
        );
    }
}
