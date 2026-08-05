# SPRINT_HISTORY

---
document: SPRINT_HISTORY
project: SuriOS Ecosystem
version: 1.1
status: Activo; registro operativo canónico
owner: Diego Pérez de Camino
last_updated: 2026-08-05
previous_snapshot: OLD/SPRINT_HISTORY_v1.0.md
---

## 1. Propósito

Registro acumulativo del estado de los Sprints. La versión anterior se conserva en [SPRINT_HISTORY v1.0](OLD/SPRINT_HISTORY_v1.0.md).

Se interpreta conforme a [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md). La aprobación documental de un Sprint no equivale a activación.

## 2. Estado general

| Proyecto | Sprint activo | Último completado | Estado |
|---|---|---|---|
| SuriOS Watch | Ninguno | Sprint 001 | Esperando autorización para Sprint 002 |
| PIP-SuriOS | Ninguno | Ninguno | Sprint 002 aprobado documentalmente, no activo |

## 3. Historial y planificación

| Sprint | Proyecto | Objetivo | Estado documental | Implementación | Inicio | Fin | Documento |
|---:|---|---|---|---|---|---|---|
| 001 | SuriOS Watch | Base de la Watch Face | Aprobado | Completado | 2026-08-05 | 2026-08-05 | [SPRINT_001](SPRINT_001.md) |
| 002 | Ecosistema SuriOS | Migración a un build Gradle multiproyecto | Aprobado | Pendiente; no activo | — | — | [Sprint 002 v1.2](SPRINT_002_v1.2.md) |
| 003 | SuriOS Watch | Ambient Mode con hora y fecha | Aprobado | Pendiente; no activo | — | — | [Sprint 003 v1.1](SPRINT_003_v1.1.md) |
| 004 | SuriOS Watch | Batería y pasos | Pendiente de documento de Sprint | Pendiente | — | — | — |
| 005 | SuriOS Watch | Spotify y Google Wallet | Pendiente de documento de Sprint | Pendiente | — | — | — |
| 006 | SuriOS Watch | Optimización, regresión y cierre | Pendiente de documento de Sprint | Pendiente | — | — | — |

## 4. Roadmap oficial

1. Sprint 002 — migración a un único build Gradle multiproyecto.
2. Sprint 003 — Ambient Mode.
3. Sprint 004 — batería y pasos.
4. Sprint 005 — Spotify y Google Wallet.
5. Sprint 006 — optimización, regresión y cierre.

El roadmap procede de [ADR-001 v1.2](../ADR/ADR_001_v1.2.md) y [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md).

## 5. Cierre de Sprint 001

- Compilación correcta.
- Instalación y validación visual correctas en emulador Wear OS.
- Instalación y validación física correctas en Xiaomi Watch 2.
- Resultado aprobado por el propietario.

Commits diferenciados:

| Commit | Papel |
|---|---|
| `10160d9` | Primera implementación estable de la Watch Face. |
| `e530164` | Base funcional de Sprint 001. |
| `96d15b3` | Cierre documental y validación física. |

### Deuda temporal aceptada

`family="SYNC_TO_DEVICE"` permanece como solución temporal autorizada. La tipografía definitiva sigue pendiente en [BACKLOG v1.2](../BACKLOG/BACKLOG_v1.2.md). Esta deuda no reabre Sprint 001.

## 6. Estadísticas

### Ecosistema

- Sprints completados: 1.
- Sprints activos: 0.
- Sprints aprobados pendientes de implementación: 2.
- Sprints pendientes de documento: 3.

### SuriOS Watch

- Sprints completados: 1.
- Sprints activos: 0.
- Sprints funcionales aprobados pendientes: 1.
- Sprints funcionales pendientes de documento: 3.

## 7. Estado operativo

[ACTIVE_SPRINT](ACTIVE_SPRINT.md) confirma que no existe ningún Sprint activo. No debe iniciarse Sprint 002 sin autorización expresa.
