package com.saluddigital.hce_service.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record AccesoEmergenciaRequest(
        @NotNull UUID pacienteId,
        @NotBlank @Size(min = 20, max = 500, message = "la justificación debe tener entre 20 y 500 caracteres")
        String justificacion
) {
}