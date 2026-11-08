# Frontend

HTML, CSS y JavaScript sin frameworks, servido como archivos estáticos.

## Vistas

| Página           | Rol        | Funciones                                                  |
|------------------|------------|------------------------------------------------------------|
| `index.html`     | todos      | Login                                                      |
| `recepcion.html` | Recepción  | Buscar o registrar paciente, agendar y cancelar citas      |
| `medico.html`    | Médico     | Agenda, historia clínica, registrar atención, resultados, acceso de emergencia |

## Decisiones

**Sin `innerHTML` con datos.** Todo el contenido se crea con `createElement`
y `textContent` (función `el` en `js/dom.js`). Un nombre o una nota con HTML
se muestra como texto y no se ejecuta, lo que evita XSS sobre datos clínicos.

**Token en `sessionStorage`.** Se borra al cerrar la pestaña. Limitación: un
XSS podría leerlo; por eso la regla anterior es obligatoria. En producción
se preferiría una cookie `httpOnly` con `SameSite=Strict`.

**El backend decide.** El frontend solo muestra las opciones de cada rol;
los permisos los valida la API. Un 403 se muestra tal cual, junto con la
opción de acceso de emergencia.

**CORS restringido.** La API acepta solo los orígenes configurados en
`CORS_ORIGENES` y solo en `/api/**`. `/fhir/**` no tiene CORS porque solo
lo llaman los servicios.