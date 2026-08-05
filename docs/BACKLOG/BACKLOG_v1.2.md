# BACKLOG v1.2

---
document: BACKLOG
project: SuriOS Ecosystem
version: 1.2
status: Aprobado y vigente
replaces: BACKLOG v1.1
owner: Diego Pérez de Camino
last_updated: 2026-08-05
---

## 1. Control documental

Esta versión sustituye a `BACKLOG v.1.1.md`, que permanece histórica. Se interpreta conforme a [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md) y [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md).

## 2. Propósito

Registrar tareas, deudas, mejoras y propuestas que no forman parte de un Sprint activo. El BACKLOG no autoriza desarrollo, no cambia el roadmap y no sustituye ADR, WFPRD o Sprint.

Solo el propietario puede priorizar, modificar, retirar o trasladar un elemento.

## 3. Estados

| Estado | Significado |
|---|---|
| Pendiente | Registrado, sin planificación. |
| En evaluación | Viabilidad en estudio. |
| Aprobado para planificación | Puede proponerse para un Sprint futuro. |
| Planificado | Incorporado documentalmente a un Sprint, todavía no necesariamente activo. |
| Implementado | Ejecutado y validado. |
| Descartado | No continuará. |

## 4. Prioridades

- **Alta:** importante para continuidad o coherencia.
- **Media:** mejora recomendable.
- **Baja:** mejora futura.
- **Sin prioridad:** idea registrada.

## 5. Elementos vigentes

| ID | Estado | Prioridad | Origen | Elemento | Observaciones |
|---|---|---|---|---|---|
| BL-001 | Pendiente | Alta | Sprint 001 | Validar tipografía definitiva Consolas | Mantener `SYNC_TO_DEVICE` como deuda temporal aceptada. |
| BL-002 | Pendiente | Media | Sprint 001 | Actualizar `preview.png` | Debe representar la esfera validada; requiere Sprint autorizado. |
| BL-003 | En evaluación | Alta | Auditoría documental | Versionar documentación oficial en Git | No se resolverá hasta autorización de staging y commit. |
| BL-004 | Pendiente | Media | Organización | Definir política GitHub | Remoto, ramas y estrategia. |
| BL-005 | Pendiente | Baja | Sprint 001 | Automatizar validaciones XML | Evaluar tras varios Sprints. |
| BL-006 | Pendiente | Baja | Sprint 001 | Estrategia de pruebas automatizadas | No prioritaria en esta fase. |
| BL-007 | Pendiente | Media | Organización | Política de archivos `.idea` | Determinar qué se versiona. |
| BL-008 | Pendiente | Baja | Auditoría documental | Resolver futuro de CHANGELOG | Mantenerlo o sustituirlo formalmente por otro registro. |
| BL-009 | Implementado documentalmente | Media | Auditoría documental | Normalizar nomenclatura vigente | Aplicado a documentos nuevos; histórico sin reescribir. |
| BL-010 | En evaluación | Alta | Auditoría documental | Determinar EDL vigente | Resolver EDL v0.6 frente a respaldo v0.7. |
| BL-011 | Pendiente | Media | Recursos | Localizar y catalogar antigua Figura 4.1 | Es histórica; no bloquea la Figura 1 vigente. |
| BL-012 | Pendiente | Media | Organización | Archivar copias de `ORCA-TO-DO` | No eliminar sin autorización. |
| BL-013 | Pendiente | Baja | Documentación | Normalizar MRPD “Product/Project” | Corrección terminológica futura. |
| BL-014 | Pendiente | Media | Recursos | Definir licencia y recurso del emblema | Obligatorio antes de implementar el emblema. |
| BL-015 | Pendiente | Media | Sprint futuro | Aprobar texto de lema o identificación CIVILIAN | No inventar durante implementación. |
| BL-016 | Pendiente | Baja | Sprint futuro | Evaluar doble pulsación en pasos | Requiere ADR, WFPRD y Sprint propios. |

## 6. Elementos aprobados que no pertenecen al BACKLOG

Los siguientes componentes forman parte de [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md) y no se consideran ideas de BACKLOG:

- día de la semana;
- emblema de la Hermandad del Acero;
- identificación SuriOS Watch;
- lema o identificación del perfil;
- separadores gráficos necesarios.

Sus detalles pendientes se registran en el BACKLOG únicamente cuando necesitan una decisión concreta, como licencia o texto definitivo.

## 7. Incorporación a Sprint

Antes de trasladar un elemento:

1. Debe existir necesidad justificada.
2. La decisión arquitectónica debe estar aprobada si corresponde.
3. El WFPRD debe contener el requisito.
4. Debe existir Sprint versionado.
5. El propietario debe aprobar la planificación.
6. La implementación solo comenzará cuando ACTIVE_SPRINT lo active.

## 8. Elementos retirados

No existen elementos retirados en esta versión. Los registros no se eliminarán sin autorización.

## 9. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Histórica | Creación inicial. |
| 1.1 | Histórica, sustituida | Incorporó origen y elementos retirados. |
| 1.2 | Aprobada y vigente | Registra deudas de la auditoría y distingue componentes aprobados. |
