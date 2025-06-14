package com.ejemplo.aplicacion.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "url_archivo_pdf", length = 512)
    private String urlArchivoPdf;

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

    public String getUrlArchivoPdf() {
        return urlArchivoPdf;
    }

    public void setUrlArchivoPdf(String urlArchivoPdf) {
        this.urlArchivoPdf = urlArchivoPdf;
    }
}
