package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.examen.Examen;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import com.saluddigital.hce_service.common.ConflictoException;
import java.util.ArrayList;
import java.util.List;

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
    private String conclusion;
    private Instant resultadoEmitidoEn;
    private Instant resultadoRecibidoEn;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    @OrderBy("posicion")
    private List<ResultadoValor> valores = new ArrayList<>();

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

        public void agregarValor(String analito, String valor, String unidad,
                             String rangoReferencia, Interpretacion interpretacion) {
        validarSinResultado();
        valores.add(new ResultadoValor(this, valores.size() + 1, analito, valor,
                unidad, rangoReferencia, interpretacion));
    }

    public void completar(String conclusion, Instant emitidoEn) {
        validarSinResultado();
        this.conclusion = conclusion;
        this.resultadoEmitidoEn = emitidoEn;
        this.resultadoRecibidoEn = Instant.now();
        this.estado = EstadoOrden.COMPLETADA;
        this.proximoIntento = null;
    }

    private void validarSinResultado() {
        if (estado == EstadoOrden.COMPLETADA) {
            throw new ConflictoException("La orden ya tiene resultado");
        }
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
        public String getConclusion() { return conclusion; }
    public Instant getResultadoEmitidoEn() { return resultadoEmitidoEn; }
    public Instant getResultadoRecibidoEn() { return resultadoRecibidoEn; }
    public List<ResultadoValor> getValores() { return valores; }
}