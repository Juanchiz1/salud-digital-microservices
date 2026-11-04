package com.saluddigital.hce_service.acceso;

import com.saluddigital.hce_service.acceso.dto.AccesoEmergenciaRequest;
import com.saluddigital.hce_service.acceso.dto.AccesoEmergenciaResponse;
import com.saluddigital.hce_service.common.RecursoNoEncontradoException;
import com.saluddigital.hce_service.paciente.PacienteRepository;
import com.saluddigital.hce_service.seguridad.UsuarioActual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class AccesoEmergenciaService {

    private static final Duration DURACION = Duration.ofHours(4);

    private final AccesoEmergenciaRepository repository;
    private final PacienteRepository pacientes;
    private final AuditoriaService auditoria;
    private final UsuarioActual usuario;

    public AccesoEmergenciaService(AccesoEmergenciaRepository repository, PacienteRepository pacientes,
                                   AuditoriaService auditoria, UsuarioActual usuario) {
        this.repository = repository;
        this.pacientes = pacientes;
        this.auditoria = auditoria;
        this.usuario = usuario;
    }

    @Transactional
    public AccesoEmergenciaResponse activar(AccesoEmergenciaRequest req) {
        if (!pacientes.existsById(req.pacienteId())) {
            throw new RecursoNoEncontradoException("Paciente no encontrado");
        }
        AccesoEmergencia acceso = repository.save(new AccesoEmergencia(req.pacienteId(), usuario.medicoId(),
                usuario.username(), req.justificacion().trim(), DURACION));
        auditoria.registrar(req.pacienteId(), RecursoAuditado.ACCESO_EMERGENCIA, acceso.getId(),
                Decision.EMERGENCIA_ACTIVADA);
        return AccesoEmergenciaResponse.from(acceso);
    }

    @Transactional(readOnly = true)
    public List<AccesoEmergenciaResponse> listar(boolean soloPendientes) {
        List<AccesoEmergencia> lista = soloPendientes
                ? repository.findByRevisadoEnIsNullOrderByOtorgadoEnDesc()
                : repository.findAllByOrderByOtorgadoEnDesc();
        return lista.stream().map(AccesoEmergenciaResponse::from).toList();
    }

    @Transactional
    public AccesoEmergenciaResponse revisar(UUID id, String observacion) {
        AccesoEmergencia acceso = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acceso de emergencia no encontrado"));
        acceso.revisar(usuario.username(), observacion.trim());
        return AccesoEmergenciaResponse.from(acceso);
    }
}