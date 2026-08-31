# Sprint 025 — Dependencias, licencias y créditos

---

document: SPRINT
project: SuriOS Ecosystem
version: 1.0
status: Planificado; no activo
owner: Diego Pérez de Camino
date: 2026-08-31
predecessor: Sprint 024

---

## Estado

Este documento prepara el siguiente Sprint, pero no autoriza su inicio. La
activación requerirá autorización expresa y deberá reflejarse en
`ACTIVE_SPRINT.md`.

## Objetivo

Reducir la deuda controlada de dependencias y dejar documentada la procedencia
de los recursos gráficos y los créditos del proyecto sin introducir cambios
funcionales no aprobados.

## Alcance previsto

### 1. Actualización de dependencias — AUD-023-08

- Inventariar las versiones fijadas y seleccionar una actualización objetivo por
  módulo.
- Actualizar de forma controlada, registrando incompatibilidades y retrocesos.
- Ejecutar la matriz de regresión en Samsung A56, Samsung Z Flip 6, Samsung
  Watch Ultra y Xiaomi Watch 2 según el módulo afectado.
- Repetir tests unitarios, lint, ensamblados y pruebas instrumentadas en A56
  cuando el cambio afecte a Android.
- Mantener `master` estable y publicar solo después de completar la validación.

### 2. Licencias y procedencia del emblema

- Documentar autoría, procedencia, licencia o permiso de uso del emblema y de
  cada variante distribuida en el repositorio.
- Revisar los recursos externos adicionales y registrar expresamente los que no
  tengan licencia o procedencia suficiente.
- No inferir una licencia ni distribuir fuera del entorno privado mientras el
  registro no esté completo.

### 3. Recolocación de agradecimientos

- Definir antes de implementar el destino funcional del apartado actual
  `SET-UP > ACKNOWLEDGEMENTS`.
- Mover la pantalla, la navegación y las referencias documentales al destino
  aprobado, conservando el contenido y el carácter de solo lectura.
- Validar el acceso y el retorno en el Samsung A56.

## Fuera de alcance

- Revisar la decisión ya cerrada de EDL v0.6, salvo que aparezca una nueva
  propuesta formal.
- Reintroducir el perfil CIVILIAN.
- Incorporar doble pulsación.
- Actualizar `preview.png` o recuperar material histórico.
- Modificar la funcionalidad actual de PROBE, P.R.S. o TERRAIN salvo regresiones
  causadas directamente por una dependencia.

## Criterios de aceptación previstos

- Existe una matriz de dependencias antes de modificar versiones.
- Las versiones adoptadas compilan en las variantes soportadas y no introducen
  errores de Lint.
- Se ejecutan y registran las pruebas obligatorias de BL-006 para cada módulo
  afectado.
- Existe un registro verificable de licencias, autoría y procedencia del
  emblema y de los recursos externos revisados.
- El nuevo destino de agradecimientos está aprobado, implementado, documentado
  y validado físicamente.
- El Sprint queda documentado y publicado solo tras el cierre técnico.

## Dependencias de decisión para activar

Antes de activar este Sprint deberá aprobarse el destino exacto de
`ACKNOWLEDGEMENTS` y confirmarse la matriz inicial de versiones objetivo. La
licencia no se completará con datos inventados si la procedencia no puede
acreditarse.
