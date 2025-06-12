package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratoControlador {

    @Autowired
    private RepositorioContrato contratoRepositorio;

    @PostMapping("/{id}/subir-pdf")
    public ResponseEntity<String> subirPdfEnBlob(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Contrato> opt = contratoRepositorio.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            Contrato contrato = opt.get();
            contrato.setArchivoPdf(file.getBytes());
            contratoRepositorio.save(contrato);

            return ResponseEntity.ok("✅ PDF guardado en la base de datos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al guardar el PDF: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/descargar-pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Optional<Contrato> opt = contratoRepositorio.findById(id);
        if (opt.isEmpty() || opt.get().getArchivoPdf() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=contrato_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(opt.get().getArchivoPdf());
    }
}
