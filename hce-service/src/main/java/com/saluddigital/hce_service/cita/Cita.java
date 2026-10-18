package com.saluddigital.hce_service.cita;

import com.saluddigital.hce_service.common.ConflictoException;
import com.saluddigital.hce_service.medico.Medico;
import com.saluddigital.hce_service.paciente.Paciente;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @Column(nullable = false)
    private Instant fechaHora;

    @Column(nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado;

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    protected Cita() {
    }

    public Cita(Paciente paciente, Medico medico, Instant fechaHora, String motivo) {
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = EstadoCita.PROGRAMADA;
    }

    @PrePersist
    void alCrear() {
        this.creadoEn = Instant.now();
    }

    public void cancelar() {
        if (estado != EstadoCita.PROGRAMADA) {
            throw new ConflictoException("Solo se puede cancelar una cita programada");
        }
        this.estado = EstadoCita.CANCELADA;
    }

    public UUID getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public Instant getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public EstadoCita getEstado() { return estado; }
    public Instant getCreadoEn() { return creadoEn; }
}