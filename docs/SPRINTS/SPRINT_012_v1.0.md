# Sprint 012 — Reorganización de HOMESCREEN y P.R.S.

## Estado

- Apertura: 2026-08-27.
- Cierre: 2026-08-27.
- Estado: cerrado.
- Versión consolidada: PIP-SuriOS v2.1.
- Sprint activo posterior: ninguno.

## Objetivo

Reorganizar el acceso operativo de HOMESCREEN, agrupar MAP y COMMS dentro de TOOLS y presentar el escáner de proximidad bajo la identidad P.R.S. sin alterar la lógica BLE existente.

## Alcance implementado

### HOMESCREEN

HOMESCREEN queda distribuido en dos columnas estables:

- izquierda: SET-UP, CURRENT GEAR, INVENTORY;
- derecha: STATUS, DATA, TOOLS.

MAP y COMMS dejan de ser accesos de primer nivel y pasan a estar disponibles desde TOOLS. Los retornos de sus pantallas de selección vuelven a TOOLS.

### TOOLS

TOOLS se muestra en orden alfabético:

1. COMMS
2. MAP
3. PROXIMITY RADIO SCANNER
4. RADS

### PROXIMITY RADIO SCANNER

Se añade la pantalla de carga propia y el menú `P.R.S.` con la estética, el pie de versión y el comportamiento de navegación del resto de módulos.

El menú contiene:

- `P.R.S. v1.0`: conserva la funcionalidad del escáner BLE de proximidad existente, incluidos radar, contactos, calibración y audio.
- `P.R.S. TESTING`: conserva el flujo experimental de identificación, calibración, muestras y exportación CSV.

El cambio de nombre es de presentación y navegación; los motores BLE, los modelos, los umbrales y la persistencia experimental no se han duplicado ni alterado.

## Versionado

- Todas las firmas visibles de PIP-SuriOS mantienen `PIP-SuriOS v2.1`.
- `app/build.gradle.kts` queda alineado con `versionName = "2.1"`.
- El versionado de SuriOS Watch permanece independiente.

## Auditoría y validación

- `:app:assembleDebug` correcto.
- `:app:testDebugUnitTest` correcto.
- `:app:lintDebug` correcto.
- `git diff --check` correcto.
- Referencias visibles obsoletas `SONAR`, `SONAR TESTING` y `SONAR-TESTING` retiradas de la interfaz móvil y de la guía vigente.
- Navegación HOMESCREEN → TOOLS → PROXIMITY RADIO SCANNER comprobada.
- Rutas TOOLS → MAP y TOOLS → COMMS comprobadas.
- APK debug instalado y arranque comprobado en Samsung Galaxy A56 y Pixel 8 Emulator cuando ambos dispositivos estuvieron conectados por ADB.

## Limitaciones conocidas

- P.R.S. v1.0 continúa siendo una estimación de proximidad basada en RSSI BLE; no proporciona distancia ni dirección física.
- P.R.S. TESTING sigue siendo experimental y sus resultados no modifican automáticamente los umbrales de P.R.S. v1.0.
- La calibración y el estado de los escáneres se mantienen en memoria durante la sesión, según el comportamiento anterior.
- La validación exterior de BLE, GPS y condiciones de partida permanece fuera del alcance técnico de este Sprint.

## Cierre

Sprint 012 queda cerrado. PIP-SuriOS v2.1 permanece como versión móvil consolidada y no existe ningún Sprint activo posterior.
