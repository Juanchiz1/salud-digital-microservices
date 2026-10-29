package com.saluddigital.hce_service.seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UsuarioActual {

    public Jwt token() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken();
        }
        throw new IllegalStateException("No hay un usuario autenticado en esta petición");
    }

    public String username() { return token().getSubject(); }
    public String nombre() { return token().getClaimAsString("nombre"); }
    public String rol() { return token().getClaimAsString("rol"); }

    public UUID medicoId() {
        String valor = token().getClaimAsString("medicoId");
        return valor == null ? null : UUID.fromString(valor);
    }
}