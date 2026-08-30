# Sprint 017 - P.R.S. compacto y clasificación de dispositivos

---

document: SPRINT
sprint: 017
version: 1.0
project: SuriOS Ecosystem / PIP-SuriOS
document_status: En curso
implementation_status: En curso
priority: Media
---

## Estado

- Apertura: 2026-08-30.
- Cierre: pendiente.
- Estado: en curso.
- Sprint activo posterior: ninguno; este Sprint sigue activo.
- Aplicación móvil de referencia: PIP-SuriOS v2.4 (`versionCode 4`).

## Objetivo

Optimizar la edición reducida de P.R.S. para la pantalla externa del Samsung Z
Flip 6 e incorporar categorías prácticas de dispositivo tanto en la edición
reducida como en la edición completa de PIP-SuriOS.

## Entregado

- `P.R.S.` centrado en la edición reducida.
- Radar centrado verticalmente en la mitad izquierda.
- Lista de nombres en la mitad derecha, con tamaño legible y sin información
  auxiliar no útil durante la conducción.
- Retirada de `LOCAL SCAN`, `A56: SCANNING`, `DENSITY ONLY // AZIMUTH N/A`, el
  contador de nodos y textos secundarios de la superficie compacta.
- Inferencia de `[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` y `[COMPUTER]` en ambas
  ediciones. Si el dispositivo no es identificable, no se muestra sufijo.
- Sin margen de confianza ni signos de interrogación en la interfaz.

## Criterio de clasificación

La inferencia usa el nombre anunciado, la clase Bluetooth y BLE Appearance.
Es una ayuda de lectura rápida y no una identificación definitiva de fabricante
o modelo. La incertidumbre propia de anuncios BLE incompletos queda aceptada.

## Validación

- Tests unitarios de `fullDebug` y `prsOnlyDebug`: `BUILD SUCCESSFUL` en
  ejecuciones aisladas.
- Lint de `fullDebug` y `prsOnlyDebug`: `BUILD SUCCESSFUL`, sin errores.
- Ensamblado de ambas APK: `BUILD SUCCESSFUL`.
- `git diff --check`: salida correcta (`exit 0`).
- Samsung Z Flip 6 (`SM-F741B`): `app-prsOnly-debug.apk` instalada con
  `Success`; paquete `com.suri.pipsurios.prs`, `versionName=2.4-prs`;
  actividad principal arrancada con `Status: ok`.
- Samsung A56 (`SM_A566B`): `app-full-debug.apk` instalada con `Success`;
  paquete `com.suri.pipsurios`, `versionName=2.4`; actividad principal
  arrancada con `Status: ok`.
- La comprobación visual previa de la edición compacta queda en
  `tmp/prs_updated.png`.

## Pendientes

Las pruebas físicas en moto —legibilidad, reflejos, estabilidad y uso con
señales reales— quedan como validación posterior. No se ha realizado
calibración física ni se han modificado los valores de `PrsTuning.DEFAULT`.

## Estado del corte

Sprint 017 no queda cerrado en este cambio. La aceptación física y cualquier
ajuste posterior permanecen dentro del Sprint en curso.

Auditoría: [AUDIT_SPRINT_017](../AUDIT_SPRINT_017.md).
