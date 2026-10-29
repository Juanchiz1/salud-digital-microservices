package com.saluddigital.hce_service.common;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}
