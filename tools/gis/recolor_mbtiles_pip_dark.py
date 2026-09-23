"""Convert the daylight terrain MBTiles to the PIP-SuriOS dark palette.

The source map remains the same offline terrain map: this only remaps the
already-rendered PNG pixels and preserves bounds, zoom levels and metadata.
It is useful when a map was generated with the daylight QGIS style but the
PIP-SuriOS terrain screen requires a black background.
"""

from __future__ import annotations

import argparse
import sqlite3
from pathlib import Path

import numpy as np
from qgis.PyQt.QtGui import QImage


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

# PIP-SuriOS dark palette: black terrain, green contours and readable
# dark-green buildings/paths. The brighter green is reserved for major lines.
TARGET_COLORS = np.array(
    [
        (5, 8, 5),
        (0, 0, 0),
        (63, 175, 90),
        (31, 96, 58),
        (63, 175, 90),
        (102, 255, 102),
    ],
    dtype=np.uint8,
)


def image_bytes(image: QImage) -> memoryview:
    bits = image.bits()
    bits.setsize(image.sizeInBytes())
    return memoryview(bits)


def recolor(data: bytes) -> bytes:
    image = QImage.fromData(data, "PNG")
    if image.isNull() or image.width() != 256 or image.height() != 256:
        raise ValueError("invalid 256x256 PNG tile")
    image = image.convertToFormat(QImage.Format_RGBA8888)
    rgba = np.frombuffer(image_bytes(image), dtype=np.uint8).reshape(
        (image.height(), image.width(), 4)
    )
    # Use int32: squared RGB differences can overflow int16 (244**2 > 32767).
    rgb = rgba[:, :, :3].astype(np.int32)
    source_colors = SOURCE_COLORS.astype(np.int32)
    distances = ((rgb[:, :, None, :] - source_colors[None, None, :, :]) ** 2).sum(axis=3)
    nearest = distances.argmin(axis=2)
    rgba[:, :, :3] = TARGET_COLORS[nearest]

    from qgis.PyQt.QtCore import QBuffer, QIODevice

    target = QBuffer()
    if not target.open(QIODevice.WriteOnly) or not image.save(target, "PNG"):
        raise ValueError("could not encode recolored PNG tile")
    result = bytes(target.data())
    target.close()
    return result


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
        metadata["style"] = "SURIOS_PIP_DARK_V1"
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
                (zoom, column, row, sqlite3.Binary(recolor(data)),),
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
    args = parser.parse_args()
    args.output.parent.mkdir(parents=True, exist_ok=True)
    total = convert(args.source, args.output)
    print(f"OUTPUT={args.output}", flush=True)
    print(f"TILES={total}", flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
