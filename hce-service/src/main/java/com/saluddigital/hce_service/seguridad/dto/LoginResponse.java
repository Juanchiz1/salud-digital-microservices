package com.saluddigital.hce_service.seguridad.dto;

import com.saluddigital.hce_service.seguridad.Rol;
import java.time.OffsetDateTime;

public record LoginResponse(String token, String tipo, OffsetDateTime expiraEn, String nombre, Rol rol) {
}