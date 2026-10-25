package com.saluddigital.hce_service.atencion;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "resultado_valor")
public class ResultadoValor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id")
    private OrdenExamen orden;

    @Column(nullable = false)
    private int posicion;

    @Column(nullable = false)
    private String analito;

    @Column(nullable = false)
    private String valor;

    private String unidad;
    private String rangoReferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Interpretacion interpretacion;

    protected ResultadoValor() {
    }

    ResultadoValor(OrdenExamen orden, int posicion, String analito, String valor,
                   String unidad, String rangoReferencia, Interpretacion interpretacion) {
        this.orden = orden;
        this.posicion = posicion;
        this.analito = analito;
        this.valor = valor;
        this.unidad = unidad;
        this.rangoReferencia = rangoReferencia;
        this.interpretacion = interpretacion;
    }

    public String getAnalito() { return analito; }
    public String getValor() { return valor; }
    public String getUnidad() { return unidad; }
    public String getRangoReferencia() { return rangoReferencia; }
    public Interpretacion getInterpretacion() { return interpretacion; }
}