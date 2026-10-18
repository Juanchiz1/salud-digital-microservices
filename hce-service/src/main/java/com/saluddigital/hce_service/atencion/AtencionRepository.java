package com.saluddigital.hce_service.atencion;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtencionRepository extends JpaRepository<Atencion, UUID> {

    @EntityGraph(attributePaths = {"paciente", "medico", "ordenes", "ordenes.examen"})
    Optional<Atencion> findDetalleById(UUID id);

    @EntityGraph(attributePaths = {"paciente", "medico", "ordenes", "ordenes.examen"})
    List<Atencion> findByPacienteIdOrderByFechaDesc(UUID pacienteId);
}