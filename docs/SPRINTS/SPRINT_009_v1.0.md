# Sprint 009 v1.0 — PIP-SuriOS v1.9

## Estado

- **Resultado:** completado y cerrado
- **Fecha de cierre:** 2026-08-12
- **Commit técnico:** `c9dc3720213d5fb4d5e77ba64ea163802548c5ba`
- **Versión consolidada:** PIP-SuriOS v1.9

No existe ningún Sprint activo. Sprint 010 no se ha iniciado.

## Objetivo

Convertir DATA en un historial operativo permanente y consultable, completar su edición, eliminación y análisis estadístico, e integrar UNIFORM en el loadout de sesión y en el snapshot histórico. El Sprint también consolidó varios refinamientos visuales y operativos autorizados.

## Arquitectura DATA

- `OperationDraft` mantiene aislado el borrador de creación.
- `OperationEditDraft` mantiene aislada la edición hasta CONFIRM MODIFICATIONS.
- `OperationLog` representa el snapshot histórico persistente.
- `OperationRepository` centraliza enumeración, lectura, creación, actualización segura y eliminación; la UI no accede directamente a archivos.
- `OperationJsonCodec` serializa y deserializa el modelo manteniendo compatibilidad hacia atrás.
- Los archivos se almacenan en `filesDir/data/operations` con nombre `AAAAMMDD.json`.

El esquema admite un único LOG por fecha. La creación detecta colisiones. En una edición con cambio de fecha, primero se comprueba el destino, después se escribe el nuevo archivo y solo tras el éxito se elimina el original. Un conflicto o fallo conserva el LOG anterior.

## Flujo funcional

INPUT OPERATION reúne DATE & LOCATION, snapshot LOADOUT, CONSUMABLES y CONFIRM DATA. LOG presenta el historial en orden descendente y LOG DETAIL muestra todos los datos persistidos. EDIT precarga el registro sin contaminar CURRENT GEAR y solo escribe en CONFIRM MODIFICATIONS. DELETE exige confirmación y elimina exclusivamente el registro seleccionado.

El snapshot incluye PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES, HEADGEAR, FRONT PANEL y UNIFORM, además de los consumibles. Los datos históricos son independientes del CURRENT GEAR actual.

## Compatibilidad histórica

Los JSON anteriores a UNIFORM siguen siendo legibles. La ausencia del campo se interpreta como NOT CONFIGURED sin reescribir ni migrar el archivo durante la lectura. Los valores históricos anteriores de HEADGEAR se preservan, pero solo `SURI-14` y `BROTHERHOOD` se consideran perfiles válidos para las estadísticas actuales.

## Statistics

`StatisticsCalculator` contiene el cálculo porcentual reutilizable con `BigDecimal`, `RoundingMode.HALF_UP`, máximo dos decimales y coma decimal en presentación. Las estadísticas se recalculan dinámicamente desde los LOG existentes; no se persisten.

Categorías finalizadas:

- PRIMARY WEAPON
- SECONDARY WEAPON
- LOCATION, dinámica, normalizada por trim y mayúsculas/minúsculas, y ordenada alfabéticamente
- HEADGEAR, limitada a los perfiles SURI-14 y BROTHERHOOD
- UNIFORM, con MCBCK - SUMMER, MCBCK - LONG y DESERT

Los registros sin valor válido no entran en el denominador. Las categorías fijas muestran opciones al 0%; si no existe ningún valor válido aparece NO DATA. DELETE y EDIT quedan reflejados al volver a calcular.

## UNIFORM y Loadout Activo

`LoadoutConfiguration` incorpora UNIFORM de forma inmutable. CURRENT GEAR ofrece sus tres opciones y APPLY publica el borrador en el Loadout Activo. El botón APPLY invierte sus colores cuando borrador y activo son equivalentes. STATUS, INPUT OPERATION, LOG DETAIL y EDIT muestran o modifican UNIFORM dentro de sus responsabilidades, sin persistencia propia de CURRENT GEAR.

## Otros refinamientos

- Secuencia de arranque acumulativa en dos fases por módulo.
- Denominación visible RADS sin cambios en su motor.
- SONAR reorganizado con paneles CONTACTS y SCAN, totales CURRENT/NEW y categorías de proximidad.
- DON'T FORGET deduplicado y ordenado alfabéticamente conservando el estado del checklist.
- Todas las firmas visibles de la aplicación muestran `PIP-SuriOS v1.9`.

## Validación y pruebas

Se superaron `:app:assembleDebug`, la compilación incremental, `:app:lintAnalyzeDebug`, la suite completa de 63 pruebas unitarias y `git diff --check`. La validación funcional y visual se realizó en Samsung Galaxy A56 y Pixel 8 Emulator, incluyendo DATA, estadísticas, compatibilidad histórica, CURRENT GEAR, STATUS, arranque, RADS, SONAR y DON'T FORGET.

## Limitaciones actuales

- Existe un único LOG por fecha.
- CURRENT GEAR y el Loadout Activo siguen siendo temporales durante la sesión.
- No hay recuperación de LOG eliminados, exportación, filtros ni gráficos estadísticos.
- LOCATION no realiza corrección ortográfica ni agrupación difusa.
- SONAR ofrece proximidad aproximada por RSSI; no mide distancia ni dirección física.

Sprint 009 queda cerrado y PIP-SuriOS v1.9 consolidado. No existe ningún Sprint activo y Sprint 010 no se ha iniciado.
