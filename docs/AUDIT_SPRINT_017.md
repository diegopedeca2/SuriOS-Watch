# Auditoría técnica — Sprint 017

Fecha: 2026-08-30
Estado: CERRADO — aceptación física registrada como deuda explícita
Proyecto: SuriOS Ecosystem / PIP-SuriOS

## Alcance

Se audita la iteración de refinamiento de P.R.S. para la pantalla externa del
Z Flip 6 y la incorporación de categorías de dispositivo en `prsOnlyDebug` y
en PIP-SuriOS `fullDebug`. Sprint 016 conserva su propio cierre histórico;
esta iteración queda atribuida exclusivamente a Sprint 017.

Los trabajos paralelos de mapas quedan fuera de alcance: no se auditan,
no se modifican y no se incluyen en las conclusiones de este documento.

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
| Arranque compacto v2.4 en Z Flip 6 | `Status: ok` |
| Arranque completo v2.4 en A56 | `Status: ok` |

Una ejecución conjunta de las suites unitarias presentó un `NoSuchMethodError`
de classpath entre variantes. Las dos suites se repitieron por separado y
terminaron correctamente; no se considera un fallo funcional.

## Dispositivos y despliegue

En el cierre ADB se localizaron:

- Z Flip 6, `SM-F741B`, serial inalámbrica
  `adb-R5CX7102VQJ-zKA5nY._adb-tls-connect._tcp`, con
  la APK final `versionName=2.4-prs` instalada con `Success` tras la
  reconexión y arrancada con `Status: ok`.
- Samsung A56, `SM_A566B`, serial `RZGYC07H0EX`, con
  `com.suri.pipsurios` `versionName=2.4` instalado con `Success` y arrancado
  con `Status: ok`.
- Emulador `emulator-5554`, conectado.

La variante reducida se verificó visualmente anteriormente en
`tmp/prs_updated.png`; esa comprobación corresponde al despliegue compacto
previo a la actualización técnica de versión.

## Límites y continuidad

- Las pruebas físicas de aceptación en moto siguen pendientes como validación
  posterior explícita.
- No se ha declarado calibración física ni conversión de RSSI a distancia.
- La conexión ADB y el despliegue final de la v2.4 reducida en el Z Flip 6
  quedan confirmados en esta revisión.
- Este documento registra el estado de validación técnica de Sprint 017;
  sólo permanece pendiente la aceptación física.

## Estado del corte

Sprint 017 queda cerrado técnica y documentalmente. El alcance queda
implementado, compilado, instalado y arrancado en los dispositivos de
validación; la aceptación física en moto permanece como continuidad posterior.
