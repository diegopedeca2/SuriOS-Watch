# Sprint 006 — Accesos directos CAPS, STATUS y RADIO

---

document: SPRINT
sprint: 006
version: 1.0
project: SuriOS Watch
type: Funcional
document_status: Aprobado
implementation_status: Completado
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-08

---

## 1. Objetivo

Convertir las referencias visuales existentes `CAPS`, `STATUS` y `RADIO` en accesos directos funcionales sin modificar la estética aprobada de la esfera.

Destinos:

- `CAPS` → Google Wallet.
- `STATUS` → aplicación de Estadísticas/Salud del Xiaomi Watch 2.
- `RADIO` → Spotify.

---

## 2. Alcance ejecutado

La implementación se limitó a:

- añadir interacción a las tres etiquetas existentes;
- conservar Watch Face Format v1;
- mantener posiciones, tamaños, fuente, color, alineación y comportamiento visual;
- validar compilación, instalación y funcionamiento físico.

No se modificaron emblema, geometría, hora, fecha, batería, pasos, firma inferior, recursos gráficos, colores ni tipografías.

---

## 3. Implementación

Único archivo técnico modificado:

`watch/watchface/src/main/res/raw/watchface.xml`

Se añadió un elemento declarativo `Launch` a cada `PartText` existente:

| Referencia | Destino |
|---|---|
| CAPS | `com.google.android.apps.walletnfcrel/com.google.commerce.tapandpay.wear.cardlist.WalletThemedWearCardListActivity` |
| STATUS | `com.xiaomi.wear.fitness/com.xiaomi.wear.fitness.sport.component.vitality.SportVitalityActivity` |
| RADIO | `com.spotify.music/com.spotify.wear.main.MainActivity` |

No fueron necesarios código ejecutable, servicios, permisos, dependencias, recursos ni cambios de Gradle o manifiesto.

La arquitectura WFF v1 prevista resultó suficiente y no fue necesario introducir mecanismos alternativos.

---

## 4. Auditoría técnica

Resultados satisfactorios:

- XML bien formado.
- `git diff --check` sin errores.
- exactamente tres elementos `Launch`.
- validación mediante WFF Validator 1.7.0 contra Watch Face Format v1.
- `:watchface:assembleDebug` correcto.
- compilación incremental correcta y completamente `UP-TO-DATE`.
- `assembleDebug` conjunto correcto.
- APK instalada correctamente en Wear OS Large Round.
- APK instalada correctamente en Xiaomi Watch 2.
- ausencia de cambios visuales en el diff técnico.

---

## 5. Validación funcional

La validación funcional definitiva fue realizada manualmente en Xiaomi Watch 2 con resultado satisfactorio:

- `CAPS` abre correctamente Google Wallet.
- `STATUS` abre correctamente la aplicación de Estadísticas/Salud.
- `RADIO` abre correctamente Spotify.
- si Google Wallet solicita PIN o desbloqueo del reloj, se considera comportamiento de seguridad esperado y acceso correcto.

También se verificó la conservación del renderizado aprobado y la ausencia de regresiones en hora, fecha, batería, pasos, emblema, firma inferior y Ambient Mode.

---

## 6. Limitaciones

- Los destinos se vinculan a componentes concretos instalados y verificados en Xiaomi Watch 2; un cambio futuro de paquetes o actividades por parte de sus proveedores podría requerir mantenimiento.
- Wear OS Large Round no dispone de Google Wallet, Spotify ni la aplicación propietaria de Salud de Xiaomi; por ello la validación funcional definitiva de los destinos se realizó en el dispositivo físico objetivo.
- La seguridad de Google Wallet puede exigir desbloqueo o PIN antes de mostrar la cartera.

Estas limitaciones no bloquean el cierre del Sprint.

---

## 7. Commit técnico

`b1a4d1605cac01e86380fb6294b0cfc995ed4de0` — **Sprint 006 - Accesos directos CAPS, STATUS y RADIO**

Archivo incluido:

`watch/watchface/src/main/res/raw/watchface.xml`

---

## 8. Estado final

Sprint 006 completado el 2026-08-08.

No existe ningún Sprint activo. Sprint 007 no se ha iniciado.

El proyecto queda preparado para una futura ronda final de pequeños ajustes estéticos orientados a Suri WatchOS v1.9. La versión 2.0 permanece reservada y no iniciada.

---

## 9. Referencias

- [ACTIVE_SPRINT](ACTIVE_SPRINT.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [WFPRD v1.5](../WFPRD/WFPRD_v1.5.md)
- [WATCHFACE_LAYOUT v1.3](../WATCHFACE_LAYOUT/WATCHFACE_LAYOUT_v1.3.md)
