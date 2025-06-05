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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getArchivoPDF() { return archivoPDF; }
    public void setArchivoPDF(String archivoPDF) { this.archivoPDF = archivoPDF; }
    public boolean getFirmado() { return firmado; }
    public void setFirmado(boolean firmado) { this.firmado = firmado; }
}