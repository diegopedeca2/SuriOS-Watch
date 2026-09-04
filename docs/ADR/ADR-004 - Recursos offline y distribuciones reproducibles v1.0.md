# ADR-004 — Recursos offline y distribuciones reproducibles

---
document: ADR
identifier: ADR-004
version: 1.0
status: Aprobado y vigente
date: 2026-09-05
owner: Diego Pérez de Camino
project: SuriOS Ecosystem / PIP-SuriOS
---

## 1. Decisión

Se adopta una estrategia híbrida para los mapas e iconos de PIP-SuriOS:

1. El proceso GIS seguirá siendo la fuente de regeneración cuando cambien las
   coordenadas, los datos o el estilo de un mapa.
2. Las salidas finales que forman parte de una distribución tester se
   conservarán versionadas en `distribution-assets/` y `distribution-res/`.
3. Los binarios grandes se gestionarán con Git LFS.
4. La compilación normal no ejecutará QGIS ni dependerá de carpetas locales de
   `build/generated`.

Esta opción es la más adecuada para la fase Alpha: un clon limpio puede
compilar las distribuciones con los recursos exactos aprobados, y la
regeneración lenta solo se realiza cuando el mapa cambia.

## 2. Estructura aplicada

```text
distribution-assets/
  common/maps/navy_7_terrain.mbtiles
  FENRIR/maps/testing_terrain.mbtiles
  ALTAMIRA/maps/testing_terrain.mbtiles
  CHECHU/maps/testing_terrain.mbtiles

distribution-res/
  FENRIR/drawable-nodpi/pip_f_icon.png
  ALTAMIRA/drawable-nodpi/pip_a_icon.png
  CHECHU/drawable-nodpi/pip_c_icon.png
```

`MAIN` conserva sus mapas en `app/src/main/assets`, mientras que cada perfil
tester combina el mapa común NAVY7 con su mapa `TESTING` específico y su icono.
El script `tools/package_alpha_testers.ps1` valida estas rutas versionadas.

## 3. Alternativas descartadas

### Regenerar siempre desde QGIS

Se descarta como flujo normal porque necesita instalaciones y datos GIS
locales, puede depender de fuentes externas y hace lenta o frágil una
compilación limpia.

### Descargar recursos desde un servidor durante la compilación

Se descarta por ahora porque añade dependencia de red y de disponibilidad de un
servidor. Puede estudiarse si el repositorio o Git LFS dejan de ser adecuados.

### Guardar copias sin control de versiones

Se descarta porque reproduce precisamente `AUD-031-01`: otro equipo no tendría
garantía de obtener los mismos mapas e iconos.

## 4. Límite de esta decisión

Versionar los MBTiles resuelve la reproducibilidad de compilación, pero no
acelera el tiempo que tarda la aplicación en abrir o dibujar un mapa en el
dispositivo. La mejora de carga queda como trabajo futuro, por ejemplo con
lectura asíncrona, caché de teselas o una preparación más eficiente del índice,
sin cambiar todavía el formato offline aprobado.
