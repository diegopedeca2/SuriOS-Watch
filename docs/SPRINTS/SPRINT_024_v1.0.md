# Sprint 024 — Hardening SuriOS / PROBE companion

**Fecha de apertura:** 2026-08-31
**Estado:** Cerrado
**Alcance:** PIP-SuriOS, PIW-SuriOS PROBE, protocolo compartido y reproducibilidad GIS

## Objetivo

Cerrar las deudas técnicas AUD-023-01 a AUD-023-09 que afectan al funcionamiento
actual del ecosistema: A56 como aplicación principal y Watch 2 como accesorio
PROBE controlado por P.R.S.

## Decisiones aprobadas

1. PROBE no es standalone. Su `applicationId` es `com.suri.pipsurios`, alineado
   con la aplicación móvil companion; la watchface continúa siendo
   `com.suri.surioswatch.probewatchface`.
2. La comunicación privada actual se limita al par A56–Watch 2. Se valida el
   `sourceNodeId` real contra el nodo autorizado y no se añade criptografía hasta
   que exista distribución fuera del entorno privado.
3. PROBE no conserva telemetría. Las muestras se envían como mensajes en vivo al
   nodo del A56; no hay cola persistente, TTL ni replay de sesiones anteriores.
   Existe únicamente un buffer BLE en memoria, limitado a 256 dispositivos y 8
   mensajes en vuelo. Si el enlace falla, se detiene la adquisición.
4. Cada adquisición lleva un `sessionId`. El A56 solo acepta el nodo y la sesión
   activa. `PING` responde `PONG` sin iniciar ubicación ni Bluetooth.
5. MORSE declara y solicita `CAMERA` al entrar en la pantalla. Sin permiso o sin
   flash, la acción se muestra como no disponible.
6. MBTiles se invalida por SHA-256 esperado y se comprueba metadata de formato,
   zoom y bounds, además de tres teselas representativas. Una copia inválida se
   recrea de forma segura.
7. Las operaciones se guardan mediante temporal y renombrado en la misma carpeta;
   el conflicto de fecha mantiene el resultado `AlreadyExists`.
8. Los dispositivos de soporte operativo son Samsung A56, Samsung Z Flip 6,
   Samsung Watch Ultra y Xiaomi Watch 2. Los avisos de versiones de dependencias
   quedan como advisories: no se actualizan automáticamente dentro de este
   sprint sin una matriz de regresión específica.
9. QGIS LTR oficial: `3.44.13-Solothurn`. El generador exige todas las rutas por
   argumento; los proyectos QGIS y el GeoPackage de trabajo permanecen fuera del
   repositorio y el MBTiles Android sí queda versionado.

## Criterios de aceptación

- `:probe:lintVitalRelease` y los ensamblados de app, PROBE y watchfaces pasan.
- Pasan las pruebas JVM de `app` y `probeprotocol` en las variantes full y prsOnly.
- PROBE se instala y aparece en Watch 2 con el paquete companion correcto.
- El A56 mantiene la regresión instrumentada y recibe la telemetría de la sesión
  activa cuando ambos dispositivos están conectados.
- La validación física confirma en A56 la solicitud de CAMERA y el bloqueo de
  MORSE sin permiso, y confirma entre A56–Watch 2 el estado ACTIVE, la posición,
  batería y observaciones BLE remotas.
- El backlog y la auditoría reflejan el estado final y los límites aceptados.
