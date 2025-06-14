package com.ejemplo.aplicacion.controlador;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "*")
public class ContratoControlador {

    @Autowired
    private RepositorioContrato contratoRepositorio;

    @Autowired
    private BlobContainerClient blobContainerClient;

    @PostMapping("/{id}/subir-pdf")
    public ResponseEntity<String> subirPdfEnBlob(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Contrato> opt = contratoRepositorio.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Contrato contrato = opt.get();

            if (file.isEmpty() || !file.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.badRequest().body("Archivo no válido. Se requiere un PDF.");
            }

            byte[] bytes = file.getBytes();

            String blobName = "contrato_" + id + ".pdf";
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            BlockBlobClient blockBlobClient = blobClient.getBlockBlobClient();

            blockBlobClient.upload(new ByteArrayInputStream(bytes), bytes.length, true);

            String url = blobClient.getBlobUrl();
            contrato.setUrlArchivoPdf(url);
            contratoRepositorio.save(contrato);

            return ResponseEntity.ok("PDF subido y URL guardada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo subir a Azure Blob Storage: " + e.getMessage());
        }
    }

    // ✅ NUEVO MÉTODO: Devuelve la URL firmada del PDF
    @GetMapping("/{id}/url-pdf")
    public ResponseEntity<String> obtenerUrlFirmada(@PathVariable Long id) {
        Optional<Contrato> opt = contratoRepositorio.findById(id);
        if (opt.isEmpty() || opt.get().getUrlArchivoPdf() == null) {
            return ResponseEntity.notFound().build();
        }

        String blobName = "contrato_" + id + ".pdf";
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

        // Crear SAS con permisos de lectura válidos por 5 minutos
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusMinutes(5),
                new BlobSasPermission().setReadPermission(true)
        );

        String sasToken = blobClient.generateSas(sasValues);
        String urlConSas = blobClient.getBlobUrl() + "?" + sasToken;

        return ResponseEntity.ok(urlConSas);  // Devuelve la URL como texto plano
    }

    @GetMapping
    public ResponseEntity<List<Contrato>> obtenerTodosLosContratos() {
        List<Contrato> contratos = contratoRepositorio.findAll();
        return ResponseEntity.ok(contratos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContrato(@PathVariable Long id) {
        if (!contratoRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        contratoRepositorio.deleteById(id);

        // También podrías eliminar el blob aquí si quieres
        try {
            String blobName = "contrato_" + id + ".pdf";
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            if (blobClient.exists()) {
                blobClient.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Ignorar errores de borrado si no se quiere afectar la UX
        }

        return ResponseEntity.noContent().build();
    }
}
