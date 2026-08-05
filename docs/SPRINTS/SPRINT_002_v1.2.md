# Sprint 002 — Migración a un único build Gradle multiproyecto

---
document: SPRINT
sprint: 002
version: 1.2
project: SuriOS Ecosystem
affected_products: PIP-SuriOS, SuriOS Watch
type: Infraestructura
document_status: Aprobado
implementation_status: Pendiente; no activo
priority: Alta
replaces: Sprint 002 v1.1
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Control documental

Esta versión sustituye a `SPRINT-002 v1.1.md`, que permanece histórica. La aprobación documental no activa el Sprint. Solo [ACTIVE_SPRINT](ACTIVE_SPRINT.md) puede identificar trabajo activo tras autorización expresa.

Referencias obligatorias:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
- [ADR-001 v1.2](../ADR/ADR_001_v1.2.md).
- [EDL v0.6](../EDL/EDL.md).
- [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md).

## 2. Objetivo

Migrar la estructura desde dos builds Gradle independientes a un único build Gradle multiproyecto, sin modificar el comportamiento funcional o visual de PIP-SuriOS ni SuriOS Watch.

Este Sprint es exclusivamente técnico. No introduce funcionalidad, diseño o requisitos de producto.

## 3. Justificación

El repositorio mantiene dos wrappers, catálogos y configuraciones raíz. ADR-001 v1.2 aprueba unificar la infraestructura conservando módulos funcionalmente independientes.

## 4. Alcance incluido

- Crear un único build Gradle.
- Integrar ambos módulos desde una raíz.
- Compartir Wrapper, catálogo y configuración raíz.
- Mantener independencia funcional.
- Definir las rutas técnicas finales.
- Verificar compilación individual y conjunta.
- Verificar apertura desde Android Studio.
- Documentar los comandos mínimos de compilación.

## 5. Fuera de alcance

- cambios funcionales o visuales;
- Ambient Mode;
- batería y pasos;
- Spotify y Wallet;
- optimización funcional;
- cambios en `watchface.xml`;
- recursos gráficos;
- actualización de `preview.png`;
- cambios documentales ajenos al cierre de la migración;
- actualización indiscriminada de dependencias.

## 6. Restricciones

- Respetar íntegramente ADR-001 v1.2.
- No modificar el resultado validado de Sprint 001.
- No alterar colores, posiciones, tamaños o tipografía.
- No reescribir historial Git.
- No eliminar configuraciones duplicadas hasta validar la alternativa única.
- Mantener operaciones reversibles.
- No realizar commit sin autorización.

## 7. Archivos previsiblemente afectados

Únicamente durante la futura implementación autorizada:

- `settings.gradle.kts`;
- `build.gradle.kts` raíz y de módulos;
- `gradle.properties`;
- `gradle/`;
- `gradlew` y `gradlew.bat`;
- catálogos de versiones;
- `.gitignore`;
- rutas estrictamente necesarias para integrar módulos.

La lista no autoriza cambios fuera del Sprint activo.

## 8. Criterios de aceptación

- un único build Gradle;
- un único Wrapper;
- un único `settings.gradle.kts` raíz;
- un catálogo principal;
- Android Studio abre ambos módulos desde la raíz;
- PIP-SuriOS compila;
- SuriOS Watch compila;
- compilación conjunta correcta;
- APK válido cuando proceda;
- Watch Face idéntica a Sprint 001;
- ausencia de cambios funcionales y visuales;
- estructura conforme a ADR-001 v1.2;
- revisión Git sin cambios ajenos;
- aprobación del propietario.

## 9. Validaciones

- sincronización Gradle;
- compilación limpia de ambos módulos;
- compilación incremental;
- compilación conjunta;
- generación e instalación de Watch Face;
- comprobación visual;
- comparación con Sprint 001;
- ausencia de regresiones;
- revisión de rutas;
- revisión de `git status` y diff.

## 10. Riesgos y mitigación

| Riesgo | Mitigación |
|---|---|
| Pérdida de configuración | Punto estable previo y reversibilidad. |
| Rutas rotas | Validación individual y conjunta. |
| Dependencias divergentes | Conservar versiones validadas. |
| Cambios accidentales | Limitar archivos y revisar diff. |
| Historial Git afectado | No reescribir historial. |
| Configuración IDE local | Validar desde raíz Gradle. |

## 11. Entregables

- proyecto reorganizado;
- build único;
- compilación satisfactoria de ambos módulos;
- validación de ausencia de regresiones;
- documentación técnica mínima;
- propuesta de commit reversible.

## 12. Definition of Done

Sprint 002 estará terminado cuando:

- todos los criterios sean correctos;
- no existan regresiones;
- se hayan ejecutado las validaciones;
- no existan cambios ajenos;
- el propietario apruebe el resultado;
- se autorice y cree un commit estable específico.

## 13. Continuidad

El siguiente Sprint previsto es [Sprint 003 v1.1](SPRINT_003_v1.1.md), dedicado exclusivamente a Ambient Mode.

## 14. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.1 | Histórica, sustituida | Primera definición aprobada de la migración. |
| 1.2 | Aprobada, pendiente de activación | Normaliza referencias, proyecto afectado y jerarquía documental. |
