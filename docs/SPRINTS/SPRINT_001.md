# SPRINT_001.md

---
document: SPRINT
project: SuriOS Watch
version: 1.0
sprint: 001
document_status: Approved
implementation_status: Completed
start_date: 2026-08-05
end_date: 2026-08-05
priority: Alta
owner: Diego Pérez de Camino
---

# Sprint 001 – Base de la Watch Face (CIVILIAN)

## Estado

- **Estado:** Completado
- **Prioridad:** Alta
- **Proyecto:** SuriOS Watch
- **Perfil:** CIVILIAN

---

# Objetivo

Implementar la primera versión funcional de la esfera SuriOS Watch.

El objetivo de este Sprint es disponer de una esfera completamente funcional mostrando únicamente la identidad visual, la hora y la fecha.

No se implementará ninguna otra funcionalidad.

---

# Documentación de referencia

Antes de comenzar el desarrollo será obligatorio revisar:

1. PROJECT_GUIDE.md
2. EDL.md
3. WFPRD.md

---

# Alcance

## Incluido

- Fondo negro AMOLED.
- Paleta oficial del EDL.
- Hora (HH:MM).
- Fecha (DD/MM/AAAA).
- Distribución oficial definida en el WFPRD.
- Tipografía oficial o alternativa aprobada.
- Compilación correcta.

## No incluido

- Batería.
- Pasos.
- Spotify.
- Wallet.
- Ambient Mode.
- Perfil OPERATION.
- Animaciones.
- Integraciones.

---

# Criterios de aceptación

El Sprint se considerará válido cuando:

- La esfera compile correctamente.
- Se instale en un Xiaomi Watch 2.
- Muestre correctamente la hora.
- Muestre correctamente la fecha.
- Respete el Ecosystem Design Language.
- No presente errores críticos.

---

# Restricciones

No está permitido:

- Añadir funcionalidades nuevas.
- Modificar el diseño aprobado.
- Introducir dependencias nuevas.
- Refactorizar código ajeno al Sprint.
- Cambiar la arquitectura.

---

# Entregables

- Código fuente actualizado.
- Proyecto compilable.
- Resumen técnico.
- Propuesta de Commit.

---

# Commit sugerido

Sprint 001 - Base funcional de la Watch Face

---

# Definition of Done

El Sprint únicamente podrá darse por terminado cuando:

- La implementación esté completada.
- Compile correctamente.
- Haya sido instalada y probada.
- No existan errores críticos.
- La documentación permanezca consistente.
- El propietario apruebe el resultado.

---

# Resultado del cierre

- **Fecha de finalización:** 2026-08-05.
- **Compilación:** Correcta.
- **Instalación en emulador Wear OS:** Correcta.
- **Validación visual en emulador:** Correcta.
- **Instalación en Xiaomi Watch 2:** Correcta.
- **Validación física:** Superada y aprobada por el propietario.
- **Commit técnico asociado:** `e530164`, “Sprint 001 - Base funcional de la Watch Face”.
- **Artefacto validado:** `watch/watchface/src/main/res/raw/watchface.xml`.
- **Resultado:** Sprint 001 completado y aprobado.

## Tipografía temporal

Se mantiene `family="SYNC_TO_DEVICE"` como solución temporal expresamente autorizada para el Sprint 001.

Esta autorización no convierte `SYNC_TO_DEVICE` en la tipografía definitiva, no sustituye la decisión pendiente sobre Consolas y no autoriza otra fuente. La validación de la tipografía definitiva permanece pendiente.

---

# Observaciones

Este Sprint tiene como único objetivo validar el flujo completo de trabajo del proyecto:

Documentación → Implementación → Compilación → Instalación → Validación.

No se evaluarán funcionalidades fuera del alcance definido.
