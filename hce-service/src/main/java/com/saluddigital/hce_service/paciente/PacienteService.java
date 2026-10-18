package com.saluddigital.hce_service.paciente;

import com.saluddigital.hce_service.common.ConflictoException;
import com.saluddigital.hce_service.common.RecursoNoEncontradoException;
import com.saluddigital.hce_service.paciente.dto.PacienteRequest;
import com.saluddigital.hce_service.paciente.dto.PacienteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PacienteResponse registrar(PacienteRequest req) {
        if (repository.existsByTipoDocumentoAndNumeroDocumento(req.tipoDocumento(), req.numeroDocumento())) {
            throw new ConflictoException("Ya existe un paciente con " + req.tipoDocumento() + " " + req.numeroDocumento());
        }
        Paciente paciente = new Paciente(req.tipoDocumento(), req.numeroDocumento(), req.nombres(),
                req.apellidos(), req.fechaNacimiento(), req.sexo(), req.telefono(), req.email());
        return PacienteResponse.from(repository.save(paciente));
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscarPorId(UUID id) {
        return repository.findById(id)
                .map(PacienteResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscarPorDocumento(TipoDocumento tipo, String numero) {
        return repository.findByTipoDocumentoAndNumeroDocumento(tipo, numero)
                .map(PacienteResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
    }
}