# Auditoría Sprint 051 - Radar v2 / LD2450

---
document: AUDIT
project: SuriOS Radar
sprint: 051
date: 2026-11-19
status: CLOSED_TECHNICAL_PHYSICAL_LIMIT_ISOLATED
---

## Alcance

- Investigación técnica sobre ESPHome, HLK-LD2450 y repositorios de referencia.
- Auditoría de `radar-esphome.yaml`, protocolo BLE y cliente Android/Wear OS.
- Creación de Radar v2 sin tocar ELY.
- Compilación y pruebas automáticas.

## Hallazgos

- No existe un umbral de 2 o 3 m en el visualizador anterior.
- El firmware tenía una retención de solo dos tramas ausentes.
- La zona se forzaba hasta 6000 mm, por debajo del límite Y documentado por
  ESPHome de 7560 mm.
- El visualizador anterior no transportaba velocidad ni resolución, por lo que
  no podía reflejar completamente los datos del componente oficial.
- Un paquete v2 de 29 bytes exige negociar MTU BLE antes de suscribirse.
- En la primera APK Radar v2 el escaneo filtraba estrictamente por nombre de
  dispositivo. El ESPHome registró `Partial data write into ADV`; Wear OS no
  entregaba de forma fiable ese nombre al filtro aunque el servicio sí estaba
  anunciado.

## Cambios auditados

- `D:\WristOS\_sprint049-esphome\radar-esphome.yaml`
  - zona Detection frontal `-4860..4860 / 0..7560`;
  - retención v1 de 10 frames;
  - IDs de velocidad y resolución;
  - característica v2 con trama completa de 29 bytes.
- `D:\WristOS\_retired-radar-20261025\watch-radarwatch\src\main\java\com\suri\suriosradar\ble\RadarBleClient.kt`
  - característica de datos configurable;
  - negociación MTU 64 con fallback;
  - compatibilidad conservada con Radar v1;
  - escaneo sin filtro rígido, validación por nombre/UUID de servicio y
    escritura de CCCD mediante la API BLE actual; el estado de notificación se
    reinicia al cerrar un GATT fallido.
- `D:\WristOS\_radar-v2`
  - módulo `:radarv2` independiente;
  - parser de trama v2;
  - presencia 5 s y coordenadas 1 s;
  - conteos de objetivos y diagnóstico;
  - etiqueta `Radar v2`, applicationId `com.suri.suriosradar.v2` e icono
    identificable de señal/radar.

## Verificación

- ESPHome 2026.10.2 `config`: PASS.
- ESPHome 2026.10.2 `compile`: PASS.
- `:radarv2:testDebugUnitTest`: PASS.
- `:radarv2:assembleDebug`: PASS.
- `:radarwatch:testDebugUnitTest`: PASS.
- `git diff --check`: PASS; solo avisos de normalización LF/CRLF de Git.
- Flasheo en `COM4`: PASS; `esptool` verificó el hash de la imagen escrita.
- Instalación ADB en `192.168.1.63:42347`: PASS para
  `com.suri.suriosradar` y `com.suri.suriosradar.v2`.
- Prueba de conexión Radar v2: PASS; el reloj detectó `SuriOS Radar`, conectó,
  descubrió el servicio `9e5d0001-...`, negoció MTU 517 y habilitó la
  característica v2 con CCCD status 0.
- Prueba fisica controlada 2026-11-09: el UART mantuvo coordenadas validas
  durante la aproximacion hasta aproximadamente `x=-254, y=1921` mm
  (distancia directa aproximada 1,94 m); despues paso a `valid=0`/`nan` durante
  el tramo lejano y recupero el objetivo al volver a `y=1775` mm.
- Durante la segunda prueba no se observaron desconexiones GATT en el reloj.
  El enlace BLE no explica la perdida del objetivo.
- Prueba con firmware diagnostico 2026-11-09: el objetivo se mantuvo hasta
  `y=1899` mm; inmediatamente despues paso a `target_count=0` durante el
  tramo lejano. En la vuelta reaparecio en `y=1538` mm y despues en `885`,
  `465` y `340` mm. La perdida y la recuperacion ocurren en el UART del
  LD2450, antes de cualquier visualizador.
- Firmware diagnostico 2026-11-09: `esptool` identifico un `ESP32-D0WD-V3`
  clasico con flash de 4 MB; no es un WROVER ni una variante incompatible con
  GPIO16/17.
- El LD2450 respondio correctamente al protocolo de configuracion: ACK de
  entrada/salida, modo multiobjetivo (`0x0002`) y zona activa (`tipo=1`) con
  coordenadas `(-4860, 0)` a `(4860, 7560)` mm; las zonas 2 y 3 estan vacias.
  La lectura de firmware devuelve una version de la familia `2.04` con fecha
  `23101915`.
- La salida UART en reposo son tramas periodicas validas con los tres objetivos
  vacios. Los avisos transitorios de longitud durante la consulta se deben a
  que ESPHome consulta automaticamente la informacion al arrancar y la variante
  diagnostica repite esas consultas; no indican por si solos un fallo de RF.
- Medicion bajo carga 2026-11-09: `ESP 5V-GND=4,74 V` y `LD 5V-GND=4,72 V`.
  La caida entre la placa y el radar es de solo 0,02 V; la alimentacion por
  USB queda validada y deja de ser la hipotesis principal.

## Artefactos

- Firmware factory: `D:\WristOS\output\sprint051\sprint051-radar.factory.bin`
  - 715872 bytes
  - SHA-256 `09C72FC2B2208F365CBCE64E3992A59EC43C9968BA486BB67042EA5A16E47738`
- APK Radar v2: `D:\WristOS\output\sprint051\sprint051-radar-v2-debug.apk`
  - SHA-256 `CB8A9349E5D1CC59A1FBBFE873D12BAF05B91A3B0D94A525B8768A4912D711EF`
- Icono Radar v2: `D:\WristOS\_radar-v2\src\main\res\drawable-nodpi\radar_v2_icon.png`
  - 2276238 bytes

## Cierre técnico final — 2026-11-19

La correlación UART/BLE/pantalla atribuye la pérdida alrededor de 2 m al primer
módulo LD2450: dejó de producir coordenadas válidas mientras el firmware
continuaba emitiendo tramas. La zona configurada permite `Y=7560` mm, por lo
que no hay un corte software a 2 m en el proyecto.

La sustitución por un segundo LD2450 mantuvo la detección hasta aproximadamente
5,6 m. Esto aísla la incidencia en el primer módulo o en su conexión física
inmediata. La alimentación medida bajo carga no muestra una caída relevante y
el ESP32-WROOM, el UART, BLE y los visualizadores quedan operativos.

La auditoría final terminó con `test`, `assembleDebug`, `lintDebug`,
comprobación XML, `git diff --check` y verificación ADB sin errores. Radar Mini
recibe tramas V2 de 29 bytes con tres ranuras de objetivo y la esfera conserva
los accesos RADAR y CAPS mediante sus aplicaciones puente.

Queda como continuidad no bloqueante una prueba física dedicada a varios
objetivos y la confirmación final del eje tras la corrección de inversión; no
se detectó un fallo de software durante esta auditoría.

## Diagnostico intermedio previo a la sustitucion del modulo

La correlacion UART/BLE/pantalla atribuye la perdida alrededor de 2 m al tramo
anterior a BLE: el LD2450 deja de producir coordenadas validas mientras el
firmware continua emitiendo tramas. La zona configurada permite `Y=7560` mm,
por lo que no hay un corte software a 2 m en el proyecto.

La causa fisica/configuracion queda pendiente: orientacion, obstrucciones o
apantallamiento, reflexiones del entorno y estado del modulo. La alimentacion
medida bajo carga no muestra una caida relevante.
El siguiente paso es revisar el montaje y, si procede, contrastar el modulo
con una lectura UART directa o con otra unidad LD2450.

El firmware diagnóstico está compilado y flasheado en `COM4`; la lectura
interna confirma que la placa ESP32, el UART y la zona almacenada del LD2450
son coherentes. El cierre técnico sigue condicionado a una prueba de alcance
con el log `radar_diag` y, si persiste el límite, a revisar el montaje o el
módulo físico.
