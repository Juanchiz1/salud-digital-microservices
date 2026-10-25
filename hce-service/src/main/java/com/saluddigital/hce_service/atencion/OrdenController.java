package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.atencion.dto.ResultadoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final ResultadoService resultados;

    public OrdenController(ResultadoService resultados) {
        this.resultados = resultados;
    }

    @GetMapping("/{id}/resultado")
    public ResultadoResponse resultado(@PathVariable UUID id) {
        return resultados.consultar(id);
    }
}