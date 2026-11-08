import { api } from "./api.js";
import { exigirRol } from "./sesion.js";
import { $, el, vaciar, fecha, estado, mostrarMensaje } from "./dom.js";
import { montarEncabezado } from "./layout.js";

const SEXO = { H: "Hombre", M: "Mujer", I: "Indeterminado" };
const INTERPRETACION = { N: "Normal", H: "Alto", L: "Bajo", A: "Anormal" };

const sesion = exigirRol("MEDICO");
let paciente = null;
let citaActual = null;

if (sesion) iniciar();

async function iniciar() {
  montarEncabezado(sesion);
  $("#form-buscar").addEventListener("submit", buscar);
  $("#form-atencion").addEventListener("submit", registrarAtencion);
  $("#form-emergencia").addEventListener("submit", activarEmergencia);
  $("#actualizar").addEventListener("click", () => paciente && cargarHistoria());
  await Promise.all([cargarAgenda(), cargarExamenes()]);
}

async function cargarAgenda() {
  const citas = await api(`/api/citas?medicoId=${sesion.medicoId}`);
  const cont = $("#agenda");
  if (!citas.length) return vaciar(cont, el("p", { class: "vacio" }, "No tiene citas programadas."));

  vaciar(cont, el("ul", { class: "lista" }, citas.map((c) => el("li", {},
    el("button", { class: "lista__item", type: "button", onclick: () => abrir(c.pacienteId, c) },
      el("strong", {}, c.paciente),
      el("span", {}, fecha(c.fechaHora)),
      el("span", { class: "detalle" }, c.motivo),
    ),
  ))));
}

async function cargarExamenes() {
  const examenes = await api("/api/examenes");
  vaciar($("#lista-examenes"), examenes.map((x) => el("label", { class: "check" },
    el("input", { type: "checkbox", name: "examenes", value: x.codigo }),
    `${x.nombre} (${x.codigo})`,
  )));
}

async function buscar(e) {
  e.preventDefault();
  try {
    const p = await api(`/api/pacientes?${new URLSearchParams(new FormData(e.target))}`);
    mostrarMensaje($("#buscar-msg"), "");
    await abrir(p.id, null, p);
  } catch (err) {
    mostrarMensaje($("#buscar-msg"), err.message);
  }
}

async function abrir(pacienteId, cita, datos) {
  paciente = datos || await api(`/api/pacientes/${pacienteId}`);
  citaActual = cita;

  $("#paciente-nombre").textContent = `${paciente.nombres} ${paciente.apellidos}`;
  $("#paciente-doc").textContent = `${paciente.tipoDocumento} ${paciente.numeroDocumento}`;
  $("#paciente-datos").textContent = `${edad(paciente.fechaNacimiento)} años · ${SEXO[paciente.sexo]}`;

  $("#bloque-atencion").hidden = !cita;
  if (cita) {
    $("#atencion-cita").textContent = `Cita del ${fecha(cita.fechaHora)} · ${cita.motivo}`;
    $("#form-atencion").reset();
    mostrarMensaje($("#atencion-msg"), "");
  }

  $("#resultado").hidden = true;
  $("#panel-paciente").hidden = false;
  await cargarHistoria();
}

async function cargarHistoria() {
  const cont = $("#historia");
  $("#bloque-emergencia").hidden = true;
  try {
    const historia = await api(`/api/atenciones?pacienteId=${paciente.id}`);
    if (!historia.length) return vaciar(cont, el("p", { class: "vacio" }, "Sin atenciones registradas."));
    vaciar(cont, historia.map(tarjetaAtencion));
  } catch (err) {
    if (err.status !== 403) return vaciar(cont, el("p", { class: "mensaje mensaje--error" }, err.message));
    vaciar(cont, el("p", { class: "mensaje mensaje--aviso" },
      "No tiene una relación de atención con este paciente. El intento quedó registrado en la auditoría."));
    $("#form-emergencia").reset();
    mostrarMensaje($("#emergencia-msg"), "");
    $("#bloque-emergencia").hidden = false;
  }
}

function tarjetaAtencion(a) {
  return el("article", { class: "atencion" },
    el("header", { class: "atencion__cabecera" },
      el("strong", {}, `${a.diagnosticoCodigo} · ${a.diagnosticoDescripcion}`),
      el("span", { class: "detalle" }, `${fecha(a.fecha)} · ${a.medico}`),
    ),
    el("p", {}, a.motivoConsulta),
    a.notas ? el("p", { class: "detalle" }, a.notas) : null,
    a.ordenes.length ? el("ul", { class: "ordenes" }, a.ordenes.map((o) => el("li", {},
      el("span", {}, o.examen),
      estado(o.estado),
      o.estado === "COMPLETADA"
        ? el("button", { class: "boton boton--texto", type: "button", onclick: () => verResultado(o.id) }, "Ver resultado")
        : null,
    ))) : null,
  );
}

async function verResultado(ordenId) {
  const cont = $("#resultado");
  try {
    const r = await api(`/api/ordenes/${ordenId}/resultado`);
    vaciar(cont,
      el("div", { class: "panel__cabecera" },
        el("h3", {}, r.examen),
        el("button", { class: "boton boton--texto", type: "button", onclick: () => { cont.hidden = true; } }, "Cerrar"),
      ),
      el("p", { class: "detalle" }, `Emitido el ${fecha(r.emitidoEn)}`),
      el("table", { class: "tabla" },
        el("thead", {}, el("tr", {}, ["Analito", "Valor", "Referencia", "Interpretación"].map((t) => el("th", {}, t)))),
        el("tbody", {}, r.valores.map((v) => el("tr", { class: v.interpretacion !== "N" ? "fila--alerta" : null },
          el("td", {}, v.analito),
          el("td", {}, v.unidad ? `${v.valor} ${v.unidad}` : v.valor),
          el("td", {}, v.rangoReferencia || "—"),
          el("td", {}, INTERPRETACION[v.interpretacion]),
        ))),
      ),
      r.conclusion ? el("p", {}, el("strong", {}, "Conclusión: "), r.conclusion) : null,
    );
  } catch (err) {
    vaciar(cont, el("p", { class: "mensaje mensaje--error" }, err.message));
  }
  cont.hidden = false;
  cont.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

async function registrarAtencion(e) {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    await api("/api/atenciones", {
      metodo: "POST",
      cuerpo: {
        citaId: citaActual.id,
        motivoConsulta: fd.get("motivoConsulta"),
        notas: fd.get("notas") || null,
        diagnosticoCodigo: fd.get("diagnosticoCodigo").trim().toUpperCase(),
        diagnosticoDescripcion: fd.get("diagnosticoDescripcion"),
        examenes: fd.getAll("examenes"),
      },
    });
    citaActual = null;
    $("#bloque-atencion").hidden = true;
    await Promise.all([cargarHistoria(), cargarAgenda()]);
  } catch (err) {
    mostrarMensaje($("#atencion-msg"), err.message);
  }
}

async function activarEmergencia(e) {
  e.preventDefault();
  const justificacion = new FormData(e.target).get("justificacion").trim();
  try {
    const acceso = await api("/api/accesos-emergencia", {
      metodo: "POST",
      cuerpo: { pacienteId: paciente.id, justificacion },
    });
    await cargarHistoria();
    $("#historia").prepend(el("p", { class: "mensaje mensaje--aviso" },
      `Acceso de emergencia activo hasta ${fecha(acceso.expiraEn)}. Quedará pendiente de revisión.`));
  } catch (err) {
    mostrarMensaje($("#emergencia-msg"), err.message);
  }
}

function edad(fechaNacimiento) {
  const n = new Date(fechaNacimiento + "T00:00:00");
  const hoy = new Date();
  let anos = hoy.getFullYear() - n.getFullYear();
  if (hoy < new Date(hoy.getFullYear(), n.getMonth(), n.getDate())) anos--;
  return anos;
}