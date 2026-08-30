# Auditoría técnica y de seguridad — Sprint 019

**Fecha:** 2026-08-30  
**Estado:** CERRADO  
**Proyecto:** SuriOS Ecosystem / PIP-SuriOS  
**Alcance:** monorepo `D:\WristOS`, aplicación Android completa y reducida,
módulos Wear OS, mapa TERRAIN, lógica RAD ZONE/Geiger, configuración de
seguridad, dependencias y despliegue en Samsung Galaxy A56.

## Resultado ejecutivo

- No se identificaron vulnerabilidades críticas o altas en la revisión estática,
  de configuración, dependencias y superficie de ataque realizada.
- La consulta de dependencias resueltas contra OSV no devolvió avisos para los
  diez componentes principales revisados.
- No se localizaron secretos, claves privadas, tokens ni credenciales en el
  código fuente rastreado.
- El riesgo de exposición por copia de seguridad de los datos locales queda
  mitigado: `allowBackup=false` y reglas explícitas de exclusión.
- Permanece un riesgo medio aceptado en el canal Android Data Layer: los
  servicios de telemetría y control son puntos de entrada para dispositivos
  emparejados y no mantienen una lista criptográfica de nodos autorizados.
- P.R.S. //COVER fue desinstalado del A56. La aplicación principal permanece
  instalada y se actualiza a PIP-SuriOS v2.5.

## Componentes auditados

| Componente | Identificador/variante | Resultado |
|---|---|---|
| Aplicación principal | `:app`, `fullDebug`, `com.suri.pipsurios` | Revisada |
| Aplicación compacta | `:app`, `prsOnlyDebug`, `com.suri.pipsurios.prs` | Desinstalada del A56 |
| Watchface | `:watchface` | Compilación revisada |
| Watchface v2 | `:watchfacev2` | Compilación revisada |
| PROBE Wear OS | `:probe` | Compilación y superficie revisadas |
| Protocolo compartido | `:probeprotocol` | Tests revisados |
| TERRAIN | MBTiles NAVY7/HOME y selector de ubicación | Revisado |
| RAD ZONE/Geiger | creación, eliminación y parada de audio | Revisado y probado |

## Matriz de versiones protegida

| Producto | Módulo | Application ID | Versión | Estado |
|---|---|---|---|---|
| PIP-SuriOS | `:app` full | `com.suri.pipsurios` | `2.5` / code `5` | APK instalada en A56 |
| P.R.S. compacto | `:app` prsOnly | `com.suri.pipsurios.prs` | `2.5-prs` / code `5` | No instalado en A56 |
| SuriOS Watch oficial | `:watchface` | `com.suri.surioswatch` | `1.1` / code `5` | Conservado |
| PROBE-SuriOS watchface | `:watchfacev2` | `com.suri.surioswatch.probewatchface` | `2.2` / code `3` | Conservado |
| P.R.S. PROBE // WATCH 2 | `:probe` | `com.suri.surioswatch.probe` | `1.0` / code `1` | Conservado |
| Protocolo compartido | `:probeprotocol` | `com.suri.probeprotocol` | sin versión de aplicación | Biblioteca |

El Sprint 019 no cambia las versiones de Watch 2, `:watchface`, `:watchfacev2`
ni `:probe`; únicamente actualiza la aplicación móvil a v2.5. La matriz queda
registrada para evitar que una futura limpieza confunda la watchface PROBE con
el ejecutable P.R.S. PROBE del reloj.

## Cambios de cierre

### Versión

- `app/build.gradle.kts`: `versionName` `2.4` → `2.5`.
- `versionCode` `4` → `5`.
- Firmas visibles de la aplicación actualizadas a `PIP-SuriOS v2.5`.
- Los módulos Watch y PROBE conservan sus propias versiones; no se alteran
  por este incremento de la aplicación móvil.

### Privacidad y copias de seguridad

- `android:allowBackup` de la aplicación principal queda en `false`.
- `backup_rules.xml` y `data_extraction_rules.xml` excluyen los dominios de
  archivos, bases de datos, preferencias y almacenamiento externo tanto de la
  copia en nube como de la transferencia entre dispositivos.
- Esta decisión evita exportar perfiles, loadouts, operaciones, preferencias,
  zonas y datos locales sin consentimiento explícito. También implica que esos
  datos no se restauran automáticamente desde una copia de Android.

### Dispositivo A56

- ADB: `RZGYC07H0EX`, modelo `SM_A566B`, estado `device`.
- Paquete eliminado: `com.suri.pipsurios.prs` (P.R.S. //COVER).
- Paquete principal conservado: `com.suri.pipsurios`.

## Análisis de vulnerabilidades

### SEC-019-01 — Copia de seguridad de datos locales — CERRADO

La configuración anterior permitía la copia de seguridad de información local
de la aplicación. Se corrigió desactivando la copia (`allowBackup=false`) y
manteniendo exclusiones explícitas en los dos formatos de reglas de Android.

### SEC-019-02 — Confianza del Android Data Layer — RIESGO MEDIO ACEPTADO

`ProbeDataLayerService` y `ProbeControlService` necesitan estar expuestos como
listeners Wearable para funcionar con el PROBE. La implementación valida la
ruta y el formato del mensaje, pero no aplica una allowlist criptográfica del
nodo origen ni una autenticación de aplicación propia.

Mitigación actual: el canal está limitado al Data Layer de dispositivos
emparejados y los mensajes se procesan con protocolos tipados. Trabajo
pendiente para una versión de endurecimiento: validar explícitamente el
`sourceNodeId` frente a nodos autorizados y añadir autenticidad/replay
protection si el escenario deja de ser un conjunto privado de dispositivos.

### SEC-019-03 — Identificadores BLE y datos operativos — RIESGO BAJO/MEDIO

El escáner P.R.S. puede conservar o mostrar direcciones BLE, nombres y datos
publicitados en hexadecimal; además, perfiles, loadouts y operaciones se
guardan localmente. El aislamiento del sandbox y la desactivación de backup
reducen la exposición. Se mantiene como deuda la revisión futura de retención,
exportación y borrado selectivo de estos datos.

### SEC-019-04 — Mantenimiento de dependencias — DEUDA DE MANTENIMIENTO

Lint detecta que varias versiones directas declaradas en el catálogo están por
detrás de versiones más recientes (AndroidX, Compose, Kotlin y ubicación). La
resolución actual compila correctamente y la consulta OSV realizada el
2026-08-30 no encontró avisos para los diez artefactos principales resueltos.
No se hace una actualización masiva dentro de este cierre para evitar cambios
de comportamiento no relacionados. Debe tratarse en un sprint específico de
dependencias, con validación funcional y de compatibilidad Wear OS.

### Controles sin hallazgos relevantes

- Sin claves API, contraseñas, tokens, certificados privados o credenciales en
  fuentes y recursos rastreados.
- Sin WebView, sockets, `Runtime`/`ProcessBuilder`, reflexión peligrosa ni
  endpoints HTTP activos en el código revisado.
- Las operaciones almacenadas validan el nombre de fichero con el patrón de
  fecha esperado y usan el almacenamiento privado de la aplicación.
- Las actividades exportadas revisadas tienen una finalidad concreta: launcher,
  acceso explícito desde la watchface o listener del Data Layer.
- Los riesgos del antiguo gateway HTTP que aparecen en documentación histórica
  no representan una ruta activa del código actual.

## Validación ejecutada

- Gradle: `BUILD SUCCESSFUL` en 5m 04s; 246 tareas procesadas.
- Tests `:app:testFullDebugUnitTest`: 107 tests, 0 fallos, 0 errores, 0
  omitidos.
- Tests `:app:testPrsOnlyDebugUnitTest`: 107 tests, 0 fallos, 0 errores, 0
  omitidos.
- `:probeprotocol:test`: completado correctamente.
- Lint `fullDebug`: 41 avisos no bloqueantes (39 warnings, 2 hints).
- Lint `prsOnlyDebug`: 43 avisos no bloqueantes (41 warnings, 2 hints).
  No hubo errores Lint bloqueantes; los avisos restantes son deuda de
  mantenimiento, APIs/declaraciones antiguas y recomendaciones de recursos.
- Ensamblados correctos: `app-full-debug.apk`, `app-prsOnly-debug.apk`,
  `watchface`, `watchfacev2` y `probe`.
- ADB A56: instalación de la APK principal con `Success`; `versionName=2.5`,
  `versionCode=5`, `targetSdk=37`; arranque confirmado en
  `com.suri.pipsurios/.MainActivity`.
- Comprobación de paquetes: únicamente aparece `com.suri.pipsurios`; no
  aparece `com.suri.pipsurios.prs`.
- `git diff --check`: sin errores de espacios; las advertencias mostradas por
  Git corresponden únicamente a conversión de finales de línea LF/CRLF.

## Dictamen

Sprint 019 queda aprobado para pruebas privadas y controladas. No se considera
un producto endurecido para redes hostiles o distribución pública mientras no
se cierre SEC-019-02 y no se complete la actualización planificada de
dependencias. El cambio de backup es intencionado y debe comunicarse si se
distribuye la APK a más operadores.
