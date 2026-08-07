# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canónico
last_updated: 2026-08-07

---

## Sprint activo

**Ninguno**

---

## Estado

Sprint 005 v1.2 está completado y cerrado técnica y documentalmente.

No existe ningún Sprint activo ni autorizado para implementación.

Sprint 006 queda reservado para la futura integración de Spotify y Google Wallet.

Sprint 006 permanece en estado **Pendiente** y no dispone todavía de autorización de implementación.

No deberá iniciarse sin autorización expresa del propietario.

---

## Proyecto

Ecosistema SuriOS:

- PIP-SuriOS
- SuriOS Watch

---

## Último Sprint completado

[Sprint 005 v1.2](SPRINT_005_v1.2.md), finalizado el 2026-08-07.

Commit técnico:

`a7ba0f5d3798815ace4d5877c211c0f7b884b1f3`

**Sprint 005 - Emblema oficial**

---

## Siguiente Sprint previsto

**Sprint 006 — Integración de Spotify y Google Wallet**

Objetivo:

Integrar Spotify y Google Wallet en SuriOS Watch.

Sprint 006 permanece pendiente y no autorizado. Esta previsión no activa el Sprint ni autoriza implementación.

La incidencia independiente del contador de pasos quedó resuelta y cerrada el 2026-08-07.

La causa estaba en la estructura declarativa de `Condition` solapados, no en Xiaomi Health Services ni en `[STEP_COUNT]`. La solución utiliza un único `PartText` dinámico por modo y fue validada físicamente en Xiaomi Watch 2, incluidos dos incrementos consecutivos y la transición activo ↔ ambiente.

Commit técnico de la corrección:

`1b8218df318a56bc17822b560f3c4dd4d0f6f603`

**Fix - Actualización del contador de pasos**

---

## Mejora estética posterior

La firma inferior de SuriOS Watch quedó actualizada y validada el 2026-08-07.

- Se eliminó el texto `BROTHERHOOD OF STEEL`.
- Se sustituyó `v1.0` por `v1.5`.
- El bloque formado por `Suri WatchOS` y `v1.5` se reposicionó hacia el borde inferior de la esfera.
- La validación visual fue superada en Wear OS Large Round y Xiaomi Watch 2.

Commit técnico:

`807e8e122c3eab93a8d783380068c391615d61b2`

**Actualización estética - Firma inferior Suri WatchOS v1.5**

Esta mejora no altera el cierre de Sprint 005, no activa un nuevo Sprint y no autoriza Sprint 006.

### Referencias visuales CAPS, STATUS y RADIO

La referencia estética de los accesos visuales quedó completada y validada el 2026-08-08.

- Se incorporó la referencia visual `CAPS`.
- Se incorporó la referencia visual `STATUS`.
- Se incorporó la referencia visual `RADIO`.
- Las tres etiquetas quedaron alineadas horizontalmente sobre una misma coordenada Y.
- La validación visual fue superada en el emulador Wear OS Large Round y en Xiaomi Watch 2.

Commit técnico:

`566aa60b395423d502e6fdf99a243bfa4b5a5d14`

**Actualización estética - Alineación de accesos CAPS, STATUS y RADIO**

Esta referencia visual no inicia Sprint 006 ni autoriza la implementación funcional de los accesos.

---

## Referencias

- PROJECT_GUIDE v1.1
- SPRINT_HISTORY
- ADR-001 v1.2
- ADR-002 v1.0
- ADR-003 v1.0
- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3
- WFPRD_BROTHERHOOD_EMBLEM v1.3
