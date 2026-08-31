# Sprint 026 — Esfera PIW-SuriOS no PROBE: STATUS y AMBIENT

---

document: SPRINT
project: SuriOS Watch
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-08-31
predecessor: Sprint 025

---

## Estado

El Sprint 026 ha sido activado por autorización expresa del propietario. Su
ámbito es exclusivamente la esfera oficial no PROBE del módulo `watchface`,
paquete `com.suri.surioswatch`. PROBE (`watchfacev2`) queda fuera de alcance.

El Sprint 026 queda cerrado tras completar los cambios aprobados, retirar la
propuesta descartada de AMBIENT, validar la esfera en el Watch Ultra y ejecutar
los gates técnicos del repositorio.

La ejecución se realizará en el Samsung Galaxy Watch Ultra, comprobando cada
punto de forma independiente antes de comenzar el siguiente.

## Objetivo

Actualizar la esfera PIW-SuriOS oficial para que sus accesos y estados visuales
se correspondan con el funcionamiento real del Watch Ultra, manteniendo
separada la esfera PROBE.

## Hoja de ruta

### 1. STATUS — Samsung Health

- Sustituir el destino histórico de PIP-SuriOS por la aplicación equivalente
  instalada en el Watch Ultra.
- Usar el componente completo
  `com.samsung.android.wear.shealth/com.samsung.android.wear.shealth.app.home.HomeActivity`.
- Construir, instalar y validar físicamente que `STATUS` abre Samsung Health.

**Estado:** implementado y validado físicamente en el Watch Ultra.

### 2. AMBIENT — propuesta descartada

- La propuesta de sustituir el contenido AMBIENT por el emblema brillante y
  animado queda descartada por decisión expresa del propietario.
- Se restaura y conserva el comportamiento AMBIENT anterior de la esfera.
- No quedan cambios de código asociados a esta propuesta en el producto.

**Estado:** descartado; sin cambio funcional.

## Criterios de aceptación

- `STATUS` abre Samsung Health en el Watch Ultra.
- AMBIENT conserva el comportamiento anterior tras retirar la propuesta
  descartada.
- La esfera activa conserva hora, fecha, indicadores y accesos sin regresiones.
- `:watchface:assembleDebug` y el gate final del repositorio terminan sin
  errores.
- `git diff --check` queda limpio.
- La validación física de los dos puntos queda completada.

## Fuera de alcance

- Cualquier cambio en `watchfacev2`/PROBE.
- Cambios en PIP-SuriOS, P.R.S., TERRAIN o Data Layer.
- Copia de assets o de la implementación visual de Samsung.

## Registro de ejecución

| Punto | Resultado | Evidencia |
|---|---|---|
| STATUS | Validado | Samsung Health visible tras pulsar `STATUS` en Watch Ultra |
| AMBIENT | Descartado | Se restauró el XML anterior; no queda cambio funcional |

## Resultado técnico

- `test`, `lint` y `assemble`: correctos en el proyecto completo.
- `:watchface:assembleDebug` y `:watchface:lintDebug`: correctos.
- `git diff --check`: correcto.
- La APK final de la esfera quedó instalada en el Watch Ultra.

**Cierre:** Sprint 026 cerrado y sin Sprint activo.
