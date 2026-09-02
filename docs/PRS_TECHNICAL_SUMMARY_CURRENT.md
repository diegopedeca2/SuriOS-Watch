# P.R.S. — Resumen técnico actual

## Ficha del documento

- **Proyecto:** PIP-SuriOS, dentro de SuriOS Ecosystem.
- **Aplicación:** `com.suri.pipsurios`.
- **Versión de la aplicación en este corte:** `2.8`.
- **Arquitectura de P.R.S.:** v3.0 activa y v4.0 inicial sobre mapa.
- **Dispositivo principal:** Samsung Galaxy A56.
- **Dispositivo auxiliar opcional:** Xiaomi Watch 2 mediante el módulo Wear OS `PROBE`.
- **Fecha del corte:** 2026-09-01.
- **Estado:** implementación actual, no diseño histórico.

Este documento describe cómo funciona hoy P.R.S. en el código de
`D:\WristOS`. Está preparado para entregarlo a otro chatbot como contexto
técnico antes de diseñar una modificación. La versión `2.8` es la versión de
la aplicación móvil; no significa que la arquitectura de P.R.S. haya vuelto a
la versión antigua.

## 1. Qué es P.R.S.

P.R.S. significa **Proximity Radio Scanner**. Es una herramienta experimental
que detecta anuncios Bluetooth Low Energy (BLE) cercanos y observa cómo cambia
la intensidad de cada señal con el tiempo.

Su salida es un indicador de señales cercanas. No es un sistema de radar, no
es un localizador y no determina si hay personas detrás de una puerta.

La cadena principal es:

```text
BLE SCAN
  -> CONTACTS
  -> RAW RSSI
  -> TEMPORAL HISTORY
  -> SMOOTHING
  -> TREND
  -> RELATIVE PROXIMITY
  -> DENSITY GRID
```

El sistema mantiene separadas tres capas:

1. **Medido:** datos entregados por Android o por el Watch 2 PROBE.
2. **Procesado:** datos calculados a partir de las mediciones, como RSSI
   suavizado, media, variación e historial.
3. **Inferido:** interpretación orientativa, como `NEAR`, `APPROACHING` o una
   nube de densidad.

Ninguna inferencia se presenta como una coordenada física medida.

## 2. Menú activo

La edición completa de PIP-SuriOS organiza P.R.S. dentro de
`TOOLS > PROXIMITY RADIO SCANNER` en dos versiones:

### v3.0 — funcionamiento actual

| Entrada | Funcionamiento |
|---|---|
| `LOCAL SCAN` | Escanea BLE solamente con el A56. |
| `SCAN + PROBE` | Escanea con el A56 y solicita al Watch 2 PROBE que haga su propia adquisición BLE. |
| `DEVICES` | Identifica dispositivos y administra reglas guardadas. |
| `INDIVIDUAL TRACKER` | Selecciona un contacto y lo sigue en una pantalla experimental junto a TERRAIN. |
| `OPERATION GUIDE` | Actualmente está vacío de forma intencionada; no hay un procedimiento de campo activo en el alcance actual. |

### v4.0 — flujo inicial sobre mapa

`v4.0` comienza con la misma idea de `INDIVIDUAL TRACKER`, pero separada en
dos pasos:

1. **Identificar TARGET y ubicación:** se selecciona el campo TERRAIN y luego
   el contacto BLE que se seguirá.
2. **GRID sobre mapa:** se muestra el mapa offline seleccionado con el GRID
   P.R.S. superpuesto y centrado en la posición GPS del A56.

El operador puede elegir dos modos:

| Modo | Funcionamiento |
|---|---|
| `ONLY PIP-BOY` | Adquisición BLE con el A56. |
| `PIP-BOY + PROBE` | El mismo flujo, añadiendo el Watch 2 PROBE como baliza remota y conservando su comunicación y telemetría actuales. |

Esta primera base de `v4.0` prepara la navegación y la superficie de mapa;
el modelo probabilístico completo de la propuesta todavía no está activo.

La edición `prsOnlyDebug` es una versión compacta para la pantalla exterior
del Z Flip 6. Arranca en un menú propio con:

- `SCAN`: lista de contactos BLE en vivo.
- `GRID`: solo la visualización de densidad.
- `DEVICES`: identificación y reglas guardadas.

La edición compacta usa el modo `LOCAL_SCAN`; no expone `SCAN + PROBE`.

## 3. Piezas principales del código

| Archivo | Responsabilidad |
|---|---|
| `app/src/main/java/com/suri/pipsurios/ui/screens/PrsTrackingScreen.kt` | Orquesta la pantalla de escaneo, permisos, ciclo de vida, tracker y PROBE. |
| `app/src/main/java/com/suri/pipsurios/prs/BleScanner.kt` | Arranca y detiene el escáner BLE del A56. |
| `app/src/main/java/com/suri/pipsurios/prs/PrsModels.kt` | Define observaciones, contactos, historial, tendencias, bandas y nubes. |
| `app/src/main/java/com/suri/pipsurios/prs/PrsContactTracker.kt` | Agrupa observaciones, conserva contactos y calcula el estado temporal de cada uno. |
| `app/src/main/java/com/suri/pipsurios/prs/PrsTuning.kt` | Contiene los valores provisionales de evaluación y umbrales. |
| `app/src/main/java/com/suri/pipsurios/prs/PrsDensityEstimator.kt` | Convierte una banda de proximidad en una nube visual de incertidumbre. |
| `app/src/main/java/com/suri/pipsurios/ui/screens/PrsDensityGrid.kt` | Dibuja la retícula, anillos, nubes, emblema y subgrid del PROBE. |
| `app/src/main/java/com/suri/pipsurios/prs/PrsDeviceRegistry.kt` | Guarda las reglas de dispositivos conocidos y sus estados. |
| `app/src/main/java/com/suri/pipsurios/prs/PrsDeviceCategory.kt` | Hace una clasificación orientativa del tipo de dispositivo. |
| `app/src/main/java/com/suri/pipsurios/prs/ProbeLink.kt` | Envía órdenes del teléfono al Watch 2 por Wear OS Data Layer. |
| `app/src/main/java/com/suri/pipsurios/prs/ProbeDataLayerService.kt` | Recibe mensajes de telemetría del PROBE. |
| `app/src/main/java/com/suri/pipsurios/prs/ProbeTelemetryStore.kt` | Mantiene en memoria el estado actual del PROBE y distribuye sus muestras. |
| `watch/probeprotocol/src/main/java/com/suri/probeprotocol/ProbeProtocol.kt` | Contrato de mensajes compartido entre móvil y reloj. |
| `app/src/main/java/com/suri/pipsurios/ui/screens/PrsDevicesScreen.kt` | Pantallas `IDENTIFY DEVICE`, `SAVED DEVICES` y `MAC ADDRESS GUIDE`. |
| `app/src/main/java/com/suri/pipsurios/ui/screens/IndividualTrackingScreens.kt` | Selección de campo TERRAIN y seguimiento experimental de un contacto. |

## 4. Adquisición BLE en el A56

### 4.1 Requisitos previos

Antes de escanear, `BleScanner` verifica:

- `BLUETOOTH_SCAN` concedido.
- `BLUETOOTH_CONNECT` concedido.
- `ACCESS_FINE_LOCATION` concedido.
- Bluetooth activado.
- Existencia de un `BluetoothLeScanner` disponible.

Si falta algo, el estado pasa a `PERMISSION_REQUIRED`, `BLUETOOTH_OFF`,
`UNSUPPORTED` o `ERROR`. La pantalla puede solicitar permisos o reintentar.

### 4.2 Funcionamiento del escáner

El escáner usa `BluetoothLeScanner.startScan` sin una lista de filtros y con
`SCAN_MODE_LOW_LATENCY`. Cada callback produce una instancia de
`BleObservation`.

Cada observación puede contener:

- identificador temporal de sesión;
- identificador técnico, normalmente la dirección BLE observada;
- RSSI bruto en dBm;
- tiempo monotónico transcurrido (`elapsedRealtime`);
- timestamp de calendario en milisegundos;
- nombre anunciado, si existe;
- bytes de advertising en hexadecimal;
- tipo de dispositivo Android;
- clase Bluetooth;
- tipo de dirección, si la API de Android lo permite;
- origen, que en este caso es `A56`.

El escáner crea etiquetas de sesión como `CONTACT-001` para facilitar la
lectura humana, pero esas etiquetas no sustituyen al identificador técnico
cuando este existe.

El escaneo se detiene cuando se abandona la pantalla P.R.S. o cuando la
actividad destruye la sesión. No se mantiene un escaneo global permanente.

## 5. Identidad de contactos

`PrsContactTracker` usa como clave principal el identificador técnico:

- En muestras del A56 se usa directamente el identificador técnico.
- En muestras del Watch 2 se antepone el origen, por ejemplo
  `WATCH-2:AA:BB:CC:DD:EE:FF`, para no confundir una observación del reloj con
  otra del teléfono.

El nombre BLE solo sirve para mostrar el contacto. Si no existe un nombre
usable, el tracker asigna una etiqueta temporal como `UNKNOWN 01`.

Las direcciones privadas o rotatorias son una limitación real de BLE. El mismo
dispositivo puede aparecer con otra dirección, y en ese caso P.R.S. no puede
afirmar que ambas observaciones sean la misma identidad.

Un contacto se elimina de la sesión cuando no recibe una observación nueva
durante 15 segundos. Los contactos y su historial son datos de la sesión en
memoria; no se crea un historial P.R.S. permanente.

## 6. Procesamiento temporal del RSSI

Una recepción BLE aislada no cambia inmediatamente la tendencia visual. El
tracker recibe callbacks, pero solo evalúa los contactos a una cadencia fija.

### 6.1 Valores actuales

Los valores de `PrsTuning.DEFAULT` son:

| Parámetro | Valor | Significado |
|---|---:|---|
| Cadencia de evaluación | 3 s | Intervalo mínimo entre evaluaciones procesadas. |
| Alpha de suavizado | 0,35 | Peso aplicado al nuevo RSSI en el suavizado exponencial. |
| Tamaño del historial | 8 muestras | Número máximo de puntos procesados conservados. |
| Muestras mínimas | 4 | Puntos necesarios para comenzar a inferir una tendencia. |
| Duración mínima | 9 s | Tiempo mínimo cubierto por la ventana de tendencia. |
| Variación significativa | 4,5 dB | Cambio mínimo para evidenciar acercamiento o alejamiento. |
| Variación estable | 2,0 dB | Cambio considerado estable. |
| Histéresis | 1,5 dB | Margen adicional para abandonar una tendencia. |
| Confirmaciones de tendencia | 2 evaluaciones | Confirmaciones necesarias para `APPROACHING` o `MOVING AWAY`. |
| Confirmaciones estable | 2 evaluaciones | Confirmaciones necesarias para `STABLE`. |
| Expiración de contacto | 15 s | Tiempo sin nueva observación antes de eliminarlo. |
| Umbral `NEAR` | -76 dBm | Banda visual relativa, no distancia. |
| Umbral `MEDIUM` | -88 dBm | Banda visual relativa, no distancia. |

Los valores de dBm dependen del teléfono, del dispositivo emisor, de la
orientación, de obstáculos y de interferencias. No son una calibración
universal.

### 6.2 Suavizado

Para cada evaluación se calcula:

```text
smoothed = previousSmoothed + alpha * (rawRssi - previousSmoothed)
```

Con `alpha = 0,35`, el valor nuevo tiene una influencia moderada y no domina
por completo a la historia anterior.

Cada punto guardado en el historial contiene:

- timestamp monotónico;
- timestamp de calendario;
- RSSI bruto;
- RSSI suavizado;
- variación respecto al punto anterior.

### 6.3 Estados de tendencia

Los estados son:

- `INSUFFICIENT_DATA`: todavía no hay suficiente historia; en la interfaz se
  presenta como espera de datos.
- `APPROACHING`: el RSSI suavizado ha aumentado lo suficiente.
- `STABLE`: la variación permanece por debajo del umbral significativo.
- `MOVING_AWAY`: el RSSI suavizado ha disminuido lo suficiente.

El tracker usa una cantidad de confirmaciones y una histéresis para evitar que
una lectura aislada provoque cambios continuos entre estados.

### 6.4 Bandas de proximidad

La banda se obtiene del RSSI suavizado:

- `NEAR` si es igual o superior a `-76 dBm`.
- `MEDIUM` si es igual o superior a `-88 dBm`, pero inferior a `-76 dBm`.
- `FAR` si hay historial, pero el RSSI está por debajo de `-88 dBm`.
- `UNKNOWN` si todavía no hay un valor procesado utilizable.

Estas etiquetas solo ordenan la lectura relativa de la señal. No representan
metros.

## 7. Pantalla de escaneo y seguimiento

La pantalla completa tiene dos zonas principales:

1. A la izquierda, el `GRID` de densidad.
2. A la derecha, el panel de estado y `CONTACT LIST // ALL NODES`.

Cada fila de contacto puede mostrar:

- nombre anunciado o etiqueta `UNKNOWN`;
- categoría inferida entre `[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` y
  `[COMPUTER]` cuando hay evidencia suficiente;
- origen `A56` o `WATCH 2 PROBE`;
- RSSI bruto;
- RSSI suavizado;
- banda relativa;
- tendencia.

Al pulsar una fila se selecciona el objetivo. Al pulsar de nuevo la misma fila
o `STOP TRACKING`, se elimina solo la selección. `CLEAR CONTACTS` limpia todos
los contactos de la sesión.

El panel de objetivo muestra además:

- identificador técnico;
- historial reciente;
- variación;
- explicación textual generada por el tracker;
- estado de tendencia;
- banda de proximidad.

La selección se pinta en ámbar en el GRID. Los demás contactos siguen
escaneándose y evaluándose.

## 8. Clasificación orientativa de dispositivos

`PrsDeviceClassifier` intenta obtener una categoría en este orden general:

1. nombre anunciado;
2. clase Bluetooth de Android;
3. campo BLE Appearance dentro de los datos anunciados.

El resultado es una ayuda para leer la lista rápidamente. No es una
identificación definitiva del fabricante o del modelo y no muestra un margen
de confianza.

## 9. GRID y nubes de densidad

### 9.1 Retícula

`PrsDensityGrid` dibuja:

- retícula de 6 columnas por 4 filas;
- líneas de exploración horizontales suaves;
- cuatro anillos concéntricos al 25 %, 50 %, 75 % y 100 % del radio máximo;
- esquinas técnicas;
- marca central del A56;
- emblema de la Hermandad como marca de agua opcional.

El radio máximo se calcula como el 46 % de la dimensión menor del GRID.

### 9.2 Modelo de nube

La implementación actual no coloca contactos en ángulos artificiales. Cada
contacto se muestra como una nube anular centrada en el nodo receptor.

El estimador por defecto usa estos centros y extensiones relativas:

| Banda | Centro radial | Extensión radial |
|---|---:|---:|
| `NEAR` | 0,27 | 0,15 |
| `MEDIUM` | 0,50 | 0,20 |
| `FAR` | 0,75 | 0,17 |
| `UNKNOWN` | 0,66 | 0,28 |

La confianza visual depende del número de puntos históricos y se limita al
intervalo `0,12`–`0,72`. El objetivo seleccionado usa ámbar y mayor énfasis.

La nube cubre todo el azimut (`azimuthCoverage = 1`). Esto es deliberado:
un solo receptor BLE puede medir que una señal está presente y comparar su
intensidad, pero no puede obtener por sí mismo un rumbo fiable.

### 9.3 Subgrid del Watch 2

En `SCAN + PROBE`, el Watch 2 puede aparecer como un nodo en un subgrid. El
subgrid:

- se calcula a partir de la diferencia GPS entre A56 y Watch 2;
- se limita a una superficie de 200 m de ancho total;
- muestra la posición relativa de los receptores, no la posición de un BLE;
- marca el estado `STALE` si la ubicación supera 60 segundos;
- no se dibuja si falta un fix válido del teléfono o del reloj.

Las señales escuchadas por el Watch 2 se dibujan alrededor del nodo del reloj,
no alrededor del centro del A56. Así se evita atribuir al teléfono una señal
que solo ha medido el reloj.

## 10. DEVICES y reglas persistentes

`DEVICES` tiene tres pantallas:

### IDENTIFY DEVICE

Hace un escaneo BLE en vivo y muestra la información disponible. El usuario
puede guardar un contacto observado:

- una dirección BLE se guarda como regla principal cuando está disponible;
- el nombre observado se conserva como nombre amigable;
- si la dirección es privada o rotatoria, se puede guardar el nombre BLE como
  alternativa.

También se admite entrada manual:

- MAC normalizada, por ejemplo `AA:BB:CC:DD:EE:FF`;
- nombre BLE exacto.

### SAVED DEVICES

Cada regla tiene uno de estos estados:

- `ENABLED`: las observaciones coincidentes se omiten antes de entrar en el
  tracker de `LOCAL SCAN` y `SCAN + PROBE`.
- `DISABLED`: la regla sigue guardada, pero las observaciones vuelven a ser
  visibles.
- `REMOVE`: elimina la regla.

La información se guarda en `SharedPreferences` bajo el nombre lógico
`prs_devices`. La aplicación migra una vez las antiguas reglas de omisión al
formato actual.

Una regla por nombre puede coincidir con varios dispositivos físicos. Por eso
la dirección técnica es preferible cuando Android la ofrece.

### MAC ADDRESS GUIDE

Explica cómo encender el dispositivo, activar Bluetooth, esperar un anuncio,
verificar nombre/RSSI/identificador y guardar la regla. También advierte sobre
direcciones privadas y sobre la necesidad de desactivar una regla antes de
seleccionar el dispositivo como objetivo de `INDIVIDUAL TRACKER`.

## 11. INDIVIDUAL TRACKER y TERRAIN

Esta función es experimental y solo depende de P.R.S. y TERRAIN; TERRAIN no
depende de P.R.S.

Flujo:

1. En `TARGET`, el usuario elige primero un campo TERRAIN.
2. Después elige un único contacto detectado por `LOCAL SCAN`.
3. En `TRACKER`, se muestra el mapa seleccionado junto al GRID P.R.S.
4. El GRID queda centrado en la posición GPS actual del A56.
5. Solo se muestra la señal del contacto elegido.

La función no calcula la coordenada del objetivo, ni su rumbo, ni metros, ni
una conversión RSSI → distancia. El parámetro opcional
`PrsMovementContext` existe como punto de extensión, pero el estimador actual
no usa movimiento, orientación ni desplazamiento.

## 12. Integración con Watch 2 PROBE

### 12.1 Qué es PROBE

El módulo Wear OS `:probe` es un nodo sensor independiente y sin interfaz de
telemetría P.R.S. propia. Escanea BLE y obtiene ubicación/estado en el reloj.
La app móvil sigue siendo el lugar donde se combinan y muestran los contactos.

### 12.2 Flujo de control

Cuando se abre `SCAN + PROBE`:

1. El teléfono busca nodos Wear OS conectados.
2. Elige el nodo conectado más cercano que no sea el nodo local.
3. Genera una sesión como `PRS-<timestamp>`.
4. Registra ese nodo y sesión como combinación esperada.
5. Envía `START_RECON` por `ProbeProtocol.CONTROL_PATH`.
6. Al abandonar la pantalla, envía `STOP`.

Si no hay nodo conectado, se muestra `NO PROBE NODE CONNECTED`. También se
puede reintentar desde la interfaz.

### 12.3 Contrato de mensajes

El protocolo compartido tiene versión `1` y usa rutas Wear OS:

- `/suri/probe/control`
- `/suri/probe/telemetry/location`
- `/suri/probe/telemetry/ble`
- `/suri/probe/telemetry/status`

Los tipos de mensaje son:

- `Control`: orden, `phoneNodeId` y `sessionId`.
- `LocationSample`: nodo, sesión, secuencia, timestamp, latitud, longitud,
  precisión, proveedor y batería.
- `BleSample`: nodo, sesión, secuencia, timestamp, identificador temporal,
  identificador técnico, nombre, RSSI, advertising y tipo de dispositivo.
- `Status`: nodo, sesión, estado, timestamp, batería y mensaje.

Los campos codificados de texto usan Base64 URL-safe dentro de un mensaje
delimitado por `|`. El protocolo valida versión, estructura, longitudes,
rangos de coordenadas, RSSI, batería, secuencias, timestamps plausibles y
hexadecimal de advertising.

### 12.4 Recepción y filtrado

`ProbeDataLayerService` solo procesa rutas de telemetría conocidas.
`ProbeTelemetryStore` acepta la muestra únicamente si coinciden:

- el `sourceNodeId` esperado;
- la sesión P.R.S. activa.

Las observaciones válidas del reloj se convierten al mismo modelo
`BleObservation` que usa el A56, cambiando únicamente el origen a
`PROBE_WATCH_2`. Desde ese punto pasan por las mismas reglas de dispositivos,
tracker, historial, tendencias y GRID.

La telemetría es en vivo y se conserva en memoria. No se almacena un archivo
de observaciones del P.R.S. en el Watch 2 ni se reproduce una sesión vieja en
la pantalla.

## 13. Ubicación relativa de los nodos

La ubicación del A56 se obtiene mediante `TerrainLocation`, que puede usar
proveedores fused, GPS y red si están disponibles. El sistema:

- exige permiso de ubicación;
- rechaza coordenadas no finitas;
- rechaza fixes con más de 60 segundos;
- mantiene el proveedor reciente de mejor precisión para evitar saltos
  innecesarios;
- actualiza la ubicación mientras `SCAN + PROBE` está abierto.

Para dibujar el Watch 2 dentro del GRID, el teléfono calcula una diferencia
aproximada en metros usando latitud/longitud y la latitud media. Esta operación
solo sitúa los dos receptores en una superficie visual limitada. No convierte
la posición del reloj en posición de ningún contacto BLE.

## 14. Ciclo de vida de la pantalla

`PrsTrackingScreen` crea durante la sesión:

- `BleScanner`;
- `ProbeLink`;
- `TerrainLocation`;
- `PrsContactTracker`;
- `PrsDeviceRegistry`.

Al entrar:

1. solicita permisos si faltan;
2. inicia el escáner local cuando el modo lo requiere;
3. inicia ubicación del teléfono para el modo PROBE;
4. registra oyentes del `ProbeTelemetryStore`;
5. inicia el bucle de evaluación temporal cada 3 segundos.

Al salir:

- detiene el escáner BLE;
- detiene la ubicación;
- quita los listeners del PROBE;
- envía `STOP` cuando corresponde;
- libera la sesión del escáner;
- limpia el tracker de esa pantalla.

## 15. Permisos y manifest

La aplicación móvil declara:

- `BLUETOOTH_SCAN`;
- `BLUETOOTH_CONNECT`;
- `ACCESS_FINE_LOCATION`;
- `ACCESS_COARSE_LOCATION`;
- `CAMERA`, usada por MORSE y no por P.R.S.

P.R.S. necesita Bluetooth y ubicación en el A56 porque Android protege el
escaneo BLE con esos permisos. La ubicación no convierte el BLE en un sistema
de dirección; solo se usa para la posición del A56 y, opcionalmente, la
posición relativa del nodo Watch 2.

## 16. Persistencia, privacidad y seguridad

- Los contactos, el RSSI y las nubes de la sesión P.R.S. viven en memoria.
- Las reglas de `DEVICES` sí se guardan localmente en `SharedPreferences`.
- La telemetría recibida del PROBE no se persiste en el flujo actual.
- Las direcciones BLE pueden ser identificadores de dispositivos personales.
- Las direcciones privadas/rotatorias pueden impedir la correlación entre
  lecturas.
- El transporte Wear OS usa Data Layer con coincidencia de nodo y sesión.
- La autenticación criptográfica propia del canal permanece fuera del alcance
  de la implementación privada actual.
- La aplicación móvil tiene desactivado el backup general en su manifest.

P.R.S. no debe presentarse como una herramienta de seguridad, vigilancia o
confirmación de ocupación.

## 17. Limitaciones explícitas

La implementación actual no ofrece:

- distancia exacta en metros;
- dirección física o bearing BLE;
- coordenadas X/Y de un contacto;
- triangulación entre varios receptores para localizar un contacto;
- Wi-Fi RTT, UWB o aprendizaje automático;
- detección fiable de personas;
- identificación definitiva del modelo del dispositivo;
- separación fiable entre una señal del pasillo y otra de una habitación;
- descubrimiento de todos los dispositivos Bluetooth clásicos;
- detección de dispositivos que no estén anunciando BLE;
- un modelo calibrado para todos los teléfonos, relojes y accesorios.

`APPROACHING`, `STABLE` y `MOVING AWAY` describen la evolución de la señal.
No prueban que un objeto o una persona se haya movido físicamente.

## 18. Puntos de extensión para una reedición

El código deja dos puntos de extensión principales:

1. `PrsTuningConfig` / `PrsTuning.DEFAULT`: permite cambiar la cadencia, el
   suavizado, el historial, las confirmaciones y las bandas relativas.
2. `PrsDensityEstimator`: permite cambiar la forma de convertir la señal en
   una nube visual sin cambiar el escáner ni la lista de contactos.

`PrsMovementContext` reserva datos de desplazamiento, rumbo y velocidad para
una posible investigación futura. El estimador actual los ignora a propósito.

Cualquier reedición debe conservar estas reglas de diseño:

- no mezclar medición con inferencia;
- no convertir automáticamente RSSI en metros;
- no dibujar un rumbo BLE que el hardware no haya medido;
- documentar si un dato viene del A56, del Watch 2 o de una inferencia;
- mantener la posibilidad de distinguir el nodo receptor de cada contacto;
- conservar el filtrado por dispositivo guardado antes del tracker;
- no volver a introducir las posiciones sintéticas del P.R.S. antiguo.

## 19. Validación del corte

En este corte del proyecto se han ejecutado correctamente:

- `:app:testFullDebugUnitTest`;
- `:app:testPrsOnlyDebugUnitTest`;
- `:app:lintFullDebug`;
- `:app:lintPrsOnlyDebug`;
- `:app:assembleFullDebug`;
- `:app:assemblePrsOnlyDebug`.

El resultado fue `BUILD SUCCESSFUL`. La APK completa se instaló también en el
Samsung Galaxy A56 conectado y la aplicación llegó a `STATUS` correctamente.

Las pruebas unitarias específicas existentes incluyen cobertura de:

- `PrsContactTracker`;
- clasificación de dispositivos P.R.S.;
- protocolo compartido del PROBE;
- otras piezas generales de PIP-SuriOS.

La calibración física universal de RSSI no se considera cerrada ni forma parte
de la reedición por defecto.

## 20. Contexto corto para otro chatbot

Usa el siguiente bloque como instrucciones iniciales al combinar este
documento con una nueva idea:

```text
P.R.S. v3.0 en PIP-SuriOS es un escáner BLE experimental para el Samsung A56.
La aplicación actual está en versión 2.8. El flujo es BLE SCAN -> CONTACTS ->
RSSI RAW -> HISTORIAL -> SUAVIZADO -> TENDENCIA -> BANDA RELATIVA -> GRID.

El A56 puede trabajar solo (LOCAL SCAN) o combinarse con el Watch 2 PROBE
(SCAN + PROBE). El PROBE envía observaciones BLE y ubicación por Wear OS Data
Layer. La app valida el nodo y la sesión esperados y después procesa las
observaciones remotas con el mismo tracker que las del A56.

El tracker evalúa cada 3 s, usa suavizado exponencial alpha 0,35, guarda 8
muestras, exige 4 muestras y 9 s para inferir tendencia, usa 4,5 dB como
variación significativa, 2 confirmaciones y expira contactos tras 15 s.
NEAR/MEDIUM/FAR son bandas relativas con umbrales -76/-88 dBm; no son metros.

El GRID muestra nubes anulares de azimut completo centradas en el nodo que
escuchó la señal. No existen bearing, coordenada BLE, RSSI->metros ni posición
real del objetivo. DEVICES guarda reglas por dirección o nombre BLE. INDIVIDUAL
TRACKER es experimental, usa solo LOCAL SCAN y combina un contacto con TERRAIN.

Al proponer cambios, conserva la separación entre datos medidos, procesados e
inferidos. Indica qué archivos cambian, qué datos nuevos son realmente medidos
y qué partes son inferencias. No reintroduzcas la arquitectura histórica de
P.R.S. v2.0 ni posiciones sintéticas sin una decisión explícita.
```

## 21. Fuentes internas

- `docs/PRS_v3.0.md`: descripción canónica de la arquitectura P.R.S. activa.
- `app/src/main/java/com/suri/pipsurios/prs/`: modelos, escáner, tracker,
  tuning, reglas y enlace PROBE.
- `app/src/main/java/com/suri/pipsurios/ui/screens/`: pantallas y GRID.
- `watch/probeprotocol/src/main/java/com/suri/probeprotocol/ProbeProtocol.kt`:
  contrato de mensajes móvil–reloj.

Los documentos `docs/PRS_v2.0.md` y `docs/PRS_REMOTE_PROBE_SATURDAY.md` son
históricos y no deben utilizarse como descripción de la implementación activa.
