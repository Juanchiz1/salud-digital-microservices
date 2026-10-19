package com.saluddigital.lab_service.fhir;

import com.saluddigital.lab_service.common.FhirInvalidoException;
import com.saluddigital.lab_service.solicitud.dto.SolicitudRequest;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class ServiceRequestMapper {

    static final String SISTEMA_ORDEN = "https://saluddigital.example/fhir/sid/orden-hce";

    public SolicitudRequest aSolicitud(ServiceRequest sr) {
        Patient paciente = sr.getContained().stream()
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .findFirst()
                .orElseThrow(() -> new FhirInvalidoException("El ServiceRequest no trae el paciente contenido"));

        Identifier documento = paciente.getIdentifierFirstRep();
        HumanName nombre = paciente.getNameFirstRep();
        Coding examen = sr.getCode().getCodingFirstRep();
        String nacimiento = paciente.getBirthDateElement().getValueAsString();

        return new SolicitudRequest(
                ordenExternaId(sr),
                documento.getType().getCodingFirstRep().getCode(),
                documento.getValue(),
                (nombre.getGivenAsSingleString() + " " + nombre.getFamily()).trim(),
                sexo(paciente.getGender()),
                nacimiento == null ? null : LocalDate.parse(nacimiento),
                examen.getCode(),
                examen.getDisplay());
    }

    private UUID ordenExternaId(ServiceRequest sr) {
        String valor = sr.getIdentifier().stream()
                .filter(i -> SISTEMA_ORDEN.equals(i.getSystem()))
                .map(Identifier::getValue)
                .findFirst()
                .orElseThrow(() -> new FhirInvalidoException("Falta el identificador de la orden"));
        try {
            return UUID.fromString(valor);
        } catch (IllegalArgumentException e) {
            throw new FhirInvalidoException("Identificador de orden inválido: " + valor);
        }
    }

    private String sexo(AdministrativeGender genero) {
        if (genero == null) {
            return null;
        }
        return switch (genero) {
            case MALE -> "H";
            case FEMALE -> "M";
            case OTHER -> "I";
            default -> null;
        };
    }
}