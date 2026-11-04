package com.saluddigital.hce_service.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RevisionRequest(@NotBlank @Size(max = 500) String observacion) {
}