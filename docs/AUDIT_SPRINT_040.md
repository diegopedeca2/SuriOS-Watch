# Auditoría Sprint 040 — PIP-SuriOS

---
document: AUDIT
project: PIP-SuriOS
sprint: 040
version: 3.5
version_code: 15
date: 2026-10-17
status: Aprobada para cierre documental
---

## Resultado

La auditoría no encuentra incidencias bloqueantes dentro del alcance del
Sprint 040. BRICKTOWN queda integrado como mapa topográfico vectorial
negro/verde, con PNOA 2023 utilizada como referencia de digitalización y sin
fotografía aérea visible en las teselas.

## Cambios revisados

- Generador reproducible en `tools/gis/build_bricktown_topographic.py`.
- Geometrías visibles documentadas en
  `tools/gis/data/bricktown_pnoa_visible.geojson`.
- MBTiles PNG de BRICKTOWN en zoom 16–19.
- `mapId = "bricktown"` conservado.
- Cobertura de 2,5 x 2,5 km: `-3.367108246144,40.419348691019,-3.337646228294,40.441862514061`.
- Atribución IGN/CNIG, captura PNOA Madrid 2023 del 28/07/2023 al 04/08/2023
  y resolución indicada de 0,25 m/píxel.
- No se modificaron los mapas de otros campos ni la lógica general de MAP.

## Integridad del mapa

| Comprobación | Resultado |
|---|---|
| Tamaño MBTiles final | 22.904.832 bytes |
| SHA-256 | `6BB6D376FE8DF7C17F310C115D90904E894E7E4195233E8B82CF5BE61A3FC748` |
| SQLite `PRAGMA integrity_check` | `ok` |
| Formato | PNG, teselas 256 x 256 |
| Teselas Z16 / Z17 / Z18 / Z19 | 36 / 132 / 506 / 1.936 |
| Muestras PNG válidas | 20/20 |

## Aplicación y datos

- `testFullDebugUnitTest` y `assembleFullDebug`: correctos.
- APK generada: `app-full-debug.apk`, código 15, versión 3.5.
- Instalación en A56 mediante actualización conservando los datos existentes.
- Se conservaron 31 archivos de operaciones locales.
- El overlay de BRICKTOWN permaneció sin alteraciones.
- Los controles `ADD RESPAWN`, `ADD RAD ZONE`, GPS y brújula siguen visibles.
- No se ejecutó `adb uninstall` ni limpieza de datos.

## Copias de recuperación

- `Previous Version/bricktown-hybrid-pnoa2023-v3.5-correction/` conserva el
  MBTiles híbrido anterior.
- `Previous Version/mobile-app-v3.5-sprint-040-final/` conserva la app v3.5
  final sin carpetas `build` ni cachés.

## Riesgos y pendientes no bloqueantes

La ortofotografía permite identificar estructuras exteriores, pero no confirma
interiores, túneles, muros ocultos ni el perímetro oficial del campo. Esas
representaciones se han excluido deliberadamente.

## Decisión

El Sprint 040 queda **CLOSED** y esta auditoría queda **APROBADA PARA CIERRE
DOCUMENTAL**.
