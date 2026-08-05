# RELEASE_NOTES v1.1

---
document: RELEASE_NOTES
project: SuriOS Watch
version: 1.1
status: Aprobado y vigente
replaces: RELEASE_NOTES v1.0
owner: Diego Pérez de Camino
last_updated: 2026-08-05
---

## 1. Propósito

Describir únicamente capacidades publicadas, finalizadas y perceptibles para el usuario. No incluye tareas internas, decisiones arquitectónicas, incidencias pendientes o funcionalidades en desarrollo.

Se interpreta conforme a [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md). No sustituye WFPRD, ADR, Sprint, Git, CHANGELOG o [SPRINT_HISTORY](../SPRINTS/SPRINT_HISTORY.md).

## 2. Estados de versión

- **En preparación:** todavía no publicada.
- **Publicada:** disponible y validada.
- **Histórica:** sustituida por una posterior.

Una entrada publicada es inmutable. Las correcciones del documento se realizan mediante nueva versión de RELEASE_NOTES sin reescribir el resultado histórico del producto.

## 3. Versiones publicadas

### SuriOS Watch 0.1 — Base funcional

| Campo | Valor |
|---|---|
| Estado | Publicada |
| Fecha | 2026-08-05 |
| Sprint | [Sprint 001](../SPRINTS/SPRINT_001.md) |

Funcionalidades disponibles:

- Watch Face base.
- Fondo PipBlack.
- Hora en formato `HH:MM`.
- Fecha en formato `DD/MM/AAAA`.
- Colores oficiales del EDL.
- Validación en emulador Wear OS.
- Validación física en Xiaomi Watch 2.

No incluye:

- Ambient Mode;
- batería;
- pasos;
- Spotify;
- Google Wallet;
- componentes visuales futuros;
- optimización final.

La tipografía usa una solución temporal aceptada. Esta deuda no cambia las funciones disponibles para el usuario.

## 4. Próxima versión funcional

No existe todavía una siguiente versión funcional publicada ni un número funcional aprobado.

Sprint 002 es una migración interna de infraestructura y no figura como funcionalidad o versión de usuario. Su ejecución no deberá generar por sí sola una nueva entrada de producto si el comportamiento visible permanece idéntico.

Sprint 003 podrá contribuir a una futura versión funcional cuando Ambient Mode esté completado, validado y publicado.

## 5. Convención de producto

- `0.x`: versiones de desarrollo publicadas.
- `1.0`: primera versión completa.
- `1.x`: funcionalidades compatibles posteriores.
- `2.0`: cambio importante de comportamiento o arquitectura de producto perceptible para el usuario.

La numeración del documento RELEASE_NOTES es independiente de la numeración de producto.

## 6. Historial documental

| Documento | Estado | Descripción |
|---|---|---|
| RELEASE_NOTES v1.0 | Histórica, sustituida | Incluía la migración Gradle como próxima versión prevista. |
| RELEASE_NOTES v1.1 | Aprobada y vigente | Separa hitos técnicos internos de versiones funcionales. |
