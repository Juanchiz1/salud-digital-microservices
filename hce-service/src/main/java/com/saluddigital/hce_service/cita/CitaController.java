package com.saluddigital.hce_service.cita;

import com.saluddigital.hce_service.cita.dto.CitaRequest;
import com.saluddigital.hce_service.cita.dto.CitaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CitaResponse> agendar(@Valid @RequestBody CitaRequest request) {
        CitaResponse creada = service.agendar(request);
        return ResponseEntity.created(URI.create("/api/citas/" + creada.id())).body(creada);
    }

    @GetMapping("/{id}")
    public CitaResponse obtener(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @GetMapping(params = "pacienteId")
    public List<CitaResponse> porPaciente(@RequestParam UUID pacienteId) {
        return service.listarPorPaciente(pacienteId);
    }

    @GetMapping(params = "medicoId")
    public List<CitaResponse> agenda(@RequestParam UUID medicoId) {
        return service.agendaDelMedico(medicoId);
    }

    @PatchMapping("/{id}/cancelar")
    public CitaResponse cancelar(@PathVariable UUID id) {
        return service.cancelar(id);
    }
}