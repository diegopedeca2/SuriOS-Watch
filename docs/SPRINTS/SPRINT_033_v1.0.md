# Sprint 033 — Correcciones de auditoría y reglas de distribución

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Cerrado
owner: Diego Pérez de Camino
date: 2026-09-05
predecessor: Sprint 032
---

## Objetivo

Resolver las incidencias de mantenimiento aceptadas en la auditoría anterior,
aclarar qué comportamientos son decisiones de diseño y fijar las reglas de
validación y distribución de las APK tester.

## Decisiones del propietario

- SENTRY y TRACKER tienen finalidades distintas: SENTRY es un sensor de
  proximidad para patrulla, vigilancia o protección; TRACKER busca la posición
  estadísticamente más probable sobre un mapa. La duplicidad interna no se
  considera un defecto funcional.
- Las APK de FENRIR, ALTAMIRA y CHECHU son versiones fijas. No se actualizan
  por cambios de MAIN, salvo orden expresa. Al crear una nueva versión tester,
  se eliminan los artefactos de distribución de la anterior.
- Las funciones que requieren gestos de pantalla se verifican físicamente, no
  mediante emulador ni ADB.
- La carga lenta de mapas se estudiará en una versión futura; no forma parte de
  esta corrección de reproducibilidad.

## Trabajo

- [x] Elegir estrategia híbrida para recursos: generación GIS cuando cambie el
      mapa y recursos finales versionados para compilar.
- [x] Añadir mapas e iconos tester a `distribution-assets/` y
      `distribution-res/` con Git LFS.
- [x] Hacer que Gradle use `directories` en vez de `setSrcDirs`.
- [x] Hacer que el empaquetador exija `-AllowTesterRelease` para evitar una
      actualizacion accidental de las APK tester.
- [x] Eliminar `ClickScheduler.intervalMillis` y los tests de cadencia antigua.
- [x] Registrar la regla de validación física de gestos.
- [x] Registrar la regla de APK tester congeladas y limpiar versiones antiguas
      al generar una nueva distribución.
- [ ] Revisar en una futura versión la carga de MBTiles en el dispositivo.
- [x] Cerrar el Sprint 033 tras la revisión final y autorización del propietario.

## Validación técnica inicial — 2026-09-05

- `test` y `lint` de MAIN pasan correctamente después de las correcciones.
- `assembleFullDebug` compila FENRIR, ALTAMIRA y CHECHU leyendo los recursos
  versionados, sin regenerar mapas mediante QGIS.
- El script de empaquetado pasa la comprobación de sintaxis PowerShell.
- Las APK tester no se han regenerado ni distribuido.

## Cierre documental del Sprint 033 — 2026-09-05

La auditoría final es positiva: no se detectan incidencias graves ni
bloqueantes. Las correcciones aceptadas se han aplicado y las reglas de
distribución quedan protegidas en el código y en la documentación.

La mejora del tiempo de carga de MBTiles queda trasladada como trabajo futuro;
no impide el cierre porque la reproducibilidad de los recursos ya está resuelta.
Las APK tester existentes permanecen sin cambios.

El Sprint 033 queda **CERRADO** documental y técnicamente el 2026-09-05.

## Documentos relacionados

- [ADR-004 — Recursos offline y distribuciones reproducibles](../ADR/ADR-004%20-%20Recursos%20offline%20y%20distribuciones%20reproducibles%20v1.0.md)
- [AUDIT_SPRINT_032](../AUDIT_SPRINT_032.md)
- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)
