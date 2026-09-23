# PIP-SuriOS - Organization overlay workflow

This is the repeatable workflow for importing a new game organizer map. The
organizer PDF/image is a reference layer only; the PIP-SuriOS MBTiles remain
the visual map base.

## 1. Prepare the QGIS project

1. Open the PIP-SuriOS terrain MBTiles for the field.
2. Set the project CRS to the CRS used by the terrain layer. For the current
   Airsoft Total map this is EPSG:3857.
3. Add the organizer PDF/image as a temporary raster layer.
4. Use QGIS Georeferencer and add at least four recognizable control points:
   buildings, road corners, paths, or field-boundary corners.
5. Check the result against the terrain. When the image and terrain disagree,
   keep the real terrain coordinates and flag the uncertain feature for manual
   review.

## 2. Reconstruct vector layers

Create a GeoPackage with these independent layers:

- `GRID`: polygon cells and a stable `cell_id` such as `F-6`.
- `FIELD_BOUNDARY`: field perimeter.
- `RESPAWNS`: organizer respawn points.
- `BASES`: organizer base points.
- `POI`: named points of interest.
- `INTERNAL_PATHS`: relevant internal paths only.

Every point in `RESPAWNS`, `BASES` and `POI` must have at least:

`id`, `type`, `name`, `latitude`, `longitude`

Initial `type` values are `RESPAWN`, `BASE`, `POI`, `AMMO`, `ENTRANCE`,
`SAFE_ZONE` and `PARKING`. New values can be added without changing the
navigation engine.

## 3. Export for Android

Export the reviewed vector data into the line-oriented asset format consumed
by `OrganizationOverlayCodec`:

`app/src/main/assets/maps/<map_id>_organization.overlay`

The asset must contain a `SOURCE_STATUS` record. Use
`PROVISIONAL_QGIS_REVIEW` until all points have been checked in QGIS; use a
reviewed status only after the owner confirms the alignment.

For `BOUNDARY` and `PATH` records, write each coordinate as
`latitude,longitude`, matching `GeoPoint`. The `GRID` record keeps its extent
order as `west|south|east|north`.

When the organizer grid is not perfectly uniform, add one `CELL` record per
cell in the form `CELL|id|west|south|east|north`. Android uses those real cell
boundaries for GRID lookup and rendering; without `CELL` records it derives a
regular grid from the `GRID` extent.

The export can be repeated with the bundled PyQGIS script after the GeoPackage
has been checked in QGIS:

```powershell
. .\tools\gis\qgis_env.ps1
Invoke-SuriQgisPython .\tools\gis\export_organization_overlay.py `
  --gpkg C:\ruta\campo.gpkg `
  --output .\app\src\main\assets\maps\airsoft_total_organization.overlay `
  --map-id airsoft_total
```

For a new field, render the MBTiles directly from its QGIS GeoPackage. For
AIRSOFT TOTAL the reproducible command is:

```powershell
. .\tools\gis\qgis_env.ps1
Invoke-SuriQgisPython .\tools\gis\build_navy7_home_style.py `
  --gpkg C:\Users\diego\Desktop\GQUIS\airsoft_total\airsoft_total_source.gpkg `
  --project-output .\tmp\gis\Airsoft_Total_HOME_STYLE_4KM_FROM_ZERO.qgz `
  --output .\tmp\gis\airsoft_total_terrain_home_style_4km_from_zero.mbtiles `
  --center-lat 40.81068714827759 `
  --center-lon -4.269950411691506 `
  --map-id airsoft_total `
  --map-name "AIRSOFT TOTAL" `
  --metadata-name "AIRSOFT TOTAL" `
  --width-metres 4000 `
  --height-metres 4000 `
  --palette day `
  --force
```

This produces the light HOME/NAVY7/OFFICE palette directly from the source
GeoPackage, with no dependency on a previous MBTiles raster. It uses a cream
background, grey buildings, blue roads, green contour lines and purple index
contours. The old recolour commands remain available only for legacy maps
that cannot yet be rendered from vector data:

```powershell
. .\tools\gis\qgis_env.ps1
Invoke-SuriQgisPython .\tools\gis\recolor_mbtiles_pip_tracker.py `
  --source .\tmp\gis\airsoft_total_terrain_daylight_backup.mbtiles `
  --output .\tmp\gis\airsoft_total_terrain_pip_tracker.mbtiles `
  --boundary-overlay .\app\src\main\assets\maps\airsoft_total_organization.overlay
```

Review the output visually before replacing the Android asset and update its
SHA-256 in `TerrainModels.kt`.

The script reads `GRID`, `FIELD_BOUNDARY`, `INTERNAL_PATHS` and the optional
POI layers, transforms them to WGS84, and writes the same format used by the
Android app.

## 4. Quality checks

- Confirm every POI is inside the intended field bounds.
- Confirm GRID row/column order and IDs against the organizer map.
- Confirm the overlay does not replace the terrain MBTiles.
- Confirm navigation uses only point coordinates, never paths.
- Run JVM tests, Android build and lint before field testing.

The current Airsoft Total asset is the first provisional case. It must be
refined with real QGIS control points before relying on it for field decisions.
