\# BACKLOG.md



Estado documental



Vigente



Versión



1.1



Última actualización



2026-08-05



Propietario



Diego Pérez de Camino



Proyecto



Ecosistema SuriOS



\---



\# Historial



v1.0



Primera creación del documento BACKLOG.



v1.1



Se incorpora el campo "Origen" en la tabla de elementos.

Se añade el apartado "Elementos retirados".

Se refuerzan las normas de uso.

No se modifica el propósito del documento.



\---



\# 1. Objetivo



Este documento recopila todas las tareas, ideas, mejoras y propuestas que todavía no forman parte de un Sprint aprobado.



El BACKLOG no constituye una autorización de desarrollo.



Ningún elemento podrá implementarse hasta ser trasladado expresamente al WFPRD y al Sprint correspondiente.



Su finalidad es servir como lista única de trabajo potencial del ecosistema SuriOS.



\---



\# 2. Normas de uso



Todo elemento del BACKLOG deberá cumplir las siguientes reglas:



\- No implica aprobación.

\- No modifica el roadmap.

\- No sustituye al WFPRD.

\- No sustituye a un ADR.

\- No sustituye a un Sprint.

\- Puede modificarse libremente mientras permanezca en este documento.

\- Solo el propietario del proyecto podrá priorizar, modificar o eliminar elementos.

\- Cada elemento del BACKLOG deberá describir una única idea, mejora o tarea.



\---



\# 3. Estados



Cada elemento utilizará uno de los siguientes estados.



Pendiente



Idea registrada sin planificación.



En evaluación



Se está estudiando su viabilidad.



Aprobado para planificación



Puede incorporarse a un futuro Sprint.



Descartado



No continuará su desarrollo.



Implementado



Ha sido trasladado al Sprint correspondiente.



\---



\# 4. Prioridades



Alta



Importante para la evolución del proyecto.



Media



Mejora recomendable.



Baja



Mejora futura.



Sin prioridad



Idea registrada sin planificación.



\---



\# 5. Backlog



| ID | Estado | Prioridad | Origen | Elemento | Observaciones |

|----|---------|-----------|---------|----------|---------------|

| BL-001 | Pendiente | Alta | Auditoría Sprint 001 | Validar tipografía definitiva (Consolas) | Mantener SYNC\_TO\_DEVICE hasta aprobación. |

| BL-002 | Pendiente | Media | Auditoría Sprint 001 | Actualizar preview.png | Debe representar la Watch Face validada. |

| BL-003 | Pendiente | Alta | Auditoría Sprint 001 | Versionar documentación oficial en Git | Revisar previamente el alcance de cada documento. |

| BL-004 | Pendiente | Media | Organización del proyecto | Definir política GitHub | Remoto, ramas y estrategia de trabajo. |

| BL-005 | Pendiente | Baja | Auditoría Sprint 001 | Automatizar validaciones XML | Evaluar tras varios Sprint. |

| BL-006 | Pendiente | Baja | Auditoría Sprint 001 | Definir estrategia de pruebas automatizadas | No prioritaria en esta fase. |

| BL-007 | Pendiente | Media | Auditoría Sprint 001 | Política de archivos .idea | Determinar qué debe versionarse. |

| BL-008 | Pendiente | Baja | Auditoría Sprint 001 | Revisar CHANGELOG | Decidir si se mantiene o se sustituye por SPRINT\_HISTORY. |

| BL-009 | Pendiente | Media | Auditoría Sprint 001 | Normalizar nomenclatura documental | WFPRD / WFRPD, rutas y referencias. |



\---



\# 6. Incorporación a un Sprint



Antes de mover un elemento del BACKLOG a un Sprint deberán cumplirse todos los requisitos siguientes:



\- Existirá una necesidad justificada.

\- La decisión arquitectónica estará aprobada, si corresponde.

\- El WFPRD contendrá los requisitos necesarios.

\- El propietario autorizará expresamente su planificación.



Una vez incorporado a un Sprint, el elemento dejará de gestionarse desde este documento.



Su estado pasará a "Implementado" o el registro podrá archivarse, según proceda.



\---



\# 7. Elementos retirados



Cuando un elemento sea descartado definitivamente podrá trasladarse a este apartado para conservar el histórico de decisiones.



No se eliminarán registros salvo autorización expresa del propietario.



Actualmente no existen elementos retirados.



\---



\# 8. Observaciones



El BACKLOG representa únicamente una lista de trabajo potencial.



El orden de los elementos no implica prioridad cronológica.



La existencia de un elemento en este documento no garantiza su futura implementación.



Las prioridades podrán modificarse conforme evolucione el ecosistema SuriOS.



Este documento deberá mantenerse ligero y evitar duplicar información ya existente en ADR, WFPRD o Sprint.

