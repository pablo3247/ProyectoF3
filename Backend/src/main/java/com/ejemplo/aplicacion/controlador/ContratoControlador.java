package com.ejemplo.aplicacion.controlador;

import com.ejemplo.aplicacion.modelo.Contrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import com.ejemplo.aplicacion.repositorio.RepositorioContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/contrato")
public class ContratoControlador {

    @Autowired
    private RepositorioContrato contratoRepositorio;

    @GetMapping("/status/{id}")
    public Map<String, Object> obtenerEstadoContrato(@PathVariable Long id) {
        Optional<Contrato> contratoOpt = contratoRepositorio.findById(id);

        if (contratoOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Contrato no encontrado");
            return error;
        }

        Contrato contrato = contratoOpt.get();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", contrato.getId());
        respuesta.put("estado", contrato.getEstado());
        respuesta.put("fechaFirma", contrato.getFechaFirma());
        respuesta.put("email", contrato.getEmail());

        return respuesta;
    }

    @GetMapping
    public List<Contrato> obtenerTodosLosContratos() {
        return contratoRepositorio.findAll();
    }

}
