# Auditoría técnica — Sprint 017

Fecha: 2026-08-30
Estado: EN CURSO
Proyecto: SuriOS Ecosystem / PIP-SuriOS

## Alcance

Se audita la iteración de refinamiento de P.R.S. para la pantalla externa del
Z Flip 6 y la incorporación de categorías de dispositivo en `prsOnlyDebug` y
en PIP-SuriOS `fullDebug`. Sprint 016 conserva su propio cierre histórico;
esta iteración queda atribuida exclusivamente a Sprint 017.

## Implementación revisada

- `PrsTrackingScreen.kt`: layout compacto con cabecera centrada, radar a la
  izquierda y listado a la derecha; la lista reducida muestra sólo nombres.
- `PrsDeviceCategory.kt`: categorías y reglas de inferencia por nombre, clase
  Bluetooth y BLE Appearance.
- `PrsModels.kt` y `BleScanner.kt`: conservación de clase Bluetooth y lectura
  compatible de `addressType` para API 35 o superior.
- `PrsDevicesScreen.kt` y `PrsTrackingScreen.kt`: sufijos de categoría en las
  superficies completa y reducida.
- `PrsDeviceClassifierTest.kt`: cobertura de nombres, clase Bluetooth,
  Appearance y caso no identificable.

## Resultado visible

Las categorías permitidas son `[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` y
`[COMPUTER]`. Cuando no existe evidencia suficiente no se añade nada. La
interfaz no expone confianza ni signos de interrogación. La clasificación es
heurística y no garantiza fabricante, modelo ni identidad permanente.

## Pruebas y calidad

| Control | Resultado |
|---|---|
| Tests unitarios `fullDebug` | `BUILD SUCCESSFUL` |
| Tests unitarios `prsOnlyDebug` | `BUILD SUCCESSFUL` |
| Lint de ambas variantes | `BUILD SUCCESSFUL`, sin errores |
| Ensamblado de ambas variantes | `BUILD SUCCESSFUL` |
| `git diff --check` | Exit 0 |
| Arranque compacto en Z Flip 6 | `Status: ok` |
| Arranque completo en A56 | `Status: ok` |

Una ejecución conjunta de las suites unitarias presentó un `NoSuchMethodError`
de classpath entre variantes. Las dos suites se repitieron por separado y
terminaron correctamente; no se considera un fallo funcional.

## Dispositivos y despliegue

En el cierre ADB se localizaron:

- Z Flip 6, `SM-F741B`, serial inalámbrica
  `adb-R5CX7102VQJ-zKA5nY._adb-tls-connect._tcp`, con
  `com.suri.pipsurios.prs` `versionName=2.4-prs` instalado y arrancado.
- Samsung A56, `SM_A566B`, serial `RZGYC07H0EX`, con
  `com.suri.pipsurios` `versionName=2.4` instalado y arrancado.
- Emulador `emulator-5554`, conectado.

La variante reducida se verificó visualmente anteriormente en
`tmp/prs_updated.png`.

## Límites y continuidad

- Las pruebas físicas de aceptación en moto siguen pendientes.
- No se ha declarado calibración física ni conversión de RSSI a distancia.
- La conexión ADB del Z Flip 6 queda confirmada en este corte y ambas APK han
  sido desplegadas; cualquier reconexión posterior es operativa, no un bloqueo
  del sprint.
- Este documento registra un corte de validación; no constituye el cierre de
  Sprint 017.

## Estado del corte

Sprint 017 continúa en curso. El alcance implementado y verificado en esta
terminal queda disponible para continuar, y la aceptación física queda
pendiente.
