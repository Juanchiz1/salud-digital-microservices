package com.saluddigital.hce_service.examen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "examen")
public class Examen {

    @Id
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    protected Examen() {
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
}