# Auditoria Sprint 047 — App movil, SuriOS Radar y traductor de runas

- Fecha de auditoria: 2026-10-26
- Estado: `APROBADA PARA CIERRE DOCUMENTAL`
- Version movil: `3.6` (`versionCode 16`)
- Dispositivos: Samsung A56 `RZGYC07H0EX`; Galaxy Watch Ultra `SM-L705F`

## Resultado resumido

El alcance de interfaz y comunicacion del Sprint queda entregado y validado.
La unica deuda tecnica relevante es el ahorro de energia real del ESP32/LD2450:
la orden ECO llega al firmware, pero el framework Arduino usado devuelve
`ESP_ERR_NOT_SUPPORTED` para el modo de bajo consumo.

## Tabla de comprobaciones

| Comprobacion | Resultado | Evidencia |
|---|---|---|
| Version movil y copia previa | PASS | `app/build.gradle.kts`, `Previous Version/mobile-app-v3.5-sprint-047` |
| DETECTOR movil | PASS | Cliente BLE, protocolo de 17 bytes y pruebas unitarias |
| Traductor de runas | PASS | Pantalla vertical y `RunicCodecTest` |
| App radar del reloj | PASS | Cliente BLE, pantalla y pruebas del modulo `radarwatch` |
| Compilacion y tests | PASS | Gradle completo del Sprint |
| Lint del reloj | PASS | `:radarwatch:lint` |
| Instalacion | PASS | A56 y Watch Ultra |
| Botones y GRID | PASS | GRID sin cambio de tamaño/posicion; controles simetricos |
| Linea de onda | PASS | Arco completo del sector con animacion lineal de 10 segundos |
| Control ECO/NORMAL | PASS PARCIAL | Firmware registra ambas ordenes; ECO detiene lectura y conserva BLE |
| Apagado fisico / ahorro ESP32 | ABIERTO | `ESP_ERR_NOT_SUPPORTED` en la prueba serie |
| Corte de alimentacion LD2450 | ABIERTO | Requiere hardware adicional |

## Archivos y areas revisadas

- App movil: `app/src/main/java/com/suri/pipsurios/detector/`.
- Traductor: `app/src/main/java/com/suri/pipsurios/runic/` y
  `CommsRunicScreens.kt`.
- Pantallas compartidas: `ToolsScreens.kt`, `CommsMorseScreens.kt` y
  `MainActivity.kt`.
- Radar de reloj: `_retired-radar-20261025/watch-radarwatch/`.
- Firmware: `_retired-radar-20261025/hardware-RADAR-PROTOYPE/src/main.cpp`.

## Deuda para el siguiente Sprint

- `AUD-047-01`: compilar el firmware con una configuracion que soporte PM o
  deep/light sleep manteniendo el enlace BLE requerido.
- `AUD-047-02`: añadir corte de alimentacion controlado para el LD2450 y
  validar temperatura.
- `AUD-047-03`: repetir la prueba con ADB desconectado y comprobar el uso
  simultaneo desde movil y reloj.

## Conclusion

No hay bloqueos para cerrar el alcance de interfaz, BLE, traductor y radar
visual del Sprint 047. El Sprint se puede cerrar documentalmente, dejando los
tres puntos anteriores como seguimiento tecnico explicito.
