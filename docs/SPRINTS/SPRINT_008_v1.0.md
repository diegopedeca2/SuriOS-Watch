# Sprint 008 v1.0 — TOOLS, GEIGER COUNTER y SONAR

---

document: SPRINT_008
project: PIP-SuriOS
version: 1.0
status: Cerrado
start_date: 2026-08-10
end_date: 2026-08-10
visible_version: PIP-SuriOS v1.7

---

## Objetivo

Sprint 008 incorporó el módulo TOOLS y sus primeras herramientas funcionales: GEIGER COUNTER y SONAR. Ambas conservan la estética terminal, la orientación horizontal y la navegación Compose mediante estado, sin introducir Navigation Compose.

## TOOLS

HOME OPERATION incorpora el acceso TOOLS. Tras su pantalla de carga de 1500 ms, el menú permite abrir GEIGER COUNTER y SONAR y regresar al HOME mediante `< BACK`.

## GEIGER COUNTER

GEIGER COUNTER es una herramienta exclusivamente inmersiva. No representa mediciones científicas ni utiliza sensores físicos.

Arquitectura:

- `GeigerCounterScreen`: composición visual y ciclo de vida.
- `GeigerEngine`: evolución progresiva del nivel y variación natural.
- `NeedleAnimation`: movimiento suave del medidor analógico.
- `ClickScheduler`: temporización de los clics según el estado.
- `VolumeKeyController`: interceptación exclusiva de `KEYCODE_VOLUME_UP` mientras la pantalla está activa.

El nivel aumenta progresivamente al mantener VOLUME UP y regresa lentamente a BACKGROUND al soltarlo. Los estados son BACKGROUND, LOW, MODERATE, HIGH y CRITICAL. El audio utiliza un sample breve y libera sus recursos al abandonar la pantalla.

## SONAR

SONAR es una herramienta experimental que detecta exclusivamente señales Bluetooth Low Energy que Android permite descubrir. No localiza personas, no determina dirección física y no calcula distancia exacta.

Arquitectura:

- interfaz declarativa en Compose;
- `BleScanner` basado en `BluetoothLeScanner` oficial de Android;
- `ContactTracker` para creación, mantenimiento y expiración;
- `RssiFilter` y `ProximityClassifier` para suavizado y clasificación;
- modelos de contacto y snapshot separados;
- `SonarTuning` como punto central de tiempos, umbrales y radios;
- `SonarSweepAudio` para pulso y avisos de contacto con un único stream activo.

Cada contacto mantiene un identificador temporal, RSSI actual, RSSI suavizado, `firstSeen`, `lastSeen`, estado y categoría. No se muestran ni almacenan permanentemente nombres o direcciones MAC.

### Baseline y CALIBRATE

CALIBRATE observa el entorno durante un periodo configurable y registra los contactos presentes como BACKGROUND. Después de crear el baseline, los contactos no incluidos se clasifican como NEW. Los contactos inactivos se eliminan tras el tiempo de expiración centralizado.

### Proximidad y radar

La posición radial utiliza exactamente las categorías derivadas del RSSI suavizado:

- VERY CLOSE
- CLOSE
- MEDIUM
- FAR

El radar muestra `SURI-14` en el centro, anillos etiquetados y un ángulo visual estable por contacto. Ese ángulo no representa dirección física. No se muestran metros, centímetros ni estimaciones numéricas.

### Audio

- Pulso electrónico original al inicio de cada barrido.
- Pitido breve y discreto cuando el barrido atraviesa un contacto BACKGROUND.
- Pitido más perceptible para un contacto NEW.
- Máximo de un aviso por contacto y pasada.
- Prioridad acústica de NEW cuando coinciden eventos.
- Un solo stream activo, sin acumulación ni reproducción en segundo plano.
- Liberación completa al abandonar SONAR.

## Permisos

Se incorporaron `BLUETOOTH_SCAN` con `neverForLocation` y `BLUETOOTH_CONNECT`. La interfaz informa cuando faltan permisos o Bluetooth está desactivado, sin provocar un fallo.

## Identidad visible

Todas las firmas visibles de la aplicación se consolidaron como `PIP-SuriOS v1.7`. No se modificó el `versionName` técnico de Gradle.

## Validación

Se completaron correctamente:

- `:app:assembleDebug`;
- compilación incremental;
- `:app:lintAnalyzeDebug`;
- 19 pruebas unitarias existentes, sin fallos;
- `git diff --check`;
- búsqueda completa de firmas visibles anteriores.

La validación manual completa fue superada en Samsung Galaxy A56. Pixel 8 Emulator se utilizó para las validaciones aplicables de interfaz, navegación, ciclo de vida y audio. También se comprobaron CURRENT GEAR, STATUS, COMPLEMENTS, MORSE TERMINAL, MAP y COMMS sin regresiones.

## Limitaciones conocidas

- SONAR estima proximidad aproximada a partir de RSSI BLE.
- Obstáculos, orientación, potencia de emisión y condiciones ambientales pueden cambiar la categoría.
- El ángulo del radar es sólo una representación visual estable.
- SONAR no detecta personas ni garantiza que todos los dispositivos cercanos sean descubribles.
- No existe Wi-Fi scanning, Wi-Fi RTT, UWB, GPS, sensores físicos ni hardware externo.
- No existe persistencia de contactos ni baseline entre sesiones.
- GEIGER COUNTER no mide radiación real.

## Cierre

Commit técnico:

`353edbf212e810e29583db5d91400eb3dfac9ec9`

Sprint 008 queda cerrado. No existe ningún Sprint activo. Sprint 009 no se ha iniciado.
