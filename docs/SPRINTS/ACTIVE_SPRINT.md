# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canonico
last_updated: 2026-08-30

---

## Sprint activo

Sprint 018 queda cerrado. [Sprint 018 v1.0](SPRINT_018_v1.0.md) — **CLOSED**

## Estado

Sprint 015: **CLOSED**
Sprint 016: **CLOSED**
Sprint 017: **CLOSED**
Sprint 018: **CLOSED**

Sprint 018 formaliza, implementa y valida la skin NECRON para PIP-SuriOS
Android. La skin conserva la funcionalidad compartida, aplica tokens y
emblema vectorial propios del prototipo, y mantiene legibilidad en Home y
P.R.S. sobre el Samsung A56. La selección es de sesión y vuelve a Brotherhood
tras reiniciar el proceso.

PIP-SuriOS v2.4 queda auditado y el Sprint 015 queda completado y cerrado
técnica, funcional y documentalmente a fecha 2026-08-30. El P.R.S. activo usa
`LOCAL SCAN`, `SCAN + PROBE`, `DEVICES` y `TRACK TARGET`, con histórico RSSI,
suavizado y nubes de densidad sin posicionamiento sintético. PIW-SuriOS Watch
v2.0 conserva el modo ambiente con el emblema girando sobre su eje vertical y
el PROBE operativo permanece separado como módulo Wear OS.

La edición `prsOnlyDebug` añade una superficie compacta para la pantalla
externa: `P.R.S.` centrado, radar a la izquierda y nombres a la derecha. La
edición completa y la reducida pueden añadir al nombre una categoría inferida
(`[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` o `[COMPUTER]`) cuando existe evidencia
reconocible; los dispositivos no identificables no reciben sufijo.

Sprint 016 queda cerrado de forma independiente. Sprint 017 queda cerrado tras
la auditoría técnica y el despliegue validado; cualquier trabajo ajeno a su
alcance requiere autorización expresa.

Los trabajos paralelos de mapas quedan fuera de este cierre y no se auditan ni
modifican aquí.

## Cierre de Sprint 018 de SuriOS Ecosystem / PIP-SuriOS

[Sprint 018 v1.0](SPRINT_018_v1.0.md), abierto y cerrado el 2026-08-30.

La skin NECRON queda documentada como prototipo privado, con referencias
públicas clasificadas, política de publicación separada y validación visual en
el A56. P.R.S. conserva el tratamiento NECRON porque no se observó distorsión
ni pérdida apreciable de lectura. La auditoría completa está en
[AUDIT_SPRINT_018](../AUDIT_SPRINT_018.md).

## Cierre de Sprint 017 de SuriOS Ecosystem / PIP-SuriOS

[Sprint 017 v1.0](SPRINT_017_v1.0.md), abierto y cerrado el 2026-08-30.

La iteración incorpora la edición compacta de P.R.S. para la pantalla externa
del Z Flip 6 y la inferencia de categorías de dispositivo en la edición reducida
y en PIP-SuriOS. La aceptación física queda como continuidad posterior; el
cierre técnico y documental de Sprint 017 está completado.

## Contexto del Sprint 015 de PIP-SuriOS

[Sprint 015 v1.0](SPRINT_015_v1.0.md), cierre documental finalizado el 2026-08-30.

**Sprint 015 - Reconstrucción P.R.S. y cierre PIW/PROBE-SuriOS**

Se valida el comportamiento de PIW en modo ambiente, se separa la esfera
`PROBE-SuriOS` de la lógica de PIW y se cierra la reconstrucción de P.R.S. con
adquisición BLE, análisis temporal, `TRACK TARGET`, `DEVICES` y GRID de
densidad. La auditoría deja como riesgos aceptados el transporte local sin
cifrado/autenticación y las limitaciones inherentes a RSSI/BLE.

## Roadmap de PIP-SuriOS

- Sprint 015 queda cerrado.
- PIP-SuriOS v2.4 queda auditado y consolidado.
- La guía imprimible del P.R.S. queda disponible en `output/pdf/PRS_CALIBRATION_GUIDE_SPRINT_015.pdf`.
- Se entregan las variantes `fullDebug` y `prsOnlyDebug` de P.R.S.
- Sprint 016 queda cerrado tras la revisión documentada de temas abiertos y el
  saneamiento de estado.
- Sprint 017 queda cerrado tras el refinamiento compacto de P.R.S. y la
  incorporación de categorías inferidas de dispositivo.

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
- La variante `prsOnlyDebug` arranca directamente en `SCAN`, conserva
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

La campaña física de P.R.S. y el ajuste de `PrsTuning.DEFAULT` quedan
cancelados por decisión expresa del propietario. La futura integración de
movimiento/orientación tampoco forma parte del Sprint 016. El resto de temas
abiertos queda trasladado a una futura autorización, sin Sprint activo.

## Addendum de cierre de SuriOS Watch y del AVD — 2026-08-30

La auditoría de esta terminal queda registrada en
[AUDIT_SPRINT_016.md](../AUDIT_SPRINT_016.md). El AVD adicional
`Galaxy_Watch_Ultra_2025` permanece disponible junto a los AVD anteriores y la
esfera OFICIAL `com.suri.surioswatch` queda validada en `v1.1` sobre Wear OS 5 /
API 34, 480 x 480, 320 dpi, Play Store y x86_64.

La esfera se dejó visible en `emulator-5554` con el marco circular, emblema,
progreso de pasos, batería sin porcentaje, firma `v1.1` y accesos CAPS/STATUS/
RADIO. La instalación en el Watch Ultra físico y la integración Orca-QGIS no
forman parte del cierre y quedan como continuidad.

## Referencias

- [Sprint 014 v1.0](SPRINT_014_v1.0.md)
- [Sprint 015 v1.0](SPRINT_015_v1.0.md)
- [Sprint 016 v1.1](SPRINT_016_v1.1.md)
- [Sprint 017 v1.0](SPRINT_017_v1.0.md) — cerrado
- [Auditoría Sprint 016](../AUDIT_SPRINT_016.md)
- [Auditoría Sprint 017](../AUDIT_SPRINT_017.md)
- [Auditoría Sprint 015](../AUDIT_SPRINT_015.md)
- [Sprint 013 v1.0](SPRINT_013_v1.0.md)
- [Sprint 012 v1.0](SPRINT_012_v1.0.md)
- [Sprint 011 v1.0](SPRINT_011_v1.0.md)
- [User Guide](../USER_GUIDE.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
