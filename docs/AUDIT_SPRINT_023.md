# Auditoría técnica de SuriOS — Sprint 023

**Fecha:** 2026-08-31
**Proyecto:** `D:\WristOS`
**Estado:** cierre técnico superseded by Sprint 024; resultados y decisiones actualizados
**Alcance:** monorepo Gradle, Android/Wear OS, P.R.S./PROBE, TERRAIN, persistencia,
dependencias, configuración y reproducibilidad GIS.

## Dictamen ejecutivo

El árbol de Sprint 023 compilaba y pasaba las pruebas JVM, lint e instrumentadas sin errores bloqueantes.
No aparece un bloqueo para integrar el nuevo asset NAVY7. Sí hay problemas
previos que deben entrar en el backlog del siguiente sprint técnico, especialmente
la identidad de paquete del módulo PROBE y el endurecimiento del canal Android
Data Layer.

NAVY7 se ha regenerado desde QGIS con el mismo modelo visual y dimensional de
HOME, centrado en `40.352971232717216, -3.423711863510395`. La salida se validó
como SQLite/MBTiles, con 5242 PNG válidos de 256x256 y cobertura completa en la
rejilla prevista.

## Hallazgos priorizados

| ID | Severidad | Área | Hallazgo y evidencia | Acción recomendada |
|---|---|---|---|---|
| AUD-023-01 | Alta | PROBE / release | `watch/probe/build.gradle.kts` declara `namespace = com.suri.surioswatch.probe`, pero el APK construido declara `applicationId = com.suri.pipsurios`. La matriz documental de Sprint 019 espera `com.suri.surioswatch.probe`. | Confirmar el ID oficial, corregir Gradle y validar instalación, actualización y Data Layer antes de distribuir PROBE. |
| AUD-023-02 | Media | Seguridad Data Layer | `ProbeDataLayerService` y `ProbeControlService` aceptan mensajes por ruta/formato sin allowlist criptográfica del nodo origen ni autenticación propia. El riesgo ya estaba aceptado en Sprint 019, pero sigue abierto. | Validar `sourceNodeId` contra nodos autorizados y añadir autenticidad/replay protection si el entorno deja de ser estrictamente privado. |
| AUD-023-03 | Media | Telemetría PROBE | La secuencia de rutas se reinicia al recrear el proceso; `phoneNodeId` no restringe el destino de `putDataItem`; tampoco hay confirmación, backpressure, TTL ni limpieza de datos pendientes. Puede haber colisiones o acumulación cuando el teléfono está ausente. | Usar nonce de sesión, destino explícito, cola acotada y política de expiración/confirmación. |
| AUD-023-04 | Media | MORSE | `MorseTransmitter` llama a `CameraManager.setTorchMode`, pero el manifest de `app` no declara `android.permission.CAMERA` y no existe solicitud runtime. La funcionalidad puede fallar con `SecurityException` según dispositivo/API. | Verificar en dispositivo real; declarar y solicitar permiso, o documentar la compatibilidad concreta si el permiso no fuera necesario en la matriz objetivo. |
| AUD-023-05 | Media | TERRAIN / caché | `MbTilesRepository` invalida la copia de `filesDir/terrain/maps` únicamente por tamaño. Un asset reemplazado con igual longitud podría conservar una copia obsoleta; la validación actual no comprueba hash, bounds, esquema ni decodificación de todas las imágenes. | Añadir versión/hash esperado por mapa y validación de metadata/esquema; ampliar la prueba instrumentada. |
| AUD-023-06 | Baja-media | Persistencia | `OperationRepository.save()` crea el fichero antes de escribirlo. Un fallo de escritura puede dejar un fichero vacío que se confunda con una operación existente. | Escribir a temporal, hacer replace atómico y eliminar el temporal/huella parcial al fallar. |
| AUD-023-07 | Baja-media | Protocolo / control | `ProbeProtocol.decode()` valida estructura, pero no rangos semánticos de coordenadas, precisión, RSSI, timestamp o finitud numérica. Además, `PING` inicia el servicio completo de adquisición. | Rechazar NaN/Infinity y valores fuera de rango; separar ping/health-check de la adquisición. |
| AUD-023-08 | Baja | Compatibilidad / deuda | Lint actual informa 38 warnings + 2 hints en `fullDebug` y 40 warnings + 2 hints en `prsOnlyDebug`: dependencias atrasadas, APIs desaconsejadas, orientación fija, recursos no usados y recomendaciones Compose. | Tratar dependencias y compatibilidad Android 16 en un sprint dedicado, con pruebas de regresión. |
| AUD-023-09 | Media | GIS / reproducibilidad | Antes de este sprint no había generador de MBTiles versionado; la fuente QGIS y GPKG viven fuera del repo y la documentación fija una ruta de usuario. | Mantener el generador versionado, parametrizar proyecto/salida y respaldar o incorporar formalmente las fuentes GIS cuando proceda. |

## Revisiones sin hallazgo bloqueante

- La configuración de backup de la aplicación principal permanece desactivada y
  con reglas de exclusión explícitas, según el cierre de Sprint 019.
- No se localizaron secretos, tokens, claves privadas ni credenciales en el
  rastreo estático de fuentes y recursos.
- `settings.gradle.kts` centraliza los repositorios y rechaza repositorios de
  proyecto, reduciendo divergencias de resolución.
- El asset HOME no cambió durante este sprint.
- El problema de encoding mostrado por algunas sesiones PowerShell es de
  interpretación de consola; los documentos son UTF-8 y no se ha hecho una
  conversión masiva de documentación histórica.

## Validación ejecutada

| Comprobación | Resultado |
|---|---|
| `:app:testFullDebugUnitTest` | OK, 112 tests, 0 fallos |
| `:app:testPrsOnlyDebugUnitTest` | OK, 112 tests, 0 fallos |
| `:probeprotocol:test` | OK |
| Lint full/prsOnly | OK sin errores bloqueantes; warnings/hints documentados arriba |
| Ensamblados `app`, `watchface`, `watchfacev2`, `probe` | OK |
| Gradle | `BUILD SUCCESSFUL`, 279 tareas procesadas (53 ejecutadas, 226 al día) |
| MBTiles NAVY7 | OK; SQLite íntegro, metadata 16–19, 5242 PNG RGBA opacos válidos |
| QA visual | OK; comparación contra la salida previa y capas QGIS activas coherentes |
| ADB / despliegue | `app-full-debug.apk` 2.6 instalado en `RZGYC07H0EX` sin borrar datos; PIP-SuriOS arrancó, se seleccionó NAVY7 y la copia materializada en el A56 coincide por SHA-256. |
| Instrumentación ADB | `:app:connectedFullDebugAndroidTest` ejecutada en `RZGYC07H0EX` con resultado correcto; valida metadata nueva y 5242 teselas. |

## Recomendación de cierre

El Sprint 023 queda cerrado técnica y documentalmente. La selección NAVY7, la
materialización del asset y la regresión instrumentada se verificaron en el A56.
Los hallazgos AUD-023-01 a AUD-023-09 fueron incorporados al [BACKLOG v1.3](BACKLOG/BACKLOG_v1.3.md) y se están cerrando en el Sprint 024.

## Addendum de Sprint 024 — estado de implementación

- PROBE conserva el modo companion `com.suri.pipsurios`, no es standalone y la
  watchface `com.suri.surioswatch.probewatchface` permanece separada.
- El transporte PROBE usa mensajes dirigidos al nodo del A56. Cada muestra lleva
  sesión; se valida el nodo real y se descartan otras sesiones. No se persiste
  telemetría en el Watch 2.
- `PING` responde `PONG` sin iniciar adquisición. La cola persistente anterior se
  elimina; solo existen buffers de memoria limitados para el funcionamiento en
  vivo.
- MORSE declara y solicita `CAMERA`; sin permiso o flash la acción queda no
  disponible. MBTiles y operaciones tienen validación/hash y escritura atómica.
- El lint de app queda sin errores; los únicos avisos restantes son advisories de
  versiones fijadas y configuración de iconos heredada. Se mantienen así hasta
  una matriz de actualización independiente.
- La validación física A56–Watch 2 queda completada: el A56 recibe `ACTIVE`,
  posición, batería y observaciones BLE del Watch 2; el servicio de adquisición
  permanece vivo durante la sesión y se detiene al salir de P.R.S.
- La prueba física de MORSE confirma la solicitud de `CAMERA` y el estado
  `FLASH UNAVAILABLE` cuando el permiso es rechazado.
