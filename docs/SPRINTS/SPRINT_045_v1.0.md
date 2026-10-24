# Sprint 045 - SuriOS Watch: nueva esfera Ely para Wear OS

---
document: SPRINT
project: SuriOS Watch / Ely
version: 1.0
status: CLOSED
owner: Diego Perez de Camino
date: 2026-10-23
predecessor: Sprint 044
watchface_name: Ely
watchface_package: com.suri.surioswatch.ely
watchface_version: 1.3
watchface_version_code: 4
watchface_format: 2
source_reference: PICTURES/WATCHFACE_REFERENCES/Ely.jpg
watch_device: Galaxy Watch Ultra
watch_model: SM-L705F
wear_os_api: 36
adb_endpoint: 192.168.1.63:41275
---

## Objetivo

Crear una segunda esfera independiente para Wear OS basada en `Ely.jpg`, sin
modificar la esfera oficial `com.suri.surioswatch`, y desplegarla en el Galaxy
Watch Ultra.

## Cambios realizados

- [x] Crear el módulo independiente `watch/watchfaceely`.
- [x] Registrar `:watchfaceely` en `settings.gradle.kts`.
- [x] Integrar la referencia Ely como `ely_background.png` en
      `res/drawable-nodpi`.
- [x] Mantener la esfera como recurso WFF sin código, con WFF 2 para igualar la
      configuración que ya funciona en el runtime Samsung del reloj.
- [x] Añadir hora, fecha, batería y pasos como capas dinámicas sobre la imagen.
- [x] Añadir el botón visual `Music`.
- [x] Configurar `Music` con `Launch target="MUSIC_PLAYER"`, sin fijar Spotify
      como paquete en la esfera.
- [x] Añadir un modo ambiente reducido con fondo negro y hora atenuada.
- [x] Incrementar el paquete Ely a `versionCode 4`, `versionName 1.3`.

## Validación técnica

- [x] `gradlew.bat :watchfaceely:assembleDebug :watchfaceely:lintDebug`:
      `BUILD SUCCESSFUL`.
- [x] Manifest WFF y XML de la esfera verificados y empaquetados sin error.
- [x] Recurso de fondo PNG integrado correctamente.
- [x] APK instalado por ADB con resultado `Success`.
- [x] Paquete comprobado en el reloj: `com.suri.surioswatch.ely`.
- [x] Versión comprobada en el reloj: `1.3`, `versionCode 4`, `minSdk 34`,
      `targetSdk 37`.

## Validación en el Galaxy Watch Ultra

- [x] Dispositivo identificado como `SM-L705F`, Wear OS API 36, pantalla
      `480x480`.
- [x] La esfera se activó mediante el canal de depuración WFF y recibió
      `Favorite Id=[5]`.
- [x] El runtime del reloj registró `WatchFaceId[com.suri.surioswatch.ely,null]`.
- [x] La captura real de la esfera activa queda archivada en
      [ely_active_watch_ultra.png](../ASSETS/ELY/ely_active_watch_ultra.png).
- [x] La pulsación sobre `Music` abrió la actividad predeterminada disponible
      en el reloj, `com.spotify.music/com.spotify.wear.main.MainActivity`.
      Esto confirma que el destino es genérico y que Spotify solo es el
      reproductor predeterminado actual del dispositivo.

## Pendientes no bloqueantes

- [ ] La imagen de referencia contiene hora, fecha, batería y pasos estáticos;
      las capas dinámicas se superponen y todavía dejan parte de esos valores
      visibles bajo las máscaras. Requiere una iteración visual posterior.
- [ ] La preview del selector parte de la referencia Ely original; la próxima
      iteración debería generar una preview limpia que también muestre `Music`
      en lugar del texto integrado `SPOTIFY`.
- [ ] Validar visualmente el modo ambiente en una sesión prolongada sobre el
      reloj físico.

Estas tareas son de refinamiento visual/operativo y no bloquean la creación,
instalación ni activación de la esfera.

## Cierre

El Sprint 045 queda **CLOSED**. La esfera Ely está separada de la esfera
SuriOS, compilada, instalada y activa en el Galaxy Watch Ultra. Las deudas
visuales quedan trasladadas explícitamente a un sprint posterior.
