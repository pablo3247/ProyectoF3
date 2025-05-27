package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*") // Permite llamadas desde el frontend (por ejemplo, localhost:3000 o un HTML estático)
public class ContratoControlador {

    @Autowired
    private RepositorioContrato contratoRepositorio;

    // Obtener todos los contratos
    @GetMapping
    public List<Contrato> obtenerTodos() {
        return contratoRepositorio.findAll();
    }

    // Obtener un contrato por ID
    @GetMapping("/{id}")
    public ResponseEntity<Contrato> obtenerPorId(@PathVariable Long id) {
        Optional<Contrato> contrato = contratoRepositorio.findById(id);
        return contrato.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo contrato
    @PostMapping
    public Contrato crearContrato(@RequestBody Contrato contrato) {
        return contratoRepositorio.save(contrato);
    }

    // Actualizar un contrato existente
    @PutMapping("/{id}")
    public ResponseEntity<Contrato> actualizarContrato(@PathVariable Long id, @RequestBody Contrato nuevoContrato) {
        return contratoRepositorio.findById(id)
                .map(contrato -> {
                    contrato.setNombre(nuevoContrato.getNombre());
                    contrato.setArchivoPDF(nuevoContrato.getArchivoPDF());
                    contrato.setFirmado(nuevoContrato.getFirmado());
                    contratoRepositorio.save(contrato);
                    return ResponseEntity.ok(contrato);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un contrato
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContrato(@PathVariable Long id) {
        if (contratoRepositorio.existsById(id)) {
            contratoRepositorio.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
