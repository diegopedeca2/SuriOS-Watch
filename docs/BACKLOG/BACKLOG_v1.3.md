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

Las decisiones del propietario registradas en el cierre administrativo del
Sprint 024 se reflejan en los elementos heredados. EDL v0.6 queda confirmado
como versión canónica del ecosistema.

## Elementos heredados

| ID | Estado | Prioridad | Origen | Elemento | Observaciones |
|---|---|---|---|---|---|
| BL-001 | Cerrado | Alta | Sprint 001 | Validar tipografía definitiva Consolas | Consolas aprobada como tipografía definitiva; `SYNC_TO_DEVICE` continúa como deuda técnica independiente. |
| BL-002 | Histórico | Media | Sprint 001 | Actualizar `preview.png` | No se actualiza; se conserva como recurso histórico. |
| BL-003 | Cerrado | Alta | Auditoría documental | Versionar documentación oficial en Git | Política de documentación en Git aprobada y aplicada. |
| BL-004 | Cerrado | Media | Organización | Definir política GitHub | `master` es la rama estable; commits funcionales y push tras validación. |
| BL-005 | Planificado | Baja | Sprint 001 | Automatizar validaciones XML | Automatizar cuando exista CI estable. |
| BL-006 | Cerrado | Baja | Sprint 001 | Estrategia de pruebas automatizadas | Unitarias, lint, builds y pruebas instrumentadas en A56 cuando afecten a Android. |
| BL-007 | Cerrado | Media | Organización | Política de archivos `.idea` | Se ignora la configuración local y solo se conserva la configuración compartida imprescindible. |
| BL-008 | Cerrado | Baja | Auditoría documental | Resolver futuro de CHANGELOG | `CHANGELOG.md` se conserva como resumen humano de cambios. |
| BL-009 | Cerrado | Media | Auditoría documental | Normalizar nomenclatura vigente | Normalización documental aprobada. |
| BL-010 | Cerrado | Alta | Auditoría documental | Determinar EDL vigente | EDL v0.6 confirmado como versión canónica; el respaldo anteriormente etiquetado v0.7 queda como copia histórica de v0.6. |
| BL-011 | Histórico | Media | Recursos | Localizar y catalogar antigua Figura 4.1 | Se conserva como material histórico y no entra en el producto. |
| BL-012 | Eliminado | Media | Organización | Eliminar copias de `ORCA-TO-DO` | Directorio eliminado; los archivos estaban versionados y siguen recuperables desde Git. |
| BL-013 | Cerrado | Baja | Documentación | Normalizar MRPD “Product/Project” | Se adopta oficialmente `Project`. |
| BL-014 | Planificado Sprint 025 | Media | Recursos | Definir licencia y recurso del emblema | Se documentará junto con autoría/procedencia antes de cualquier distribución externa. |
| BL-015 | Eliminado | Media | Sprint futuro | Retirar perfil CIVILIAN | El perfil y su ruta huérfana se eliminan; las menciones históricas no se reescriben. |
| BL-016 | Cerrado | Baja | Sprint futuro | Evaluar doble pulsación en pasos | No se incorpora al producto actual. |

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
| AUD-023-08 | Planificado Sprint 025 | Baja | Compatibilidad / deuda | APIs y avisos de código corregidos. La actualización de dependencias requiere una matriz de regresión dedicada. |
| AUD-023-09 | Implementado | Media | GIS / reproducibilidad | Generador con rutas obligatorias, QGIS LTR 3.44.13 y política explícita de fuentes externas/asset Android versionado. |

## Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.2 | Histórica, sustituida | Backlog vigente antes de sincronizar la auditoría Sprint 023. |
| 1.3 | Aprobada y vigente | Incorpora AUD-023-01 a AUD-023-09 y conserva los elementos heredados. |
