package com.ejemplo.aplicacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FirmadoSinTokenTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void accesoSinTokenDebeResponder401() {
        webClient.get()
            .uri("/firmado")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}