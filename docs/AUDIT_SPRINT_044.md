# Auditoria Sprint 044 - SuriOS Watch v1.8, RADAR compacto y RADAR PROTOYPE

---
document: AUDIT
project: SuriOS Watch / RADAR PROTOYPE
sprint: 044
version: 1.0
date: 2026-10-23
status: Aprobada para cierre documental
watchface_version: 1.8
watchface_version_code: 12
radar_app_version: 0.1
radar_app_version_code: 1
adb_endpoint: 192.168.1.63:41979
---

## Resultado

La auditoria completa no encuentra incidencias tecnicas bloqueantes dentro del
alcance del Sprint 044. La watchface v1.8, la app SuriOS Radar y el firmware
del prototipo ESP32 quedan compilados, documentados y desplegados en sus
entornos correspondientes.

## Alcance revisado

- Watchface v1.8: firma `v1.8`, `versionCode 12`, retirada de la linea de
  terminal del modo AMBIENT y descenso de los indicadores de objetivos.
- App RADAR: tipografia Consolas, paleta oficial, menu con rueda dentada y
  emblema, botones con el mismo patron de la watchface y cabecera textual
  institucional omitida por limitacion de espacio.
- RADAR PROTOYPE: firmware PlatformIO para ESP32 + LD2450, seguimiento de
  tres objetivos, eje X corregido para la representacion del reloj y filtrado
  de objetivos posteriores por zona y coordenadas.
- Documentacion: EDL, README de RADAR, registro de Sprints y copia `V0.bin`.

## Evidencia tecnica

| Comprobacion | Resultado |
|---|---|
| `gradlew.bat test lint :watchface:assembleDebug :radarwatch:test :radarwatch:lint :radarwatch:assembleDebug` | Correcto; `BUILD SUCCESSFUL` |
| `pio run` en `hardware/RADAR PROTOYPE` | Correcto |
| Parseo XML de `watchface.xml` | Correcto |
| `git diff --check` | Sin errores; solo avisos de finales de linea CRLF/LF |
| Instalacion de `radarwatch-debug.apk` por ADB | `Success` |
| Arranque de `com.suri.suriosradar` | Correcto; `MainActivity` visible |
| UI dump del menu RADAR | `SURIOS RADAR`, `REAL` y `SIM` presentes; `BROTHERHOOD OF STEEL` ausente |
| Backup `hardware/RADAR PROTOYPE/V0.bin` | Conservado; SHA-256 `40D6B38F3261578C7FA4A5E05D4C24D20403DE6111032969147D34A163798621` |

## Incidencias y riesgos residuales

1. El eco posterior del LD2450 requiere una prueba física con apantallamiento
   metálico. El filtrado de software ya está implementado; la comprobación
   restante es de montaje físico y no invalida el firmware compilado.
2. La desconexión completa de ADB durante una sesión BLE queda como prueba
   operativa heredada del Sprint 042. No se presenta como validada.

Ambas quedan registradas como pendientes no bloqueantes en
`docs/SPRINTS/ACTIVE_SPRINT.md`.

## Elementos fuera de alcance

La referencia `Ely.jpg`, la captura destinada a BRICKTOWN y el borrador
independiente `watchfaceely` permanecen como trabajo futuro intencional. No se
integran ni se despliegan dentro del Sprint 044.

## Decision

El Sprint 044 queda **CLOSED**. Esta auditoria queda **APROBADA PARA CIERRE
DOCUMENTAL** y el conjunto de cambios queda autorizado para commit y push.
