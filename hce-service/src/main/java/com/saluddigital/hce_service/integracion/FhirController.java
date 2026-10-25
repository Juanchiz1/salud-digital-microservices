package com.saluddigital.hce_service.integracion;

import ca.uhn.fhir.context.FhirContext;
import com.saluddigital.hce_service.atencion.ResultadoRecibido;
import com.saluddigital.hce_service.atencion.ResultadoService;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/fhir")
public class FhirController {

    private final EnvioOrdenService envio;
    private final ResultadoService resultados;
    private final DiagnosticReportMapper mapper;
    private final FhirContext fhir;

    public FhirController(EnvioOrdenService envio, ResultadoService resultados,
                          DiagnosticReportMapper mapper, FhirContext fhir) {
        this.envio = envio;
        this.resultados = resultados;
        this.mapper = mapper;
        this.fhir = fhir;
    }

    @GetMapping("/ServiceRequest/{id}")
    public ResponseEntity<String> serviceRequest(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/fhir+json"))
                .body(envio.verServiceRequest(id));
    }

    @PostMapping(value = "/DiagnosticReport", consumes = {"application/fhir+json", "application/json"})
    public ResponseEntity<Void> recibirResultado(@RequestBody String cuerpo) {
        DiagnosticReport dr = fhir.newJsonParser().parseResource(DiagnosticReport.class, cuerpo);
        ResultadoRecibido r = mapper.aResultado(dr);
        boolean nuevo = resultados.registrar(r);
        URI ubicacion = URI.create("/api/ordenes/" + r.ordenId() + "/resultado");
        return nuevo
                ? ResponseEntity.created(ubicacion).build()
                : ResponseEntity.ok().location(ubicacion).build();
    }
}