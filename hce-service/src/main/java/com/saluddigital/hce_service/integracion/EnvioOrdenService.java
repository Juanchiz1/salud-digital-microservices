package com.saluddigital.hce_service.integracion;

import ca.uhn.fhir.context.FhirContext;
import com.saluddigital.hce_service.acceso.AccesoClinicoService;
import com.saluddigital.hce_service.acceso.RecursoAuditado;
import com.saluddigital.hce_service.atencion.EstadoOrden;
import com.saluddigital.hce_service.atencion.OrdenExamen;
import com.saluddigital.hce_service.atencion.OrdenExamenRepository;
import com.saluddigital.hce_service.common.RecursoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.UUID;

@Service
public class EnvioOrdenService {

    private static final Logger log = LoggerFactory.getLogger(EnvioOrdenService.class);
    private static final MediaType FHIR_JSON = MediaType.parseMediaType("application/fhir+json");

    private final OrdenExamenRepository ordenes;
    private final ServiceRequestMapper mapper;
    private final FhirContext fhir;
    private final RestClient laboratorio;
    private final AccesoClinicoService acceso;

    public EnvioOrdenService(OrdenExamenRepository ordenes, ServiceRequestMapper mapper, FhirContext fhir,
                             RestClient laboratorio, AccesoClinicoService acceso) {
        this.ordenes = ordenes;
        this.mapper = mapper;
        this.fhir = fhir;
        this.laboratorio = laboratorio;
        this.acceso = acceso;
    }

    @Transactional
    public void enviar(UUID ordenId) {
        OrdenExamen orden = obtener(ordenId);
        if (orden.getEstado() != EstadoOrden.CREADA) {
            return;
        }
        String cuerpo = fhir.newJsonParser().encodeResourceToString(mapper.aServiceRequest(orden));
        try {
            laboratorio.post()
                    .uri("/fhir/ServiceRequest")
                    .contentType(FHIR_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .toBodilessEntity();
            orden.marcarEnviada();
            log.info("Orden {} enviada a laboratorio", ordenId);
        } catch (RestClientException e) {
            orden.registrarFallo(e.getMessage());
            log.warn("No se pudo enviar la orden {} (intento {}): {}",
                    ordenId, orden.getIntentosEnvio(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String verServiceRequest(UUID ordenId) {
        OrdenExamen orden = obtener(ordenId);
        acceso.verificar(orden.getAtencion().getPaciente().getId(), RecursoAuditado.ATENCION,
                orden.getAtencion().getId());
        return fhir.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(mapper.aServiceRequest(orden));
    }

    private OrdenExamen obtener(UUID ordenId) {
        return ordenes.findParaEnvioById(ordenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));
    }
}