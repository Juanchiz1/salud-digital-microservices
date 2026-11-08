import { api } from "./api.js";
import { guardarSesion, leerSesion, PAGINA_POR_ROL } from "./sesion.js";
import { $, mostrarMensaje } from "./dom.js";

const form = $("#form-login");
const error = $("#login-error");

const actual = leerSesion();
if (actual && PAGINA_POR_ROL[actual.rol] && new Date(actual.expiraEn) > new Date()) {
  location.href = PAGINA_POR_ROL[actual.rol];
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  mostrarMensaje(error, "");
  const boton = form.querySelector("button");
  boton.disabled = true;

  try {
    const r = await api("/api/auth/login", { metodo: "POST", cuerpo: Object.fromEntries(new FormData(form)) });
    const sesion = { token: r.token, expiraEn: r.expiraEn, nombre: r.nombre, rol: r.rol };
    guardarSesion(sesion);

    const yo = await api("/api/auth/yo");
    guardarSesion({ ...sesion, medicoId: yo.medicoId });

    const destino = PAGINA_POR_ROL[r.rol];
    if (!destino) {
      sessionStorage.clear();
      mostrarMensaje(error, "La vista de este rol todavía no está disponible.", "info");
      return;
    }
    location.href = destino;
  } catch (err) {
    mostrarMensaje(error, err.message);
  } finally {
    boton.disabled = false;
  }
});