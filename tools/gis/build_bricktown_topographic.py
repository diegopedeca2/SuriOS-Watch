"""Build the offline BRICKTOWN topographic map.

PNOA 2023 is used only as the source for the extra vector geometries stored in
``bricktown_pnoa_visible.geojson``. No raster or WMS layer is loaded, so the
result is a black/green vector-styled map with the existing contour layer.
"""

from __future__ import annotations

import argparse
import math
import os
import sqlite3
from pathlib import Path


TILE_SIZE = 256
WEB_MERCATOR_RADIUS = 6378137.0
WEB_MERCATOR_HALF_WORLD = math.pi * WEB_MERCATOR_RADIUS


def lon_to_x(lon: float, zoom: int) -> float:
    return (lon + 180.0) / 360.0 * (TILE_SIZE * 2**zoom)


def lat_to_y(lat: float, zoom: int) -> float:
    latitude = max(-85.05112878, min(85.05112878, lat))
    sine = math.sin(math.radians(latitude))
    return (0.5 - math.log((1.0 + sine) / (1.0 - sine)) / (4.0 * math.pi)) * (
        TILE_SIZE * 2**zoom
    )


def mercator_x(pixel: float, zoom: int) -> float:
    world = TILE_SIZE * 2**zoom
    return pixel / world * 2.0 * math.pi * WEB_MERCATOR_RADIUS - WEB_MERCATOR_HALF_WORLD


def mercator_y(pixel: float, zoom: int) -> float:
    world = TILE_SIZE * 2**zoom
    return WEB_MERCATOR_HALF_WORLD - pixel / world * 2.0 * WEB_MERCATOR_HALF_WORLD


def metres_per_degree_latitude(latitude: float) -> float:
    radians = math.radians(latitude)
    return (
        111132.92
        - 559.82 * math.cos(2.0 * radians)
        + 1.175 * math.cos(4.0 * radians)
        - 0.0023 * math.cos(6.0 * radians)
    )


def metres_per_degree_longitude(latitude: float) -> float:
    radians = math.radians(latitude)
    return (
        111412.84 * math.cos(radians)
        - 93.5 * math.cos(3.0 * radians)
        + 0.118 * math.cos(5.0 * radians)
    )


def metadata_bounds(center_lat: float, center_lon: float, width: float, height: float) -> str:
    width_degrees = width / metres_per_degree_longitude(center_lat)
    height_degrees = height / metres_per_degree_latitude(center_lat)
    return ",".join(
        (
            f"{center_lon - width_degrees / 2.0:.12f}",
            f"{center_lat - height_degrees / 2.0:.12f}",
            f"{center_lon + width_degrees / 2.0:.12f}",
            f"{center_lat + height_degrees / 2.0:.12f}",
        )
    )


def dimensions_in_web_mercator_metres(latitude: float, width: float, height: float) -> tuple[float, float]:
    scale = 1.0 / math.cos(math.radians(latitude))
    return width * scale, height * scale


def tile_ranges(center_lat: float, center_lon: float, min_zoom: int, max_zoom: int, width: float, height: float):
    extent_width, extent_height = dimensions_in_web_mercator_metres(center_lat, width, height)
    ranges = {}
    for zoom in range(min_zoom, max_zoom + 1):
        metres_per_pixel = 2.0 * math.pi * WEB_MERCATOR_RADIUS / (TILE_SIZE * 2**zoom)
        center_x = lon_to_x(center_lon, zoom)
        center_y = lat_to_y(center_lat, zoom)
        left = center_x - extent_width / metres_per_pixel / 2.0
        right = center_x + extent_width / metres_per_pixel / 2.0
        top = center_y - extent_height / metres_per_pixel / 2.0
        bottom = center_y + extent_height / metres_per_pixel / 2.0
        ranges[zoom] = (
            math.floor(left / TILE_SIZE),
            math.ceil(right / TILE_SIZE) - 1,
            math.floor(top / TILE_SIZE),
            math.ceil(bottom / TILE_SIZE) - 1,
        )
    return ranges


def load_layer(project, path: str | Path, name: str, provider: str, subset: str | None = None):
    from qgis.core import QgsVectorLayer

    layer = QgsVectorLayer(str(path), name, provider)
    if not layer.isValid():
        raise RuntimeError(f"No se pudo cargar {path}")
    if subset:
        layer.setSubsetString(subset)
    project.addMapLayer(layer)
    return layer


def build_layers(project, gpkg: Path, visible_geojson: Path):
    from qgis.core import QgsFillSymbol, QgsLineSymbol, QgsRuleBasedRenderer, QgsSingleSymbolRenderer

    source_buildings = load_layer(project, f"{gpkg}|layername=building", "OSM · edificios existentes", "ogr", None)
    source_roads = load_layer(project, f"{gpkg}|layername=highway", "OSM · caminos existentes", "ogr", None)
    contours = load_layer(project, f"{gpkg}|layername=contours_2m", "Curvas de nivel · 2 m", "ogr", None)

    # Separate filtered views keep the GeoJSON source reproducible in the QGIS
    # project while allowing each category to have its own visual style.
    pnoa_buildings = load_layer(
        project,
        visible_geojson,
        "PNOA 2023 · estructuras visibles digitalizadas",
        "ogr",
        '"kind" = \'building\'',
    )
    pnoa_surfaces = load_layer(
        project,
        visible_geojson,
        "PNOA 2023 · explanadas y superficies visibles",
        "ogr",
        '"kind" = \'surface\'',
    )
    pnoa_tracks = load_layer(
        project,
        visible_geojson,
        "PNOA 2023 · accesos y pistas visibles",
        "ogr",
        '"kind" = \'track\'',
    )
    pnoa_vegetation = load_layer(
        project,
        visible_geojson,
        "PNOA 2023 · matorral visible",
        "ogr",
        '"kind" = \'vegetation\'',
    )

    pnoa_vegetation.setRenderer(QgsSingleSymbolRenderer(QgsFillSymbol.createSimple({
        "color": "#0B3218",
        "outline_color": "#1A6B35",
        "outline_width": "0.18",
    })))
    pnoa_surfaces.setRenderer(QgsSingleSymbolRenderer(QgsFillSymbol.createSimple({
        "color": "#102719",
        "outline_color": "#2A8F4C",
        "outline_width": "0.20",
    })))

    building_symbol = QgsFillSymbol.createSimple({
        "color": "#06130B",
        "outline_color": "#63FF8D",
        "outline_width": "0.25",
    })
    source_buildings.setRenderer(QgsSingleSymbolRenderer(building_symbol.clone()))
    pnoa_buildings.setRenderer(QgsSingleSymbolRenderer(building_symbol.clone()))

    source_roads.setRenderer(QgsSingleSymbolRenderer(QgsLineSymbol.createSimple({
        "color": "#4CFF7F",
        "width": "0.55",
        "capstyle": "round",
    })))
    pnoa_tracks.setRenderer(QgsSingleSymbolRenderer(QgsLineSymbol.createSimple({
        "color": "#2DBB58",
        "width": "0.55",
        "capstyle": "round",
        "line_style": "dash",
    })))

    minor = QgsLineSymbol.createSimple({"color": "#1C6A38", "width": "0.42", "capstyle": "round"})
    major = QgsLineSymbol.createSimple({"color": "#5BE47B", "width": "0.78", "capstyle": "round"})
    root = QgsRuleBasedRenderer.Rule(None)
    root.appendChild(QgsRuleBasedRenderer.Rule(major, filterExp='round("ELEV") % 10 = 0', label="Indice 10 m"))
    root.appendChild(QgsRuleBasedRenderer.Rule(minor, filterExp='round("ELEV") % 10 <> 0', label="Curva 2 m"))
    contours.setRenderer(QgsRuleBasedRenderer(root))

    # QGIS renders the first item at the top of the stack.
    return [pnoa_vegetation, pnoa_surfaces, source_buildings, pnoa_buildings, pnoa_tracks, source_roads, contours]


def render_strip(project, layers, x_min: int, x_max: int, y: int, zoom: int) -> list[bytes]:
    from qgis.core import QgsCoordinateReferenceSystem, QgsMapRendererSequentialJob, QgsMapSettings, QgsRectangle
    from qgis.PyQt.QtCore import QBuffer, QIODevice, QSize
    from qgis.PyQt.QtGui import QColor, QImage

    columns = x_max - x_min + 1
    settings = QgsMapSettings()
    settings.setLayers(list(reversed(layers)))
    settings.setDestinationCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
    settings.setTransformContext(project.transformContext())
    settings.setExtent(QgsRectangle(
        mercator_x(x_min * TILE_SIZE, zoom),
        mercator_y((y + 1) * TILE_SIZE, zoom),
        mercator_x((x_max + 1) * TILE_SIZE, zoom),
        mercator_y(y * TILE_SIZE, zoom),
    ))
    settings.setOutputSize(QSize(columns * TILE_SIZE, TILE_SIZE))
    settings.setOutputImageFormat(QImage.Format_ARGB32_Premultiplied)
    settings.setBackgroundColor(QColor(0, 0, 0, 255))
    job = QgsMapRendererSequentialJob(settings)
    job.start(); job.waitForFinished()
    if job.errors():
        raise RuntimeError(f"Falló el render z{zoom}/{y}: {job.errors()}")
    image = job.renderedImage()
    pngs = []
    for offset in range(columns):
        tile = image.copy(offset * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE)
        buffer = QBuffer()
        buffer.open(QIODevice.WriteOnly)
        if not tile.save(buffer, "PNG"):
            raise RuntimeError(f"No se pudo codificar la tesela z{zoom}")
        pngs.append(bytes(buffer.data()))
        buffer.close()
    return pngs


def render_preview(project, layers, center_lat: float, center_lon: float, width: float, height: float, output: Path, pixel_width: int, pixel_height: int) -> None:
    from qgis.core import QgsCoordinateReferenceSystem, QgsMapRendererSequentialJob, QgsMapSettings, QgsRectangle
    from qgis.PyQt.QtCore import QSize
    from qgis.PyQt.QtGui import QColor, QImage

    bounds = metadata_bounds(center_lat, center_lon, width, height).split(",")
    west, south, east, north = [float(value) for value in bounds]
    radius = WEB_MERCATOR_RADIUS
    extent = QgsRectangle(
        radius * math.radians(west),
        radius * math.log(math.tan(math.pi / 4.0 + math.radians(south) / 2.0)),
        radius * math.radians(east),
        radius * math.log(math.tan(math.pi / 4.0 + math.radians(north) / 2.0)),
    )
    settings = QgsMapSettings()
    settings.setLayers(list(reversed(layers)))
    settings.setDestinationCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
    settings.setTransformContext(project.transformContext())
    settings.setExtent(extent)
    settings.setOutputSize(QSize(pixel_width, pixel_height))
    settings.setOutputImageFormat(QImage.Format_ARGB32_Premultiplied)
    settings.setBackgroundColor(QColor(0, 0, 0, 255))
    job = QgsMapRendererSequentialJob(settings)
    job.start(); job.waitForFinished()
    if job.errors():
        raise RuntimeError(f"Falló la previsualización: {job.errors()}")
    output.parent.mkdir(parents=True, exist_ok=True)
    if not job.renderedImage().save(str(output), "PNG"):
        raise RuntimeError(f"No se pudo guardar la previsualización {output}")


def build(args: argparse.Namespace) -> None:
    from qgis.core import QgsApplication, QgsCoordinateReferenceSystem, QgsProject
    from qgis.PyQt.QtGui import QColor

    prefix = os.environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise RuntimeError("QGIS_PREFIX_PATH no está definido; usa python-qgis-ltr.bat")
    if args.output.exists() and not args.force:
        raise RuntimeError(f"No se sobrescribe sin --force: {args.output}")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.project_output.parent.mkdir(parents=True, exist_ok=True)
    if args.preview_output:
        args.preview_output.parent.mkdir(parents=True, exist_ok=True)

    qgis = QgsApplication([], False)
    qgis.setPrefixPath(prefix, True)
    qgis.initQgis()
    try:
        if args.output.exists():
            args.output.unlink()
        project = QgsProject()
        project.setCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
        project.setTitle("SuriOS BRICKTOWN topographic PNOA 2023")
        project.setBackgroundColor(QColor(0, 0, 0, 255))
        layers = build_layers(project, args.gpkg, args.visible_geojson)
        root = project.layerTreeRoot()
        root.setHasCustomLayerOrder(True)
        root.setCustomLayerOrder(list(reversed(layers)))
        for layer in layers:
            root.findLayer(layer.id()).setItemVisibilityChecked(True)
        if not project.write(str(args.project_output)):
            raise RuntimeError(f"No se pudo guardar el proyecto {args.project_output}")

        if args.preview_output:
            render_preview(project, layers, args.center_lat, args.center_lon, args.width_metres, args.height_metres, args.preview_output, 1600, 1600)
        if args.central_preview_output:
            render_preview(project, layers, args.center_lat, args.center_lon, 600.0, 600.0, args.central_preview_output, 1600, 1600)

        ranges = tile_ranges(args.center_lat, args.center_lon, args.min_zoom, args.max_zoom, args.width_metres, args.height_metres)
        connection = sqlite3.connect(str(args.output))
        connection.execute("PRAGMA journal_mode=DELETE")
        connection.execute("PRAGMA synchronous=FULL")
        connection.executescript("""
            CREATE TABLE metadata (name TEXT PRIMARY KEY, value TEXT);
            CREATE TABLE tiles (zoom_level INTEGER, tile_column INTEGER, tile_row INTEGER, tile_data BLOB);
            CREATE UNIQUE INDEX tiles_idx ON tiles (zoom_level, tile_column, tile_row);
        """)
        metadata = {
            "format": "png",
            "map_id": args.map_id,
            "name": args.metadata_name,
            "description": "BRICKTOWN topographic vector map from PNOA 2023 references",
            "version": "1.1",
            "style": "SURIOS_PIP_BRICKTOWN_TOPOGRAPHIC_V2",
            "type": "overlay",
            "minzoom": str(args.min_zoom),
            "maxzoom": str(args.max_zoom),
            "bounds": metadata_bounds(args.center_lat, args.center_lon, args.width_metres, args.height_metres),
            "attribution": "PNOA 2023 · IGN/CNIG; geometrías visibles digitalizadas sobre ortofotografía oficial",
            "source": "https://www.ign.es/wms-inspire/pnoa-ma",
            "source_capture": "Madrid, vuelo PNOA 2023 · 28/07/2023–04/08/2023",
            "source_resolution": "0.25 m/pixel",
            "source_license": "CC BY 4.0 · IGN/CNIG",
            "notes": "Sin imagen aérea en las teselas; PNOA utilizada únicamente como referencia de digitalización.",
        }
        connection.executemany("INSERT INTO metadata(name,value) VALUES (?,?)", metadata.items())
        total = 0
        for zoom in range(args.min_zoom, args.max_zoom + 1):
            x_min, x_max, y_min, y_max = ranges[zoom]
            for y in range(y_min, y_max + 1):
                pngs = render_strip(project, layers, x_min, x_max, y, zoom)
                for offset, png in enumerate(pngs):
                    x = x_min + offset
                    tms_y = (1 << zoom) - 1 - y
                    connection.execute("INSERT INTO tiles VALUES (?,?,?,?)", (zoom, x, tms_y, sqlite3.Binary(png)))
                    total += 1
            connection.commit()
            print(f"ZOOM={zoom} TILES={(x_max - x_min + 1) * (y_max - y_min + 1)}", flush=True)
        connection.commit()
        connection.close()
        print(f"OUTPUT={args.output}", flush=True)
        print(f"BOUNDS={metadata['bounds']}", flush=True)
        print(f"TILES={total}", flush=True)
    finally:
        os._exit(0)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--gpkg", type=Path, required=True)
    parser.add_argument("--visible-geojson", type=Path, required=True)
    parser.add_argument("--project-output", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--preview-output", type=Path)
    parser.add_argument("--central-preview-output", type=Path)
    parser.add_argument("--center-lat", type=float, required=True)
    parser.add_argument("--center-lon", type=float, required=True)
    parser.add_argument("--width-metres", type=float, default=2500.0)
    parser.add_argument("--height-metres", type=float, default=2500.0)
    parser.add_argument("--map-id", default="bricktown")
    parser.add_argument("--metadata-name", default="bricktown_terrain")
    parser.add_argument("--min-zoom", type=int, default=16)
    parser.add_argument("--max-zoom", type=int, default=19)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()
    if args.min_zoom > args.max_zoom:
        parser.error("min zoom no puede superar max zoom")
    build(args)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
