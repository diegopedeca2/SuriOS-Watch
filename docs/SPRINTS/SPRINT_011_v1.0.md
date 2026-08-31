# Sprint 011 — SONAR-TESTING y MAP TERRAIN

## Estado

- Apertura: 2026-08-12.
- Cierre: 2026-08-26.
- Estado: cerrado.
- Versión consolidada: PIP-SuriOS v2.1.
- Commit técnico: `92f4ccda44fc2024caa2124eb854b702d3bedfab`.
- Sprint activo posterior: ninguno.
- Sprint 012: no iniciado.

## Alcance

Sprint 011 se ejecutó en dos bloques funcionales aislados y añadió posteriormente los refinamientos de viewport, PAN 2D e identidad visible v2.1.

## PARTE 1 — SONAR-TESTING

SONAR-TESTING es una herramienta experimental independiente destinada a capturar datos de calibración BLE sin trasladar resultados al SONAR estable ni modificar `SonarTuning`.

Reutiliza el escaneo BLE, `ContactTracker`, el suavizado, las categorías, la expiración y los estados NEW/BACKGROUND existentes. La extensión compartida se limitó a exponer contactos expirados para registrar pérdidas reales sin alterar umbrales.

El flujo operativo consolidado es:

`SET TEST → IDENTIFY TARGET → PLACE TARGET / RUN SAMPLE → RESULT`

Incluye:

- targets FLIP 6, HONOR 8, MOTOROLA, WATCH 2 y CHECHU;
- posición física manual NORTH/SOUTH/EAST/WEST y notas;
- identificación BLE guiada y binding exclusivamente temporal;
- muestras estáticas de aproximadamente 30 segundos;
- CAL-MOVEMENT con START/STOP manual;
- RSSI RAW recibido antes del suavizado y RSSI SMOOTHED mediante el algoritmo estable;
- categoría SONAR, estado, pérdidas, recuperaciones y contador de observaciones;
- sesiones persistentes legibles `CAL-###`;
- NEXT SAMPLE, RE-IDENTIFY TARGET y RESET TEST;
- persistencia privada separada y exportación CSV UTF-8 mediante Android Sharesheet/FileProvider.

No se almacenan permanentemente MAC, nombres BLE ni identificadores de hardware. Los CSV y sesiones anteriores permanecen compatibles. Los resultados obtenidos no constituyen una calibración empírica de `SonarTuning`.

## PARTE 2 — MAP TERRAIN

MAP TERRAIN integra NAVY7 como primer campo offline extensible por `mapId`. El mapa base es un MBTiles raster PNG generado desde QGIS, leído directamente mediante SQLite y empaquetado sin compresión. No se añadió proveedor cartográfico ni mapa base online.

Características consolidadas:

- viewport fijo y recortado a pantalla completa;
- cámara única `mapCenterGeo + zoom + heading`;
- transformación común world/screen para teselas, GPS, RESPawns y RAD ZONES;
- pan 2D corregido con transformación vectorial reversible y una sola actualización de centro;
- pinch-to-zoom anclado al centro del gesto;
- Z16–Z19 nativos y overzoom controlado hasta Z20;
- heading-up mediante `TYPE_ROTATION_VECTOR`, suavizado circular y tratamiento 359°/0°;
- GPS activo únicamente durante MAP TERRAIN, sin historial ni recentrado automático;
- múltiples RESPawns y RAD ZONES persistentes por mapa en coordenadas geográficas;
- DELETE/CLEAR/EMPTY MAP siempre protegidos por CONFIRM;
- distancia a RAD ZONE calculada respecto al borde más próximo del polígono;
- reacción Geiger progresiva dentro del umbral aproximado de 10 m, con filtro de precisión, suavizado e histéresis;
- reutilización de `ClickScheduler`, sin segundo motor Geiger simultáneo.

El MBTiles NAVY7 permanece inalterado respecto al artefacto auditado. MAP OPERATION conserva sin cambios funcionales el flujo CivTAK y fallback Google Maps.

## Refinamiento — viewport y PAN 2D

El render dejó de tratar el mapa como una lámina transformada. El Canvas constituye una ventana fija; las transformaciones se aplican únicamente al mundo cartográfico bajo clipping. La cobertura utiliza la diagonal del viewport para evitar esquinas vacías durante la rotación.

El defecto de PAN vertical diagonal procedía del recorte independiente de los componentes del centro al alcanzar los límites de cobertura. La corrección convierte el vector completo entre pantalla y mundo, mantiene magnitud y ortogonalidad, y limita ambos ejes mediante un único factor escalar. No existe `panOffset` residual.

La firma visible consolidada es `PIP-SuriOS v2.1`. `app/src/main` contiene cero firmas visibles v2.0. El `versionName` técnico de Gradle permanece fuera de este versionado visual y no fue modificado por el Sprint.

## Validación completada

- `:app:assembleDebug` correcto.
- compilación incremental correcta.
- `:app:lintAnalyzeDebug` correcto.
- suite unitaria completa sin fallos.
- pruebas instrumentadas correctas en Samsung Galaxy A56 y Pixel 8 Emulator.
- `git diff --check` correcto.
- validación manual final disponible aceptada por el propietario.
- SONAR estable sin regresiones funcionales.
- `SonarTuning` sin cambios.
- MAP OPERATION, RADS, DATA, STORAGE y navegación principal sin regresiones funcionales.
- NAVY7 offline y entrada SONAR-TESTING comprobados.

## FIELD VALIDATION posterior

El Sprint se cierra técnicamente. Permanecen como validación física exterior posterior del sábado, sin constituir desarrollo pendiente:

- calibración empírica de SONAR-TESTING en entorno controlado;
- alineación GPS real en NAVY7;
- precisión exterior del heading e influencia magnética del equipamiento/placa;
- ergonomía, pinch y pan durante uso físico;
- RAD ZONES y audio Geiger alrededor del umbral sobre el terreno;
- estabilidad y consumo durante una partida.

No se consideran calibrados los umbrales SONAR ni se presentan los resultados actuales como cambios recomendados para `SonarTuning`.

## Cierre

Sprint 011 queda cerrado y PIP-SuriOS v2.1 consolidado. No existe ningún Sprint activo y Sprint 012 no se ha iniciado.
