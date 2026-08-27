# Sprint 003 — Implementación de Ambient Mode

---
document: SPRINT
sprint: 003
version: 1.1
project: SuriOS Watch
type: Funcional
document_status: Aprobado
implementation_status: Completado
priority: Alta
replaces: Sprint 003 v1.0
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Control documental

Esta versión sustituye a `SPRINT-003 v1.0.md`, que permanece histórica. La implementación fue completada y validada el 2026-08-05. [ACTIVE_SPRINT](ACTIVE_SPRINT.md) confirma que no existe ningún Sprint activo tras su cierre.

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

## 7. Archivo modificado

La implementación modificó exclusivamente:

- `watch/watchface/src/main/res/raw/watchface.xml`.

No se añadieron código Kotlin o Java, servicios, dependencias, permisos, recursos alternativos ni configuraciones específicas por dispositivo.

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

## 12. Cierre técnico

Sprint 003 está completado. La implementación quedó registrada en el commit técnico:

- `bc35866153d43442dc896a4bd9370e0f5cf29f4c` — `Sprint 003 - Ambient Mode`.

Ambient Mode se implementó mediante elementos `Variant` compatibles con WFF v1. La representación activa y la representación ambiente permanecen separadas, mientras el fondo PipBlack es compartido. El modo activo conserva íntegramente sus posiciones, dimensiones, formatos, alineaciones, colores y tipografía temporal autorizada.

Validaciones superadas:

| Validación | Resultado |
|---|---|
| Compilación Gradle | Correcta para `:watchface` y para `assembleDebug` conjunto. |
| Android Studio | Correcto; módulo y configuración reconocidos desde el build raíz. |
| Wear OS Large Round | Modo activo y Ambient Mode correctos; estado ambiente confirmado mediante Logcat. |
| Xiaomi Watch 2 | Instalación y validación física correctas. |
| Transición | Entrada y salida de Ambient Mode correctas. |
| Actualización | Hora y fecha actualizadas correctamente en ambiente. |
| Regresión | Sin regresiones funcionales, visuales o de geometría. |

## 13. Continuidad

Sprint 004 incorporará batería y pasos tanto al modo activo como, mediante sus representaciones simplificadas, a Ambient Mode.

## 14. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Histórica, sustituida | Primera definición aprobada. |
| 1.1 | Aprobada; implementación completada | Alinea el alcance y registra implementación, validaciones y cierre técnico. |
