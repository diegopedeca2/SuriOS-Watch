# Sprint 036 — AIRSOFT TOTAL REFERENCE MAP + BETA TESTER BUILDS

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Closed
owner: Diego
date: 2026-09-24
predecessor: Sprint 035
app_version: 3.4
version_code: 14
---

## Objetivo

Mejorar AIRSOFT TOTAL como mapa de referencia para futuros trabajos de QGIS y
preparar una distribución BETA para los perfiles de tester existentes:
FENRIR, ALTAMIRA, CHECHU y JAVI.

## Alcance

- [x] Hacer el GRID negro y aumentar el grosor de sus líneas.
- [x] Mantener los identificadores de celda del GRID.
- [x] Mantener el rojo oficial para POI y añadir un halo negro de separación.
- [x] Dibujar los caminos internos en azul oficial, con más intensidad y un
      halo oscuro para que no se confundan con las curvas de nivel.
- [x] Mantener separadas las capas GRID, FIELD_BOUNDARY, POI e INTERNAL_PATHS.
- [x] Subir la firma de la aplicación a `3.4` / `versionCode=14`.
- [x] Generar desde cero el mapa `TESTING` de JAVI con centro
      `40.431175043353754, -3.638558623703386`.
- [x] Integrar el icono PIP-J de JAVI y registrar su perfil Android.
- [x] Generar y revisar las APK BETA de FENRIR, ALTAMIRA, CHECHU y JAVI.
- [x] Instalar y comprobar el arranque de la APK BETA JAVI en el A56.

## Decisiones visuales

El GRID utiliza `PipBlack` y un grosor de 3,5 px. Los caminos utilizan
`PipBlue` con un grosor de 4,5 px y un halo oscuro de 7 px. Los POI conservan
`PipRed`, pero incorporan un halo negro y un marcador mayor. Esta combinación
separa visualmente las tres capas sin inventar colores fuera de la paleta PIP.

## Distribución BETA

La distribución se genera para los perfiles ya existentes:

- FENRIR: `NAVY7` y su mapa `TESTING`.
- ALTAMIRA: `NAVY7` y su mapa `TESTING`.
- CHECHU: `NAVY7` y su mapa `TESTING`.
- JAVI: `NAVY7` y su mapa `TESTING` centrado en `40.431175043353754,
  -3.638558623703386`.

La salida se guardará en `output/SPRINT_036_BETA/`. Cada perfil tendrá su APK,
la guía de pruebas, la plantilla CSV de campo, un SHA-256 y un ZIP independiente.
La distribución BETA no modifica los paquetes ALPHA históricos.

## Fuera de alcance

- Routing, A*, Dijkstra o navegación giro a giro.
- Cambiar la geometría del perímetro o de los caminos sin una nueva revisión
  en QGIS.
- Eliminar o sobrescribir las APK históricas de Alpha.

## Estado

El código visual está implementado y ha superado la compilación, los tests, el
lint y las pruebas instrumentadas en el A56. Las cuatro APK BETA se han
generado. JAVI se ha instalado y ha arrancado correctamente en el A56.

El Sprint queda **CLOSED**. La validación física completa de los flujos MAP,
GPS, brújula y Bluetooth queda registrada como comprobación operativa posterior
y no como incidencia técnica bloqueante.
