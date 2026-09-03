"""Prepare a small GeoPackage from Overpass JSON for the terrain renderer.

The input is the JSON returned by an Overpass query using ``out tags geom``.
Only real OSM building polygons and useful road classes are copied. The
resulting GeoPackage is a local source file and is not committed to the app.
"""

from __future__ import annotations

import argparse
import json
import os
from pathlib import Path


EXCLUDED_HIGHWAYS = {
    "bridleway",
    "bus_stop",
    "construction",
    "corridor",
    "crossing",
    "cycleway",
    "elevator",
    "footway",
    "path",
    "pedestrian",
    "platform",
    "proposed",
    "raceway",
    "steps",
    "track",
}


def read_elements(source: Path) -> tuple[list[dict], list[dict]]:
    payload = json.loads(source.read_text(encoding="utf-8"))
    buildings: list[dict] = []
    roads: list[dict] = []
    for element in payload.get("elements", []):
        if element.get("type") != "way":
            continue
        geometry = element.get("geometry") or []
        points = [(point["lon"], point["lat"]) for point in geometry]
        tags = element.get("tags") or {}
        if tags.get("building") and len(points) >= 4 and points[0] == points[-1]:
            buildings.append({"id": element["id"], "kind": tags["building"], "points": points})
        highway = tags.get("highway")
        if highway and highway not in EXCLUDED_HIGHWAYS and len(points) >= 2:
            roads.append({"id": element["id"], "kind": highway, "points": points})
    if not buildings:
        raise RuntimeError("The Overpass source contains no closed building ways")
    if not roads:
        raise RuntimeError("The Overpass source contains no usable highway ways")
    return buildings, roads


def write_layer(
    gpkg: Path,
    layer_name: str,
    geometry_type: str,
    features: list[dict],
    overwrite_file: bool,
) -> None:
    from qgis.PyQt.QtCore import QVariant
    from qgis.core import (
        QgsFeature,
        QgsField,
        QgsGeometry,
        QgsPointXY,
        QgsProject,
        QgsVectorFileWriter,
        QgsVectorLayer,
    )

    layer = QgsVectorLayer(f"{geometry_type}?crs=EPSG:4326", layer_name, "memory")
    if not layer.isValid():
        raise RuntimeError(f"Could not create memory layer {layer_name}")
    provider = layer.dataProvider()
    provider.addAttributes(
        [QgsField("osm_id", QVariant.LongLong), QgsField("kind", QVariant.String)]
    )
    layer.updateFields()
    for item in features:
        feature = QgsFeature(layer.fields())
        points = [QgsPointXY(lon, lat) for lon, lat in item["points"]]
        if geometry_type == "Polygon":
            feature.setGeometry(QgsGeometry.fromPolygonXY([points]))
        else:
            feature.setGeometry(QgsGeometry.fromPolylineXY(points))
        feature.setAttribute("osm_id", item["id"])
        feature.setAttribute("kind", item["kind"])
        provider.addFeature(feature)
    layer.updateExtents()

    options = QgsVectorFileWriter.SaveVectorOptions()
    options.driverName = "GPKG"
    options.layerName = layer_name
    options.actionOnExistingFile = (
        QgsVectorFileWriter.CreateOrOverwriteFile
        if overwrite_file
        else QgsVectorFileWriter.CreateOrOverwriteLayer
    )
    result = QgsVectorFileWriter.writeAsVectorFormatV3(
        layer,
        str(gpkg),
        QgsProject.instance().transformContext(),
        options,
    )
    error = result[0]
    message = result[1] if len(result) > 1 else ""
    if error != QgsVectorFileWriter.NoError:
        raise RuntimeError(f"Could not write {layer_name}: {message}")


def copy_contour_layer(gpkg: Path, source_gpkg: Path, layer_name: str) -> int:
    """Copy a generated elevation contour layer into the OSM GeoPackage."""
    from qgis.core import QgsProject, QgsVectorFileWriter, QgsVectorLayer

    source = QgsVectorLayer(f"{source_gpkg}|layername={layer_name}", layer_name, "ogr")
    if not source.isValid():
        raise RuntimeError(f"Could not load {layer_name} from {source_gpkg}")
    options = QgsVectorFileWriter.SaveVectorOptions()
    options.driverName = "GPKG"
    options.layerName = layer_name
    options.actionOnExistingFile = QgsVectorFileWriter.CreateOrOverwriteLayer
    result = QgsVectorFileWriter.writeAsVectorFormatV3(
        source,
        str(gpkg),
        QgsProject.instance().transformContext(),
        options,
    )
    if result[0] != QgsVectorFileWriter.NoError:
        message = result[1] if len(result) > 1 else ""
        raise RuntimeError(f"Could not copy {layer_name}: {message}")
    return source.featureCount()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", type=Path, required=True, help="Overpass JSON source")
    parser.add_argument("--output", type=Path, required=True, help="GeoPackage output")
    parser.add_argument(
        "--contours-source",
        type=Path,
        help="Optional GeoPackage containing a generated contours_2m layer",
    )
    parser.add_argument("--contour-layer", default="contours_2m")
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()
    if not args.input.is_file():
        parser.error(f"Input does not exist: {args.input}")
    if args.output.exists() and not args.force:
        parser.error(f"Refusing to overwrite existing output: {args.output}")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    if args.output.exists():
        args.output.unlink()

    from qgis.core import QgsApplication

    prefix = os.environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise RuntimeError("QGIS_PREFIX_PATH is not defined; use python-qgis-ltr.bat")
    qgis = QgsApplication([], False)
    qgis.setPrefixPath(prefix, True)
    qgis.initQgis()
    try:
        buildings, roads = read_elements(args.input)
        write_layer(args.output, "building", "Polygon", buildings, overwrite_file=True)
        write_layer(args.output, "highway", "LineString", roads, overwrite_file=False)
        if args.contours_source:
            if not args.contours_source.is_file():
                raise RuntimeError(f"Contours source does not exist: {args.contours_source}")
            count = copy_contour_layer(args.output, args.contours_source, args.contour_layer)
            print(f"CONTOURS={count}", flush=True)
        print(f"BUILDINGS={len(buildings)}", flush=True)
        print(f"ROADS={len(roads)}", flush=True)
        print(f"OUTPUT={args.output}", flush=True)
    finally:
        qgis.exitQgis()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
