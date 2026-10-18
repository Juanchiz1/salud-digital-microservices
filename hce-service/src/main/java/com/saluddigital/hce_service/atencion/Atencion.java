package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.cita.Cita;
import com.saluddigital.hce_service.examen.Examen;
import com.saluddigital.hce_service.medico.Medico;
import com.saluddigital.hce_service.paciente.Paciente;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "atencion")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @Column(nullable = false)
    private String motivoConsulta;

    private String notas;

    @Column(nullable = false)
    private String diagnosticoCodigo;

    @Column(nullable = false)
    private String diagnosticoDescripcion;

    @Column(nullable = false, updatable = false)
    private Instant fecha;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL)
    @OrderBy("creadaEn")
    private List<OrdenExamen> ordenes = new ArrayList<>();

    protected Atencion() {
    }

    public Atencion(Cita cita, String motivoConsulta, String notas,
                    String diagnosticoCodigo, String diagnosticoDescripcion) {
        this.cita = cita;
        this.paciente = cita.getPaciente();
        this.medico = cita.getMedico();
        this.motivoConsulta = motivoConsulta;
        this.notas = notas;
        this.diagnosticoCodigo = diagnosticoCodigo;
        this.diagnosticoDescripcion = diagnosticoDescripcion;
    }

    @PrePersist
    void alCrear() {
        this.fecha = Instant.now();
    }

    public void ordenar(Examen examen) {
        ordenes.add(new OrdenExamen(this, examen));
    }

    public UUID getId() { return id; }
    public Cita getCita() { return cita; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getNotas() { return notas; }
    public String getDiagnosticoCodigo() { return diagnosticoCodigo; }
    public String getDiagnosticoDescripcion() { return diagnosticoDescripcion; }
    public Instant getFecha() { return fecha; }
    public List<OrdenExamen> getOrdenes() { return ordenes; }
}