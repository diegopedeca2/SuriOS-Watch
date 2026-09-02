"""Copy contour geometry while assigning the CRS used by the source DEM."""

from __future__ import annotations

import argparse
from pathlib import Path


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--layer", default="contours_2m")
    parser.add_argument("--geometry-crs", default="EPSG:25830")
    parser.add_argument("--output-crs", default="EPSG:25830")
    args = parser.parse_args()

    from qgis.core import (
        QgsApplication,
        QgsCoordinateReferenceSystem,
        QgsCoordinateTransform,
        QgsCoordinateTransformContext,
        QgsFeature,
        QgsVectorFileWriter,
        QgsVectorLayer,
    )

    prefix = __import__("os").environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise RuntimeError("QGIS_PREFIX_PATH is not defined")

    qgis = QgsApplication([], False)
    qgis.setPrefixPath(prefix, True)
    qgis.initQgis()
    try:
        source = QgsVectorLayer(
            f"{args.source}|layername={args.layer}", args.layer, "ogr"
        )
        if not source.isValid():
            raise RuntimeError(f"Could not load {args.layer} from {args.source}")

        output_crs = QgsCoordinateReferenceSystem(args.output_crs)
        geometry_crs = QgsCoordinateReferenceSystem(args.geometry_crs)
        transform = (
            QgsCoordinateTransform(
                geometry_crs,
                output_crs,
                QgsCoordinateTransformContext(),
            )
            if geometry_crs != output_crs
            else None
        )
        target = QgsVectorLayer(
            f"LineString?crs={args.output_crs}&field=ID:integer&field=ELEV:double",
            args.layer,
            "memory",
        )
        provider = target.dataProvider()
        for feature in source.getFeatures():
            copied = QgsFeature(target.fields())
            geometry = feature.geometry()
            if transform is not None:
                geometry.transform(transform)
            copied.setGeometry(geometry)
            copied.setAttribute("ID", feature.attribute("ID"))
            copied.setAttribute("ELEV", feature.attribute("ELEV"))
            provider.addFeature(copied)
        target.updateExtents()

        args.output.parent.mkdir(parents=True, exist_ok=True)
        options = QgsVectorFileWriter.SaveVectorOptions()
        options.driverName = "GPKG"
        options.layerName = args.layer
        options.actionOnExistingFile = QgsVectorFileWriter.CreateOrOverwriteFile
        result = QgsVectorFileWriter.writeAsVectorFormatV3(
            target,
            str(args.output),
            QgsCoordinateTransformContext(),
            options,
        )
        if result[0] != QgsVectorFileWriter.NoError:
            raise RuntimeError(f"Could not write normalized contours: {result}")
        print(f"FEATURES={target.featureCount()}", flush=True)
        print(f"CRS={target.crs().authid()}", flush=True)
        print(f"OUTPUT={args.output}", flush=True)
    finally:
        qgis.exitQgis()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
