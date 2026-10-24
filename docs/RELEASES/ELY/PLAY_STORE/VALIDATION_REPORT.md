# ELY — Validación de release

Fecha: 24 de octubre de 2026

## Resultado

- `:watchfaceely:assembleRelease`: correcto.
- `:watchfaceely:bundleRelease`: correcto.
- `:watchfaceely:lintRelease`: correcto.
- Firma del AAB: verificada con `jarsigner`.
- WFF XML versión 2: válido con el validador oficial.
- AAB: `watchfaceely-release.aab`.
- SHA-256: `0005B8815797D90A42B132F59AAE610FF05C32B48EA99AF07395EAD1C1200992`.

## Validación WFF

El archivo `watchface.xml` supera la validación oficial para WFF versión 2.

## Validación de memoria

- Evaluador oficial: correcto.
- `maxActiveBytes`: `1.861.232` (~1,77 MiB).
- `maxAmbientBytes`: `1.861.232` (~1,77 MiB).
- Límites usados: 100 MB activo y 10 MB ambiente.
- Resultado: pasa ambos límites.

## Nota de firma

La clave de subida está en `watch/watchfaceely/keystore/` y sus propiedades en
`watch/watchfaceely/signing.properties`. Ambos elementos están excluidos de
Git y deben conservarse para futuras actualizaciones.
