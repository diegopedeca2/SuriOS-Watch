# BACKLOG v1.3

---
document: BACKLOG
project: SuriOS Ecosystem
version: 1.3
status: Aprobado y vigente
replaces: BACKLOG v1.2
owner: Diego Pérez de Camino
last_updated: 2026-08-31
---

Esta versión incorpora al backlog los hallazgos AUD-023-01 a AUD-023-09 de la
auditoría del Sprint 023. Se interpreta conforme a [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md) y no autoriza por sí misma desarrollo.

## Elementos heredados

| ID | Estado | Prioridad | Origen | Elemento | Observaciones |
|---|---|---|---|---|---|
| BL-001 | Pendiente | Alta | Sprint 001 | Validar tipografía definitiva Consolas | Mantener `SYNC_TO_DEVICE` como deuda temporal aceptada. |
| BL-002 | Pendiente | Media | Sprint 001 | Actualizar `preview.png` | Debe representar la esfera validada. |
| BL-003 | En evaluación | Alta | Auditoría documental | Versionar documentación oficial en Git | Requiere staging y commit documental. |
| BL-004 | Pendiente | Media | Organización | Definir política GitHub | Remoto, ramas y estrategia. |
| BL-005 | Pendiente | Baja | Sprint 001 | Automatizar validaciones XML | Evaluar tras varios Sprints. |
| BL-006 | Pendiente | Baja | Sprint 001 | Estrategia de pruebas automatizadas | No prioritaria en esta fase. |
| BL-007 | Pendiente | Media | Organización | Política de archivos `.idea` | Determinar qué se versiona. |
| BL-008 | Pendiente | Baja | Auditoría documental | Resolver futuro de CHANGELOG | Mantenerlo o sustituirlo formalmente. |
| BL-009 | Implementado documentalmente | Media | Auditoría documental | Normalizar nomenclatura vigente | Aplicado a documentos nuevos. |
| BL-010 | En evaluación | Alta | Auditoría documental | Determinar EDL vigente | Resolver EDL v0.6 frente a respaldo v0.7. |
| BL-011 | Pendiente | Media | Recursos | Localizar y catalogar antigua Figura 4.1 | Histórica; no bloquea la Figura 1 vigente. |
| BL-012 | Pendiente | Media | Organización | Archivar copias de `ORCA-TO-DO` | No eliminar sin autorización. |
| BL-013 | Pendiente | Baja | Documentación | Normalizar MRPD “Product/Project” | Corrección terminológica futura. |
| BL-014 | Pendiente | Media | Recursos | Definir licencia y recurso del emblema | Obligatorio antes de implementar el emblema. |
| BL-015 | Pendiente | Media | Sprint futuro | Aprobar texto de lema o identificación CIVILIAN | No inventar durante implementación. |
| BL-016 | Pendiente | Baja | Sprint futuro | Evaluar doble pulsación en pasos | Requiere ADR, WFPRD y Sprint propios. |

## Hallazgos del Sprint 023

| ID | Estado | Prioridad | Área | Resultado / continuidad |
|---|---|---|---|---|
| AUD-023-01 | Implementado | Alta | PROBE / release | Companion `com.suri.pipsurios`; watchface separada `com.suri.surioswatch.probewatchface`. |
| AUD-023-02 | Implementado | Media | Seguridad Data Layer | Allowlist de nodo y coincidencia estricta `sourceNodeId`/payload; criptografía queda fuera mientras el entorno sea privado. |
| AUD-023-03 | Implementado | Media | Telemetría PROBE | Mensajes en vivo, sesión, destino explícito, buffer BLE 256, máximo 8 en vuelo y parada al perder enlace; sin persistencia ni replay. |
| AUD-023-04 | Implementado | Media | MORSE | `CAMERA` opcional, solicitud al entrar y acción no disponible sin permiso/flash. |
| AUD-023-05 | Implementado | Media | TERRAIN | SHA-256 por asset, metadata de formato/zoom/bounds y tres teselas representativas; recreación segura. |
| AUD-023-06 | Implementado | Baja-media | Persistencia | Escritura a temporal y renombrado; se conserva el conflicto `AlreadyExists` por fecha. |
| AUD-023-07 | Implementado | Baja-media | Protocolo | Rangos semánticos, finitud, timestamps ±5 minutos y `PING` como `PONG` sin adquisición. |
| AUD-023-08 | Implementado parcialmente | Baja | Compatibilidad / deuda | APIs y avisos de código corregidos. Versiones de dependencias permanecen fijadas hasta una matriz de regresión dedicada. |
| AUD-023-09 | Implementado | Media | GIS / reproducibilidad | Generador con rutas obligatorias, QGIS LTR 3.44.13 y política explícita de fuentes externas/asset Android versionado. |

## Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.2 | Histórica, sustituida | Backlog vigente antes de sincronizar la auditoría Sprint 023. |
| 1.3 | Aprobada y vigente | Incorpora AUD-023-01 a AUD-023-09 y conserva los elementos heredados. |
