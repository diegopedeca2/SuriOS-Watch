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
