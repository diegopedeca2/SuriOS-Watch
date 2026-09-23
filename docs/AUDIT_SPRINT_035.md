# Auditoría Sprint 035 — PIP-SuriOS MAP

---
document: AUDIT
project: SuriOS Ecosystem / PIP-SuriOS
sprint: 035
version: 3.3
date: 2026-09-23
status: Aprobada para cierre
---

## Resultado sencillo

La auditoría no encuentra errores críticos ni bloqueantes en el código del
Sprint 035. La aplicación queda en `versionName=3.3` y `versionCode=13`.
La alineación inicial del overlay se acepta como base operativa del Sprint;
la revisión fina de coordenadas en QGIS se difiere sin bloquearlo.

La prueba física de GPS, brújula, gestos, distancia y flecha queda registrada
como comprobación operativa posterior al cierre. No hay una incidencia de
código reproducida que impida cerrar el Sprint.

La regresión manual de cambio entre mapas ya se ha repetido con el A56
conectado y ha quedado corregida. La validación física de sensores y campo se
mantiene como comprobación operativa posterior al cierre.

La prueba física pendiente es de datos y sensores. Una prueba en interiores
puede mostrar errores de brújula por interferencias magnéticas y no basta para
validar el rumbo en campo. No impide compilar ni probar la aplicación.

## Cambios revisados

- ORGANIZATION OVERLAY separado de los datos base del mapa.
- GRID preparado para obtener la celda actual y la celda del destino.
- POI geográficos con `id`, `type`, `name`, `latitude` y `longitude`.
- AIRSOFT TOTAL regenerado desde cero con la paleta clara `SURIOS_DAY_V1`,
  siguiendo los criterios visuales de HOME, NAVY7 y OFFICE.
- Perímetro del campo revisado como capa vectorial independiente; RESPawn 1
  queda dentro del perímetro provisional.
- Cobertura AIRSOFT TOTAL comprobada: 4 km x 4 km, centro conservado y margen
  suficiente para ver el campo al hacer zoom hacia fuera.
- OFFICE incorpora los POI `RAST`, `REPLICANT`, `ELÍAS` y `CHURROSTAR`.
- USER WAYPOINT y ORGANIZATION POI comparten `MapDestination` y
  `NavigationEngine`.
- La flecha NAV pasa a 44sp y el mapa usa tipografía sans-serif para mejorar
  la lectura con luz natural.
- Las etiquetas de GRID conservan `PipAmber`; los nombres de POI usan `PipRed`,
  25% más de tamaño y negrita para mejorar la lectura con luz natural.
- El cambio de mapa protege contra cargas SQLite antiguas: cancela la carga
  anterior, cierra el recurso anterior y descarta resultados obsoletos.
- Revisión de orientación: el código usa `TYPE_ROTATION_VECTOR`, remapea los
  ejes según la pantalla horizontal y aplica suavizado circular. No se aprecia
  una inversión fija de 90°/180° en la transformación. Sí queda una limitación
  conocida: no se descartan lecturas con baja precisión magnética, por lo que
  interiores, vehículos y objetos metálicos pueden provocar desvíos o saltos.
- El mapa gira alrededor del centro de la ventana. Para valorar la alineación
  con el teléfono, primero hay que recentrar sobre el punto azul del usuario;
  si el mapa está desplazado, el marcador puede parecer que se mueve al girar
  aunque el cálculo de coordenadas sea correcto.
- Se conservan RESPawns, RAD ZONE, Geiger, DELETE/CLEAR/EMPTY y pinch-to-zoom.
- No se ha añadido routing, A*, Dijkstra, navegación giro a giro ni integración
  con CivTAK.

## Comprobaciones técnicas

- `test`: correcto.
- `:app:assembleFullDebug`: correcto.
- `:app:lintAnalyzeFullDebug`: correcto.
- `:app:connectedFullDebugAndroidTest`: correcto, 7/7 pruebas en
  `SM-A566B` / A56, API 35.
- `git diff --check`: sin errores de espacios; los avisos restantes son solo
  conversiones normales de finales de línea CRLF/LF de Git.
- MBTiles: formato PNG, zoom 16–19, 6.727 teselas, `PRAGMA integrity_check`
  devuelve `ok`.
- Metadata MBTiles: `style=SURIOS_DAY_V1` y bounds
  `-4.293653996060,40.792677278878,-4.246246827323,40.828697017677`.
- SHA-256 del asset AIRSOFT TOTAL:
  `92C78730E0846F4E0FF3092299A6981F021750302995713DFFE6EE8D5865B350`.
- QGIS 3.44.13: generación de teselas, lectura de metadata y exportación de
  una tesela de control correctas.
- APK `fullDebug` instalada en el A56 con resultado `Success`; versión
  confirmada: `3.3`, `versionCode=13`.
- Comprobación manual en A56 antes de la última desconexión: `OFFICE` cargó el
  overlay, mostró `RAST`, `NAVIGATE` abrió el HUD con flecha grande, distancia,
  rumbo y `STOP NAVIGATION`; no hubo excepciones fatales en logcat.
- Tras reconectar el A56 (`RZGYC07H0EX`), la APK `3.3` volvió a instalarse con
  `Success` y `:app:connectedFullDebugAndroidTest` volvió a terminar con 7/7.
- La prueba manual AIRSOFT TOTAL → OFFICE → AIRSOFT TOTAL → OFFICE se repitió
  en el A56, incluyendo dos ciclos adicionales, sin cierre de la aplicación ni
  `FATAL EXCEPTION` en `logcat`.
- La causa del cierre detectado durante la auditoría era una etiqueta GRID del
  overlay anterior dibujada muy lejos de la pantalla durante la transición.
  Se corrigió limpiando el overlay antes de cargar el nuevo mapa, publicando
  solo datos del mapa seleccionado y omitiendo etiquetas fuera de un margen
  seguro del canvas.

## Puntos no bloqueantes y mejoras futuras

| ID | Gravedad | Estado | Descripción |
|---|---|---|---|
| AUD-035-01 | Baja | Aceptada y cerrada | La alineación inicial del overlay se acepta como base operativa. La revisión fina en QGIS queda como mejora posterior; se conserva la marca `PROVISIONAL_QGIS_REVIEW` para no falsear precisión. |
| AUD-035-02 | Media | Validación operativa posterior | Caminar físicamente con el A56 para comprobar actualización de distancia y estabilidad de la brújula. No bloquea el cierre técnico. |
| AUD-035-03 | Baja | Futuro | La carga inicial de MBTiles puede optimizarse en otro Sprint; no afecta a la corrección de los datos ni a la navegación. |
| AUD-035-04 | Media | Resuelta | Cambio OFFICE → AIRSOFT TOTAL → OFFICE repetido en A56; no se reproduce el cierre y no aparecen excepciones fatales. |

## Decisión

El código, los assets y la documentación están preparados para la prueba
física. El punto de QGIS se da por completo dentro de este Sprint. La auditoría
queda aprobada para cierre y la prueba de campo se realizará como verificación
operativa posterior, sin mantener abierto el Sprint.
