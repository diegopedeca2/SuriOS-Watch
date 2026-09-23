# Sprint 035 - Prueba de campo A56

Esta lista sirve para validar MAP sin conexión a Internet. El overlay de
Airsoft Total se acepta como base operativa de este Sprint y conserva la marca
`PROVISIONAL_QGIS_REVIEW` para indicar que su revisión fina en QGIS se hará
más adelante.

## Preparación

- Instalar la APK `fullDebug` v3.3.
- Confirmar que la ubicación está autorizada y que el GPS está activo.
- Llevar el teléfono con brújula/sensor de orientación disponible.
- No activar datos móviles ni Wi-Fi durante la prueba.
- Seleccionar `TOOLS > MAP > TERRAIN` y elegir `AIRSOFT TOTAL`.

La orientación probada en interiores no es concluyente: estructuras metálicas
u otros dispositivos pueden alterar el campo magnético. La validación final
de la flecha debe hacerse en exterior, lejos de vehículos y masas metálicas.

## Prueba del overlay

- Confirmar que se ve el mapa topográfico PIP-SuriOS.
- Confirmar que el fondo del raster es crema claro, los edificios son grises,
  los caminos son azules y las curvas de nivel son verdes, con curvas
  principales moradas, sin ocultar GRID ni POI.
- Confirmar que se dibujan GRID, perímetro, caminos y POI.
- Usar también el listado `ORGANIZATION POI` del panel si un marcador queda
  tapado o es difícil de pulsar.
- Pulsar `RESPAWN 2`, `BASE 1` y un POI con nombre.
- Confirmar que aparece `NAVIGATE` sin eliminar ni modificar el POI.

## Prueba de navegación a POI

1. Pulsar `NAVIGATE` sobre un POI.
2. Pulsar el punto azul del usuario para recentrar el mapa antes de valorar su
   alineación con el teléfono.
3. Confirmar que el HUD muestra nombre, flecha, distancia y rumbo.
4. Girar lentamente el teléfono y comprobar que la flecha cambia de forma
   estable y representa la dirección relativa.
5. Caminar unos metros y comprobar que la distancia se actualiza.
6. Hacer zoom y desplazar el mapa; el HUD debe continuar visible.
7. Pulsar `STOP NAVIGATION` y confirmar. El POI debe permanecer en el mapa.

## Prueba de waypoint manual

1. Mantener pulsada una zona libre del mapa.
2. Confirmar que aparece el marcador provisional y `SET WAYPOINT?`.
3. Pulsar `CONFIRM`.
4. Comprobar distancia, rumbo y flecha mientras se camina.
5. Desplazar o ampliar el mapa hasta que el waypoint no sea visible.
6. Pulsar `CLEAR WAYPOINT` desde el HUD y confirmar.
7. Confirmar que desaparece el HUD y que no queda destino activo.

## Prueba de POI en OFFICE

1. Volver a `TOOLS > MAP > TERRAIN` y elegir `OFFICE`.
2. Confirmar que aparecen `RAST`, `REPLICANT`, `ELÍAS` y `CHURROSTAR`.
3. Seleccionar uno de ellos desde el mapa o desde `ORGANIZATION POI`.
4. Pulsar `NAVIGATE` y repetir la comprobación de distancia, rumbo y flecha.
5. Pulsar `STOP NAVIGATION` y confirmar que el POI sigue visible.

## Funciones existentes

- Crear y eliminar un RESPawn manual.
- Crear y eliminar una RAD ZONE.
- Confirmar que el efecto Geiger sigue funcionando a menos de 10 m.
- Probar `EMPTY MAP` y confirmar la acción dos veces.
- Confirmar que el pinch-to-zoom sigue funcionando.

## Resultado

Registrar fecha, modelo del teléfono, precisión GPS aproximada y cualquier
salto anómalo de brújula. La revisión fina de coordenadas se hará más
adelante; durante esta prueba se debe anotar cualquier POI que parezca
desplazado para revisarlo posteriormente en QGIS.
