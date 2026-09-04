# Integración Orca - QGIS para PIP-SuriOS

Esta carpeta contiene la vinculación reproducible de Orca con la instalación QGIS
existente y los generadores de mapas offline de SuriOS.

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

## Regeneración de NAVY7 en Sprint 030

La regeneración de NAVY7 se realizó con QGIS LTR 3.44.13 y `Navy7.gpkg`, usando
las capas `highway`, `contours_2m` y `building`. El resultado es completamente
offline y conserva el centro `40.352971232717216, -3.423711863510395`.

Desde Sprint 030, NAVY7 usa una huella cuadrada de 2 km x 2 km, con curvas de
nivel visibles, y 1.699 teselas PNG en zoom 16–19. La salida final es
`app/src/main/assets/maps/navy_7_terrain.mbtiles`, compatible con
`MbTilesRepository`. Sus bounds WGS84 son
`-3.435483145327,40.343965582217,-3.411940581694,40.361976883217`.

## Modelo estándar HOME para NAVY7 y futuros mapas

El generador `build_navy7_home_style.py` crea un proyecto QGIS nuevo y renderiza
el MBTiles desde el GeoPackage local. Aunque conserva ese nombre por
compatibilidad, sus parametros permiten generar NAVY7, OFFICE u otros campos.
El punto de entrada recomendado para nuevos mapas es
`build_terrain_home_style.py`.
No lee ni transforma el MBTiles anterior.
Todas las rutas de entrada y salida son argumentos obligatorios; así el pipeline
no depende de una ruta de usuario concreta.
El modelo actual usa una huella estándar de 2 km x 2 km, teselas PNG RGBA opacas
de 256 x 256, overlay SQLite y zoom 16–19. El centro y el GeoPackage se pasan
como argumentos:

```powershell
$env:QGIS_PREFIX_PATH = "C:\Program Files\QGIS 3.44.13\apps\qgis-ltr"
& "C:\Program Files\QGIS 3.44.13\bin\python-qgis-ltr.bat" `
  tools/gis/build_navy7_home_style.py `
  --gpkg "C:\Users\diego\Desktop\GQUIS\Navy7.gpkg" `
  --project-output "C:\Users\diego\Desktop\GQUIS\Navy7_HOME_STYLE.qgz" `
  --output "C:\Users\diego\Desktop\GQUIS\navy_7_terrain_HOME_STYLE.mbtiles" `
  --center-lat 40.352971232717216 `
  --center-lon -3.423711863510395 `
  --width-metres 2000 `
  --height-metres 2000 `
  --contour-layer contours_2m `
  --force
```

Los valores estándar fijados son:

- huella cuadrada de 2 km x 2 km, centrada en las coordenadas suministradas;
- tesela PNG RGBA opaca de 256 x 256, fondo `#050805`, overlay SQLite y zoom
  16–19;
- edificios `#606060` con borde `#050805` de 0,10 mm;
- carreteras `#2f7ebe`, ancho 0,45 mm, extremos redondeados;
- curvas de nivel `#4cb359`, ancho 0,70 mm;
- jerarquía de capas: edificios, carreteras y curvas de nivel.

El proyecto y la salida se escriben en GQUIS como
`Navy7_2KM_STYLE.qgz` y `navy_7_terrain_2km.mbtiles`. MDT y OSM online
permanecen desactivados.

HOME ya disponía de edificios, carreteras y curvas de nivel en su MBTiles
validado, pero no se encontró su GeoPackage original. Para no perder esas
capas, Sprint 030 usa `crop_mbtiles_centered.py` y conserva las teselas
existentes dentro de la nueva huella de 2 km x 2 km. La salida final contiene
1.699 teselas y sus bounds WGS84 son
`-3.882292827336,40.438894497808,-3.858717172664,40.456905502192`.

## Política de fuentes y reproducibilidad

Cada mapa solicitado con nuevas coordenadas se genera siempre desde cero:
primero se obtiene o prepara una fuente geográfica que cubra esas coordenadas,
después se crea un GeoPackage nuevo y finalmente se renderiza un MBTiles nuevo.
No se reutiliza, recorta ni se centra un mapa existente para una ubicación
distinta. Si no hay datos suficientes para una capa, esa ausencia se declara y
no se inventa contenido cartográfico.

El GeoPackage y los proyectos QGIS editables se mantienen fuera del repositorio:
son fuentes de trabajo locales que pueden contener datos cartográficos pesados o
de terceros. El repositorio conserva el generador, sus parámetros, la versión
oficial de QGIS LTR (`3.44.13-Solothurn`) y el MBTiles final que consume Android.

La salida Android se valida por SHA-256, metadata MBTiles (`format`, zoom y
bounds) y tres teselas representativas antes de abrirse. Si el hash no coincide,
la copia local se recrea automáticamente mediante un temporal y un reemplazo
seguro. Los GeoPackage y proyectos QGIS de trabajo no se versionan. Los mapas e
iconos finales de las distribuciones tester se conservan en
`distribution-assets/` y `distribution-res/`, con Git LFS, para que un clon
limpio pueda compilar sin regenerar todo el GIS. La generación GIS se reserva
para cuando cambie un mapa.

Esta decisión resuelve la reproducibilidad de compilación, pero no acelera la
carga de mapas dentro de la aplicación. La mejora de carga en el dispositivo
queda como trabajo futuro independiente.

## Curvas de nivel en la automatizaciÃ³n

Las curvas de nivel forman parte del proceso automatizado. Cuando existe un MDT
para la zona, se generan con `gdal_contour` en intervalos de 2 m, se incorporan
al GeoPackage con `prepare_overpass_gpkg.py --contours-source` y el renderizador
se ejecuta con `--contour-layer contours_2m`. No se debe usar `--no-contours` en
un mapa solicitado con coordenadas si se dispone de datos de elevaciÃ³n.

## OFFICE en Sprint 027

OFFICE se genera con el mismo contrato visual que NAVY7, centrado en
`40.43717182620207, -3.620425636696507`. Su fuente local es un GeoPackage
preparado desde OpenStreetMap mediante Overpass, con edificios y carreteras
reales de la zona. No se generan curvas de nivel cuando la fuente urbana no las
aporta. Desde Sprint 029 la huella se reduce a 2 km x 2 km para acelerar la
carga en el dispositivo.

El flujo reproducible es:

1. Consultar Overpass para el rectangulo de 2 km x 2 km alrededor del centro.
2. Convertir el JSON con `prepare_overpass_gpkg.py` a capas `building` y
   `highway` en un GeoPackage local.
3. Descargar desde el MDT05 del IGN/CNIG el recorte de elevación que cubre el
   centro y generar la capa `contours_2m` con `gdal_contour`.
4. Normalizar la referencia espacial de las curvas con
   `normalize_contour_crs.py` y añadirlas al GeoPackage junto a `building` y
   `highway`.
5. Ejecutar `build_terrain_home_style.py` con `--map-id office`, `--map-name
   OFFICE`, `--width-metres 2000`, `--height-metres 2000` y
   `--contour-layer contours_2m`.
6. Copiar el MBTiles resultante a `app/src/main/assets/maps/office_terrain.mbtiles`
   y registrar su SHA-256 y sus bounds en `OfflineMapCatalog`.

La salida generada contiene 1.669 teselas PNG en zoom 16-19 y 310 curvas de
nivel derivadas del MDT05. Sus bounds son
`-3.632211590216,40.428166307246,-3.608639683177,40.446177345158`.
