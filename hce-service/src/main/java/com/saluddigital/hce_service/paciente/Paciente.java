package com.saluddigital.hce_service.paciente;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false)
    private String numeroDocumento;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    private String telefono;
    private String email;

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    protected Paciente() {
    }

    public Paciente(TipoDocumento tipoDocumento, String numeroDocumento, String nombres,
                    String apellidos, LocalDate fechaNacimiento, Sexo sexo,
                    String telefono, String email) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
    }

    @PrePersist
    void alCrear() {
        this.creadoEn = Instant.now();
    }

    public UUID getId() { return id; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public Sexo getSexo() { return sexo; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public Instant getCreadoEn() { return creadoEn; }
}