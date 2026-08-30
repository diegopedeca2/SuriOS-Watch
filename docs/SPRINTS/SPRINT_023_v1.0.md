# Sprint 023 — Auditoría completa y reconstrucción de TERRAIN NAVY7

## Estado

- Apertura: 2026-08-31.
- Cierre: 2026-08-31.
- Estado: cerrado con deudas residuales explícitas.
- Aplicación objetivo: PIP-SuriOS full y `prsOnly` mediante el asset compartido.
- Alcance geográfico: `40.352971232717216, -3.423711863510395`.
- Documento de auditoría: [AUDIT_SPRINT_023](../AUDIT_SPRINT_023.md).

## Objetivo

Auditar el monorepo `D:\WristOS` y rehacer desde cero el mapa TERRAIN de
NAVY7 siguiendo el mismo contrato de salida que HOME: MBTiles PNG opaco,
overlay offline, zoom 16–19 y consumo mediante `MbTilesRepository`.

La fuente editable continúa siendo el proyecto QGIS externo
`C:\Users\diego\Desktop\GQUIS\MAP SuriOS.qgz`. La regeneración no reutiliza el
MBTiles anterior: renderiza las teselas desde las capas activas del proyecto.

## Implementación realizada

- Se versionó `tools/gis/build_navy7_home_style.py` como modelo estándar para
  renderizar QGIS a MBTiles.
- Se utilizaron las capas activas `highway`, `contours_2m` y `building`.
- MDT y OSM online permanecieron desactivados, por lo que la salida es offline.
- Se conservó la huella física de HOME, aproximadamente 5 km x 2,5 km, y se
  trasladó su centro al punto solicitado.
- Se creó en GQUIS el proyecto independiente
  `Navy7_HOME_STYLE.qgz`, sin reutilizar la simbología del proyecto NAVY7
  anterior.
- Se fijó la paleta final de HOME: fondo `#050805`, edificios `#606060`,
  carreteras `#2f7ebe`, curvas menores `#4cb359` e índice `#5bd66b`.
- Se actualizó `OfflineMapCatalog.NAVY7` y la prueba de catálogo.
- Se añadió una aserción instrumentada sobre los bounds publicados por MBTiles.
- HOME no se modificó; su SHA-256 permanece
  `5BA9265B51C681075BA967E6DFB51DE546D51261DE51E915938F2D7A471CFFE5`.

## Contrato de salida

| Propiedad | Valor |
|---|---|
| Asset | `app/src/main/assets/maps/navy_7_terrain.mbtiles` |
| Centro | `40.352971232717216, -3.423711863510395` |
| Bounds | `-3.453046863510,40.341471232717,-3.394376863510,40.364471232717` |
| Dimensiones | 5 km x 2,5 km, huella física estándar HOME |
| Formato | PNG RGBA opaco sobre fondo `#050805` |
| Tipo | overlay |
| Zoom | 16–19 |
| Teselas | z16: 84, z17: 276, z18: 1012, z19: 3870; total: 5242 |
| SHA-256 | `885290661BA89E3FC2A60D61C135E623751D6911E06E35597A08EB8075C4F394` |

## Criterios de aceptación

1. El catálogo centra NAVY7 exactamente en las coordenadas solicitadas.
2. El asset abre como MBTiles válido y mantiene el contrato que utiliza HOME.
3. Todas las teselas generadas son PNG 256x256, opacas y se renderizan sin
   depender de red.
4. La salida se puede repetir suministrando únicamente el centro al modelo
   estándar y usando el GeoPackage de trabajo de GQUIS.
5. Las pruebas JVM, lint y ensamblados del monorepo terminan correctamente.
6. La prueba instrumentada de MBTiles se ejecuta correctamente en el A56 y la
   aplicación 2.6 arranca con NAVY7 seleccionado y el asset materializado.

## Fuera de alcance de esta apertura

Los hallazgos de seguridad, permisos, identidad del módulo PROBE, caché y
dependencias quedan documentados como backlog priorizado en la auditoría. No se
mezclan cambios funcionales de esas áreas con la reconstrucción cartográfica.

La versión móvil se actualiza a `2.6` (`versionCode 6`) como parte del cierre.
El commit de cierre queda registrado tras la validación final, de acuerdo con
`PROJECT_GUIDE`.
