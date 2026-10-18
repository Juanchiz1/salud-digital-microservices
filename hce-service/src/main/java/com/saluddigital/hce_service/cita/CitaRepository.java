package com.saluddigital.hce_service.cita;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CitaRepository extends JpaRepository<Cita, UUID> {

    boolean existsByMedicoIdAndFechaHoraAndEstadoNot(UUID medicoId, Instant fechaHora, EstadoCita estado);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByPacienteIdOrderByFechaHoraDesc(UUID pacienteId);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Cita> findByMedicoIdAndEstadoOrderByFechaHoraAsc(UUID medicoId, EstadoCita estado);
}