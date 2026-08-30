# Sprint 019 — Auditoría, seguridad y PIP-SuriOS v2.5

**Fecha de apertura:** 2026-08-30  
**Fecha de cierre:** 2026-08-30  
**Estado:** CLOSED

## Objetivo

Auditar el proyecto completo, revisar vulnerabilidades, retirar P.R.S. //COVER
del Samsung A56, incrementar la firma de versión de PIP-SuriOS en 0,1 y dejar
registrado el estado verificable del sprint.

## Entregables

- Auditoría técnica y de seguridad en [AUDIT_SPRINT_019](../AUDIT_SPRINT_019.md).
- `versionName=2.5` y `versionCode=5` en la aplicación móvil.
- Firmas visibles actualizadas a `PIP-SuriOS v2.5`.
- Copias de seguridad de datos locales desactivadas y excluidas explícitamente.
- P.R.S. //COVER (`com.suri.pipsurios.prs`) desinstalado del A56.
- Se conserva `com.suri.pipsurios` como aplicación principal.
- Estado de TERRAIN, MBTiles HOME/NAVY7 y corrección de parada de audio de RAD
  ZONE documentados como parte del estado funcional auditado.

## Versiones de todos los módulos

| Módulo/producto | Application ID | Versión | Acción en Sprint 019 |
|---|---|---|---|
| PIP-SuriOS full | `com.suri.pipsurios` | `2.5` / code `5` | Actualizada e instalada en A56 |
| P.R.S. compacto | `com.suri.pipsurios.prs` | `2.5-prs` / code `5` | Desinstalada del A56 |
| SuriOS Watch oficial | `com.suri.surioswatch` | `1.1` / code `5` | Sin cambios |
| PROBE-SuriOS watchface | `com.suri.surioswatch.probewatchface` | `2.2` / code `3` | Sin cambios |
| P.R.S. PROBE // WATCH 2 | `com.suri.surioswatch.probe` | `1.0` / code `1` | Sin cambios |
| Protocolo | `com.suri.probeprotocol` | Biblioteca | Sin cambios |

## Riesgos residuales aceptados

- Confianza del Data Layer sin allowlist criptográfica de nodos: riesgo medio
  para un entorno privado de dispositivos emparejados.
- Identificadores BLE y datos de operación locales: riesgo bajo/medio de
  privacidad, mitigado por sandbox y backup desactivado.
- Dependencias con versiones directas pendientes de actualización coordinada.

## Criterio de cierre

El sprint queda cerrado con la matriz de compilación/tests/lint ejecutada, la
APK principal instalada en el A56 con la versión 2.5 y la ausencia verificada
del paquete compacto. `fullDebug` y `prsOnlyDebug` pasan 107 tests cada uno;
Lint termina sin errores bloqueantes.

No se crea un commit Git automático porque el árbol ya contenía cambios de
trabajo previos ajenos al cierre y deben conservarse para su revisión por el
propietario.
