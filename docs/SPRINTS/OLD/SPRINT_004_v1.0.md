# Sprint 004 — Indicadores de batería y pasos

---
document: SPRINT
sprint: 004
version: 1.0
project: SuriOS Watch
type: Funcional
document_status: Aprobado
implementation_status: Completado
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-06
---

## 1. Control documental

Primera versión del documento operativo de Sprint 004. La implementación fue completada y validada el 2026-08-06. [ACTIVE_SPRINT](ACTIVE_SPRINT.md) confirma que no existe ningún Sprint activo tras su cierre.

Referencias obligatorias:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
- [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md).
- [WFPRD Battery Indicator v1.2](../WFPRD/WFPRD_BATTERY_INDICATOR_v1.2.md).
- [WFPRD Step Indicator v1.1](../WFPRD/WFPRD_STEP_INDICATOR_v1.1.md).
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>).
- [ADR-003 v1.0](<../ADR/ADR-003 - Arquitectura de batería y pasos v1.0.md>).
- [EDL v0.6](../EDL/EDL.md).
- [WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md).

## 2. Objetivo

Incorporar los indicadores de batería y pasos a SuriOS Watch mediante Watch Face Format v1, tanto en modo activo como en sus representaciones aprobadas para Ambient Mode.

## 3. Implementación completada

La implementación es íntegramente declarativa mediante WFF v1 y modifica exclusivamente `watch/watchface/src/main/res/raw/watchface.xml`.

Incluye:

- indicador horizontal de batería con diez niveles;
- porcentaje entero de batería;
- estado `RECHARGING`, que sustituye al icono y al porcentaje durante la carga;
- indicador de pasos sin icono ni barra;
- representación de pasos por cuatro rangos:
  - `0` → `--`;
  - `1–999` → número sin separador;
  - `1 000–999 999` → número con espacio como separador de millares;
  - valores superiores a `999 999` → `999 999+`;
- integración en Ambient Mode con porcentaje de batería y pasos, sin icono de batería.

No se introdujeron código Kotlin o Java, servicios, dependencias, permisos, recursos gráficos adicionales ni animaciones.

## 4. Comportamiento por modo

### Modo activo

- hora y fecha conservadas sin cambios;
- batería con icono de diez niveles y porcentaje;
- `RECHARGING` durante la carga;
- número de pasos conforme a los cuatro rangos aprobados.

### Ambient Mode

- hora y fecha;
- porcentaje de batería, sin icono ni barra;
- pasos, sin icono ni barra;
- `RECHARGING` sustituye al porcentaje cuando la representación ambiente de SuriOS Watch permanece visible durante la carga.

## 5. Archivo modificado

- `watch/watchface/src/main/res/raw/watchface.xml`.

Permanecieron sin cambios funcionales `app/**`, Gradle, manifiestos, recursos gráficos, documentación y el resto de `watch/watchface/**`.

## 6. Validaciones superadas

| Validación | Resultado |
|---|---|
| Gradle CLI | Compilaciones individual, incremental y conjunta correctas. |
| Android Studio | Sincronización e importación correctas. |
| Wear OS Large Round | Batería, pasos, Ambient Mode y transiciones validados. |
| Xiaomi Watch 2 | Instalación y validación física correctas. |
| Batería | Porcentaje y diez niveles coherentes con el estado del dispositivo. |
| Pasos | Incremento y actualización correctos; caso cero representado como `--`. |
| RECHARGING | Sustitución correcta de icono y porcentaje en modo activo. |
| Ambient Mode | Hora, fecha, pasos y porcentaje correctos; icono de batería ausente. |
| Regresión | Sin regresiones funcionales, visuales o de geometría. |

## 7. Observación no bloqueante

Wear OS puede mostrar su propia pantalla o esfera de carga al entrar en Ambient Mode mientras el reloj está conectado al cargador. Este comportamiento se observó tanto en el emulador Wear OS Large Round como en el Xiaomi Watch 2 y no constituye un fallo de SuriOS Watch.

La sustitución por la interfaz del sistema puede impedir observar directamente `RECHARGING` en ambiente, pero no afecta a su implementación declarativa ni al funcionamiento normal de la esfera.

## 8. Cierre técnico

Sprint 004 está completado. La implementación quedó registrada en el commit técnico:

- `0e1a73f339889d874ed27089832b6b5d8fc64e0a` — `Sprint 004 - Batería y pasos`.

No se detectaron regresiones funcionales. Hora y fecha conservan íntegramente su geometría, formato, color y tipografía temporal autorizada.

## 9. Continuidad

Sprint 005 es el siguiente Sprint previsto en el roadmap y abordará Spotify y Google Wallet. Todavía no está autorizado.

## 10. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Aprobada; implementación completada | Registra alcance, implementación, validaciones y cierre técnico de Sprint 004. |
