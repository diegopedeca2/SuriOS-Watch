# Sprint 035 - MAP: ORGANIZATION OVERLAY + WAYPOINT NAVIGATION

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Closed
owner: Diego
date: 2026-09-23
predecessor: Sprint 034
app_version: 3.3
---

## Objetivo

Incorporar al mapa AIRSOFT TOTAL una primera capa vectorial de organizacion y
una navegacion offline sencilla por distancia, rumbo y direccion relativa.

La navegacion no calcula rutas y no sustituye a CivTAK.

## Plan por fases

- [x] Inspeccionar MAP actual, MBTiles, coordenadas, sensores y persistencia.
- [x] Separar los modelos ORGANIZATION OVERLAY, GRID, POI y NAVIGATION.
- [x] Incorporar el asset vectorial provisional de AIRSOFT TOTAL.
- [x] Integrar GRID, POI, waypoint manual y destino comun en MAP.
- [x] Mantener RESPawns, RAD ZONE, Geiger, DELETE/CLEAR/EMPTY y pinch-to-zoom.
- [x] Ejecutar pruebas JVM, ensamblado y lint.
- [x] Aceptar para este Sprint la alineación inicial de las coordenadas; la
      verificación fina en QGIS se difiere a una revisión posterior.
- [x] Recuperar la conexion A56 y ejecutar las pruebas instrumentadas de la
      version anterior.
- [x] Repetir las pruebas instrumentadas en A56 con el raster luminoso nuevo.
- [x] Regenerar AIRSOFT TOTAL desde cero, con la paleta clara de HOME, NAVY7
      y OFFICE, cobertura 4 km x 4 km y perímetro vectorial visible.
- [x] Incorporar los POI de prueba RAST, REPLICANT, ELÍAS y CHURROSTAR en
      OFFICE.
- [x] Corregir y repetir el cambio entre mapas en A56 sin cierre de la app.
- [x] Dejar documentada la prueba física de campo con GPS, orientación y
      gestos como comprobación operativa posterior al cierre.

## QGIS y assets

La imagen `assets/map/MAPA JUGADORES.pdf` se usa como referencia visual. No se
usa como mapa base final. El asset Android
`app/src/main/assets/maps/airsoft_total_organization.overlay` contiene capas
vectoriales independientes: GRID, FIELD_BOUNDARY, INTERNAL_PATHS y POI.

El raster AIRSOFT TOTAL usa ahora la paleta clara `SURIOS_DAY_V1` de HOME,
NAVY7 y OFFICE: fondo crema, edificios grises, caminos azules, curvas verdes
y curvas principales moradas. Se ha regenerado desde el GeoPackage de QGIS,
no desde el MBTiles anterior. La cobertura nueva es de 4 km x 4 km, con el
mismo centro, zoom 16..19 y 6.727 teselas PNG. El perímetro se dibuja como
capa vectorial independiente sobre el raster y se ha revisado para que RESPawn
1 quede dentro.

La orden reproducible queda documentada en
`tools/gis/ORGANIZATION_OVERLAY_WORKFLOW.md`; el mapa Android usa los bounds
`-4.293653996060,40.792677278878,-4.246246827323,40.828697017677`.

El mapa OFFICE dispone además de `office_organization.overlay`, con los cuatro
POI suministrados para las pruebas: RAST, REPLICANT, ELÍAS y CHURROSTAR.

Sus coordenadas permanecen marcadas como `PROVISIONAL_QGIS_REVIEW` para no
falsear la precision. Para este Sprint se acepta la alineación inicial como
base operativa y se da por cerrado este punto; la revisión fina del PDF en
QGIS con puntos de control reales queda como mejora posterior y no bloquea la
prueba actual.

## Android

`NavigationEngine` calcula localmente distancia, bearing, bearing relativo y
celda GRID. `MapDestination` es comun para USER_WAYPOINT y ORGANIZATION_POI.
La orientación del teléfono se obtiene con `TYPE_ROTATION_VECTOR`, se remapea
según la rotación horizontal de la pantalla y se suaviza antes de aplicarla al
mapa y al HUD.
El HUD usa una tipografía sans-serif más legible, y la flecha se ha ampliado a
44sp para facilitar su lectura en campo. Las etiquetas de GRID conservan el
naranja oficial `PipAmber`; los textos de los POI usan ahora el rojo oficial
`PipRed`, son 25% más grandes y mantienen la negrita.
El cambio de mapa cancela cargas antiguas, cierra su SQLite y no publica datos
si la carga terminó después de seleccionar otro campo.
El indicador de navegacion vive en la interfaz, por lo que sigue activo aunque
el destino quede fuera de la zona visible del mapa. El mismo HUD ofrece
`CLEAR WAYPOINT` o `STOP NAVIGATION`, sin depender de que el marcador sea
visible.

## Fuera de alcance

Routing, A*, Dijkstra, navegacion giro a giro, colaboracion, CivTAK, objetivos
moviles y seguimiento de jugadores quedan fuera de este Sprint.

## Validacion actual

La secuencia manual para la validacion en A56 esta documentada en
[`SPRINT_035_FIELD_TEST.md`](SPRINT_035_FIELD_TEST.md).

- Pruebas JVM: correctas.
- Ensamblado `fullDebug`: correcto.
- Lint `fullDebug`: correcto.
- Las geometrías del overlay usan `latitud,longitud` y se validan contra los
  bounds del MBTiles de AIRSOFT TOTAL.
- Entorno QGIS/Orca comprobado con QGIS 3.44.13: PyQGIS, Processing,
  escritura/lectura de GeoPackage y proyecto QGIS funcionan. La revisión fina
  de la georreferenciación del PDF se difiere a una iteración posterior y no
  bloquea este Sprint.
- Pruebas instrumentadas en emulador `Pixel_8` (API 35): correctas, 5/5,
  incluyendo carga offline del overlay, GRID, POI y geometrías.
- Pruebas instrumentadas actuales en A56: correctas, 7/7 en `SM-A566B`,
  identificador ADB `RZGYC07H0EX`, incluyendo los POI de OFFICE.
- El asset AIRSOFT TOTAL fue generado desde cero con QGIS y
  `SURIOS_DAY_V1`, hash SHA-256 actualizado y comprobación de
  integridad, metadata y 6.727 teselas.
- La APK `fullDebug` v3.3 se instaló correctamente y `MainActivity` arrancó en
  el A56; la prueba de campo con GPS y brújula todavía queda pendiente.
- El cambio AIRSOFT TOTAL → OFFICE → AIRSOFT TOTAL → OFFICE se repitió en el
  A56 y la aplicación permaneció activa. El cierre detectado inicialmente se
  debía a una etiqueta GRID fuera de pantalla durante la transición; la carga
  de overlays y el dibujo de etiquetas quedaron protegidos.
- Smoke test de interfaz en A56 con la APK nueva y el raster claro: se abrió
  `MAP > TERRAIN > AIRSOFT TOTAL`, se
  confirmó `GPS ACTIVE` con rumbo, se visualizaron curvas de nivel, GRID y POI,
  y se completó `long press > SET WAYPOINT > CONFIRM > NAVIGATION`. El HUD
  mostró flecha, distancia, bearing y `GRID TO`; después se completó
  `CLEAR WAYPOINT > CONFIRM`.
- También se completó el flujo `RESPAWN 2 > NAVIGATE`, con HUD, `GRID TO B-4`,
  `STOP NAVIGATION > CONFIRM` y desaparición del HUD. Las acciones de parada y
  limpieza quedan en el contenido desplazable del panel para que el texto no
  duplique acciones ni ocupe espacio innecesario.
- Esta comprobación fue de interfaz y no equivale a caminar en campo. La
  actualización de distancia al caminar, la orientación física, las
  coordenadas QGIS y la regresión completa de funciones existentes quedan en
  la prueba física.
- Prueba física completa en campo: queda como validación operativa del
  propietario posterior al cierre. No bloquea la entrega de este Sprint.

## Cierre documental - 2026-09-23

La auditoría final no detecta incidencias técnicas abiertas dentro del alcance.
El Sprint 035 queda **CLOSED** con PIP-SuriOS `versionName=3.3` y
`versionCode=13`. La prueba física en exteriores se mantiene como verificación
de uso real de GPS y brújula; una eventual desviación de sensores en interiores
no se considera un fallo confirmado del código.
