package com.saluddigital.hce_service.cita.dto;

import com.saluddigital.hce_service.cita.Cita;
import com.saluddigital.hce_service.cita.EstadoCita;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record CitaResponse(
        UUID id,
        UUID pacienteId,
        String paciente,
        UUID medicoId,
        String medico,
        String especialidad,
        OffsetDateTime fechaHora,
        String motivo,
        EstadoCita estado
) {
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    public static CitaResponse from(Cita c) {
        var p = c.getPaciente();
        var m = c.getMedico();
        return new CitaResponse(c.getId(), p.getId(), p.getNombres() + " " + p.getApellidos(),
                m.getId(), m.getNombreCompleto(), m.getEspecialidad(),
                OffsetDateTime.ofInstant(c.getFechaHora(), ZONA), c.getMotivo(), c.getEstado());
    }
}