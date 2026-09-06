# Sprint 034 — P.R.S., mapas, comunicaciones y cierre de mantenimiento

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Cerrado
owner: Diego Pérez de Camino
date: 2026-09-06
predecessor: Sprint 033
---

## Objetivo

Consolidar las modificaciones funcionales y visuales acumuladas desde el cierre
del Sprint 033, actualizar la firma de PIP-SuriOS a `v3.2` y dejar el proyecto
auditado y documentado.

## Trabajo cerrado

- [x] P.R.S. usa las siglas en su menú principal y mantiene `PROBE` como
      selector del papel del Watch 2: baliza remota o dispositivo BLE local.
- [x] TRACKER muestra una única área probable vigente con líneas rojas finas e
      intermitentes; las áreas anteriores no se acumulan.
- [x] El área probable usa el rumbo del A56 y la evolución del RSSI como
      evidencia experimental de izquierda/derecha para un objetivo estático.
- [x] TERRAIN y TRACKER muestran el punto azul del usuario y permiten
      recentrar el mapa al tocarlo.
- [x] Se optimiza la carga de mapas con consultas por lotes, caché limitada y
      uso temporal del nivel de teselas más cercano durante el zoom.
- [x] Se incorporan los mapas `BRICKTOWN` y `AIRSOFT TOTAL`.
- [x] Se incorpora la herramienta BINARY siguiendo la lógica de MORSE; el `1`
      usa un flash largo y el `0` dos flashes cortos claramente separados.
- [x] Se añade audio de lectura de P.R.S. y sonido de interacción de botones.
- [x] Se mejora el área táctil de `BACK` y se protege de la zona de gestos del
      sistema.
- [x] Se actualizan textos, pantallas TERMINAL, documentación y versión.

## Validación

- `test`, `lint` y ensamblado de todas las variantes y módulos: correctos.
- Pruebas instrumentadas en Samsung A56: 3/3 correctas.
- APK MAIN `v3.2`, `versionCode=12`: instalada y arrancada sin excepción fatal.
- Los mapas pasan integridad SQLite, formato PNG, niveles 16..19 y hashes
      declarados.
- Sintaxis del empaquetador PowerShell: correcta.
- `git diff --check`: correcto.

## Cierre

El Sprint 034 queda **CERRADO** documental y técnicamente el 2026-09-06.

## Documentos relacionados

- [AUDIT_SPRINT_034](../AUDIT_SPRINT_034.md)
- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)
- [PRS technical summary](../PRS_TECHNICAL_SUMMARY_CURRENT.md)
