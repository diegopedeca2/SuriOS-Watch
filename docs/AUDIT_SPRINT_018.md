# Auditoría técnica — Sprint 018

Fecha: 2026-08-30
Estado: CERRADO — skin NECRON validada y documentada
Proyecto: SuriOS Ecosystem / PIP-SuriOS

## Alcance

Esta auditoría cubre la skin NECRON para PIP-SuriOS Android, su integración
con la arquitectura compartida de skins, la aplicación visual a P.R.S. y la
validación en el Samsung A56.

Quedan fuera del alcance de Sprint 018 los cambios paralelos que permanecen en
el árbol de trabajo: el mapa offline HOME y su optimización de carga, los
metadatos `.idea`, `ORCA-TO-DO`, `PICTURES` y cualquier otro recurso no citado
en la especificación NECRON. Esos elementos no se incluyen en el commit de
este Sprint y requieren una revisión independiente.

## Resumen ejecutivo

La skin cumple el alcance visual aprobado: conserva navegación, datos,
persistencia, BLE, GPS, MAP, SONAR, RADS, DATA, STORAGE, P.R.S., estadísticas y
permisos, y sustituye únicamente tokens visuales, superficies y emblema. La
aplicación de NECRON a P.R.S. no produjo distorsión apreciable ni pérdida de
legibilidad en la revisión del A56, por lo que no se activa el fallback a
Brotherhood.

La selección continúa siendo de sesión. Tras reiniciar el proceso, la
aplicación vuelve a Brotherhood of Steel y el usuario puede elegir NECRON de
nuevo. Esto mantiene el comportamiento no persistente definido para el
prototipo.

## Cambios auditados

- `SkinId.NECRON` queda habilitada como segunda skin implementada.
- `SkinSession` mantiene la skin activa en el proceso sin crear una nueva
  bifurcación funcional.
- `SkinPalette` separa los tokens de Brotherhood y NECRON y mantiene nombres
  fuente compatibles (`PipGreen`, `PipAmber`, `PipRed`, etc.).
- `PIPSuriOSTheme` resuelve el esquema Material 3 según la skin activa.
- El emblema NECRON es un recurso vectorial nativo del proyecto; no se añade
  ninguna imagen externa a la APK.
- Home, MAP y las superficies P.R.S. usan tokens dinámicos para paneles,
  fondos, datos, estados y emblema.
- `SkinCatalogTest` y `SkinPaletteTest` cubren catálogo, activación, paleta y
  retorno a Brotherhood.
- La política de uso privado y de variante separada para publicación queda
  documentada en `docs/PROJECT_GUIDE/PROJECT_SCOPE_POLICY_v1.0.md`.

## Validación técnica

| Control | Resultado |
|---|---|
| `:app:testFullDebugUnitTest` | `BUILD SUCCESSFUL` |
| `:app:testPrsOnlyDebugUnitTest` | `BUILD SUCCESSFUL` |
| `:probeprotocol:test` | `BUILD SUCCESSFUL` |
| `:app:lintFullDebug` | `BUILD SUCCESSFUL` |
| `:app:lintPrsOnlyDebug` | `BUILD SUCCESSFUL` |
| `:app:assembleFullDebug` | `BUILD SUCCESSFUL` |
| `:app:assemblePrsOnlyDebug` | `BUILD SUCCESSFUL` |
| `:watchface:assembleDebug` | `BUILD SUCCESSFUL` |
| `:watchfacev2:assembleDebug` | `BUILD SUCCESSFUL` |
| `:probe:assembleDebug` | `BUILD SUCCESSFUL` |
| `git diff --check` y `git diff --cached --check` | Sin errores de whitespace |

Las suites específicas de skin registraron 4 tests, 0 fallos y 0 errores:
`SkinCatalogTest` (2) y `SkinPaletteTest` (2).

## Validación en dispositivo

- Samsung A56, modelo `SM_A566B`, serial ADB `RZGYC07H0EX`.
- Paquete `com.suri.pipsurios`, `versionName=2.4`, `versionCode=4`.
- `app-full-debug.apk` instalada con `Success`.
- `MainActivity` arrancada y comprobada en orientación horizontal.
- NECRON revisada en Home, menú P.R.S. y `LOCAL SCAN`.
- Se comprobaron texto cian/teal, paneles oscuros, acentos de estado,
  cuadrícula de densidad, lista de contactos y ruta de vuelta.
- Evidencias locales: `output/necron_a56_reconnected.png`,
  `output/necron_a56_prs_tracking.png` y
  `output/necron_a56_prs_menu.png`.

El intento de abrir `SCAN + PROBE` derivó a los ajustes del sistema del A56
por el flujo de preparación/permisos. No se interpreta como una distorsión de
la skin ni como un fallo de renderizado P.R.S.; el tratamiento visual es común
al contenido P.R.S. validado.

## Seguridad, propiedad intelectual y distribución

- No se detectaron secretos, claves privadas ni tokens de acceso en los
  cambios de Sprint 018.
- NECRON utiliza nombres y referencias de una propiedad intelectual externa.
  Se mantiene como prototipo privado.
- No se incorporan assets externos a la aplicación; el emblema entregado es
  vectorial y nativo del repositorio.
- Las carpetas `PICTURES` y `ORCA-TO-DO` contienen material de referencia y
  no se suben como parte de este Sprint.
- Cualquier futura publicación exigirá una variante independiente, con nombre,
  símbolos y recursos originales de SuriOS y revisión documental propia.

## Riesgos y deuda aceptada

1. La selección no se persiste; queda aceptada para este prototipo y se
   reevalúa si el propietario solicita una preferencia permanente.
2. La aceptación física en condiciones de uso, reflejos, brillo bajo y
   ahorro de batería no equivale a la revisión visual realizada en el A56 y
   queda como validación posterior.
3. Los cambios de mapas y el asset MBTiles de aproximadamente 75 MB requieren
   Sprint propio, revisión de licencia/origen y decisión sobre Git LFS antes
   de publicarse.
4. El historial contiene riesgos P.R.S. ya documentados en auditorías
   anteriores, incluido el transporte local sin autenticación; NECRON no los
   modifica ni los resuelve.

## Dictamen

Sprint 018 queda aprobado para el alcance de la skin NECRON y cerrado técnica
y documentalmente. El commit de cierre registra únicamente esta auditoría,
la especificación de skin, la política de privacidad documental y las
actualizaciones operativas del Sprint. Los cambios paralelos permanecen
intactos y sin incluir.
