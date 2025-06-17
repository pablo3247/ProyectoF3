package com.ejemplo.aplicacion.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "contrato")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // título del contrato

    private String dni; // DNI del usuario asociado

    private String email; // Email del usuario

    private String estado; // pendiente | firmado

    @Lob
    @Column(name = "archivo_pdf")
    private byte[] archivoPdf;

    @Column(name = "url_archivo_pdf")
    private String urlArchivoPdf; // Nueva propiedad para guardar la URL del archivo en blob

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
}
