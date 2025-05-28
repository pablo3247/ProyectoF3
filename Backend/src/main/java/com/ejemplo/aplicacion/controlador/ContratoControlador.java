package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.modelo.FirmaTemporal;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratoControlador {

    @Autowired
    private RepositorioContrato contratoRepositorio;

    private Map<Long, FirmaTemporal> firmasTemporales = new ConcurrentHashMap<>();

    // Obtener todos los contratos
    @GetMapping
    public List<Contrato> getTodosLosContratos() {
        return contratoRepositorio.findAll();
    }

    // Obtener un contrato por ID
    @GetMapping("/{id}")
    public ResponseEntity<Contrato> getContratoPorId(@PathVariable Long id) {
        Optional<Contrato> contrato = contratoRepositorio.findById(id);
        return contrato.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Día 7: Guardar firma temporal
    @PostMapping("/firma-temporal")
    public ResponseEntity<String> guardarFirmaTemporal(@RequestBody FirmaTemporal firma) {
        firmasTemporales.put(firma.getContratoId(), firma);
        return ResponseEntity.ok("Firma temporal almacenada");
    }

    // Día 8: Obtener PDF de vista previa con firma simulada
    @GetMapping("/preview/{id}")
    public ResponseEntity<byte[]> obtenerPreviewContrato(@PathVariable Long id) {
        FirmaTemporal firma = firmasTemporales.get(id);
        if (firma == null) {
            return ResponseEntity.badRequest().body(null);
        }

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("PREVISUALIZACIÓN DEL CONTRATO ID: " + id);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Firma (simulada):");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(firma.getDatosFirma().replaceAll("\\n", " "));
            contentStream.endText();
            contentStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "preview_" + id + ".pdf");

            return ResponseEntity.ok().headers(headers).body(out.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
