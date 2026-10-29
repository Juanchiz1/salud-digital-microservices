package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.atencion.dto.AtencionRequest;
import com.saluddigital.hce_service.atencion.dto.AtencionResponse;
import com.saluddigital.hce_service.cita.Cita;
import com.saluddigital.hce_service.cita.CitaRepository;
import com.saluddigital.hce_service.common.RecursoNoEncontradoException;
import com.saluddigital.hce_service.examen.Examen;
import com.saluddigital.hce_service.examen.ExamenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.saluddigital.hce_service.common.AccesoDenegadoException;
import com.saluddigital.hce_service.seguridad.UsuarioActual;

@Service
public class AtencionService {

    private final AtencionRepository atenciones;
    private final CitaRepository citas;
    private final ExamenRepository examenes;
    private final UsuarioActual usuario;

    public AtencionService(AtencionRepository atenciones, CitaRepository citas, ExamenRepository examenes, UsuarioActual usuario) {
        this.atenciones = atenciones;
        this.citas = citas;
        this.examenes = examenes;
        this.usuario = usuario;
    }

    @Transactional
    public AtencionResponse registrar(AtencionRequest req) {
        Cita cita = citas.findById(req.citaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));
            if (!cita.getMedico().getId().equals(usuario.medicoId())) {
            throw new AccesoDenegadoException("Solo el médico asignado a la cita puede registrar la atención");
        }
        cita.atender();

        Atencion atencion = new Atencion(cita, req.motivoConsulta().trim(), req.notas(),
                req.diagnosticoCodigo(), req.diagnosticoDescripcion().trim());

        Set<String> codigos = req.examenes() == null ? Set.of() : new LinkedHashSet<>(req.examenes());
        for (String codigo : codigos) {
            Examen examen = examenes.findById(codigo)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Examen no encontrado: " + codigo));
            atencion.ordenar(examen);
        }

        return AtencionResponse.from(atenciones.save(atencion));
    }

    @Transactional(readOnly = true)
    public AtencionResponse buscarPorId(UUID id) {
        return atenciones.findDetalleById(id)
                .map(AtencionResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Atención no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<AtencionResponse> historiaDelPaciente(UUID pacienteId) {
        return atenciones.findByPacienteIdOrderByFechaDesc(pacienteId).stream()
                .map(AtencionResponse::from).toList();
    }
}