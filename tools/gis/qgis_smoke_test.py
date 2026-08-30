"""Minimal PyQGIS communication test used by the Orca integration."""

from __future__ import annotations

import argparse
import os
import sys
from pathlib import Path


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True, help="Destination .qgz path")
    args = parser.parse_args()

    output = Path(args.output).resolve()
    if output.exists():
        raise SystemExit(f"El archivo de prueba ya existe; no se sobrescribe: {output}")
    output.parent.mkdir(parents=True, exist_ok=True)

    prefix = os.environ.get("QGIS_PREFIX_PATH")
    if not prefix:
        raise SystemExit("QGIS_PREFIX_PATH no está definido. Ejecuta este script con python-qgis-ltr.bat.")

    plugin_path = Path(prefix) / "python" / "plugins"
    if str(plugin_path) not in sys.path:
        sys.path.insert(0, str(plugin_path))

    from qgis.core import (
        Qgis,
        QgsApplication,
        QgsCoordinateReferenceSystem,
        QgsCoordinateTransformContext,
        QgsFeature,
        QgsGeometry,
        QgsPointXY,
        QgsProject,
        QgsVectorFileWriter,
        QgsVectorLayer,
    )

    app = QgsApplication([], False)
    app.setPrefixPath(prefix, True)
    app.initQgis()
    print("PYQGIS_INIT=OK", flush=True)

    try:
        try:
            import processing  # noqa: F401

            processing_status = "OK"
            print("PROCESSING_IMPORT=OK", flush=True)
        except Exception as error:  # pragma: no cover - diagnostic path
            processing_status = f"ERROR: {error}"

        project = QgsProject()
        project.setCrs(QgsCoordinateReferenceSystem("EPSG:3857"))
        project.writeEntry("SuriOS", "orca_smoke_test", "created")

        layer = QgsVectorLayer(
            "Point?crs=EPSG:3857&field=name:string(32)",
            "orca_smoke",
            "memory",
        )
        if not layer.isValid():
            raise RuntimeError("No se pudo crear la capa de prueba.")

        feature = QgsFeature(layer.fields())
        feature.setGeometry(QgsGeometry.fromPointXY(QgsPointXY(0, 0)))
        feature.setAttribute("name", "orca")
        if not layer.dataProvider().addFeature(feature):
            raise RuntimeError("No se pudo añadir la entidad de prueba.")
        layer.updateExtents()

        gpkg = output.with_suffix(".gpkg")
        if gpkg.exists():
            raise RuntimeError(f"El GeoPackage de prueba ya existe; no se sobrescribe: {gpkg}")
        options = QgsVectorFileWriter.SaveVectorOptions()
        options.driverName = "GPKG"
        options.layerName = "orca_smoke"
        writer_result, _new_filename, _new_layer, writer_error = QgsVectorFileWriter.writeAsVectorFormatV3(
            layer,
            str(gpkg),
            QgsCoordinateTransformContext(),
            options,
        )
        if writer_result != QgsVectorFileWriter.NoError:
            raise RuntimeError(f"No se pudo escribir el GeoPackage: {writer_error}")

        persisted = QgsVectorLayer(f"{gpkg}|layername=orca_smoke", "orca_smoke", "ogr")
        if not persisted.isValid():
            raise RuntimeError("No se pudo volver a abrir la capa persistente.")
        project.addMapLayer(persisted)
        print("GPKG_WRITE=OK", flush=True)

        if not project.write(str(output)):
            raise RuntimeError(f"QGIS no pudo escribir el proyecto: {output}")
        print("PROJECT_WRITE=OK", flush=True)

        project.writeEntry("SuriOS", "orca_smoke_test", "modified")
        if not project.write(str(output)):
            raise RuntimeError(f"QGIS no pudo modificar el proyecto: {output}")

        check = QgsProject()
        if not check.read(str(output)):
            raise RuntimeError(f"QGIS no pudo volver a leer el proyecto: {output}")
        print("PROJECT_READ=OK", flush=True)
        loaded = check.mapLayersByName("orca_smoke")
        if len(loaded) != 1:
            raise RuntimeError("La capa de prueba no se conservó al reabrir el proyecto.")
        feature_count = loaded[0].featureCount()
        if feature_count != 1:
            raise RuntimeError(f"Se esperaban 1 entidad y se encontraron {feature_count}.")
        property_value, property_ok = check.readEntry("SuriOS", "orca_smoke_test", "")
        if not property_ok:
            raise RuntimeError("No se encontró la entrada de prueba del proyecto.")
        if property_value != "modified":
            raise RuntimeError("La modificación de propiedad del proyecto no se conservó.")
        print("PROJECT_VERIFY=OK", flush=True)

        print(f"QGIS_VERSION={Qgis.QGIS_VERSION}", flush=True)
        print(f"QGIS_PREFIX={prefix}", flush=True)
        print(f"PROCESSING_IMPORT={processing_status}", flush=True)
        print(f"PROJECT_PATH={output}", flush=True)
        print(f"GPKG_PATH={gpkg}", flush=True)
        print(f"PROJECT_CRS={check.crs().authid()}", flush=True)
        print("LAYER=orca_smoke", flush=True)
        print(f"FEATURE_COUNT={feature_count}", flush=True)
        print(f"PROJECT_PROPERTY={property_value}", flush=True)
        os._exit(0)
    finally:
        # QGIS 3.44 may crash while tearing down a headless GPKG provider on Windows.
        # Process termination releases this short-lived smoke-test context safely.
        pass


if __name__ == "__main__":
    raise SystemExit(main())
