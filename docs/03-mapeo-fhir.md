# Correspondencia con FHIR R4

La API interna (`/api/...`) usa DTOs propios. FHIR se usa en la frontera,
cuando la HCE intercambia información con otro sistema.

| Modelo interno | Recurso FHIR R4      | Se intercambia con          |
|----------------|----------------------|-----------------------------|
| Paciente       | Patient              | Laboratorio, RDA nacional   |
| Cita           | Appointment          | —                           |
| Atención       | Encounter            | RDA nacional                |
| Diagnóstico    | Condition            | RDA nacional                |
| OrdenExamen    | ServiceRequest       | Laboratorio                 |
| Resultado      | DiagnosticReport + Observation | Laboratorio       |

En el alcance implementado, el intercambio real es HCE ↔ Laboratorio
(ServiceRequest de ida, DiagnosticReport de vuelta).

## ServiceRequest (HCE → Laboratorio)

| Campo FHIR                     | Origen en la HCE                     |
|--------------------------------|--------------------------------------|
| `identifier` (sid/orden-hce)   | `orden_examen.id`                    |
| `status` / `intent`            | `active` / `order`                   |
| `code.coding`                  | Código CUPS y nombre del examen      |
| `subject`                      | `Patient` contenido (ver abajo)      |
| `authoredOn`                   | `orden_examen.creada_en`             |

El `Patient` viaja como recurso contenido y solo con identificación (tipo y
número de documento), nombre, sexo y fecha de nacimiento.

Sexo → `gender`: H → `male`, M → `female`, I → `other`.

Los `system` usan el dominio reservado `saluddigital.example`. En producción
se reemplazarían por los identificadores que define la guía nacional de
interoperabilidad (RDA).

## DiagnosticReport (Laboratorio → HCE)

| Campo FHIR                         | Origen en laboratorio                     |
|------------------------------------|-------------------------------------------|
| `identifier` (sid/solicitud-lab)   | `solicitud.id`                            |
| `basedOn.identifier` (orden-hce)   | `solicitud.orden_externa_id`              |
| `status`                           | `final`                                   |
| `code.coding`                      | Código CUPS del examen                    |
| `issued`                           | `solicitud.resultado_en`                  |
| `conclusion`                       | `solicitud.conclusion`                    |
| `result`                           | Un `Observation` contenido por analito    |

Cada `Observation` lleva el analito en `code.text`, el valor como `Quantity`
(si es numérico) o `string`, el rango en `referenceRange.text` y la
interpretación con el sistema `v3-ObservationInterpretation`.

El reporte no incluye `subject`: la HCE identifica al paciente por la orden.

Pendiente fuera de alcance: codificar los analitos con LOINC en lugar de texto.