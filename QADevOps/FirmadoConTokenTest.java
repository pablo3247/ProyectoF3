package com.ejemplo.aplicacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FirmadoConTokenTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void accesoConTokenValidoDebeResponder200() {
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR..."; // JWT válido generado

        webClient.get()
            .uri("/firmado")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus().isOk();
    }
}