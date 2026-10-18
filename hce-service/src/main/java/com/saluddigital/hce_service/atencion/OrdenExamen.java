package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.examen.Examen;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orden_examen")
public class OrdenExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atencion_id")
    private Atencion atencion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examen_codigo")
    private Examen examen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado;

    @Column(nullable = false, updatable = false)
    private Instant creadaEn;

    protected OrdenExamen() {
    }

    OrdenExamen(Atencion atencion, Examen examen) {
        this.atencion = atencion;
        this.examen = examen;
        this.estado = EstadoOrden.CREADA;
    }

    @PrePersist
    void alCrear() {
        this.creadaEn = Instant.now();
    }

    public UUID getId() { return id; }
    public Atencion getAtencion() { return atencion; }
    public Examen getExamen() { return examen; }
    public EstadoOrden getEstado() { return estado; }
    public Instant getCreadaEn() { return creadaEn; }
}