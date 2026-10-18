package com.saluddigital.hce_service.paciente.dto;

import com.saluddigital.hce_service.paciente.Paciente;
import com.saluddigital.hce_service.paciente.Sexo;
import com.saluddigital.hce_service.paciente.TipoDocumento;
import java.time.LocalDate;
import java.util.UUID;

public record PacienteResponse(
        UUID id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        Sexo sexo,
        String telefono,
        String email
) {
    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(p.getId(), p.getTipoDocumento(), p.getNumeroDocumento(),
                p.getNombres(), p.getApellidos(), p.getFechaNacimiento(), p.getSexo(),
                p.getTelefono(), p.getEmail());
    }
}