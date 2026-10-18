package com.saluddigital.hce_service.paciente.dto;

import com.saluddigital.hce_service.paciente.Sexo;
import com.saluddigital.hce_service.paciente.TipoDocumento;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PacienteRequest(
        @NotNull TipoDocumento tipoDocumento,
        @NotBlank @Size(max = 20) @Pattern(regexp = "[A-Za-z0-9]+") String numeroDocumento,
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidos,
        @NotNull @Past LocalDate fechaNacimiento,
        @NotNull Sexo sexo,
        @Size(max = 20) String telefono,
        @Email @Size(max = 120) String email
) {
}
