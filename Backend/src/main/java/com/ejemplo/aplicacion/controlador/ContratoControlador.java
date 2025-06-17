package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.dto.ContratoResumen;
import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.ContratoRepository;
import com.ejemplo.aplicacion.repositorio.RepositorioUsuario;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratoControlador {

    @Autowired
    private ContratoRepository contratoRepositorio;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private BlobContainerClient containerClient;

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
            // Primero guardamos el contrato para obtener el ID
            Contrato contratoGuardado = contratoRepositorio.save(contrato);

            if (!containerClient.exists()) {
                containerClient.create();
            }

            // Nombre del archivo en blob
            String blobName = "contrato_" + contratoGuardado.getId() + ".pdf";
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            // Solo guardamos el nombre del archivo, no la URL local de blob
            contratoGuardado.setArchivopdf(blobName);
            contratoRepositorio.save(contratoGuardado);

            return ResponseEntity.ok("✅ Contrato creado correctamente con nombre: " + blobName);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al subir el archivo: " + e.getMessage());
        }
    }



    private byte[] generarPDFContrato(Usuario usuario, String titulo) throws IOException {
        PDDocument documento = new PDDocument();
        PDPage pagina = new PDPage();
        documento.addPage(pagina);

        PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
        contenido.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contenido.beginText();
        contenido.newLineAtOffset(100, 700);
        contenido.showText("Contrato: " + titulo);
        contenido.newLineAtOffset(0, -20);
        contenido.showText("Nombre: " + usuario.getNombre());
        contenido.newLineAtOffset(0, -20);
        contenido.showText("Email: " + usuario.getEmail());
        contenido.newLineAtOffset(0, -20);
        contenido.showText("DNI: " + usuario.getDni());
        contenido.endText();
        contenido.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        documento.save(out);
        documento.close();
        return out.toByteArray();
    }

    @PostMapping("/{id}/firmar")
    public ResponseEntity<String> firmarContrato(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        try {
            Optional<Contrato> opt = contratoRepositorio.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado");
            }

            Contrato contrato = opt.get();

            // ⚠️ Cargar PDF desde Azure Blob si no está en BBDD
            byte[] pdfBytes;
            if (contrato.getArchivoPdf() != null) {
                pdfBytes = contrato.getArchivoPdf();
            } else if (contrato.getArchivopdf() != null) {
                BlobClient blobClient = containerClient.getBlobClient(contrato.getArchivopdf());
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
                PDPageContentStream contenido = new PDPageContentStream(doc, pagina, PDPageContentStream.AppendMode.APPEND, true);
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

    @PostMapping("/asignar")
    public ResponseEntity<String> asignarContrato(@RequestBody Map<String, String> datos) {
        String dni = datos.get("dni");
        String titulo = datos.get("titulo");
        String contenido = datos.get("contenido");

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

        contratoRepositorio.save(contrato);

        return ResponseEntity.ok("Contrato asignado correctamente");
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearContrato(@RequestBody Contrato contrato) {
        Contrato guardado = contratoRepositorio.save(contrato);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", guardado.getId());
        return ResponseEntity.ok(respuesta);
    }



    @GetMapping("/{id}/descargar-pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Optional<Contrato> opt = contratoRepositorio.findById(id);
        if (opt.isEmpty() || opt.get().getArchivopdf() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String blobName = opt.get().getArchivopdf();
            BlobClient blobClient = containerClient.getBlobClient(blobName);

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


    @Autowired
    private ContratoRepository contratoRepository;


    @GetMapping("/dni/{dni}")
    public ResponseEntity<List<ContratoResumen>> getContratosPorDni(@PathVariable String dni) {
        List<ContratoResumen> lista = contratoRepository.findContratosResumenPorDni(dni);
        return ResponseEntity.ok(lista);
    }


}
