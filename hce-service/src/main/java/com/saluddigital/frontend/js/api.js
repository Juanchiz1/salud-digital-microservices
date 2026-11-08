import { API_HCE } from "./config.js";
import { leerSesion, cerrarSesion } from "./sesion.js";

export class ApiError extends Error {
  constructor(status, mensaje) {
    super(mensaje);
    this.status = status;
  }
}

const MENSAJES = {
  400: "Revise los datos del formulario.",
  403: "No tiene permiso para esta acción.",
  404: "No encontrado.",
  409: "La operación no es posible en el estado actual.",
};

export async function api(ruta, { metodo = "GET", cuerpo, base = API_HCE } = {}) {
  const sesion = leerSesion();
  const headers = {};
  if (sesion?.token) headers.Authorization = `Bearer ${sesion.token}`;
  if (cuerpo !== undefined) headers["Content-Type"] = "application/json";

  let resp;
  try {
    resp = await fetch(base + ruta, {
      method: metodo,
      headers,
      body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo),
    });
  } catch {
    throw new ApiError(0, "No se pudo conectar con el servidor.");
  }

  if (resp.status === 401 && sesion?.token) {
    cerrarSesion();
    throw new ApiError(401, "La sesión expiró.");
  }

  let datos = null;
  const texto = await resp.text();
  if (texto) {
    try { datos = JSON.parse(texto); } catch { datos = null; }
  }

  if (!resp.ok) {
    const detalle = resp.status === 400 && datos?.detail === "Invalid request content." ? null : datos?.detail;
    throw new ApiError(resp.status, detalle || MENSAJES[resp.status] || "Error inesperado.");
  }
  return datos;
}