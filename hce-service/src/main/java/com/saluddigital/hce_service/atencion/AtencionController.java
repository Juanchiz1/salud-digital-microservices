package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.atencion.dto.AtencionRequest;
import com.saluddigital.hce_service.atencion.dto.AtencionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final AtencionService service;

    public AtencionController(AtencionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AtencionResponse> registrar(@Valid @RequestBody AtencionRequest request) {
        AtencionResponse creada = service.registrar(request);
        return ResponseEntity.created(URI.create("/api/atenciones/" + creada.id())).body(creada);
    }

    @GetMapping("/{id}")
    public AtencionResponse obtener(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @GetMapping(params = "pacienteId")
    public List<AtencionResponse> historia(@RequestParam UUID pacienteId) {
        return service.historiaDelPaciente(pacienteId);
    }
}