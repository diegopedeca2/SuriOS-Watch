# Sprint 003 — Implementación de Ambient Mode

---
document: SPRINT
sprint: 003
version: 1.1
project: SuriOS Watch
type: Funcional
document_status: Aprobado
implementation_status: Pendiente; no activo
priority: Alta
replaces: Sprint 003 v1.0
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Control documental

Esta versión sustituye a `SPRINT-003 v1.0.md`, que permanece histórica. No puede activarse antes de completar y aprobar Sprint 002 ni sin autorización expresa reflejada en [ACTIVE_SPRINT](ACTIVE_SPRINT.md).

Referencias obligatorias:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
- [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md).
- [WFPRD Ambient Mode v1.1](../WFPRD/WFPRD_AMBIENT_MODE_v1.1.md).
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>).
- [EDL v0.6](../EDL/EDL.md).
- [WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md).

## 2. Objetivo

Implementar exclusivamente Ambient Mode con hora y fecha, conforme a ADR-002 y WFPRD Ambient Mode v1.1.

No modifica distribución, añade componentes ni altera la identidad del modo activo.

## 3. Justificación

Ambient Mode prepara la esfera para uso continuado y establece la representación energética base antes de incorporar los indicadores de Sprint 004.

## 4. Alcance incluido

- representación ambiente de hora;
- representación ambiente de fecha;
- configuración WFF necesaria para cambiar entre estados;
- conservación exacta de geometría;
- validación de modo activo y ambiente;
- tratamiento de color aprobado durante el Sprint dentro de la paleta EDL.

## 5. Fuera de alcance

- batería;
- pasos;
- Spotify;
- Google Wallet;
- día de la semana;
- emblema;
- identificación SuriOS o perfil;
- separadores;
- reorganización Gradle;
- optimizaciones generales;
- nuevos recursos gráficos.

Los indicadores se incorporarán al Ambient Mode durante Sprint 004, no durante este Sprint.

## 6. Restricciones

- No modificar hora o fecha del modo activo.
- No modificar sus coordenadas, tamaños, alineaciones o márgenes.
- No cambiar tipografía.
- No introducir colores fuera del EDL.
- No mostrar componentes distintos de hora y fecha.
- No introducir interacción o animaciones decorativas.
- No modificar arquitectura aprobada en ADR-002.
- No realizar commit sin autorización.

## 7. Archivos previsiblemente afectados

Durante la implementación futura autorizada:

- configuración WFF estrictamente relacionada con Ambient Mode;
- `watchface.xml` o ruta equivalente tras Sprint 002;
- únicamente archivos imprescindibles para el cambio de estado.

## 8. Criterios de aceptación

- Ambient Mode muestra hora y fecha únicamente;
- geometría idéntica al modo activo;
- transición estable;
- identidad visual conservada;
- ausencia de errores de representación;
- modo activo sin cambios;
- ausencia de regresiones respecto a Sprint 001 y 002;
- compilación e instalación correctas;
- validación en emulador;
- validación satisfactoria en Xiaomi Watch 2;
- aprobación del propietario.

## 9. Validaciones

- compilación limpia e incremental;
- modo activo;
- Ambient Mode;
- transición entre modos;
- comparación con Sprint 001;
- inspección visual;
- validación física;
- consumo observado;
- ausencia de regresiones;
- revisión Git.

## 10. Riesgos y mitigación

| Riesgo | Mitigación |
|---|---|
| Representación incorrecta | Validación continua en emulador. |
| Diferencia en dispositivo | Validación física obligatoria. |
| Consumo elevado | Limitar actualizaciones y elementos. |
| Cambio accidental en activo | Comparación con Sprint 001. |
| Limitación WFF | Detener, documentar y proponer alternativas. |

## 11. Entregables

- Ambient Mode con hora y fecha;
- compilación e instalación satisfactorias;
- validación en emulador y dispositivo;
- documentación mínima del resultado;
- propuesta de commit.

## 12. Definition of Done

Sprint 003 estará terminado cuando:

- cumpla todos los criterios;
- no existan regresiones;
- modo activo permanezca igual;
- Ambient Mode funcione correctamente;
- el propietario apruebe el resultado;
- se autorice y cree un commit estable.

## 13. Continuidad

Sprint 004 incorporará batería y pasos tanto al modo activo como, mediante sus representaciones simplificadas, a Ambient Mode.

## 14. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Histórica, sustituida | Primera definición aprobada. |
| 1.1 | Aprobada, pendiente de activación | Alinea alcance con WFPRD v1.4 y Ambient Mode por fases. |
