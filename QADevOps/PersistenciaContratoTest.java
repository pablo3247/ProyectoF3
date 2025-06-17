package com.ejemplo.aplicacion;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class PersistenciaContratoTest {

    @Autowired
    private RepositorioContrato repo;

    @Test
    void contratoSeGuardaYRecuperaCorrectamente() {
        Contrato contrato = new Contrato("cliente@example.com", "PDF_firmado");
        repo.save(contrato);

        Optional<Contrato> encontrado = repo.findByEmail("cliente@example.com");
        Assertions.assertTrue(encontrado.isPresent());
    }
}