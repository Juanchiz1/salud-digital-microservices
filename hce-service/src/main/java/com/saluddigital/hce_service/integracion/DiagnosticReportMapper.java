package com.saluddigital.hce_service.integracion;

import com.saluddigital.hce_service.atencion.Interpretacion;
import com.saluddigital.hce_service.atencion.ResultadoRecibido;
import com.saluddigital.hce_service.common.FhirInvalidoException;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class DiagnosticReportMapper {

    public ResultadoRecibido aResultado(DiagnosticReport dr) {
        List<ResultadoRecibido.Valor> valores = dr.getContained().stream()
                .filter(Observation.class::isInstance)
                .map(Observation.class::cast)
                .map(this::valor)
                .toList();
        if (valores.isEmpty()) {
            throw new FhirInvalidoException("El DiagnosticReport no trae observaciones");
        }
        Instant emitido = dr.hasIssued() ? dr.getIssued().toInstant() : Instant.now();
        return new ResultadoRecibido(ordenId(dr), dr.getConclusion(), emitido, valores);
    }

    private UUID ordenId(DiagnosticReport dr) {
        String valor = dr.getBasedOn().stream()
                .map(Reference::getIdentifier)
                .filter(i -> ServiceRequestMapper.SISTEMA_ORDEN.equals(i.getSystem()))
                .map(Identifier::getValue)
                .findFirst()
                .orElseThrow(() -> new FhirInvalidoException("Falta basedOn con el identificador de la orden"));
        try {
            return UUID.fromString(valor);
        } catch (IllegalArgumentException e) {
            throw new FhirInvalidoException("Identificador de orden inválido: " + valor);
        }
    }

    private ResultadoRecibido.Valor valor(Observation o) {
        String analito = o.getCode().getText();
        String valor;
        String unidad = null;

        if (o.hasValueQuantity()) {
            Quantity q = o.getValueQuantity();
            valor = q.getValue().toPlainString();
            unidad = q.getUnit();
        } else if (o.hasValueStringType()) {
            valor = o.getValueStringType().getValue();
        } else {
            throw new FhirInvalidoException("Observation sin valor: " + analito);
        }

        if (analito == null || analito.isBlank() || valor == null || valor.isBlank()) {
            throw new FhirInvalidoException("Observation incompleta");
        }

        return new ResultadoRecibido.Valor(analito, valor, unidad,
                o.getReferenceRangeFirstRep().getText(), interpretacion(o));
    }

    private Interpretacion interpretacion(Observation o) {
        String codigo = o.getInterpretationFirstRep().getCodingFirstRep().getCode();
        try {
            return Interpretacion.valueOf(codigo);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new FhirInvalidoException("Interpretación inválida: " + codigo);
        }
    }
}