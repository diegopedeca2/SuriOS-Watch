# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canónico
last_updated: 2026-08-09

---

## Sprint activo

**Ninguno**

## Estado

Sprint 006 de PIP-SuriOS está completado y cerrado técnica, funcional y documentalmente.

No existe ningún Sprint activo. Sprint 007 de PIP-SuriOS no se ha iniciado.

PIW-SuriOS v1.9 continúa siendo la versión visual consolidada vigente de SuriOS Watch y no fue modificada durante este Sprint.

## Último Sprint completado de PIP-SuriOS

[Sprint 006 v1.0](SPRINT_006_v1.0.md), finalizado el 2026-08-09.

**Sprint 006 - Inventory y Current Gear PIP-SuriOS v1.4**

Commit técnico:

`148be0bf52c65813b8f42ca383f207cd8fc9e834`

Implementación final:

- INVENTORY incorpora el selector `ARMORY`, `CONSUMABLES` y `LOADOUTS`.
- ARMORY conserva íntegramente las categorías y fichas existentes.
- CONSUMABLES incorpora las ramas BBs, GRENADES y GAS.
- LOADOUTS incorpora HEADGEAR y FRONT PANEL.
- CURRENT GEAR incorpora PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES, HEADGEAR y FRONT PANEL.
- Los selectores dependientes reutilizan `InventoryItem` y `PrimaryWeaponRole` cuando corresponde.
- ACCESORIES permite multiselección temporal.
- HEADGEAR y FRONT PANEL presentan listados visuales dependientes del perfil o rol.
- Todo el estado de CURRENT GEAR es temporal mediante `remember`, sin persistencia.
- La identidad visible consolidada es `PIP-SuriOS v1.4`.

La validación manual fue superada en Samsung Galaxy A56 y Pixel 8 Emulator.

## Roadmap de PIP-SuriOS

- Sprint 006 queda cerrado.
- No existe Sprint activo.
- Sprint 007 no se ha iniciado.
- La planificación posterior requiere autorización expresa.

## Referencias

- [Sprint 006 v1.0](SPRINT_006_v1.0.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
