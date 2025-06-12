package com.ejemplo.aplicacion;

import com.ejemplo.aplicacion.controlador.ControladorAutenticacion;
import com.ejemplo.aplicacion.dto.PeticionLogin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControladorAutenticacionTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void loginDebeResponder200OK() {
        webClient.post()
            .uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{"username": "admin", "password": "admin123"}")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.token").isNotEmpty();
    }

    @Test
    void loginConCredencialesInvalidasDebeFallar() {
        webClient.post()
            .uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{"username": "fake", "password": "wrong"}")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}