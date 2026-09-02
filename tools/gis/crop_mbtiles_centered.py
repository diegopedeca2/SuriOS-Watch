"""Create a centered, smaller MBTiles view without re-rendering its PNG tiles.

This is used when the original vector source is not available. It keeps the
existing rendered map (buildings, roads and contours) and selects only the
tiles needed around the requested centre.
"""

from __future__ import annotations

import argparse
import math
import sqlite3
from pathlib import Path


TILE_SIZE = 256
EARTH_RADIUS = 6378137.0
WORLD_HALF = math.pi * EARTH_RADIUS


def lon_to_world(lon: float, zoom: int) -> float:
    return (lon + 180.0) / 360.0 * TILE_SIZE * (2**zoom)


def lat_to_world(lat: float, zoom: int) -> float:
    lat = max(-85.05112878, min(85.05112878, lat))
    sine = math.sin(math.radians(lat))
    return (0.5 - math.log((1.0 + sine) / (1.0 - sine)) / (4.0 * math.pi)) * (
        TILE_SIZE * (2**zoom)
    )


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


def bounds(center_lat: float, center_lon: float, width: float, height: float):
    width_degrees = width / metres_per_degree_longitude(center_lat)
    height_degrees = height / metres_per_degree_latitude(center_lat)
    return (
        center_lon - width_degrees / 2.0,
        center_lat - height_degrees / 2.0,
        center_lon + width_degrees / 2.0,
        center_lat + height_degrees / 2.0,
    )


def tile_range(west: float, south: float, east: float, north: float, zoom: int):
    x_left = lon_to_world(west, zoom)
    x_right = lon_to_world(east, zoom)
    y_top = lat_to_world(north, zoom)
    y_bottom = lat_to_world(south, zoom)
    return (
        math.floor(x_left / TILE_SIZE),
        math.ceil(x_right / TILE_SIZE) - 1,
        math.floor(y_top / TILE_SIZE),
        math.ceil(y_bottom / TILE_SIZE) - 1,
    )


def copy_crop(source: Path, output: Path, center_lat: float, center_lon: float,
              width: float, height: float) -> None:
    west, south, east, north = bounds(center_lat, center_lon, width, height)
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
        metadata = dict(source_db.execute("SELECT name, value FROM metadata"))
        metadata.update(
            {
                "map_id": "home",
                "bounds": f"{west:.12f},{south:.12f},{east:.12f},{north:.12f}",
                "description": "HOME",
                "name": "home_terrain",
                "type": "overlay",
            }
        )
        output_db.executemany(
            "INSERT INTO metadata(name, value) VALUES (?, ?)", metadata.items()
        )

        min_zoom = int(metadata.get("minzoom", 0))
        max_zoom = int(metadata.get("maxzoom", 22))
        total = 0
        for zoom in range(min_zoom, max_zoom + 1):
            x_min, x_max, y_min, y_max = tile_range(
                west, south, east, north, zoom
            )
            tms_min = (1 << zoom) - 1 - y_max
            tms_max = (1 << zoom) - 1 - y_min
            rows = source_db.execute(
                """
                SELECT zoom_level, tile_column, tile_row, tile_data
                FROM tiles
                WHERE zoom_level = ?
                  AND tile_column BETWEEN ? AND ?
                  AND tile_row BETWEEN ? AND ?
                """,
                (zoom, x_min, x_max, tms_min, tms_max),
            ).fetchall()
            output_db.executemany("INSERT INTO tiles VALUES (?, ?, ?, ?)", rows)
            total += len(rows)
            print(
                f"ZOOM={zoom} RANGE={x_min}:{x_max},{y_min}:{y_max} "
                f"TILES={len(rows)}",
                flush=True,
            )
        output_db.commit()
        print(f"BOUNDS={metadata['bounds']}", flush=True)
        print(f"TILES={total}", flush=True)
    finally:
        source_db.close()
        output_db.close()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--center-lat", type=float, required=True)
    parser.add_argument("--center-lon", type=float, required=True)
    parser.add_argument("--width-metres", type=float, default=2000.0)
    parser.add_argument("--height-metres", type=float, default=2000.0)
    args = parser.parse_args()
    if args.width_metres <= 0 or args.height_metres <= 0:
        parser.error("map dimensions must be greater than zero")
    copy_crop(
        args.source,
        args.output,
        args.center_lat,
        args.center_lon,
        args.width_metres,
        args.height_metres,
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
