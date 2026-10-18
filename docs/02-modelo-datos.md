# Modelo de datos

## hce-service

```mermaid
erDiagram
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