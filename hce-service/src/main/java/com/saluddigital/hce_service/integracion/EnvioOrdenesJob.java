package com.saluddigital.hce_service.integracion;

import com.saluddigital.hce_service.atencion.EstadoOrden;
import com.saluddigital.hce_service.atencion.OrdenExamenRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class EnvioOrdenesJob {

    private final OrdenExamenRepository ordenes;
    private final EnvioOrdenService envio;

    public EnvioOrdenesJob(OrdenExamenRepository ordenes, EnvioOrdenService envio) {
        this.ordenes = ordenes;
        this.envio = envio;
    }

    @Scheduled(fixedDelayString = "${laboratorio.envio.intervalo-ms:10000}")
    public void enviarPendientes() {
        List<UUID> pendientes = ordenes.findIdsPendientesDeEnvio(
                EstadoOrden.CREADA, Instant.now(), PageRequest.of(0, 20));
        pendientes.forEach(envio::enviar);
    }
}