# Sprint 018 — Skin NECRON para PIP-SuriOS

---

document: SPRINT
sprint: 018
version: 1.0
project: SuriOS Ecosystem / PIP-SuriOS
document_status: Cerrado
implementation_status: Completado con deudas explícitas
priority: Media

---

## Estado

- Apertura: 2026-08-30.
- Cierre: 2026-08-30.
- Estado: cerrado técnica y documentalmente.
- Aplicación de referencia: PIP-SuriOS v2.4 (`versionCode 4`).
- Dispositivo de validación: Samsung A56 (`SM_A566B`).

## Objetivo

Crear la skin NECRON como prototipo privado para PIP-SuriOS, manteniendo la
funcionalidad y la distribución de Brotherhood of Steel, y comprobar su
aplicación en Home y P.R.S. sobre el A56.

## Entregado

- NECRON habilitada en el catálogo de skins.
- Paleta NECRON con blackstone, teal energético, bronce, neutral y estados
  críticos diferenciados.
- Tokens dinámicos compartidos por Home, MAP y P.R.S.
- Emblema NECRON vectorial nativo.
- P.R.S. tematizado con paneles, textos, mapa de densidad, contactos, estados
  de enlace y ruta de vuelta legibles.
- Tests unitarios de catálogo y paleta.
- Especificación visual y política de uso privado.

## Criterios de aceptación

| Criterio | Resultado |
|---|---|
| No cambia la funcionalidad compartida | Cumplido |
| Mantiene orientación y distribución horizontal | Cumplido |
| Mantiene contraste y lectura AMOLED | Cumplido en revisión A56 |
| P.R.S. conserva legibilidad | Cumplido; no se activa fallback |
| No incorpora assets externos | Cumplido; emblema vectorial nativo |
| Compilación, tests y lint | Cumplido |
| Uso privado documentado | Cumplido |

## Validación y despliegue

La matriz completa de validación está en [AUDIT_SPRINT_018](../AUDIT_SPRINT_018.md).
El APK completo se instaló en el A56 y NECRON quedó seleccionada y visible en
Home. La comprobación P.R.S. cubrió el menú y `LOCAL SCAN`, incluyendo radar,
cuadrícula, lista de contactos, estados y paneles.

La selección de skin no se persiste. Tras reiniciar la aplicación hay que
seleccionar `> NECRON` de nuevo, de acuerdo con el alcance del prototipo.

## Fuera de alcance

Los cambios de mapas offline, el asset HOME MBTiles, metadatos del IDE,
`ORCA-TO-DO` y `PICTURES` permanecen sin mezclar y requieren revisión propia.
No se incluyen en el commit de este Sprint.

## Estado del corte

Sprint 018 queda cerrado con NECRON implementada, validada e instalada en el
A56. La propiedad intelectual externa limita el resultado a uso privado; una
publicación futura deberá partir de una variante original sin elementos de
terceros.

Commit de cierre: commit que contiene este documento, con mensaje `Sprint 018 - NECRON skin audit and A56 validation`.
