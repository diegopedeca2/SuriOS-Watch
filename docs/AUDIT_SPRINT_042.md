# Auditoria Sprint 042 — SuriOS Radar: estabilidad BLE y orientacion fija

---
document: AUDIT
project: SuriOS Watch
sprint: 042
version: 1.6
version_code: 10
watchface_format: 2
radar_app_version: 0.1
radar_app_version_code: 1
radar_app_module: radarwatch
date: 2026-10-18
status: Aprobada para cierre documental; una validacion operativa pendiente
---

## Resultado

La auditoria no encuentra incidencias bloqueantes dentro del alcance del
Sprint 042. La orientacion de SuriOS Radar, el ciclo de vida BLE y el emblema
estatico quedan implementados y verificados por compilacion, pruebas,
instalacion y logs de GATT.

## Cambios revisados

- Orientacion de Radar fijada sin depender del sensor del reloj.
- Conexion BLE idempotente, callbacks antiguos ignorados y reconexion
  automatica conservada.
- Conexion GATT cerrada correctamente al detener el cliente.
- Giro del emblema eliminado en esfera normal, bloqueo y ambiente.
- Cableado, firmware ESP32 y protocolo BLE sin cambios.

## Evidencia tecnica

- Compilacion de `radarwatch` y `watchface`: correcta.
- Pruebas unitarias y lint de ambos modulos: correctos.
- XML de la esfera bien formado.
- Instalacion en el Watch Ultra mediante ADB Wi-Fi: correcta.
- `SCREEN_ORIENTATION_NOSENSOR` verificado en el estado del dispositivo.
- Conexion GATT REAL, descubrimiento de servicios y notificaciones activadas.
- Reconexion verificada sin reinicio del ESP32.
- No quedan tokens de giro del emblema en la esfera.
- Comprobacion de diferencias sin errores de whitespace.

## Validaciones confirmadas por el propietario

El 2026-10-23 quedan confirmadas las siguientes comprobaciones visuales y
funcionales:

- SuriOS Radar permanece fijo al mover la muñeca.
- Emblema estatico en la esfera normal.
- Emblema estatico en la pantalla de bloqueo.
- Emblema estatico en modo ambiente.

## Validacion operativa pendiente

Permanece pendiente repetir una prueba con ADB completamente desconectado y
confirmar que la sesion BLE no se interrumpe mientras la aplicacion sigue
activa. Es una comprobacion operativa no bloqueante y queda explicitamente
separada de la evidencia ya confirmada.

## Decision

El Sprint 042 queda **CLOSED** y esta auditoria queda **APROBADA PARA CIERRE
DOCUMENTAL**, con la prueba de ADB desconectado registrada como validacion
operativa posterior.
