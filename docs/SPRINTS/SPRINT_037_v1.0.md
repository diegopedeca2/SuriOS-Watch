# Sprint 037 — SuriOS Watch: armadura, pasos y terminal animado

---
document: SPRINT
project: SuriOS Watch
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-10-16
predecessor: Sprint 036
watchface_version: 1.3
watchface_version_code: 7
---

## Objetivo

Actualizar la esfera oficial `com.suri.surioswatch` del Galaxy Watch Ultra
manteniendo el estilo terminal/Fallout, incorporar la armadura como indicador
visual de pasos y dejar una presentación ambiental simple y estática.

## Alcance completado

- [x] Conservar la versión funcional del inicio del Sprint en
      `Previous Version/watchface-v1.3-calories-sprint/`.
- [x] Retirar el emblema de la Hermandad y el medidor circular de pasos.
- [x] Incorporar la armadura de `assets/armor` como fondo de la esfera.
- [x] Hacer que el color de la armadura disminuya al acercarse al objetivo de
      pasos mediante `STEP_PERCENT`.
- [x] Ajustar hora, armadura, fecha, batería y posiciones de los botones.
- [x] Mantener `CAPS`, `STATUS` y `RADIO` funcionales.
- [x] Añadir `TRAINING` y enlazarlo con la lista de ejercicios de Samsung
      Health del Watch Ultra.
- [x] Incorporar en modo normal la línea de refresco vertical inspirada en la
      aplicación móvil: intensidad 40%, 6 segundos por recorrido y 30 fps.
- [x] Mantener el modo ambiente estático con hora, armadura/progreso de pasos
      y batería.
- [x] Conservar la versión inicial `v1.2` y trabajar con la esfera incrementada
      a `v1.3` y `versionCode 7`, conforme a la aprobación del propietario.

## Validación del propietario

La validación visual se completó de forma incremental en el Watch Ultra. La
última incidencia del botón `TRAINING` fue corregida sustituyendo la actividad
interna de sesión por `ExerciseListActivity`, y el propietario confirmó que la
lista de ejercicios se abre correctamente.

## Resultado técnico

- Paquete: `com.suri.surioswatch`.
- Watch Face Format: v1, sin código ejecutable.
- Versión: `versionName=1.3`, `versionCode=7`.
- Dispositivo de validación: Samsung Galaxy Watch Ultra, ADB Wi-Fi
  `192.168.1.63:42847`.
- La copia de recuperación se realizó una sola vez al inicio del Sprint; no se
  generaron copias adicionales por cada cambio.

## Fuera de alcance

- Cambios funcionales en la aplicación móvil.
- Cambios en `watchfacev2`/PROBE.
- Integración de calorías de Samsung Health; el indicador usa pasos, según la
  decisión posterior del propietario.

## Cierre

El Sprint 037 queda **CLOSED**. No quedan incidencias técnicas abiertas dentro
de su alcance.
