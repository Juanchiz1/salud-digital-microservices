package com.saluddigital.hce_service.common;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class Fechas {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private Fechas() {
    }

    public static OffsetDateTime local(Instant instante) {
        return instante == null ? null : OffsetDateTime.ofInstant(instante, ZONA);
    }
}