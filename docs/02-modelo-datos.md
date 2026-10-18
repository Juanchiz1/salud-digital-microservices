# Modelo de datos

## hce-service

```mermaid
erDiagram
    PACIENTE ||--o{ CITA : agenda
    MEDICO ||--o{ CITA : atiende

    PACIENTE {
        uuid id PK
        varchar tipo_documento
        varchar numero_documento
        varchar nombres
        varchar apellidos
        date fecha_nacimiento
        varchar sexo
        varchar telefono
        varchar email
        timestamptz creado_en
    }
    MEDICO {
        uuid id PK
        varchar nombres
        varchar apellidos
        varchar especialidad
        varchar registro_medico
    }
    CITA {
        uuid id PK
        uuid paciente_id FK
        uuid medico_id FK
        timestamptz fecha_hora
        varchar motivo
        varchar estado
        timestamptz creado_en
    }

        ATENCION {
        uuid id PK
        uuid cita_id FK
        uuid paciente_id FK
        uuid medico_id FK
        varchar motivo_consulta
        varchar notas
        varchar diagnostico_codigo
        varchar diagnostico_descripcion
        timestamptz fecha
    }
    ORDEN_EXAMEN {
        uuid id PK
        uuid atencion_id FK
        varchar examen_codigo FK
        varchar estado
        timestamptz creada_en
    }
    EXAMEN {
        varchar codigo PK
        varchar nombre
    }

        CITA ||--o| ATENCION : genera
    PACIENTE ||--o{ ATENCION : recibe
    ATENCION ||--o{ ORDEN_EXAMEN : ordena
    EXAMEN ||--o{ ORDEN_EXAMEN : "se solicita en"
```

## Decisiones

**Registro único de paciente.** El par (tipo_documento, numero_documento) es
único. Recepción busca al paciente por documento antes de registrarlo, así que
sus datos se capturan una sola vez y el resto de módulos usa su `id`.

**Identificadores UUID.** Un id secuencial permite adivinar otras historias
cambiando el número en la URL. Con UUID eso no es posible.

**Esquema versionado con Flyway.** Hibernate solo valida (`ddl-auto: validate`).
Todo cambio al esquema entra como un script `V{n}__descripcion.sql`, de modo
que local y producción siempre tienen la misma estructura.

**Sexo con códigos H / M / I**, la misma codificación de RIPS.

**Un médico, una cita por horario.** Un índice único parcial sobre
(medico_id, fecha_hora) ignora las citas canceladas. Cancelar libera el
horario sin borrar el registro.

**Estados de la cita.** PROGRAMADA → ATENDIDA cuando el médico registra la
consulta, o PROGRAMADA → CANCELADA. Una cita atendida o cancelada no cambia más.

**Horas en UTC.** `fecha_hora` se guarda como TIMESTAMPTZ y la API la devuelve
en hora de Colombia (America/Bogota).

**Médicos de demostración.** Se cargan en la migración V2. La administración
de médicos queda fuera del alcance.

**La atención cierra la cita.** Registrar la atención, pasar la cita a
ATENDIDA y crear las órdenes ocurre en una sola transacción. Si un examen no
existe, no se guarda nada y la cita sigue PROGRAMADA.

**Codificación estándar.** Diagnósticos en CIE-10 (4 caracteres) y exámenes
en CUPS. Con códigos estándar, otro sistema entiende la información sin
traducciones manuales.

**Estados de la orden.** CREADA → ENVIADA (laboratorio la recibió) →
COMPLETADA (llegó el resultado).