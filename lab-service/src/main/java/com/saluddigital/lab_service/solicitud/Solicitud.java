package com.saluddigital.lab_service.solicitud;

import com.saluddigital.lab_service.common.ConflictoException;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID ordenExternaId;

    @Column(nullable = false)
    private String tipoDocumento;

    @Column(nullable = false)
    private String numeroDocumento;

    @Column(nullable = false)
    private String pacienteNombre;

    @Column(nullable = false)
    private String sexo;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String examenCodigo;

    @Column(nullable = false)
    private String examenNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    private String conclusion;

    @Column(nullable = false, updatable = false)
    private Instant recibidaEn;

    private Instant resultadoEn;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    @OrderBy("posicion")
    private List<ResultadoItem> items = new ArrayList<>();

    protected Solicitud() {
    }

    public Solicitud(UUID ordenExternaId, String tipoDocumento, String numeroDocumento,
                     String pacienteNombre, String sexo, LocalDate fechaNacimiento,
                     String examenCodigo, String examenNombre) {
        this.ordenExternaId = ordenExternaId;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.pacienteNombre = pacienteNombre;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.examenCodigo = examenCodigo;
        this.examenNombre = examenNombre;
        this.estado = EstadoSolicitud.RECIBIDA;
    }

    @PrePersist
    void alRecibir() {
        this.recibidaEn = Instant.now();
    }

    public void agregarItem(String analito, String valor, String unidad,
                            String rangoReferencia, Interpretacion interpretacion) {
        validarAbierta();
        items.add(new ResultadoItem(this, items.size() + 1, analito, valor,
                unidad, rangoReferencia, interpretacion));
    }

    public void registrarResultado(String conclusion) {
        validarAbierta();
        this.conclusion = conclusion;
        this.estado = EstadoSolicitud.RESULTADO_LISTO;
        this.resultadoEn = Instant.now();
    }

    private void validarAbierta() {
        if (estado != EstadoSolicitud.RECIBIDA) {
            throw new ConflictoException("La solicitud ya tiene resultado registrado");
        }
    }

    public UUID getId() { return id; }
    public UUID getOrdenExternaId() { return ordenExternaId; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getPacienteNombre() { return pacienteNombre; }
    public String getSexo() { return sexo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getExamenCodigo() { return examenCodigo; }
    public String getExamenNombre() { return examenNombre; }
    public EstadoSolicitud getEstado() { return estado; }
    public String getConclusion() { return conclusion; }
    public Instant getRecibidaEn() { return recibidaEn; }
    public Instant getResultadoEn() { return resultadoEn; }
    public List<ResultadoItem> getItems() { return items; }
}