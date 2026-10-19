package com.saluddigital.lab_service.solicitud.dto;

import com.saluddigital.lab_service.solicitud.Interpretacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResultadoItemRequest(
        @NotBlank @Size(max = 100) String analito,
        @NotBlank @Size(max = 50) String valor,
        @Size(max = 30) String unidad,
        @Size(max = 50) String rangoReferencia,
        @NotNull Interpretacion interpretacion
) {
}