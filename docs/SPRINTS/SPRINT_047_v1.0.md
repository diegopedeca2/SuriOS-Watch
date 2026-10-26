# Sprint 047 v1.0 — App movil, SuriOS Radar y traductor de runas

- Apertura: 2026-10-25
- Cierre: 2026-10-26
- Estado: `CLOSED DOCUMENTALLY`
- Version movil: `3.6` (`versionCode 16`)
- Copia de seguridad previa: `Previous Version/mobile-app-v3.5-sprint-047`

## Alcance

- Crear y terminar `TOOLS > DETECTOR` conectado al ESP32 y al LD2450.
- Terminar `COMMS > RUNE TRANSLATOR` con disposicion vertical.
- Mantener una lectura de radar coherente entre la app movil y la app del reloj.
- Añadir control manual `ECO` / `NORMAL` desde movil y reloj.
- Corregir la posicion de los controles sin cambiar el tamaño ni la posicion
  del GRID.

## Entregado

- Cliente BLE y decodificador de paquetes del LD2450 en la app movil.
- Cliente BLE, controles y pantalla de radar en Wear OS.
- Traductor de lenguaje runico y sus pruebas unitarias.
- Movil: eliminada la linea `SEQ`; botones `ECO` y `NORMAL` con el mismo ancho
  y centrados simetricamente respecto a la pantalla.
- Reloj: eliminada la linea gris `SEQ`; botones pequeños `E` y `N` dentro de
  la rueda dentada.
- Movil y reloj: onda como linea curva que ocupa el ancho del sector del GRID
  y se repite cada 10 segundos.
- Fuentes antiguas del radar archivadas en `_retired-radar-20261025` y modulo
  de reloj reactivado para mantener la funcionalidad actual.

## Validacion realizada

- `testFullDebugUnitTest`: correcto.
- `assembleFullDebug`: correcto.
- `:radarwatch:test`: correcto.
- `:radarwatch:lint`: correcto.
- `:radarwatch:assembleDebug`: correcto.
- Comando completo validado:
  `gradlew.bat testFullDebugUnitTest assembleFullDebug :radarwatch:test :radarwatch:lint :radarwatch:assembleDebug`.
- APK movil instalada correctamente en Samsung A56 (`RZGYC07H0EX`).
- APK del radar instalada correctamente en Galaxy Watch Ultra
  (`SM-L705F`, ADB `192.168.1.63:41519`).
- Comprobacion final de accesibilidad: sin linea `SEQ`, botones simetricos en
  movil y botones `E` / `N` dentro de la zona dentada del reloj.
- El firmware registra las ordenes `RADAR_CONTROL=ECO` y `RADAR_CONTROL=NORMAL`.

## Temas abiertos

1. El modo `ECO` actual detiene el sondeo y las notificaciones del radar y
   mantiene Bluetooth, pero el firmware Arduino devuelve
   `ESP_ERR_NOT_SUPPORTED` al intentar activar el ahorro de energia del ESP32.
   Por tanto, el ESP32 y el LD2450 no quedan apagados fisicamente.
2. Para reducir el calentamiento del LD2450 hace falta un futuro cambio de
   hardware con corte de alimentacion mediante MOSFET, rele o equivalente.
3. Queda pendiente una prueba de campo con ADB desconectado y una validacion
   especifica de conexion simultanea movil + reloj.

Estos puntos no bloquean el cierre de la interfaz movil/reloj de este Sprint,
pero deben permanecer visibles para el siguiente trabajo de bajo consumo.

## Cierre

El Sprint 047 queda cerrado documentalmente. La version `3.6` queda preparada
para continuar el desarrollo, con las deudas de bajo consumo y alimentacion
fisica identificadas y separadas del trabajo visual y de comunicacion ya
validado.
