# Sprint 044 - SuriOS Watch: v1.8, RADAR compacto y prototipo LD2450

---
document: SPRINT
project: SuriOS Watch / RADAR PROTOYPE
version: 1.0
status: CLOSED
owner: Diego Perez de Camino
date: 2026-10-23
predecessor: Sprint 043
watchface_version: 1.8
watchface_version_code: 12
watchface_format: 2
radar_app_version: 0.1
radar_app_version_code: 1
radar_app_module: radarwatch
hardware_project: hardware/RADAR PROTOYPE
adb_endpoint: 192.168.1.63:41979
---

## Objetivo

Cerrar la iteracion de SuriOS Watch y RADAR: publicar la watchface v1.8,
consolidar la app RADAR para la pantalla compacta del reloj y dejar documentado
el primer firmware del prototipo ESP32 con LD2450.

## Cambios realizados

- [x] Actualizar la firma visible y tecnica de la watchface a v1.8,
      `versionCode 12`.
- [x] Retirar la linea de terminal estatica del modo AMBIENT.
- [x] Bajar los indicadores de objetivos completados para separarlos del
      indicador de bateria.
- [x] Mantener en `RADAR` el seguimiento de hasta tres objetivos, la inversion
      del eje X y el filtrado de la zona frontal.
- [x] Crear `hardware/RADAR PROTOYPE` con firmware PlatformIO, README y copia
      de recuperacion `V0.bin`.
- [x] Incorporar Consolas, paleta oficial y el marco dentado con emblema en el
      menu de la app RADAR.
- [x] Ajustar los botones `REAL` y `SIM` al patron de la watchface:
      `92 x 46`, radio `8`, borde `2` y Consolas `18`.
- [x] Retirar el texto `BROTHERHOOD OF STEEL` de las pantallas RADAR por
      limitacion de espacio, conservando `SURIOS RADAR`.
- [x] Documentar la excepcion compacta de cabecera y el uso de PipBlue solo
      para la geometria del radar.

## Auditoria tecnica y funcional

- [x] Tests, Lint y ensamblados Android ejecutados correctamente.
- [x] Firmware ESP32 compilado correctamente con PlatformIO.
- [x] XML de la watchface bien formado.
- [x] `git diff --check` sin errores de whitespace.
- [x] APK RADAR instalada y arrancada en `192.168.1.63:41979`.
- [x] Menu RADAR verificado con `SURIOS RADAR`, `REAL` y `SIM`; la cabecera
      institucional no aparece en la superficie compacta.
- [x] Backup `V0.bin` conservado junto al proyecto del hardware.

## Pendientes no bloqueantes

- [ ] Validar fisicamente el apantallamiento trasero del LD2450 para confirmar
      que desaparece el eco posterior fuera del area teorica.
- [ ] Repetir, como validacion operativa heredada del Sprint 042, una prueba
      con ADB completamente desconectado durante una sesion BLE activa.

Estas comprobaciones requieren observacion fisica adicional y no bloquean el
cierre tecnico ni documental de este Sprint.

## Cierre

El Sprint 044 queda **CLOSED** y su auditoria queda aprobada para cierre
documental. La watchface v1.8, la app RADAR, el firmware del prototipo y la
documentacion quedan preparados para publicarse en el repositorio remoto.
