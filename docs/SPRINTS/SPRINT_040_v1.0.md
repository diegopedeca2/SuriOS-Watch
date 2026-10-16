# Sprint 040 — PIP-SuriOS: cartografía topográfica BRICKTOWN

---
document: SPRINT
project: PIP-SuriOS
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-10-17
predecessor: Sprint 039
mobile_app_version: 3.5
mobile_app_version_code: 15
---

## Objetivo

Corregir la cartografía de BRICKTOWN para que conserve el mapa topográfico
negro/verde de PIP-SuriOS y utilice PNOA 2023 únicamente como referencia para
digitalizar estructuras visibles.

## Alcance completado

- [x] Sustituir el fondo fotográfico por un mapa vectorial topográfico.
- [x] Mantener curvas de nivel, edificios, caminos y superficies reconocibles.
- [x] Conservar `mapId = "bricktown"` y la compatibilidad MBTiles.
- [x] Mantener PNG y zoom nativo 16–19.
- [x] Mantener cobertura de 2,5 x 2,5 km con margen alrededor del campo.
- [x] No modificar la lógica general de MAP ni los demás campos.
- [x] Mantener la atribución IGN/CNIG y la fecha de captura PNOA 2023.
- [x] Conservar una copia del mapa anterior y una copia final de la app v3.5.
- [x] Compilar, instalar y comprobar visualmente en Samsung Galaxy A56.

## Resultado

BRICKTOWN muestra fondo negro, curvas verdes, edificios con contorno verde,
caminos y superficies diferenciadas. No contiene imagen aérea visible. Las
geometrías adicionales proceden de elementos identificables en PNOA 2023 y no
representan un perímetro oficial ni zonas jugables inventadas.

## Validación y cierre

La auditoría queda registrada en [AUDIT_SPRINT_040](../AUDIT_SPRINT_040.md).
El Sprint 040 queda **CLOSED**.
