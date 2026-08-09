# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canónico
last_updated: 2026-08-09

---

## Sprint activo

**Ninguno**

## Estado

Sprint 005 de PIP-SuriOS está completado y cerrado técnica, funcional y documentalmente.

No existe ningún Sprint activo. Sprint 006 de PIP-SuriOS no se ha iniciado.

PIW-SuriOS v1.9 continúa siendo la versión visual consolidada vigente de SuriOS Watch y no fue modificada durante este Sprint.

## Último Sprint completado de PIP-SuriOS

[Sprint 005 v1.0](SPRINT_005_v1.0.md), finalizado el 2026-08-09.

**Sprint 005 - Implementación de MORSE TERMINAL**

Commit técnico:

`84a2c42e6a82f2fa1a3b863deff0eb9b362f60ba`

Implementación final:

- COMMS incorpora un selector entre `FREQUENCIES` y `MORSE`.
- `FREQUENCIES` conserva la tabla PMR existente sin cambios funcionales.
- `COMMS // MORSE TERMINAL` incorpora los modos `TEXT > MORSE` y `MORSE > TEXT`.
- Las conversiones funcionan completamente offline con soporte inicial A-Z y 0-9.
- `TEXT > MORSE` utiliza una Activity vertical exclusiva y permite transmitir mediante la linterna.
- La transmisión incorpora `TRANSMIT // FLASH`, `STOP`, cancelación segura y apagado garantizado del flash.
- Ambos modos incorporan controles `CLEAR` y `DELETE` adecuados a su entrada.
- La aplicación oculta la barra superior mediante `WindowInsetsController` y conserva la navegación inferior.
- La temporización Morse depende de una constante centralizada y configurable.

La validación manual fue superada en Samsung Galaxy A56 y Pixel 8 Emulator, incluida la transmisión real mediante flash en el Galaxy A56.

## Estado estable anterior

Sprint 004 de PIP-SuriOS consolidó PIP-SuriOS v1.0 con MAP, tabla PMR de COMMS, INVENTORY y navegación local. Sprint 005 amplía ese estado estable sin modificar MAP, INVENTORY, HOME, PIW-SuriOS ni la watchface.

## Roadmap de PIP-SuriOS

- MORSE TERMINAL queda implementado dentro del módulo COMMS mediante Sprint 005.
- Sprint 005 queda cerrado.
- No existe Sprint activo.
- Sprint 006 no se ha iniciado.
- La planificación posterior conserva su carácter informativo y requiere autorización expresa para comenzar.

## Referencias

- [Sprint 005 v1.0](SPRINT_005_v1.0.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
