package com.saluddigital.hce_service.atencion.dto;

import com.saluddigital.hce_service.atencion.Interpretacion;
import com.saluddigital.hce_service.atencion.OrdenExamen;
import com.saluddigital.hce_service.common.Fechas;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ResultadoResponse(
        UUID ordenId,
        UUID pacienteId,
        String paciente,
        String examenCodigo,
        String examen,
        String conclusion,
        OffsetDateTime emitidoEn,
        OffsetDateTime recibidoEn,
        List<Valor> valores
) {
    public record Valor(String analito, String valor, String unidad,
                        String rangoReferencia, Interpretacion interpretacion) {
    }

    public static ResultadoResponse from(OrdenExamen o) {
        var p = o.getAtencion().getPaciente();
        return new ResultadoResponse(o.getId(), p.getId(), p.getNombres() + " " + p.getApellidos(),
                o.getExamen().getCodigo(), o.getExamen().getNombre(), o.getConclusion(),
                Fechas.local(o.getResultadoEmitidoEn()), Fechas.local(o.getResultadoRecibidoEn()),
                o.getValores().stream()
                        .map(v -> new Valor(v.getAnalito(), v.getValor(), v.getUnidad(),
                                v.getRangoReferencia(), v.getInterpretacion()))
                        .toList());
    }
}