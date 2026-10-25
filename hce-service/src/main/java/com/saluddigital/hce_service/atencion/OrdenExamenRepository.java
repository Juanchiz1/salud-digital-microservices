package com.saluddigital.hce_service.atencion;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdenExamenRepository extends JpaRepository<OrdenExamen, UUID> {

    @Query("""
            select o.id from OrdenExamen o
            where o.estado = :estado
              and (o.proximoIntento is null or o.proximoIntento <= :ahora)
            order by o.creadaEn
            """)
    List<UUID> findIdsPendientesDeEnvio(@Param("estado") EstadoOrden estado,
                                        @Param("ahora") Instant ahora,
                                        Pageable pagina);

    @EntityGraph(attributePaths = {"examen", "atencion", "atencion.paciente"})
    Optional<OrdenExamen> findParaEnvioById(UUID id);

    @EntityGraph(attributePaths = {"examen", "valores", "atencion", "atencion.paciente"})
    Optional<OrdenExamen> findResultadoById(UUID id);
}