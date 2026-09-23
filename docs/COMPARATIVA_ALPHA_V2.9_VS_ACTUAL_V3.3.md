# Comparativa PIP-SuriOS Alpha v2.9 frente a versión actual v3.3

---
document: COMPARATIVA_TECNICA
project: SuriOS Ecosystem / PIP-SuriOS
baseline: Primera distribución Alpha, Sprint 029
current: PIP-SuriOS v3.3, Sprint 035
date: 2026-09-23
---

## 1. Qué se está comparando

La primera distribución para testers se documentó como **Alpha**, no como Beta.
Se generó durante el Sprint 029 y contenía cuatro APK:

- `PIP-SuriOS_MAIN_v2.9.apk`
- `PIP-SuriOS_FENRIR_v1.0.apk`
- `PIP-SuriOS_CHECHU_v1.0.apk`
- `PIP-SuriOS_ALTAMIRA_v1.0.apk`

Para que la comparación sea equivalente, este documento usa como referencia la
APK principal `MAIN v2.9` y la compara con la APK principal actual
`fullDebug v3.3`. Las variantes de tester tenían mapas y configuración de
perfil propios, por lo que no se mezclan con la comparación principal.

## 2. Datos comprobados directamente en las APK

| Dato | Primera Alpha | Versión actual |
|---|---|---|
| Archivo | `output/SPRINT_029_APK/PIP-SuriOS_MAIN_v2.9.apk` | `app/build/outputs/apk/full/debug/app-full-debug.apk` |
| Versión visible | `2.9` | `3.3` |
| Código interno | `9` | `13` |
| Paquete Android | `com.suri.pipsurios` | `com.suri.pipsurios` |
| Android mínimo | API 34 | API 34 |
| Android objetivo | API 37 | API 37 |
| Tamaño aproximado | 132,48 MiB | 184,41 MiB |
| Diferencia | — | +51,92 MiB / +39,2 % |
| SHA-256 | `B8A2F214CCF5420586316D1E31AC4DE2FE4F0D3EE9B3738F0679C74C358240A3` | `303C4C8492C1AB6B43177D46343604570416AA68CF686CAE8AD3F95A3B9129A9` |

El aumento de tamaño se debe principalmente a la incorporación de más mapas,
capas vectoriales, el material de referencia y recursos de sonido/branding.

## 3. Diferencias funcionales principales

| Área | Primera Alpha v2.9 | Versión actual v3.3 |
|---|---|---|
| Configuración | Formularios y listas personalizables de equipamiento | Se conservan y se han ampliado las pantallas de uso y ayuda |
| P.R.S. | SENTRY, TRACKER, DEVICES y USER GUIDE | Mismos módulos, con búsqueda de dispositivos más útil, filas compactas, ordenación por relevancia y mejoras de TRACKER |
| MAP | Cartografía offline inicial | Cartografía offline ampliada, overlays de organización, GRID, POI, RESPawns, RAD ZONE, Geiger, EMPTY MAP y pinch-to-zoom |
| Navegación MAP | No existía la navegación waypoint del Sprint 035 | Waypoint manual y navegación hacia POI mediante distancia, bearing y dirección relativa |
| Organización de partidas | No había capas de organización independientes | Capas `GRID`, `FIELD_BOUNDARY`, `RESPAWNS`, `BASES`, `POI` e `INTERNAL_PATHS` mediante overlays |
| Cambio de mapa | Flujo inicial | Carga protegida frente a recursos antiguos y cambio AIRSOFT TOTAL ↔ OFFICE verificado en A56 |
| Interfaz de carga | Animación inicial | El cursor de escritura acompaña al texto mientras aparece |
| Lectura en campo | Estética inicial PIP | Textos más grandes, negrita, color naranja oficial en textos de interfaz y rojo oficial para POI |
| Flecha NAV | No disponible | Flecha grande con giro continuo de 360 grados, sin limitarse a 8 posiciones |

## 4. Mapas incluidos

### Primera Alpha MAIN v2.9

La APK contiene tres mapas MBTiles:

- `HOME`
- `NAVY7`
- `OFFICE`

Las variantes FENRIR, CHECHU y ALTAMIRA utilizaban su configuración de perfil
y sus mapas de distribución correspondientes.

### Versión actual MAIN v3.3

La APK contiene seis mapas MBTiles:

- `AIRSOFT TOTAL`
- `BRICKTOWN`
- `HOME`
- `MAJADAHONDA`
- `NAVY7`
- `OFFICE`

También incluye:

- overlay de organización para AIRSOFT TOTAL;
- overlay de organización para OFFICE;
- overlay de organización para MAJADAHONDA;
- PDF original `MAPA JUGADORES.pdf` como referencia de QGIS;
- perímetro revisado de AIRSOFT TOTAL;
- cobertura ampliada de AIRSOFT TOTAL para permitir visualizar el campo con
  zoom negativo;
- estética clara compartida con HOME, NAVY7 y OFFICE.

## 5. Evolución de MAP

La evolución más importante entre ambas versiones está en MAP:

1. La Alpha partía de una cartografía offline básica con mapas preparados.
2. La versión actual separa los datos base del mapa, los overlays de
   organización, GRID, POI, USER MARKERS, RAD ZONES y navegación.
3. Los POI son objetos geográficos con identificador, tipo, nombre y
   coordenadas, no simples dibujos.
4. Un POI de organización y un waypoint manual usan el mismo destino interno
   de navegación.
5. La navegación calcula localmente distancia, bearing, orientación relativa y
   celda GRID, sin routing, A*, Dijkstra ni navegación giro a giro.

## 6. Evolución de P.R.S. y TRACKER

La primera Alpha ya incluía el flujo de P.R.S. destinado a pruebas con
FENRIR, CHECHU y ALTAMIRA. En la versión actual:

- se conserva la separación entre SENTRY, TRACKER, DEVICES y USER GUIDE;
- la identificación de dispositivos muestra principalmente nombre, fuente,
  RSSI, identificador corto y dirección;
- los dispositivos guardados aparecen integrados en la búsqueda;
- se redujo el texto repetido para dar más espacio a la lista;
- se conserva la lectura automática de TRACKER;
- se mantienen el zoom y la niebla de probabilidad donde corresponden.

## 7. Compatibilidad y estado de validación

Ambas APK usan el mismo paquete Android y los mismos niveles mínimo y objetivo
de Android. La versión actual mantiene la compatibilidad de instalación de la
aplicación principal y eleva la firma interna de `2.9`/`9` a `3.3`/`13`.

La Alpha v2.9 quedó documentada como instalada y probada en el A56 durante el
Sprint 029. La versión actual v3.3 ha superado:

- pruebas JVM;
- compilación `fullDebug`;
- lint;
- 7 pruebas instrumentadas en el Samsung A56;
- comprobación del cambio entre mapas;
- smoke test de overlays, POI y waypoint NAV.

La prueba física exterior de GPS y brújula de la versión actual queda como
comprobación operativa de campo. No se considera una incidencia técnica abierta
del Sprint 035.

## 8. Conclusión sencilla

La Alpha v2.9 era una primera distribución funcional para validar la aplicación
con testers y perfiles concretos. La v3.3 conserva esa base, pero añade cuatro
versiones de producto posteriores de trabajo, cartografía más amplia, mapas
nuevos, capas de organización, navegación offline, mejoras de TRACKER y una
interfaz más legible en campo.

En términos prácticos, la versión actual es una evolución compatible de la
misma aplicación, no una aplicación distinta: mantiene el paquete
`com.suri.pipsurios`, pero tiene cuatro incrementos de versión interna y un
alcance funcional considerablemente mayor.

## Referencias

- [Sprint 029 — Alpha testers](SPRINTS/SPRINT_029_v1.0.md)
- [Sprint 035 — MAP y WAYPOINT NAVIGATION](SPRINTS/SPRINT_035_v1.0.md)
- [Auditoría Sprint 035](AUDIT_SPRINT_035.md)
- [Historial de Sprints](SPRINTS/SPRINT_HISTORY%20v1.3.md)
