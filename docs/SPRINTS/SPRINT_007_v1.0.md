# Sprint 007 — STATUS y Complements PIP-SuriOS v1.5

---

document: SPRINT
sprint: 007
version: 1.0
project: PIP-SuriOS
type: Funcional
document_status: Cerrado
implementation_status: Completado
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-10

---

## 1. Objetivo

Implementar STATUS sobre un Loadout Activo compartido, ampliar ARMORY mediante COMPLEMENTS, generar recordatorios reutilizables en DON'T FORGET y consolidar la identidad visible como `PIP-SuriOS v1.5`.

## 2. Loadout Activo y APPLY

- CURRENT GEAR mantiene una configuración de borrador durante la sesión.
- El botón APPLY copia el borrador al Loadout Activo.
- STATUS consulta exclusivamente el Loadout Activo.
- Los cambios no aplicados no alteran STATUS.
- El estado es efímero y no utiliza persistencia.

## 3. STATUS

STATUS queda integrado como módulo funcional de HOME OPERATION con carga de 1500 ms.

Muestra en modo de sólo lectura:

- PRIMARY WEAPON;
- SECONDARY WEAPON;
- ACCESORIES;
- HEADGEAR;
- FRONT PANEL.

Los campos no aplicados muestran `NOT CONFIGURED`.

## 4. COMPLEMENTS

ARMORY incorpora la rama COMPLEMENTS con selectores dependientes ROLE y WEAPON/ITEM.

Roles disponibles:

- SNIPER;
- ASSAULT;
- HANDGUN;
- DEMOLITION;
- ACCESORIES.

El catálogo estructurado representa BBs, SLING, HOLSTER, MAGs, AMMO y OTHER. Las definiciones cubren L96, LevAR-15, MCX, APC-9K, DESERT EAGLE, AAP-01C, MGL, VOLCANO, DETON-A y THUNDER B. Las entradas sin definición permanecen en construcción sin inventar información.

## 5. DON'T FORGET

DON'T FORGET genera automáticamente los complementos correspondientes a PRIMARY WEAPON, SECONDARY WEAPON y ACCESORIES del Loadout Activo.

- reutiliza el mismo `ComplementCatalog` que COMPLEMENTS;
- no mantiene listas duplicadas;
- elimina duplicados exactos mediante consolidación simple;
- no suma cantidades ni aplica cálculos;
- dispone de scroll vertical con título y pie fijos.

## 6. Checklist interactivo

Cada línea de DON'T FORGET puede alternar visualmente entre `[ ]` y `[X]`.

- el estado se conserva únicamente en la composición actual;
- cambiar el Loadout y aplicar una nueva configuración regenera el checklist desmarcado;
- marcar elementos no modifica el catálogo, CURRENT GEAR, INVENTORY ni el Loadout Activo;
- no existe persistencia, exportación, porcentaje ni contador.

## 7. Arquitectura

- navegación integrada en el estado Compose existente de `MainActivity`;
- `LoadoutConfiguration` como modelo compartido de borrador y Loadout Activo;
- estado compartido elevado a `PIPSuriOSApp` mediante `remember`;
- copia explícita del borrador mediante APPLY;
- `ComplementCatalog` como fuente única para COMPLEMENTS y DON'T FORGET;
- clave compuesta ROLE + `InventoryItem` para evitar ambigüedades;
- checklist mediante estado Compose local y efímero;
- sin Room, DataStore, SharedPreferences ni ViewModel persistente.

## 8. Identidad visual

Todas las firmas visibles de la aplicación quedan consolidadas como `PIP-SuriOS v1.5`. El `versionName` técnico de Gradle no fue modificado.

## 9. Validaciones

Validación manual superada en:

- Samsung Galaxy A56;
- Pixel 8 Emulator.

Se verificó:

- CURRENT GEAR y APPLY;
- actualización inmediata del Loadout Activo;
- STATUS y valores `NOT CONFIGURED`;
- navegación completa de COMPLEMENTS;
- definiciones de todas las armas y accesorios implementados;
- generación compartida de DON'T FORGET;
- deduplicación simple;
- checklist `[ ] ↔ [X]`;
- regeneración del checklist tras cambiar CURRENT GEAR y pulsar APPLY;
- scroll, BACK, orientación y firma v1.5;
- compilación principal e incremental;
- lint;
- pruebas unitarias;
- `git diff --check`;
- ausencia de regresiones.

## 10. Guía de usuario

Se creó [PIP-SuriOS - User Guide](../USER_GUIDE.md) como manual oficial orientado al usuario final y preparado para ampliaciones en futuros Sprints.

## 11. Commit técnico

`1dbc32f6e13c9f2a94c7c55c533733647fb8f67a` — **Sprint 007 - STATUS y Complements PIP-SuriOS v1.5**

## 12. Estado final y roadmap

Sprint 007 de PIP-SuriOS cerrado el 2026-08-10.

No existe ningún Sprint activo. Sprint 008 no se ha iniciado.

## 13. Referencias

- [User Guide](../USER_GUIDE.md)
- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
