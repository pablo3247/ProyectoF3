package com.ejemplo.aplicacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContratoControladorTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void contratoViewDebeResponder200OK() {
        webClient.get()
            .uri("/contrato/view/1")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void contratoInexistenteDebeResponder404() {
        webClient.get()
            .uri("/contrato/view/999")
            .exchange()
            .expectStatus().isNotFound();
    }
}