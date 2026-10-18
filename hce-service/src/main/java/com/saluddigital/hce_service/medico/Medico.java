package com.saluddigital.hce_service.medico;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "medico")
public class Medico {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false)
    private String registroMedico;

    protected Medico() {
    }

    public UUID getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getEspecialidad() { return especialidad; }
    public String getRegistroMedico() { return registroMedico; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}