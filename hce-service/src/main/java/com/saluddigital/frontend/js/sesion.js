const CLAVE = "saluddigital.sesion";

export const PAGINA_POR_ROL = {
  RECEPCION: "recepcion.html",
  MEDICO: "medico.html",
};

export function guardarSesion(datos) {
  sessionStorage.setItem(CLAVE, JSON.stringify(datos));
}

export function leerSesion() {
  try {
    return JSON.parse(sessionStorage.getItem(CLAVE));
  } catch {
    return null;
  }
}

export function cerrarSesion() {
  sessionStorage.removeItem(CLAVE);
  location.href = "index.html";
}

export function exigirRol(...roles) {
  const s = leerSesion();
  if (!s || new Date(s.expiraEn) <= new Date() || !roles.includes(s.rol)) {
    cerrarSesion();
    return null;
  }
  return s;
}