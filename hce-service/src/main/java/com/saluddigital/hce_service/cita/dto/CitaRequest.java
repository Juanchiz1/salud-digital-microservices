package com.saluddigital.hce_service.cita.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CitaRequest(
        @NotNull UUID pacienteId,
        @NotNull UUID medicoId,
        @NotNull @Future OffsetDateTime fechaHora,
        @NotBlank @Size(max = 255) String motivo
) {
}