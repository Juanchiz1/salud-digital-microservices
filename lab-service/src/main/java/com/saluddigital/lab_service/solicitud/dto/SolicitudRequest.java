package com.saluddigital.lab_service.solicitud.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record SolicitudRequest(
        @NotNull UUID ordenExternaId,
        @NotBlank @Pattern(regexp = "CC|TI|CE|RC|PA|PT") String tipoDocumento,
        @NotBlank @Size(max = 20) String numeroDocumento,
        @NotBlank @Size(max = 200) String pacienteNombre,
        @NotBlank @Pattern(regexp = "[HMI]") String sexo,
        @NotNull @Past LocalDate fechaNacimiento,
        @NotBlank @Size(max = 10) String examenCodigo,
        @NotBlank @Size(max = 150) String examenNombre
) {
}