# SaludDigital

Propuesta de integración para una clínica privada que hoy maneja historia clínica, laboratorio y citas en sistemas separados. Son dos microservicios independientes, cada uno con su propia base de datos, que intercambian información usando recursos HL7 FHIR R4.

Todos los datos son ficticios.

## Servicios

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `hce-service` | 8080 | `saluddigital_hce` | Pacientes, médicos, citas, atenciones y órdenes de examen |
| `lab-service` | 8081 | `saluddigital_lab` | Solicitudes de laboratorio y registro de resultados |

Stack de ambos: Java 17, Spring Boot 4.1, Spring Data JPA, PostgreSQL, Flyway para las migraciones y HAPI FHIR (estructuras R4) para serializar los recursos.

## Flujo

1. El paciente agenda una cita.
2. El médico registra la atención y ordena exámenes.
3. Un job de `hce-service` envía cada orden al laboratorio como `ServiceRequest` FHIR.
4. Laboratorio registra los resultados en su propio sistema.
5. *(pendiente)* El resultado vuelve a la historia clínica como `DiagnosticReport`.

Los pasos 1 a 4 están implementados.

## Decisiones de la integración

- **Envío asíncrono.** Registrar la atención no llama al laboratorio. Si laboratorio está caído, el médico cierra la consulta igual y la orden se envía cuando vuelva.
- **La orden es su propia cola de salida.** Se guarda en la misma transacción que la atención; mientras esté en estado `CREADA`, el job la sigue intentando.
- **Reintentos con backoff exponencial**: 10 s, 20 s, 40 s, 80 s, 160 s y luego cada 5 minutos. Cada orden guarda sus intentos y el último error.
- **Idempotencia.** La entrega es "al menos una vez"; laboratorio identifica la orden por su id y no la duplica si llega dos veces.
- **Timeouts** de 3 s de conexión y 5 s de lectura, para que un laboratorio colgado no bloquee el resto de envíos.

El detalle está en [`docs/`](docs): alcance, modelo de datos, correspondencia con FHIR y diagramas de secuencia de la integración.

## Correr en local

Requisitos: JDK 17+ y PostgreSQL.

Crea las dos bases de datos:

```sql
CREATE DATABASE saluddigital_hce;
CREATE DATABASE saluddigital_lab;
```

Por defecto los servicios se conectan como `postgres`/`postgres` en `localhost:5432`. Para cambiarlo usa las variables `DB_URL`, `DB_USER` y `DB_PASSWORD`. Flyway crea las tablas al arrancar.

En dos terminales:

```bash
cd hce-service && ./mvnw spring-boot:run
```

```bash
cd lab-service && ./mvnw spring-boot:run
```

`hce-service` busca el laboratorio en `http://localhost:8081`; se puede cambiar con `LAB_URL`.

## Endpoints principales

`hce-service`:

- `/api/pacientes`, `/api/medicos`, `/api/examenes`
- `/api/citas`, con `PATCH /api/citas/{id}/cancelar`
- `POST /api/atenciones`: registra la atención y crea las órdenes de examen
- `GET /fhir/ServiceRequest/{id}`: una orden en formato FHIR

`lab-service`:

- `POST /fhir/ServiceRequest`: recibe órdenes desde la HCE
- `/api/solicitudes`, con `POST /api/solicitudes/{id}/resultado` para registrar resultados

## Autor

**Juan Diego Negrete Portillo**
Estudiante de Ingeniería de Sistemas — Universidad de Córdoba
[Portafolio](https://juanchiz1.github.io/Portafolio-Juan_Diego_Negrete/) · [LinkedIn](https://www.linkedin.com/in/juan-diego-negrete-portillo-352111195/) · [GitHub](https://github.com/Juanchiz1)
