package com.saluddigital.hce_service.seguridad.dto;

import java.util.UUID;

public record UsuarioResponse(String username, String nombre, String rol, UUID medicoId) {
}