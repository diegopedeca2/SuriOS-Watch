# Sprint 015 - Reconstrucción P.R.S. y cierre PIW/PROBE-SuriOS

## Estado

- Apertura: 2026-08-28.
- Cierre: 2026-08-30.
- Cierre documental final: 2026-08-30.
- Estado: cerrado.
- Aplicación móvil: PIP-SuriOS v2.3 (`versionCode 3`).
- PIW-SuriOS Watch: v2.0 (`versionCode 2`).
- PROBE-SuriOS: v2.1 (`versionCode 2`).
- Sprint activo posterior: ninguno.

## Objetivo

Cerrar la reconstrucción de P.R.S. sobre BLE/RSSI temporal, conservar la estética
GRID sin conservar su posicionamiento sintético, dejar operativo el PROBE del
Watch 2 y entregar una documentación coherente con el código y las pruebas
realizadas.

## Alcance implementado

### PIW-SuriOS Watch

- El modo ambiente muestra sólo el emblema de la Hermandad.
- El emblema usa una escala horizontal periódica para simular un giro sobre su eje vertical.
- No se alteran las funciones principales ni la identidad del paquete PIW.

### PROBE-SuriOS

- Se mantiene como módulo de esfera independiente de PIW.
- La firma visible queda como `PROBE-SuriOS` y `v2.1`.
- El paquete es `com.suri.surioswatch.probewatchface`.
- La esfera expone únicamente el botón/acción `PROBE`.
- Se instaló en el emulador y en el Xiaomi Watch 2.
- Se dejó seleccionada como esfera activa en el Watch 2.

### P.R.S. v3.0 y documentación

- Se retiraron del flujo activo el sonar anterior, el posicionamiento sintético,
  las pasadas CLOSE/WIDE, la conversión RSSI → metros y el gateway remoto HTTP.
- Se consolidó el flujo `BLE SCAN → CONTACT LIST → RSSI HISTORY → SMOOTHING →
  TREND → DENSITY GRID`.
- Se implementaron `LOCAL SCAN` y `SCAN + PROBE` como los dos modos actuales.
- Se conservaron los contactos múltiples y el seguimiento temporal mientras se
  prioriza un objetivo mediante `TRACK TARGET`.
- Se conservó el GRID visual de v2.0 como superficie de incertidumbre, con
  nubes de densidad sin azimut ni coordenadas inventadas.
- Se añadió el resaltado del objetivo y la salida `STOP TRACKING` sin reiniciar
  el escaneo.
- Se separaron datos medidos, procesados e inferidos en modelos P.R.S.
- Se centralizaron los parámetros de evaluación en `PrsTuning.DEFAULT` y se
  dejó preparado `PrsDensityEstimator` para sensores y movimiento futuros.
- Se reorganizó `DEVICES` en `IDENTIFY DEVICE` y `SAVED DEVICES`, con reglas por
  dirección/nombre, estado `enabled`, migración persistente y eliminación.
- Se vació deliberadamente `OPERATION GUIDE` y se eliminó el menú independiente
  `DIAGNOSTICS`; la instrumentación vive en `CONTACT LIST` y `TRACK TARGET`.
- Se actualizó la [guía de usuario](../USER_GUIDE.md), el documento técnico
  [P.R.S. v3.0](../OLD VERSIONS/v3.0/PRS_v3.0.md) y esta auditoría.
- Se conserva la [guía imprimible de calibración P.R.S.](../../output/pdf/PRS_CALIBRATION_GUIDE_SPRINT_015.pdf)
  como material histórico de pruebas, no como flujo operativo vigente.

## Fuera de alcance

- No se implementó una localización física definitiva ni una reducción espacial
  probabilística basada en sensores.
- No se implementaron RSSI → metros exactos, azimut BLE, Wi-Fi RTT, UWB,
  triangulación, machine learning ni clasificación avanzada.
- No se ejecutó una campaña de campo completa ni se inventaron resultados de
  calibración.
- No se cambió la versión base de la aplicación móvil PIP-SuriOS, que permanece
  en v2.3; se añadió la variante técnica `prsOnly`.

## Validación

```text
gradlew.bat :app:testFullDebugUnitTest
gradlew.bat :app:testPrsOnlyDebugUnitTest
gradlew.bat :app:assembleFullDebug
gradlew.bat :app:assemblePrsOnlyDebug
gradlew.bat :app:lintFullDebug :app:lintPrsOnlyDebug
```

Resultado: `BUILD SUCCESSFUL`; tests, ensamblados y lint de las dos variantes
correctos. `git diff --check` no encontró errores de formato.

Verificación de dispositivos:

- Galaxy A56 conectado como `RZGYC07H0EX`; APK completa final instalada y
  orientación confirmada como `SCREEN_ORIENTATION_LANDSCAPE`.
- Xiaomi Watch 2 conectado como `192.168.1.56:5555`.
- La variante compacta se instaló temporalmente para validar arranque, lista de
  contactos y `DEVICES`; después se restauró la APK completa en el A56.

## Riesgos y deuda aceptada

- La conexión ADB inalámbrica puede desaparecer temporalmente y requiere
  redescubrimiento o reconexión.
- RSSI/BLE es una señal comparativa y no una medición de distancia, coordenadas
  o presencia humana.
- Las direcciones privadas/rotatorias pueden cambiar y los nombres BLE pueden
  coincidir con más de un dispositivo.
- La calibración física de `PrsTuning.DEFAULT` queda como siguiente actividad
  y debe producir observaciones reales antes de modificar los umbrales.

## Addendum de cierre final — 2026-08-30

La sesión de cierre incorpora la reorganización de `DEVICES` y la variante de
distribución P.R.S. local:

- `IDENTIFY DEVICE` escanea anuncios BLE en directo y permite guardar un
  contacto por dirección o por nombre exacto.
- `SAVED DEVICES` permite activar, desactivar y eliminar cada regla sin borrar
  el resto del histórico ni reiniciar el sonar.
- Las reglas activas se aplican a la adquisición local y al flujo combinado con
  PROBE; las reglas desactivadas dejan visible el contacto.
- Se generaron `app-full-debug.apk` y `app-prsOnly-debug.apk` con identificador
  de paquete separado para la edición local.
- La variante local arranca en `LOCAL SCAN`, conserva `DEVICES` y no expone
  `SCAN + PROBE` ni registra el servicio PROBE.
- La edición completa mantiene `SCAN + PROBE` y el A56 en horizontal.
- La prueba de interfaz de `DEVICES` verificó el ciclo `SAVE → DISABLE →
  ENABLE → REMOVE`; no se dejó ninguna regla de prueba persistida.

La auditoría completa está en [AUDIT_SPRINT_015.md](../AUDIT_SPRINT_015.md).
El documento técnico histórico está en
[PRS_v3.0.md](../OLD VERSIONS/v3.0/PRS_v3.0.md).

La validación del 2026-08-30 ejecutó los tests y ensamblados de ambas variantes
con resultado `BUILD SUCCESSFUL`. Durante el despliegue, ADB confirmó
simultáneamente el A56 y el Watch 2 conectados. En la re-comprobación posterior
al reinicio del daemon ADB, el Watch 2 pudo reconectarse y el A56 no volvió a
aparecer en ese corte; queda como acción operativa de continuidad, sin impacto
en el resultado de los builds.

## Addendum histórico — 2026-08-29

La revisión final de `P.R.S. TESTING` queda incorporada al alcance del sprint:

- `A56 + WATCH 2 / DUAL NODE` es el modo inicial; `A56 ONLY / WITHOUT WATCH` queda como control.
- La línea base de 30 s se reinicia al entrar o usar `RESET TEST`; la identificación queda bloqueada hasta `BASELINE: READY`.
- Las muestras exponen `GPS_RELATIVE_FILTERED`, `BLE_RANGE_ONLY`, `LINK_ONLY` o `A56_ONLY` según la evidencia disponible.
- La posición relativa solo se calcula con fixes recientes y fiables, y se suaviza antes de registrarse.
- Se añadió una prueba de exportación que verifica telemetría relativa y ausencia de coordenadas GPS en bruto.

La validación reproducible del 2026-08-29 ejecutó `test`, los ensamblados debug de `app`, `remoteprobe`, `watchface` y `probewatchface`, y `:app:lintAnalyzeDebug`: `BUILD SUCCESSFUL`.

En la reverificación de dispositivos, el Watch 2 siguió operativo por `192.168.1.56:5555` con `RemoteProbeService` en primer plano. El A56 había recibido el APK actualizado y completado la prueba manual de Testing en el cierre anterior, pero no estaba conectado en el corte final; la reconexión física queda pendiente. El emulador conserva PROBE-SuriOS v2.1, pero no admite el APK móvil actual por usar API 34 frente al mínimo API 35.

La guía PDF conserva una estructura válida (`%PDF-1.3` y `%%EOF`) y el enlace está comprobado. La revisión visual mediante render queda pendiente porque el entorno de auditoría no dispone de Poppler ni Python/PyMuPDF.

## Cierre

Sprint 015 queda cerrado técnica, funcional y documentalmente. La campaña de
calibración física posterior y el ajuste de `PrsTuning.DEFAULT` quedan
cancelados por decisión expresa del propietario; los valores actuales siguen
siendo provisionales y no calibrados.
