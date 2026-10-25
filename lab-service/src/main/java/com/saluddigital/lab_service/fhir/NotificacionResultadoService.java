package com.saluddigital.lab_service.fhir;

import ca.uhn.fhir.context.FhirContext;
import com.saluddigital.lab_service.common.ConflictoException;
import com.saluddigital.lab_service.common.RecursoNoEncontradoException;
import com.saluddigital.lab_service.solicitud.EstadoSolicitud;
import com.saluddigital.lab_service.solicitud.Solicitud;
import com.saluddigital.lab_service.solicitud.SolicitudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.UUID;

@Service
public class NotificacionResultadoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionResultadoService.class);
    private static final MediaType FHIR_JSON = MediaType.parseMediaType("application/fhir+json");

    private final SolicitudRepository solicitudes;
    private final DiagnosticReportMapper mapper;
    private final FhirContext fhir;
    private final RestClient hce;

    public NotificacionResultadoService(SolicitudRepository solicitudes, DiagnosticReportMapper mapper,
                                        FhirContext fhir, RestClient hce) {
        this.solicitudes = solicitudes;
        this.mapper = mapper;
        this.fhir = fhir;
        this.hce = hce;
    }

    @Transactional
    public void notificar(UUID solicitudId) {
        Solicitud s = obtener(solicitudId);
        if (s.getEstado() != EstadoSolicitud.RESULTADO_LISTO || s.getNotificadaEn() != null) {
            return;
        }
        String cuerpo = fhir.newJsonParser().encodeResourceToString(mapper.aDiagnosticReport(s));
        try {
            hce.post()
                    .uri("/fhir/DiagnosticReport")
                    .contentType(FHIR_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .toBodilessEntity();
            s.marcarNotificada();
            log.info("Resultado de la solicitud {} notificado a la HCE", solicitudId);
        } catch (RestClientException e) {
            s.registrarFalloNotificacion(e.getMessage());
            log.warn("No se pudo notificar la solicitud {} (intento {}): {}",
                    solicitudId, s.getIntentosNotificacion(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String verDiagnosticReport(UUID solicitudId) {
        Solicitud s = obtener(solicitudId);
        if (s.getEstado() != EstadoSolicitud.RESULTADO_LISTO) {
            throw new ConflictoException("La solicitud todavía no tiene resultado");
        }
        return fhir.newJsonParser().setPrettyPrint(true).encodeResourceToString(mapper.aDiagnosticReport(s));
    }

    private Solicitud obtener(UUID id) {
        return solicitudes.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));
    }
}