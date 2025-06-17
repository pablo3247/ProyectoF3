package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.Util.AzureBlobSasUtil;
import com.ejemplo.aplicacion.dto.ContratoResumen;
import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.ContratoRepository;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
@RestController
@RequestMapping("/api/contratos")
public class ContratoControlador {

    @Autowired
    private AzureBlobSasUtil azureBlobSasUtil;

    @Autowired
    private ContratoRepository contratoRepositorio;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    /**
     * Crear contrato con archivo PDF subido
     */
    @PostMapping("/crear-con-archivo")
    public ResponseEntity<String> crearContratoConArchivo(
            @RequestParam("dni") String dni,
            @RequestParam("titulo") String titulo,
            @RequestParam("file") MultipartFile file) {

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByDni(dni);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        Contrato contrato = new Contrato();
        contrato.setNombre(titulo);
        contrato.setDni(usuario.getDni());
        contrato.setEmail(usuario.getEmail());
        contrato.setEstado("pendiente");
        contrato.setFirmado(false);
        contrato.setFechaFirma(LocalDateTime.now());

        try {
            // Guardar contrato para obtener ID
            Contrato contratoGuardado = contratoRepositorio.save(contrato);

            // Crear contenedor si no existe
            if (!azureBlobSasUtil.getContainerClient().exists()) {
                azureBlobSasUtil.getContainerClient().create();
            }

            // Subir archivo PDF a Blob Storage
            String blobName = "contrato_" + contratoGuardado.getId() + ".pdf";
            BlobClient blobClient = azureBlobSasUtil.getContainerClient().getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            // Guardar nombre del archivo (blobName)
            contratoGuardado.setArchivopdf(blobName);
            contratoRepositorio.save(contratoGuardado);

            return ResponseEntity.ok("✅ Contrato creado correctamente con nombre: " + blobName);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al subir el archivo: " + e.getMessage());
        }
    }

    /**
     * Firmar contrato con imagen de firma en Base64
     */
    @PostMapping("/{id}/firmar")
    public ResponseEntity<String> firmarContrato(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        try {
            Optional<Contrato> opt = contratoRepositorio.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado");
            }

            Contrato contrato = opt.get();

            byte[] pdfBytes;
            if (contrato.getArchivoPdf() != null && contrato.getArchivoPdf().length > 0) {
                pdfBytes = contrato.getArchivoPdf();
            } else if (contrato.getArchivopdf() != null) {
                BlobClient blobClient = azureBlobSasUtil.getContainerClient().getBlobClient(contrato.getArchivopdf());
                if (!blobClient.exists()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PDF no encontrado en Blob");
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                blobClient.download(outputStream);
                pdfBytes = outputStream.toByteArray();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato sin PDF asociado");
            }

            String firmaBase64 = datos.get("firma");
            if (firmaBase64 == null || !firmaBase64.contains(",")) {
                return ResponseEntity.badRequest().body("Firma no válida");
            }

            byte[] firmaBytes = Base64.getDecoder().decode(firmaBase64.split(",")[1]);
            BufferedImage firmaImage = ImageIO.read(new ByteArrayInputStream(firmaBytes));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (PDDocument doc = PDDocument.load(pdfBytes)) {
                PDPage pagina = doc.getPage(0);
                PDImageXObject firma = PDImageXObject.createFromByteArray(doc, firmaBytes, "firma");
                PDPageContentStream contenido = new PDPageContentStream(doc, pagina,
                        PDPageContentStream.AppendMode.APPEND, true);
                contenido.drawImage(firma, 100, 100, 150, 50);
                contenido.close();
                doc.save(out);
            }

            contrato.setArchivoPdf(out.toByteArray());
            contrato.setEstado("firmado");
            contrato.setFirmado(true);
            contrato.setFechaFirma(LocalDateTime.now());
            contratoRepositorio.save(contrato);

            return ResponseEntity.ok("✅ Contrato firmado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al firmar el contrato: " + e.getMessage());
        }
    }

    /**
     * Subir PDF directamente a contrato (base de datos)
     */
    @PostMapping("/{id}/subir-pdf")
    public ResponseEntity<String> subirPdfEnContrato(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Contrato> opt = contratoRepositorio.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado");

            Contrato contrato = opt.get();
            contrato.setArchivoPdf(file.getBytes());
            contratoRepositorio.save(contrato);

            return ResponseEntity.ok("PDF guardado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar PDF: " + e.getMessage());
        }
    }

    /**
     * Asignar contrato a usuario (sin archivo)
     */
    @PostMapping("/asignar")
    public ResponseEntity<String> asignarContrato(@RequestBody Map<String, String> datos) {
        String dni = datos.get("dni");
        String titulo = datos.get("titulo");

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByDni(dni);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        Contrato contrato = new Contrato();
        contrato.setNombre(titulo);
        contrato.setDni(usuario.getDni());
        contrato.setEmail(usuario.getEmail());
        contrato.setEstado("pendiente");
        contrato.setFirmado(false);
        contrato.setFechaFirma(null);

        contratoRepositorio.save(contrato);

        return ResponseEntity.ok("Contrato asignado correctamente");
    }

    /**
     * Crear contrato simple (sin archivo ni usuario)
     */
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearContrato(@RequestBody Contrato contrato) {
        Contrato guardado = contratoRepositorio.save(contrato);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", guardado.getId());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Descargar PDF desde Blob Storage
     */
    @GetMapping("/{id}/descargar-pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Optional<Contrato> opt = contratoRepositorio.findById(id);
        if (opt.isEmpty() || opt.get().getArchivopdf() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String blobName = opt.get().getArchivopdf();
            BlobClient blobClient = azureBlobSasUtil.getContainerClient().getBlobClient(blobName);

            if (!blobClient.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.download(outputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + blobName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(outputStream.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Listar contratos resumidos por DNI
     */
    @GetMapping("/dni/{dni}")
    public ResponseEntity<List<ContratoResumen>> getContratosPorDni(@PathVariable String dni) {
        List<ContratoResumen> lista = contratoRepositorio.findContratosResumenPorDni(dni);
        return ResponseEntity.ok(lista);
    }

    /**
     * Obtener URL para descargar PDF (solo ruta interna)
     */
    @GetMapping("/{id}/url-pdf")
    public ResponseEntity<String> obtenerUrlPdfConSas(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepositorio.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Contrato contrato = contratoOpt.get();
        String blobName = contrato.getArchivopdf();

        if (blobName == null || blobName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay PDF asociado a este contrato");
        }

        try {
            String urlConSas = azureBlobSasUtil.generarUrlSas(blobName);
            return ResponseEntity.ok(urlConSas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generando URL SAS: " + e.getMessage());
        }
    }

    /**
     * Eliminar contrato por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarContrato(@PathVariable Long id) {
        if (!contratoRepositorio.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado");
        }
        contratoRepositorio.deleteById(id);
        return ResponseEntity.ok("Contrato eliminado");
    }

    @GetMapping("")
    public ResponseEntity<List<Contrato>> listarContratos() {
        List<Contrato> contratos = contratoRepositorio.findAll();
        return ResponseEntity.ok(contratos);
    }
}
