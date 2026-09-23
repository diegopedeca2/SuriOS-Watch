"""Export reviewed QGIS organization layers to the Android overlay format.

The GeoPackage is the editable source of truth. This script only creates the
small, offline asset consumed by OrganizationOverlayCodec; it does not replace
the terrain MBTiles or use organizer artwork as a map background.
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from pathlib import Path


SUPPORTED_POI_TYPES = {
    "RESPAWN",
    "BASE",
    "POI",
    "AMMO",
    "ENTRANCE",
    "SAFE_ZONE",
    "PARKING",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--gpkg", required=True, help="Reviewed GeoPackage")
    parser.add_argument("--output", required=True, help="Destination .overlay file")
    parser.add_argument("--map-id", required=True, help="PIP-SuriOS map id")
    parser.add_argument(
        "--source-status",
        default="PROVISIONAL_QGIS_REVIEW",
        help="SOURCE_STATUS written to the asset",
    )
    parser.add_argument("--grid-layer", default="GRID")
    parser.add_argument("--boundary-layer", default="FIELD_BOUNDARY")
    parser.add_argument("--paths-layer", default="INTERNAL_PATHS")
    return parser.parse_args()


def init_qgis():
    prefix = os.environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise SystemExit("QGIS_PREFIX_PATH is not set. Use python-qgis-ltr.bat.")
    plugin_path = Path(prefix) / "python" / "plugins"
    if str(plugin_path) not in sys.path:
        sys.path.insert(0, str(plugin_path))
    from qgis.core import QgsApplication

    app = QgsApplication([], False)
    app.setPrefixPath(prefix, True)
    app.initQgis()
    return app


def load_layer(gpkg: Path, name: str):
    from qgis.core import QgsVectorLayer

    layer = QgsVectorLayer(f"{gpkg}|layername={name}", name, "ogr")
    if not layer.isValid():
        return None
    return layer


def field_value(feature, names: tuple[str, ...], fallback: str = "") -> str:
    for name in names:
        if name in feature.fields().names():
            value = feature[name]
            if value is not None and str(value).strip():
                return str(value).strip()
    return fallback


def transform_geometry(layer, feature):
    from qgis.core import QgsCoordinateReferenceSystem, QgsCoordinateTransform, QgsProject

    geometry = feature.geometry()
    if geometry.isEmpty():
        return None
    target = QgsCoordinateReferenceSystem("EPSG:4326")
    if layer.crs() != target:
        transform = QgsCoordinateTransform(layer.crs(), target, QgsProject.instance())
        geometry = geometry.clone()
        geometry.transform(transform)
    return geometry


def vertices(layer, feature) -> list[tuple[float, float]]:
    geometry = transform_geometry(layer, feature)
    if geometry is None:
        return []
    return [(float(point.y()), float(point.x())) for point in geometry.vertices()]


def point(layer, feature) -> tuple[float, float] | None:
    values = vertices(layer, feature)
    if not values:
        return None
    geometry = transform_geometry(layer, feature)
    if geometry is not None and not geometry.isMultipart():
        center = geometry.centroid().asPoint()
        return float(center.y()), float(center.x())
    return values[0]


def fmt(value: float) -> str:
    return f"{value:.12f}".rstrip("0").rstrip(".")


def coordinate_text(values: list[tuple[float, float]]) -> str:
    return "|".join(f"{fmt(latitude)},{fmt(longitude)}" for latitude, longitude in values)


def validate_point(latitude: float, longitude: float) -> None:
    if not -90.0 <= latitude <= 90.0 or not -180.0 <= longitude <= 180.0:
        raise ValueError(f"Invalid WGS84 coordinate: {latitude},{longitude}")


def grid_records(
    layer,
) -> tuple[
    tuple[float, float, float, float],
    list[str],
    list[str],
    list[tuple[str, float, float, float, float]],
]:
    if layer is None:
        raise ValueError("GRID layer is required")
    all_points: list[tuple[float, float]] = []
    cell_ids: list[str] = []
    cells: list[tuple[str, float, float, float, float]] = []
    for feature in layer.getFeatures():
        geometry = transform_geometry(layer, feature)
        if geometry is None:
            continue
        all_points.extend((float(point.y()), float(point.x())) for point in geometry.vertices())
        cell_id = field_value(feature, ("cell_id", "grid_id", "id"))
        if not cell_id:
            continue
        cell_ids.append(cell_id)
        box = geometry.boundingBox()
        cells.append(
            (
                cell_id,
                float(box.xMinimum()),
                float(box.yMinimum()),
                float(box.xMaximum()),
                float(box.yMaximum()),
            )
        )
    if not all_points:
        raise ValueError("GRID layer has no geometry")
    if not cell_ids:
        raise ValueError("GRID layer needs a cell_id, grid_id or id field")
    if len(set(cell_ids)) != len(cell_ids):
        raise ValueError("GRID cell identifiers must be unique")

    pattern = re.compile(r"^(.+?)(?:[-_])?(\d+)$")
    parsed = [pattern.match(value) for value in cell_ids]
    if any(match is None for match in parsed):
        raise ValueError("GRID identifiers must look like A-1 or A1")
    rows = sorted({match.group(1) for match in parsed if match}, key=lambda value: (len(value), value))
    columns = sorted({match.group(2) for match in parsed if match}, key=int)
    latitudes = [value[0] for value in all_points]
    longitudes = [value[1] for value in all_points]
    return (
        (
            min(longitudes),
            min(latitudes),
            max(longitudes),
            max(latitudes),
        ),
        rows,
        columns,
        cells,
    )


def poi_type(layer_name: str, feature) -> str:
    default_types = {
        "RESPAWNS": "RESPAWN",
        "BASES": "BASE",
        "AMMO": "AMMO",
        "ENTRANCE": "ENTRANCE",
        "SAFE_ZONE": "SAFE_ZONE",
        "PARKING": "PARKING",
        "POI": "POI",
    }
    raw = field_value(feature, ("type", "poi_type", "kind"), default_types.get(layer_name, "POI"))
    normalized = raw.upper().strip().replace("-", "_").replace(" ", "_")
    if normalized == "SAFEZONE":
        normalized = "SAFE_ZONE"
    if normalized not in SUPPORTED_POI_TYPES:
        normalized = "POI"
    return normalized


def export(args: argparse.Namespace) -> int:
    from qgis.core import QgsProject

    gpkg = Path(args.gpkg).resolve()
    output = Path(args.output).resolve()
    if not gpkg.exists():
        raise SystemExit(f"GeoPackage does not exist: {gpkg}")
    output.parent.mkdir(parents=True, exist_ok=True)

    grid = load_layer(gpkg, args.grid_layer)
    bounds, rows, columns, cells = grid_records(grid)
    lines = [
        f"MAP|{args.map_id}",
        f"META|SOURCE_STATUS|{args.source_status}",
        "GRID|{}|{}|{}|{}|{}|{}".format(
            *(fmt(value) for value in bounds), ",".join(rows), ",".join(columns)
        ),
    ]
    lines.extend(
        "CELL|{}|{}|{}|{}|{}".format(
            cell_id,
            *(fmt(value) for value in (west, south, east, north)),
        )
        for cell_id, west, south, east, north in cells
    )

    boundary = load_layer(gpkg, args.boundary_layer)
    if boundary is not None:
        feature = next(boundary.getFeatures(), None)
        values = vertices(boundary, feature) if feature is not None else []
        if values:
            lines.append(f"BOUNDARY|field|{coordinate_text(values)}")

    paths = load_layer(gpkg, args.paths_layer)
    if paths is not None:
        for index, feature in enumerate(paths.getFeatures(), start=1):
            values = vertices(paths, feature)
            if len(values) < 2:
                continue
            path_id = field_value(feature, ("id", "path_id"), f"path_{index}")
            name = field_value(feature, ("name", "label", "nombre"), path_id)
            lines.append(f"PATH|{path_id}|{name}|{coordinate_text(values)}")

    for layer_name in (
        "RESPAWNS",
        "BASES",
        "POI",
        "AMMO",
        "ENTRANCE",
        "SAFE_ZONE",
        "PARKING",
    ):
        layer = load_layer(gpkg, layer_name)
        if layer is None:
            continue
        for index, feature in enumerate(layer.getFeatures(), start=1):
            value = point(layer, feature)
            if value is None:
                continue
            latitude, longitude = value
            validate_point(latitude, longitude)
            poi_id = field_value(feature, ("id", "poi_id"), f"{layer_name.lower()}_{index}")
            name = field_value(feature, ("name", "label", "nombre"), poi_id)
            lines.append(
                f"POI|{poi_id}|{poi_type(layer_name, feature)}|{name}|"
                f"{fmt(latitude)}|{fmt(longitude)}"
            )

    output.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"OVERLAY_WRITTEN={output}")
    print(f"GRID_ROWS={','.join(rows)}")
    print(f"GRID_COLUMNS={','.join(columns)}")
    print(f"RECORDS={len(lines)}")
    return 0


def main() -> int:
    args = parse_args()
    app = init_qgis()
    from qgis.core import QgsProject

    try:
        return export(args)
    finally:
        QgsProject.instance().clear()
        app.exitQgis()


if __name__ == "__main__":
    raise SystemExit(main())
