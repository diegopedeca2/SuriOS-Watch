# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canónico
last_updated: 2026-08-10

---

## Sprint activo

**Ninguno**

## Estado

Sprint 008 de PIP-SuriOS está completado y cerrado técnica, funcional y documentalmente.

No existe ningún Sprint activo. Sprint 009 de PIP-SuriOS no se ha iniciado.

PIW-SuriOS v1.9 continúa siendo la versión visual consolidada vigente de SuriOS Watch y no fue modificada durante este Sprint.

## Último Sprint completado de PIP-SuriOS

[Sprint 008 v1.0](SPRINT_008_v1.0.md), iniciado y finalizado el 2026-08-10.

**Sprint 008 - Tools (Geiger Counter y Sonar) PIP-SuriOS v1.7**

Commit técnico:

`353edbf212e810e29583db5d91400eb3dfac9ec9`

Implementación final:

- TOOLS proporciona acceso a GEIGER COUNTER y SONAR mediante la navegación Compose existente.
- GEIGER COUNTER es una simulación inmersiva controlada con VOLUME UP, con aguja analógica y clics de frecuencia variable.
- SONAR escanea exclusivamente señales BLE permitidas por Android.
- El tracking mantiene identificadores temporales, RSSI suavizado, baseline y estados BACKGROUND/NEW sólo durante la sesión.
- CALIBRATE registra el entorno conocido como baseline.
- El radar utiliza VERY CLOSE, CLOSE, MEDIUM y FAR sin mostrar metros ni dirección física.
- El barrido dispone de pulso general y avisos diferenciados para contactos BACKGROUND y NEW.
- La identidad visible consolidada es `PIP-SuriOS v1.7`.
- No se incorporaron sensores reales, Wi-Fi, UWB, persistencia ni identificación de personas.

Las validaciones automáticas y la validación manual en Samsung Galaxy A56 fueron superadas. Pixel 8 Emulator se utilizó para las comprobaciones aplicables de interfaz, navegación y audio.

## Roadmap de PIP-SuriOS

- Sprint 008 queda cerrado.
- No existe Sprint activo.
- Sprint 009 no se ha iniciado.
- La planificación posterior requiere autorización expresa.

## Referencias

- [Sprint 008 v1.0](SPRINT_008_v1.0.md)
- [User Guide](../USER_GUIDE.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
