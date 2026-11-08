import { api } from "./api.js";
import { exigirRol } from "./sesion.js";
import { $, el, vaciar, fecha, estado, mostrarMensaje } from "./dom.js";
import { montarEncabezado } from "./layout.js";

const sesion = exigirRol("RECEPCION");
let paciente = null;
let documentoBuscado = null;

if (sesion) iniciar();

async function iniciar() {
  montarEncabezado(sesion);
  $("#form-buscar").addEventListener("submit", buscar);
  $("#form-registro").addEventListener("submit", registrar);
  $("#form-cita").addEventListener("submit", agendar);

  const medicos = await api("/api/medicos");
  vaciar($("#select-medico"), medicos.map((m) => el("option", { value: m.id }, `${m.nombre} · ${m.especialidad}`)));
}

async function buscar(e) {
  e.preventDefault();
  const datos = Object.fromEntries(new FormData(e.target));
  const msg = $("#buscar-msg");
  mostrarMensaje(msg, "");
  $("#panel-registro").hidden = true;
  $("#panel-paciente").hidden = true;

  try {
    await mostrarPaciente(await api(`/api/pacientes?${new URLSearchParams(datos)}`));
  } catch (err) {
    if (err.status !== 404) return mostrarMensaje(msg, err.message);
    documentoBuscado = datos;
    $("#registro-doc").textContent = `${datos.tipoDocumento} ${datos.numeroDocumento}`;
    $("#form-registro").reset();
    mostrarMensaje($("#registro-msg"), "");
    $("#panel-registro").hidden = false;
    mostrarMensaje(msg, "No hay un paciente con ese documento. Puede registrarlo abajo.", "info");
  }
}

async function registrar(e) {
  e.preventDefault();
  const datos = { ...documentoBuscado, ...Object.fromEntries(new FormData(e.target)) };
  for (const opcional of ["telefono", "email"]) if (!datos[opcional]) delete datos[opcional];

  try {
    const creado = await api("/api/pacientes", { metodo: "POST", cuerpo: datos });
    $("#panel-registro").hidden = true;
    mostrarMensaje($("#buscar-msg"), "Paciente registrado.", "ok");
    await mostrarPaciente(creado);
  } catch (err) {
    mostrarMensaje($("#registro-msg"), err.message);
  }
}

async function mostrarPaciente(p) {
  paciente = p;
  $("#paciente-nombre").textContent = `${p.nombres} ${p.apellidos}`;
  $("#paciente-doc").textContent = `${p.tipoDocumento} ${p.numeroDocumento}`;
  mostrarMensaje($("#cita-msg"), "");
  $("#panel-paciente").hidden = false;
  await cargarCitas();
}

async function cargarCitas() {
  const citas = await api(`/api/citas?pacienteId=${paciente.id}`);
  const lista = $("#lista-citas");
  if (!citas.length) return vaciar(lista, el("p", { class: "vacio" }, "Sin citas registradas."));

  vaciar(lista, el("table", { class: "tabla" },
    el("thead", {}, el("tr", {}, ["Fecha", "Médico", "Motivo", "Estado", ""].map((t) => el("th", {}, t)))),
    el("tbody", {}, citas.map((c) => el("tr", {},
      el("td", {}, fecha(c.fechaHora)),
      el("td", {}, c.medico),
      el("td", {}, c.motivo),
      el("td", {}, estado(c.estado)),
      el("td", {}, c.estado === "PROGRAMADA"
        ? el("button", { class: "boton boton--texto", type: "button", onclick: () => cancelar(c) }, "Cancelar")
        : null),
    ))),
  ));
}

async function agendar(e) {
  e.preventDefault();
  const f = Object.fromEntries(new FormData(e.target));
  try {
    await api("/api/citas", {
      metodo: "POST",
      cuerpo: {
        pacienteId: paciente.id,
        medicoId: f.medicoId,
        fechaHora: new Date(f.fechaHora).toISOString(),
        motivo: f.motivo,
      },
    });
    e.target.reset();
    mostrarMensaje($("#cita-msg"), "Cita agendada.", "ok");
    await cargarCitas();
  } catch (err) {
    mostrarMensaje($("#cita-msg"), err.message);
  }
}

async function cancelar(cita) {
  if (!confirm(`¿Cancelar la cita del ${fecha(cita.fechaHora)}?`)) return;
  try {
    await api(`/api/citas/${cita.id}/cancelar`, { metodo: "PATCH" });
    mostrarMensaje($("#cita-msg"), "Cita cancelada.", "ok");
    await cargarCitas();
  } catch (err) {
    mostrarMensaje($("#cita-msg"), err.message);
  }
}