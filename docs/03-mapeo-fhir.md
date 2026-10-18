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