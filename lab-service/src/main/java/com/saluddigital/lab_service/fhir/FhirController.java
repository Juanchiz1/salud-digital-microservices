package com.saluddigital.lab_service.fhir;

import ca.uhn.fhir.context.FhirContext;
import com.saluddigital.lab_service.common.FhirInvalidoException;
import com.saluddigital.lab_service.solicitud.SolicitudService;
import com.saluddigital.lab_service.solicitud.dto.SolicitudRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fhir")
public class FhirController {

    private final FhirContext fhir;
    private final ServiceRequestMapper mapper;
    private final SolicitudService service;
    private final NotificacionResultadoService notificacion;
    private final Validator validator;

    public FhirController(FhirContext fhir, ServiceRequestMapper mapper, SolicitudService service,
                          NotificacionResultadoService notificacion, Validator validator) {
        this.fhir = fhir;
        this.mapper = mapper;
        this.service = service;
        this.notificacion = notificacion;
        this.validator = validator;
    }

    @PostMapping(value = "/ServiceRequest", consumes = {"application/fhir+json", "application/json"})
    public ResponseEntity<Void> recibir(@RequestBody String cuerpo) {
        ServiceRequest sr = fhir.newJsonParser().parseResource(ServiceRequest.class, cuerpo);
        SolicitudRequest req = mapper.aSolicitud(sr);

        Set<ConstraintViolation<SolicitudRequest>> errores = validator.validate(req);
        if (!errores.isEmpty()) {
            throw new FhirInvalidoException(errores.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; ")));
        }

        SolicitudService.Recepcion r = service.recibir(req);
        URI ubicacion = URI.create("/api/solicitudes/" + r.solicitud().id());
        return r.nueva()
                ? ResponseEntity.created(ubicacion).build()
                : ResponseEntity.ok().location(ubicacion).build();
    }

    @GetMapping("/DiagnosticReport/{id}")
    public ResponseEntity<String> diagnosticReport(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/fhir+json"))
                .body(notificacion.verDiagnosticReport(id));
    }
}