@Test
void accesoNoAutorizadoDebeFallar() {
    webClient.get()
            .uri("/root/contratos")
            .exchange()
            .expectStatus().isUnauthorized();
}