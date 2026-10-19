package com.saluddigital.lab_service.solicitud;

import com.saluddigital.lab_service.solicitud.dto.ResultadoRequest;
import com.saluddigital.lab_service.solicitud.dto.SolicitudRequest;
import com.saluddigital.lab_service.solicitud.dto.SolicitudResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponse> recibir(@Valid @RequestBody SolicitudRequest request) {
        SolicitudService.Recepcion r = service.recibir(request);
        if (r.nueva()) {
            return ResponseEntity.created(URI.create("/api/solicitudes/" + r.solicitud().id()))
                    .body(r.solicitud());
        }
        return ResponseEntity.ok(r.solicitud());
    }

    @GetMapping
    public List<SolicitudResponse> listar(@RequestParam(defaultValue = "RECIBIDA") EstadoSolicitud estado) {
        return service.listar(estado);
    }

    @GetMapping("/{id}")
    public SolicitudResponse obtener(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @PostMapping("/{id}/resultado")
    public SolicitudResponse registrarResultado(@PathVariable UUID id,
                                                @Valid @RequestBody ResultadoRequest request) {
        return service.registrarResultado(id, request);
    }
}