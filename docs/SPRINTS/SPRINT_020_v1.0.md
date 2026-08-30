# Sprint 020 — Corrección de TERRAIN y regeneración de NAVY7

## Estado

- Apertura: 2026-08-30.
- Cierre: 2026-08-30.
- Estado: cerrado.
- Aplicación objetivo: PIP-SuriOS full.

## Objetivo

Corregir el campo NAVY7 y el selector de ubicaciones de `MAP - TERRAIN` sin
alterar la configuración validada de HOME.

## Entregables

- Regenerar desde QGIS el MBTiles raster PNG de NAVY7 con centro
  `40.35297419412242, -3.4237021485063486`.
- Sustituir el asset anterior de NAVY7 en
  `app/src/main/assets/maps/navy_7_terrain.mbtiles`.
- Mostrar `CHOOSE LOCATION` por defecto al entrar en TERRAIN.
- Mantener `CHOOSE LOCATION` como primera opción excepcional y ordenar HOME y
  NAVY7 alfabéticamente después.
- Añadir pruebas del centro de NAVY7 y del orden del catálogo.

## Configuración cartográfica

QGIS LTR 3.44.13 utilizó el proyecto piloto `MAP SuriOS.qgz`, con las capas
`highway`, `contours_2m` y `building` activas. El MDT y OpenStreetMap quedaron
desactivados. La salida mantiene el contrato de HOME: MBTiles PNG de tipo
overlay, zoom nativo 16–19 y lectura offline mediante SQLite.

La huella WGS84 de trabajo fue:

`-3.4266621485063486,40.35152419412242,-3.4207421485063486,40.35442419412242`

La cuadrícula XYZ ajusta los límites raster a sus teselas; por eso el centro
físico del mosaico puede diferir unos metros del centro decimal solicitado.

## Validación

- `PRAGMA integrity_check` del nuevo MBTiles: `ok`.
- Metadatos: `format=png`, `type=overlay`, `minzoom=16`, `maxzoom=19`.
- HOME permanece sin cambios.
- `:app:testFullDebugUnitTest`: correcto.
- `:app:lintFullDebug`: correcto.
- `:app:connectedFullDebugAndroidTest` en Samsung A56 (`SM-A566B`): 2 tests correctos.
- La propiedad `android.injected.androidTest.leaveApksInstalledAfterRun=true`
  conserva la aplicación y el APK de tests instalados tras la validación.

## Cierre documental

El asset instalado en `app/src/main/assets/maps/navy_7_terrain.mbtiles` es
idéntico al MBTiles generado en QGIS y distinto del asset anterior registrado
en Git. Su SHA-256 es:

`5260274BBECA9E11573A9EBADB1D917DF90ADB3D866E6292684D6219DD6BA568`

Sprint 020 queda cerrado técnica, funcional y documentalmente el 2026-08-30.
No quedan acciones pendientes dentro de su alcance.
