"""Re-render the existing raster MBTiles with the SuriOS daylight palette.

This fallback is used only when the original vector source is unavailable
(including HOME). It keeps the MBTiles footprint, zoom levels and metadata,
and changes the PNG pixels only. Run it with the QGIS Python wrapper because
the Android maps are PNG rasters and QGIS already provides a reliable QImage
decoder on Windows.
"""

from __future__ import annotations

import argparse
import sqlite3
from pathlib import Path

import numpy as np
from qgis.PyQt.QtGui import QImage


OLD_BACKGROUND = (5, 8, 5)
OLD_BUILDING = (96, 96, 96)
OLD_ROAD = (47, 126, 190)
OLD_CONTOUR_MINOR = (76, 179, 89)
OLD_CONTOUR_MAJOR = (91, 214, 107)

DAY_BACKGROUND = (244, 241, 232)
DAY_BUILDING = (102, 114, 122)
DAY_OUTLINE = (38, 50, 56)
DAY_ROAD = (0, 90, 115)
DAY_CONTOUR_MINOR = (96, 125, 59)
DAY_CONTOUR_MAJOR = (122, 62, 141)


def image_bytes(image: QImage) -> memoryview:
    bits = image.bits()
    bits.setsize(image.sizeInBytes())
    return memoryview(bits)


def recolor(image: QImage, already_day: bool = False) -> QImage:
    image = image.convertToFormat(QImage.Format_RGBA8888)
    width, height = image.width(), image.height()
    rgba = np.frombuffer(image_bytes(image), dtype=np.uint8).reshape((height, width, 4))
    if already_day:
        current = rgba[:, :, :3]
        outline = np.all(current == np.array(DAY_OUTLINE, dtype=np.uint8), axis=2)
        rgba[outline, :3] = DAY_BACKGROUND
        return image

    rgb_pixels = rgba[:, :, :3].astype(np.int16)
    background_pixels = np.all(rgb_pixels == np.array(OLD_BACKGROUND, dtype=np.int16), axis=2)
    source_colors = np.array([
        OLD_BUILDING,
        OLD_ROAD,
        OLD_CONTOUR_MINOR,
        OLD_CONTOUR_MAJOR,
    ], dtype=np.int16)
    target_colors = np.array([
        DAY_BUILDING,
        DAY_ROAD,
        DAY_CONTOUR_MINOR,
        DAY_CONTOUR_MAJOR,
    ], dtype=np.uint8)
    distances = ((rgb_pixels[:, :, None, :] - source_colors[None, None, :, :]) ** 2).sum(axis=3)
    nearest = distances.argmin(axis=2)
    rgba[:, :, :3] = target_colors[nearest]
    # The legacy raster used the same RGB for the background and outlines.
    # Prioritise a clean daylight base; building silhouettes remain clear from
    # their slate fill and the map no longer contains enclosed black islands.
    rgba[background_pixels, :3] = DAY_BACKGROUND
    return image


def convert_tile(data: bytes, already_day: bool = False) -> bytes:
    image = QImage.fromData(data, "PNG")
    if image.isNull() or image.width() != 256 or image.height() != 256:
        raise ValueError("invalid 256x256 PNG tile")
    output = recolor(image, already_day=already_day)
    buffer = bytearray()
    # QBuffer avoids a temporary image file and preserves PNG output.
    from qgis.PyQt.QtCore import QBuffer, QIODevice

    target = QBuffer()
    if not target.open(QIODevice.WriteOnly) or not output.save(target, "PNG"):
        raise ValueError("could not encode recolored PNG tile")
    buffer.extend(bytes(target.data()))
    target.close()
    return bytes(buffer)


def convert(source: Path, output: Path) -> int:
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
        already_day = metadata.get("style") == "SURIOS_DAY_V1"
        metadata["style"] = "SURIOS_DAY_V1"
        output_db.executemany("INSERT INTO metadata(name,value) VALUES (?,?)", metadata.items())

        total = 0
        for zoom, column, row, data in source_db.execute(
            "SELECT zoom_level,tile_column,tile_row,tile_data FROM tiles ORDER BY zoom_level,tile_column,tile_row"
        ):
            output_db.execute(
                "INSERT INTO tiles VALUES (?,?,?,?)",
                (zoom, column, row, sqlite3.Binary(convert_tile(data, already_day))),
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
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()
    if args.output.exists() and not args.force:
        parser.error("output exists; use --force")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    total = convert(args.source, args.output)
    print(f"OUTPUT={args.output}", flush=True)
    print(f"TILES={total}", flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
