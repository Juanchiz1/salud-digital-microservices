package com.saluddigital.lab_service.fhir;

import com.saluddigital.lab_service.solicitud.ResultadoItem;
import com.saluddigital.lab_service.solicitud.Solicitud;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Date;

@Component
public class DiagnosticReportMapper {

    static final String SISTEMA_SOLICITUD = "https://saluddigital.example/fhir/sid/solicitud-lab";
    static final String SISTEMA_CUPS = "https://saluddigital.example/fhir/CodeSystem/cups";
    static final String SISTEMA_INTERPRETACION = "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation";

    public DiagnosticReport aDiagnosticReport(Solicitud s) {
        DiagnosticReport dr = new DiagnosticReport();
        dr.setId(s.getId().toString());
        dr.addIdentifier().setSystem(SISTEMA_SOLICITUD).setValue(s.getId().toString());
        dr.addBasedOn(new Reference().setIdentifier(new Identifier()
                .setSystem(ServiceRequestMapper.SISTEMA_ORDEN)
                .setValue(s.getOrdenExternaId().toString())));
        dr.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        dr.setCode(new CodeableConcept().addCoding(
                new Coding(SISTEMA_CUPS, s.getExamenCodigo(), s.getExamenNombre())));
        dr.setIssued(Date.from(s.getResultadoEn()));
        dr.setConclusion(s.getConclusion());
        for (ResultadoItem item : s.getItems()) {
            dr.addResult(new Reference(observacion(item)));
        }
        return dr;
    }

    private Observation observacion(ResultadoItem item) {
        Observation o = new Observation();
        o.setStatus(Observation.ObservationStatus.FINAL);
        o.setCode(new CodeableConcept().setText(item.getAnalito()));

        BigDecimal numero = comoNumero(item.getValor());
        if (numero != null) {
            o.setValue(new Quantity().setValue(numero).setUnit(item.getUnidad()));
        } else {
            o.setValue(new StringType(item.getValor()));
        }

        if (item.getRangoReferencia() != null) {
            o.addReferenceRange().setText(item.getRangoReferencia());
        }
        o.addInterpretation().addCoding(
                new Coding(SISTEMA_INTERPRETACION, item.getInterpretacion().name(), null));
        return o;
    }

    private BigDecimal comoNumero(String valor) {
        try {
            return new BigDecimal(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}