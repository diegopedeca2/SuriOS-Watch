# WATCHFACE_LAYOUT v1.3

---

document: WATCHFACE_LAYOUT
project: SuriOS Watch
version: 1.3
status: Aprobado y vigente
implementation_status: Aprobado
replaces: WATCHFACE_LAYOUT v1.2
owner: Diego Pérez de Camino
date: 2026-08-06

---

# 1. Autoridad y propósito

Este documento constituye el anexo visual normativo de **WFPRD v1.4** y está subordinado a él.

Su finalidad es definir exclusivamente la composición visual, la geometría, la jerarquía y las relaciones espaciales de todos los componentes de la esfera SuriOS Watch.

Este documento no puede:

- añadir funcionalidades;
- modificar comportamientos;
- alterar reglas de implementación;
- sustituir documentos funcionales;
- autorizar implementaciones.

Su marco metodológico queda definido por:

- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)

Los comportamientos funcionales de cada componente se regulan en sus respectivos WFPRD.

Actualmente:

- [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md)
- [WFPRD_AMBIENT_MODE v1.1](../WFPRD/WFPRD_AMBIENT_MODE_v1.1.md)
- [WFPRD_BATTERY_INDICATOR v1.2](../WFPRD/WFPRD_BATTERY_INDICATOR_v1.2.md)
- [WFPRD_STEP_INDICATOR v1.1](../WFPRD/WFPRD_STEP_INDICATOR_v1.1.md)
- [WFPRD_BROTHERHOOD_EMBLEM v1.3](../WFPRD/WFPRD_BROTHERHOOD_EMBLEM_v1.3.md)

Las especificaciones gráficas del emblema institucional quedan reguladas por:

- [BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3](../ASSETS/BROTHERHOOD%20EMBLEM/BROTHERHOOD_EMBLEM_ASSET_SPEC_v1.3.md)

Esta versión sustituye a WATCHFACE_LAYOUT v1.2, que permanece únicamente como referencia histórica.

---

# 2. Figura de referencia

**Figura 1 — Referencia visual conceptual vigente de SuriOS Watch**

```
assets/SURIOS_WATCH_REFERENCE_v1.png
```

La figura representa exclusivamente la intención visual del proyecto.

En caso de discrepancia prevalecerán siempre:

1. las coordenadas normativas;
2. las dimensiones aprobadas;
3. las reglas funcionales;
4. los WFPRD específicos.

La antigua Figura 4.1 queda definitivamente sustituida.

---

# 3. Filosofía

La composición de SuriOS Watch se basa en los siguientes principios:

- interpretación inmediata;
- prioridad absoluta de la información;
- identidad técnica;
- minimalismo funcional;
- equilibrio visual;
- simetría antes que ocupación completa;
- ausencia de elementos decorativos innecesarios;
- coherencia con la paleta definida en EDL;
- reutilización de componentes gráficos comunes en todo el ecosistema SuriOS.

El emblema oficial de la Hermandad del Acero constituye un elemento institucional de identidad y nunca podrá comprometer la legibilidad de la información funcional.

---

# 4. Lienzo

Resolución lógica:

450 × 450

Origen:

(0,0)

Centro geométrico:

(225,225)

Eje principal de simetría:

X = 225

Todas las coordenadas del presente documento se expresan sobre este sistema de referencia.

---

# 5. Naturaleza de las coordenadas

## 5.1 Geometría física validada

La geometría correspondiente a:

- hora;
- fecha;

es la implementada y validada físicamente durante Sprint 001.

Constituye la fuente de verdad del proyecto y no podrá modificarse sin una nueva aprobación documental.

---

## 5.2 Geometría objetivo

Las coordenadas del resto de componentes representan objetivos de diseño.

Podrán ajustarse únicamente cuando:

- exista una validación física;
- no se altere la jerarquía visual;
- no se rompa la simetría general;
- el cambio quede documentado;
- exista aprobación expresa del propietario;
- se publique una nueva versión del presente documento.

---

# 6. Jerarquía visual

La prioridad visual aprobada de la esfera será:

1. Hora.
2. Fecha.
3. Batería.
4. Pasos.
5. Spotify.
6. Google Wallet.
7. Día de la semana.
8. Identificación SuriOS.
9. Emblema oficial de la Hermandad del Acero.
10. Separadores gráficos.

La incorporación de nuevos componentes nunca podrá alterar esta jerarquía sin aprobación documental.

Los componentes pendientes de implementación mantienen su posición en la jerarquía únicamente como referencia de diseño y no implican su presencia en la versión actual de la esfera.

---

# 7. Composición general

```text
              DÍA DE LA SEMANA

                   HORA

                  FECHA


        PASOS                    BATERÍA


       WALLET                  SPOTIFY


      EMBLEMA INSTITUCIONAL


       SURIOS WATCH · CIVILIAN
```

La figura representa únicamente la composición conceptual.

Las coordenadas normativas de cada componente prevalecen sobre esta representación.

---

# 8. Componentes

## 8.1 Hora

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Implementada y validada |
| x | 55 |
| y | 42 |
| width | 340 |
| height | 92 |
| Tamaño tipográfico | 82 |
| Formato | HH:MM (24 horas) |
| Alineación | Centro |

La geometría de este componente constituye una referencia física validada y no podrá modificarse mediante este documento.

---

## 8.2 Fecha

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Implementada y validada |
| x | 65 |
| y | 138 |
| width | 320 |
| height | 38 |
| Tamaño tipográfico | 28 |
| Formato | DD/MM/AAAA |
| Alineación | Centro |

La geometría de este componente constituye una referencia física validada y no podrá modificarse mediante este documento.

## 8.3 Día de la semana

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Pendiente. Sprint por determinar. |
| Centro X | 225 |
| Centro Y objetivo | 78 |
| Ancho objetivo | 180 |
| Alto objetivo | 24 |
| Formato | Texto completo (ej.: LUNES) |

La geometría procede de versiones anteriores y permanece como objetivo de diseño.

Su implementación deberá reconciliarse con la geometría física validada de la hora y recibir aprobación expresa antes de incorporarse a la watchface.

---

## 8.4 Pasos

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Implementado y validado durante Sprint 004 |
| Estado físico | Validado en Wear OS Large Round y Xiaomi Watch 2 |
| Centro X objetivo | 105 |
| Centro Y objetivo | 225 |
| Diámetro de zona objetivo | 88 |
| Contenido activo | Número completo |
| Contenido ambiente | Número completo |

La representación funcional queda definida exclusivamente por:

- WFPRD_STEP_INDICATOR v1.1

Este documento únicamente fija su posición dentro de la composición.

No se mostrará:

- barra;
- icono;
- porcentaje;
- objetivo diario.

---

## 8.5 Batería

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Implementada y validada durante Sprint 004 |
| Estado físico | Validada en Wear OS Large Round y Xiaomi Watch 2 |
| Centro X objetivo | 345 |
| Centro Y objetivo | 225 |
| Diámetro de zona objetivo | 88 |
| Contenido activo | Icono de diez niveles y porcentaje |
| Contenido ambiente | Porcentaje |

La representación funcional queda definida exclusivamente por:

- WFPRD_BATTERY_INDICATOR v1.2

Este documento únicamente fija su posición dentro de la composición.

No se mostrará barra.

Durante la carga, la representación correspondiente será sustituida por **RECHARGING** conforme al WFPRD específico.

---

## 8.6 Google Wallet

| Propiedad | Valor |
|---|---:|
| Estado documental | Objetivo aprobado |
| Implementación | Pendiente. Previsto para Sprint 006 |
| Centro X objetivo | 145 |
| Centro Y objetivo | 335 |
| Diámetro objetivo | 64 |
| Tipo | Acceso directo |

No mostrará información adicional.

La implementación funcional quedará regulada por su correspondiente WFPRD.

---

## 8.7 Spotify

| Propiedad | Valor |
|---|---:|
| Estado documental | Objetivo aprobado |
| Implementación | Pendiente. Previsto para Sprint 006 |
| Centro X objetivo | 305 |
| Centro Y objetivo | 335 |
| Diámetro objetivo | 64 |
| Tipo | Acceso directo |

No mostrará:

- controles;
- carátulas;
- información de reproducción.

La implementación funcional quedará regulada por su correspondiente WFPRD.

---

## 8.8 Emblema oficial de la Hermandad del Acero

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Sprint 005 |
| Centro X objetivo | 225 |
| Centro Y objetivo | 235 |
| Tamaño máximo objetivo | 190 × 190 |
| Opacidad | Muy baja. Valor pendiente de validación física |

Este documento únicamente define:

- posición;
- jerarquía visual;
- tamaño máximo.

Toda la normativa gráfica del componente queda definida exclusivamente por:

- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3
- WFPRD_BROTHERHOOD_EMBLEM v1.3

El emblema deberá:

- permanecer detrás de la información;
- no dificultar la lectura;
- mantener la transparencia aprobada;
- utilizar únicamente el recurso oficial del proyecto.

El tamaño efectivo podrá reducirse durante Sprint 005 para preservar la legibilidad del conjunto, sin superar el tamaño máximo definido.

---

## 8.9 Identificación SuriOS Watch

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Pendiente. Sprint por determinar |
| Centro X objetivo | 225 |
| Centro Y objetivo | 390 |
| Ancho objetivo | 170 |
| Alto objetivo | 30 |

El contenido incluirá el texto:

**SuriOS Watch**

---

## 8.10 Identificación CIVILIAN

Estado documental:

Componente visual aprobado.

Implementación:

Pendiente. Sprint por determinar.

Texto definitivo:

Pendiente de aprobación.

Zona:

Integrada con la identificación SuriOS Watch.

No deberá competir visualmente con los indicadores funcionales.

---

## 8.11 Separadores gráficos

Estado documental:

Aprobados cuando resulten necesarios para reproducir la composición oficial.

Implementación:

Pendiente.

Reglas:

- líneas finas;
- PipGreenDim;
- sin sombras;
- sin degradados;
- sin rellenos.

No deberán interferir visualmente con el emblema oficial de la Hermandad del Acero.

La geometría definitiva se validará durante el Sprint correspondiente.

---

# 9. Simetría

La composición deberá respetar permanentemente:

- batería y pasos simétricos respecto al eje X = 225;
- Spotify y Wallet con igual tamaño y separación respecto al eje;
- información superior completamente centrada;
- hora y fecha como referencia absoluta de la composición;
- emblema institucional completamente centrado respecto al eje principal.

Ningún componente visual podrá desplazar la geometría física validada de hora y fecha.

---

# 10. Paleta y tipografía

Paleta oficial:

- Fondo: PipBlack
- Color principal: PipGreen
- Color secundario: PipGreenDim

No podrán utilizarse colores ajenos al EDL.

Tipografía:

La aprobada oficialmente por el proyecto.

Hasta resolver la deuda técnica aceptada continuará utilizándose:

SYNC_TO_DEVICE

como implementación temporal.

# 11. Interacción

Los siguientes componentes no dispondrán de interacción:

- hora;
- fecha;
- día de la semana;
- batería;
- pasos;
- emblema oficial de la Hermandad del Acero;
- identificación SuriOS Watch;
- identificación CIVILIAN;
- separadores gráficos.

Los únicos componentes interactivos previstos son:

### Spotify

- toque único;
- pendiente de validación técnica;
- implementación prevista durante Sprint 006.

### Google Wallet

- toque único;
- pendiente de validación técnica;
- implementación prevista durante Sprint 006.

No se contemplan gestos adicionales, pulsaciones prolongadas ni interacciones múltiples salvo aprobación documental posterior.

---

# 12. Ambient Mode

## Sprint 003

Se incorporó:

- hora;
- fecha.

---

## Desde Sprint 004

Se incorporó:

- hora;
- fecha;
- porcentaje de batería;
- número de pasos.

El icono de batería permanece oculto.

No cambian:

- posición;
- geometría;
- alineación.

---

## Sprint 005

La incorporación del emblema institucional no modificará el contenido funcional del Ambient Mode.

Su comportamiento específico será definido exclusivamente por:

- WFPRD_BROTHERHOOD_EMBLEM.

La decisión final sobre la presencia, opacidad o ausencia del emblema en Ambient Mode quedará documentada en WFPRD_BROTHERHOOD_EMBLEM.

---

## Componentes ocultos

Durante Ambient Mode permanecerán ocultos:

- Spotify;
- Google Wallet;
- separadores gráficos;
- cualquier otro elemento decorativo no autorizado.

---

# 13. Restricciones

Durante la evolución del proyecto no estará permitido:

- modificar la geometría física validada de hora y fecha;
- introducir barras en batería o pasos;
- añadir requisitos funcionales desde este documento;
- implementar componentes fuera de su Sprint correspondiente;
- alterar la jerarquía visual aprobada;
- romper la simetría general de la composición;
- utilizar la imagen conceptual como sustituto de las reglas normativas;
- duplicar requisitos gráficos definidos por BROTHERHOOD_EMBLEM_ASSET_SPEC;
- introducir variantes no aprobadas del emblema institucional.

---

# 14. Criterios de aceptación visual

La composición visual se considerará aprobada cuando se verifique:

- lectura inmediata;
- jerarquía inequívoca;
- equilibrio general;
- simetría correcta;
- utilización exclusiva de la paleta definida por el EDL;
- conservación íntegra de la geometría validada de hora y fecha;
- ausencia de solapamientos;
- fidelidad razonable a la Figura 1;
- prevalencia de las reglas normativas sobre la imagen conceptual;
- validación física correspondiente al Sprint;
- integración correcta del emblema oficial de la Hermandad del Acero;
- conservación de la legibilidad de todos los indicadores;
- ausencia de impacto apreciable sobre el rendimiento de la watchface.

---

# 15. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.1 | Histórica | Plano inicial con geometría y representaciones contradictorias. |
| 1.2 | Histórica | Consolida la geometría tras Sprint 004, incorpora la nueva referencia visual y actualiza la jerarquía compositiva. |
| 1.3 | Actualiza el roadmap tras Sprint 004, incorpora el emblema oficial como componente institucional del Sprint 005, desplaza Spotify y Google Wallet al Sprint 006 y consolida la separación entre geometría (WATCHFACE_LAYOUT), comportamiento (WFPRD) y recursos gráficos (BROTHERHOOD_EMBLEM_ASSET_SPEC). |