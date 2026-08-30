# Auditoría técnica y cierre documental — Sprint 016

Fecha de cierre: 2026-08-30
Estado: CERRADO para el alcance técnico y documental de esta terminal
Proyecto: SuriOS Ecosystem / PIP-SuriOS

> Nota de atribución: por decisión del propietario, el refinamiento compacto de
> P.R.S. y la clasificación de dispositivos descritos en este informe se
> consideran parte de Sprint 017. El informe vigente para esta iteración es
> [AUDIT_SPRINT_017](AUDIT_SPRINT_017.md); este archivo se conserva como
> histórico de Sprint 016 y de sus addenda.

## Alcance auditado

Este cierre recoge únicamente el trabajo realizado y comprobado durante la
sesión actual. El árbol de trabajo ya contenía cambios amplios anteriores; no
se han revertido, limpiado ni incorporado como parte de este sprint.

Se auditó la variante reducida `prsOnlyDebug`, la variante completa
`fullDebug`, la integración de categorías en P.R.S., la instalación disponible
por ADB y la documentación canónica del sprint.

## Resultado funcional

- La edición reducida coloca el radar en la mitad izquierda, centrado
  verticalmente, y el listado en la mitad derecha.
- `P.R.S.` queda centrado en la cabecera de la edición reducida.
- El listado reducido muestra únicamente nombres legibles. Se retiraron de esa
  superficie `LOCAL SCAN`, `A56: SCANNING`, `DENSITY ONLY // AZIMUTH N/A`, el
  contador de nodos y los textos auxiliares que no aportan valor durante la
  conducción.
- La identificación de tipo se aplica tanto a la edición reducida como a la
  edición incluida en PIP-SuriOS. Cuando existe evidencia suficiente se añade
  al nombre una categoría entre corchetes: `[PHONE]`, `[WATCH]`, `[TV]`,
  `[AUDIO]` o `[COMPUTER]`. Si no es identificable no se añade ningún sufijo.
- La categoría es una inferencia heurística basada, por orden, en el nombre
  anunciado, la clase Bluetooth y el campo BLE Appearance. No se muestra
  confianza ni signos de interrogación y no se presenta como identificación
  forense definitiva.

## Cambios auditados

- `app/src/main/java/com/suri/pipsurios/prs/PrsDeviceCategory.kt`: categorías,
  reglas de inferencia y sufijo visible.
- `app/src/main/java/com/suri/pipsurios/prs/PrsModels.kt`: clase Bluetooth y
  categoría derivada en `BleObservation`.
- `app/src/main/java/com/suri/pipsurios/prs/BleScanner.kt`: captura de la clase
  Bluetooth y lectura de `addressType` protegida para API 35 o superior.
- `app/src/main/java/com/suri/pipsurios/ui/screens/PrsTrackingScreen.kt`:
  composición de la edición reducida y categorías en lista/seguimiento.
- `app/src/main/java/com/suri/pipsurios/ui/screens/PrsDevicesScreen.kt`:
  categoría en la lista de identificación de dispositivos.
- `app/src/test/java/com/suri/pipsurios/PrsDeviceClassifierTest.kt`: pruebas de
  nombre, clase Bluetooth, Appearance y dispositivo desconocido.

## Evidencia de validación

| Comprobación | Resultado |
|---|---|
| `:app:testFullDebugUnitTest --rerun-tasks` | `BUILD SUCCESSFUL` |
| `:app:testPrsOnlyDebugUnitTest --rerun-tasks` | `BUILD SUCCESSFUL` |
| `:app:lintFullDebug :app:lintPrsOnlyDebug` | `BUILD SUCCESSFUL`, sin errores |
| `:app:assembleFullDebug :app:assemblePrsOnlyDebug` | `BUILD SUCCESSFUL` |
| `git diff --check` | Limpio en los cambios rastreados |
| Comprobación visual de la edición reducida | Correcta en `tmp/prs_updated.png` |

Una ejecución conjunta de las dos suites unitarias produjo un
`NoSuchMethodError` de classpath entre variantes. La aceptación se hizo
repitiendo cada suite por separado, ambas con resultado correcto; no se ha
considerado ese incidente un fallo funcional del clasificador.

El lint detectó inicialmente el acceso a `BluetoothDevice.addressType` sin
protección de API. Se corrigió con una guarda `SDK_INT >= 35` y el lint final de
ambas variantes quedó correcto.

## ADB y despliegue

- El último despliegue confirmado de `prsOnlyDebug` terminó en `Success` sobre
  el Samsung Z Flip 6 (`SM-F741B`, serie inalámbrica
  `adb-R5CX7102VQJ-zKA5nY._adb-tls-connect._tcp`). La comprobación visual del
  layout reducido quedó registrada en `tmp/prs_updated.png`.
- La recompilación final posterior a la corrección de API generó ambas APK,
  pero no pudo reinstalarse en el cierre porque el Z Flip 6 dejó de aparecer en
  `adb devices`.
- En el último corte ADB sólo mostró el emulador `emulator-5554`. El intento de
  conexión a `192.168.1.58:36457` fue rechazado por el dispositivo (`10061`),
  por lo que no se declara una instalación final de esa recompilación.
- `fullDebug` queda compilada y auditada. La instalación de esta iteración en
  el A56 no queda confirmada en el último corte por falta de conexión ADB.

## Alcance complementario documentado

Como parte del mismo cierre de terminal también quedó registrado el AVD
`Galaxy_Watch_Ultra_2025` y la esfera OFICIAL `com.suri.surioswatch` `v1.1`
validada en Wear OS 5 / API 34, 480 x 480, 320 dpi, con accesos CAPS/STATUS/
RADIO. La esfera PROBE permanece separada. No se declara instalación en el
Galaxy Watch Ultra físico ni integración Orca-QGIS.

## Pendientes y límites

- Quedan pendientes las pruebas físicas de aceptación en moto: legibilidad,
  estabilidad, reflejos, visibilidad de la lista y comportamiento con señales
  reales. No se ha realizado calibración física ni se han modificado los
  parámetros de `PrsTuning.DEFAULT`.
- La clasificación puede no aparecer cuando el anuncio no aporta nombre,
  clase o Appearance reconocible; también puede ser genérica o equivocada en
  dispositivos que anuncien datos incompletos.
- No se declara medición de azimut, distancia exacta, coordenadas ni
  identificación definitiva del fabricante/modelo.

## Auditoria adicional de SuriOS Watch y del AVD

La lista final de AVD conserva `Pixel_8` y `Wear_OS_Large_Round` y añade el
AVD independiente `Galaxy_Watch_Ultra_2025`.

| Propiedad | Resultado |
|---|---|
| Hardware Profile | `wearos_xl_round (Google)` |
| AVD ID interno | `Wear_OS_XL_Round` |
| Resolucion / densidad | 480 x 480 / 320 dpi |
| Imagen | Wear OS 5 / Android API 34 |
| Play Store | Activada |
| Arquitectura | `x86_64` |
| CPU / RAM | 4 nucleos / 512 MB |

No se eliminaron ni sustituyeron AVD existentes. El perfil reproduce una
superficie Wear OS estable, pero no el hardware propietario del Galaxy Watch
Ultra: corona, bisel, sensores, autonomia y comportamiento de salud deben
validarse en el dispositivo fisico.

La esfera OFICIAL esta en `watch/watchface`, paquete
`com.suri.surioswatch`, Watch Face Format v1, `versionName=1.1` y
`versionCode=5`. La validacion visual en `emulator-5554` confirmo:

- marco circular y emblema de la referencia;
- firma visible `v1.1`;
- botones CAPS, STATUS y RADIO sin iconos, con recolocacion y separacion
  respecto al marco;
- grafico de progreso de pasos por diez segmentos basado en `[STEP_COUNT]`,
  sin cifra y con objetivo de referencia de 10.000 pasos;
- pila centrada sin porcentaje;
- esfera PROBE separada y sin cambios en esta iteracion.

El emulador devolvio estado ADB `device`, el wallpaper activo fue
`DeclarativeWatchFaceRuntime0` y la esfera se dejo visible. STATUS abrio
`DATA - STATISTICS`. Google Pay y Spotify no se declaran probados end-to-end
en el AVD porque sus paquetes no estaban instalados.

La validacion reproducible final incluyo `:watchface:assembleDebug`, las dos
suites unitarias, los ensamblados `fullDebug`/`prsOnlyDebug` y el lint de ambas
variantes, todo con `BUILD SUCCESSFUL`. Se corrigio la proteccion de API de
`BluetoothDevice.addressType` para mantener la compatibilidad con API 34.

La integracion Orca-QGIS no se audito ni modifico en esta terminal; queda
pendiente de una fase propia.

## Cierre

Sprint 016 queda cerrado técnica y documentalmente para lo realizado en esta
terminal. La validación física se conserva como actividad posterior de
aceptación y no como evidencia ya superada. La documentación y el código
asociado se incluyen en el commit de cierre de esta entrega.

Documentos relacionados: [Sprint 016 v1.1](SPRINTS/SPRINT_016_v1.1.md),
[ACTIVE_SPRINT](SPRINTS/ACTIVE_SPRINT.md), [SPRINT_HISTORY v1.3](SPRINTS/SPRINT_HISTORY%20v1.3.md)
y [CHANGELOG](CHANGELOG/CHANGELOG.md).
