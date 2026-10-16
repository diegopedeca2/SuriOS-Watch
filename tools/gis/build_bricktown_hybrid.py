"""Build the offline BRICKTOWN hybrid map.

The base is the official IGN/CNIG PNOA WMS (the service currently returns the
2023-07 Madrid orthophoto at this location). Existing local OSM geometry is
used only for restrained visual emphasis: mapped buildings and roads. No game
perimeter, corridors or interior structures are created by this script.

The output remains a PNG MBTiles database so the current Android repository
can consume it without changing the general MAP code.
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
MAX_WMS_REQUEST_PIXELS = 1536

WGS84_METRES_PER_DEGREE_LAT = (111132.92, -559.82, 1.175, -0.0023)


def metres_per_degree_latitude(latitude: float) -> float:
    radians = math.radians(latitude)
    return (
        WGS84_METRES_PER_DEGREE_LAT[0]
        + WGS84_METRES_PER_DEGREE_LAT[1] * math.cos(2.0 * radians)
        + WGS84_METRES_PER_DEGREE_LAT[2] * math.cos(4.0 * radians)
        + WGS84_METRES_PER_DEGREE_LAT[3] * math.cos(6.0 * radians)
    )


def metres_per_degree_longitude(latitude: float) -> float:
    radians = math.radians(latitude)
    return (
        111412.84 * math.cos(radians)
        - 93.5 * math.cos(3.0 * radians)
        + 0.118 * math.cos(5.0 * radians)
    )


def dimensions_in_degrees(
    center_lat: float, width_metres: float, height_metres: float
) -> tuple[float, float]:
    return (
        width_metres / metres_per_degree_longitude(center_lat),
        height_metres / metres_per_degree_latitude(center_lat),
    )


def dimensions_in_web_mercator_metres(
    center_lat: float, width_metres: float, height_metres: float
) -> tuple[float, float]:
    scale = 1.0 / math.cos(math.radians(center_lat))
    return width_metres * scale, height_metres * scale


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


def metadata_bounds(
    center_lat: float, center_lon: float, width_metres: float, height_metres: float
) -> str:
    width_degrees, height_degrees = dimensions_in_degrees(
        center_lat, width_metres, height_metres
    )
    return ",".join(
        (
            f"{center_lon - width_degrees / 2.0:.12f}",
            f"{center_lat - height_degrees / 2.0:.12f}",
            f"{center_lon + width_degrees / 2.0:.12f}",
            f"{center_lat + height_degrees / 2.0:.12f}",
        )
    )


def tile_ranges(
    center_lat: float,
    center_lon: float,
    min_zoom: int,
    max_zoom: int,
    width_metres: float,
    height_metres: float,
) -> dict[int, tuple[int, int, int, int]]:
    extent_width, extent_height = dimensions_in_web_mercator_metres(
        center_lat, width_metres, height_metres
    )
    ranges = {}
    for zoom in range(min_zoom, max_zoom + 1):
        metres_per_pixel = 2.0 * math.pi * WEB_MERCATOR_RADIUS / (
            TILE_SIZE * 2**zoom
        )
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


def add_layers(
    project,
    gpkg: Path,
    map_name: str,
    building_layer_name: str,
    road_layer_name: str,
    raster_opacity: float,
):
    from qgis.core import (
        QgsFillSymbol,
        QgsLineSymbol,
        QgsProject,
        QgsRasterLayer,
        QgsSingleSymbolRenderer,
        QgsVectorLayer,
    )

    wms_uri = (
        "url=https://www.ign.es/wms-inspire/pnoa-ma"
        "&layers=OI.OrthoimageCoverage"
        "&styles="
        "&format=image/jpeg"
        "&crs=EPSG:3857"
        "&version=1.1.1"
        "&transparent=false"
    )
    orthophoto = QgsRasterLayer(wms_uri, "PNOA 2023 - IGN/CNIG", "wms")
    if not orthophoto.isValid():
        raise RuntimeError(
            "Could not load the official PNOA WMS: " + orthophoto.error().summary()
        )
    try:
        orthophoto.setOpacity(raster_opacity)
    except AttributeError:
        orthophoto.renderer().setOpacity(raster_opacity)
    project.addMapLayer(orthophoto)

    def add_vector(name: str, layer_name: str):
        layer = QgsVectorLayer(f"{gpkg}|layername={layer_name}", name, "ogr")
        if not layer.isValid():
            raise RuntimeError(f"Could not load {layer_name} from {gpkg}")
        project.addMapLayer(layer)
        return layer

    buildings = add_vector(f"BUILDINGS - mapped reference - {map_name}", building_layer_name)
    building_symbol = QgsFillSymbol.createSimple(
        {
            "color": "#07140D",
            "outline_color": "#65F28E",
            "outline_width": "0.18",
        }
    )
    building_symbol.setOpacity(0.72)
    buildings.setRenderer(QgsSingleSymbolRenderer(building_symbol))

    roads = add_vector(f"ROADS - mapped reference - {map_name}", road_layer_name)
    road_symbol = QgsLineSymbol.createSimple(
        {"color": "#63F18C", "width": "0.55", "capstyle": "round"}
    )
    road_symbol.setOpacity(0.9)
    roads.setRenderer(QgsSingleSymbolRenderer(road_symbol))

    return [orthophoto, buildings, roads]


def render_strip(
    project,
    layers,
    x_min: int,
    x_max: int,
    y: int,
    zoom: int,
) -> list[bytes]:
    from qgis.PyQt.QtCore import QBuffer, QIODevice, QSize, Qt
    from qgis.PyQt.QtGui import QImage
    from qgis.core import (
        QgsCoordinateReferenceSystem,
        QgsMapRendererSequentialJob,
        QgsMapSettings,
        QgsRectangle,
    )

    pngs: list[bytes] = []
    chunk_columns = max(1, MAX_WMS_REQUEST_PIXELS // TILE_SIZE)
    for chunk_min in range(x_min, x_max + 1, chunk_columns):
        chunk_max = min(x_max, chunk_min + chunk_columns - 1)
        columns = chunk_max - chunk_min + 1
        settings = QgsMapSettings()
        # QgsMapSettings expects the first layer at the top of the stack.
        # Keep the orthophoto below the green/black reference layers.
        settings.setLayers(list(reversed(layers)))
        settings.setDestinationCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
        settings.setTransformContext(project.transformContext())
        settings.setExtent(
            QgsRectangle(
                mercator_x(chunk_min * TILE_SIZE, zoom),
                mercator_y((y + 1) * TILE_SIZE, zoom),
                mercator_x((chunk_max + 1) * TILE_SIZE, zoom),
                mercator_y(y * TILE_SIZE, zoom),
            )
        )
        settings.setOutputSize(QSize(columns * TILE_SIZE, TILE_SIZE))
        settings.setOutputImageFormat(QImage.Format_ARGB32_Premultiplied)
        job = QgsMapRendererSequentialJob(settings)
        job.start()
        job.waitForFinished()
        if job.errors():
            raise RuntimeError(f"QGIS strip render failed at z{zoom}, row {y}: {job.errors()}")
        image = job.renderedImage()
        if image.isNull() or image.width() != columns * TILE_SIZE or image.height() != TILE_SIZE:
            raise RuntimeError(f"QGIS returned an invalid strip at z{zoom}, row {y}")
        for column in range(columns):
            tile = image.copy(column * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE)
            # PNOA is photographic. Indexed PNG with ordered dithering keeps
            # the orthophoto recognizable while cutting APK/MBTiles weight
            # substantially; it remains a normal PNG for the current app.
            tile = tile.convertToFormat(QImage.Format_Indexed8, Qt.OrderedDither)
            buffer = QBuffer()
            if not buffer.open(QIODevice.WriteOnly) or not tile.save(buffer, "PNG"):
                raise RuntimeError(f"Could not encode PNG at z{zoom}/{chunk_min + column}/{y}")
            pngs.append(bytes(buffer.data()))
            buffer.close()
    return pngs


def build(args: argparse.Namespace) -> None:
    from qgis.core import QgsApplication, QgsCoordinateReferenceSystem, QgsProject
    from qgis.PyQt.QtGui import QColor

    prefix = os.environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise RuntimeError("QGIS_PREFIX_PATH is not defined; use python-qgis-ltr.bat")
    qgis = QgsApplication([], False)
    qgis.setPrefixPath(prefix, True)
    qgis.initQgis()
    try:
        if args.output.exists():
            if not args.force:
                raise RuntimeError(f"Refusing to overwrite existing output: {args.output}")
            args.output.unlink()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.project_output.parent.mkdir(parents=True, exist_ok=True)

        project = QgsProject()
        project.setCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
        project.setTitle("SuriOS BRICKTOWN - PNOA 2023 hybrid")
        project.setBackgroundColor(QColor(7, 20, 13, 255))
        layers = add_layers(
            project,
            args.gpkg,
            args.map_name,
            args.building_layer,
            args.road_layer,
            args.raster_opacity,
        )
        root = project.layerTreeRoot()
        root.setHasCustomLayerOrder(True)
        root.setCustomLayerOrder(list(reversed(layers)))
        for layer in layers:
            root.findLayer(layer.id()).setItemVisibilityChecked(True)
        if not project.write(str(args.project_output)):
            raise RuntimeError(f"Could not write QGIS project {args.project_output}")

        ranges = tile_ranges(
            args.center_lat,
            args.center_lon,
            args.min_zoom,
            args.max_zoom,
            args.width_metres,
            args.height_metres,
        )
        connection = sqlite3.connect(str(args.output))
        try:
            connection.execute("PRAGMA journal_mode=DELETE")
            connection.execute("PRAGMA synchronous=FULL")
            connection.executescript(
                """
                CREATE TABLE metadata (name TEXT PRIMARY KEY, value TEXT);
                CREATE TABLE tiles (
                    zoom_level INTEGER,
                    tile_column INTEGER,
                    tile_row INTEGER,
                    tile_data BLOB
                );
                CREATE UNIQUE INDEX tiles_idx
                    ON tiles (zoom_level, tile_column, tile_row);
                """
            )
            metadata = {
                "format": "png",
                "map_id": args.map_id,
                "name": args.metadata_name,
                "description": "BRICKTOWN - ortofotografia PNOA 2023 hibrida",
                "version": "1.2",
                "style": "SURIOS_PIP_BRICKTOWN_HYBRID_V1",
                "type": "overlay",
                "minzoom": str(args.min_zoom),
                "maxzoom": str(args.max_zoom),
                "bounds": metadata_bounds(
                    args.center_lat,
                    args.center_lon,
                    args.width_metres,
                    args.height_metres,
                ),
                "attribution": "PNOA Maxima Actualidad 2023 (captura 2023-07) (c) IGN/CNIG",
                "source": "https://www.ign.es/wms-inspire/pnoa-ma",
                "capture_date": "2023-07",
                "coverage": f"{args.width_metres:.0f} x {args.height_metres:.0f} m",
                "overlay_note": "Edificios y caminos son referencias cartografiadas; sin perimetro de juego inventado",
            }
            connection.executemany(
                "INSERT INTO metadata(name,value) VALUES (?,?)", metadata.items()
            )
            total = 0
            for zoom in range(args.min_zoom, args.max_zoom + 1):
                x_min, x_max, y_min, y_max = ranges[zoom]
                for y in range(y_min, y_max + 1):
                    pngs = render_strip(project, layers, x_min, x_max, y, zoom)
                    for offset, png in enumerate(pngs):
                        x = x_min + offset
                        tms_y = (1 << zoom) - 1 - y
                        connection.execute(
                            "INSERT INTO tiles VALUES (?,?,?,?)",
                            (zoom, x, tms_y, sqlite3.Binary(png)),
                        )
                        total += 1
                connection.commit()
                print(
                    f"ZOOM={zoom} TILES={(x_max - x_min + 1) * (y_max - y_min + 1)}",
                    flush=True,
                )
            connection.commit()
        finally:
            connection.close()

        print(f"OUTPUT={args.output}", flush=True)
        print(f"PROJECT={args.project_output}", flush=True)
        print(f"BOUNDS={metadata['bounds']}", flush=True)
        print(f"TILES={total}", flush=True)
        print(f"RANGES={ranges}", flush=True)
        os._exit(0)
    finally:
        pass


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--gpkg", type=Path, required=True)
    parser.add_argument("--project-output", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--center-lat", type=float, required=True)
    parser.add_argument("--center-lon", type=float, required=True)
    parser.add_argument("--map-id", default="bricktown")
    parser.add_argument("--map-name", default="BRICKTOWN")
    parser.add_argument("--metadata-name", default="bricktown_terrain")
    parser.add_argument("--building-layer", default="building")
    parser.add_argument("--road-layer", default="highway")
    parser.add_argument("--width-metres", type=float, default=2500.0)
    parser.add_argument("--height-metres", type=float, default=2500.0)
    parser.add_argument("--raster-opacity", type=float, default=0.86)
    parser.add_argument("--min-zoom", type=int, default=16)
    parser.add_argument("--max-zoom", type=int, default=19)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()
    if args.min_zoom > args.max_zoom:
        parser.error("min zoom must not exceed max zoom")
    if args.width_metres <= 0 or args.height_metres <= 0:
        parser.error("coverage dimensions must be positive")
    if not 0.0 <= args.raster_opacity <= 1.0:
        parser.error("raster opacity must be between 0 and 1")
    build(args)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
