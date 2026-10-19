package com.saluddigital.hce_service.integracion;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/fhir")
public class FhirController {

    private final EnvioOrdenService envio;

    public FhirController(EnvioOrdenService envio) {
        this.envio = envio;
    }

    @GetMapping("/ServiceRequest/{id}")
    public ResponseEntity<String> serviceRequest(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/fhir+json"))
                .body(envio.verServiceRequest(id));
    }
}