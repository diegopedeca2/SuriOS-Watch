# Sprint 043 - SuriOS Watch: watchface v1.7 y acceso RADAR

---
document: SPRINT
project: SuriOS Watch
version: 1.0
status: CLOSED
owner: Diego Perez de Camino
date: 2026-10-18
predecessor: Sprint 042
watchface_version: 1.7
watchface_version_code: 11
watchface_format: 2
radar_app_version: 0.1
radar_app_version_code: 1
radar_app_module: radarwatch
adb_endpoint: 192.168.1.63:46623
---

## Objetivo

Actualizar la esfera oficial del Watch Ultra y mejorar el acceso y la lectura
de SuriOS Radar, manteniendo la identidad visual existente.

## Cambios realizados

- [x] Eliminar el lanzamiento del radar desde el centro de la armadura.
- [x] Reducir la firma de versión y colocarla encima de la fecha.
- [x] Colocar un botón RADAR en la posición de la firma anterior.
- [x] Ajustar el botón RADAR al mismo patrón visual que CAPS, STATUS,
      TRAINING y RADIO: 46 px de alto, esquinas de 8 px, borde de 2 px y
      tipografía Consolas de 18 px.
- [x] Suavizar la transición de la línea de progreso de la armadura mediante
      una zona de difuminado más amplia y gradual.
- [x] Empaquetar Consolas en `watch/watchface/src/main/res/font` para que la
      esfera no dependa de la fuente instalada en el reloj.
- [x] Mantener activa la pantalla de SuriOS Radar mientras la aplicación está
      visible mediante `FLAG_KEEP_SCREEN_ON`.
- [x] Eliminar las etiquetas de los objetivos del radar y conservar solo sus
      puntos rojos.

## Auditoría técnica

- [x] `gradlew.bat test lint :watchface:assembleDebug :radarwatch:assembleDebug :radarwatch:lint`.
- [x] XML de la esfera validado como bien formado.
- [x] No quedan referencias activas a `SYNC_TO_DEVICE` en la esfera oficial.
- [x] No quedan etiquetas de objetivos ni llamadas de dibujo de texto en la
      capa de marcadores del radar.
- [x] `git diff --check` sin errores de espacios.
- [x] APK de la watchface instalado con resultado `Success`.
- [x] APK de SuriOS Radar instalado con resultado `Success`.
- [x] Paquete `com.suri.surioswatch` verificado en v1.7, `versionCode 11`.
- [x] Validación visual del botón RADAR confirmada por el propietario.

## Estado final

No quedan incidencias técnicas abiertas dentro del alcance de este Sprint.
Las futuras modificaciones visuales o funcionales deberán abrir un nuevo
Sprint y conservar esta versión como referencia histórica.

## Cierre

El Sprint 043 queda **CLOSED**. La watchface v1.7 y la app Radar actualizada
están compiladas, instaladas y documentadas. El código queda listo para
publicarse en el repositorio remoto.
