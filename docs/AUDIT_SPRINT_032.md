# Auditoría externa — Sprint 032 / PIP-SuriOS v3.1

---
document: AUDIT
project: SuriOS Ecosystem / PIP-SuriOS
sprint: 032
version: 3.1
date: 2026-09-04
auditor: Revisión externa asistida
status: Aprobada para cierre; sin incidencias críticas ni bloqueantes
---

## Resultado ejecutivo

La revisión completa no encuentra incidencias graves nuevas que impidan cerrar
el Sprint 032. La versión pasa de `3.0` a `3.1` y el `versionCode` de Android de
`10` a `11`.

Las modificaciones de RADS y TRACKER han sido validadas físicamente por el
propietario. `test`, `lint` y la compilación de MAIN terminan correctamente. La
APK MAIN v3.1 contiene los tres audios nuevos, no contiene el audio retirado y
declara `versionName=3.1` y `versionCode=11`.

El A56 estaba desconectado al intentar reinstalar la APK después del incremento
de versión. Esto deja pendiente únicamente esa comprobación administrativa; no
invalida la validación física de las modificaciones funcionales ya comunicada.

## Alcance y comprobaciones

- revisión de estado Git, rama, historial y remoto;
- inspección de la configuración Gradle y de todas las referencias activas de
  versión;
- revisión de RADS, ciclo de vida de audio y selección de capas;
- revisión de TRACKER, niebla de probabilidad, zoom y conservación del GRID en
  SENTRY;
- búsqueda de duplicidades de nombres y lógica repetida;
- revisión de permisos, backup, PROBE y patrones de secretos;
- `./gradlew.bat test lint -PdistributionProfile=MAIN`;
- `./gradlew.bat :app:assembleFullDebug -PdistributionProfile=MAIN`;
- inspección del manifiesto de la APK MAIN v3.1 y de sus recursos ZIP;
- `git diff --check`.

## Controles satisfactorios

- Las pruebas unitarias pasan para `fullDebug` y `prsOnlyDebug`.
- `lint` termina correctamente sin incidencias reportadas.
- RADS mantiene silencio en nivel 0, cambia de capa cada 40 ms y solapa las
  capas en los niveles 3 y 6 sin alterar deliberadamente el volumen.
- TRACKER usa `PrsProbabilityFog` y admite el gesto de pellizco manteniendo la
  lectura y la niebla.
- SENTRY y la superficie P.R.S. v4.0 mantienen su GRID.
- PROBE continúa desactivado en las distribuciones de testers.
- La APK MAIN declara el paquete `com.suri.pipsurios`, versión `3.1` y código
  `11`.
- La APK MAIN contiene `sounds/1.mp3`, `sounds/2.mp3` y `sounds/3.mp3`, y no
  contiene `sounds/radiation.mp3`.
- No se localizaron claves privadas, credenciales ni secretos evidentes.
- `allowBackup=false` permanece activo.
- No hay nombres de archivos Kotlin duplicados dentro de `app`.

## Incidencias y propuestas para el siguiente Sprint

| ID | Nivel | Estado | Incidencia | Propuesta |
|---|---|---|---|---|
| AUD-031-01 | Alto operativo | Pausada y aceptada | Los mapas e iconos de perfiles tester se generan en `build/`, que no se versiona; un clon limpio puede no reproducirlos. | Versionar un paquete de recursos o hacer reproducible su generación desde el repositorio y validarla en CI. |
| AUD-032-01 | Medio | Pendiente | SENTRY y TRACKER repiten parte del ciclo de escaneo, evaluación y ciclo de vida. | Extraer un controlador común de adquisición/evaluación y mantener separadas las superficies visuales y sus diferencias funcionales. |
| AUD-032-02 | Bajo | Pendiente | Gradle avisa de que `setSrcDirs` está obsoleto en `app/build.gradle.kts`. | Migrar a la API `directories` en una tarea de mantenimiento, verificando que no cambien los recursos de cada perfil. |
| AUD-032-03 | Bajo | Pendiente | `ClickScheduler.intervalMillis` y sus constantes ya no participan en el audio continuo; se conservan porque los tests históricos todavía los cubren. | Sustituir esos tests por pruebas del nuevo comportamiento y eliminar el código muerto cuando se confirme que no se necesita compatibilidad. |
| AUD-032-04 | Bajo | Aceptada | El gesto multitáctil no puede automatizarse por ADB en este dispositivo; la validación depende del uso físico. | Añadir una prueba UI multitáctil en un entorno que admita inyección o conservar la comprobación manual documentada. |
| AUD-032-05 | Bajo operativo | Por política | FENRIR, ALTAMIRA y CHECHU permanecen en la distribución v3.0 y no reciben el ajuste de 40 ms ni el zoom de esta iteración. | Regenerar y distribuir las APK tester solo si el propietario lo ordena expresamente. |
| AUD-032-06 | Bajo administrativo | Pendiente | La APK MAIN v3.1 no pudo reinstalarse en el A56 porque el dispositivo se desconectó. | Reconectar el A56 e instalar la APK v3.1 antes de la próxima validación física, si se considera necesario. |

## Decisión de cierre

No se detectan errores críticos, pérdidas de datos, secretos expuestos ni
fallos de compilación que obliguen a detener el cierre. `AUD-031-01` sigue
siendo un riesgo importante de reproducibilidad, pero permanece pausada por la
decisión explícita del propietario para esta Alpha controlada.

El Sprint 032 y la versión PIP-SuriOS v3.1 quedan aprobados para commit y push.
El feedback adicional de los testers, la limpieza de duplicidades y la
reproducibilidad de los paquetes se trasladan al siguiente Sprint.
