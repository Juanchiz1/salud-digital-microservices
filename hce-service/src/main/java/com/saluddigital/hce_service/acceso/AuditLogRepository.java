package com.saluddigital.hce_service.acceso;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByPacienteIdOrderByFechaDesc(UUID pacienteId);
}