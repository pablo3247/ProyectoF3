package com.ejemplo.aplicacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuditoriaLogTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void firmaContratoDebeRegistrarEnAuditoria() {
        webClient.post()
                .uri("/contrato/firmar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"nombre\":\"Ana\", \"dni\":\"12345678Z\"}")
                .exchange()
                .expectStatus().isOk();

        // Verificar logs (simulado)
        assertTrue(Files.readAllLines(Paths.get("/var/log/contratos.log"))
                .stream().anyMatch(line -> line.contains("AUDIT")));
    }
}