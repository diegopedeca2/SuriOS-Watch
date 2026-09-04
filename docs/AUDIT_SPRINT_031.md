# Auditoría externa — Sprint 031

---
document: AUDIT
project: SuriOS Ecosystem / PIP-SuriOS
sprint: 031
date: 2026-09-04
auditor: Revisión externa asistida
status: Cerrada con AUD-031-01 pausada
---

## Resultado ejecutivo

La auditoría no encuentra fallos de compilación ni de `lint` en la variante
principal. Sí confirma una incidencia de reproducibilidad y varias mejoras de
mantenimiento. El propietario decide mantener la incidencia de recursos en
pausa durante esta Alpha controlada: los testers recibirán las APK actuales y
deben informar si el mapa o el icono no se carga correctamente.

La diferencia entre SENTRY y TRACKER se mantiene por decisión de diseño. La
duplicidad interna no se tratará ahora mientras ambas experiencias conserven
sus diferencias.

PROBE se elimina de las APK de ALTAMIRA, CHECHU y FENRIR porque esos testers no
disponen de reloj o baliza externa. El soporte permanece únicamente en MAIN.

## Alcance y método

Se revisaron el historial y el estado Git, la configuración Gradle, los módulos
Android/Wear OS, recursos, mapas, lógica P.R.S., servicios del Data Layer,
pruebas, documentación y herramientas auxiliares.

Comprobaciones realizadas:

- inspección de `git status`, `git ls-files` y de los archivos ignorados;
- comparación de una copia limpia creada con `git archive`;
- búsqueda de duplicidades de nombres, contenido y lógica repetida;
- lectura de los puntos de entrada de TRACKER, PROBE y Data Layer;
- `.\gradlew.bat test lint -PdistributionProfile=MAIN`;
- revisión de manifiestos, reglas de copia de seguridad y posibles secretos.

La copia limpia de `HEAD` contiene el mapa principal en
`app/src/main/assets`, pero no contiene los directorios generados de perfiles
`app/build/generated/distributionAssets/*` ni
`app/build/generated/distributionRes/*`. En el entorno local sí existen, por
ejemplo, los mapas y los iconos de ALTAMIRA, CHECHU y FENRIR. Esa diferencia es
la evidencia principal de la falta de reproducibilidad.

## Incidencias

| ID | Nivel | Estado | Evidencia | Riesgo futuro |
|---|---|---|---|---|
| AUD-031-01 | Alto | Pausada; feedback Alpha | `app/build.gradle.kts:6-21,76-83` usa `build/generated/...` para perfiles distintos de MAIN; `.gitignore` ignora `**/build/` | En otra instalación pueden faltar mapas o iconos. El tester debe reportarlo; no se corrige todavía |
| AUD-031-02 | Medio | Aceptada por ahora | `PrsTrackingScreen.kt:91-198` y `IndividualTrackingScreens.kt:152-232` tienen lógica repetida, pero SENTRY y TRACKER conservan diferencias intencionadas | Se mantiene el comportamiento actual y se espera feedback antes de unificar |
| AUD-031-03 | Medio | Fuera de APK tester; reservado para MAIN | Servicios y protocolo PROBE en `app` y `watch/probe` | No se expone PROBE en las APK de testers. El riesgo técnico sigue documentado para MAIN |
| AUD-031-04 | Medio | Fuera de APK tester; reservado para MAIN | `ProbeTelemetryStore.kt:11-29,91-97` y `ProbeLink.kt:26-37` | No afecta al flujo de testers porque sus APK no activan PROBE |
| AUD-031-05 | Bajo | Resuelta | Las firmas visibles leen ahora `BuildConfig.VERSION_NAME` desde `AppVersion.kt` | Una subida de versión actualiza la firma común |
| AUD-031-06 | Bajo | Omitida por decisión | Copias idénticas de recursos entre módulos | No se prioriza la estética ni la limpieza de recursos en esta Alpha |
| AUD-031-07 | Bajo/medio | Omitida por decisión | Cobertura instrumentada limitada | Se conserva la metodología manual actual y se espera feedback real de testers |
| AUD-031-08 | Bajo/medio | Resuelta para Alpha | `tools/create_alpha_test_guide.py` acepta raíz, Sprint, fecha, versión y salida; `tools/package_alpha_testers.ps1` parametriza el empaquetado | La entrega ya se puede repetir con rutas distintas; los datos GIS históricos siguen siendo locales |

## Duplicidades detectadas

La duplicidad más importante es funcional, no solo de archivos: las dos rutas
de TRACKER crean sus propios objetos de captura y traducen por separado las
muestras de `ProbeProtocol.BleSample` a `BleObservation`. También repiten el
`DisposableEffect`, el `LaunchedEffect` y el intervalo de evaluación. Se deja
registrada como deuda técnica, pero se acepta ahora para conservar las
diferencias de SENTRY y TRACKER y observar el comportamiento real en Alpha.

Se encontraron además copias idénticas del emblema en cuatro módulos y copias
idénticas de reglas de extracción de datos en dos módulos. No se consideran un
fallo inmediato, pero sí una fuente de divergencia.

No se encontraron archivos Kotlin con el mismo nombre dentro de los grupos
revisados ni secretos privados evidentes en el código fuente.

## Controles que sí funcionan

- `test` y `lint` terminaron correctamente para MAIN en esta auditoría.
- El informe de `lint` no contiene incidencias reportadas.
- El protocolo PROBE usa tipos definidos y valida la forma básica de los
  mensajes antes de publicarlos.
- `allowBackup=false` y las reglas de copia excluyen los dominios locales de
  datos.
- En el Sprint 030 se validaron las cuatro APK v3.0 y su instalación en el A56
  `RZGYC07H0EX`.
- En este Sprint se han compilado FENRIR, ALTAMIRA y CHECHU con `PROBE_ENABLED`
  desactivado y se han generado sus paquetes Alpha individuales.

Estos controles reducen el riesgo inmediato, pero no corrigen la
reproducibilidad ni las deudas de diseño indicadas arriba.

## Acciones aprobadas en este Sprint

1. Mantener `AUD-031-01` pausada y explicarla en la guía Alpha como posible
   incidencia conocida.
2. Mantener intactas las diferencias de SENTRY y TRACKER.
3. Desactivar PROBE en las distribuciones FENRIR, ALTAMIRA y CHECHU.
4. Leer la versión visible desde `BuildConfig` mediante `AppVersion.kt`.
5. Omitir por ahora la centralización estética y las nuevas pruebas automáticas.
6. Hacer portátiles los scripts y crear un empaquetado verificable para cada
   tester.

## Decisión de empaquetado

La condición inicial de “si no salen incidencias” queda reinterpretada por
decisión del propietario: la Alpha es controlada y la incidencia de recursos
se entrega como riesgo conocido para obtener feedback. PROBE queda fuera de las
APK de testers.

La guía Alpha se ha actualizado, se han compilado FENRIR, ALTAMIRA y CHECHU y
se ha generado un ZIP independiente para cada tester:

- `output/SPRINT_031_APK/PIP-SuriOS_FENRIR_SPRINT_031.zip`
- `output/SPRINT_031_APK/PIP-SuriOS_ALTAMIRA_SPRINT_031.zip`
- `output/SPRINT_031_APK/PIP-SuriOS_CHECHU_SPRINT_031.zip`

## Validación física y resultado de cierre — 2026-09-04

La validación se realizó en un Samsung A56, modelo `SM_A566B`, con
identificador ADB `RZGYC07H0EX`.

Se instalaron y abrieron las tres APK tester v3.0: FENRIR, ALTAMIRA y CHECHU.
En las tres se confirmó la identidad del perfil, la navegación principal y el
funcionamiento de P.R.S. SENTRY y TRACKER. Se concedieron los permisos de
Bluetooth que solicitaron ALTAMIRA y CHECHU. No se observaron cierres de la
aplicación durante estas pruebas.

Resultados concretos:

- SENTRY funcionó con el A56 como fuente de vigilancia y mostró lecturas de
  nodos.
- TRACKER mantuvo el modo `ONLY PIP-BOY`, inició la lectura automáticamente al
  entrar en el objetivo y volvió al paso anterior mediante `BACK`.
- No apareció ninguna opción PROBE en las APK tester.
- El mapa `TESTING` de FENRIR mostró cartografía base.
- ALTAMIRA y CHECHU cargaron correctamente el campo y la cuadrícula de
  `TESTING`, pero la cartografía base se mostró vacía.

La inspección de las APK confirmó que el archivo de mapa está incluido en las
tres. Por tanto, el resultado de ALTAMIRA y CHECHU se registra como
`AUD-031-01`: recurso incluido, pero no visualizado correctamente en esas
pruebas. La incidencia permanece pausada para que los testers la confirmen o
descarten con sus dispositivos.

Con esta validación, la auditoría y el Sprint 031 quedan cerrados. El siguiente
Sprint activo es el 032.
