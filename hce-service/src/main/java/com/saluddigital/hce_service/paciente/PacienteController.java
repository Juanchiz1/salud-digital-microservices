package com.saluddigital.hce_service.paciente;

import com.saluddigital.hce_service.paciente.dto.PacienteRequest;
import com.saluddigital.hce_service.paciente.dto.PacienteResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> registrar(@Valid @RequestBody PacienteRequest request) {
        PacienteResponse creado = service.registrar(request);
        return ResponseEntity.created(URI.create("/api/pacientes/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public PacienteResponse obtener(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    public PacienteResponse buscarPorDocumento(@RequestParam TipoDocumento tipoDocumento,
                                               @RequestParam String numeroDocumento) {
        return service.buscarPorDocumento(tipoDocumento, numeroDocumento);
    }
}