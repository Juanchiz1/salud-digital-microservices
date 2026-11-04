package com.saluddigital.hce_service.acceso.dto;

import com.saluddigital.hce_service.acceso.AuditLog;
import com.saluddigital.hce_service.acceso.Decision;
import com.saluddigital.hce_service.acceso.RecursoAuditado;
import com.saluddigital.hce_service.common.Fechas;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AuditLogResponse(
        OffsetDateTime fecha,
        String usuario,
        String rol,
        RecursoAuditado recurso,
        UUID recursoId,
        Decision decision
) {
    public static AuditLogResponse from(AuditLog a) {
        return new AuditLogResponse(Fechas.local(a.getFecha()), a.getUsuario(), a.getRol(),
                a.getRecurso(), a.getRecursoId(), a.getDecision());
    }
}