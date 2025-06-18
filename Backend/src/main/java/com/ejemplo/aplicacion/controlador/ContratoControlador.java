package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.servicio.EnvioCorreoService;
import org.springframework.data.domain.Page;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@RestController
@RequestMapping("/api/contratos")
public class ContratoControlador {


    @Autowired
    private EnvioCorreoService envioCorreoService;


    @Autowired
    private ContratoRepository contratoRepositorio;

    @Autowired
    private AzureBlobSasUtil azureBlobSasUtil;


    @Autowired
    private RepositorioUsuario repositorioUsuario;

    /**
     * Crear contrato con archivo PDF subido
     */
    @PostMapping("/crear-con-archivo")
    public ResponseEntity<String> crearContratoConArchivo(
            @RequestParam("dnis") List<String> dnis,
            @RequestParam("titulo") String titulo,
            @RequestParam("file") MultipartFile file) {

        if (dnis == null || dnis.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Debes seleccionar al menos un usuario.");
        }

        List<String> creados = new ArrayList<>();
        List<String> fallidos = new ArrayList<>();
        String blobName = "contrato_" + UUID.randomUUID() + ".pdf";

        try {
            // Asegurarse de que el contenedor existe
            if (!azureBlobSasUtil.getContainerClient().exists()) {
                azureBlobSasUtil.getContainerClient().create();
            }

            // Subir el archivo una vez
            BlobClient blobClient = azureBlobSasUtil.getContainerClient().getBlobClient(blobName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            for (String dni : dnis) {
                Optional<Usuario> usuarioOpt = repositorioUsuario.findByDni(dni);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();

                    Contrato contrato = new Contrato();
                    contrato.setNombre(titulo);
                    contrato.setDni(usuario.getDni());
                    contrato.setEmail(usuario.getEmail());
                    contrato.setEstado("pendiente");
                    contrato.setFirmado(false);
                    contrato.setFechaFirma(LocalDateTime.now());
                    contrato.setArchivopdf(blobName); // todos comparten el mismo archivo

                    contratoRepositorio.save(contrato);
                    creados.add(dni);
                } else {
                    fallidos.add(dni);
                }
            }

            StringBuilder mensaje = new StringBuilder("✅ Contratos creados: " + creados.size());
            if (!fallidos.isEmpty()) {
                mensaje.append(". ❌ DNIs no encontrados: ").append(String.join(", ", fallidos));
            }

            return ResponseEntity.ok(mensaje.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al procesar los contratos: " + e.getMessage());
        }
    }


    /**
     * Firmar contrato con imagen de firma en Base64
     */
    @PostMapping("/{id}/firmar")
    public ResponseEntity<String> firmarContrato(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String firmaBase64 = payload.get("firma");
        if (firmaBase64 == null || !firmaBase64.startsWith("data:image/png;base64,")) {
            return ResponseEntity.badRequest().body("Formato de firma inválido");
        }

        Optional<Contrato> contratoOpt = contratoRepositorio.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado");
        }

        Contrato contrato = contratoOpt.get();
        String blobName = contrato.getArchivopdf();

        try {
            // Descargar el PDF desde Azure
            BlobClient blobClient = azureBlobSasUtil.getContainerClient().getBlobClient(blobName);
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            blobClient.download(pdfOutputStream);
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Decodificar firma base64
            byte[] firmaBytes = Base64.getDecoder().decode(firmaBase64.replace("data:image/png;base64,", ""));

            // Insertar firma en el PDF
            ByteArrayOutputStream outputStreamFirmado = new ByteArrayOutputStream();
            try (PDDocument doc = PDDocument.load(pdfBytes)) {
                PDPage page = doc.getPage(0);
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, firmaBytes, "firma");

                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    contentStream.drawImage(pdImage, 100, 100, 150, 75);
                }

                doc.save(outputStreamFirmado);
            }

            // Subir el PDF firmado al blob
            blobClient.upload(new ByteArrayInputStream(outputStreamFirmado.toByteArray()), outputStreamFirmado.size(), true);

            contrato.setFirmado(true);
            contrato.setEstado("firmado");
            contrato.setFechaFirma(LocalDateTime.now());
            contratoRepositorio.save(contrato);

            // === Enviar correo al usuario con el PDF firmado ===
            // 1. Guardar temporalmente el archivo en disco
            File archivoTemp = File.createTempFile("contrato_firmado_", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(archivoTemp)) {
                fos.write(outputStreamFirmado.toByteArray());
            }

            // 2. Enviar el correo
            envioCorreoService.enviarContratoFirmado(contrato.getEmail(), archivoTemp);

            // 3. Eliminar el archivo temporal si quieres
            archivoTemp.delete();

            return ResponseEntity.ok("✅ Contrato firmado y enviado al correo " + contrato.getEmail());
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
        Optional<Contrato> contratoOpt = contratoRepositorio.findById(id);
        if (contratoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato no encontrado");
        }

        Contrato contrato = contratoOpt.get();

        // Borrar el archivo PDF de Azure si existe
        String blobName = contrato.getArchivopdf();
        if (blobName != null && !blobName.isEmpty()) {
            try {
                BlobClient blobClient = azureBlobSasUtil.getContainerClient().getBlobClient(blobName);
                if (blobClient.exists()) {
                    blobClient.delete();
                }
            } catch (Exception e) {
                // Puedes loguear el error pero no detener la eliminación del contrato
                e.printStackTrace();
            }
        }

        // Borrar el contrato de la base de datos
        contratoRepositorio.deleteById(id);
        return ResponseEntity.ok("Contrato eliminado");
    }


    // Autocompletar DNI: GET /api/contratos/dni?query=12
    @GetMapping("/dni")
    public List<String> buscarDniPorQuery(@RequestParam String query) {
        return contratoRepositorio.findDniStartingWith(query.toLowerCase());
    }


    @GetMapping
    public ResponseEntity<Page<Contrato>> listarContratos(Pageable pageable) {
        Page<Contrato> contratos = contratoRepositorio.findAll(pageable);
        return ResponseEntity.ok(contratos);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<String>> buscarUsuarios(@RequestParam String query) {
        List<String> nombres = contratoRepositorio.findDistinctUsuarioNombreStartingWith(query.toLowerCase() + "%");
        return ResponseEntity.ok(nombres);
    }



}
