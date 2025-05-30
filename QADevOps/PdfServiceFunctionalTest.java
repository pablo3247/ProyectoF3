import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class PdfServiceFunctionalTest {

    @Test
    public void testGenerarPdfFirmado() throws Exception {
        // Crear PDF base en memoria
        PDDocument documento = new PDDocument();
        documento.addPage(new PDPage());
        ByteArrayOutputStream pdfBaseStream = new ByteArrayOutputStream();
        documento.save(pdfBaseStream);
        documento.close();

        byte[] pdfBase = pdfBaseStream.toByteArray();

        // Simular datos cliente y firma
        DatosCliente datos = new DatosCliente("Ana", "López", "12345678Z", "ana@email.com", "600000000");
        Firma firma = new Firma("[[10,10],[20,20]]"); // Garabato simulado

        // Instanciar servicio real
        PdfService pdfService = new PdfService();
        byte[] pdfFirmado = pdfService.generarPdfFirmado(pdfBase, datos, firma);

        assertNotNull(pdfFirmado);
        assertTrue(pdfFirmado.length > 200);

        PDDocument pdfResult = PDDocument.load(new ByteArrayInputStream(pdfFirmado));
        assertEquals(1, pdfResult.getNumberOfPages());
        pdfResult.close();

        File output = new File("pdf_firmado_funcional_test.pdf");
        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(pdfFirmado);
        }

        assertTrue(output.exists());
        output.delete(); // limpieza
    }

    // Clases de prueba
    static class DatosCliente {
        String nombre, apellidos, dni, email, telefono;
        public DatosCliente(String nombre, String apellidos, String dni, String email, String telefono) {
            this.nombre = nombre; this.apellidos = apellidos; this.dni = dni;
            this.email = email; this.telefono = telefono;
        }
    }

    static class Firma {
        String trazosJson;
        public Firma(String trazosJson) { this.trazosJson = trazosJson; }
    }
}
