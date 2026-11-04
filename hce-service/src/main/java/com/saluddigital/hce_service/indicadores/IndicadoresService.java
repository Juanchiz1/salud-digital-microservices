package com.saluddigital.hce_service.indicadores;

import com.saluddigital.hce_service.common.Fechas;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndicadoresService {

    @PersistenceContext
    private EntityManager em;

    private final int umbral;

    public IndicadoresService(@Value("${indicadores.umbral-minimo}") int umbral) {
        this.umbral = umbral;
    }

    @Transactional(readOnly = true)
    public IndicadoresResponse calcular() {
        long pacientes = numero("SELECT COUNT(*) FROM paciente").longValue();

        Map<String, Long> citas = conteo(
                "SELECT estado, COUNT(*) FROM cita GROUP BY estado ORDER BY estado");

        long atenciones30 = numero(
                "SELECT COUNT(*) FROM atencion WHERE fecha >= NOW() - INTERVAL '30 days'").longValue();

        Map<String, Long> porEspecialidad = conteo("""
                SELECT m.especialidad, COUNT(*)
                FROM atencion a JOIN medico m ON m.id = a.medico_id
                GROUP BY m.especialidad
                ORDER BY COUNT(*) DESC
                """);

        Map<String, Long> ordenes = conteo(
                "SELECT estado, COUNT(*) FROM orden_examen GROUP BY estado ORDER BY estado");

        Number horas = numero("""
                SELECT AVG(EXTRACT(EPOCH FROM (resultado_recibido_en - creada_en))) / 3600
                FROM orden_examen
                WHERE estado = 'COMPLETADA'
                """);

        return new IndicadoresResponse(Fechas.local(Instant.now()), pacientes, citas, atenciones30,
                porEspecialidad, ordenes,
                horas == null ? null : Math.round(horas.doubleValue() * 10) / 10.0,
                diagnosticosFrecuentes(), umbral);
    }

    @SuppressWarnings("unchecked")
    private List<IndicadoresResponse.DiagnosticoFrecuente> diagnosticosFrecuentes() {
        List<Object[]> filas = em.createNativeQuery("""
                SELECT diagnostico_codigo, MAX(diagnostico_descripcion), COUNT(*)
                FROM atencion
                WHERE fecha >= NOW() - INTERVAL '90 days'
                GROUP BY diagnostico_codigo
                ORDER BY COUNT(*) DESC, diagnostico_codigo
                """).getResultList();

        List<IndicadoresResponse.DiagnosticoFrecuente> lista = new ArrayList<>();
        long otros = 0;
        for (Object[] f : filas) {
            long casos = ((Number) f[2]).longValue();
            if (casos >= umbral) {
                lista.add(new IndicadoresResponse.DiagnosticoFrecuente((String) f[0], (String) f[1], casos));
            } else {
                otros += casos;
            }
        }
        if (otros > 0) {
            lista.add(new IndicadoresResponse.DiagnosticoFrecuente("OTROS",
                    "Diagnósticos con menos de " + umbral + " casos", otros));
        }
        return lista;
    }

    private Number numero(String sql) {
        return (Number) em.createNativeQuery(sql).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> conteo(String sql) {
        List<Object[]> filas = em.createNativeQuery(sql).getResultList();
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Object[] f : filas) {
            mapa.put((String) f[0], ((Number) f[1]).longValue());
        }
        return mapa;
    }
}