# SPRINT_001.md

---
document: SPRINT
project: SuriOS Watch
version: 1.0
sprint: 001
status: Planned
priority: Alta
owner: Diego Pérez de Camino
---

# Sprint 001 – Base de la Watch Face (CIVILIAN)

## Estado

- **Estado:** En desarrollo
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

# Observaciones

Este Sprint tiene como único objetivo validar el flujo completo de trabajo del proyecto:

Documentación → Implementación → Compilación → Instalación → Validación.

No se evaluarán funcionalidades fuera del alcance definido.