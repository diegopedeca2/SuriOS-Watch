"""Create the bright P.R.S.-Tracker-style palette for an existing MBTiles map.

The terrain source and its coverage are preserved. PNG pixels are remapped to
the PIP-SuriOS bright palette and an optional organization field boundary is
drawn into each tile. The Android vector overlay still draws the same boundary
above the raster, so it remains an editable geographic layer as well.
"""

from __future__ import annotations

import argparse
import math
import sqlite3
from pathlib import Path

import numpy as np
from qgis.PyQt.QtCore import QBuffer, QIODevice, QPointF
from qgis.PyQt.QtGui import QColor, QImage, QPainter, QPainterPath, QPen


# SURIOS_DAY_V1 colors used by the existing terrain renderer.
SOURCE_COLORS = np.array(
    [
        (244, 241, 232),  # background
        (102, 114, 122),  # buildings
        (38, 50, 56),  # building outline
        (0, 90, 115),  # paths/roads
        (96, 125, 59),  # minor contours
        (122, 62, 141),  # major contours
    ],
    dtype=np.int16,
)

# P.R.S. Tracker-inspired bright map palette.
TARGET_COLORS = np.array(
    [
        (0, 0, 0),  # map background
        (0, 0, 0),  # buildings remain black
        (63, 175, 90),  # building outline
        (102, 255, 153),  # paths/roads
        (63, 175, 90),  # minor contours
        (102, 255, 102),  # major contours
    ],
    dtype=np.uint8,
)


def image_bytes(image: QImage) -> memoryview:
    bits = image.bits()
    bits.setsize(image.sizeInBytes())
    return memoryview(bits)


def encode_png(image: QImage) -> bytes:
    target = QBuffer()
    if not target.open(QIODevice.WriteOnly) or not image.save(target, "PNG"):
        raise ValueError("could not encode PNG tile")
    result = bytes(target.data())
    target.close()
    return result


def recolor_image(data: bytes) -> QImage:
    image = QImage.fromData(data, "PNG")
    if image.isNull() or image.width() != 256 or image.height() != 256:
        raise ValueError("invalid 256x256 PNG tile")
    image = image.convertToFormat(QImage.Format_RGBA8888)
    rgba = np.frombuffer(image_bytes(image), dtype=np.uint8).reshape(
        (image.height(), image.width(), 4)
    )
    # Use int32: squared RGB differences can overflow int16.
    rgb = rgba[:, :, :3].astype(np.int32)
    source_colors = SOURCE_COLORS.astype(np.int32)
    distances = ((rgb[:, :, None, :] - source_colors[None, None, :, :]) ** 2).sum(axis=3)
    nearest = distances.argmin(axis=2)
    rgba[:, :, :3] = TARGET_COLORS[nearest]
    return image


def read_boundary(overlay: Path | None) -> list[tuple[float, float]]:
    if overlay is None:
        return []
    for raw_line in overlay.read_text(encoding="utf-8").splitlines():
        parts = raw_line.strip().split("|")
        if not parts or parts[0] != "BOUNDARY":
            continue
        points = []
        for value in parts[2:]:
            latitude, longitude = value.split(",")
            points.append((float(latitude), float(longitude)))
        return points
    return []


def world_pixel(latitude: float, longitude: float, zoom: int) -> tuple[float, float]:
    latitude = max(-85.05112878, min(85.05112878, latitude))
    scale = 256.0 * (1 << zoom)
    x = (longitude + 180.0) / 360.0 * scale
    sine = math.sin(math.radians(latitude))
    y = (0.5 - math.log((1.0 + sine) / (1.0 - sine)) / (4.0 * math.pi)) * scale
    return x, y


def draw_boundary(
    image: QImage,
    zoom: int,
    column: int,
    row: int,
    boundary: list[tuple[float, float]],
) -> None:
    if len(boundary) < 2:
        return
    xyz_row = (1 << zoom) - 1 - row
    path = QPainterPath()
    for index, (latitude, longitude) in enumerate(boundary):
        world_x, world_y = world_pixel(latitude, longitude, zoom)
        point = QPointF(world_x - column * 256.0, world_y - xyz_row * 256.0)
        if index == 0:
            path.moveTo(point)
        else:
            path.lineTo(point)

    painter = QPainter(image)
    painter.setRenderHint(QPainter.Antialiasing, True)
    # A dark halo keeps the bright boundary readable over contours and roads.
    zoom_width = max(2.0, min(6.0, 2.5 * (2.0 ** (zoom - 17))))
    painter.setPen(QPen(QColor(0, 0, 0, 230), zoom_width + 3.0))
    painter.drawPath(path)
    painter.setPen(QPen(QColor(102, 255, 153, 245), zoom_width))
    painter.drawPath(path)
    painter.end()


def transform_tile(
    data: bytes,
    zoom: int,
    column: int,
    row: int,
    boundary: list[tuple[float, float]],
) -> bytes:
    image = recolor_image(data)
    draw_boundary(image, zoom, column, row, boundary)
    return encode_png(image)


def convert(source: Path, output: Path, boundary: list[tuple[float, float]]) -> int:
    if output.exists():
        output.unlink()
    source_db = sqlite3.connect(str(source))
    output_db = sqlite3.connect(str(output))
    try:
        output_db.executescript(
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
        metadata = dict(source_db.execute("SELECT name,value FROM metadata"))
        metadata["style"] = "SURIOS_PIP_TRACKER_V1"
        metadata["description"] = "Bright P.R.S. Tracker-style terrain with organization boundary"
        output_db.executemany(
            "INSERT INTO metadata(name,value) VALUES (?,?)", metadata.items()
        )
        total = 0
        for zoom, column, row, data in source_db.execute(
            "SELECT zoom_level,tile_column,tile_row,tile_data "
            "FROM tiles ORDER BY zoom_level,tile_column,tile_row"
        ):
            output_db.execute(
                "INSERT INTO tiles VALUES (?,?,?,?)",
                (
                    zoom,
                    column,
                    row,
                    sqlite3.Binary(transform_tile(data, zoom, column, row, boundary)),
                ),
            )
            total += 1
            if total % 100 == 0:
                output_db.commit()
                print(f"TILES={total}", flush=True)
        output_db.commit()
        return total
    finally:
        source_db.close()
        output_db.close()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--boundary-overlay", type=Path)
    args = parser.parse_args()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    boundary = read_boundary(args.boundary_overlay)
    total = convert(args.source, args.output, boundary)
    print(f"OUTPUT={args.output}", flush=True)
    print(f"TILES={total}", flush=True)
    print(f"BOUNDARY_POINTS={len(boundary)}", flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
