package com.saluddigital.hce_service.atencion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResultadoRecibido(UUID ordenId, String conclusion, Instant emitidoEn, List<Valor> valores) {

    public record Valor(String analito, String valor, String unidad,
                        String rangoReferencia, Interpretacion interpretacion) {
    }
}