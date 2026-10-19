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