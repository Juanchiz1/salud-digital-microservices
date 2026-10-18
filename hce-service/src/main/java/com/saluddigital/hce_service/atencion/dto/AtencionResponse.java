package com.saluddigital.hce_service.atencion.dto;

import com.saluddigital.hce_service.atencion.Atencion;
import com.saluddigital.hce_service.common.Fechas;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AtencionResponse(
        UUID id,
        UUID citaId,
        UUID pacienteId,
        String paciente,
        String medico,
        String motivoConsulta,
        String notas,
        String diagnosticoCodigo,
        String diagnosticoDescripcion,
        OffsetDateTime fecha,
        List<OrdenResponse> ordenes
) {
    public static AtencionResponse from(Atencion a) {
        var p = a.getPaciente();
        return new AtencionResponse(a.getId(), a.getCita().getId(), p.getId(),
                p.getNombres() + " " + p.getApellidos(), a.getMedico().getNombreCompleto(),
                a.getMotivoConsulta(), a.getNotas(), a.getDiagnosticoCodigo(),
                a.getDiagnosticoDescripcion(), Fechas.local(a.getFecha()),
                a.getOrdenes().stream().map(OrdenResponse::from).toList());
    }
}