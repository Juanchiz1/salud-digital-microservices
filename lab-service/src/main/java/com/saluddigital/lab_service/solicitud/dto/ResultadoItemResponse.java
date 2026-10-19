package com.saluddigital.lab_service.solicitud.dto;

import com.saluddigital.lab_service.solicitud.Interpretacion;
import com.saluddigital.lab_service.solicitud.ResultadoItem;

public record ResultadoItemResponse(
        String analito,
        String valor,
        String unidad,
        String rangoReferencia,
        Interpretacion interpretacion
) {
    public static ResultadoItemResponse from(ResultadoItem i) {
        return new ResultadoItemResponse(i.getAnalito(), i.getValor(), i.getUnidad(),
                i.getRangoReferencia(), i.getInterpretacion());
    }
}