package com.saluddigital.lab_service.solicitud;

import com.saluddigital.lab_service.common.RecursoNoEncontradoException;
import com.saluddigital.lab_service.solicitud.dto.ResultadoItemRequest;
import com.saluddigital.lab_service.solicitud.dto.ResultadoRequest;
import com.saluddigital.lab_service.solicitud.dto.SolicitudRequest;
import com.saluddigital.lab_service.solicitud.dto.SolicitudResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class SolicitudService {

    public record Recepcion(SolicitudResponse solicitud, boolean nueva) {
    }

    private final SolicitudRepository repository;

    public SolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Recepcion recibir(SolicitudRequest req) {
        return repository.findByOrdenExternaId(req.ordenExternaId())
                .map(existente -> new Recepcion(SolicitudResponse.from(existente), false))
                .orElseGet(() -> {
                    Solicitud nueva = new Solicitud(req.ordenExternaId(), req.tipoDocumento(),
                            req.numeroDocumento(), req.pacienteNombre().trim(), req.sexo(),
                            req.fechaNacimiento(), req.examenCodigo(), req.examenNombre());
                    return new Recepcion(SolicitudResponse.from(repository.save(nueva)), true);
                });
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listar(EstadoSolicitud estado) {
        return repository.findByEstadoOrderByRecibidaEnAsc(estado).stream()
                .map(SolicitudResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SolicitudResponse buscarPorId(UUID id) {
        return SolicitudResponse.from(obtener(id));
    }

    @Transactional
    public SolicitudResponse registrarResultado(UUID id, ResultadoRequest req) {
        Solicitud solicitud = obtener(id);
        for (ResultadoItemRequest item : req.items()) {
            solicitud.agregarItem(item.analito().trim(), item.valor().trim(), item.unidad(),
                    item.rangoReferencia(), item.interpretacion());
        }
        solicitud.registrarResultado(req.conclusion());
        return SolicitudResponse.from(solicitud);
    }

    private Solicitud obtener(UUID id) {
        return repository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));
    }
}