# Seguridad y acceso a la información

## Respuesta al debate

No toda la información del paciente debe estar disponible para todos los
empleados. Cada rol accede solo a lo que necesita para su función (mínimo
privilegio), y el paciente es el titular de su historia clínica (Ley 2015 de
2020; Ley 1581 de 2012 de protección de datos personales).

## Autenticación

- Login con usuario y contraseña en `POST /api/auth/login`. La respuesta trae
  un JWT firmado con HS256, válido por 8 horas.
- Contraseñas guardadas con BCrypt.
- Ante un usuario inexistente o una contraseña errada, la respuesta es la
  misma y tarda lo mismo, para no revelar qué usuarios existen.
- El token lleva `sub` (usuario), `rol`, `nombre` y, para médicos, `medicoId`.
  Se valida firma, expiración y emisor (`saluddigital-hce`).
- La clave de firma viene de la variable de entorno `JWT_SECRET` (mínimo 256
  bits). No hay valor por defecto.

## Matriz de acceso (implementada)

| Endpoint                          | Recepción | Médico | Gerencia |
|-----------------------------------|:---------:|:------:|:--------:|
| Registrar paciente                | ✔         |        |          |
| Buscar / ver paciente (demografía)| ✔         | ✔      |          |
| Agendar / cancelar cita           | ✔         |        |          |
| Ver citas y agendas               | ✔         | ✔      |          |
| Registrar atención y órdenes      |           | ✔ (*)  |          |
| Ver historia clínica              |           | ✔      |          |
| Ver resultados de laboratorio     |           | ✔      |          |

(*) Solo el médico asignado a la cita.

Todo endpoint que no aparece en la matriz se deniega por defecto.

## Usuarios de demostración

`recepcion`, `dr.gomez`, `dra.diaz` y `gerencia`, todos con la contraseña
`SaludDigital2026`. Son públicos a propósito para que el proyecto se pueda
probar. En un sistema real no se cargan usuarios en las migraciones.

## Pendiente

- Médico tratante y auditoría de consultas (6b).
- Autenticación entre servicios para `/fhir/**`, hoy abierto (6c).