# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canonico
last_updated: 2026-08-30

---

## Sprint activo

**Ninguno**

## Estado

Sprint 015: **CLOSED**

PIP-SuriOS v2.3 queda auditado y el Sprint 015 queda completado y cerrado
técnica, funcional y documentalmente a fecha 2026-08-30. El P.R.S. activo usa
`LOCAL SCAN`, `SCAN + PROBE`, `DEVICES` y `TRACK TARGET`, con histórico RSSI,
suavizado y nubes de densidad sin posicionamiento sintético. PIW-SuriOS Watch
v2.0 conserva el modo ambiente con el emblema girando sobre su eje vertical y
el PROBE operativo permanece separado como módulo Wear OS.

No existe ningun Sprint activo posterior. Cualquier trabajo nuevo requiere autorizacion expresa.

## Ultimo Sprint completado de PIP-SuriOS

[Sprint 015 v1.0](SPRINT_015_v1.0.md), cierre documental finalizado el 2026-08-30.

**Sprint 015 - Reconstrucción P.R.S. y cierre PIW/PROBE-SuriOS**

Se valida el comportamiento de PIW en modo ambiente, se separa la esfera
`PROBE-SuriOS` de la lógica de PIW y se cierra la reconstrucción de P.R.S. con
adquisición BLE, análisis temporal, `TRACK TARGET`, `DEVICES` y GRID de
densidad. La auditoría deja como riesgos aceptados el transporte local sin
cifrado/autenticación y las limitaciones inherentes a RSSI/BLE.

## Roadmap de PIP-SuriOS

- Sprint 015 queda cerrado.
- PIP-SuriOS v2.3 queda auditado y consolidado.
- La guía imprimible del P.R.S. queda disponible en `output/pdf/PRS_CALIBRATION_GUIDE_SPRINT_015.pdf`.
- Se entregan las variantes `fullDebug` y `prsOnlyDebug` de P.R.S.
- No existe Sprint activo.

## Addendum histórico — 2026-08-29

La auditoría final incorpora la actualización de `P.R.S. TESTING`: modo dual por defecto, línea base obligatoria de 30 s, estados de evidencia de ubicación y CSV de 30 columnas sin coordenadas GPS en bruto. La compilación de los cuatro módulos, las pruebas unitarias y Lint vuelven a estar correctos.

En el corte de esta fecha el Watch 2 permanece conectado por ADB inalámbrico en `192.168.1.56:5555` y `RemoteProbeService` está activo. El A56 no aparece actualmente en `adb devices`, por lo que queda como acción de continuidad reconectar el teléfono y repetir la validación de despliegue móvil. El emulador está conectado, pero la aplicación móvil requiere API 35 y el emulador actual usa API 34.

El sprint queda cerrado documentalmente con esas limitaciones abiertas y explícitas; no se inicia un sprint posterior.

## Addendum de cierre — 2026-08-30

El cierre de hoy consolida el estado operativo real del repositorio:

- `DEVICES` queda dividido en `IDENTIFY DEVICE` y `SAVED DEVICES`, con reglas
  persistentes que pueden habilitarse, deshabilitarse o eliminarse.
- La edición completa conserva `LOCAL SCAN`, `SCAN + PROBE`, el subgrid de
  PROBE y la orientación horizontal del P.R.S. en el A56.
- La variante `prsOnlyDebug` arranca directamente en `LOCAL SCAN`, conserva
  `DEVICES` y no expone `SCAN + PROBE` ni registra el servicio PROBE.
- Se validó en la variante compacta el ciclo `SAVE → DISABLE → ENABLE → REMOVE`
  y no queda ninguna regla de prueba persistida.
- Se ejecutaron tests, ensamblados y lint de `fullDebug` y `prsOnlyDebug` con
  resultado `BUILD SUCCESSFUL`; `git diff --check` queda limpio.
- Durante la validación de despliegue, el A56 (`RZGYC07H0EX`) y el Watch 2
  (`192.168.1.56:5555`) figuraron conectados. La APK completa quedó restaurada
  e instalada en el A56 y la orientación se confirmó como
  `SCREEN_ORIENTATION_LANDSCAPE`.
- En la re-comprobación posterior al reinicio del daemon ADB, el Watch 2 pudo
  reconectarse y el A56 no volvió a aparecer en ese corte. Queda como acción
  operativa de continuidad, sin impacto en el resultado de los builds.

Queda pendiente únicamente la calibración física de `PrsTuning.DEFAULT` y la
futura integración de movimiento/orientación para refinar las nubes. No se
abre un sprint nuevo con este cierre.

## Referencias

- [Sprint 014 v1.0](SPRINT_014_v1.0.md)
- [Sprint 015 v1.0](SPRINT_015_v1.0.md)
- [Auditoría Sprint 015](../AUDIT_SPRINT_015.md)
- [Sprint 013 v1.0](SPRINT_013_v1.0.md)
- [Sprint 012 v1.0](SPRINT_012_v1.0.md)
- [Sprint 011 v1.0](SPRINT_011_v1.0.md)
- [User Guide](../USER_GUIDE.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
