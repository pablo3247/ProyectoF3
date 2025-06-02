
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class EmailUnitTest {

    private GreenMail smtpMock;

    @BeforeEach
    public void setup() {
        smtpMock = new GreenMail(ServerSetupTest.SMTP);
        smtpMock.start();
    }

    @AfterEach
    public void cleanup() {
        smtpMock.stop();
    }

    @Test
    public void testEmailServiceEnviaContratoFirmado() throws Exception {
        byte[] contratoPDF = "PDF-FIRMADO-DE-EJEMPLO".getBytes();
        String destinatario = "cliente@ejemplo.com";

        EmailService emailService = new EmailService();
        emailService.enviarContratoFirmado(destinatario, contratoPDF, "contrato_cliente.pdf");

        smtpMock.waitForIncomingEmail(1);
        Message[] correos = smtpMock.getReceivedMessages();

        assertEquals(1, correos.length);
        assertEquals(destinatario, ((InternetAddress) correos[0].getAllRecipients()[0]).getAddress());
        assertTrue(correos[0].getContentType().contains("multipart"));
    }

    static class EmailService {
        public void enviarContratoFirmado(String to, byte[] pdf, String nombreArchivo) throws Exception {
            Properties props = new Properties();
            props.put("mail.smtp.host", "localhost");
            props.put("mail.smtp.port", "3025");
            Session session = Session.getInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("firma@empresa.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Su contrato firmado");

            MimeBodyPart texto = new MimeBodyPart();
            texto.setText("Adjunto su contrato firmado.");

            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.setDataHandler(new DataHandler(new ByteArrayDataSource(pdf, "application/pdf")));
            adjunto.setFileName(nombreArchivo);

            Multipart contenido = new MimeMultipart();
            contenido.addBodyPart(texto);
            contenido.addBodyPart(adjunto);

            message.setContent(contenido);
            Transport.send(message);
        }
    }
}
