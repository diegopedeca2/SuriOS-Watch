# Investigación LD2450 / ESPHome / Radar v2 - Sprint 051

## Fuentes principales consultadas

1. [ESPHome LD2450](https://next.esphome.io/components/sensor/ld2450/):
   componente oficial, zonas, filtros, presencia, pérdida de enlace y límites
   de coordenadas.
2. [Código oficial `ld2450.cpp`](https://github.com/esphome/esphome/blob/dev/esphome/components/ld2450/ld2450.cpp):
   parser de la trama periódica, decodificación signed little-endian, velocidad,
   distancia, estados de presencia y callback posterior al procesamiento.
3. [Manual oficial HLK-LD2450](https://h.hlktech.com/download/HLK-LD2450-24G/1/HLK%20LD2450%201T2R%E8%BF%90%E5%8A%A8%E7%9B%AE%E6%A0%87%E6%A3%80%E6%B5%8B%E8%BF%BD%E8%B8%AA%E6%A8%A1%E7%BB%84%E8%AF%B4%E6%98%8E%E4%B9%A6%20V1.02%20.pdf): UART, protocolo y recomendaciones de instalación.
4. [TillFleisch/ESPHome-HLK-LD2450](https://github.com/TillFleisch/ESPHome-HLK-LD2450):
   zonas poligonales, márgenes para evitar flicker, diagnóstico por coordenadas,
   alimentación a 5 V y aumento de `rx_buffer_size` cuando se pierden frames.
5. [EverythingSmartHome/everything-presence-lite](https://github.com/EverythingSmartHome/everything-presence-lite/blob/main/common/ld2450-base.yaml):
   parser de 30 bytes, gestión de objetivos obsoletos, cadencia de actualización
   y limpieza explícita de estados.
6. [zomco/mmwave-component](https://github.com/zomco/mmwave-component/tree/main/docs/ld2450):
   separación entre zonas nativas persistentes del radar y filtrado poligonal
   de software; recomienda no asumir que una zona vieja se borra por omitirla.
7. [NorthWorks-Lab/NorthWorks-Wireless-Radar](https://github.com/NorthWorks-Lab/NorthWorks-Wireless-Radar):
   puente ESP32 de recepción UART y transporte de datos completos a una app
   Android, con medición de frames válidos del LD2450.
8. [eric-rolph/esp32-radar-tracker](https://github.com/eric-rolph/esp32-radar-tracker):
   visualización OLED, tres objetivos, modo diagnóstico y depuración del
   protocolo crudo.

## Funcionamiento relevante de ESPHome

El componente oficial espera tramas periódicas de 30 bytes con tres objetivos.
Para cada slot decodifica X e Y como coordenadas signed de 16 bits, velocidad
signed multiplicada por 10, resolución y distancia `sqrt(x²+y²)`. Un slot se
considera activo cuando la distancia es mayor que cero; si no, sus sensores de
detalle pasan a desconocido y las coordenadas internas se ponen a `(0, 0)`.

Los sensores numéricos aplican por defecto:

```yaml
- timeout:
    timeout: 1s
    value: last
- throttle_with_priority: 1000ms
```

La presencia `has_target`, `has_moving_target` y `has_still_target` conserva por
defecto 5 s después de la última detección. Esto es distinto de mantener una
posición válida indefinidamente: la posición es una lectura de coordenadas y
la presencia es un estado temporal.

El componente considera perdido el enlace cuando no llega una trama válida
durante `link_timeout`; una sala vacía con tramas válidas no es una pérdida de
enlace. Las zonas nativas son rectángulos y sus coordenadas documentadas son X
`-4860..4860` y Y `0..7560`.

## Comparación con nuestro proyecto

| Área | Antes | Radar v2 / Sprint 051 |
|---|---|---|
| Zona frontal | Y `0..6000` | Y `0..7560`, Detection forzado al arrancar |
| Retención firmware | 2 frames, ~200 ms | 10 frames, ~1 s para Radar actual |
| Datos al visualizador | 17 bytes: estado/X/Y | 29 bytes: estado/X/Y/velocidad/resolución |
| Semántica de presencia | implícita en el punto visible | presencia independiente con 5 s |
| MTU BLE | no solicitado | MTU 64 solicitado, fallback |
| Diagnóstico | RX/edad/secuencia | además slots, velocidad, resolución y conteos |
| Origen de coordenadas | X invertida para la pantalla v1 | Radar v2 transporta X/Y crudos de ESPHome |

## Conclusiones operativas

1. Una pérdida exacta a 3 m no puede atribuirse a la pantalla sin comprobar
   `radar_diag`; el valor nominal del módulo no garantiza el alcance del
   montaje.
2. Si UART mantiene `x/y` válidos y Radar v2 deja de aumentar `RX`, el problema
   está en BLE/MTU/cliente.
3. Si Radar v2 recibe el frame y el punto no aparece, el problema está en la
   transformación o el filtro de pantalla.
4. Si UART deja de emitir el objetivo mientras el enlace continúa vivo, deben
   probarse alimentación estable a 5 V, conectores, inclinación, carcasa,
   obstáculos, reflejos y blindaje posterior.
5. El blindaje posterior no se sustituye por un filtro estético: el manual y
   los proyectos de campo tratan los ecos y la geometría como variables físicas.
6. Para futuras mejoras estéticas no debe modificarse el contrato de datos ni
   la separación entre posición, presencia y estado del enlace.

## Plan de prueba física pendiente

Con el mismo montaje y alimentación, registrar simultáneamente:

- `radar_diag` durante 60 s sin objetivo;
- objetivo frontal a 1, 2, 3 y 6 m;
- movimiento lateral a 3 m;
- objetivo detrás y entorno detrás quieto;
- línea de Radar v2: `RX`, `SEQ`, `AGE`, `LOST`, `T/M/S/P`;
- si es posible, tensión de 5 V y cualquier reinicio del ESP32.

La prueba debe distinguir un hueco corto, que Radar v2 retiene 1 s, de una
pérdida sostenida del radar o del enlace.

## Resultado de la consulta interna del LD2450 (2026-11-19)

Se cargó temporalmente un firmware de consulta en el ESP32 WROOM. La lectura
se repitió varias veces y el sensor respondió correctamente a todas las
consultas:

- Firmware interno: `V2.04.23101915`.
- Modo de seguimiento: `0x0002`, multi-target.
- Filtrado nativo: activo en modo "detectar solo la zona".
- Zona 1: X `-4860..4860 mm`, Y `0..7560 mm`.
- Zonas 2 y 3: sin uso.

La zona almacenada coincide con los límites máximos documentados del módulo y
no puede explicar por sí sola que la detección frontal termine cerca de 2 o 3
metros. El firmware normal `radar-esphome.yaml` se restauró y volvió a entregar
tramas válidas por UART tras la prueba.

La siguiente comprobación debe centrarse en la lectura UART cruda a 3-6 m y en
la parte física: alimentación de 5 V, orientación, carcasa, obstáculos,
reflejos y posible blindaje posterior.

## Prueba directa a 3 m (2026-11-19)

La prueba se hizo con el ESP32 conectado directamente al PC y observando sus
lecturas, sin usar el reloj ni el enlace BLE. Durante la ida el LD2450 llego a
reportar `y=2254 mm` con `x=-405 mm`, una distancia aproximada de 2,3 m.
Despues dejo de entregar coordenadas validas durante unos 20 segundos. Al
volver el objetivo hacia el radar, las coordenadas reaparecieron.

Este resultado confirma que la perdida observada no se origina en la pantalla
del reloj ni en BLE. El siguiente diagnostico debe centrarse en el propio
LD2450 y en su parte fisica: alimentacion de 5 V bajo carga, cableado,
orientacion, carcasa, obstaculos, reflejos y blindaje posterior.

## Sustitucion del modulo y prueba a 3 m (2026-11-19)

Se sustituyo el primer LD2450 por otro modulo manteniendo el mismo ESP32,
firmware, cableado logico y procedimiento. El nuevo modulo entrego lecturas
validas hasta aproximadamente `y=3015 mm` y mantuvo valores cercanos a 3 m
durante mas de 20 segundos. Al regresar el objetivo, las lecturas volvieron a
acercarse al radar con normalidad.

La comparacion aisla el fallo en el primer modulo LD2450 o en su conexion
fisica inmediata. El ESP32 WROOM, el firmware normal, la alimentacion medida
y la aplicacion del reloj quedan descartados como causa principal del limite
de 2-3 m.

## Prueba del segundo modulo a 6 m (2026-11-19)

El segundo LD2450 mantuvo la deteccion durante mas de 15 segundos mientras el
objetivo se alejaba. La lectura maxima registrada fue aproximadamente
`x=-1798 mm`, `y=5350 mm`, equivalente a unos 5,6 m de distancia radial, y el
objetivo siguio siendo valido alrededor de 5,3 m en el tramo estable.

El modulo nuevo se comporta de forma coherente con el alcance esperado. El
primer modulo queda identificado como defectuoso o degradado, pendiente de
una inspeccion separada de sus conexiones fisicas.
