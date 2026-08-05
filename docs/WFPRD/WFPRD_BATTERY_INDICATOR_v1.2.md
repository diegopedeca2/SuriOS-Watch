# WFPRD — Indicador de batería v1.2

---
document: WFPRD especializado
component: Indicador de batería
project: SuriOS Watch
version: 1.2
status: Aprobado y vigente
implementation_status: Pendiente, Sprint 004
replaces: WFPRD Indicador de batería v1.1
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Referencias

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
- [WFPRD v1.4](WFPRD_v1.4.md).
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>).
- [ADR-003 v1.0](<../ADR/ADR-003 - Arquitectura de batería y pasos v1.0.md>).
- [WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md).

La versión histórica sustituida se conserva en `WFPRD del indicador de batería v1.1.md`.

## 2. Objetivo y alcance

Mostrar exclusivamente la batería del reloj. No mostrará batería del teléfono, auriculares u otros dispositivos. No define implementación técnica ni autoriza Sprint 004.

## 3. Modo activo

Se mostrarán:

- icono de batería con diez niveles gráficos;
- porcentaje entero con precisión de 1 %.

No se mostrará barra de progreso o estado. El icono es apoyo visual y el porcentaje es la lectura precisa.

## 4. Diez niveles

| Nivel | Intervalo |
|---:|---|
| 10 | 100–91 % |
| 9 | 90–81 % |
| 8 | 80–71 % |
| 7 | 70–61 % |
| 6 | 60–51 % |
| 5 | 50–41 % |
| 4 | 40–31 % |
| 3 | 30–21 % |
| 2 | 20–11 % |
| 1 | 10–1 % |
| 0 | 0 % |

El porcentaje se actualizará en incrementos de 1 %. El icono cambiará al cruzar el intervalo correspondiente.

## 5. Estado de carga

### Modo activo

`RECHARGING` sustituye inmediatamente icono y porcentaje mientras el reloj carga. Al finalizar, reaparecen icono y porcentaje actualizados.

### Ambient Mode desde Sprint 004

`RECHARGING` sustituye el porcentaje. No se mostrará iconografía.

## 6. Ambient Mode

- Sprint 003: el componente permanece oculto porque aún no está implementado.
- Desde Sprint 004: se muestra únicamente el porcentaje.
- Sin icono, barra, color alternativo o elementos adicionales.

## 7. Representación visual

- Posición y dimensiones: WATCHFACE_LAYOUT v1.2.
- Simetría respecto a pasos.
- PipGreen/PipGreenDim según jerarquía aprobada.
- Sin cambio de color por nivel.
- Tipografía oficial o excepción temporal vigente.
- Geometría pendiente de validación física durante Sprint 004.

## 8. Interacción

No tendrá interacción.

## 9. Actualización

El indicador se actualizará cuando:

- cambie el porcentaje;
- cambie el intervalo gráfico;
- comience la carga;
- finalice la carga.

No realizará actualizaciones periódicas innecesarias.

## 10. Casos límite

- 100 %: porcentaje `100%` e icono lleno.
- 1–99 %: porcentaje exacto e intervalo correspondiente.
- 0 %: `0%` e icono vacío.
- Dato no disponible: no se inventará estado alternativo; se documentará la limitación técnica antes de decidir.

## 11. Restricciones

- No mostrar baterías externas.
- No añadir barra.
- No añadir interacción.
- No cambiar colores por nivel.
- No modificar geometría sin aprobación.
- No implementar antes de Sprint 004 activo.

## 12. Criterios de aceptación

- fuente de datos correcta;
- porcentaje exacto;
- diez niveles correctos;
- sin barra;
- carga activa y ambiente correctas;
- simetría y geometría aprobadas;
- Ambient Mode conforme a la fase;
- validación en emulador y Xiaomi Watch 2;
- ausencia de regresiones.

## 13. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.1 | Histórica, sustituida | Primera definición detallada. |
| 1.2 | Aprobada y vigente | Elimina contradicción de barra y define Ambient Mode por fases y carga. |
