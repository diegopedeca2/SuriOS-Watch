# Sprint 021 — INDIVIDUAL TRACKING

## Estado

- Apertura: 2026-08-30.
- Cierre: 2026-08-30.
- Estado: cerrado.
- Implementación: completada y validada.
- Aplicación objetivo: PIP-SuriOS full.
- Punto de entrada previsto: `TOOLS > PROXIMITY RADIO SCANNER > INDIVIDUAL TRACKER`.

## Objetivo

Crear una herramienta experimental que combine `MAP - TERRAIN` con P.R.S. para
seguir temporalmente un único contacto BLE seleccionado por el operador.

La herramienta debe mostrar el contexto geográfico real del operador junto con
la evidencia temporal del objetivo seleccionado, sin convertir P.R.S. en un
sistema de posicionamiento.

## Flujo previsto

`TOOLS → PROXIMITY RADIO SCANNER → INDIVIDUAL TRACKER → TARGET → TRACKER`

En `TARGET` se selecciona primero un campo TERRAIN y después un único contacto
detectado por el escaneo local del A56. Las reglas de `DEVICES` se reutilizan;
si una regla de omisión habilitada oculta el contacto, debe deshabilitarse antes
de seleccionarlo. `TRACKER` abre el campo TERRAIN offline y presenta:

- posición y precisión GPS del operador;
- heading y modo de brújula del campo;
- mapa MBTiles y overlays TERRAIN existentes;
- nombre, fuente, identificador de sesión, RSSI RAW, RSSI SMOOTHED,
  tendencia, banda relativa, confianza y número de muestras del objetivo;
- evolución temporal del objetivo asociada a los fixes del operador durante la
  sesión, como evidencia experimental de campo.

## Regla de representación

El objetivo no se dibuja como una coordenada, rumbo, distancia exacta o punto
BLE sobre el mapa. Una señal BLE única no proporciona esos datos. Cualquier
superficie visual debe comunicar incertidumbre y distinguir claramente:

- datos medidos: observación BLE y GPS del operador;
- datos procesados: suavizado, historial y variación RSSI;
- inferencias: tendencia, banda relativa y nube/densidad experimental.

No se permitirá una conversión RSSI → metros, azimut BLE, triangulación ni
posición sintética del objetivo.

## Alcance técnico

- Añadir la entrada `INDIVIDUAL TRACKER` al menú P.R.S. dentro de TOOLS.
- Añadir la ruta de navegación y el ciclo de vida de la nueva pantalla.
- Reutilizar `BleScanner`, `PrsContactTracker` y `PrsDeviceRegistry`; no
  duplicar adquisición ni reglas de filtrado. Esta herramienta usa únicamente
  `LOCAL SCAN` del A56 y no es compatible con `SCAN + PROBE`.
- Reutilizar `TerrainLocation`, `TerrainHeading`, `MbTilesRepository` y
  `TerrainViewportTransform`.
- Mantener los overlays persistentes de TERRAIN separados de la evidencia
  temporal de la sesión de tracking.
- Mantener `prsOnlyDebug` sin un menú TOOLS paralelo; cualquier cambio de su
  superficie compacta requiere justificación específica.

## Fuera de alcance

- Posición física definitiva del objetivo.
- RSSI convertido en metros o coordenadas.
- Bearing BLE, Wi-Fi RTT, UWB, triangulación o machine learning.
- Modificación de `PrsTuning.DEFAULT` sin evidencia de campo separada.
- Escritura automática de la ruta experimental en los overlays persistentes del
  mapa.
- Integración con `SCAN + PROBE`, PROBE o Watch 2.
- Cambios funcionales en MAP OPERATION, SONAR estable, RADS o SuriOS Watch.

## Entregables

- Pantalla y estado de sesión para `INDIVIDUAL TRACKER`.
- Integración visible en `TOOLS > PROXIMITY RADIO SCANNER`.
- Modelos o transformaciones necesarios para representar la evidencia temporal
  sin inventar coordenadas del objetivo.
- Tests unitarios de selección, expiración, historial y representación de
  evidencia.
- Validación de navegación, permisos, back y detención del escaneo local.
- Actualización posterior de `PRS_v3.0`, `USER_GUIDE`, changelog y auditoría
  cuando la implementación quede terminada.

## Criterios de aceptación

1. La entrada aparece en la ruta exacta indicada y abre una pantalla propia.
2. Se puede seleccionar un único contacto activo y ver sus datos P.R.S. durante
   el seguimiento.
3. El mapa funciona offline con los campos TERRAIN existentes y muestra el fix
   del operador con su estado de precisión.
4. La ausencia de GPS, heading, mapa o permisos produce un estado explícito y
   no una posición inventada.
5. La sesión no altera los overlays persistentes ni los umbrales P.R.S.
6. `BACK` detiene correctamente los recursos temporales y devuelve al menú P.R.S.
7. Tests, lint, ensamblados y `git diff --check` terminan correctamente.

## Validación del cierre y regresión

- Sprint 020 continúa íntegro: el SHA-256 actual de NAVY7 es
  `5260274BBECA9E11573A9EBADB1D917DF90ADB3D866E6292684D6219DD6BA568`,
  coincidente con su cierre documental; `CHOOSE LOCATION`, el centro NAVY7 y
  el orden del catálogo siguen cubiertos por `MapTerrainTest`.
- La incidencia intermedia de la aplicación no deja una regresión observable
  en el alcance de Sprint 021: la compilación fullDebug, los tests JVM, lint y
  el ensamblado vuelven a completarse correctamente sobre el árbol actual.
- `connectedFullDebugAndroidTest` ejecuta 2/2 pruebas correctamente en el
  Samsung A56 (`SM-A566B`). La revisión UI en el dispositivo confirmó la ruta
  `TOOLS > PROXIMITY RADIO SCANNER > INDIVIDUAL TRACKER > TARGET`, la selección
  de `NAVY7`, la llegada al listado de objetivos BLE y la apertura de
  `TRACKER` con `GRID: TARGET ONLY`, `CENTER: A56 // GPS FOLLOW` y `SOURCE: A56`.
- `git diff --check` termina correctamente. La prueba física de variación RSSI
  y cualquier modelo estadístico de recorte de incertidumbre quedan como
  trabajo posterior y no bloquean este cierre.

## Cierre

Sprint 021 queda cerrado técnica, funcional y documentalmente el 2026-08-30.
La herramienta permanece aislada como dependencia de P.R.S. y TERRAIN, sin
compatibilidad con PROBE ni `SCAN + PROBE`, y no modifica el estado operativo de
Sprint 022, que se cierra de forma independiente en su propio documento.
