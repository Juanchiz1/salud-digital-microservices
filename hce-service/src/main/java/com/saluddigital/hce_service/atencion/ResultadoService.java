package com.saluddigital.hce_service.atencion;

import com.saluddigital.hce_service.atencion.dto.ResultadoResponse;
import com.saluddigital.hce_service.common.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class ResultadoService {

    private final OrdenExamenRepository ordenes;

    public ResultadoService(OrdenExamenRepository ordenes) {
        this.ordenes = ordenes;
    }

    @Transactional
    public boolean registrar(ResultadoRecibido r) {
        OrdenExamen orden = obtener(r.ordenId());
        if (orden.getEstado() == EstadoOrden.COMPLETADA) {
            return false;
        }
        for (ResultadoRecibido.Valor v : r.valores()) {
            orden.agregarValor(v.analito(), v.valor(), v.unidad(), v.rangoReferencia(), v.interpretacion());
        }
        orden.completar(r.conclusion(), r.emitidoEn());
        return true;
    }

    @Transactional(readOnly = true)
    public ResultadoResponse consultar(UUID ordenId) {
        OrdenExamen orden = obtener(ordenId);
        if (orden.getEstado() != EstadoOrden.COMPLETADA) {
            throw new RecursoNoEncontradoException("La orden todavía no tiene resultado");
        }
        return ResultadoResponse.from(orden);
    }

    private OrdenExamen obtener(UUID id) {
        return ordenes.findResultadoById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));
    }
}