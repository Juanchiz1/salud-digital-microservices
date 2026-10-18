package com.saluddigital.hce_service.atencion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record AtencionRequest(
        @NotNull UUID citaId,
        @NotBlank @Size(max = 500) String motivoConsulta,
        @Size(max = 2000) String notas,
        @NotBlank @Pattern(regexp = "[A-Z][0-9]{2}[0-9X]", message = "debe ser un código CIE-10 de 4 caracteres, ej. R51X")
        String diagnosticoCodigo,
        @NotBlank @Size(max = 255) String diagnosticoDescripcion,
        List<@NotBlank String> examenes
) {
}