# Auditoría Sprint 037 — SuriOS Watch

---
document: AUDIT
project: SuriOS Watch
sprint: 037
version: 1.3
version_code: 7
date: 2026-10-16
status: Aprobada para cierre
---

## Resultado

La auditoría final no encuentra incidencias técnicas abiertas dentro del
alcance del Sprint 037. La esfera oficial queda en `versionName=1.3`,
`versionCode=7`, instalada y validada en el Samsung Galaxy Watch Ultra.

La referencia a `v1.4` localizada durante la revisión pertenece a documentos
de especificación, como `WFPRD_v1.4.md`; no corresponde a la versión técnica ni
a la firma visible de esta watchface.

## Código y comportamiento revisados

- `watch/watchface/build.gradle.kts`: `versionName=1.3` y `versionCode=7`.
- `watch/watchface/src/main/res/raw/watchface.xml`: firma visible `v1.3`,
  armadura, progreso invertido de pasos, batería, botones y modo ambiente.
- El progreso usa `[STEP_PERCENT]`: la capa verde se desvanece a medida que
  aumenta el porcentaje de pasos, dejando la armadura sin energía al alcanzar
  el objetivo.
- La línea de refresco solo pertenece al modo normal: color `#6666FF66`,
  grosor 2, duración 6 segundos y `fps=30`.
- El modo ambiente no contiene la línea animada, fecha, botones ni texto de
  recarga; conserva hora, armadura con progreso de pasos y batería.
- `TRAINING` abre
  `com.samsung.android.wear.shealth/.app.exercise.view.ExerciseListActivity`.
  La pantalla se comprobó en el dispositivo y fue validada por el propietario.

## Copias de recuperación

- `Previous Version/watchface-v1.3-calories-sprint/` conserva la esfera
  funcional al inicio del Sprint.
- `Previous Version/mobile-app-v3.4-calories-sprint/` conserva la aplicación
  móvil al inicio del Sprint.
- No se encontraron carpetas `build` dentro de `Previous Version`.

## Gates técnicos

| Comprobación | Resultado |
|---|---|
| `gradlew.bat test` | Correcto |
| `gradlew.bat lint` | Correcto |
| `gradlew.bat :watchface:assembleDebug` | Correcto |
| Parseo XML de `watchface.xml` | Correcto |
| `git diff --check` | Sin errores; solo avisos normales LF/CRLF |
| Marcadores de conflicto Git | No encontrados |
| Instalación ADB Wi-Fi | `Success` |
| Versión instalada | `1.3`, código `7` |
| Validación visual final | Aprobada por el propietario |

## Incidencia resuelta

Durante la auditoría se detectó que `TRAINING` lanzaba la actividad interna
`ExerciseActivity`, que necesita datos de una sesión y podía dejar una pantalla
negra. Se sustituyó por `ExerciseListActivity`, la pantalla pública de selección
de ejercicios. La corrección se compiló, instaló y validó correctamente.

## Deuda o advertencia no bloqueante

El destino de `TRAINING` pertenece a la aplicación Samsung Health y podría
cambiar si Samsung modifica sus componentes internos. La versión actual está
validada en el Watch Ultra objetivo; cualquier cambio futuro de Samsung deberá
revisarse en una nueva auditoría.

## Decisión

El Sprint 037 queda **CLOSED** y esta auditoría queda **APROBADA PARA CIERRE**.
