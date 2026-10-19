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