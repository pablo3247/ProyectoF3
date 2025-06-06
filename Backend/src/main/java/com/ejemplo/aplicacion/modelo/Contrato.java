package com.ejemplo.aplicacion.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "contratos")
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String archivoPDF;
    private boolean firmado;

    // 🔽 NUEVOS CAMPOS
    private String estado;
    private String fechaFirma;
    private String email;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getArchivoPDF() { return archivoPDF; }
    public void setArchivoPDF(String archivoPDF) { this.archivoPDF = archivoPDF; }

    public boolean getFirmado() { return firmado; }
    public void setFirmado(boolean firmado) { this.firmado = firmado; }

    // 🔽 NUEVOS GETTERS Y SETTERS
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaFirma() { return fechaFirma; }
    public void setFechaFirma(String fechaFirma) { this.fechaFirma = fechaFirma; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
