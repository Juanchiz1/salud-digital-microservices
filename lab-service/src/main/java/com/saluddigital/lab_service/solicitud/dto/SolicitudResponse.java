package com.saluddigital.lab_service.solicitud.dto;

import com.saluddigital.lab_service.common.Fechas;
import com.saluddigital.lab_service.solicitud.EstadoSolicitud;
import com.saluddigital.lab_service.solicitud.Solicitud;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SolicitudResponse(
        UUID id,
        UUID ordenExternaId,
        String tipoDocumento,
        String numeroDocumento,
        String pacienteNombre,
        String examenCodigo,
        String examenNombre,
        EstadoSolicitud estado,
        String conclusion,
        OffsetDateTime recibidaEn,
        OffsetDateTime resultadoEn,
        List<ResultadoItemResponse> items
) {
    public static SolicitudResponse from(Solicitud s) {
        return new SolicitudResponse(s.getId(), s.getOrdenExternaId(), s.getTipoDocumento(),
                s.getNumeroDocumento(), s.getPacienteNombre(), s.getExamenCodigo(),
                s.getExamenNombre(), s.getEstado(), s.getConclusion(),
                Fechas.local(s.getRecibidaEn()), Fechas.local(s.getResultadoEn()),
                s.getItems().stream().map(ResultadoItemResponse::from).toList());
    }
}