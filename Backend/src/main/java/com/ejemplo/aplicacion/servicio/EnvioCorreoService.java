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

        helper.setFrom("no-reply@demoapp.com");  // <--- AÑADE ESTO
        helper.setTo(destinatario);
        helper.setSubject("Contrato firmado");
        helper.setText("Hola, te adjuntamos el contrato firmado. Gracias por usar nuestra aplicación.");
        helper.addAttachment("contrato_firmado.pdf", new FileSystemResource(archivoPdf));

        mailSender.send(mensaje);
    }

}
