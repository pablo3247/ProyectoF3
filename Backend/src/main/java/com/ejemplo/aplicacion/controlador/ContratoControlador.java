package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratoControlador {
    @PostMapping("/{id}/subir-pdf")
    public ResponseEntity<String> subirPdf(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Usa una ruta fija y segura dentro del sistema de archivos
            String baseDir = System.getProperty("user.home") + "/contratos_subidos";
            File destino = new File(baseDir + "/contrato_" + id + ".pdf");

            destino.getParentFile().mkdirs(); // crea ~/contratos_subidos si no existe
            file.transferTo(destino);

            return ResponseEntity.ok("✅ Contrato guardado en: " + destino.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al guardar el contrato: " + e.getMessage());
        }
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarContrato(@PathVariable Long id) throws IOException {
        String ruta = System.getProperty("user.home")+"/contratos_subidos/contrato_" + id + ".pdf";
        File archivo = new File(ruta);
        if (!archivo.exists()) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(archivo.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + archivo.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


    @Autowired
    private RepositorioContrato contratoRepositorio;

    @GetMapping("/status/{id}")
    public Map<String, Object> obtenerEstadoContrato(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepositorio.findById(id);

        if (contratoOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Contrato no encontrado");
            return error;
        }

        Contrato contrato = contratoOpt.get();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", contrato.getId());
        respuesta.put("estado", contrato.getEstado());
        respuesta.put("fechaFirma", contrato.getFechaFirma());
        respuesta.put("email", contrato.getEmail());

        return respuesta;
    }

    @GetMapping
    public List<Contrato> obtenerTodosLosContratos() {
        return contratoRepositorio.findAll();
    }

    @GetMapping("/buscar/dni")
    public List<Contrato> buscarPorDni(@RequestParam String dni) {
        return contratoRepositorio.findByDniContainingIgnoreCase(dni);
    }

    @GetMapping("/buscar/apellidos")
    public List<Contrato> buscarPorApellidos(@RequestParam String apellidos) {
        return contratoRepositorio.findByApellidosContainingIgnoreCase(apellidos);
    }

    @GetMapping("/buscar/fecha")
    public List<Contrato> buscarPorFecha(@RequestParam String desde, @RequestParam String hasta) {
        LocalDate inicio = LocalDate.parse(desde);
        LocalDate fin = LocalDate.parse(hasta);
        return contratoRepositorio.findByFechaFirmaBetween(inicio, fin);
    }

    @GetMapping("/filtrar")
    public List<Contrato> filtrarContratos(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        if (dni != null && !dni.isEmpty()) {
            return contratoRepositorio.findByDniContainingIgnoreCase(dni);
        } else if (apellidos != null && !apellidos.isEmpty()) {
            return contratoRepositorio.findByApellidosContainingIgnoreCase(apellidos);
        } else if (desde != null && hasta != null) {
            return contratoRepositorio.findByFechaFirmaBetween(desde, hasta);
        } else {
            return contratoRepositorio.findAll(); // si no hay filtros, devuelve todos
        }
    }


}
