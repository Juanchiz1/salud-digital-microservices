package com.saluddigital.hce_service.seguridad;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    private UUID medicoId;

    @Column(nullable = false)
    private boolean activo;

    protected Usuario() {
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getNombre() { return nombre; }
    public Rol getRol() { return rol; }
    public UUID getMedicoId() { return medicoId; }
    public boolean isActivo() { return activo; }
}