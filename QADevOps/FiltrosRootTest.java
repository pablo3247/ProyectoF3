package com.ejemplo.aplicacion;

@SpringBootTest
public class FiltrosRootTest {

    @Test
    void filtrarPorDNIDebeRetornarContratoCorrecto() {
        webClient.get()
                .uri("/root/contratos?dni=12345678Z")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {token_admin}")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].dni").isEqualTo("12345678Z");
    }
}