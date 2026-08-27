# SuriOS Watch — WFPRD v1.4

---
document: WFPRD
project: SuriOS Watch
version: 1.4
status: Aprobado y vigente
implementation_status: Desarrollo incremental
replaces: WFPRD v1.3
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 0. Control documental

### 0.1 Propósito de la versión

WFPRD v1.4 es la especificación maestra completa y autocontenida de SuriOS Watch. Sustituye a [WFPRD v1.3](WFPRD_v1.3.md), que permanece histórico, e incorpora las decisiones aprobadas durante la corrección documental previa a Sprint 002.

La versión 1.2 se conserva como [WFPRD histórico](WFPRD.md). Ninguna versión anterior se modifica.

### 0.2 Jerarquía

La interpretación obligatoria es:

1. [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
2. [EDL v0.6](../EDL/EDL.md).
3. Este WFPRD v1.4.
4. ADR, WFPRD especializados y WATCHFACE_LAYOUT dentro de sus competencias.
5. Sprint aprobado y activado.
6. Implementación y validación.

[WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md) es un anexo visual normativo subordinado a este documento. No puede añadir funcionalidad ni contradecirlo. Los WFPRD especializados definen el comportamiento concreto de cada componente.

### 0.3 Documentos normativos relacionados

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).
- [EDL v0.6](../EDL/EDL.md).
- [MRPD v1.1.1](../MRPD/MRPD.md), para decisiones compartidas del ecosistema.
- [ADR-001 v1.2](../ADR/ADR_001_v1.2.md), arquitectura Gradle.
- [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>).
- [ADR-003 v1.0](<../ADR/ADR-003 - Arquitectura de batería y pasos v1.0.md>).
- [WFPRD Ambient Mode v1.1](WFPRD_AMBIENT_MODE_v1.1.md).
- [WFPRD batería v1.2](WFPRD_BATTERY_INDICATOR_v1.2.md).
- [WFPRD pasos v1.1](WFPRD_STEP_INDICATOR_v1.1.md).
- [WATCHFACE_LAYOUT v1.2](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.2.md).
- [ACTIVE_SPRINT](../SPRINTS/ACTIVE_SPRINT.md).
- [SPRINT_HISTORY](../SPRINTS/SPRINT_HISTORY.md).
- [BACKLOG v1.2](../BACKLOG/BACKLOG_v1.2.md).

### 0.4 Referencia visual

La [Figura 1 — Referencia visual conceptual vigente](../WATCHFACE_LAYOUT/assets/SURIOS_WATCH_REFERENCE_v1.png) expresa la intención estética y compositiva del propietario.

Las coordenadas, dimensiones, estados y reglas textuales aprobadas en este WFPRD, los WFPRD especializados y WATCHFACE_LAYOUT v1.2 prevalecen si existe alguna diferencia con la imagen.

La Figura 4.1 de WFPRD v1.1 queda clasificada como referencia histórica sustituida. Su localización material continúa como tarea documental no bloqueante.

## 1. Visión y propósito

SuriOS Watch es una esfera para Xiaomi Watch 2 sobre Wear OS, integrada visualmente en el ecosistema SuriOS. Debe presentar información esencial de forma inmediata, legible, técnica y coherente con el EDL, sin convertirse en un juego ni copiar literalmente una interfaz de ficción.

El producto prioriza:

- utilidad;
- lectura inmediata;
- identidad SuriOS;
- estabilidad;
- bajo consumo;
- evolución incremental documentada.

## 2. Alcance

### 2.1 Incluido

- Main Watch Face del perfil CIVILIAN.
- Ambient Mode por fases.
- Hora y fecha.
- Componentes visuales de identidad aprobados.
- Indicadores de batería y pasos.
- Accesos a Spotify y Google Wallet cuando sean técnicamente aprobados.
- Validación en emulador y Xiaomi Watch 2.

### 2.2 Excluido actualmente

- perfil OPERATION;
- Tiles;
- aplicación Wear OS independiente;
- pantallas secundarias;
- configuración avanzada;
- batería del teléfono;
- sincronización funcional con PIP-SuriOS;
- biometría;
- complicaciones configurables;
- interacción no aprobada de los indicadores.

### 2.3 Perfiles

- **CIVILIAN:** aprobado, prioridad alta, desarrollo incremental.
- **OPERATION:** pendiente de definición y fuera del roadmap 002–006.

## 3. Principios de diseño

- La hora domina la jerarquía.
- La fecha aporta contexto secundario.
- La simetría tiene prioridad sobre llenar todo el espacio.
- Ningún elemento debe dificultar la lectura.
- El fondo será PipBlack puro.
- La información principal usará PipGreen.
- La secundaria usará PipGreenDim.
- No se introducirán colores, sombras, degradados o animaciones no aprobados.
- Los elementos visuales institucionales serán subordinados a la información.

## 4. Lienzo y geometría

El lienzo lógico de referencia es `450 × 450`, con origen `(0,0)` en la esquina superior izquierda y centro `(225,225)`.

### 4.1 Geometría validada físicamente

Los siguientes valores fueron implementados y validados físicamente durante Sprint 001 y son fuente de verdad:

| Componente | x | y | width | height | Tamaño tipográfico |
|---|---:|---:|---:|---:|---:|
| Hora | 55 | 42 | 340 | 92 | 82 |
| Fecha | 65 | 138 | 320 | 38 | 28 |

No podrán cambiarse sin una nueva decisión aprobada.

### 4.2 Geometría objetivo pendiente

Las coordenadas restantes de WATCHFACE_LAYOUT v1.2 son objetivos de diseño. Su validación física corresponde al Sprint que implemente cada componente. Los ajustes menores exigirán revisión documental si alteran posición, tamaño, simetría o jerarquía.

## 5. Sistema de componentes

### 5.1 Hora

- **Estado documental:** aprobado.
- **Estado de implementación:** implementado y validado físicamente.
- **Formato:** `HH:MM`, 24 horas.
- **Geometría:** la definida en 4.1.
- **Interacción:** ninguna.
- **Ambient Mode:** visible desde Sprint 003.

### 5.2 Fecha

- **Estado documental:** aprobado.
- **Estado de implementación:** implementado y validado físicamente.
- **Formato:** `DD/MM/AAAA`.
- **Geometría:** la definida en 4.1.
- **Interacción:** ninguna.
- **Ambient Mode:** visible desde Sprint 003.

### 5.3 Día de la semana

- **Estado documental:** aprobado como componente visual.
- **Estado de implementación:** pendiente.
- **Función:** aportar contexto de calendario mediante texto completo.
- **Ejemplo:** `LUNES`.
- **Posición objetivo:** zona superior, subordinada a hora y fecha.
- **Sprint previsto:** Sprint 006, salvo reasignación expresa.
- **Ambient Mode:** oculto.

### 5.4 Indicador de batería

- **Estado documental:** aprobado.
- **Estado de implementación:** pendiente; Sprint 004.
- **Fuente:** batería del reloj exclusivamente.
- **Modo activo:** icono de diez niveles y porcentaje; sin barra.
- **Carga activa:** `RECHARGING` sustituye icono y porcentaje.
- **Ambient Mode desde Sprint 004:** porcentaje únicamente.
- **Carga en Ambient Mode:** `RECHARGING` sustituye el porcentaje.
- **Interacción:** ninguna.
- **Norma especializada:** [WFPRD batería v1.2](WFPRD_BATTERY_INDICATOR_v1.2.md).

### 5.5 Indicador de pasos

- **Estado documental:** aprobado.
- **Estado de implementación:** pendiente; Sprint 004.
- **Fuente:** contador de pasos del reloj exclusivamente.
- **Modo activo:** número completo con separación de miles mediante espacio; sin barra.
- **Ambient Mode desde Sprint 004:** número únicamente.
- **Interacción:** ninguna.
- **Excepción EDL:** para este componente, el número completo constituye la representación aprobada; no se añadirá icono o barra sin nueva versión.
- **Norma especializada:** [WFPRD pasos v1.1](WFPRD_STEP_INDICATOR_v1.1.md).

### 5.6 Spotify

- **Estado documental:** objetivo aprobado; comportamiento técnico pendiente.
- **Estado de implementación:** pendiente; Sprint 005.
- **Función prevista:** acceso directo mediante toque único.
- **No permitido:** controles, carátulas o información de reproducción sin aprobación.
- **Ambient Mode:** oculto.

### 5.7 Google Wallet

- **Estado documental:** objetivo aprobado; comportamiento técnico pendiente.
- **Estado de implementación:** pendiente; Sprint 005.
- **Función prevista:** acceso directo mediante toque único.
- **Ambient Mode:** oculto.
- **Condición:** deberá validarse la capacidad real de Wear OS antes de implementar.

### 5.8 Emblema de la Hermandad del Acero

- **Estado documental:** aprobado como componente visual de referencia.
- **Estado de implementación:** pendiente.
- **Función:** identidad institucional subordinada.
- **Tratamiento:** opacidad muy baja, sin dificultar información.
- **Condición:** recurso y uso deberán quedar aprobados y trazados.
- **Sprint previsto:** Sprint 006, salvo reasignación expresa.
- **Ambient Mode:** oculto.

### 5.9 Identificación SuriOS Watch

- **Estado documental:** aprobado como componente visual.
- **Estado de implementación:** pendiente.
- **Contenido:** nombre del proyecto.
- **Jerarquía:** subordinada a todos los datos funcionales.
- **Sprint previsto:** Sprint 006, salvo reasignación expresa.
- **Ambient Mode:** oculto.

### 5.10 Lema o identificación del perfil

- **Estado documental:** aprobado como componente visual.
- **Estado de implementación:** pendiente.
- **Contenido:** identificación del perfil CIVILIAN o lema que apruebe el propietario.
- **Texto definitivo:** pendiente de aprobación; no podrá inventarse durante implementación.
- **Sprint previsto:** Sprint 006, salvo reasignación expresa.
- **Ambient Mode:** oculto.

### 5.11 Separadores gráficos

- **Estado documental:** aprobados cuando sean necesarios para reproducir la referencia visual.
- **Estado de implementación:** pendiente.
- **Regla:** deberán ser mínimos, coherentes con el EDL y no decorativos sin función compositiva.
- **Sprint previsto:** junto al componente al que sirven o Sprint 006 para cierre visual.
- **Ambient Mode:** ocultos.

## 6. Ambient Mode

Ambient Mode es una representación simplificada de la misma esfera, no una esfera independiente. Se rige por [ADR-002 v1.0](<../ADR/ADR-002 - Estrategia de Ambient Mode v1.0.md>) y [WFPRD Ambient Mode v1.1](WFPRD_AMBIENT_MODE_v1.1.md).

### 6.1 Sprint 003

Permanecerán visibles únicamente:

- hora;
- fecha.

### 6.2 Desde Sprint 004

Al implementarse los indicadores se añadirán:

- porcentaje de batería, sin icono;
- número de pasos, sin icono ni barra.

Esta ampliación no altera la arquitectura de ADR-002: sigue siendo información esencial presentada mediante una representación simplificada.

### 6.3 Reglas

- conservar posiciones y geometría del modo activo;
- fondo PipBlack;
- sin animaciones innecesarias;
- sin Spotify, Wallet ni componentes visuales de identidad;
- reducir actualizaciones;
- priorizar legibilidad y consumo;
- validar en Xiaomi Watch 2.

## 7. Tipografía

La fuente prevista por el EDL es Consolas. Sprint 001 se cerró con `family="SYNC_TO_DEVICE"` como excepción temporal expresamente aceptada. Esta excepción:

- permite considerar Sprint 001 cerrado;
- no convierte la fuente en definitiva;
- no autoriza otras fuentes;
- permanece registrada como deuda en BACKLOG v1.2.

Tamaños aprobados: hora 82 y fecha 28. Los tamaños restantes deberán aprobarse y validarse con su componente.

## 8. Paleta

| Token | Valor | Uso |
|---|---|---|
| PipBlack | `#000000` | Fondo |
| PipGreen | `#66FF66` | Información principal |
| PipGreenDim | `#3FAF5A` | Información secundaria |
| PipAmber | `#FFC857` | Advertencia aprobada |
| PipRed | `#FF4D4D` | Estado crítico aprobado |

Los indicadores de batería y pasos no cambiarán de color en función de su valor.

## 9. Roadmap oficial

| Sprint | Alcance | Estado documental | Implementación |
|---|---|---|---|
| 001 | Base: fondo, hora y fecha | Aprobado | Completado |
| 002 | Migración a un build Gradle multiproyecto | Aprobado | Pendiente |
| 003 | Ambient Mode con hora y fecha | Aprobado | Pendiente |
| 004 | Batería y pasos, incluido Ambient Mode | Pendiente de documento de Sprint | Pendiente |
| 005 | Spotify y Google Wallet | Pendiente de documento de Sprint | Pendiente |
| 006 | Optimización, regresión y cierre visual | Pendiente de documento de Sprint | Pendiente |

[ACTIVE_SPRINT](../SPRINTS/ACTIVE_SPRINT.md) es la única fuente de activación operativa. La aprobación documental de un Sprint no lo activa.

## 10. Criterios por Sprint

### Sprint 002

- una sola raíz Gradle;
- ambos productos compilan;
- ausencia de cambios funcionales y visuales;
- reversibilidad;
- aprobación del propietario.

### Sprint 003

- Ambient Mode muestra hora y fecha;
- modo activo sin cambios;
- transición estable;
- legibilidad y consumo validados;
- validación física.

### Sprint 004

- batería muestra icono de diez niveles y porcentaje en activo;
- pasos muestra número completo;
- no existen barras;
- Ambient Mode muestra porcentaje y número;
- estados de carga correctos;
- simetría y datos validados.

### Sprint 005

- accesos técnicamente viables y funcionales;
- áreas táctiles aprobadas;
- sin capacidades inventadas;
- modo activo y Ambient Mode sin regresiones.

### Sprint 006

- optimización;
- regresión completa;
- validación física final;
- cierre de componentes visuales planificados;
- versión estable.

## 11. Sprint 001: cierre real

Sprint 001 está completado y aprobado. Se validaron compilación, instalación, emulador y Xiaomi Watch 2.

Commits diferenciados:

| Commit | Papel histórico |
|---|---|
| `10160d9` | Primera implementación estable de la Watch Face. |
| `e530164` | Base funcional de Sprint 001. |
| `96d15b3` | Cierre documental y validación física de Sprint 001. |

La deuda de tipografía no reabre el Sprint porque fue aceptada expresamente y está documentada.

## 12. Recursos

- Los recursos deberán tener origen, licencia, versión y estado.
- La imagen conceptual vigente se almacena en `docs/WATCHFACE_LAYOUT/assets/`.
- No se usarán recursos provisionales en implementación sin aprobación.
- El emblema institucional requiere validación específica antes de integrarse.

## 13. Reglas para implementación asistida

- Leer la jerarquía completa.
- Trabajar solo sobre el Sprint activo.
- No completar detalles pendientes.
- No modificar geometría o comportamiento sin aprobación.
- Documentar limitaciones y proponer alternativas.
- No modificar código ajeno al alcance.
- Compilar y validar cuando el Sprint lo autorice.
- No realizar commits sin autorización.

## 14. Definition of Done del producto CIVILIAN

La esfera CIVILIAN estará finalizada cuando:

- todos los Sprints 001–006 estén completados;
- hora, fecha, Ambient Mode, batería, pasos y accesos aprobados funcionen;
- los componentes visuales planificados estén resueltos;
- respete EDL y documentos normativos;
- se valide físicamente;
- no existan errores bloqueantes;
- deudas aceptadas estén documentadas;
- exista una versión estable y commit autorizado.

## 15. Trazabilidad v1.3 → v1.4

| Materia v1.3 | Resolución v1.4 |
|---|---|
| Roadmap 2–5 antiguo | Sustituido por Sprints 002–006. |
| Sprint 001 pendiente | Cerrado con deuda tipográfica aceptada. |
| Figura 4.1 sin localizar | Histórica; Figura 1 recuperada pasa a ser vigente. |
| Layout sin autoridad definida | Anexo normativo subordinado. |
| Hora y fecha con geometría implementada | Se consolidan los valores físicos validados. |
| Batería pendiente | Concretada por WFPRD batería v1.2. |
| Pasos pendiente | Concretados por WFPRD pasos v1.1. |
| Ambient Mode sin indicadores | Fase 003 y ampliación 004 definidas. |
| Componentes visuales pendientes | Aprobados y planificados sin autorización anticipada. |

## 16. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.2 | Histórica | Requisitos iniciales completos. |
| 1.3 | Histórica, sustituida | Ampliación documental y trazabilidad. |
| 1.4 | Aprobada y vigente | Consolida roadmap, cierre de Sprint 001, layout, Ambient Mode e indicadores. |

## 17. Estado final

**Documento aprobado y vigente. Producto en desarrollo incremental. Ningún Sprint está activo hasta autorización expresa.**
