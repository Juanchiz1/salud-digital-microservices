package com.saluddigital.hce_service.indicadores;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record IndicadoresResponse(
        OffsetDateTime generadoEn,
        long pacientesRegistrados,
        Map<String, Long> citasPorEstado,
        long atencionesUltimos30Dias,
        Map<String, Long> atencionesPorEspecialidad,
        Map<String, Long> ordenesPorEstado,
        Double horasPromedioEntregaResultado,
        List<DiagnosticoFrecuente> diagnosticosFrecuentes90Dias,
        int umbralMinimoCasos
) {
    public record DiagnosticoFrecuente(String codigo, String descripcion, long casos) {
    }
}