package com.saluddigital.hce_service.atencion.dto;

import com.saluddigital.hce_service.atencion.EstadoOrden;
import com.saluddigital.hce_service.atencion.OrdenExamen;
import com.saluddigital.hce_service.common.Fechas;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrdenResponse(
        UUID id,
        String examenCodigo,
        String examen,
        EstadoOrden estado,
        OffsetDateTime creadaEn
) {
    public static OrdenResponse from(OrdenExamen o) {
        return new OrdenResponse(o.getId(), o.getExamen().getCodigo(), o.getExamen().getNombre(),
                o.getEstado(), Fechas.local(o.getCreadaEn()));
    }
}