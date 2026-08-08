# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canónico
last_updated: 2026-08-09

---

## Sprint activo

**Ninguno**

---

## Estado

Sprint 004 de PIP-SuriOS está completado y cerrado técnica, funcional y documentalmente.

No existe ningún Sprint activo. Sprint 005 de PIP-SuriOS todavía no ha comenzado.

PIW-SuriOS v1.9 constituye la versión visual consolidada vigente de SuriOS Watch.

---

## Proyecto

Ecosistema SuriOS:

- PIP-SuriOS
- SuriOS Watch

---

## Último Sprint completado

[Sprint 006 v1.0](SPRINT_006_v1.0.md), finalizado el 2026-08-08.

Commit técnico:

`b1a4d1605cac01e86380fb6294b0cfc995ed4de0`

**Sprint 006 - Accesos directos CAPS, STATUS y RADIO**

Implementación final:

- `CAPS` abre Google Wallet.
- `STATUS` abre la aplicación de Estadísticas/Salud del Xiaomi Watch 2.
- `RADIO` abre Spotify.
- La solicitud de PIN o desbloqueo de Google Wallet es un comportamiento de seguridad esperado y constituye una validación satisfactoria.
- No se modificó la estética aprobada.

La validación funcional definitiva fue realizada manualmente en Xiaomi Watch 2 y resultó satisfactoria para los tres accesos.

---

## Consolidación visual PIW-SuriOS v1.9

La mejora estética posterior a Sprint 006 quedó completada y validada el 2026-08-08.

- Los textos `CAPS`, `STATUS` y `RADIO` utilizan el color PIPGreen principal.
- Cada acceso incorpora un contorno rectangular fino, sin relleno y del mismo color PIPGreen principal.
- La firma inferior `Suri WatchOS` fue sustituida por `PIW-SuriOS`, abreviatura de **Personal Information Watch**.
- La versión visible se actualizó de `v1.5` a `v1.9`.
- La validación visual fue superada en Wear OS Large Round y Xiaomi Watch 2.

Commit técnico:

`ab85f56806713efec733ea1d9085ad56ad6578b2`

**Actualización estética - PIW-SuriOS v1.9**

Esta mejora no modifica el cierre de Sprint 006 ni inicia Sprint 007.

---

## Continuidad

PIW-SuriOS v1.9 queda oficialmente consolidado como estado visual vigente de SuriOS Watch.

La versión 2.0 permanece reservada para una futura fase de evolución funcional y refinamiento del sistema. No se ha iniciado.

El foco principal de desarrollo puede pasar a PIP-SuriOS.

---

## Cierre de PIP-SuriOS v1.0

Sprint 004 de PIP-SuriOS quedó completado y cerrado el 2026-08-09.

- El módulo MAP incorpora selección TERRAIN/OPERATION, detección y lanzamiento de CivTAK y fallback automático a Google Maps.
- El módulo COMMS incorpora la tabla PMR informativa y desplazable de 16 canales.
- El módulo INVENTORY incorpora categorías, fichas informativas y navegación local completa.
- HOME OPERATION incorpora `CURRENT GEAR` como referencia visual para el siguiente ciclo funcional.
- La identidad visual visible se consolidó como `PIP-SuriOS v1.0`.
- La aplicación conserva la orientación horizontal permanente.
- La validación visual y funcional fue superada en Pixel 8 Emulator y Samsung Galaxy A56.

Commit técnico:

`d55d26c298db5b60d3e96cfb83ce070e9a318129`

**Sprint 004 - Finalización de PIP-SuriOS v1.0**

PIP-SuriOS v1.0 constituye la versión móvil estable vigente para el alcance implementado.

No existe ningún Sprint activo. Sprint 005 de PIP-SuriOS todavía no ha comenzado.

## Planificación informativa posterior

La planificación siguiente no está iniciada. A partir de Sprint 005, el desarrollo se organizará por capacidades funcionales:

1. Sprint 005 — Gestión de equipamiento (`CURRENT GEAR` + `INVENTORY`).
2. Sprint 006 — Integración avanzada con CivTAK.
3. Sprint 007 — DATA / Mission Log.
4. Sprint 008 — STATS.
5. Sprint 009 — COMMS avanzado.
6. Sprint 010 — Optimización visual, animaciones y refinamiento de la experiencia de usuario.

---

## Referencias

- [Sprint 006 v1.0](SPRINT_006_v1.0.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [WFPRD v1.5](../WFPRD/WFPRD_v1.5.md)
- [WATCHFACE_LAYOUT v1.3](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.3.md)
