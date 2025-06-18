package com.ejemplo.aplicacion.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;

@Service
public class EnvioCorreoService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarContratoFirmado(String destinatario, File archivoPdf) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        // 👇 Usa tu remitente de Brevo verificado
        helper.setFrom("correorelay0@gmail.com"); // ← Tu correo verificado
        helper.setTo(destinatario);
        helper.setSubject("Contrato firmado");
        helper.setText("Hola, te adjuntamos el contrato firmado. Gracias por usar nuestra aplicación.");
        helper.addAttachment("contrato_firmado.pdf", new FileSystemResource(archivoPdf));

        System.out.println("📧 Enviando correo desde: " + helper.getMimeMessage().getFrom()[0] + " a: " + destinatario);

        mailSender.send(mensaje);
    }
}
