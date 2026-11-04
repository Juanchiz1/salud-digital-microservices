package com.saluddigital.hce_service.acceso.dto;

import com.saluddigital.hce_service.acceso.AccesoEmergencia;
import com.saluddigital.hce_service.common.Fechas;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccesoEmergenciaResponse(
        UUID id,
        UUID pacienteId,
        UUID medicoId,
        String usuario,
        String justificacion,
        OffsetDateTime otorgadoEn,
        OffsetDateTime expiraEn,
        boolean vigente,
        String revisadoPor,
        OffsetDateTime revisadoEn,
        String observacionRevision
) {
    public static AccesoEmergenciaResponse from(AccesoEmergencia a) {
        return new AccesoEmergenciaResponse(a.getId(), a.getPacienteId(), a.getMedicoId(), a.getUsuario(),
                a.getJustificacion(), Fechas.local(a.getOtorgadoEn()), Fechas.local(a.getExpiraEn()),
                a.isVigente(), a.getRevisadoPor(), Fechas.local(a.getRevisadoEn()), a.getObservacionRevision());
    }
}