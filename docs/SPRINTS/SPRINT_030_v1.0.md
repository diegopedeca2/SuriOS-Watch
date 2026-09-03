# Sprint 030 — Mapas TESTING de ALTAMIRA y CHECHU

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Cerrado
owner: Diego Pérez de Camino
date: 2026-09-03
predecessor: Sprint 029
---

## Ajuste visual de pantallas LOADING

Se ha aplicado `TerminalScreen` a todas las pantallas de carga independientes,
incluidas MAP, DATA, INVENTORY, STATUS, TOOLS, P.R.S., CURRENT GEAR y COMMS.
Las pantallas de comprobación de CivTAK y Google Maps también usan ahora la
misma estética y la distorsión animada, incluso cuando muestran estados
dinámicos como `CHECKING`, `LAUNCHING` o `NOT FOUND`.

Las cuatro APK v3.0 se han regenerado y reinstalado en el Samsung A56.

## Documentación viva y campaña P.R.S.

Las tres guías de aprendizaje se mantienen como documentos vivos y se han
revisado con el comportamiento actual de TRACKER: inicio automático al entrar,
lectura BLE continua, análisis aproximado cada 3 segundos, espera de unos
12–15 segundos para valorar una tendencia y finalización con `< BACK`.

- [Guía EXTREMADAMENTE BÁSICA](../GUIA_FUNCIONAMIENTO_EXTREMADAMENTE_BASICO.md)
- [Guía BÁSICA](../GUIA_FUNCIONAMIENTO_BASICO.md)
- [Guía MEDIA](../GUIA_FUNCIONAMIENTO_MEDIO.md)
- [Guía de usuario nativa y completa](../USER_GUIDE.md)
- [Plantilla CSV de campo P.R.S.](../PRS_FIELD_DATA_TEMPLATE.csv)
- `output/SPRINT_030_APK/PIP-SuriOS_ALPHA_TEST_GUIDE_SPRINT_030.docx`

La guía de testers incluye las pruebas empíricas P01–P05 para medir señal
estable, movimiento, orientación/obstáculos, desaparición y PROBE. La plantilla
CSV se distribuye también junto a las APK para que cada tester pueda devolver
datos comparables.

## Objetivo

Generar e integrar los mapas offline `TESTING` específicos para ALTAMIRA y
CHECHU, manteniendo el mismo contrato visual y técnico que los mapas TERRAIN
existentes.

## Alcance inicial

- Crear una fuente OSM y un GeoPackage nuevos para cada tester.
- Descargar un MDT05 nuevo para cada zona y generar `contours_2m` con intervalo
  de 2 m.
- Renderizar un MBTiles PNG independiente de 2 km × 2 km, centrado en cada
  coordenada, con zoom 16–19.
- Integrar el MBTiles de ALTAMIRA en su distribución.
- Sustituir el placeholder vacío de CHECHU por su MBTiles real.
- Actualizar el catálogo de mapas con hashes SHA-256 y bounds WGS84.
- Validar metadatos, integridad, compilación y carga de las variantes Alpha.

## Coordenadas aprobadas

| Tester | Latitud | Longitud | Mapa |
|---|---:|---:|---|
| ALTAMIRA | 40.34897942140349 | -3.818235386395919 | `TESTING` |
| CHECHU | 40.433753 | -3.625904 | `TESTING` |

## Regla de generación

Cada zona se genera desde cero. No se reutiliza, recorta ni recentra un mapa
existente de otra ubicación. Cuando hay MDT disponible se generan e incorporan
curvas `contours_2m`; no se inventan edificios, carreteras ni elevaciones.

## Estado final

Los GeoTIFF MDT05, las fuentes OSM, los GeoPackage, los proyectos QGIS y los
MBTiles se han generado en el entorno local de trabajo. ALTAMIRA y CHECHU ya
usan sus mapas `TESTING` reales en el catálogo de distribución y en sus APK.

La tarea queda completada: las APK MAIN, FENRIR, ALTAMIRA y CHECHU se han
compilado con versión 3.0, han pasado las pruebas y `lint`, y están instaladas
en el A56 (`RZGYC07H0EX`). Las APK de los testers quedan preparadas para su
distribución posterior.

## Cierre documental

El Sprint 030 se cierra el 2026-09-03 tras completar la generación e
integración de los mapas `TESTING` de ALTAMIRA y CHECHU, la actualización de la
documentación de usuario, la guía nativa de P.R.S. y la guía de pruebas Alpha.

También queda preparada la plantilla `PRS_FIELD_DATA_TEMPLATE.csv` para la
campaña empírica de P.R.S. Las tres guías de funcionamiento quedan registradas
como documentos vivos para actualizarse con cada cambio posterior del código.

No quedan tareas abiertas dentro del alcance de este Sprint. Cualquier cambio
de comportamiento, nueva calibración o nueva campaña de campo deberá abrirse
en un Sprint posterior con autorización expresa.
