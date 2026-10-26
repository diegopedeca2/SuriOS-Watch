# SuriOS Radar — ESP32 + HLK-LD2450

Firmware inicial para la placa ESP32 de `RADAR PROTOYPE`.

## Cableado previsto

Con la placa ESP32 mirando al USB:

| LD2450 | ESP32 |
|---|---|
| 5V | 5V/VIN |
| GND | GND |
| TX | GPIO16 / RX2 |
| RX | GPIO17 / TX2 |

El LD2450 necesita alimentación de 5 V y una fuente capaz de entregar más de
200 mA. La señal UART es de 3,3 V. TX y RX se conectan cruzados.

## Comportamiento

- Lee los tres bloques de objetivos del LD2450 a 256000 baudios, 8N1.
- Activa el modo multiobjetivo del radar.
- Configura una zona de detección frontal de `x=-6000..6000`, `y=0..6000` mm.
- Rechaza por software objetivos fuera de la zona frontal o fuera de ±60°.
- Exige dos frames consecutivos para confirmar un contacto.
- Conserva un contacto confirmado hasta dos frames ausentes para evitar parpadeos.
- Publica exactamente el paquete BLE de 17 bytes que espera la app Wear OS.
- Invierte el eje X antes de enviarlo al reloj; se puede cambiar con
  `INVERT_X_FOR_WATCH` en `src/main.cpp`.

## BLE

- Nombre: `SuriOS Radar`
- Servicio: `9e5d0001-7b3a-4c0f-91c2-245000000001`
- Característica de datos: `9e5d0002-7b3a-4c0f-91c2-245000000001`

## Compilar y cargar

Con PlatformIO instalado:

```powershell
pio run
pio run --target upload --upload-port COM4
pio device monitor --port COM4 --baud 115200
```

La copia original de la memoria flash está en `V0.bin`. El firmware todavía no
debe considerarse calibrado hasta probar físicamente el sentido del eje X y la
zona trasera con el LD2450 conectado.
