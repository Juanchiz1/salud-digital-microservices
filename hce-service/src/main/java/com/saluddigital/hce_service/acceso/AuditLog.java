package com.saluddigital.hce_service.acceso;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant fecha;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String rol;

    private UUID pacienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecursoAuditado recurso;

    private UUID recursoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    protected AuditLog() {
    }

    public AuditLog(String usuario, String rol, UUID pacienteId,
                    RecursoAuditado recurso, UUID recursoId, Decision decision) {
        this.fecha = Instant.now();
        this.usuario = usuario;
        this.rol = rol;
        this.pacienteId = pacienteId;
        this.recurso = recurso;
        this.recursoId = recursoId;
        this.decision = decision;
    }

    public Long getId() { return id; }
    public Instant getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
    public UUID getPacienteId() { return pacienteId; }
    public RecursoAuditado getRecurso() { return recurso; }
    public UUID getRecursoId() { return recursoId; }
    public Decision getDecision() { return decision; }
}