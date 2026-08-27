# WFPRD — Brotherhood Emblem

---

document: WFPRD
module: Brotherhood Emblem
version: 1.0
project: Ecosistema SuriOS
type: Requisito funcional de interfaz
document_status: Aprobado
implementation_status: Pendiente
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-06

---

# 1. Objetivo

Este documento define el comportamiento funcional del emblema oficial de la Hermandad del Acero dentro del ecosistema SuriOS.

Su finalidad es establecer cuándo, cómo y bajo qué condiciones deberá utilizarse el emblema dentro de las diferentes interfaces del proyecto.

No define el recurso gráfico en sí mismo, sino exclusivamente su utilización funcional.

---

# 2. Alcance

Este documento es aplicable a:

- SuriOS Watch.
- PIP-SuriOS.
- Splash Screens.
- Pantallas de inicio.
- Pantallas de carga.
- Widgets.
- Interfaces futuras que adopten la identidad visual del proyecto.

---

# 3. Dependencia del Asset

Todas las implementaciones deberán utilizar exclusivamente el recurso oficial definido en:

BROTHERHOOD_EMBLEM_ASSET_SPEC

Queda prohibido utilizar versiones alternativas del emblema.

---

# 4. Objetivos funcionales

El emblema tiene una función exclusivamente visual.

Su presencia debe:

- reforzar la identidad del proyecto;
- mejorar la coherencia visual;
- servir como elemento decorativo;
- mantener la estética inspirada en Fallout.

El emblema nunca constituye un elemento interactivo.

---

# 5. Función dentro de la interfaz

Como norma general, el emblema actuará como elemento gráfico de fondo.

No representa información funcional.

No indica estados.

No comunica eventos.

No sustituye indicadores.

---

# 6. Prioridad visual

La información funcional siempre tendrá prioridad sobre el emblema.

Por tanto:

- hora;
- fecha;
- batería;
- pasos;
- Spotify;
- Wallet;
- indicadores;
- controles;
- botones;
- texto;

deberán permanecer completamente legibles.

---

# 7. Modo activo

En modo activo el emblema podrá mostrarse cuando:

- no dificulte la lectura;
- mantenga una intensidad adecuada;
- respete la composición general.

La intensidad concreta será definida por el documento de layout correspondiente.

---

# 8. Ambient Mode

Ambient Mode deberá priorizar el ahorro energético.

Como norma general:

- el emblema podrá mostrarse únicamente si no compromete la legibilidad;
- deberá utilizar la variante PipGreenDim cuando proceda;
- podrá omitirse completamente si el diseño del Sprint así lo requiere.

La decisión concreta corresponderá al Sprint de implementación.

---

# 9. Transparencia

El emblema deberá utilizar un nivel de transparencia suficiente para:

- permanecer reconocible;
- evitar competir con la información funcional.

No se fija un porcentaje concreto.

Cada implementación determinará el valor adecuado.

---

# 10. Escalado

El tamaño dependerá del dispositivo y del contexto.

Será responsabilidad del layout correspondiente.

No existe un tamaño oficial único.

---

# 11. Posicionamiento

Este documento no fija coordenadas.

La posición será definida por:

- WATCHFACE_LAYOUT.
- Layout de PIP-SuriOS.
- Sprint correspondiente.

---

# 12. Capas

El orden de dibujo deberá cumplir siempre:

1. Fondo.
2. Emblema.
3. Información funcional.
4. Elementos interactivos.

El emblema nunca podrá ocultar información.

---

# 13. Colores

Las variantes autorizadas son exclusivamente:

- PipGreen.
- PipGreenDim.
- Monocromática.

No podrán utilizarse otros colores salvo autorización documental expresa.

---

# 14. Animaciones

Por defecto:

No existirán animaciones.

No existirán rotaciones.

No existirán efectos de brillo.

No existirán pulsaciones.

Una futura versión podrá autorizarlas expresamente.

---

# 15. Rendimiento

La presencia del emblema no deberá:

- aumentar significativamente el consumo;
- introducir animaciones permanentes;
- incrementar innecesariamente el número de renderizados;
- afectar a Ambient Mode.

---

# 16. Compatibilidad

La implementación deberá ser compatible con:

- Watch Face Format v1.
- Wear OS.
- Android.
- futuras evoluciones del proyecto.

---

# 17. Fuera del alcance

Este documento no define:

- el recurso gráfico;
- colores exactos;
- tamaños;
- coordenadas;
- porcentajes de opacidad;
- variantes gráficas.

Todos estos aspectos pertenecen a:

BROTHERHOOD_EMBLEM_ASSET_SPEC

---

# 18. Dependencias documentales

Este documento depende de:

- BROTHERHOOD_EMBLEM_ASSET_SPEC.
- PROJECT_GUIDE.
- WFPRD.
- WATCHFACE_LAYOUT.
- ACTIVE_SPRINT.
- SPRINT_HISTORY.

---

# 19. Implementación

La incorporación del emblema se realizará mediante el Sprint correspondiente.

La existencia de este documento no autoriza por sí misma ninguna implementación.

Toda integración deberá quedar registrada documentalmente.

---

# 20. Control de versiones

| Versión | Estado | Descripción |
|----------|--------|-------------|
| 1.0 | Aprobada | Primera especificación funcional del uso del emblema oficial de la Hermandad del Acero dentro del ecosistema SuriOS. |