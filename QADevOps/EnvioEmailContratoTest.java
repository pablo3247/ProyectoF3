package com.ejemplo.aplicacion;

import com.ejemplo.aplicacion.servicio.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EnvioEmailContratoTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private EmailService emailService;

    @Test
    void envioDeEmailDebeLlamarAlServicio() {
        Mockito.doNothing().when(emailService).sendContrato(Mockito.any());

        webClient.post()
            .uri("/contrato/enviar")
            .exchange()
            .expectStatus().isOk();

        Mockito.verify(emailService, Mockito.times(1)).sendContrato(Mockito.any());
    }
}