# Sprint 042 - SuriOS Radar: estabilidad BLE y orientacion fija

---
document: SPRINT
project: SuriOS Watch
version: 1.0
status: CLOSED
owner: Diego Perez de Camino
date: 2026-10-18
predecessor: Sprint 041
watchface_version: 1.6
watchface_version_code: 10
watchface_format: 2
radar_app_version: 0.1
radar_app_version_code: 1
radar_app_module: radarwatch
adb_endpoint: 192.168.1.63:45843
---

## Objetivo

Corregir dos incidencias observadas despues de desconectar ADB: la rotacion
involuntaria de la aplicacion y la perdida de estabilidad de la conexion BLE.
Tambien se elimina la rotacion animada del emblema en la esfera, incluida la
vista normal, bloqueo y ambiente.

## Cambios realizados

- [x] Fijar la orientacion de SuriOS Radar ignorando el sensor del reloj.
- [x] Evitar que volver a primer plano reinicie una conexion BLE ya activa.
- [x] Ignorar callbacks de conexiones GATT antiguas o ya cerradas.
- [x] Hacer el arranque BLE idempotente para evitar escaneos duplicados.
- [x] Cerrar correctamente la conexion GATT al detener el cliente.
- [x] Mantener la reconexion automatica cuando se pierde la conexion.
- [x] Eliminar el giro del emblema en los modos normal, bloqueo y ambiente.
- [x] Documentar el comportamiento esperado al desconectar ADB.
- [x] No modificar el cableado, el firmware del ESP32 ni el protocolo BLE.

## Evidencia tecnica

- [x] Compilacion correcta de `radarwatch` y `watchface`.
- [x] Pruebas unitarias del modulo `radarwatch` correctas.
- [x] Lint de ambos modulos correcto.
- [x] XML de la esfera validado como bien formado.
- [x] Instalacion realizada en el Watch Ultra mediante ADB Wi-Fi en
      `192.168.1.63:45843`.
- [x] `MainActivity` muestra `SCREEN_ORIENTATION_NOSENSOR` en el estado del
      dispositivo despues de instalar la correccion.
- [x] El modo REAL completo una conexion GATT: descubrimiento de servicios y
      activacion de notificaciones de la caracteristica del radar.
- [x] El ESP32 no se reinicio durante la prueba de reconexion.
- [x] No quedan tokens de giro del emblema en la definicion de la esfera.
- [x] La comprobacion de diferencias no detecta errores de whitespace.

## Validaciones pendientes

Las siguientes comprobaciones requieren observar el resultado en uso normal y
se mantienen pendientes hasta disponer de esa validacion visual o funcional:

- [x] Confirmar visualmente que SuriOS Radar permanece fijo al mover la
      muneca.
- [x] Confirmar visualmente el emblema estatico en la esfera normal.
- [x] Confirmar visualmente el emblema estatico en la pantalla de bloqueo.
- [x] Confirmar visualmente el emblema estatico en modo ambiente.
- [ ] Repetir una prueba con ADB completamente desconectado y confirmar que la
      sesion BLE no se interrumpe mientras la aplicacion sigue activa.

Las cuatro validaciones visuales fueron confirmadas por el propietario el
2026-10-23. La prueba con ADB completamente desconectado permanece pendiente y
no bloquea el cierre tecnico del Sprint.

## Cierre

El Sprint 042 queda **CLOSED**. Las correcciones de orientacion, ciclo de vida
BLE y emblema estan implementadas y verificadas por compilacion, pruebas,
instalacion y logs de GATT. Las validaciones pendientes quedan registradas de
forma explicita para no presentarlas como comprobadas antes de verlas en el
reloj.
