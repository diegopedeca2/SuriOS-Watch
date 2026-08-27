# WFPRD — Brotherhood Emblem

---

document: WFPRD
module: Brotherhood Emblem
module_id: BROTHERHOOD_EMBLEM
version: 1.3
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

**BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3**

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

# 5. Principios de diseño

Toda implementación del emblema deberá respetar los siguientes principios:

- simplicidad;
- coherencia visual;
- legibilidad;
- consistencia entre plataformas;
- mínimo impacto sobre la información funcional.

Estos principios prevalecerán sobre cualquier decisión estética puntual.

---

# 6. Función dentro de la interfaz

Como norma general, el emblema actuará como elemento gráfico de fondo.

No representa información funcional.

No indica estados.

No comunica eventos.

No sustituye indicadores.

---

# 7. Prioridad visual

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

# 8. Modo activo

En modo activo el emblema podrá mostrarse cuando:

- no dificulte la lectura;
- mantenga una intensidad adecuada;
- respete la composición general.

La intensidad concreta será definida por el documento de layout correspondiente.

---

# 9. Ambient Mode

Ambient Mode deberá priorizar el ahorro energético.

Como norma general:

- el emblema podrá mostrarse únicamente si no compromete la legibilidad;
- deberá utilizar la variante PipGreenDim cuando proceda;
- podrá omitirse completamente si el diseño del Sprint así lo requiere.

La decisión concreta corresponderá al Sprint de implementación.

---

# 10. Transparencia

El emblema deberá utilizar un nivel de transparencia suficiente para:

- permanecer reconocible;
- evitar competir con la información funcional.

No se fija un porcentaje concreto.

Cada implementación determinará el valor adecuado.

---

# 11. Escalado

El tamaño dependerá del dispositivo y del contexto.

Será responsabilidad del layout correspondiente.

No existe un tamaño oficial único.

---

# 12. Posicionamiento

Este documento no fija coordenadas.

La posición será definida por:

- WATCHFACE_LAYOUT.
- Layout de PIP-SuriOS.
- Sprint correspondiente.

---

# 13. Capas

El orden de dibujo deberá cumplir siempre:

1. Fondo.
2. Emblema.
3. Información funcional.
4. Elementos interactivos.

El emblema nunca podrá ocultar información.

---

# 14. Colores

Las variantes autorizadas son exclusivamente:

- PipGreen.
- PipGreenDim.
- Monocromática.

No podrán utilizarse otros colores salvo autorización documental expresa.

---

# 15. Animaciones

Por defecto:

No existirán animaciones.

No existirán rotaciones.

No existirán efectos de brillo.

No existirán pulsaciones.

Una futura versión podrá autorizarlas expresamente.

---

# 16. Rendimiento

La presencia del emblema no deberá:

- aumentar significativamente el consumo;
- introducir animaciones permanentes;
- incrementar innecesariamente el número de renderizados;
- afectar a Ambient Mode.

---

# 17. Compatibilidad

La implementación deberá ser compatible con:

- Watch Face Format v1.
- Wear OS.
- Android.
- futuras evoluciones del proyecto.

---

# 18. Fuera del alcance

Este documento no define:

- el recurso gráfico;
- colores exactos;
- tamaños;
- coordenadas;
- porcentajes de opacidad;
- variantes gráficas.

Todos estos aspectos pertenecen a:

**BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3**

---

# 19. Dependencias documentales

Este documento depende de:

- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3.
- PROJECT_GUIDE.
- WFPRD.
- WATCHFACE_LAYOUT.
- ACTIVE_SPRINT.
- SPRINT_HISTORY.

---

# 20. Implementación

La incorporación del emblema se realizará mediante el Sprint correspondiente.

La existencia de este documento no autoriza por sí misma ninguna implementación.

Toda integración deberá quedar registrada documentalmente.

---

# 21. Estado de implementación

Estado actual de adopción del emblema dentro del ecosistema SuriOS:

| Módulo | Estado | Sprint |
|---------|---------|---------|
| SuriOS Watch | Pendiente | — |
| PIP-SuriOS | Pendiente | — |
| Splash Screen | Pendiente | — |
| Widgets | Pendiente | — |

Cada Sprint que incorpore el emblema deberá actualizar esta tabla para reflejar el estado real de implementación.

---

# 22. Estabilidad documental

Este documento constituye la referencia funcional oficial para el uso del emblema dentro del ecosistema SuriOS.

Las futuras revisiones deberán limitarse a:

- ampliar escenarios de utilización;
- incorporar nuevas plataformas compatibles;
- registrar implementaciones realizadas;
- aclarar requisitos funcionales previamente aprobados.

No deberán modificarse las reglas fundamentales aquí definidas salvo aprobación documental expresa.

---

# 23. Compatibilidad futura

Este documento está diseñado para permanecer vigente durante la evolución del ecosistema SuriOS.

Las futuras plataformas deberán adoptar estas normas de utilización del emblema salvo que exista una especificación funcional específica, aprobada documentalmente, que establezca un comportamiento diferente.

Esta compatibilidad incluye, entre otras:

- SuriOS Watch;
- PIP-SuriOS;
- futuras aplicaciones del ecosistema;
- nuevas interfaces de usuario;
- dispositivos Wear OS compatibles.

---

# 24. Control de versiones

| Versión | Estado | Descripción |
|----------|--------|-------------|
| 1.0 | Aprobada | Primera especificación funcional del uso del emblema oficial de la Hermandad del Acero dentro del ecosistema SuriOS. |
| 1.1 | Aprobada | Se incorpora el identificador del módulo (`module_id`), se referencia explícitamente `BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3` como documento normativo y se añade el estado de implementación para el seguimiento de la adopción del emblema en los distintos módulos del ecosistema. |
| 1.2 | Aprobada | Se añade el apartado de estabilidad documental y se amplía el estado de implementación con la columna **Sprint** para registrar la integración del emblema en cada módulo del ecosistema. |
| 1.3 | Aprobada | Se incorporan los principios generales de diseño y un apartado de compatibilidad futura para consolidar el documento como referencia funcional permanente del uso del emblema dentro del ecosistema SuriOS. |