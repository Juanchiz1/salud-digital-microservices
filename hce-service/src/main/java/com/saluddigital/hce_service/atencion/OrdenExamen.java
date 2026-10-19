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

    @Column(nullable = false)
    private int intentosEnvio;

    private String ultimoError;
    private Instant proximoIntento;
    private Instant enviadaEn;

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

        public void marcarEnviada() {
        this.estado = EstadoOrden.ENVIADA;
        this.enviadaEn = Instant.now();
        this.ultimoError = null;
        this.proximoIntento = null;
    }

    public void registrarFallo(String error) {
        this.intentosEnvio++;
        String mensaje = error == null ? "Error desconocido" : error;
        this.ultimoError = mensaje.length() > 500 ? mensaje.substring(0, 500) : mensaje;
        long espera = Math.min(10L << Math.min(intentosEnvio - 1, 5), 300);
        this.proximoIntento = Instant.now().plusSeconds(espera);
    }

    public UUID getId() { return id; }
    public Atencion getAtencion() { return atencion; }
    public Examen getExamen() { return examen; }
    public EstadoOrden getEstado() { return estado; }
    public Instant getCreadaEn() { return creadaEn; }
        public int getIntentosEnvio() { return intentosEnvio; }
    public String getUltimoError() { return ultimoError; }
    public Instant getProximoIntento() { return proximoIntento; }
    public Instant getEnviadaEn() { return enviadaEn; }
}