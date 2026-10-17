# Alcance

SaludDigital es la propuesta de integración para una clínica privada que hoy
maneja historia clínica, laboratorio y citas en sistemas separados.

## Flujo implementado

1. El paciente agenda una cita.
2. El médico lo atiende y registra la consulta.
3. El médico ordena un examen de laboratorio.
4. Laboratorio carga el resultado en su propio sistema.
5. El resultado llega a la historia clínica y el médico lo consulta.
   Cada consulta a una historia queda registrada.

## Fuera de alcance

Farmacia y facturación aparecen en el diseño de arquitectura,
pero no se implementan. Todos los datos son ficticios.

## Servicios

| Servicio      | Puerto | Base de datos     |
|---------------|--------|-------------------|
| hce-service   | 8080   | saluddigital_hce  |
| lab-service   | 8081   | saluddigital_lab  |