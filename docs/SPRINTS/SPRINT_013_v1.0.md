# Sprint 013 - P.R.S. v2.0, REMOTE PROBE y P.R.S. TESTING

## Estado

- Apertura: 2026-08-27.
- Cierre: 2026-08-27.
- Estado: cerrado.
- Versión móvil consolidada: PIP-SuriOS v2.2 (`versionCode 2`).
- Versión de la watchface: PIW-SuriOS v2.0 (`versionCode 2`).
- App independiente del reloj: P.R.S. REMOTE PROBE (`versionName 0.1`).
- Sprint activo posterior: ninguno.

## Objetivo

Preparar P.R.S. para una prueba funcional de campo con dos puntos físicos independientes de detección BLE:

- Galaxy A56 como nodo `OPERATOR`.
- Xiaomi Watch 2 como nodo `PROBE` remoto.

El alcance no calcula coordenadas, metros ni triangulación. RSSI se conserva como dato bruto de radio para comparación y calibración posterior.

## Alcance implementado

### P.R.S. v2.0 en el A56

- Composición visual simplificada con grid 2D, retícula, anillos y nube de intensidad.
- Panel lateral con controles, estado y contadores `A56`, `WATCH`, `TOTAL` y `MATCHED`.
- `TOTAL` representa la unión de identificadores activos; un contacto compartido cuenta una sola vez.
- Punto azul para distinguir el Watch 2 como nodo de enlace. No representa una coordenada.
- P.R.S. v2.0 no ofrece exportación CSV; sus observaciones se guardan internamente durante la sesión.
- Gateway local del A56 mediante TCP `28771`, descubrimiento NSD y respaldo UDP `28772`.

### P.R.S. REMOTE PROBE en Xiaomi Watch 2

- Módulo Wear OS independiente de la watchface `watchface`.
- Icono identificable: letra `P` verde sobre fondo negro.
- Escaneo BLE con identificador observado, RSSI, timestamp, nombre, advertising data y tipo de dispositivo cuando Android los proporciona.
- Servicio foreground para mantener el escaneo y el envío mientras el reloj está desplegado.
- Envío por Wi-Fi local al A56; no depende de LTE ni de Internet.
- Copia local NDJSON en el Watch para recuperación técnica si el enlace se interrumpe.

### P.R.S. TESTING

- Modo `A56 ONLY / WITHOUT WATCH` para línea base y pruebas negativas.
- Modo `A56 + WATCH 2 / DUAL NODE` para lectura doble.
- Registro de identificador, RSSI, timestamp, nodo y metadatos disponibles.
- Asociación de muestras del Watch por identificador observado y ventana temporal; no se inventa una identidad permanente.
- Comparación experimental `NEAR OPERATOR`, `BETWEEN`, `NEAR PROBE` y `UNCERTAIN`.
- Conservación de `probe_session_id`, estado del enlace, RSSI remoto y número de muestras en el CSV de Testing.
- Exportación CSV permanece disponible únicamente en `P.R.S. TESTING`.

### Navegación y documentación

- `BACK` desde `P.R.S. TESTING` vuelve a `PROXIMITY RADIO SCANNER`; un segundo `BACK` vuelve a `TOOLS`.
- Guía integrada en `TOOLS > PROXIMITY RADIO SCANNER > OPERATION GUIDE`.
- Manual operativo del Watch 2 y protocolo de prueba del sábado incluidos en `docs/`.
- `ACTIVE_SPRINT`, historial, changelog, auditoría y guía de usuario alineados con v2.2.

## Limitaciones aceptadas

- Las direcciones BLE privadas o rotatorias pueden impedir correlacionar el mismo dispositivo entre nodos.
- RSSI depende de potencia, orientación, obstáculos, interferencias y posición; no equivale a distancia.
- El gateway y el servicio de escaneo están ligados al ciclo de vida de las pantallas/aplicaciones experimentales.
- El comportamiento con pantalla apagada o Ambient Mode depende de las restricciones de Wear OS y debe repetirse en campo.
- El transporte HTTP local no tiene cifrado ni autenticación. Sólo es aceptable para una red controlada de prueba; queda como riesgo de seguridad documentado.
- La validación física de campo y la calibración de umbrales quedan como actividad posterior al cierre.

## Validación

Comandos ejecutados correctamente:

```text
gradlew test lint assembleDebug :app:connectedDebugAndroidTest
git diff --check
```

Resultado:

- Tests unitarios de `:app` correctos; `:remoteprobe` y `:watchface` no contienen tests unitarios.
- Lint de `app`, `remoteprobe` y `watchface` correcto, sin errores bloqueantes.
- APK debug de los tres módulos generada correctamente.
- Tests instrumentados ejecutados en Samsung Galaxy A56 y Pixel 8 Emulator.
- Xiaomi Watch 2 visible por ADB; se omite de los tests instrumentados de `:app` por API 34 inferior al `minSdk 35` móvil.
- APK móvil actualizada previamente en A56 y emulador.
- Conexiones ADB disponibles durante la validación: Galaxy A56, Xiaomi Watch 2 y emulador.

## Cierre

Sprint 013 queda cerrado técnica, funcional y documentalmente. PIP-SuriOS v2.2 queda preparada para una prueba de campo controlada con lectura doble. La auditoría de seguridad posterior y la calibración empírica de umbrales no reabren este Sprint y se registran como trabajo posterior.
