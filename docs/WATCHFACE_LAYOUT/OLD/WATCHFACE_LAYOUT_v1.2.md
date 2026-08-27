# WATCHFACE_LAYOUT v1.2

---
document: WATCHFACE_LAYOUT
project: SuriOS Watch
version: 1.2
status: Aprobado y vigente
implementation_status: En desarrollo
replaces: WATCHFACE_LAYOUT v1.1
owner: Diego Pérez de Camino
date: 2026-08-05
---

## 1. Autoridad y propósito

Este documento es el anexo visual normativo de [WFPRD v1.4](../WFPRD/WFPRD_v1.4.md) y está subordinado a él. Define composición, geometría y relaciones espaciales. No puede añadir funcionalidad, cambiar comportamiento ni autorizar implementación.

Su marco de proceso es [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md).

Los comportamientos concretos se definen en:

- [Ambient Mode v1.1](../WFPRD/WFPRD_AMBIENT_MODE_v1.1.md);
- [batería v1.2](../WFPRD/WFPRD_BATTERY_INDICATOR_v1.2.md);
- [pasos v1.1](../WFPRD/WFPRD_STEP_INDICATOR_v1.1.md).

Esta versión sustituye a [WATCHFACE_LAYOUT v1.1](WATCHFACE_LAYOUT_v1.1.md), que permanece histórico.

## 2. Figura de referencia

[Figura 1 — Referencia visual conceptual vigente de SuriOS Watch](assets/SURIOS_WATCH_REFERENCE_v1.png).

La figura expresa la intención visual y compositiva. Las coordenadas, dimensiones, estados y reglas textuales aprobadas prevalecen si existe alguna diferencia.

La antigua Figura 4.1 queda como referencia histórica sustituida.

## 3. Filosofía

- Interpretación inmediata.
- Hora como elemento dominante.
- Simetría antes que ocupación completa.
- Identidad técnica, limpia y minimalista.
- Información por encima de decoración.
- Componentes institucionales subordinados.
- Coherencia con [EDL v0.6](../EDL/EDL.md).

## 4. Lienzo

- Resolución lógica: `450 × 450`.
- Origen: `(0,0)`, esquina superior izquierda.
- Centro: `(225,225)`.
- Eje de simetría: `X=225`.

## 5. Naturaleza de las coordenadas

### 5.1 Geometría física validada

Hora y fecha usan exactamente los valores implementados y validados durante Sprint 001. Son fuente de verdad y no coordenadas orientativas.

### 5.2 Geometría objetivo

Las coordenadas restantes son objetivos de diseño pendientes de validación física en su Sprint. Un ajuste solo podrá aceptarse si:

- se documenta;
- no altera intención, jerarquía o simetría;
- recibe aprobación del propietario;
- se refleja en una nueva versión cuando cambie un valor normativo.

## 6. Jerarquía visual

1. Hora.
2. Fecha.
3. Batería.
4. Pasos.
5. Spotify.
6. Google Wallet.
7. Día de la semana.
8. Identificación SuriOS y perfil.
9. Emblema y separadores.

## 7. Composición general

```text
              DÍA DE LA SEMANA
                     HORA
                    FECHA

        PASOS                    BATERÍA

        WALLET                  SPOTIFY

          EMBLEMA INSTITUCIONAL
        SURIOS WATCH · CIVILIAN
```

## 8. Componentes

### 8.1 Hora

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Implementada y validada |
| x | 55 |
| y | 42 |
| width | 340 |
| height | 92 |
| Tamaño tipográfico | 82 |
| Formato | HH:MM, 24 horas |
| Alineación | Centro |

### 8.2 Fecha

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

### 8.3 Día de la semana

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Pendiente, prevista Sprint 006 |
| Centro X | 225 |
| Centro Y objetivo | 78 |
| Ancho objetivo | 180 |
| Alto objetivo | 24 |
| Formato | Texto completo, p. ej. LUNES |

La coordenada procede de v1.1 y permanece como objetivo de diseño. En combinación con la geometría física validada de la hora puede producir solapamiento, por lo que deberá reconciliarse y recibir aprobación expresa antes de implementar el componente.

### 8.4 Pasos

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Pendiente, Sprint 004 |
| Centro X objetivo | 105 |
| Centro Y objetivo | 225 |
| Diámetro de zona objetivo | 88 |
| Contenido activo | Número completo |
| Contenido ambiente | Número desde Sprint 004 |

No se mostrará barra, icono, porcentaje u objetivo diario.

### 8.5 Batería

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Pendiente, Sprint 004 |
| Centro X objetivo | 345 |
| Centro Y objetivo | 225 |
| Diámetro de zona objetivo | 88 |
| Contenido activo | Icono de diez niveles y porcentaje |
| Contenido ambiente | Porcentaje desde Sprint 004 |

No se mostrará barra. Durante carga, `RECHARGING` sustituirá la representación definida para cada modo.

### 8.6 Google Wallet

| Propiedad | Valor |
|---|---:|
| Estado documental | Objetivo aprobado; técnica pendiente |
| Implementación | Pendiente, Sprint 005 |
| Centro X objetivo | 145 |
| Centro Y objetivo | 335 |
| Diámetro objetivo | 64 |
| Tipo | Acceso directo |

No mostrará información adicional.

### 8.7 Spotify

| Propiedad | Valor |
|---|---:|
| Estado documental | Objetivo aprobado; técnica pendiente |
| Implementación | Pendiente, Sprint 005 |
| Centro X objetivo | 305 |
| Centro Y objetivo | 335 |
| Diámetro objetivo | 64 |
| Tipo | Acceso directo |

No mostrará controles, carátulas o información de reproducción.

### 8.8 Emblema de la Hermandad del Acero

| Propiedad | Valor |
|---|---:|
| Estado documental | Componente visual aprobado |
| Implementación | Pendiente, prevista Sprint 006 |
| Centro X objetivo | 225 |
| Centro Y objetivo | 235 |
| Tamaño máximo objetivo | 190 × 190 |
| Opacidad | Muy baja; valor pendiente de validación |

Deberá permanecer detrás de la información, no dificultar la lectura y usar únicamente un recurso aprobado y trazado.

### 8.9 Identificación SuriOS Watch

| Propiedad | Valor |
|---|---:|
| Estado documental | Aprobado |
| Implementación | Pendiente, prevista Sprint 006 |
| Centro X objetivo | 225 |
| Centro Y objetivo | 390 |
| Ancho objetivo | 170 |
| Alto objetivo | 30 |

El contenido incluirá el nombre `SuriOS Watch`.

### 8.10 Lema o identificación CIVILIAN

- **Estado documental:** componente visual aprobado.
- **Implementación:** pendiente, prevista Sprint 006.
- **Texto definitivo:** pendiente de aprobación.
- **Zona:** integrada con la identificación SuriOS, sin competir con datos funcionales.

No se inventará texto durante implementación.

### 8.11 Separadores gráficos

- **Estado documental:** aprobados cuando sean necesarios para reproducir la referencia.
- **Implementación:** pendiente.
- **Regla:** línea fina, PipGreenDim, sin rellenos, sombras ni degradados.
- **Geometría:** se definirá con el componente servido y se validará físicamente.

## 9. Simetría

- Batería y pasos serán simétricos respecto a `X=225`.
- Wallet y Spotify tendrán el mismo tamaño y distancia equivalente al eje.
- La información superior permanecerá centrada.
- Los componentes visuales no desplazarán hora y fecha.

## 10. Paleta y tipografía

- Fondo: PipBlack.
- Principal: PipGreen.
- Secundario: PipGreenDim.
- Sin colores fuera del EDL.
- Fuente: la aprobada por el proyecto.
- Excepción temporal: `SYNC_TO_DEVICE` hasta resolver la deuda aceptada.

## 11. Interacción

- Hora, fecha, día, batería, pasos, emblema, identificación y separadores: sin interacción.
- Spotify: toque único previsto, pendiente de validación técnica.
- Wallet: toque único previsto, pendiente de validación técnica.

## 12. Ambient Mode

### Sprint 003

- hora;
- fecha.

### Desde Sprint 004

- hora;
- fecha;
- porcentaje de batería;
- número de pasos.

Se ocultarán todos los componentes visuales, Spotify y Wallet. No cambiarán posiciones o tamaños.

## 13. Restricciones

- No modificar la geometría física de hora y fecha.
- No introducir barras en batería o pasos.
- No añadir requisitos funcionales desde este documento.
- No implementar componentes fuera de su Sprint.
- No alterar simetría o jerarquía sin nueva aprobación.
- No tratar la imagen conceptual como sustituto de las reglas textuales.

## 14. Criterios de aceptación visual

- lectura inmediata;
- jerarquía inequívoca;
- equilibrio y simetría;
- paleta EDL;
- geometría validada de hora y fecha intacta;
- componentes futuros sin solapamientos;
- fidelidad razonable a la Figura 1;
- prevalencia de reglas textuales ante diferencias;
- validación física por Sprint.

## 15. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.1 | Histórica, sustituida | Plano inicial con geometría y representaciones contradictorias. |
| 1.2 | Aprobada y vigente | Se subordina al WFPRD v1.4, consolida geometría física, indicadores e imagen oficial. |
