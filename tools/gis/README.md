# Integración Orca - QGIS para PIP-SuriOS

Esta carpeta contiene la vinculación reproducible de Orca con la instalación QGIS
existente. La primera fase no rehace NAVY7 ni descarga datos: solo comprueba el
entorno y la comunicación.

## Entorno auditado

- QGIS LTR: 3.44.13-Solothurn
- Instalación: C:\Program Files\QGIS 3.44.13
- PyQGIS: Python 3.12.13, mediante bin\python-qgis-ltr.bat
- GDAL: 3.13.2
- Processing: disponible mediante bin\qgis_process-qgis-ltr.bat
- QuickOSM: 2.5.3, instalado en el perfil QGIS default y activo como proveedor
  quickosm de Processing

Los wrappers de OSGeo4W son importantes porque preparan PATH,
QGIS_PREFIX_PATH, PYTHONPATH y las DLL correctas. No se usa el Python de
Windows ni se cambia la configuración de QGIS.

## Cómo lo ejecuta Orca

Desde una terminal de Orca en el repositorio:

~~~powershell
cd D:\WristOS
powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\gis\run_qgis_smoke.ps1
~~~

qgis_env.ps1 detecta automáticamente la instalación QGIS LTR. Si en el futuro
hay varias, se puede indicar una concreta con la variable de proceso
SURIOS_QGIS_ROOT.

La comunicación se divide así:

1. PyQGIS crea y modifica proyectos .qgz, capas, GeoPackages y estilos.
2. qgis_process ejecuta algoritmos Processing/GDAL reproducibles.
3. QuickOSM queda disponible para consultas OSM, pero los resultados vacíos se
   informan y nunca se convierten en geometrías inventadas.
4. La interfaz QGIS se reserva para inspección y edición manual cuando falten
   datos, por ejemplo caminos internos ausentes de OSM.

## Prueba mínima

run_qgis_smoke.ps1 crea output/gis/orca_qgis_smoke_test.qgz y su GeoPackage
sidecar, añade una capa de un punto, modifica una propiedad del proyecto,
vuelve a abrirlo y verifica que ambas operaciones se conservaron. También
consulta los proveedores gdal, qgis y quickosm.

El archivo de prueba se crea solo si no existe; no sobrescribe resultados.

## Piloto encontrado

El proyecto utilizado para las pruebas está fuera del repositorio, en:

C:\Users\diego\Desktop\GQUIS\MAP SuriOS.qgz

Su contenido actual es:

- CRS de proyecto: EPSG:3857.
- Dos MDT MDT50CM de la 3.ª cobertura, EPSG:3042, resolución de 0,5 m.
- Navy7.gpkg con contours_2m (28.333 entidades), building (4) y
  highway (4).
- Una capa XYZ de OpenStreetMap, actualmente online.
- navy_7_terrain.mbtiles, PNG, niveles 16-19, usado como overlay offline.

La cartografía existente usa colores PIP-SuriOS, pero la capa de caminos se llama
highway y contiene principalmente vías exteriores. El futuro pipeline deberá
separar paths y filtrar track, path, footway, service y equivalentes,
dejando una capa editable vacía o parcial cuando OSM no aporte caminos internos.

## Arquitectura acordada para la siguiente fase

~~~text
área + margen
    -> fuentes CNIG/OSM
    -> PyQGIS + Processing/GDAL
    -> GeoPackage maestro + proyecto QGIS
    -> exportación MBTiles PNG
    -> asset offline de PIP-SuriOS
~~~

El formato Android no se cambia en esta fase: el código actual lee MBTiles PNG
mediante SQLite (assets/maps/...). Por tanto, GeoPackage será el formato de
trabajo editable y MBTiles seguirá siendo la salida Android hasta que se apruebe
otra implementación.

Hillshade queda fuera por defecto. El margen del área, el intervalo de curvas
(referencia actual: 2 m), la resolución de salida y el estilo serán parámetros
del futuro generador, no valores codificados para NAVY7.

## Regeneración de NAVY7 en Sprint 020

La regeneración de NAVY7 se realizó con QGIS LTR 3.44.13 y el proyecto piloto
`C:\Users\diego\Desktop\GQUIS\MAP SuriOS.qgz`, usando las capas activas
`highway`, `contours_2m` y `building`. El MDT y OpenStreetMap permanecieron
desactivados para conservar una salida completamente offline.

El centro solicitado es `40.35297419412242, -3.4237021485063486`. Se conservó
la huella aproximada del mapa anterior, con la extensión WGS84
`-3.4266621485063486,40.35152419412242,-3.4207421485063486,40.35442419412242`.
La salida final es `app/src/main/assets/maps/navy_7_terrain.mbtiles`, PNG,
overlay, zoom 16–19, compatible con `MbTilesRepository`.

## Modelo estándar HOME para NAVY7 y futuros mapas

El generador `build_navy7_home_style.py` crea un proyecto QGIS nuevo y renderiza
el MBTiles desde el GeoPackage local. No lee ni transforma el MBTiles anterior.
Todas las rutas de entrada y salida son argumentos obligatorios; así el pipeline
no depende de una ruta de usuario concreta.
El modelo deja fijos los parámetros de HOME; para repetirlo solo se suministra
el centro geográfico:

```powershell
$env:QGIS_PREFIX_PATH = "C:\Program Files\QGIS 3.44.13\apps\qgis-ltr"
& "C:\Program Files\QGIS 3.44.13\bin\python-qgis-ltr.bat" `
  tools/gis/build_navy7_home_style.py `
  --gpkg "C:\Users\diego\Desktop\GQUIS\Navy7.gpkg" `
  --project-output "C:\Users\diego\Desktop\GQUIS\Navy7_HOME_STYLE.qgz" `
  --output "C:\Users\diego\Desktop\GQUIS\navy_7_terrain_HOME_STYLE.mbtiles" `
  --center-lat 40.352971232717216 `
  --center-lon -3.423711863510395 `
  --force
```

Los valores estándar fijados son:

- dimensiones geográficas HOME: 0,05867° x 0,023° (aproximadamente 5 km x
  2,5 km), centradas en las coordenadas suministradas;
- tesela PNG RGBA opaca de 256 x 256, fondo `#050805`, overlay SQLite y zoom
  16–19;
- edificios `#606060` con borde `#050805` de 0,10 mm;
- carreteras `#2f7ebe`, ancho 0,45 mm, extremos redondeados;
- curvas menores `#4cb359`, ancho 0,55 mm, y curvas índice `#5bd66b`, ancho
  0,90 mm;
- jerarquía de capas: edificios, carreteras, curvas menores y curvas índice.

El proyecto y la salida se escriben por defecto en GQUIS como
`Navy7_HOME_STYLE.qgz` y `navy_7_terrain_HOME_STYLE.mbtiles`. La salida Sprint
023 utiliza `highway`, `contours_2m` y `building`; MDT y OSM online permanecen
desactivados. La matriz resultante para este centro es 84, 276, 1012 y 3870
teselas por zoom, 5242 en total, con bounds WGS84
`-3.453046863510,40.341471232717,-3.394376863510,40.364471232717`.

## Política de fuentes y reproducibilidad

El GeoPackage y los proyectos QGIS editables se mantienen fuera del repositorio:
son fuentes de trabajo locales que pueden contener datos cartográficos pesados o
de terceros. El repositorio conserva el generador, sus parámetros, la versión
oficial de QGIS LTR (`3.44.13-Solothurn`) y el MBTiles final que consume Android.

La salida Android se valida por SHA-256, metadata MBTiles (`format`, zoom y
bounds) y tres teselas representativas antes de abrirse. Si el hash no coincide,
la copia local se recrea automáticamente mediante un temporal y un reemplazo
seguro. No se introduce Git LFS en esta fase: el GeoPackage no se versiona y el
asset Android ya forma parte del artefacto de release.
