# Comparativa PIP-SuriOS Alpha v2.9 frente a Beta v3.4

---
document: COMPARATIVA_DISTRIBUCION
project: SuriOS Ecosystem / PIP-SuriOS
baseline: Primera distribución Alpha, Sprint 029
target: Distribución Beta, Sprint 036
date: 2026-09-24
status: Cierre técnico aprobado; validación física operativa pendiente
---

## 1. Resumen

La Alpha v2.9 fue la primera distribución para FENRIR, ALTAMIRA y CHECHU. La
Beta v3.4 conserva la aplicación y los módulos principales, pero incorpora las
mejoras realizadas desde aquella primera entrega y añade el perfil JAVI.

La comparación se centra en el comportamiento que recibirá un tester. No se
consideran cambios internos que no modifiquen la experiencia de uso.

## 2. Identidad de la aplicación

| Dato | Alpha | Beta |
|---|---|---|
| Versión visible | `2.9` | `3.4` |
| Código interno | `9` | `14` |
| Paquete principal | `com.suri.pipsurios` | `com.suri.pipsurios` |
| Perfiles tester | FENRIR, ALTAMIRA, CHECHU | FENRIR, ALTAMIRA, CHECHU, JAVI |
| Distribución histórica | Alpha | Beta |
| Mapa común de perfiles | NAVY7 | NAVY7 |
| Mapa específico | TESTING por perfil | TESTING por perfil |

La Beta no sustituye ni borra los paquetes Alpha históricos. Cada APK Beta se
identifica con el nombre y el icono del tester correspondiente.

Los cuatro paquetes se han generado en `output/SPRINT_036_BETA/`. Cada uno
incluye la APK, la guía, la plantilla de pruebas, el SHA-256 y un ZIP propio.

## 3. Cambios visibles para el tester

| Área | Alpha v2.9 | Beta v3.4 |
|---|---|---|
| MAP | Mapas offline iniciales | Mapas offline ampliados y capas de organización separadas |
| GRID | Visualización inicial | Líneas negras más gruesas e identificadores de celda legibles |
| POI | Puntos pequeños con poca separación | Rojo oficial, marcador mayor y halo oscuro contra las curvas |
| Caminos | Sin el tratamiento visual actual | Caminos internos azules, más intensos y con halo oscuro |
| Perímetro | Disponible según el mapa | Capa de perímetro diferenciada en los mapas de organización |
| Navegación | Sin el flujo actual de waypoint | `SET WAYPOINT`, `NAVIGATE`, distancia, dirección relativa y parada |
| Destinos | No existía el destino común actual | Un POI y un waypoint usan el mismo motor de navegación |
| Flecha NAV | No disponible como función actual | Flecha grande con giro continuo de 360 grados |
| TRACKER | Flujo inicial de búsqueda y seguimiento | Búsqueda más eficiente, filas compactas y menos texto repetido |
| Carga | Animación inicial | El cursor de escritura acompaña a las letras durante la animación |
| Lectura en campo | Estética inicial | Tipografías más legibles y jerarquía visual reforzada |

## 4. Perfiles y mapas TESTING

| Perfil | Mapa TESTING en Beta | Centro conocido |
|---|---|---|
| FENRIR | Mapa existente conservado | El MBTiles conserva sus bounds históricos; no se encontró en la documentación el centro decimal original |
| ALTAMIRA | Mapa específico conservado | `40.34897942140349, -3.818235386395919` |
| CHECHU | Mapa específico conservado | `40.433753, -3.625904` |
| JAVI | Mapa nuevo generado desde cero | `40.431175043353754, -3.638558623703386` |

El mapa de JAVI se genera como un asset independiente. No se recorta ni se
recentra el mapa de otro tester. Su icono usa la carcasa de la aplicación
principal con el emblema Unown J y la etiqueta `PIP-J`.

## 5. MAP principal y referencia AIRSOFT TOTAL

La APK principal `MAIN` de la Beta incorpora AIRSOFT TOTAL como referencia
para futuros mapas. Este mapa contiene:

- raster topográfico offline;
- curvas de nivel;
- edificios y caminos del mapa base;
- GRID de organización;
- perímetro del campo;
- caminos internos extraídos del mapa de referencia;
- POI de organización;
- RESPawns y RAD ZONE editables;
- navegación básica hacia POI o waypoint.

La navegación sigue siendo deliberadamente sencilla. No incluye routing,
carreteras, A*, Dijkstra, instrucciones giro a giro ni integración con
CivTAK.

## 6. Cambios técnicos de distribución

- Las APK Beta se generan mediante un empaquetador separado del histórico de
  Alpha.
- Cada perfil incluye su APK, guía, plantilla CSV y SHA-256.
- Cada perfil se entrega en un ZIP independiente.
- Las APK de perfiles no activan PROBE; la APK MAIN conserva la configuración
  completa de desarrollo.
- El proceso de generación comprueba que NAVY7 y el TESTING correspondiente
  existen antes de crear el paquete.

## 7. Validación

La APK JAVI se ha instalado y ha arrancado en el A56 conectado. La comprobación
de instalación no sustituye la prueba física completa en campo.

Antes de enviar las Beta se deben comprobar:

1. compilación `fullDebug`;
2. tests JVM;
3. lint;
4. carga offline de NAVY7 y TESTING;
5. nombre e icono de cada perfil;
6. P.R.S. y TRACKER;
7. lectura del GRID, POI y caminos en la MAIN cuando proceda;
8. instalación y apertura en el A56;
9. prueba física de GPS, brújula, gestos y Bluetooth.

La validación física no se sustituye por una prueba de emulador o por una
comprobación mediante ADB.

## Conclusión

La Beta v3.4 es una evolución de la Alpha v2.9, no una aplicación diferente.
Mantiene el paquete Android y los flujos principales, pero añade una base de
mapas más amplia, mejor lectura en campo, navegación offline, organización de
POI/GRID y el nuevo perfil JAVI.
