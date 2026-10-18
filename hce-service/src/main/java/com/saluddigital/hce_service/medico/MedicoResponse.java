package com.saluddigital.hce_service.medico;

import java.util.UUID;

public record MedicoResponse(UUID id, String nombre, String especialidad, String registroMedico) {

    public static MedicoResponse from(Medico m) {
        return new MedicoResponse(m.getId(), m.getNombreCompleto(), m.getEspecialidad(), m.getRegistroMedico());
    }
}