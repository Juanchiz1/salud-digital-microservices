export const $ = (selector, raiz = document) => raiz.querySelector(selector);

/** Crea un elemento. Los hijos de texto siempre se insertan como texto, nunca como HTML. */
export function el(tag, props = {}, ...hijos) {
  const nodo = document.createElement(tag);
  for (const [clave, valor] of Object.entries(props)) {
    if (valor == null || valor === false) continue;
    if (clave === "class") nodo.className = valor;
    else if (clave.startsWith("on")) nodo.addEventListener(clave.slice(2), valor);
    else nodo.setAttribute(clave, valor === true ? "" : valor);
  }
  for (const hijo of hijos.flat()) {
    if (hijo == null || hijo === false) continue;
    nodo.append(hijo instanceof Node ? hijo : String(hijo));
  }
  return nodo;
}

export function vaciar(nodo, ...hijos) {
  nodo.replaceChildren(...hijos.flat().filter((h) => h != null && h !== false));
}

export function mostrarMensaje(nodo, texto, tipo = "error") {
  nodo.textContent = texto;
  nodo.className = `mensaje mensaje--${tipo}`;
  nodo.hidden = !texto;
}

export function fecha(iso) {
  return new Date(iso).toLocaleString("es-CO", { dateStyle: "medium", timeStyle: "short" });
}

export function estado(valor) {
  return el("span", { class: `estado estado--${valor.toLowerCase()}` }, valor.replace("_", " ").toLowerCase());
}