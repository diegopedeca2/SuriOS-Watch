# Previous Version

Esta carpeta conserva la última versión funcional de cada elemento antes de
iniciar un Sprint que lo vaya a modificar.

Formato recomendado:

`<elemento>-v<version>/`

La copia se realiza una sola vez al inicio del Sprint. Debe incluir el código
fuente, los recursos y la configuración del elemento. Las carpetas generadas
de compilación, cachés y salidas temporales quedan fuera de la copia.

Primera copia realizada:

- `watchface-v1.2`: esfera oficial de Galaxy Watch Ultra.

Copias posteriores:

- `watchface-v1.3-sprint-038`: esfera oficial v1.3 conservada antes de iniciar
  el Sprint 038.
- `watchface-v1.4-sprint-039`: esfera oficial v1.4 conservada antes de iniciar
  el Sprint 039.
- `mobile-app-v3.4-sprint-040`: aplicación móvil v3.4 conservada antes de
  iniciar el Sprint 040.
- `bricktown-v1.1-sprint-040`: MBTiles de BRICKTOWN conservado antes de
  sustituirlo por la cartografía híbrida PNOA 2023.
- `bricktown-hybrid-pnoa2023-v3.5-correction`: copia exacta del MBTiles
  híbrido anterior, conservada antes de aplicar la corrección topográfica.
- `mobile-app-v3.5-sprint-040-final`: copia de la aplicación móvil v3.5 al
  cierre del Sprint 040, sin salidas de compilación ni cachés.
- `mobile-app-v3.5-sprint-047`: copia de la aplicación móvil v3.5 conservada
  antes de iniciar el nuevo Sprint móvil.

La cartografía híbrida anterior se conserva dentro de esa copia. La salida
actual corregida vuelve al estilo topográfico negro/verde y usa PNOA 2023 solo
como referencia para digitalizar estructuras visibles.
