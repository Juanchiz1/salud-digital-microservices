package com.saluddigital.lab_service.fhir;

import com.saluddigital.lab_service.solicitud.EstadoSolicitud;
import com.saluddigital.lab_service.solicitud.SolicitudRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class NotificacionResultadosJob {

    private final SolicitudRepository solicitudes;
    private final NotificacionResultadoService notificacion;

    public NotificacionResultadosJob(SolicitudRepository solicitudes, NotificacionResultadoService notificacion) {
        this.solicitudes = solicitudes;
        this.notificacion = notificacion;
    }

    @Scheduled(fixedDelayString = "${hce.notificacion.intervalo-ms:10000}")
    public void notificarPendientes() {
        solicitudes.findIdsPendientesDeNotificacion(
                        EstadoSolicitud.RESULTADO_LISTO, Instant.now(), PageRequest.of(0, 20))
                .forEach(notificacion::notificar);
    }
}