package com.saluddigital.hce_service.acceso;

import com.saluddigital.hce_service.acceso.dto.AuditLogResponse;
import com.saluddigital.hce_service.seguridad.UsuarioActual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AuditoriaService {

    private final AuditLogRepository repository;
    private final UsuarioActual usuario;

    public AuditoriaService(AuditLogRepository repository, UsuarioActual usuario) {
        this.repository = repository;
        this.usuario = usuario;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(UUID pacienteId, RecursoAuditado recurso, UUID recursoId, Decision decision) {
        repository.save(new AuditLog(usuario.username(), usuario.rol(), pacienteId, recurso, recursoId, decision));
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> porPaciente(UUID pacienteId) {
        return repository.findByPacienteIdOrderByFechaDesc(pacienteId).stream()
                .map(AuditLogResponse::from).toList();
    }
}