package com.saluddigital.hce_service.acceso;

import com.saluddigital.hce_service.acceso.dto.AccesoEmergenciaRequest;
import com.saluddigital.hce_service.acceso.dto.AccesoEmergenciaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accesos-emergencia")
public class AccesoEmergenciaController {

    private final AccesoEmergenciaService service;

    public AccesoEmergenciaController(AccesoEmergenciaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccesoEmergenciaResponse activar(@Valid @RequestBody AccesoEmergenciaRequest request) {
        return service.activar(request);
    }
}