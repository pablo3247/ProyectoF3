package com.ejemplo.aplicacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FormularioValidacionTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void camposVaciosDebenRetornar400() {
        webClient.post()
            .uri("/contrato/firmar")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void dniInvalidoDebeRetornar400() {
        webClient.post()
            .uri("/contrato/firmar")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{"dni":"INVALIDO"}")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void formularioCorrectoDebeRetornar200() {
        webClient.post()
            .uri("/contrato/firmar")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{"nombre":"Ana", "apellidos":"López", "dni":"12345678Z", "email":"ana@email.com", "telefono":"600123456", "firma":"datosfirma"}")
            .exchange()
            .expectStatus().isOk();
    }
}