# SuriOS Watch — Armor — Validación de release

Fecha: 24 de octubre de 2026

## Resultado

- `:watchface:assembleDebug`: correcto.
- `:watchface:lintDebug`: correcto.
- `:watchface:bundleRelease`: correcto.
- `:watchface:lintRelease`: correcto.
- Firma del AAB: verificada durante el proceso `signReleaseBundle`.
- WFF XML versión 2: válido con el validador oficial.
- Preview: sustituida la plantilla por una captura real limpia de la armadura.
- AAB: `watchface-release.aab`.
- SHA-256 actual: `BB35315830BE0E09E57AA3A3F70197C918836B413CA73990885794A3502E789D`.

## Validación de memoria

- Evaluador oficial: correcto.
- `maxActiveBytes`: `1.698.439` (~1,62 MiB).
- `maxAmbientBytes`: `1.621.271` (~1,55 MiB).
- Límites usados: 100 MB activo y 10 MB ambiente.
- Resultado: pasa ambos límites.

## Nota de firma

La clave de subida está en `watch/watchface/keystore/` y sus propiedades en
`watch/watchface/signing.properties`. Ambos elementos están excluidos de Git y
deben conservarse para futuras actualizaciones.
