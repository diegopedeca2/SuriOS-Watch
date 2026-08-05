# WFPRD — Ambient Mode v1.1

---
document: WFPRD especializado
component: Ambient Mode
project: SuriOS Watch
version: 1.1
status: Aprobado y vigente
implementation_status: Pendiente
replaces: WFPRD Ambient Mode v1.0
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Control documental

Esta versión sustituye al documento histórico `WFPRD SPRINT-02 v1.0.md`. Su nombre es funcional e independiente del número de Sprint.

Depende de:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md);
- [WFPRD v1.4](WFPRD_v1.4.md);
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>);
- [WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md);
- [Sprint 003 v1.1](../SPRINTS/SPRINT_003_v1.1.md).

## 2. Objetivo

Definir la representación de bajo consumo de la Watch Face manteniendo identidad, legibilidad y continuidad con el modo activo. Ambient Mode no es otra esfera y no añade funcionalidad exclusiva.

## 3. Principios

- Mantener la distribución.
- Mostrar solo información esencial.
- Eliminar decoración y accesos.
- Evitar animaciones y actualizaciones innecesarias.
- Conservar fondo PipBlack.
- Mantener la tipografía aprobada o la excepción temporal vigente.
- No alterar el modo activo.

## 4. Fases

### 4.1 Sprint 003

Visibles únicamente:

- hora `HH:MM`;
- fecha `DD/MM/AAAA`.

Ocultos:

- batería;
- pasos;
- Spotify;
- Wallet;
- día de la semana;
- emblema;
- identificación SuriOS;
- lema y separadores.

### 4.2 Desde Sprint 004

Al implementarse los indicadores se añaden:

- porcentaje de batería sin icono;
- número completo de pasos sin icono ni barra.

La incorporación es una ampliación de información esencial dentro de la arquitectura aprobada en ADR-002 y no la modifica.

## 5. Geometría

Todos los elementos conservarán exactamente sus coordenadas, tamaños, alineaciones y márgenes del modo activo. La hora y fecha usarán los valores físicos validados de WFPRD v1.4. Los indicadores usarán la geometría de WATCHFACE_LAYOUT v1.2 una vez validada en Sprint 004.

## 6. Color y tipografía

- Fondo PipBlack.
- Hora PipGreen o tratamiento reducido que se apruebe durante Sprint 003.
- Fecha PipGreenDim o tratamiento reducido aprobado.
- No se introducirán colores fuera del EDL.
- Se mantendrá la misma familia tipográfica que en activo.

Los valores exactos de brillo u opacidad deberán validarse durante Sprint 003 sin cambiar la paleta.

## 7. Estado de carga desde Sprint 004

- Modo activo cargando: `RECHARGING` sustituye icono y porcentaje.
- Ambient Mode cargando: `RECHARGING` sustituye el porcentaje.
- El estado de carga no añade animaciones ni iconografía en Ambient Mode.

## 8. Actualización y consumo

- Hora y fecha se actualizarán con la mínima frecuencia necesaria.
- Batería y pasos solo se actualizarán cuando cambie el dato o estado relevante.
- No se ejecutarán actualizaciones periódicas sin función.
- No habrá animaciones decorativas.

## 9. Restricciones

Ambient Mode no podrá:

- mover componentes;
- cambiar tipografía;
- alterar el modo activo;
- introducir componentes antes de su Sprint;
- mostrar Spotify o Wallet;
- incorporar decoración;
- añadir interacción exclusiva.

## 10. Criterios de aceptación de Sprint 003

- hora y fecha visibles;
- geometría idéntica al modo activo;
- transición estable;
- legibilidad en emulador y Xiaomi Watch 2;
- ausencia de regresiones;
- ausencia de indicadores todavía no implementados;
- compilación e instalación correctas;
- aprobación del propietario.

## 11. Criterios adicionales de Sprint 004

- porcentaje de batería visible sin icono;
- número de pasos visible sin barra;
- `RECHARGING` sustituye el porcentaje en carga;
- datos correctos;
- consumo y legibilidad validados.

## 12. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Histórica, sustituida | Definición inicial asociada erróneamente a Sprint 02. |
| 1.1 | Aprobada y vigente | Nombre funcional y despliegue por fases 003–004. |
