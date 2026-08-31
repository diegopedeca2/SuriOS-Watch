"""Build an offline terrain map in QGIS using the final HOME visual contract.

The default arguments reproduce NAVY7, while the same parameterized pipeline
can build other fields such as OFFICE. The source GeoPackage must contain a
building polygon layer and a highway line layer; contour lines are optional.
The project created by this script is independent from older QGIS projects:
it contains only local, offline layers and applies the final HOME palette and
line widths directly.
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

# HOME's final geographic contract.
HOME_WIDTH_DEGREES = 0.05867
HOME_HEIGHT_DEGREES = 0.023
HOME_EXTENT_WIDTH_METRES = 6530.5898802350275
HOME_EXTENT_HEIGHT_METRES = 3359.221663842909

# Final opaque HOME palette, measured from home_terrain-v10 / app asset.
BACKGROUND = (5, 8, 5, 255)
BUILDING = "#606060"
CONTOUR_MINOR = "#4cb359"
CONTOUR_MAJOR = "#5bd66b"
ROAD = "#2f7ebe"

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


def tile_extent(x: int, y: int, zoom: int):
    from qgis.core import QgsRectangle

    return QgsRectangle(
        mercator_x(x * TILE_SIZE, zoom),
        mercator_y((y + 1) * TILE_SIZE, zoom),
        mercator_x((x + 1) * TILE_SIZE, zoom),
        mercator_y(y * TILE_SIZE, zoom),
    )


def metadata_bounds(center_lat: float, center_lon: float) -> str:
    return ",".join(
        (
            f"{center_lon - HOME_WIDTH_DEGREES / 2.0:.12f}",
            f"{center_lat - HOME_HEIGHT_DEGREES / 2.0:.12f}",
            f"{center_lon + HOME_WIDTH_DEGREES / 2.0:.12f}",
            f"{center_lat + HOME_HEIGHT_DEGREES / 2.0:.12f}",
        )
    )


def style_layers(
    project,
    gpkg: Path,
    map_name: str,
    building_layer_name: str,
    road_layer_name: str,
    contour_layer_name: str | None,
):
    from qgis.core import QgsFillSymbol, QgsLineSymbol, QgsSingleSymbolRenderer, QgsVectorLayer

    def add(name: str, layer_name: str):
        layer = QgsVectorLayer(f"{gpkg}|layername={layer_name}", name, "ogr")
        if not layer.isValid():
            raise RuntimeError(f"Could not load {layer_name} from {gpkg}")
        project.addMapLayer(layer)
        return layer

    # Buildings are the most visible HOME/NAVY7 distinction: solid neutral grey.
    buildings = add(f"BUILDINGS · {map_name} STYLE", building_layer_name)
    buildings.setRenderer(
        QgsSingleSymbolRenderer(
            QgsFillSymbol.createSimple(
                {
                    "color": BUILDING,
                    "outline_color": "#050805",
                    "outline_width": "0.10",
                }
            )
        )
    )

    # HOME's final road colour is the blue produced by its palette normalizer.
    roads = add(f"ROADS · {map_name} STYLE", road_layer_name)
    roads.setRenderer(
        QgsSingleSymbolRenderer(
            QgsLineSymbol.createSimple(
                {"color": ROAD, "width": "0.45", "capstyle": "round"}
            )
        )
    )

    if contour_layer_name is None:
        return [buildings, roads]

    contours_minor = add("ALTITUDE · 10 m", contour_layer_name)
    contours_minor.setSubsetString('round("ELEV") % 10 <> 0')
    contours_minor.setRenderer(
        QgsSingleSymbolRenderer(
            QgsLineSymbol.createSimple(
                {"color": CONTOUR_MINOR, "width": "0.55", "capstyle": "round"}
            )
        )
    )

    contours_major = add("ALTITUDE · 20 m (index)", contour_layer_name)
    contours_major.setSubsetString('round("ELEV") % 10 = 0')
    contours_major.setRenderer(
        QgsSingleSymbolRenderer(
            QgsLineSymbol.createSimple(
                {"color": CONTOUR_MAJOR, "width": "0.90", "capstyle": "round"}
            )
        )
    )

    # QgsMapSettings draws the list from bottom to top.  Buildings are below
    # roads and contours, exactly like the HOME composition.
    return [buildings, roads, contours_minor, contours_major]


def render_strip(project, layers, x_min: int, x_max: int, y: int, zoom: int) -> list[bytes]:
    from qgis.PyQt.QtCore import QBuffer, QIODevice, QSize
    from qgis.PyQt.QtGui import QColor, QImage
    from qgis.core import QgsCoordinateReferenceSystem, QgsMapRendererSequentialJob, QgsMapSettings, QgsRectangle

    columns = x_max - x_min + 1
    settings = QgsMapSettings()
    settings.setLayers(layers)
    settings.setDestinationCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
    settings.setTransformContext(project.transformContext())
    settings.setExtent(
        QgsRectangle(
            mercator_x(x_min * TILE_SIZE, zoom),
            mercator_y((y + 1) * TILE_SIZE, zoom),
            mercator_x((x_max + 1) * TILE_SIZE, zoom),
            mercator_y(y * TILE_SIZE, zoom),
        )
    )
    settings.setOutputSize(QSize(columns * TILE_SIZE, TILE_SIZE))
    settings.setOutputImageFormat(QImage.Format_ARGB32_Premultiplied)
    settings.setBackgroundColor(QColor(*BACKGROUND))

    job = QgsMapRendererSequentialJob(settings)
    job.start()
    job.waitForFinished()
    if job.errors():
        raise RuntimeError(f"QGIS strip render failed at z{zoom}, row {y}: {job.errors()}")
    image = job.renderedImage()
    if image.isNull() or image.width() != columns * TILE_SIZE or image.height() != TILE_SIZE:
        raise RuntimeError(f"QGIS returned an invalid strip at z{zoom}, row {y}")

    pngs: list[bytes] = []
    for column in range(columns):
        tile = image.copy(column * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE)
        buffer = QBuffer()
        if not buffer.open(QIODevice.WriteOnly) or not tile.save(buffer, "PNG"):
            raise RuntimeError(f"Could not encode PNG at z{zoom}/{x_min + column}/{y}")
        pngs.append(bytes(buffer.data()))
        buffer.close()
    return pngs


def tile_ranges(center_lat: float, center_lon: float, min_zoom: int, max_zoom: int):
    ranges = {}
    for zoom in range(min_zoom, max_zoom + 1):
        metres_per_pixel = 2.0 * math.pi * WEB_MERCATOR_RADIUS / (TILE_SIZE * 2**zoom)
        center_x = lon_to_x(center_lon, zoom)
        center_y = lat_to_y(center_lat, zoom)
        left = center_x - HOME_EXTENT_WIDTH_METRES / metres_per_pixel / 2.0
        right = center_x + HOME_EXTENT_WIDTH_METRES / metres_per_pixel / 2.0
        top = center_y - HOME_EXTENT_HEIGHT_METRES / metres_per_pixel / 2.0
        bottom = center_y + HOME_EXTENT_HEIGHT_METRES / metres_per_pixel / 2.0
        ranges[zoom] = (
            math.floor(left / TILE_SIZE),
            math.ceil(right / TILE_SIZE) - 1,
            math.floor(top / TILE_SIZE),
            math.ceil(bottom / TILE_SIZE) - 1,
        )
    return ranges


def create_project(
    project_path: Path,
    gpkg: Path,
    center_lat: float,
    center_lon: float,
    map_name: str,
    building_layer_name: str,
    road_layer_name: str,
    contour_layer_name: str | None,
):
    from qgis.core import QgsCoordinateReferenceSystem, QgsProject
    from qgis.PyQt.QtGui import QColor

    project = QgsProject()
    project.setCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
    project.setTitle(f"SuriOS {map_name} HOME-style terrain")
    project.setBackgroundColor(QColor(*BACKGROUND))
    layers = style_layers(
        project,
        gpkg,
        map_name,
        building_layer_name,
        road_layer_name,
        contour_layer_name,
    )
    ranges = tile_ranges(center_lat, center_lon, 16, 19)
    root = project.layerTreeRoot()
    root.setHasCustomLayerOrder(True)
    root.setCustomLayerOrder([layer for layer in reversed(layers)])
    for layer in layers:
        root.findLayer(layer.id()).setItemVisibilityChecked(True)
    if not project.write(str(project_path)):
        raise RuntimeError(f"Could not write QGIS project {project_path}")
    return project, layers, ranges


def build(args: argparse.Namespace) -> dict[str, object]:
    from qgis.core import QgsApplication

    prefix = os.environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise RuntimeError("QGIS_PREFIX_PATH is not defined; use python-qgis-ltr.bat")

    qgis = QgsApplication([], False)
    qgis.setPrefixPath(prefix, True)
    qgis.initQgis()
    try:
        args.project_output.parent.mkdir(parents=True, exist_ok=True)
        args.output.parent.mkdir(parents=True, exist_ok=True)
        if args.output.exists():
            if not args.force:
                raise RuntimeError(f"Refusing to overwrite existing output: {args.output}")
            args.output.unlink()

        project, layers, ranges = create_project(
            args.project_output,
            args.gpkg,
            args.center_lat,
            args.center_lon,
            args.map_name,
            args.building_layer,
            args.road_layer,
            None if args.no_contours else args.contour_layer,
        )
        print("ACTIVE_LAYERS=" + ",".join(layer.name() for layer in layers), flush=True)
        print(f"QGIS_PROJECT={args.project_output}", flush=True)

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
                "description": args.map_name,
                "version": "1.1",
                "type": "overlay",
                "minzoom": str(args.min_zoom),
                "maxzoom": str(args.max_zoom),
                "bounds": metadata_bounds(args.center_lat, args.center_lon),
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

        result = {
            "output": args.output,
            "project": args.project_output,
            "bounds": metadata_bounds(args.center_lat, args.center_lon),
            "tile_count": total,
            "ranges": ranges,
        }
        print(f"OUTPUT={result['output']}", flush=True)
        print(f"BOUNDS={result['bounds']}", flush=True)
        print(f"TILES={result['tile_count']}", flush=True)
        print(f"RANGES={result['ranges']}", flush=True)
        os._exit(0)
    finally:
        # QGIS 3.44 can crash while tearing down a headless provider on
        # Windows. Successful runs terminate explicitly after all output has
        # been flushed, matching the guarded smoke-test harness.
        pass


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--gpkg", type=Path, required=True, help="Input GeoPackage")
    parser.add_argument("--project-output", type=Path, required=True, help="QGIS project output")
    parser.add_argument("--output", type=Path, required=True, help="MBTiles output")
    parser.add_argument("--center-lat", type=float, required=True)
    parser.add_argument("--center-lon", type=float, required=True)
    parser.add_argument("--map-id", default="navy7", help="Stable map identifier")
    parser.add_argument("--map-name", default="NAVY7", help="Visible map name")
    parser.add_argument("--metadata-name", default="navy_7_terrain")
    parser.add_argument("--building-layer", default="building")
    parser.add_argument("--road-layer", default="highway")
    parser.add_argument("--contour-layer", default="contours_2m")
    parser.add_argument("--no-contours", action="store_true")
    parser.add_argument("--min-zoom", type=int, default=16)
    parser.add_argument("--max-zoom", type=int, default=19)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()
    if args.min_zoom > args.max_zoom:
        parser.error("min zoom must not exceed max zoom")
    build(args)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
