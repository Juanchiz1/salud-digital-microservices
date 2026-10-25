package com.saluddigital.hce_service.common;

public class FhirInvalidoException extends RuntimeException {
    public FhirInvalidoException(String mensaje) {
        super(mensaje);
    }
}