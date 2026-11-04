package com.saluddigital.hce_service.acceso;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AccesoEmergenciaRepository extends JpaRepository<AccesoEmergencia, UUID> {

    boolean existsByPacienteIdAndMedicoIdAndExpiraEnAfter(UUID pacienteId, UUID medicoId, Instant ahora);

    List<AccesoEmergencia> findByRevisadoEnIsNullOrderByOtorgadoEnDesc();

    List<AccesoEmergencia> findAllByOrderByOtorgadoEnDesc();
}