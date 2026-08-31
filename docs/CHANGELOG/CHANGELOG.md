## Sprint 025 — INFORMATION, disclaimers y actualización de dependencias — 2026-08-31

### Añadido y modificado

- Se añade `INFORMATION` a la columna derecha de `HOMESCREEN`, debajo de
  `TOOLS`.
- `ACKNOWLEDGEMENTS` se mueve desde `SET-UP` a `INFORMATION` y se incorpora el
  reconocimiento a `CAINSHARK`.
- Se añade `DISCLAIMERS` con cláusulas formales sobre propiedad intelectual,
  inspiración visual, asistencia de Codex, desarrollo propio, uso no comercial
  y distribución privada.
- Se actualizan las dependencias estables de Kotlin, Compose, AndroidX Test,
  Lifecycle, Activity y Play Services Location; se mantienen AGP, Gradle,
  Wearable y JUnit cuando ya estaban en la versión objetivo.
- Se eliminan recursos bitmap de iconos incompatibles con `minSdk 34` y se
  conserva el icono adaptativo.

### Validación

- `test`, `lint` y `assemble`: correctos en todas las variantes y módulos.
- Lint de `app`: 0 incidencias.
- La validación física de `HOMESCREEN > INFORMATION`, sus dos subapartados y
  el retorno a `HOMESCREEN` queda completada en el Samsung A56.

## Cierre administrativo posterior al Sprint 024 — 2026-08-31

### Decisiones

- Se aprueban Consolas, la política documental en Git, la estrategia de pruebas,
  la política de `.idea`, `CHANGELOG.md`, la nomenclatura `Project` y la no
  incorporación de doble pulsación.
- `preview.png` y la Figura 4.1 quedan como material histórico.
- Se eliminan las copias `ORCA-TO-DO` y el perfil CIVILIAN activo, incluida su
  ruta huérfana en PIP-SuriOS; la documentación histórica no se reescribe.
- La actualización de dependencias, la documentación de licencias y la
  recolocación de agradecimientos quedan planificadas para Sprint 025.
- BL-010 queda cerrado: EDL v0.6 es la versión canónica y la copia histórica
  se ha renombrado para reflejarlo.

## Sprint 024 — Hardening SuriOS / PROBE companion — 2026-08-31

### Añadido y modificado

- PROBE queda definido como companion no standalone con `applicationId`
  `com.suri.pipsurios`; la watchface conserva su paquete independiente.
- La telemetría pasa a mensajes en vivo dirigidos al A56, con allowlist de nodo,
  sesión, validación semántica y sin persistencia en Watch 2.
- MORSE solicita `CAMERA`, MBTiles valida SHA-256/metadata y operaciones usan
  escritura atómica sin cambiar los conflictos por fecha.
- Se sincroniza `BACKLOG_v1.3` y se parametrizan las rutas del generador GIS.

### Validación

- Tests JVM full/prsOnly y `probeprotocol`: OK.
- Lint full/prsOnly y `:probe:lintVitalRelease`: OK sin errores.
- Ensamblados app, PROBE y watchfaces: OK.
- La comprobación física final A56–Watch 2 queda pendiente de reconexión ADB del
  A56.

## Sprint 023 — PIP-SuriOS v2.6 y reconstrucción HOME-style de NAVY7 — 2026-08-31

### Añadido y modificado

- Versión móvil actualizada a `2.6` con `versionCode=6`.
- Firmas visibles de PIP-SuriOS actualizadas a `PIP-SuriOS v2.6`.
- NAVY7 reconstruido desde QGIS con el modelo estándar de HOME: huella de
  5 km x 2,5 km, fondo opaco, edificios grises, carreteras azules y jerarquía
  de curvas con grosores fijados.
- Modelo reproducible en `tools/gis/build_navy7_home_style.py`, con proyecto
  independiente creado en GQUIS y ejecución basada en las coordenadas de centro.

### Validación y cierre

- MBTiles íntegro, 5242 teselas PNG 256x256 en zoom 16–19.
- El hash de NAVY7 se verificó en el repositorio, GQUIS y el Samsung A56.
- Sprint 023 cerrado con los riesgos residuales de auditoría documentados como
  backlog independiente.

## Sprint 022 — Agradecimientos en SET-UP — 2026-08-31

### Añadido

- Entrada `SET-UP > ACKNOWLEDGEMENTS` y pantalla de consulta de solo lectura.
- Seis agradecimientos iniciales con nombre y aportación concreta.
- Desplazamiento vertical para mantener la legibilidad de la lista.

### Validación y cierre

- La lista no es editable ni persistente; las ampliaciones se realizan mediante
  cambios de código.
- La regresión posterior a la incidencia del SPRINT 21 terminó con tests JVM,
  lint, ensamblados, 2/2 tests instrumentados en el Samsung A56 y comprobación
  manual de `HOME > SET-UP > ACKNOWLEDGEMENTS`.
- SPRINT 22 queda cerrado técnica, funcional y documentalmente.

## Sprint 021 — INDIVIDUAL TRACKER — 2026-08-30

### Añadido

- Menú experimental `TOOLS > PROXIMITY RADIO SCANNER > INDIVIDUAL TRACKER`,
  con los submenús `TARGET` y `TRACKER`.
- `TARGET` selecciona primero un campo TERRAIN y después un único dispositivo
  detectado por `LOCAL SCAN` del A56, reutilizando las reglas de `DEVICES`.
- `TRACKER` combina el mapa TERRAIN con el GRID P.R.S. centrado en el GPS del
  A56 y muestra exclusivamente la señal del objetivo seleccionado.
- `DEVICES > MAC ADDRESS GUIDE` explica cómo verificar y guardar direcciones
  BLE, incluidas las direcciones privadas o rotatorias mediante nombre BLE.

### Límites, regresión y cierre

- La herramienta está aislada como dependencia de P.R.S. y TERRAIN; no añade
  dependencia inversa ni compatibilidad con `PROBE` o `SCAN + PROBE`.
- No se representa una posición, rumbo o distancia del objetivo, ni se aplica
  conversión RSSI → metros. El modelo de recorte de incertidumbre queda para
  después de las pruebas físicas.
- Sprint 020 se ha revalidado sin regresiones: el hash de NAVY7 coincide con su
  cierre, el catálogo TERRAIN mantiene `CHOOSE LOCATION` y las pruebas de mapa
  siguen correctas.
- La regresión completa posterior a la incidencia intermedia terminó con
  compilación, tests JVM, lint, ensamblado y 2/2 tests instrumentados en el
  Samsung A56 (`SM-A566B`). La ruta UI hasta `TARGET` y la selección de `NAVY7`
  también fueron comprobadas en el dispositivo; `TRACKER` abrió el campo con
  `GRID: TARGET ONLY`, `CENTER: A56 // GPS FOLLOW` y `SOURCE: A56`.
- Sprint 021 queda cerrado técnica, funcional y documentalmente, manteniendo
  la prueba física RSSI y el futuro modelo estadístico fuera del cierre.

## Sprint 020 — Corrección de TERRAIN y regeneración de NAVY7 — 2026-08-30

### Modificado

- NAVY7 regenerado con QGIS LTR 3.44.13 como MBTiles PNG offline, centrado en
  `40.35297419412242, -3.4237021485063486`.
- El selector de `MAP - TERRAIN` comienza en `CHOOSE LOCATION` y no carga un
  mapa por defecto.
- HOME y NAVY7 se muestran en orden alfabético después de `CHOOSE LOCATION`.
- HOME conserva su configuración MBTiles validada; solo se actualiza la huella
  y el asset de NAVY7.

### Validación

- MBTiles NAVY7 con `PRAGMA integrity_check = ok`, formato PNG y zoom 16–19.
- Capas QGIS activas: `highway`, `contours_2m` y `building`; MDT y
  OpenStreetMap permanecen desactivados para mantener el mapa offline.
- AGP configurado para conservar la aplicación y los APKs de tests tras
  `connectedFullDebugAndroidTest`, evitando desinstalaciones implícitas.

## Sprint 019 — Auditoría, seguridad y PIP-SuriOS v2.5 — 2026-08-30

### Añadido y modificado

- Auditoría completa del monorepo, incluidos Android, Wear OS, TERRAIN,
  RAD ZONE/Geiger, dependencias y superficie de seguridad.
- Firma visible actualizada a `PIP-SuriOS v2.5`; `versionName=2.5` y
  `versionCode=5`.
- P.R.S. //COVER (`com.suri.pipsurios.prs`) desinstalado del Samsung A56.
- Backup de la aplicación principal desactivado y reglas de transferencia
  configuradas para no exportar datos locales.
- Corregida la parada del audio del Geiger al eliminar una RAD ZONE.
- Matriz de versiones protegida: SuriOS Watch `1.1`/code `5`, PROBE-SuriOS
  watchface `2.2`/code `3` y P.R.S. PROBE // WATCH 2 `1.0`/code `1` se
  conservan sin cambios.

### Seguridad y validación

- Sin secretos detectados y sin avisos OSV en los artefactos principales
  resueltos a fecha 2026-08-30.
- El riesgo residual del Android Data Layer queda documentado para una futura
  allowlist/autenticación de nodos.
- Detalle y dictamen en [AUDIT_SPRINT_019](../AUDIT_SPRINT_019.md).

## Sprint 018 — Skin NECRON para PIP-SuriOS — 2026-08-30

### Añadido

- Skin NECRON como prototipo privado, con paleta blackstone/teal, acentos de
  bronce y emblema vectorial nativo.
- Especificación visual, referencias públicas clasificadas y política de uso
  privado con variante independiente para cualquier publicación.
- Auditoría técnica y cierre documental de Sprint 018.

### Modificado

- Home, MAP y P.R.S. resuelven los tokens visuales según la skin activa.
- P.R.S. conserva NECRON tras la revisión visual en el Samsung A56; no se
  activa el fallback a Brotherhood.
- La selección de skin permanece de sesión y vuelve a Brotherhood después de
  reiniciar el proceso.

### Validación

- Tests unitarios, lint y ensamblados de las variantes Android y módulos Watch:
  `BUILD SUCCESSFUL`.
- `app-full-debug.apk` instalada y validada en Samsung A56 (`SM_A566B`).
- Auditoría: [AUDIT_SPRINT_018](../AUDIT_SPRINT_018.md).

## Sprint 017 — PIP-SuriOS v2.4, cierre de P.R.S. compacto y clasificación de dispositivos — 2026-08-30

### Estado del corte

- Iteración registrada por decisión del propietario como Sprint 017 y cerrada
  técnica y documentalmente.
- Firma visible y versión técnica de PIP-SuriOS actualizadas a `v2.4` (`versionCode 4`).
- `prsOnlyDebug` optimizada para la pantalla externa del Z Flip 6: `P.R.S.`
  centrado, radar a la izquierda y lista de nombres a la derecha.
- Categorías inferidas `[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` y `[COMPUTER]`
  en la edición reducida y en PIP-SuriOS; los casos no identificables no
  muestran sufijo.

### Validación y despliegue

- Tests unitarios, lint y ensamblados de `fullDebug` y `prsOnlyDebug`:
  `BUILD SUCCESSFUL`.
- Z Flip 6 (`SM-F741B`): la APK reducida `2.4-prs` instalada con `Success`
  tras la reconexión y arrancada con `Status: ok`.
- A56 (`SM_A566B`): `app-full-debug.apk` instalada con `Success` y arrancada
  con `Status: ok`.
- Pruebas físicas en moto y calibración: quedan como validación posterior; no
  bloquean el cierre técnico.
- Los trabajos paralelos de mapas y skins quedan fuera del alcance de este
  cierre y no se modifican.
- Auditoría: [AUDIT_SPRINT_017](../AUDIT_SPRINT_017.md).

## Sprint 016 — saneamiento documental y validación Watch/AVD — 2026-08-30

Los cambios de P.R.S. compacto y clasificación de dispositivos se reatribuyen
a Sprint 017. Esta entrada conserva únicamente el registro histórico de la
revisión documental y de SuriOS Watch/AVD.

### SuriOS Watch OFICIAL y AVD adicional

- Se prepara el AVD adicional `Galaxy_Watch_Ultra_2025` sobre `wearos_xl_round`,
  Wear OS 5 / API 34, 480 x 480, 320 dpi, Play Store y `x86_64`, sin alterar
  los AVD existentes.
- La esfera OFICIAL `com.suri.surioswatch` queda en `v1.1` (`versionCode 5`),
  validada en el emulador con progreso de pasos sin cifra, bateria sin
  porcentaje, accesos CAPS/STATUS/RADIO y marco circular ajustado.
- La instalacion en el Galaxy Watch Ultra fisico queda pendiente; el AVD no
  se considera equivalente a sensores ni funciones propietarias de Samsung.

### Validación y cierre

- Tests unitarios aislados, lint y ensamblado de `fullDebug` y `prsOnlyDebug`:
  `BUILD SUCCESSFUL`.
- El despliegue de la iteración P.R.S. queda documentado en
  [AUDIT_SPRINT_017](../AUDIT_SPRINT_017.md).
- Pruebas físicas de aceptación en moto y calibración de campo: pendientes y
  fuera del cierre técnico.
- Auditoría: [AUDIT_SPRINT_016](../AUDIT_SPRINT_016.md).

## Sprint 015 — cierre P.R.S. y edición local — 2026-08-30

### P.R.S.

- P.R.S. queda reconstruido alrededor de `LOCAL SCAN`, `SCAN + PROBE`,
  `CONTACT LIST`, `TRACK TARGET` y `DEVICES`.
- El GRID visual se conserva como base estética, pero la representación pasa a
  nubes de densidad e incertidumbre: no se presentan azimut, coordenadas X/Y ni
  metros como si fueran mediciones BLE.
- El análisis mantiene RSSI RAW, timestamp, RSSI suavizado, histórico, media,
  variación y tendencia por contacto; el objetivo seleccionado se resalta y se
  puede abandonar con `STOP TRACKING` sin reiniciar el escaneo.
- `DEVICES` queda reorganizado en `IDENTIFY DEVICE` y `SAVED DEVICES`, con
  identificación por dirección/nombre, persistencia y control independiente de
  habilitación, deshabilitación y eliminación.
- La instrumentación queda integrada en la lista y el seguimiento; se elimina
  el menú independiente `DIAGNOSTICS` y `OPERATION GUIDE` queda vacío para
  completarlo después de la calibración de campo.

### Ediciones y validación

- Se generan `app-full-debug.apk` y `app-prsOnly-debug.apk`.
- La edición local arranca en `LOCAL SCAN`, no expone `SCAN + PROBE` ni registra
  el servicio PROBE; la edición completa mantiene el flujo con PROBE y el P.R.S.
  en horizontal.
- Se verificó el ciclo `SAVE → DISABLE → ENABLE → REMOVE` en `DEVICES` sin
  dejar reglas de prueba persistidas.
- Tests, ensamblados y lint de `fullDebug` y `prsOnlyDebug`: `BUILD SUCCESSFUL`.
- Auditoría final: [AUDIT_SPRINT_015.md](../AUDIT_SPRINT_015.md). Registro
  canónico: [ACTIVE_SPRINT.md](../SPRINTS/ACTIVE_SPRINT.md).

## Sprint 015 — addendum de auditoría y cierre final — 2026-08-29

### Modificado

- `P.R.S. TESTING` inicia en modo dual, exige línea base de 30 s y expone la calidad de ubicación relativa.
- El CSV de calibración pasa a 30 columnas, con precisión, modo de ubicación, este/norte/distancia relativos y estado del fix, sin coordenadas GPS en bruto.
- La documentación de Sprint 015, `ACTIVE_SPRINT` y P.R.S. queda reconciliada con el comportamiento actual.

### Validación y estado

- Build completa de `app`, `remoteprobe`, `watchface` y `probewatchface`, tests y Lint: `BUILD SUCCESSFUL`.
- Watch 2 conectado por ADB inalámbrico y `RemoteProbeService` activo.
- A56 pendiente de reconexión en el corte del 2026-08-29; emulador API 34 incompatible con el APK móvil que requiere API 35.

## Sprint 015 - PIW/PROBE-SuriOS y cierre de calibración P.R.S. - 2026-08-28

### Añadido

- Nueva esfera independiente `PROBE-SuriOS` con un único botón `PROBE`.
- Guía imprimible de calibración del P.R.S. con preparación, matriz D1-D10, pruebas A56 ONLY/DUAL NODE, exportación, criterios y hojas de campo.
- Auditoría técnica y cierre documental de Sprint 015.

### Modificado

- PIW-SuriOS Watch mantiene el modo ambiente con sólo el emblema de la Hermandad girando sobre su eje vertical.
- PROBE-SuriOS queda firmado como `v2.1` (`versionCode 2`) y desplegado en emulador y Watch 2.
- La documentación del P.R.S. queda alineada con las ventanas reales CLOSE 6 s y WIDE 10 s.

### Validación

- Tests unitarios, compilación de `app`, `remoteprobe`, `watchface` y `probewatchface`, y lint correctos.
- Watch 2 reconectado por ADB inalámbrico vía mDNS y PROBE-SuriOS seleccionado como esfera activa.

## v2.3 - SET-UP persistente y WATCH 2 en ACCESORIES - 2026-08-28

### Añadido

- SET-UP reorganizado en los submenús `INPUT` y `DATA`.
- Actividad dedicada en orientación vertical para todos los menús de SET-UP.
- Persistencia de los datos OPERATOR: ID, NAME, CALLSIGN, NUMBER, COUNTRY y TEAM.
- Gestión de configuración de loadout mediante `LoadoutConfigurationRepository`.
- `WEAPON` de PRIMARY WEAPON convertido en campo de texto libre; `ROLE` conserva el selector desplegable.
- `EDIT` y `DELETE` en DATA para los datos del operador y PRIMARY WEAPON.
- `WATCH 2` añadido a INVENTORY, CURRENT GEAR, SET-UP, edición de operaciones y COMPLEMENTS.

### Modificado

- Firmas visibles de PIP-SuriOS actualizadas a `PIP-SuriOS v2.3`.
- `versionName` técnico actualizado a `2.3` y `versionCode` a `3`.
- CURRENT GEAR, STATUS y los snapshots de operaciones muestran la réplica personalizada guardada cuando existe.

### Validación

- Tests unitarios, compilación de `app`, `remoteprobe` y `watchface`, y lint correctos.
- SET-UP comprobado en vertical sobre Samsung Galaxy A56.
- APK móvil instalada y verificada en Galaxy A56 y emulador Pixel.

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
