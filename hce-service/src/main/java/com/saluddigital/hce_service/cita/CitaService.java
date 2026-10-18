package com.saluddigital.hce_service.cita;

import com.saluddigital.hce_service.cita.dto.CitaRequest;
import com.saluddigital.hce_service.cita.dto.CitaResponse;
import com.saluddigital.hce_service.common.ConflictoException;
import com.saluddigital.hce_service.common.RecursoNoEncontradoException;
import com.saluddigital.hce_service.medico.Medico;
import com.saluddigital.hce_service.medico.MedicoRepository;
import com.saluddigital.hce_service.paciente.Paciente;
import com.saluddigital.hce_service.paciente.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CitaService {

    private final CitaRepository citas;
    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;

    public CitaService(CitaRepository citas, PacienteRepository pacientes, MedicoRepository medicos) {
        this.citas = citas;
        this.pacientes = pacientes;
        this.medicos = medicos;
    }

    @Transactional
    public CitaResponse agendar(CitaRequest req) {
        Paciente paciente = pacientes.findById(req.pacienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
        Medico medico = medicos.findById(req.medicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado"));

        Instant fechaHora = req.fechaHora().toInstant();
        if (citas.existsByMedicoIdAndFechaHoraAndEstadoNot(medico.getId(), fechaHora, EstadoCita.CANCELADA)) {
            throw new ConflictoException("El médico ya tiene una cita a esa hora");
        }

        Cita cita = new Cita(paciente, medico, fechaHora, req.motivo().trim());
        return CitaResponse.from(citas.save(cita));
    }

    @Transactional(readOnly = true)
    public CitaResponse buscarPorId(UUID id) {
        return CitaResponse.from(obtener(id));
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listarPorPaciente(UUID pacienteId) {
        return citas.findByPacienteIdOrderByFechaHoraDesc(pacienteId).stream()
                .map(CitaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> agendaDelMedico(UUID medicoId) {
        return citas.findByMedicoIdAndEstadoOrderByFechaHoraAsc(medicoId, EstadoCita.PROGRAMADA).stream()
                .map(CitaResponse::from).toList();
    }

    @Transactional
    public CitaResponse cancelar(UUID id) {
        Cita cita = obtener(id);
        cita.cancelar();
        return CitaResponse.from(cita);
    }

    private Cita obtener(UUID id) {
        return citas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));
    }
}