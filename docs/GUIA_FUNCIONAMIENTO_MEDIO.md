# Guía de funcionamiento de SuriOS — nivel MEDIO

> Documento vivo. Última revisión: 2026-09-04. Se actualizará junto con los
> cambios de pantallas, funcionamiento, parámetros y pruebas. Si el código
> cambia y esta guía no cambia con él, la guía queda pendiente de revisión.

## 1. Arquitectura general

PIP-SuriOS es una aplicación Android escrita en Kotlin con interfaz Jetpack
Compose. La ejecución puede entenderse en cuatro capas:

```text
Pantallas Compose
      ↓
Estado de pantalla y navegación
      ↓
Servicios/repositorios de datos y sensores
      ↓
Android, Bluetooth, GPS, sensores, Data Layer y archivos offline
```

La navegación principal se concentra en `MainActivity`, que mantiene el destino
actual y compone la pantalla correspondiente. Las pantallas de P.R.S. reutilizan
los motores comunes de adquisición y procesamiento.

Referencias principales del código:

- [`MainActivity.kt`](../app/src/main/java/com/suri/pipsurios/MainActivity.kt)
- [`PrsTrackingScreen.kt`](../app/src/main/java/com/suri/pipsurios/ui/screens/PrsTrackingScreen.kt)
- [`IndividualTrackingScreens.kt`](../app/src/main/java/com/suri/pipsurios/ui/screens/IndividualTrackingScreens.kt)
- [`BleScanner.kt`](../app/src/main/java/com/suri/pipsurios/prs/BleScanner.kt)
- [`PrsContactTracker.kt`](../app/src/main/java/com/suri/pipsurios/prs/PrsContactTracker.kt)
- [`PrsModels.kt`](../app/src/main/java/com/suri/pipsurios/prs/PrsModels.kt)

## 2. Dos superficies que conviene distinguir

### 2.1 P.R.S. general

`PrsTrackingScreen` puede mostrar todos los contactos detectados. En SENTRY se
llama con `allowTargetSelection = false`, por lo que sirve como vigilancia
general. En esta superficie el escáner se inicia dentro de un
`DisposableEffect` cuando la pantalla entra en composición y se detiene al
desmontarse.

### 2.2 TRACKER individual

El flujo actual de TRACKER utiliza `IndividualTrackingTargetScreen` y
`IndividualTrackerMapContent`:

1. `IndividualTrackingTargetScreen` arranca el escaneo para poblar la lista de
   objetivos.
2. El operador selecciona un mapa y un contacto.
3. La selección se entrega mediante `IndividualTrackingSelection` en memoria.
4. `IndividualTrackerMapContent` crea un nuevo `BleScanner` y un nuevo
   `PrsContactTracker`.
5. En cada observación, solo deja pasar el contacto que coincide con el
   objetivo seleccionado.
6. El resultado se dibuja sobre el mapa TERRAIN.

La selección no es un registro persistente. Es un objeto temporal que enlaza el
`mapId` con los datos del objetivo elegido.

## 3. Respuesta exacta sobre START, STOP y CALCULATE

La implementación actual no contiene un estado de sesión equivalente a:

```text
IDLE → READING → STOPPED → CALCULATING → RESULT
```

En su lugar, el ciclo de vida es:

```text
COMPOSED
  ├─ inicia BLE
  ├─ inicia GPS y heading
  ├─ inicia START_RECON en PROBE si corresponde
  ├─ recibe observaciones
  ├─ evalúa el tracker cada 3.000 ms
  └─ DISPOSED: detiene y limpia recursos
```

Esto significa que tu interpretación de “lecturas y cálculos fluidos mientras
ando” es correcta para el modelo actual. Es importante separar dos frecuencias:

- **frecuencia de adquisición**: la determina Android y el ritmo de anuncios
  BLE; no está fijada a un número de muestras por segundo por el código de
  TRACKER;
- **frecuencia de evaluación**: `PrsTuning.DEFAULT.evaluationIntervalMillis`,
  actualmente `3.000 ms`.

El callback BLE llama a `tracker.observe(observation)`. Ese método actualiza
`latest` y `sampleCount`, pero no añade un punto al historial ni cambia
directamente la tendencia. La coroutine periódica llama a
`tracker.evaluate(SystemClock.elapsedRealtime())`, que sí produce el punto
temporal procesado.

Una consecuencia visible es que `RAW RSSI` puede cambiar antes que `SMOOTH RSSI`
o `TREND`. No es un fallo de sincronización: son capas diferentes del modelo.

## 4. Cadena de datos de una lectura

Una observación del A56 sigue esta cadena:

```text
BluetoothLeScanner
  → ScanResult
  → BleObservation
  → filtro DEVICES
  → PrsContactTracker.observe()
  → snapshot inmediato
  → evaluate() cada 3 s
  → PrsContactSnapshot
  → PrsProbabilityFog en TRACKER / PrsDensityGrid en las superficies GRID
```

En modo `SCAN + PROBE`, el Watch 2 envía muestras por la Data Layer. El
`ProbeTelemetryStore` comprueba que el nodo y la sesión sean los esperados. Las
muestras aceptadas se convierten al mismo tipo `BleObservation`, con fuente
`PROBE_WATCH_2`, y entran en el mismo tracker.

Así se mantienen separadas la fuente A56 y la fuente Watch 2, aunque el
procesamiento posterior sea común.

## 5. Algoritmo de procesamiento actual

Para cada contacto, `PrsContactTracker` conserva:

- la observación más reciente;
- el momento de primera aparición;
- el número total de observaciones recibidas;
- el RSSI suavizado;
- hasta 8 puntos en `RssiHistoryPoint`;
- la tendencia actual y una tendencia candidata pendiente de confirmación.

### 5.1 Suavizado

Con el valor por defecto `α = 0,35`, el suavizado es equivalente a:

```text
suavizado_nuevo = suavizado_anterior
                  + 0,35 × (RSSI_raw - suavizado_anterior)
```

El primer valor suavizado parte del RSSI RAW. Un `α` menor reaccionaría más
despacio; uno mayor seguiría más de cerca el ruido. El valor actual es una
decisión inicial de campo, no una calibración universal.

### 5.2 Historial temporal

Solo se añade un punto cuando `evaluate()` encuentra una observación posterior
a la última evaluada. El historial conserva como máximo 8 puntos y elimina el
más antiguo cuando se supera ese tamaño.

La evidencia de tendencia exige:

- al menos 4 puntos;
- que el primer y el último punto estén separados por al menos 9 segundos.

Hasta entonces, el estado es `INSUFFICIENT_DATA`.

### 5.3 Tendencia

La evidencia principal es la diferencia entre el RSSI suavizado más nuevo y el
más antiguo de la ventana:

```text
delta = suavizado_más_nuevo - suavizado_más_antiguo
```

Con la convención de RSSI usada por Android, un incremento significa un número
menos negativo y normalmente una señal más fuerte.

- `delta >= +4,5 dB`: evidencia de `APPROACHING`.
- `delta <= -4,5 dB`: evidencia de `MOVING_AWAY`.
- dentro de la zona pequeña: `STABLE`.

El cambio no se acepta inmediatamente. Se mantiene una tendencia candidata y
se requieren dos evaluaciones de confirmación. La histéresis de `1,5 dB`
reduce los cambios repetidos alrededor del umbral.

### 5.4 Banda de proximidad

La banda se calcula a partir del RSSI suavizado actual:

| Banda | Condición |
|---|---|
| NEAR | `smoothedRssi >= -76` |
| MEDIUM | `-88 <= smoothedRssi < -76` |
| FAR | `smoothedRssi < -88` |
| UNKNOWN | sin historial procesado |

La palabra “proximidad” aquí significa categoría relativa. No hay una función
`RSSI → metros`.

### 5.5 Expiración y limpieza

Si pasan más de 15 segundos desde la última observación de un contacto, el
contacto expira. `CLEAR CONTACTS` vacía las estructuras en memoria, pero no
detiene el `BluetoothLeScanner`; por ello los contactos pueden reaparecer.

## 6. Qué dibuja el mapa TRACKER

El mapa contiene el MBTiles seleccionado, overlays TERRAIN y la posición del
operador. El centro sigue el fix GPS del A56 cuando este está disponible. La
orientación se obtiene de `TerrainHeading` y se incorpora a la transformación
de la vista.

En TRACKER, el objetivo seleccionado se dibuja con `PrsProbabilityFog`. La
capa cubre el mapa con una niebla irregular y calcula su densidad a partir de
`DensityCloud`. Las áreas con menor probabilidad reciben menos niebla y dejan
ver más cartografía. En las superficies que conservan el GRID, el objetivo
sigue dibujándose con `PrsDensityGrid`.

El modelo mantiene `azimuthCoverage = 1f`: la cobertura angular es completa
porque un receptor BLE único no proporciona bearing. Por ello la niebla es una
representación relativa de incertidumbre, no una coordenada del objetivo.

El mapa de TRACKER admite `detectTransformGestures` sobre el área cartográfica.
El pellizco modifica `zoom` entre los límites del mapa y conserva el punto
geográfico situado bajo el centro del gesto mediante
`TerrainViewportTransform.applyGesture()`. El movimiento se limita a la
cobertura disponible de los tiles para no enseñar zonas fuera del mapa.

### 6.1 Cálculo visual de la niebla

`PrsProbabilityFog` divide visualmente el mapa en una malla de nubes suaves,
pero no dibuja líneas de grid. Para cada zona calcula una distancia relativa al
A56 y la compara con el centro y la extensión de `DensityCloud`:

```text
densidad de niebla = incertidumbre restante + probabilidad relativa
```

La confianza reduce la incertidumbre restante. Cuando la confianza es baja,
se conserva una capa amplia para no dar una falsa sensación de precisión.
Cuando aumenta, las zonas alejadas del centro probable se despejan más. La
función sigue siendo radial porque el BLE no aporta dirección.

En `SCAN + PROBE`, `probeGridPosition()` sí calcula la posición relativa del
Watch 2 usando sus coordenadas GPS y las del A56. Esto localiza el nodo PROBE,
no el dispositivo BLE que el operador está siguiendo.

## 7. Lo que no está implementado en TRACKER

La implementación actual no tiene:

- botón explícito `START READING`;
- botón explícito `STOP READING` que congele la sesión;
- cálculo final separado al detener;
- almacenamiento automático de la sesión de TRACKER;
- distancia exacta del objetivo;
- bearing BLE del objetivo;
- triangulación o coordenada física del objetivo;
- fusión de movimiento del operador para convertir la nube en una posición.

Existe un modelo opcional `PrsMovementContext`, pero en esta versión no se usa
para crear una localización del objetivo. Mantener esa separación evita
presentar una inferencia experimental como si fuera una medición física.

## 8. Implicación práctica para una prueba de campo

Una prueba coherente con la implementación actual sería:

1. Abrir TRACKER y seleccionar el campo.
2. Seleccionar el dispositivo objetivo.
3. Esperar varios segundos para que aparezca historial suficiente.
4. Caminar manteniendo TRACKER abierto.
5. Observar por separado RSSI RAW, RSSI suavizado, tendencia y banda.
6. Salir con BACK para detener los recursos.

No conviene evaluar una tendencia con una sola lectura ni interpretar una nube
como un punto GPS. Para un futuro flujo manual habría que introducir un estado
explícito de captura, congelar o etiquetar el intervalo de observación y
definir qué significa “calcular” al pulsar STOP.

## 9. Pruebas y límites de interpretación

Las pruebas unitarias de `PrsContactTracker` cubren, entre otros casos, que:

- una observación RAW nueva no cambia inmediatamente el historial procesado;
- una tendencia necesita ventana y confirmación;
- los contactos caducan;
- se distingue el identificador del contacto.

Estas pruebas verifican la lógica del modelo, no demuestran que RSSI sea una
medida fiable de distancia en todos los entornos. La calibración física del A56,
del Watch 2 y de cada dispositivo objetivo sigue siendo una actividad de campo
separada.

## 10. RADS y las tres capas de audio

`RadsClickSound` carga `sounds/1.mp3`, `sounds/2.mp3` y `sounds/3.mp3`
mediante `SoundPool`, desde el directorio común `assets/sounds`. Las tres
pistas se reproducen en bucle mientras el nivel lo requiere y cada canal usa
volumen fijo `1.0`.

`ClickScheduler` convierte el nivel interno 0..1 al nivel visible entero 0..10:

```text
nivelVisible = redondear(nivel × 10)
```

La selección es:

```text
0       -> silencio
1..2    -> audio 1
3       -> audio 1 + audio 2
4..5    -> audio 2
6       -> audio 2 + audio 3
7..10   -> audio 3
```

Los niveles 3 y 6 son solapes intencionados. Cambia la selección de pistas,
no el volumen ni la velocidad de reproducción.

El cambio de pista se revisa cada 40 ms. Así se evita que la aguja cambie de
nivel y el audio espere al siguiente intervalo largo del programador.
