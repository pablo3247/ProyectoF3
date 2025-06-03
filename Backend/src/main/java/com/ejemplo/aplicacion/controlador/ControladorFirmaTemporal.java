package com.ejemplo.aplicacion.controlador;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/firmas-temporales")
public class ControladorFirmaTemporal {

    private final String rutaFirmas = "temp_firmas";

    public ControladorFirmaTemporal() throws IOException {
        Files.createDirectories(Paths.get(rutaFirmas));
    }

    @PostMapping
    public ResponseEntity<String> guardarFirmaTemporal(@RequestBody FirmaTemporalRequest request) throws IOException {
        String base64 = request.getImagenBase64().split(",")[1];
        byte[] imageBytes = Base64Utils.decodeFromString(base64);

        Path outputPath = Paths.get(rutaFirmas, request.getContratoId() + ".png");
        Files.write(outputPath, imageBytes);

        return ResponseEntity.ok("Firma guardada temporalmente.");
    }

    @GetMapping(value = "/preview/{contratoId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> verPdfPreview(@PathVariable int contratoId) throws IOException {
        Path firmaPath = Paths.get(rutaFirmas, contratoId + ".png");
        if (!Files.exists(firmaPath)) {
            return ResponseEntity.notFound().build();
        }

        BufferedImage firmaImg = ImageIO.read(firmaPath.toFile());
        BufferedImage pdfImg = new BufferedImage(595, 842, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = pdfImg.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 595, 842);
        g2d.drawImage(firmaImg, 50, 700, 200, 100, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(pdfImg, "png", baos);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                .body(baos.toByteArray());
    }

    public static class FirmaTemporalRequest {
        private int contratoId;
        private String imagenBase64;

        public int getContratoId() { return contratoId; }
        public void setContratoId(int contratoId) { this.contratoId = contratoId; }

        public String getImagenBase64() { return imagenBase64; }
        public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }
    }
}
