# Sprint 029 — Alpha testers y personalización del equipamiento

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Cerrado; pendiente no crítico: coordenadas de CHECHU
owner: Diego Pérez de Camino
date: 2026-09-02
predecessor: Sprint 028
---

## Objetivo

Preparar la primera distribución de PIP-SuriOS v2.9 para tres Alpha Testers y
mejorar la personalización del equipamiento mediante formularios de texto
libre y listas editables para el resto del equipo.

## Alcance

### 1. Alpha Testers

Incorporar en `INFORMATION > TESTERS` un único párrafo de agradecimiento a
FENRIR, CHECHU y ALTAMIRA por estar pendientes del proyecto y ser las primeras
personas en probar la aplicación y aportar feedback.

### 2. PRIMARY y SECONDARY WEAPON

- Sustituir las listas fijas de réplicas por cuadros de texto rellenables.
- PRIMARY permite escribir manualmente el ROLE y el MODEL; `APPLY` añade la
  combinación a su lista de opciones.
- SECONDARY permite escribir manualmente el TYPE y el MODEL; `APPLY` añade la
  combinación a su lista de opciones.
- Mantener la información introducida para STATUS, CURRENT GEAR y registros de
  DATA.

### 3. SET-UP > DATA

- Añadir el submenú vertical `WEAPON REPLICAS`.
- Mostrar todas las opciones guardadas de PRIMARY WEAPON y SECONDARY WEAPON.
- Permitir editar o borrar cada opción de las dos listas.
- Mantener identificada la opción activa y dejarla como `NOT SET` si se borra.

### 4. Listas personalizables y HEADGEAR

- Hacer editables y borrables las listas de `ACCESORIES`, `FRONT PANEL` y
  `UNIFORM` desde `SET-UP > DATA > CUSTOM LISTS`.
- Añadir en `SET-UP > ACCESORIES` un cuadro de texto y el botón `APPLY` para
  guardar nuevos accesorios y vaciar el campo.
- Retirar el bloque de creación de accesorios de `SET-UP > INPUT`.
- Configurar `HEADGEAR` en dos pasos: nombre y componentes introducidos en
  cuadros de texto editables.
- Convertir `FRONT PANEL` y `UNIFORM` en cuadros de texto sin opciones fijas.

### 5. P.R.S. combinado

- Archivar la documentación histórica de las versiones v3.0 y v4.0 en
  `docs/OLD VERSIONS`.
- Sustituir el selector de versiones por `SENTRY`, `TRACKER`, `DEVICES` y
  `USER GUIDE`.
- En `SENTRY`, ofrecer `PIP` y `PIP + PROBE` como modos de vigilancia sin
  seguimiento de un dispositivo concreto.
- En `TRACKER`, conservar el flujo de v4.0 y los nombres `ONLY PIP-BOY` y
  `PIP-BOY + PROBE`.
- Para guardar un dispositivo en `TRACKER` y poder rastrearlo, vincular
  previamente los dos dispositivos participantes.
- Hacer que `SENTRY` y `TRACKER` respeten las exclusiones configuradas en
  `DEVICES`.
- Crear un `USER GUIDE` nuevo en castellano sin contenido de `OLD VERSIONS`.

### 6. RADS

- Corregir la activación del audio en los modos manual y sensor.
- Sustituir el clic anterior por un efecto original de contador Geiger,
  inspirado en el carácter del vídeo de referencia.
- Mantener el mismo sonido para el medidor RADS y la lectura de radiación del
  mapa.

### 7. Distribución Alpha

Generar tres copias de la APK `fullDebug` v2.9, con nombres identificables para
FENRIR, CHECHU y ALTAMIRA. Las copias incluyen los mapas definidos para cada
tester.

### 8. Regla de generación de mapas

Cuando se soliciten mapas con nuevas coordenadas, se deben generar siempre
desde cero: fuente geográfica nueva para la zona, GeoPackage nuevo y MBTiles
nuevo. No se debe reutilizar, recortar ni recentrar un mapa existente en otra
ubicación.

La automatizaciÃ³n de mapas debe generar e incorporar la capa `contours_2m`
con intervalo de 2 m cuando exista un MDT para la zona solicitada.

## Cierre

Sprint 029 cerrado el 2026-09-03. El alcance comprometido queda completado y
validado. Queda como pendiente no crítico recibir las coordenadas definitivas
de CHECHU para regenerar desde cero su mapa `TESTING`, que permanece vacío
hasta disponer de esa información.

## Fuera de alcance

- Añadir nuevas armas o modelos al catálogo de INVENTORY.
- Crear perfiles de equipamiento múltiples independientes.
- Cambiar la lógica de DATA LOG, estadísticas de operaciones o el motor
  estadístico de P.R.S.
- Publicar las APK en una tienda o servicio externo.

## Criterios de aceptación

- `versionName=2.9` y `versionCode=9`.
- No aparecen listas fijas de réplicas en los flujos de configuración.
- PRIMARY y SECONDARY permiten introducir manualmente rol/tipo y modelo, y
  `APPLY` genera opciones persistentes.
- `SET-UP > DATA > WEAPON REPLICAS` es vertical y ofrece `EDIT` y `DELETE`.
- `SET-UP > DATA > CUSTOM LISTS` permite editar y borrar cada opción de las
  tres listas personalizables.
- `SET-UP > ACCESORIES` permite guardar un accesorio nuevo con `APPLY` y deja
  el campo vacío para introducir otro.
- `SET-UP > INPUT` ya no muestra el bloque `NEW ACCESORY`.
- DATA muestra y permite editar o borrar todas las opciones de armas creadas.
- `HEADGEAR` solicita primero el nombre y después sus componentes en cuadros
  de texto.
- `FRONT PANEL` y `UNIFORM` se rellenan manualmente en cuadros de texto.
- `INFORMATION > TESTERS` muestra un párrafo, no una lista de nombres.
- `P.R.S.` muestra `SENTRY`, `TRACKER`, `DEVICES` y `USER GUIDE`.
- `SENTRY` ofrece `PIP` y `PIP + PROBE` sin selección de objetivo.
- `TRACKER` conserva `ONLY PIP-BOY` y `PIP-BOY + PROBE` y respeta `DEVICES`.
- `TRACKER` exige vincular previamente los dos dispositivos antes de guardar un
  objetivo que se quiera rastrear.
- `USER GUIDE` está en castellano y no incluye contenidos de `OLD VERSIONS`.
- `RADS` emite el sonido de contador tanto en manual como en sensor cuando hay
  nivel de radiación representado.
- Tests, lint y ensamblado terminan correctamente.
- Existen tres APK v2.9 con el mismo contenido y nombres de Alpha Tester.

## Estado actual

La firma v2.9 ya está aplicada. Los tests, lint y la compilación `fullDebug` de
desarrollo se han validado. La APK `MAIN` y las tres copias Alpha se han
generado e instalado en el A56; la guía DOCX de pruebas está preparada para su
envío.
