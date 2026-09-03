"""Create the blank TESTING placeholder used by the CHECHU build."""

from __future__ import annotations

import sqlite3
import sys
from pathlib import Path


def main() -> int:
    output = Path(sys.argv[1])
    output.parent.mkdir(parents=True, exist_ok=True)
    if output.exists():
        output.unlink()

    connection = sqlite3.connect(output)
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
        "map_id": "testing",
        "name": "testing_empty",
        "description": "TESTING",
        "version": "1.1",
        "type": "overlay",
        "minzoom": "16",
        "maxzoom": "19",
        "bounds": "-0.010000000000,-0.010000000000,0.010000000000,0.010000000000",
    }
    connection.executemany("INSERT INTO metadata(name,value) VALUES (?,?)", metadata.items())

    transparent_png = bytes.fromhex(
        "89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C489"
        "0000000D49444154789C6360000000020001E221BC330000000049454E44AE426082"
    )
    for zoom in range(16, 20):
        centre_tile = 1 << (zoom - 1)
        tms_y = (1 << zoom) - 1 - centre_tile
        connection.execute(
            "INSERT INTO tiles VALUES (?,?,?,?)",
            (zoom, centre_tile, tms_y, transparent_png),
        )
    connection.commit()
    connection.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
