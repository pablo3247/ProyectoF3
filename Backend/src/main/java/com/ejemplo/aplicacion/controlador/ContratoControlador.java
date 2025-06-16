package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.modelo.Usuario;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
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
import java.util.*;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratoControlador {

    @Autowired
    private RepositorioContrato contratoRepositorio;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

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
            if (opt.isEmpty() || opt.get().getArchivoPdf() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado o sin PDF");
            }

            String firmaBase64 = datos.get("firma");
            if (firmaBase64 == null || !firmaBase64.contains(",")) {
                return ResponseEntity.badRequest().body("Firma no válida");
            }

            byte[] pdfBytes = opt.get().getArchivoPdf();
            byte[] firmaBytes = Base64.getDecoder().decode(firmaBase64.split(",")[1]);

            BufferedImage firmaImage = ImageIO.read(new ByteArrayInputStream(firmaBytes));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try (PDDocument doc = PDDocument.load(pdfBytes)) {
                PDPage pagina = doc.getPage(0); // en la primera página
                PDImageXObject firma = PDImageXObject.createFromByteArray(doc, firmaBytes, "firma");
                PDPageContentStream contenido = new PDPageContentStream(doc, pagina, PDPageContentStream.AppendMode.APPEND, true);

                // Posicionar firma (ajusta si necesitas)
                PDRectangle mediaBox = pagina.getMediaBox();
                float x = 100;
                float y = 100;

                contenido.drawImage(firma, x, y, 150, 50);
                contenido.close();

                doc.save(out);
            }

            Contrato contrato = opt.get();
            contrato.setArchivoPdf(out.toByteArray());
            contrato.setEstado("firmado");
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
        if (opt.isEmpty() || opt.get().getArchivoPdf() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=contrato_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(opt.get().getArchivoPdf());
    }

    @GetMapping
    public ResponseEntity<List<Contrato>> obtenerContratos() {
        List<Contrato> contratos = contratoRepositorio.findAll();
        return ResponseEntity.ok(contratos);
    }



}
