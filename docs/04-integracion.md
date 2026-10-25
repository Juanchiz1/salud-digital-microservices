# Integración HCE ↔ Laboratorio

## Envío de órdenes

```mermaid
sequenceDiagram
    participant M as Médico
    participant H as hce-service
    participant J as Job de envío (HCE)
    participant L as lab-service

    M->>H: POST /api/atenciones (con exámenes)
    H-->>M: 201, órdenes en CREADA
    loop cada 10 s
        J->>H: órdenes CREADA listas para envío
        J->>L: POST /fhir/ServiceRequest
        alt laboratorio responde 2xx
            L-->>J: 201 Created (nueva) / 200 OK (ya existía)
            J->>H: orden → ENVIADA
        else error o sin respuesta
            J->>H: intentos + 1, próximo intento con backoff
        end
    end
```

## Decisiones

**Envío asíncrono.** Registrar la atención no llama al laboratorio. Si
laboratorio está caído, el médico cierra la consulta igual y la orden se
envía cuando vuelva.

**La orden es su propia cola de salida.** La orden se guarda en la misma
transacción que la atención. No existe el caso "se creó la orden pero se
perdió el envío": mientras esté en CREADA, el job la sigue intentando.

**Reintentos con backoff exponencial.** 10 s, 20 s, 40 s, 80 s, 160 s y luego
cada 5 minutos. Cada orden guarda sus intentos y el último error.

**Al menos una vez + idempotencia.** La HCE puede enviar una orden más de una
vez (por ejemplo, si se corta la conexión después de que laboratorio la
guardó). Laboratorio identifica la orden por su id y no la duplica.

**Timeouts.** Conexión 3 s, lectura 5 s. Un laboratorio colgado no bloquea
el envío de las demás órdenes indefinidamente.

**Errores.** Laboratorio responde los errores como Problem Details (RFC 9457)
y no como `OperationOutcome` de FHIR. Es una simplificación del alcance.

## Retorno de resultados

```mermaid
sequenceDiagram
    participant B as Bacteriólogo
    participant L as lab-service
    participant J as Job de notificación (Lab)
    participant H as hce-service
    participant M as Médico

    B->>L: POST /api/solicitudes/{id}/resultado
    L-->>B: 200, RESULTADO_LISTO
    loop cada 10 s
        J->>L: resultados sin notificar
        J->>H: POST /fhir/DiagnosticReport
        alt HCE responde 2xx
            H-->>J: 201 Created / 200 OK (ya estaba)
            J->>L: notificada_en = ahora
        else error o sin respuesta
            J->>L: intentos + 1, backoff
        end
    end
    M->>H: GET /api/ordenes/{id}/resultado
```

**El resultado puede llegar antes que la confirmación del envío.** Si la
respuesta de laboratorio a un ServiceRequest se perdió, la orden sigue en
CREADA en la HCE aunque laboratorio ya la procesó. La HCE acepta el
resultado igual y pasa la orden directo a COMPLETADA.

**Recepción idempotente en la HCE.** Si llega un resultado para una orden
ya COMPLETADA, responde 200 sin modificar nada.