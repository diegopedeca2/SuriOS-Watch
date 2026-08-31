# SuriOS Ecosystem — Project Guide v1.1

---
document: PROJECT_GUIDE
project: SuriOS Ecosystem
version: 1.1
status: Aprobado y vigente
replaces: PROJECT_GUIDE v1.0
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Propósito

Este documento define la metodología documental y de desarrollo aplicable a todos los proyectos del ecosistema SuriOS. No sustituye los requisitos de producto ni autoriza por sí mismo implementaciones.

La versión 1.1 sustituye a `PROJECT_GUIDE v1.0`, que permanece como versión histórica en [PROJECT_GUIDE.md](PROJECT_GUIDE.md).

## 2. Fuentes documentales oficiales

### 2.1 EDL

El [EDL v0.6](../EDL/EDL.md) gobierna la identidad visual, los componentes comunes, la interacción y las reglas compartidas del ecosistema. La revisión de la copia de respaldo v0.7 permanece pendiente y no modifica por sí sola la versión vigente.

### 2.2 MRPD

El [MRPD v1.1.1](../MRPD/MRPD.md) es la fuente de verdad de PIP-SuriOS y de las decisiones compartidas que declare expresamente.

### 2.3 WFPRD maestro

El [WFPRD v1.5](../WFPRD/WFPRD_v1.5.md) es la fuente de verdad vigente de SuriOS Watch. Las versiones anteriores se conservan como históricas.

### 2.4 ADR

Un Architecture Decision Record documenta una decisión arquitectónica, sus alternativas, consecuencias y criterios de revisión. Un ADR:

- no sustituye al EDL ni al WFPRD;
- no autoriza implementación sin un Sprint aprobado y activo;
- no define por sí solo el diseño concreto de un componente;
- permanece vigente hasta que otra versión o ADR lo sustituya expresamente.

ADR vigentes relacionados con SuriOS Watch:

- [ADR-001 v1.2](../ADR/ADR_001_v1.2.md): arquitectura Gradle multiproyecto.
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>): Ambient Mode.
- [ADR-003 v1.0](<../ADR/ADR-003 - Arquitectura de batería y pasos v1.0.md>): batería y pasos.

### 2.5 WFPRD especializados

Los WFPRD especializados concretan el comportamiento de un componente aprobado por el WFPRD maestro. Deben ser compatibles con él y no pueden ampliar el producto por sí solos.

- [Ambient Mode v1.1](../WFPRD/WFPRD_AMBIENT_MODE_v1.1.md).
- [Indicador de batería v1.2](../WFPRD/WFPRD_BATTERY_INDICATOR_v1.2.md).
- [Indicador de pasos v1.1](../WFPRD/WFPRD_STEP_INDICATOR_v1.1.md).

### 2.6 WATCHFACE_LAYOUT

[WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md) es un anexo visual normativo subordinado al WFPRD maestro. Define geometría, composición y relaciones espaciales, pero no puede:

- añadir requisitos funcionales;
- contradecir el WFPRD maestro;
- contradecir un WFPRD especializado en el comportamiento de su componente;
- autorizar una implementación.

### 2.7 Sprint

Un Sprint aprobado define el trabajo que podrá activarse mediante autorización expresa. La aprobación documental y la activación son estados independientes.

- [ACTIVE_SPRINT](../SPRINTS/ACTIVE_SPRINT.md) identifica el Sprint operativo vigente.
- [SPRINT_HISTORY](../SPRINTS/SPRINT_HISTORY.md) mantiene el historial acumulativo.
- [Sprint 002 v1.2](../SPRINTS/SPRINT_002_v1.2.md) y [Sprint 003 v1.1](../SPRINTS/SPRINT_003_v1.1.md) están aprobados documentalmente, pero pendientes de implementación.

### 2.8 BACKLOG

El [BACKLOG v1.3](../BACKLOG/BACKLOG_v1.3.md) registra trabajo potencial, deudas e ideas. Ningún elemento del BACKLOG autoriza desarrollo.

### 2.9 RELEASE_NOTES

Las [RELEASE_NOTES v1.1](../RELEASE_NOTES/RELEASE_NOTES_v1.1.md) describen exclusivamente capacidades publicadas y perceptibles para el usuario. No sustituyen a Sprint, ADR, WFPRD, Git ni historial técnico.

## 3. Jerarquía documental

La jerarquía normativa es:

1. PROJECT_GUIDE vigente.
2. EDL vigente.
3. WFPRD maestro vigente.
4. ADR aplicable, WFPRD especializado y WATCHFACE_LAYOUT, cada uno dentro de su competencia.
5. Sprint aprobado y activado.
6. Implementación.
7. Pruebas.
8. Commit autorizado.

Si dos documentos del nivel 4 entran en conflicto, prevalece el WFPRD maestro y la implementación se detendrá hasta documentar una resolución aprobada.

## 4. Fuente de verdad y formato

- Markdown es la fuente documental editable.
- DOCX y PDF son derivados o copias de seguridad.
- Cada referencia normativa debe indicar nombre, versión y enlace relativo exacto.
- No se utilizarán referencias genéricas capaces de resolver a varias versiones.
- Los documentos históricos no se reescribirán para aparentar que una decisión posterior ya existía.

## 5. Flujo documental

1. Idea.
2. Discusión.
3. Propuesta.
4. Aprobación expresa del propietario.
5. Actualización del documento normativo correspondiente.
6. Creación o actualización del Sprint.
7. Activación expresa del Sprint.
8. Implementación.
9. Pruebas.
10. Aprobación del resultado.
11. Commit autorizado.

## 6. Reglas de desarrollo

- Documentación antes que código.
- No implementar requisitos pendientes o ausentes.
- No modificar diseño, arquitectura, terminología o prioridades sin aprobación.
- Trabajar únicamente dentro del Sprint activo.
- Una limitación técnica se documenta; no autoriza cambios automáticos.
- No realizar commits sin autorización expresa.
- No mezclar funcionalidades independientes.

## 7. Roles

### Propietario

Diego Pérez de Camino conserva la autoridad final sobre visión, prioridades, diseño, excepciones, Sprints, versiones, aceptación y commits.

### Asistentes de IA

ChatGPT, Codex, Orca y otros asistentes pueden analizar, documentar, implementar o validar únicamente dentro del alcance autorizado. Deben diferenciar siempre propuesta, aprobación e implementación.

## 8. Versionado documental

- Toda nueva versión normativa será completa y autocontenida.
- Una versión aprobada no se sobrescribirá con cambios de contenido.
- La nueva versión identificará expresamente qué versión sustituye.
- Las versiones anteriores se conservarán como históricas o sustituidas.
- Los documentos operativos `ACTIVE_SPRINT.md` y `SPRINT_HISTORY.md` son acumulativos y mutables; antes de cambios estructurales se conservará la versión anterior en `docs/SPRINTS/OLD/`.
- El nombre canónico de un documento versionado incluirá `_v<major>.<minor>`.
- Los identificadores de Sprint usarán tres dígitos: `SPRINT_002_v1.2.md`.

## 9. Definition of Done

Un Sprint solo estará terminado cuando:

- todo su alcance esté implementado;
- compile sin errores bloqueantes;
- se hayan ejecutado las validaciones previstas;
- no existan cambios ajenos;
- las deudas o excepciones estén documentadas;
- el propietario apruebe el resultado;
- exista autorización para el commit;
- el commit estable se haya creado.

## 10. Estructura documental vigente

```text
docs/
├── ADR/
├── BACKLOG/
├── EDL/
├── MRPD/
├── PROJECT_GUIDE/
├── RELEASE_NOTES/
├── SPRINTS/
├── WATCHFACE_LAYOUT/
└── WFPRD/
```

## 11. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Histórica, sustituida | Metodología inicial del ecosistema. |
| 1.1 | Aprobada y vigente | Incorpora ADR, WFPRD especializados, WATCHFACE_LAYOUT, BACKLOG, RELEASE_NOTES y versionado explícito. |

## 12. Estado

**Aprobado y vigente.**
