"""Export one MBTiles tile as a PNG for visual QA."""

from __future__ import annotations

import argparse
import sqlite3
from pathlib import Path

from qgis.PyQt.QtGui import QImage


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--zoom", type=int, default=19)
    parser.add_argument("--column", type=int)
    parser.add_argument("--xyz-row", type=int)
    args = parser.parse_args()
    database = sqlite3.connect(str(args.source))
    try:
        if args.column is None or args.xyz_row is None:
            data = database.execute(
                "SELECT tile_data FROM tiles WHERE zoom_level=? ORDER BY tile_column,tile_row LIMIT 1",
                (args.zoom,),
            ).fetchone()
        else:
            tms_row = (1 << args.zoom) - 1 - args.xyz_row
            data = database.execute(
                "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?",
                (args.zoom, args.column, tms_row),
            ).fetchone()
    finally:
        database.close()
    if data is None or not QImage.fromData(data[0], "PNG").save(str(args.output), "PNG"):
        raise RuntimeError("could not export an MBTiles tile")
    print(f"OUTPUT={args.output}", flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
