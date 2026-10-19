package com.saluddigital.hce_service.integracion;

import com.saluddigital.hce_service.atencion.OrdenExamen;
import com.saluddigital.hce_service.paciente.Paciente;
import com.saluddigital.hce_service.paciente.Sexo;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class ServiceRequestMapper {

    static final String SISTEMA_ORDEN = "https://saluddigital.example/fhir/sid/orden-hce";
    static final String SISTEMA_TIPO_DOCUMENTO = "https://saluddigital.example/fhir/CodeSystem/tipo-documento";
    static final String SISTEMA_CUPS = "https://saluddigital.example/fhir/CodeSystem/cups";

    public ServiceRequest aServiceRequest(OrdenExamen orden) {
        Paciente p = orden.getAtencion().getPaciente();

        Patient paciente = new Patient();
        paciente.addIdentifier()
                .setType(new CodeableConcept().addCoding(
                        new Coding(SISTEMA_TIPO_DOCUMENTO, p.getTipoDocumento().name(), null)))
                .setValue(p.getNumeroDocumento());
        paciente.addName().setFamily(p.getApellidos()).addGiven(p.getNombres());
        paciente.setGender(genero(p.getSexo()));
        paciente.setBirthDateElement(new DateType(p.getFechaNacimiento().toString()));

        ServiceRequest sr = new ServiceRequest();
        sr.setId(orden.getId().toString());
        sr.addIdentifier().setSystem(SISTEMA_ORDEN).setValue(orden.getId().toString());
        sr.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
        sr.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
        sr.setCode(new CodeableConcept().addCoding(
                new Coding(SISTEMA_CUPS, orden.getExamen().getCodigo(), orden.getExamen().getNombre())));
        sr.setSubject(new Reference(paciente));
        sr.setAuthoredOnElement(new DateTimeType(Date.from(orden.getCreadaEn())));
        return sr;
    }

    private AdministrativeGender genero(Sexo sexo) {
        return switch (sexo) {
            case H -> AdministrativeGender.MALE;
            case M -> AdministrativeGender.FEMALE;
            case I -> AdministrativeGender.OTHER;
        };
    }
}