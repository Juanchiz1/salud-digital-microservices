import { cerrarSesion } from "./sesion.js";
import { $ } from "./dom.js";

export function montarEncabezado(sesion) {
  $("#usuario-nombre").textContent = sesion.nombre;
  $("#usuario-rol").textContent = sesion.rol.toLowerCase();
  $("#salir").addEventListener("click", cerrarSesion);
}