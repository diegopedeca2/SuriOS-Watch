# Auditoría técnica y cierre — Sprint 015

**Fecha de auditoría:** 2026-08-30
**Proyecto:** WristOS / PIP-SuriOS
**Estado:** CERRADO

## Dictamen ejecutivo

El rediseño de P.R.S. está integrado en la aplicación móvil y el flujo activo
queda alineado con el comportamiento real del repositorio. La implementación
ya no intenta presentar una posición física de un contacto BLE: mantiene
contactos, analiza su evolución RSSI y permite seguir un objetivo concreto.

La auditoría confirma:

- `LOCAL SCAN` y `SCAN + PROBE` son los dos modos funcionales del menú P.R.S.
- `DEVICES` está separado en `IDENTIFY DEVICE` y `SAVED DEVICES`.
- La adquisición BLE, el procesamiento temporal y las inferencias están
  separados en modelos distintos.
- El GRID conserva la estética anterior, pero las nubes ocupan el azimut
  completo y no representan coordenadas ni dirección.
- `TRACK TARGET` resalta el contacto seleccionado y se puede abandonar sin
  reiniciar el sonar ni borrar el resto de contactos.
- El Watch 2 PROBE permanece como nodo operativo opcional en la APK completa.
- La edición `prsOnly` es local, compacta y no expone opciones ni servicio
  PROBE.

## Alcance revisado

### P.R.S. móvil

El flujo actual se encuentra en:

- `app/src/main/java/com/suri/pipsurios/prs/`
- `app/src/main/java/com/suri/pipsurios/ui/screens/PrsTrackingScreen.kt`
- `app/src/main/java/com/suri/pipsurios/ui/screens/PrsDensityGrid.kt`
- `app/src/main/java/com/suri/pipsurios/ui/screens/PrsDevicesScreen.kt`
- `app/src/main/java/com/suri/pipsurios/ui/screens/PrsNavigation.kt`

`MainActivity` conecta el menú P.R.S. con `LOCAL SCAN`, `SCAN + PROBE`,
`DEVICES` y `OPERATION GUIDE`. La guía interna se mantiene vacía por decisión
de producto hasta disponer de un procedimiento de campo estable.

### Adquisición y análisis

`BleScanner` conserva por observación el identificador técnico, RSSI RAW,
timestamps, nombre anunciado, advertising data, tipo de dispositivo y tipo de
dirección. `PrsContactTracker` mantiene los contactos por identificador técnico
y aplica la evaluación temporal configurada.

`PrsTuning.DEFAULT` centraliza la cadencia, suavizado, histórico, umbrales,
confirmación, histéresis y expiración. `PrsDensityEstimator` mantiene separada
la capa visual de densidad y reserva el punto de extensión para movimiento y
orientación futuros.

### Dispositivos conocidos

`PrsDeviceRegistry` persiste reglas por dirección BLE o por nombre BLE exacto.
La dirección es el método principal; el nombre queda como alternativa para
direcciones privadas o rotatorias. Cada regla incorpora `enabled`, de modo que
puede desactivarse temporalmente sin eliminarla. Las reglas habilitadas se
filtran antes de entrar en el tracker, tanto para muestras del A56 como para
muestras recibidas del PROBE.

La migración de `ignored_rules` a `saved_devices` se ejecuta una sola vez y no
forma parte del algoritmo activo. La prueba de UI de esta sesión creó una regla
temporal, verificó `DISABLE`, `ENABLE` y `REMOVE`, y terminó con `0 TOTAL`.

### Watch 2 PROBE

La integración activa se limita a los módulos reales:

- `watch/probe`: aplicación Wear OS operativa para escaneo, ubicación y estado;
- `watch/probeprotocol`: contrato compartido del Data Layer;
- `app/.../ProbeLink.kt`: envío de comandos desde el teléfono;
- `app/.../ProbeDataLayerService.kt` y `ProbeTelemetryStore.kt`: recepción y
  publicación de telemetría.

El PROBE no representa información de contactos en el reloj. El A56 incorpora
las observaciones remotas al mismo modelo `BleObservation` y al mismo tracker.
Cuando existe un fix válido, la posición relativa de los nodos se dibuja como
subgrid de referencia dentro del GRID principal; no es un azimut BLE ni una
posición del contacto.

### Retirada del sistema anterior

La lógica antigua de sonar/posicionamiento, el gateway remoto HTTP, el flujo de
dos pasadas, las posiciones angulares sintéticas y la conversión RSSI → metros
se retiraron del flujo activo. También se retiraron las referencias de código
de las pantallas y modelos antiguos que ya no usa P.R.S.

El GRID visual se conservó únicamente como superficie gráfica. No se conserva
su interpretación antigua de puntos o ángulos.

## Verificación de interfaz

Se comprobó el flujo siguiente en el dispositivo conectado:

1. P.R.S. abre `DEVICES`.
2. `IDENTIFY DEVICE` muestra anuncios BLE reales con nombre, ID, tipo de
   dirección, RSSI RAW y advertising data.
3. `SAVE DEVICE` crea una regla persistente asociada a la dirección.
4. `SAVED DEVICES` muestra el dispositivo como `ENABLED`.
5. `DISABLE` lo convierte en `DISABLED` y permite que vuelva a aparecer en el
   análisis.
6. `ENABLE` restablece la omisión.
7. `REMOVE` elimina la regla y deja la lista vacía.

También se verificó el arranque de la edición compacta, su `LOCAL SCAN`, la
lista de contactos y el menú `DEVICES`. La opción `SCAN + PROBE` no se expone
en esa edición.

## Verificación automatizada

Comandos ejecutados desde `D:\WristOS`:

```text
gradlew.bat :app:testFullDebugUnitTest
gradlew.bat :app:testPrsOnlyDebugUnitTest
gradlew.bat :app:assembleFullDebug
gradlew.bat :app:assemblePrsOnlyDebug
gradlew.bat :app:lintFullDebug :app:lintPrsOnlyDebug
```

Resultado: `BUILD SUCCESSFUL` en tests, ensamblados y lint de ambas variantes.

También se ejecutó `git diff --check`, sin errores de formato. Las advertencias
observadas corresponden a conversiones de fin de línea del working tree, no a
fallos de compilación.

## Artefactos generados

| Edición | Paquete | APK |
| --- | --- | --- |
| Completa | `com.suri.pipsurios` | `app/build/outputs/apk/full/debug/app-full-debug.apk` |
| P.R.S. local compacta | `com.suri.pipsurios.prs` | `app/build/outputs/apk/prsOnly/debug/app-prsOnly-debug.apk` |

La edición completa mantiene `SCREEN_ORIENTATION_LANDSCAPE`. La edición
compacta usa orientación vertical y arranca directamente en `LOCAL SCAN`.

## Verificación de dispositivos

En el cierre se obtuvo:

```text
RZGYC07H0EX            device  model:SM_A566B
192.168.1.56:5555      device  model:Xiaomi_Watch_2
```

La APK completa final quedó instalada en el A56. La actividad activa confirmó
`mCurrentAppOrientation=SCREEN_ORIENTATION_LANDSCAPE`. La edición compacta se
instaló temporalmente para comprobar su arranque y navegación, y después se
restauró la edición completa en el A56.

La re-comprobación posterior al reinicio del daemon ADB de la sesión de cierre
mostró el Watch 2 reconectado en `192.168.1.56:5555`; el A56 no volvió a
aparecer en ese corte. Esto no invalida la validación de despliegue anterior,
en la que ambos dispositivos figuraban como `device`, pero deja la
reaparición ADB del A56 como acción de continuidad.

## Riesgos y deuda aceptada

- RSSI depende del hardware, orientación, obstáculos, potencia, propagación e
  interferencias.
- Una dirección BLE privada o rotatoria puede cambiar y romper la continuidad
  de un contacto; las reglas por nombre pueden coincidir con varios equipos.
- Las tendencias son inferencias de evolución de señal y no prueban movimiento
  físico del objetivo.
- El GRID no aporta rumbo, azimut, coordenadas ni distancia exacta.
- La estimación probabilística con movimiento/orientación aún no está activa;
  sólo queda preparada la interfaz de extensión.
- La calibración de campo de los umbrales debe continuar con muestras reales.
- El transporte Wear OS/Data Layer requiere que el Watch 2 esté emparejado y
  que sus permisos estén concedidos cuando se use `SCAN + PROBE`.

## Decisiones de cierre

- No se implementan RSSI → metros exactos, triangulación, Wi-Fi RTT, UWB,
  machine learning ni clasificación avanzada.
- No se reactiva `DIAGNOSTICS` como menú independiente: la instrumentación vive
  en `CONTACT LIST` y `TRACK TARGET`.
- `OPERATION GUIDE` permanece vacío de forma deliberada.
- La campaña física de P.R.S. y el ajuste centralizado de `PrsTuning.DEFAULT`
  quedan cancelados por decisión posterior del propietario; no se abre una
  nueva capa de posicionamiento.

## Estado final

Sprint 015 queda **cerrado técnica, funcional y documentalmente** a fecha
2026-08-30. El repositorio contiene la reconstrucción P.R.S., la gestión de
dispositivos, las variantes APK verificadas, la documentación de usuario
actualizada y este informe de auditoría.

## Addendum de decisión posterior — 2026-08-30

Por decisión expresa del propietario, la campaña física de P.R.S. y el ajuste
de `PrsTuning.DEFAULT` no continúan como trabajo posterior ni forman parte del
Sprint 016. Los valores actuales permanecen provisionales y no calibrados.
La futura fusión de movimiento/orientación y la guía de campo asociada tampoco
se desarrollarán en el alcance activo.
