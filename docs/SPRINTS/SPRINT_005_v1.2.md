# Sprint 005 — Implementación del emblema oficial de la Hermandad del Acero

---

document: SPRINT
sprint: 005
version: 1.2
project: SuriOS Watch
type: Funcional
document_status: Aprobado
implementation_status: Completado
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-07

---

# 1. Control documental

Versión 1.2 del documento operativo correspondiente al Sprint 005.

Este Sprint quedó completado técnica y visualmente. Su cierre operativo queda registrado en:

- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)

Referencias normativas:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [WFPRD v1.5](../WFPRD/WFPRD_v1.5.md)
- [BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3](../ASSETS/BROTHERHOOD%20EMBLEM/BROTHERHOOD_EMBLEM_ASSET_SPEC_v1.3.md)
- [WFPRD_BROTHERHOOD_EMBLEM v1.3](../WFPRD/WFPRD_BROTHERHOOD_EMBLEM_v1.3.md)
- [WATCHFACE_LAYOUT v1.3](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.3.md)
- [ADR-001 v1.2](../ADR/ADR_001_v1.2.md)
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>)
- [ADR-003 v1.0](<../ADR/ADR-003 - Arquitectura de batería y pasos v1.0.md>)

---

# 2. Objetivo

Incorporar el emblema oficial de la Hermandad del Acero como elemento gráfico permanente de SuriOS Watch.

El objetivo del Sprint es dotar a la watchface de una identidad visual propia manteniendo íntegramente la arquitectura funcional desarrollada durante los Sprint anteriores.

No se modificará el comportamiento de la esfera ni se introducirán nuevas funciones de usuario.

---

# 3. Alcance

El Sprint comprende exclusivamente:

- incorporación del recurso gráfico oficial;
- integración del emblema oficial como elemento gráfico permanente de la watchface;
- aplicación de la intensidad PipGreen establecida para el reloj;
- conservación del fondo PipBlack;
- mantenimiento de la legibilidad de hora, fecha, batería y pasos;
- funcionamiento idéntico en modo activo y Ambient Mode cuando corresponda.

---

# 4. Fuera de alcance

Este Sprint no incluye:

- Spotify;
- Google Wallet;
- nuevas fuentes de datos;
- modificaciones del sistema de batería;
- modificaciones del sistema de pasos;
- cambios de tipografía;
- nuevas animaciones;
- nuevos permisos;
- cambios de Gradle;
- modificaciones del proyecto Android;
- cambios funcionales en PIP-SuriOS.

Spotify y Google Wallet quedan reservados para Sprint 006.

---

# 5. Requisitos funcionales

La implementación deberá respetar íntegramente:

- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3;
- WFPRD_BROTHERHOOD_EMBLEM v1.3.

En particular:

- utilización exclusiva del PNG oficial;
- ausencia de modificaciones del diseño;
- ausencia de recoloreado manual;
- ausencia de escalados no uniformes;
- conservación de la transparencia;
- utilización del color PipGreen correspondiente al reloj;
- posibilidad de reutilizar el mismo recurso gráfico en otros componentes del ecosistema mediante variaciones únicamente de escala e intensidad.

---

# 6. Restricciones técnicas

La implementación deberá:

- conservar la arquitectura declarativa WFF v1;
- mantener la compatibilidad con Wear OS;
- evitar dependencias nuevas;
- evitar recursos redundantes;
- evitar duplicados del emblema;
- mantener el rendimiento actual de la watchface.

---

# 7. Archivos previstos

La implementación deberá limitarse exclusivamente a los archivos necesarios para incorporar el recurso gráfico oficial.

Como referencia inicial, la implementación afectará al menos a:

- `watch/watchface/src/main/res/raw/watchface.xml`

y utilizará el recurso gráfico oficial definido en:

- `assets/branding/brotherhood/brotherhood_emblem_pipgreen.png`

Si durante la implementación fuese necesario incorporar el PNG oficial al árbol de recursos de Android, dicho archivo podrá añadirse siempre que no altere el alcance funcional del Sprint.

No deberán modificarse componentes funcionales ajenos al alcance del Sprint.

Durante la auditoría técnica se verificará el alcance exacto del diff.

---

# 8. Validaciones previstas

Durante este Sprint deberán verificarse:

## Gradle

- compilación individual;
- compilación incremental;
- compilación completa.

## Android Studio

- Gradle Sync correcto;
- ausencia de advertencias nuevas.

## Emulador Wear OS

- posición del emblema;
- tamaño;
- intensidad;
- legibilidad de todos los indicadores;
- ausencia de solapamientos.

## Xiaomi Watch 2

- correcta visualización;
- ausencia de pérdida de rendimiento;
- ausencia de retenciones visuales;
- funcionamiento correcto de Ambient Mode;
- transición correcta entre modos.

---

# 9. Riesgos

Los principales riesgos identificados son:

- pérdida de legibilidad por exceso de intensidad;
- tamaño inadecuado del emblema;
- interferencia visual con los indicadores existentes;
- incremento innecesario del consumo gráfico;
- diferencias de legibilidad entre el emulador Wear OS y el Xiaomi Watch 2;
- diferencias de representación entre el emulador Wear OS y el comportamiento real del Xiaomi Watch 2.

Estos riesgos deberán resolverse sin modificar la arquitectura funcional existente.

---

# 10. Criterios de aceptación

El Sprint se considerará completado cuando:

- el emblema oficial esté integrado correctamente;
- la geometría coincida con la aprobada;
- la intensidad visual sea la prevista;
- no existan regresiones respecto a Sprint 004;
- las compilaciones sean correctas;
- el emulador supere las pruebas;
- el Xiaomi Watch 2 supere la validación física;
- el rendimiento permanezca equivalente al de Sprint 004;
- la incorporación del emblema no modifique el comportamiento funcional de la watchface;
- el recurso gráfico oficial quede preparado para su reutilización futura en PIP-SuriOS sin necesidad de generar variantes adicionales.

---

# 11. Continuidad

El siguiente Sprint previsto es:

**Sprint 006 — Integración de Spotify y Google Wallet.**

Sprint 006 permanece pendiente y no autorizado. Su previsión no constituye autorización de inicio.

---

# 12. Cierre

Sprint 005 quedó completado el 2026-08-07.

La implementación final incorpora:

- el recurso oficial `brotherhood_emblem_pipgreen.png`;
- integración declarativa mediante `PartImage` en Watch Face Format v1;
- geometría final validada: `width="292"`, `height="346"`, `x="79"`, `y="62"`;
- centro efectivo `(225, 235)`;
- `alpha="64"`;
- ocultación completa en Ambient Mode mediante `Variant` con `mode="AMBIENT"`, `target="alpha"` y `value="0"`;
- orden de dibujo fondo PipBlack → emblema → información funcional.

Las compilaciones individual, incremental y conjunta mediante Gradle fueron superadas sin errores.

La validación visual fue superada en:

- Wear OS Large Round;
- Xiaomi Watch 2.

No se detectaron regresiones funcionales en hora, fecha, batería, pasos ni Ambient Mode.

Commit técnico:

`a7ba0f5d3798815ace4d5877c211c0f7b884b1f3` — **Sprint 005 - Emblema oficial**

La incidencia conocida del contador de pasos permanece abierta como incidencia independiente. Queda expresamente fuera del alcance de Sprint 005 y no bloquea su cierre.

Tras este cierre no existe ningún Sprint activo.

---

# 13. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Aprobada | Documento inicial del Sprint 005 para la implementación del emblema oficial de la Hermandad del Acero en SuriOS Watch. |
| 1.1 | Aprobada | Incorpora el repositorio de activos como referencia normativa, concreta los archivos previstos, amplía los riesgos identificados y refuerza los criterios de aceptación del Sprint. |
| 1.2 | Aprobada | Flexibiliza el alcance de los archivos previstos para permitir la incorporación del recurso gráfico oficial al proyecto Android, amplía los riesgos asociados a diferencias de representación entre plataformas y añade como criterio de aceptación la reutilización futura del activo gráfico en PIP-SuriOS. |
