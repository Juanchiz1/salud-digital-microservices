package com.saluddigital.lab_service.common;

public class FhirInvalidoException extends RuntimeException {
    public FhirInvalidoException(String mensaje) {
        super(mensaje);
    }
}