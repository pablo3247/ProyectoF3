@Test
void editarMetadatosDebeRegistrarEnLog() {
    webClient.put()
            .uri("/root/contrato/edit/1")
            .bodyValue("{\"dni\":\"NUEVO_DNI\"}")
            .exchange()
            .expectStatus().isOk();
}