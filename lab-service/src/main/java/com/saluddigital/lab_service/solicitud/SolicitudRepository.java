package com.saluddigital.lab_service.solicitud;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {

    Optional<Solicitud> findByOrdenExternaId(UUID ordenExternaId);

    @EntityGraph(attributePaths = "items")
    Optional<Solicitud> findDetalleById(UUID id);

    @EntityGraph(attributePaths = "items")
    List<Solicitud> findByEstadoOrderByRecibidaEnAsc(EstadoSolicitud estado);
}