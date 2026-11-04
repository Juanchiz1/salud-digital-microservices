package com.saluddigital.hce_service.acceso;

import com.saluddigital.hce_service.acceso.dto.AccesoEmergenciaResponse;
import com.saluddigital.hce_service.acceso.dto.AuditLogResponse;
import com.saluddigital.hce_service.acceso.dto.RevisionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoria;
    private final AccesoEmergenciaService emergencias;

    public AuditoriaController(AuditoriaService auditoria, AccesoEmergenciaService emergencias) {
        this.auditoria = auditoria;
        this.emergencias = emergencias;
    }

    @GetMapping(params = "pacienteId")
    public List<AuditLogResponse> porPaciente(@RequestParam UUID pacienteId) {
        return auditoria.porPaciente(pacienteId);
    }

    @GetMapping("/emergencias")
    public List<AccesoEmergenciaResponse> emergencias(@RequestParam(defaultValue = "true") boolean pendientes) {
        return emergencias.listar(pendientes);
    }

    @PatchMapping("/emergencias/{id}/revisar")
    public AccesoEmergenciaResponse revisar(@PathVariable UUID id, @Valid @RequestBody RevisionRequest request) {
        return emergencias.revisar(id, request.observacion());
    }
}