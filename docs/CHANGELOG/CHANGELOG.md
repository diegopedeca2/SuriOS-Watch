## v2.2 — P.R.S. REMOTE PROBE — 2026-08-27

### Añadido

- Aplicación Wear OS independiente `P.R.S. REMOTE PROBE` para Xiaomi Watch 2.
- Escaneo BLE remoto con identificador observado, RSSI, timestamp, nombre, advertising data y tipo Bluetooth cuando están disponibles.
- Gateway local del A56 por Wi-Fi/NSD, sin LTE, coordenadas ni triangulación.
- Registro bruto en CSV en el A56 y copia NDJSON local en el Watch.
- Comparación experimental `NEAR OPERATOR`, `BETWEEN`, `NEAR PROBE` y `UNCERTAIN`.
- `P.R.S. TESTING` incorpora modos `A56 ONLY / WITHOUT WATCH` y `A56 + WATCH 2 / DUAL NODE`, con metadatos remotos en sus muestras.

### Modificado

- Firmas visibles de PIP-SuriOS actualizadas a `PIP-SuriOS v2.2`.
- `versionName` técnico actualizado a `2.2` y `versionCode` a `2`.
- Firma visible y metadata técnica de la watchface actualizadas a `PIW-SuriOS v2.0`.
- `BACK` desde `P.R.S. TESTING` devuelve al menú `P.R.S.` antes de volver a `TOOLS`.

### Validación

- Tests unitarios, compilación de `app`, `remoteprobe` y `watchface` correctos.
- Instalación y prueba de enlace verificadas en Galaxy A56, Xiaomi Watch 2 y emulador Pixel.

## P.R.S. v2.0 — cribado de presencia en puerta — 2026-08-27

### Añadido

- Nueva entrada `P.R.S. v2.0` dentro de `PROXIMITY RADIO SCANNER`.
- Encuesta en dos fases: `START REFERENCE` y `START DOOR SCAN`.
- Indicadores `NEW SIGNALS`, `STABLE SIGNALS`, `STRONGEST RSSI`, `SIGNAL INDEX` y resultados `NO DEVICE SIGNAL`, `POSSIBLE SIGNAL` o `PROBABLE SIGNAL`.

### Criterio de diseño

- La funcionalidad se presenta como evidencia de dispositivos BLE y no como detección de personas.
- Se eliminó `neverForLocation` de `BLUETOOTH_SCAN` porque esta versión compara observaciones de dos posiciones físicas.
- Se documentan las limitaciones de BLE, RSSI, paredes, aleatorización de direcciones y ausencia de sensores de ocupación en el A56.

## v2.1 — Sprint 012 — 2026-08-27

### Añadido

- Menú `PROXIMITY RADIO SCANNER` con pantalla de carga y título `P.R.S.`.
- Entradas `P.R.S. v1.0` y `P.R.S. TESTING`, conservando la funcionalidad BLE y experimental existente.

### Modificado

- TOOLS ordenado alfabéticamente: COMMS, MAP, PROXIMITY RADIO SCANNER y RADS.
- HOMESCREEN reorganizado en dos columnas: SET-UP, CURRENT GEAR, INVENTORY / STATUS, DATA, TOOLS.
- MAP y COMMS trasladados desde HOMESCREEN al menú TOOLS.
- Firmas visibles y `versionName` técnico alineados con PIP-SuriOS v2.1 (`2.1`).

### Validación

- `:app:assembleDebug`, `:app:testDebugUnitTest` y `:app:lintDebug` correctos.
- Navegación y arranque comprobados en Samsung Galaxy A56 y Pixel 8 Emulator.
## P.R.S. v2.0 — grid simple y pruebas de campo — 2026-08-27

### Modificado

- P.R.S. v2.0 cambia a dos pasadas operativas: `CLOSE SCAN` y `WIDE SCAN`.
- El primer barrido conserva solo lecturas fuertes para aproximar un alcance corto; el segundo compara todas las lecturas y resalta las nuevas.
- Se incorpora un grid 2D con nube de intensidad y puntos nuevos en ambar. El angulo es estable para evitar saltos visuales.
- Se simplifican los textos a `NO NEW POINTS`, `NEW POINTS FOUND` y `MORE NEW POINTS`.
- P.R.S. TESTING añade sitio, posiciones, entorno, colocacion, orientacion, notas y el modo experimental de doble lectura; la informacion se registra en el CSV de Testing.

### Validacion

- La composicion final queda despejada: solo se conserva el titulo `P.R.S.`, se retiran subtitulos, leyendas, guia de uso y el mensaje superior, y se muestran los contadores `CLOSE` y `NEW`.
- `:app:testDebugUnitTest`, `:app:lintAnalyzeDebug` y `:app:assembleDebug` correctos.
