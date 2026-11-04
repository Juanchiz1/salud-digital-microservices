package com.saluddigital.hce_service.acceso;

import com.saluddigital.hce_service.cita.CitaRepository;
import com.saluddigital.hce_service.cita.EstadoCita;
import com.saluddigital.hce_service.common.AccesoDenegadoException;
import com.saluddigital.hce_service.seguridad.UsuarioActual;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class AccesoClinicoService {

    private final CitaRepository citas;
    private final AccesoEmergenciaRepository emergencias;
    private final AuditoriaService auditoria;
    private final UsuarioActual usuario;

    public AccesoClinicoService(CitaRepository citas, AccesoEmergenciaRepository emergencias,
                                AuditoriaService auditoria, UsuarioActual usuario) {
        this.citas = citas;
        this.emergencias = emergencias;
        this.auditoria = auditoria;
        this.usuario = usuario;
    }

    public void verificar(UUID pacienteId, RecursoAuditado recurso, UUID recursoId) {
        UUID medicoId = usuario.medicoId();
        Decision decision;

        if (medicoId != null
                && citas.existsByPacienteIdAndMedicoIdAndEstadoNot(pacienteId, medicoId, EstadoCita.CANCELADA)) {
            decision = Decision.PERMITIDO_TRATANTE;
        } else if (medicoId != null
                && emergencias.existsByPacienteIdAndMedicoIdAndExpiraEnAfter(pacienteId, medicoId, Instant.now())) {
            decision = Decision.PERMITIDO_EMERGENCIA;
        } else {
            decision = Decision.DENEGADO;
        }

        auditoria.registrar(pacienteId, recurso, recursoId, decision);

        if (decision == Decision.DENEGADO) {
            throw new AccesoDenegadoException(
                    "No tiene una relación de atención con este paciente. En una urgencia, solicite acceso de emergencia.");
        }
    }
}