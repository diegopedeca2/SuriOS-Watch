# SPRINT_HISTORY

---

document: SPRINT_HISTORY
project: SuriOS Ecosystem
version: 1.3
status: Activo; registro operativo canónico
owner: Diego Pérez de Camino
last_updated: 2026-08-08
previous_snapshot: OLD/SPRINT_HISTORY_v1.0.md

---

## 1. Propósito

Registro acumulativo del estado de los Sprints del ecosistema SuriOS.

La versión anterior se conserva en [SPRINT_HISTORY v1.0](OLD/SPRINT_HISTORY_v1.0.md).

Este documento se interpreta conjuntamente con:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)

La aprobación documental de un Sprint no implica su activación. El único Sprint operativo es el indicado en ACTIVE_SPRINT.

---

## 2. Estado general

| Proyecto | Sprint activo | Último completado | Estado |
|---|---|---|---|
| SuriOS Watch | Ninguno | Sprint 006 | Sprint 006 completado; sin Sprint activo |
| PIP-SuriOS | Ninguno | Sprint 002 | Sprint 002 completado |

---

## 3. Historial y planificación

| Sprint | Proyecto | Objetivo | Estado documental | Implementación | Inicio | Fin | Documento |
|---:|---|---|---|---|---|---|---|
| 001 | SuriOS Watch | Base de la Watch Face | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [SPRINT_001](OLD/SPRINT_001.md) |
| 002 | Ecosistema SuriOS | Migración a un build Gradle multiproyecto | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [Sprint 002 v1.2](OLD/SPRINT_002_v1.2.md) |
| 003 | SuriOS Watch | Ambient Mode con hora y fecha | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [Sprint 003 v1.1](OLD/SPRINT_003_v1.1.md) |
| 004 | SuriOS Watch | Batería y pasos | Aprobado | Completado | 2026-08-06 | 2026-08-06 | [Sprint 004 v1.0](OLD/SPRINT_004_v1.0.md) |
| 005 | SuriOS Watch | Implementación del emblema oficial de la Hermandad del Acero | Aprobado | Completado | 2026-08-06 | 2026-08-07 | [Sprint 005 v1.2](SPRINT_005_v1.2.md) |
| 006 | SuriOS Watch | Accesos directos CAPS, STATUS y RADIO | Aprobado | Completado | 2026-08-08 | 2026-08-08 | [Sprint 006 v1.0](SPRINT_006_v1.0.md) |

---

## 4. Roadmap oficial

1. Sprint 002 — migración a un único build Gradle multiproyecto.
2. Sprint 003 — Ambient Mode.
3. Sprint 004 — batería y pasos.
4. Sprint 005 — implementación del emblema oficial de la Hermandad del Acero.
5. Sprint 006 — accesos directos CAPS, STATUS y RADIO para Google Wallet, Estadísticas/Salud y Spotify.

El roadmap procede de:

- [ADR-001 v1.2](../ADR/ADR_001_v1.2.md)
- [WFPRD v1.5](../WFPRD/WFPRD_v1.5.md)

---

## 5. Cierre de Sprint 001

- Compilación correcta.
- Instalación y validación visual correctas en el emulador Wear OS.
- Instalación y validación física correctas en Xiaomi Watch 2.
- Resultado aprobado por el propietario.

### Commits

| Commit | Papel |
|---|---|
| `10160d9` | Primera implementación estable de la Watch Face. |
| `e530164` | Base funcional de Sprint 001. |
| `96d15b3` | Cierre documental y validación física. |

### Deuda temporal aceptada

`family="SYNC_TO_DEVICE"` permanece como solución temporal autorizada.

La tipografía definitiva continúa pendiente en [BACKLOG v1.2](../BACKLOG/BACKLOG_v1.2.md).

Esta deuda técnica no reabre Sprint 001.

---

## 6. Cierre de Sprint 002

- Build Gradle multiproyecto único y wrapper raíz único operativos.
- Módulos `:app` y `:watchface` integrados desde la raíz.
- Validaciones superadas mediante Gradle CLI, Android Studio, emuladores y Xiaomi Watch 2.
- `app/**` y `watch/watchface/**` permanecieron sin modificaciones funcionales.
- No se detectaron regresiones funcionales ni visuales.

### Commit técnico

| Commit | Papel |
|---|---|
| `fe59cfb54895ba2eec52d5d27255dfe721f96a37` | Migración a un único build Gradle multiproyecto. |

### Observación no bloqueante

Durante la validación física se observó una ralentización temporal asociada a la depuración ADB.

El comportamiento desapareció al desactivar la depuración y no afecta al funcionamiento normal del proyecto.

---

## 7. Cierre de Sprint 003

- Ambient Mode implementado mediante `Variant` en WFF v1.
- Fondo PipBlack.
- Hora PipGreen.
- Fecha PipGreenDim.
- Conservación íntegra del modo activo.
- Validaciones superadas mediante Gradle CLI, Android Studio, Wear OS Large Round y Xiaomi Watch 2.
- Entrada, salida y actualización del estado ambiente correctas.
- Sin regresiones funcionales ni visuales.
- Único archivo funcional modificado:

`watch/watchface/src/main/res/raw/watchface.xml`

- No se introdujeron código Kotlin, Java ni servicios.

### Commit técnico

| Commit | Papel |
|---|---|
| `bc35866153d43442dc896a4bd9370e0f5cf29f4c` | Implementación de Ambient Mode con hora y fecha. |

---

## 8. Cierre de Sprint 004

- Indicadores de batería y pasos implementados íntegramente mediante Watch Face Format v1.
- Indicador horizontal de batería con diez niveles.
- Porcentaje entero de batería.
- Estado `RECHARGING`, que sustituye icono y porcentaje durante la carga.
- Indicador de pasos con cuatro rangos:
  - `0` → `--`
  - `1–999`
  - `1 000–999 999`
  - `999 999+`
- Integración completa en Ambient Mode.
- El icono de batería permanece ausente en Ambient Mode.
- Validaciones superadas mediante:
  - Gradle CLI.
  - Android Studio.
  - Wear OS Large Round.
  - Xiaomi Watch 2.
- Sin regresiones funcionales ni visuales.
- Único archivo funcional modificado:

`watch/watchface/src/main/res/raw/watchface.xml`

### Commit técnico

| Commit | Papel |
|---|---|
| `0e1a73f339889d874ed27089832b6b5d8fc64e0a` | Implementación de los indicadores de batería y pasos. |

### Observación no bloqueante

Durante la carga, Wear OS puede sustituir temporalmente la watchface por su propia pantalla de carga al entrar en Ambient Mode.

Este comportamiento se reprodujo tanto en el emulador Wear OS Large Round como en el Xiaomi Watch 2.

No constituye un fallo de SuriOS Watch ni invalida la implementación declarativa del Sprint.

---

## 9. Cierre de Sprint 005

Sprint 005 quedó completado el 2026-08-07.

La implementación incorporó el emblema oficial de la Hermandad del Acero mediante:

- el recurso oficial `brotherhood_emblem_pipgreen.png`;
- `PartImage` en Watch Face Format v1;
- geometría final `width="292"`, `height="346"`, `x="79"`, `y="62"`;
- centro efectivo `(225, 235)`;
- `alpha="64"`;
- ocultación completa en Ambient Mode mediante `Variant`;
- orden de dibujo fondo PipBlack → emblema → información funcional.

Las compilaciones Gradle individual, incremental y conjunta fueron superadas sin errores.

La validación visual fue superada en Wear OS Large Round y Xiaomi Watch 2, sin regresiones funcionales.

### Commit técnico

| Commit | Papel |
|---|---|
| `a7ba0f5d3798815ace4d5877c211c0f7b884b1f3` | Implementación del emblema oficial de la Hermandad del Acero. |

### Incidencia independiente del contador de pasos

La incidencia quedó resuelta y cerrada el 2026-08-07, después del cierre de Sprint 005 y sin modificar su alcance histórico.

La investigación confirmó que Xiaomi Health Services, `StepComplicationService` y `[STEP_COUNT]` entregaban correctamente el valor real. El fallo estaba en la lógica declarativa de presentación mediante condiciones solapadas, que podía conservar un valor renderizado anterior.

La corrección sustituyó los bloques condicionales de activo y ambiente por un único `PartText` dinámico basado directamente en `[STEP_COUNT]`. Se validó físicamente en Xiaomi Watch 2 el valor inicial, dos actualizaciones consecutivas sin reiniciar la esfera, Ambient Mode y la transición activo ↔ ambiente, sin duplicaciones, superposiciones ni regresiones funcionales.

| Commit | Papel |
|---|---|
| `1b8218df318a56bc17822b560f3c4dd4d0f6f603` | Corrección de la actualización del contador de pasos. |

### Mejora estética posterior: firma inferior Suri WatchOS v1.5

La mejora estética quedó completada y validada el 2026-08-07, fuera de Sprint 006 y sin modificar el estado cerrado de Sprint 005.

- Se eliminó el texto `BROTHERHOOD OF STEEL`.
- Se sustituyó `v1.0` por `v1.5`.
- El bloque inferior formado por `Suri WatchOS` y `v1.5` se reposicionó hacia el borde inferior visible de la esfera.
- La validación visual fue superada en Wear OS Large Round y Xiaomi Watch 2.

| Commit | Papel |
|---|---|
| `807e8e122c3eab93a8d783380068c391615d61b2` | Actualización estética de la firma inferior Suri WatchOS v1.5. |

### Referencias visuales CAPS, STATUS y RADIO

La referencia estética de los accesos visuales quedó completada y validada el 2026-08-08, sin iniciar Sprint 006 ni incorporar interacción funcional.

- Se incorporaron las referencias visuales `CAPS`, `STATUS` y `RADIO`.
- Las tres etiquetas quedaron alineadas horizontalmente sobre una misma coordenada Y.
- La validación visual fue superada en el emulador Wear OS Large Round y en Xiaomi Watch 2.

| Commit | Papel |
|---|---|
| `566aa60b395423d502e6fdf99a243bfa4b5a5d14` | Alineación horizontal de las referencias visuales CAPS, STATUS y RADIO. |

Esta mejora estética precedió al inicio funcional de Sprint 006. El cierre posterior queda registrado en la sección siguiente.

---

## 10. Cierre de Sprint 006

Sprint 006 quedó completado el 2026-08-08.

Las referencias visuales existentes se convirtieron en accesos directos declarativos mediante `Launch` en Watch Face Format v1:

- `CAPS` abre Google Wallet.
- `STATUS` abre Estadísticas/Salud del Xiaomi Watch 2.
- `RADIO` abre Spotify.

No se alteraron posiciones, tamaños, fuentes, colores, alineación ni ningún otro elemento visual o funcional de la esfera.

Se superaron XML bien formado, `git diff --check`, validación oficial WFF v1, compilación individual, compilación incremental y compilación conjunta. La APK se instaló en Wear OS Large Round y Xiaomi Watch 2.

La validación funcional definitiva fue realizada manualmente en Xiaomi Watch 2 y confirmó el funcionamiento correcto de los tres accesos. La solicitud de PIN o desbloqueo de Google Wallet se considera comportamiento de seguridad esperado.

### Commit técnico

| Commit | Papel |
|---|---|
| `b1a4d1605cac01e86380fb6294b0cfc995ed4de0` | Accesos directos CAPS, STATUS y RADIO. |

Documento de cierre: [Sprint 006 v1.0](SPRINT_006_v1.0.md).

No existe ningún Sprint activo. Sprint 007 y la versión 2.0 no se han iniciado. El proyecto queda preparado para una futura ronda final de pequeños ajustes estéticos orientados a Suri WatchOS v1.9.

---

## 11. Estadísticas

### Ecosistema

- Sprints registrados: 6.
- Sprints completados: 6.
- Sprints activos: 0.
- Sprints aprobados pendientes de implementación: 0.
- Sprints pendientes de documento: 0.

### SuriOS Watch

- Sprints registrados: 6.
- Sprints completados: 6.
- Sprints activos: 0.
- Sprints funcionales aprobados pendientes: 0.
- Sprints funcionales pendientes de documento: 0.
---

## 12. Estado operativo

[ACTIVE_SPRINT](ACTIVE_SPRINT.md) constituye la referencia operativa oficial del proyecto.

Actualmente confirma que Sprint 006 está completado y que no existe ningún Sprint activo.

Sprint 007 no se ha iniciado. La versión 2.0 permanece reservada para una futura fase de evolución funcional y refinamiento.

El proyecto queda preparado para una futura ronda final de pequeños ajustes estéticos orientados a declarar oficialmente Suri WatchOS v1.9.
