package com.ejemplo.aplicacion.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // título del contrato

    private String dni; // DNI del usuario asociado

    private String email; // Email del usuario

    private String estado; // pendiente | firmado

    private String archivopdf; // nombre del archivo, ej: contrato_1234.pdf

    @Lob
    @Column(name = "archivo_pdf")
    private byte[] archivoPdf; // contenido PDF

    @Column(name = "url_archivo_pdf")
    private String urlArchivoPdf; // URL pública del blob

    private Boolean firmado; // true o false para saber si está firmado

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma; // fecha y hora de la firma

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getArchivopdf() {
        return archivopdf;
    }

    public void setArchivopdf(String archivopdf) {
        this.archivopdf = archivopdf;
    }

    public byte[] getArchivoPdf() {
        return archivoPdf;
    }

    public void setArchivoPdf(byte[] archivoPdf) {
        this.archivoPdf = archivoPdf;
    }

    public String getUrlArchivoPdf() {
        return urlArchivoPdf;
    }

    public void setUrlArchivoPdf(String urlArchivoPdf) {
        this.urlArchivoPdf = urlArchivoPdf;
    }

    public Boolean getFirmado() {
        return firmado;
    }

    public void setFirmado(Boolean firmado) {
        this.firmado = firmado;
    }

    public LocalDateTime getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(LocalDateTime fechaFirma) {
        this.fechaFirma = fechaFirma;
    }
}
