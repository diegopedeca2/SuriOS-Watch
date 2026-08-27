# BROTHERHOOD_EMBLEM_ASSET_SPEC

---

document: ASSET_SPEC
asset: Brotherhood Emblem
version: 1.0
project: Ecosistema SuriOS
type: Recurso gráfico oficial
document_status: Aprobado
implementation_status: Pendiente
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-06

---

# 1. Objetivo

Este documento define la especificación oficial del emblema de la Hermandad del Acero utilizado en el ecosistema SuriOS.

Su finalidad es garantizar que todos los proyectos compartan exactamente la misma identidad visual, evitando diferencias de forma, color, proporciones o tratamiento gráfico entre aplicaciones, módulos o dispositivos.

Este documento constituye la referencia única para cualquier implementación futura del emblema.

---

# 2. Alcance

Esta especificación es aplicable a:

- SuriOS Watch.
- PIP-SuriOS.
- Pantallas de carga.
- Pantallas de inicio.
- Splash Screens.
- Widgets.
- Iconografía interna.
- Material gráfico del proyecto.
- Documentación técnica cuando proceda.

No afecta a logotipos de terceros ni a otros recursos gráficos independientes del proyecto.

---

# 3. Recurso maestro

El emblema oficial del proyecto será un único archivo maestro.

Este archivo será la fuente de todas las variantes utilizadas posteriormente.

Ninguna implementación podrá utilizar imágenes descargadas nuevamente de Internet ni versiones diferentes del emblema.

Todas las adaptaciones deberán derivarse exclusivamente del recurso maestro aprobado.

---

# 4. Características del recurso maestro

El archivo maestro deberá cumplir las siguientes características:

- Fondo completamente transparente.
- Resolución suficiente para permitir escalado sin pérdida apreciable.
- Proporciones originales conservadas.
- Sin deformaciones.
- Sin recortes.
- Sin sombras añadidas.
- Sin efectos tridimensionales adicionales.
- Sin desenfoques.
- Sin texto integrado.
- Sin marcos externos.

El emblema conservará siempre su geometría original.

---

# 5. Variantes oficiales

Se autorizan únicamente las siguientes variantes.

## 5.1 Variante principal

Uso:

Interfaces activas.

Color:

PipGreen.

Esta será la variante utilizada por defecto en la mayoría de pantallas del proyecto.

---

## 5.2 Variante tenue

Uso:

Ambient Mode.

Color:

PipGreenDim.

Su finalidad es reducir el consumo energético y mantener la coherencia visual con el resto de elementos del modo ambiente.

---

## 5.3 Variante monocromática

Uso:

Casos excepcionales donde la plataforma limite el color disponible.

Color:

Un único color sólido.

No podrán añadirse degradados ni efectos.

---

# 6. Colores

El emblema utilizará exclusivamente la paleta oficial definida por el proyecto.

Los valores exactos de cada color estarán definidos por la guía de estilo general.

Como norma:

- PipGreen.
- PipGreenDim.
- Transparente.
- Negro cuando forme parte del propio diseño de la interfaz.

No se utilizarán colores adicionales.

---

# 7. Intensidad

La intensidad visual dependerá del contexto.

Como norma general:

- Elemento protagonista:
  intensidad alta.

- Elemento decorativo:
  intensidad media.

- Fondo:
  intensidad baja.

El objetivo es evitar competir visualmente con la información funcional de la interfaz.

---

# 8. Escalado

El emblema podrá escalarse libremente siempre que:

- mantenga las proporciones originales;
- no se deforme;
- no se estire;
- no se comprima;
- no se recorte.

No existe un tamaño único obligatorio.

Cada módulo definirá el tamaño adecuado según su diseño.

---

# 9. Posicionamiento

Este documento no define posiciones concretas.

La ubicación del emblema será responsabilidad del documento de diseño correspondiente.

Por ejemplo:

- WATCHFACE_LAYOUT.
- Layout de PIP-SuriOS.
- Splash Screen.
- Pantallas específicas.

---

# 10. Orden de dibujo

Cuando el emblema actúe como elemento de fondo:

Siempre deberá dibujarse por detrás de:

- hora;
- fecha;
- batería;
- pasos;
- indicadores;
- botones;
- controles;
- texto.

Nunca podrá ocultar información funcional.

---

# 11. Transparencia

El porcentaje de opacidad dependerá del contexto.

Como norma:

El emblema deberá permanecer claramente reconocible sin dificultar la lectura de la información principal.

Cada módulo establecerá el valor exacto necesario.

---

# 12. Reglas de uso

Está permitido:

- cambiar el tamaño;
- cambiar la intensidad;
- utilizar PipGreen;
- utilizar PipGreenDim;
- utilizar transparencia;
- utilizar la versión monocromática.

No está permitido:

- modificar la geometría;
- eliminar elementos;
- añadir elementos;
- cambiar proporciones;
- aplicar rotaciones arbitrarias;
- aplicar perspectivas;
- añadir efectos gráficos;
- sustituir el recurso maestro por otro diferente.

---

# 13. Implementación

La incorporación del emblema en cada módulo se realizará mediante su correspondiente Sprint.

La presente especificación no autoriza por sí misma ninguna implementación.

Cada integración deberá quedar registrada en:

- WFPRD correspondiente.
- Sprint correspondiente.
- Historial del proyecto.

---

# 14. Dependencias documentales

Este documento se complementa con:

- PROJECT_GUIDE.
- WFPRD.
- WATCHFACE_LAYOUT.
- Sprint correspondiente.
- ACTIVE_SPRINT.
- SPRINT_HISTORY.

---

# 15. Control de versiones

| Versión | Estado | Descripción |
|----------|--------|-------------|
| 1.0 | Aprobada | Primera especificación oficial del emblema de la Hermandad del Acero como recurso gráfico único del ecosistema SuriOS. |