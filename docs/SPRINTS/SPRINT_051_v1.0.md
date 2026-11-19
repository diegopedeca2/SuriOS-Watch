# Sprint 051 v1.0 - Radar v2 alineado con ESPHome

- Apertura: 2026-11-09
- Estado: CLOSED TECHNICALLY
- Proyecto: SuriOS Radar / HLK-LD2450 + ESP32-WROOM + Galaxy Watch Ultra
- Continuidad: Sprint 050, prueba física de alcance realizada; causa física
  pendiente

## Objetivo

Investigar la pérdida percibida del objetivo a 3 m y crear una aplicación Radar
v2 que reproduzca la semántica de datos de ESPHome antes de aplicar cambios
estéticos.

## Resultado de la auditoría

El visualizador anterior no contiene un corte a 2 o 3 m. La divergencia más
relevante estaba en el adaptador: retenía únicamente dos tramas ausentes,
aproximadamente 200 ms a 10 Hz, mientras ESPHome usa filtros de coordenadas de
1 s y presencia por defecto de 5 s. La zona frontal se limitaba además a
6000 mm, aunque el componente documenta coordenadas Y hasta 7560 mm.

Esto no demuestra que el LD2450 alcance 3 m en el montaje actual. Si
`radar_diag` deja de mostrar coordenadas válidas, la causa sigue estando en el
radar, alimentación, orientación, obstáculos o instalación; Radar v2 sirve
para separar esa pérdida de una pérdida posterior en BLE o pantalla.

## Implementación

- La zona Detection se fuerza en cada arranque a X `-4860..4860` y Y
  `0..7560`.
- Radar actual conserva el protocolo de 17 bytes, pero tolera 10 tramas
  ausentes, aproximadamente 1 s.
- El firmware añade una característica BLE v2 con UUID
  `9e5d0004-7b3a-4c0f-91c2-245000000001`.
- Cada trama v2 contiene 29 bytes: versión, secuencia y, para los tres slots,
  estado, X, Y, velocidad y resolución, en little-endian.
- El cliente BLE solicita MTU 64 antes de activar notificaciones y tiene un
  fallback para stacks que rechacen la negociación.
- El escaneo BLE no depende de que Wear OS exponga el nombre en el anuncio:
  valida el servicio del radar por UUID y configura el CCCD con la API actual.
- El módulo `:radarv2` reutiliza el transporte y recursos existentes, pero usa
  `com.suri.suriosradar.v2`, etiqueta `Radar v2` y un visualizador separado,
  instalable en móvil Android o Wear OS.
- Radar v2 aplica retención de coordenadas de 1 s, presencia de 5 s, contador
  total/móvil/estático y diagnóstico de secuencia/edad.
- No se modifican ELY ni las watchfaces.
- Radar v2 incorpora `radar_v2_icon.png`: conserva el marco industrial de la
  aplicaciÃ³n y sustituye el emblema central por una seÃ±al de radar con arcos,
  punto, anillos y retÃ­cula.

## Validación automática

- ESPHome 2026.10.2 `config`: PASS.
- ESPHome 2026.10.2 `compile`: PASS; imagen factory de 715872 bytes.
- `:radarv2:testDebugUnitTest`: PASS.
- `:radarv2:assembleDebug`: PASS.
- `:radarwatch:testDebugUnitTest`: PASS.
- Firmware factory flasheado en `COM4` con esptool; escritura y hash verificados.
- Las APK Radar actual y Radar v2 instaladas en Galaxy Watch Ultra mediante
  `192.168.1.63:42347`.
- Radar v2 conectado y verificado en el reloj: servicio descubierto, MTU 517
  y notificaciones v2 habilitadas.
- La prueba física controlada confirmó coordenadas válidas hasta unos 1,94 m y
  pérdida de coordenadas a partir de aproximadamente 2 m; el retorno recuperó
  el objetivo. No hubo desconexión GATT durante la sesión.
- Firmware diagnóstico flasheado y verificado en `COM4`: ESP32-D0WD-V3 clásico,
  LD2450 en modo multiobjetivo y zona almacenada `(-4860, 0)` a
  `(4860, 7560)` mm. La placa y el filtro de zona no explican el límite de 2 m.
- Segunda prueba con diagnóstico: el objetivo llegó a `y=1899` mm, desapareció
  durante el tramo lejano y reapareció en `y=1538` mm al regresar. El límite se
  confirma en el propio LD2450/UART.
- Alimentación medida bajo carga: `ESP 5V-GND=4,74 V` y `LD 5V-GND=4,72 V`,
  con una caída de solo 0,02 V. La alimentación USB no explica el límite.
- La revisión de orientación, obstrucciones/apantallamiento, reflexiones y
  estado físico del módulo sigue pendiente.

## Artefactos

- `D:\WristOS\output\sprint051\sprint051-radar.factory.bin`
- `D:\WristOS\output\sprint051\sprint051-radar-v2-debug.apk`
- Icono: `D:\WristOS\_radar-v2\src\main\res\drawable-nodpi\radar_v2_icon.png`
- Diagnóstico: `D:\WristOS\output\sprint051\sprint051-radar-diagnostic.factory.bin`

## Criterio de cierre

El sprint queda cerrado tecnicamente el 2026-11-19. La correlacion entre UART,
BLE y pantalla aislo la perdida inicial en el primer modulo LD2450. Un segundo
modulo mantuvo la deteccion hasta aproximadamente 5,6 m, mientras que el
ESP32-WROOM, el firmware, el enlace BLE y los visualizadores pasaron la
auditoria automatica y operativa.

La validacion especifica de varios objetivos y la confirmacion fisica final del
eje corregido quedan como continuidad no bloqueante.

El sprint se cerró tras una prueba física que correlacionó el log UART
`radar_diag`, el contador/edad de Radar v2 y el marcador mostrado a 1, 2, 3 y
6 m, y se determine la causa del límite aproximado de 2 m en el propio radar o
en su montaje.
