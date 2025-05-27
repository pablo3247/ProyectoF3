package com.ejemplo.aplicacion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "firmas")
public class Firma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"contrato\"")
    private Long contrato;

    @Column(name = "\"usuario\"")
    private Long usuario;

    @Column(name = "\"fecha\"")
    private LocalDateTime fecha;

    @Column(name = "\"tipo_Firma\"")
    private String tipoFirma;

    @Column(name = "\"valido\"")
    private boolean valido;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getContrato() { return contrato; }
    public void setContrato(Long contrato) { this.contrato = contrato; }

    public Long getUsuario() { return usuario; }
    public void setUsuario(Long usuario) { this.usuario = usuario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getTipoFirma() { return tipoFirma; }
    public void setTipoFirma(String tipoFirma) { this.tipoFirma = tipoFirma; }

    public boolean getValido() { return valido; }
    public void setValido(boolean valido) { this.valido = valido; }
}
