# Sprint 010 — RADS v2 y STORAGE

## Estado

- Apertura: 2026-08-12.
- Cierre: 2026-08-12.
- Estado: cerrado.
- Versión consolidada: PIP-SuriOS v2.0.
- Commit técnico: `a254c7b78eb7359fc6f67e391d73a146d1d44842`.
- Sprint activo posterior: ninguno.
- Sprint 011: no iniciado.

## Objetivo

Evolucionar RADS con un segundo modo inmersivo controlado mediante inclinación y sustituir el antiguo catálogo CONSUMABLES por un inventario STORAGE persistente, conectado dinámicamente al historial de operaciones.

## RADS v2

RADS conserva el funcionamiento base, los sonidos y la animación analógica. Mantener pulsado VOLUME UP eleva progresivamente el nivel y soltarlo inicia el retorno gradual. La escala presenta LOW, HIGH y CRITICAL y la velocidad de subida queda centralizada para permitir un control más preciso.

VOLUME DOWN alterna entre `RADS` y `RADS.` sin menús adicionales. En `RADS.` el Rotation Vector controla la lectura: 45° actúan como referencia baja y la aproximación a la horizontal eleva el nivel hacia CRITICAL. El sensor solo permanece registrado durante este modo y se libera al abandonarlo.

La aguja y la cadencia de clics utilizan el mismo nivel efectivo suavizado. La variación aleatoria del sonido se conserva sin separar su evolución del movimiento visible.

## Arquitectura de STORAGE

STORAGE se integra en INVENTORY y conserva los grupos BBs, GRENADES y GAS. Cada artículo utiliza un identificador persistente estable y único.

La arquitectura separa:

- `StorageCatalog`: catálogo y stable IDs.
- `StorageLedgerEntry`: PURCHASE, USED y cache derivada TOTAL.
- `StorageRepository`: lectura, escritura atómica, operaciones manuales y reconciliación.
- `StorageCalculator`: cálculo de CONSUMED y TOTAL.
- `StorageScreen`: presentación de resultados y acciones de usuario.
- `OperationRepository`: fuente exclusiva del consumo histórico.

El ledger se almacena en `filesDir/data/storage/ledger.json`. PURCHASE registra compras manuales y USED registra retiradas fuera de una operación. CONSUMED nunca se almacena: se calcula desde los LOG persistentes. TOTAL se calcula siempre mediante:

`TOTAL = PURCHASE - USED - CONSUMED`

TOTAL se conserva como cache persistente, pero el valor derivado siempre prevalece. Al abrir STORAGE o cambiar el historial mediante creación, edición o eliminación de un LOG, `reconcile()` recalcula y corrige TOTAL sin modificar PURCHASE, USED ni los LOG.

## Consumo automático y límites actuales

El descuento automático solo se aplica cuando existe correspondencia inequívoca con DATA: `9mm GRENADES` y `CO2 GRENADES`. Los artículos BBs y los formatos individuales de GAS mantienen PURCHASE y USED, pero CONSUMED permanece en cero hasta que INPUT OPERATION pueda identificar sus subtipos. `40mm GRENADES` continúa en DATA y los LOG históricos, pero no forma parte de STORAGE.

TOTAL puede ser negativo si el consumo histórico supera las compras registradas. El sistema muestra y persiste el resultado real sin alterar los datos para ocultarlo.

## Compatibilidad y persistencia

Los ledgers provisionales anteriores con INITIAL/ADD/REMOVE siguen siendo legibles y se interpretan con la semántica PURCHASE/USED. Los archivos sin TOTAL se cargan conservando sus datos manuales; TOTAL se calcula y persiste durante la reconciliación.

Durante la validación se detectó que el decoder no reconstruía en Android el JSON emitido por el propio repositorio. Esto provocaba que cada pulsación partiera de un ledger vacío aunque Compose mostrase temporalmente el incremento. La corrección final implementó un round-trip robusto para JSON compacto y multilínea, preservación de múltiples entradas y escritura completa mediante archivo temporal y sustitución atómica.

## Validación

Se superaron:

- `:app:assembleDebug`.
- compilación incremental.
- `:app:lintAnalyzeDebug`.
- 79 pruebas unitarias sin fallos.
- `git diff --check`.
- validación manual en Samsung Galaxy A56.
- validación manual aplicable en Pixel 8 Emulator.

Las pruebas físicas confirmaron que PURCHASE, USED y TOTAL sobreviven a salida/reentrada, cierre completo y reapertura; que varios artículos permanecen simultáneamente; y que CONSUMED y TOTAL se recalculan sin perder los valores manuales.

## Cierre

Sprint 010 queda cerrado y PIP-SuriOS v2.0 consolidado. No existe ningún Sprint activo y Sprint 011 no se ha iniciado.
