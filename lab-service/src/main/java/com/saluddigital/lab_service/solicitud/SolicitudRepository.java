package com.saluddigital.lab_service.solicitud;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {

    Optional<Solicitud> findByOrdenExternaId(UUID ordenExternaId);

    @EntityGraph(attributePaths = "items")
    Optional<Solicitud> findDetalleById(UUID id);

    @EntityGraph(attributePaths = "items")
    List<Solicitud> findByEstadoOrderByRecibidaEnAsc(EstadoSolicitud estado);
        @Query("""
            select s.id from Solicitud s
            where s.estado = :estado
              and s.notificadaEn is null
              and (s.proximoIntento is null or s.proximoIntento <= :ahora)
            order by s.resultadoEn
            """)
    List<UUID> findIdsPendientesDeNotificacion(@Param("estado") EstadoSolicitud estado,
                                               @Param("ahora") Instant ahora,
                                               Pageable pagina);
}