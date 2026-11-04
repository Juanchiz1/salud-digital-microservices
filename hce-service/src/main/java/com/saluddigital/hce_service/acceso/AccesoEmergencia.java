package com.saluddigital.hce_service.acceso;

import com.saluddigital.hce_service.common.ConflictoException;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "acceso_emergencia")
public class AccesoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID pacienteId;

    @Column(nullable = false, updatable = false)
    private UUID medicoId;

    @Column(nullable = false, updatable = false)
    private String usuario;

    @Column(nullable = false, updatable = false)
    private String justificacion;

    @Column(nullable = false, updatable = false)
    private Instant otorgadoEn;

    @Column(nullable = false, updatable = false)
    private Instant expiraEn;

    private String revisadoPor;
    private Instant revisadoEn;
    private String observacionRevision;

    protected AccesoEmergencia() {
    }

    public AccesoEmergencia(UUID pacienteId, UUID medicoId, String usuario,
                            String justificacion, Duration duracion) {
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.usuario = usuario;
        this.justificacion = justificacion;
        this.otorgadoEn = Instant.now();
        this.expiraEn = otorgadoEn.plus(duracion);
    }

    public void revisar(String revisor, String observacion) {
        if (revisadoEn != null) {
            throw new ConflictoException("Este acceso de emergencia ya fue revisado");
        }
        this.revisadoPor = revisor;
        this.revisadoEn = Instant.now();
        this.observacionRevision = observacion;
    }

    public boolean isVigente() {
        return Instant.now().isBefore(expiraEn);
    }

    public UUID getId() { return id; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getMedicoId() { return medicoId; }
    public String getUsuario() { return usuario; }
    public String getJustificacion() { return justificacion; }
    public Instant getOtorgadoEn() { return otorgadoEn; }
    public Instant getExpiraEn() { return expiraEn; }
    public String getRevisadoPor() { return revisadoPor; }
    public Instant getRevisadoEn() { return revisadoEn; }
    public String getObservacionRevision() { return observacionRevision; }
}