# SPRINT_HISTORY

---

document: SPRINT_HISTORY
project: SuriOS Ecosystem
version: 1.2
status: Activo; registro operativo canónico
owner: Diego Pérez de Camino
last_updated: 2026-08-06
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
| SuriOS Watch | Sprint 005 | Sprint 004 | Sprint 005 autorizado; Sprint 006 pendiente |
| PIP-SuriOS | Ninguno | Sprint 002 | Sprint 002 completado |

---

## 3. Historial y planificación

| Sprint | Proyecto | Objetivo | Estado documental | Implementación | Inicio | Fin | Documento |
|---:|---|---|---|---|---|---|---|
| 001 | SuriOS Watch | Base de la Watch Face | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [SPRINT_001](SPRINT_001.md) |
| 002 | Ecosistema SuriOS | Migración a un build Gradle multiproyecto | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [Sprint 002 v1.2](SPRINT_002_v1.2.md) |
| 003 | SuriOS Watch | Ambient Mode con hora y fecha | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [Sprint 003 v1.1](SPRINT_003_v1.1.md) |
| 004 | SuriOS Watch | Batería y pasos | Aprobado | Completado | 2026-08-06 | 2026-08-06 | [Sprint 004 v1.0](SPRINT_004_v1.0.md) |
| 005 | SuriOS Watch | Implementación del emblema oficial de la Hermandad del Acero | Aprobado | En curso | 2026-08-06 | — | [Sprint 005 v1.0](SPRINT_005_v1.0.md) |
| 006 | SuriOS Watch | Spotify y Google Wallet | Pendiente de documento de Sprint | Pendiente | — | — | — |

---

## 4. Roadmap oficial

1. Sprint 002 — migración a un único build Gradle multiproyecto.
2. Sprint 003 — Ambient Mode.
3. Sprint 004 — batería y pasos.
4. Sprint 005 — implementación del emblema oficial de la Hermandad del Acero.
5. Sprint 006 — Spotify y Google Wallet.

El roadmap procede de:

- [ADR-001 v1.2](../ADR/ADR_001_v1.2.md)
- [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md)

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

## 9. Sprint activo

Sprint 005 constituye el Sprint actualmente autorizado del proyecto.

Su objetivo es incorporar el emblema oficial de la Hermandad del Acero en SuriOS Watch utilizando exclusivamente el recurso gráfico oficial definido para el proyecto.

La implementación deberá cumplir las especificaciones establecidas en:

- [BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3](../ASSETS/BROTHERHOOD%20EMBLEM/BROTHERHOOD_EMBLEM_ASSET_SPEC_v1.3.md)
- [WFPRD_BROTHERHOOD_EMBLEM v1.3](../WFPRD/WFPRD_BROTHERHOOD_EMBLEM_v1.3.md)

Durante este Sprint deberá mantenerse íntegramente la arquitectura aprobada de la watchface.

No deberán introducirse regresiones funcionales ni visuales respecto a los Sprint anteriores.

Sprint 006 queda reservado para la futura integración de Spotify y Google Wallet.

No deberá iniciarse hasta la finalización técnica y documental de Sprint 005.

---

## 10. Estadísticas

### Ecosistema

- Sprints completados: 4.
- Sprints activos: 1.
- Sprints aprobados pendientes de implementación: 0.
- Sprints pendientes de documento: 1.

### SuriOS Watch

- Sprints completados: 4.
- Sprints activos: 1.
- Sprints funcionales aprobados pendientes: 0.
- Sprints funcionales pendientes de documento: 1.

---

## 11. Estado operativo

[ACTIVE_SPRINT](ACTIVE_SPRINT.md) constituye la referencia operativa oficial del proyecto.

Actualmente confirma que Sprint 005 es el único Sprint autorizado y en ejecución.

Su alcance se limita exclusivamente a la incorporación del emblema oficial de la Hermandad del Acero conforme a:

- [BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3](../ASSETS/BROTHERHOOD%20EMBLEM/BROTHERHOOD_EMBLEM_ASSET_SPEC_v1.3.md)
- [WFPRD_BROTHERHOOD_EMBLEM v1.3](../WFPRD/WFPRD_BROTHERHOOD_EMBLEM_v1.3.md)

Sprint 006 permanece reservado para la futura integración de Spotify y Google Wallet.

No deberá iniciarse hasta el cierre técnico y documental de Sprint 005.