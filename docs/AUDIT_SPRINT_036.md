# Auditoría Sprint 036 — AIRSOFT TOTAL + BETA TESTER BUILDS

---
document: AUDIT
project: SuriOS Ecosystem / PIP-SuriOS
sprint: 036
version: 3.4
version_code: 14
date: 2026-09-24
status: Aprobada para cierre
---

## Resultado sencillo

La auditoría no encuentra errores críticos ni bloqueantes en el código del
Sprint 036. La aplicación queda en `versionName=3.4` y `versionCode=14`.

Se validan los cambios visuales de AIRSOFT TOTAL, el perfil Android de JAVI,
el mapa TESTING de JAVI y la generación de las cuatro distribuciones BETA.
No se ha añadido routing ni lógica de cálculo de caminos.

La prueba física completa de GPS, brújula, navegación y Bluetooth queda como
verificación operativa posterior. No hay una incidencia reproducida de código
que impida cerrar el Sprint.

## Código revisado

- `app/build.gradle.kts`: versión 3.4/código 14, perfil JAVI, nombre de la
  aplicación e icono específicos.
- `TerrainModels.kt`: mapa TESTING de JAVI con el centro solicitado
  `40.431175043353754, -3.638558623703386`, bounds que contienen ese centro,
  asset independiente y hash registrado.
- `MapTerrainScreen.kt`: GRID negro de 3,5 px, caminos azules de 4,5 px con
  halo oscuro y POI rojos con halo negro. Las capas continúan separadas.
- `MapTerrainTest.kt`: comprobación del catálogo de JAVI y de su centro.
- `package_beta_testers.ps1`: comprueba los mapas requeridos, exige una orden
  explícita para generar una nueva distribución y limita la salida a `output`.
- No se introducen dependencias online ni se modifica el motor de navegación.

## Assets y distribución

- MBTiles TESTING de JAVI: 1.753 teselas, estilo `SURIOS_DAY_V1`, integridad
  `ok` durante su generación.
- SHA-256 del MBTiles JAVI:
  `229972A70C3AE47746A5E1D2E4BACF0947FD78A1ACBD8200D69007DEDE1A2207`.
- Se generan BETA para FENRIR, ALTAMIRA, CHECHU y JAVI. Cada paquete contiene
  APK, guía, plantilla CSV, SHA-256 y ZIP individual.
- La APK JAVI se instala en el A56 y arranca como
  `com.suri.pipsurios.javi`, versión 3.4/código 14.

## Comprobaciones técnicas

- `test`: correcto para MAIN.
- `test -PdistributionProfile=JAVI`: correcto.
- `:app:assembleFullDebug`: correcto.
- `:app:lintAnalyzeFullDebug`: correcto.
- `:app:connectedFullDebugAndroidTest`: 7/7 correctas en el Samsung A56
  conectado (`SM-A566B`).
- `git diff --check`: sin errores de espacios; solo avisos normales de
  conversión de finales de línea CRLF/LF.
- Las cuatro APK declaran `versionName=3.4` y `versionCode=14`.
- Los ZIP BETA contienen los cuatro archivos esperados y sus hashes están
  incluidos en cada paquete.
- No aparecen excepciones fatales en el buffer de crash del A56 tras instalar
  y arrancar JAVI.

## Puntos no bloqueantes

| ID | Gravedad | Estado | Descripción |
|---|---|---|---|
| AUD-036-01 | Media | Validación operativa posterior | Comprobar en campo la estabilidad de GPS, brújula, flecha NAV y distancia. La prueba en interiores no sustituye la de campo. |
| AUD-036-02 | Baja | Documentado | El mapa JAVI se generó con estilo claro y datos offline de edificios/caminos; la fuente de elevación no estaba disponible durante la generación, por lo que las curvas de nivel quedan para una revisión futura de asset si fueran necesarias. |

## Decisión

El código, los assets, las APK BETA y la documentación están preparados para
la prueba física. La auditoría queda aprobada para cierre y el Sprint 036 se
marca como **CLOSED**. Los puntos anteriores no son bloqueantes ni impiden la
distribución BETA.
