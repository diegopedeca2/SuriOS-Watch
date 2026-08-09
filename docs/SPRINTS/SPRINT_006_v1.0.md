# Sprint 006 — Inventory y Current Gear PIP-SuriOS v1.4

---

document: SPRINT
sprint: 006
version: 1.0
project: PIP-SuriOS
type: Funcional
document_status: Cerrado
implementation_status: Completado
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-09

---

## 1. Objetivo

Ampliar el módulo INVENTORY, implementar la primera versión navegable y funcional de CURRENT GEAR y consolidar la identidad visible como `PIP-SuriOS v1.4`.

## 2. INVENTORY

INVENTORY incorpora una pantalla previa de selección con tres ramas.

### ARMORY

- SNIPER
- ASSAULT
- DEMOLITION
- HANDGUN
- ACCESORIES

ARMORY conserva íntegramente las fichas, contenidos, colores, scroll y retornos existentes.

### CONSUMABLES

- BBs
- GRENADES
- GAS

Las ramas y submenús son visuales y navegables, sin cantidades, edición ni persistencia.

### LOADOUTS

- HEADGEAR
- FRONT PANEL

Los perfiles y paneles son visuales y navegables. No existe todavía selección activa ni lógica de loadout.

## 3. CURRENT GEAR

CURRENT GEAR queda accesible desde OPERATION - HOMESCREEN mediante una pantalla `LOADING...` de 1500 ms.

Categorías implementadas:

- PRIMARY WEAPON
- SECONDARY WEAPON
- ACCESORIES
- HEADGEAR
- FRONT PANEL

### Comportamiento

- PRIMARY WEAPON utiliza selectores dependientes ROLE y WEAPON.
- SECONDARY WEAPON utiliza selectores dependientes TYPE y WEAPON para HANDGUN y DEMOLITION.
- ACCESORIES permite multiselección temporal de varios elementos.
- HEADGEAR conserva PROFILE como selector y muestra ITEM como listado visual dependiente.
- FRONT PANEL conserva ROLE como selector y muestra PANEL como listado visual dependiente.
- Los selectores dependientes limpian automáticamente las selecciones incompatibles.
- Se reutilizan `InventoryItem` y `PrimaryWeaponRole` siempre que corresponde.
- El estado se gestiona temporalmente mediante `remember`.
- No se implementan persistencia, guardado, cantidades ni equipamiento activo.

## 4. Arquitectura

- Navegación integrada en el estado Compose existente de `MainActivity`.
- Nuevas pantallas Compose específicas para CURRENT GEAR.
- Componentes de selector con estética terminal propia.
- Catálogo compartido entre ARMORY y CURRENT GEAR mediante `InventoryItem` y `PrimaryWeaponRole`.
- Estado efímero local sin DataStore, SharedPreferences, Room ni ViewModel de persistencia.

## 5. Identidad visual

Todas las firmas visibles de la aplicación quedan consolidadas como:

`PIP-SuriOS v1.4`

El `versionName` técnico de Gradle no se modifica.

## 6. Validaciones

Validación manual superada en:

- Samsung Galaxy A56
- Pixel 8 Emulator

Se verificó:

- navegación completa de INVENTORY;
- conservación íntegra de ARMORY;
- navegación de CONSUMABLES y LOADOUTS;
- funcionamiento de las cinco categorías de CURRENT GEAR;
- selectores dependientes;
- multiselección temporal de ACCESORIES;
- listados visuales de HEADGEAR y FRONT PANEL;
- BACK en todos los niveles;
- orientación horizontal;
- ausencia de recortes y regresiones;
- compilación principal e incremental correctas;
- lint correcto;
- pruebas unitarias correctas;
- `git diff --check` correcto.

## 7. Commit técnico

`148be0bf52c65813b8f42ca383f207cd8fc9e834` — **Sprint 006 - Inventory y Current Gear PIP-SuriOS v1.4**

## 8. Estado final y roadmap

Sprint 006 de PIP-SuriOS cerrado el 2026-08-09.

No existe ningún Sprint activo. Sprint 007 no se ha iniciado.

## 9. Referencias

- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
