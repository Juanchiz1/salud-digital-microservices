package com.saluddigital.lab_service.solicitud.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ResultadoRequest(
        @Size(max = 500) String conclusion,
        @NotEmpty List<@Valid ResultadoItemRequest> items
) {
}