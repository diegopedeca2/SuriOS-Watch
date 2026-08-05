# WFPRD — Indicador de pasos v1.1

---
document: WFPRD especializado
component: Indicador de pasos
project: SuriOS Watch
version: 1.1
status: Aprobado y vigente
implementation_status: Pendiente, Sprint 004
replaces: WFPRD Indicador de pasos v1.0
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Referencias

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
- [WFPRD v1.4](WFPRD_v1.4.md).
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>).
- [ADR-003 v1.0](<../ADR/ADR-003 - Arquitectura de batería y pasos v1.0.md>).
- [WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md).

La versión histórica sustituida se conserva en `WFPRD del Indicador de pasos v1.0.md`.

## 2. Objetivo y alcance

Mostrar el contador diario de pasos proporcionado exclusivamente por el reloj. No usará datos del teléfono, aplicaciones externas u otros dispositivos. No autoriza Sprint 004 ni define la implementación técnica.

## 3. Modo activo

Se mostrará únicamente el número completo de pasos. No se mostrarán:

- porcentaje;
- objetivo diario;
- barra de progreso;
- icono;
- mensajes adicionales.

El número utilizará un espacio como separador de miles, por ejemplo `12 345`.

## 4. Ambient Mode

- Sprint 003: permanece oculto porque aún no está implementado.
- Desde Sprint 004: se muestra únicamente el número completo.
- Sin icono, barra, porcentaje, objetivo o mensaje.

## 5. Representación visual

- Posición y dimensiones: WATCHFACE_LAYOUT v1.2.
- Simetría respecto a batería.
- PipGreen/PipGreenDim según jerarquía.
- Sin cambio de color por cantidad.
- Tipografía oficial o excepción temporal vigente.
- Geometría pendiente de validación física durante Sprint 004.

La representación numérica aislada constituye una excepción específica aprobada a la regla genérica del EDL que combina gráfico y valor. No se añadirá representación gráfica sin nueva aprobación.

## 6. Actualización

El dato se actualizará cuando el sistema registre un nuevo paso. No se realizarán actualizaciones periódicas innecesarias.

## 7. Interacción

La versión 1.1 no tendrá interacción. La posible apertura futura de estadísticas mediante doble pulsación permanece fuera de alcance y requerirá ADR, WFPRD y Sprint específicos.

## 8. Casos límite

- 0 pasos: mostrar `0`.
- Valores con miles: usar espacio.
- Dato no disponible: no inventar valor; documentar la limitación antes de decidir.
- Cambio de día: reflejar el valor proporcionado por el sistema.

## 9. Restricciones

- No usar datos externos al reloj.
- No añadir barra o icono.
- No añadir interacción.
- No cambiar color por cantidad.
- No modificar geometría sin aprobación.
- No implementar antes de Sprint 004 activo.

## 10. Criterios de aceptación

- fuente correcta;
- número correcto y completo;
- formato de miles correcto;
- sin barra ni icono;
- sin interacción;
- simetría y geometría aprobadas;
- Ambient Mode conforme a la fase;
- validación en emulador y Xiaomi Watch 2;
- ausencia de regresiones.

## 11. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Histórica, sustituida | Primera definición detallada. |
| 1.1 | Aprobada y vigente | Consolida ausencia de barra y Ambient Mode por fases. |
