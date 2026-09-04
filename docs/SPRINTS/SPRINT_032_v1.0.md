# Sprint 032 — Seguimiento de Alpha y feedback de testers

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Cerrado
owner: Diego Pérez de Camino
date: 2026-09-04
predecessor: Sprint 031
---

## Objetivo

Recoger el resultado de las pruebas de los testers Alpha y usar datos reales
para decidir las próximas correcciones del proyecto.

## Alcance inicial

- recibir y revisar los informes de FENRIR, ALTAMIRA y CHECHU;
- comprobar si `AUD-031-01` se reproduce en otros dispositivos;
- conservar la diferencia funcional entre SENTRY y TRACKER;
- mantener PROBE fuera de las APK tester;
- actualizar la firma de versión al cierre si las validaciones son satisfactorias;
- actualizar las guías dinámicas con los resultados confirmados.

## Trabajo pendiente

- [ ] Recibir los formularios CSV de los tres testers.
- [ ] Revisar instalación, permisos, identidad, mapas, SENTRY y TRACKER.
- [ ] Comparar los resultados del mapa `TESTING` de ALTAMIRA y CHECHU con
      FENRIR.
- [ ] Decidir si `AUD-031-01` se corrige, se mantiene pausada o se cierra.
- [ ] Registrar nuevas incidencias reproducibles y priorizarlas.
- [x] No regenerar ni redistribuir APK tester en futuros cambios sin una orden
      explícita del propietario.
- [ ] Actualizar las tres guías de tester y las tres guías de funcionamiento
      si el feedback cambia el comportamiento documentado.
- [x] Cerrar el Sprint 032 con decisión documentada y autorización del
      propietario.

## Avance implementado — 2026-09-04

- [x] Se retiró `assets/sounds/radiation.mp3` y se prepararon las tres capas
      `assets/sounds/1.mp3`, `2.mp3` y `3.mp3`.
- [x] Se retiró el generador de sonido PCM sintético de RADS.
- [x] RADS mantiene silencio en 0, selecciona capas en 1–10 y solapa 1+2 en
      3 y 2+3 en 6, sin modificar el volumen de las pistas.
- [x] RADS revisa el nivel cada 40 ms para reducir el retraso entre aguja y
      audio.
- [x] TRACKER sustituyó el grid circular por una nube visual de probabilidad.
- [x] TRACKER admite zoom manual mediante gesto de pellizco sobre el mapa.
- [x] SENTRY y la superficie v4.0 conservaron su GRID.
- [x] `test` y `lint` pasaron después de los cambios.

## Validación física inicial — 2026-09-04

- [x] Las tres APK tester v3.0 de la compilación previa se instalaron
      correctamente en el Samsung A56.
- [x] ALTAMIRA abrió RADS y mostró los estados LOW, HIGH y CRITICAL.
- [x] ALTAMIRA recorrió TRACKER hasta `FIELD: TESTING` y mostró
      `DISPLAY: PROBABILITY FOG` con un objetivo BLE detectado.
- [x] No se observaron cierres durante el recorrido.
- [x] Las cuatro APK finales v3.0 se generaron y verificaron en
      `output/SPRINT_032_APK`; contienen los tres audios nuevos y no contienen
      `radiation.mp3`.
- [x] Las cuatro APK finales v3.0 se instalaron en el A56 con resultado
      `Success`.
- [x] MAIN se recorrió hasta `P.R.S. / TRACKER / STEP 2 // FOG` y mostró
      `DISPLAY: PROBABILITY FOG`; RADS abrió sin cierres.
- [x] Valorar auditivamente las tres capas y los solapes de RADS en LOW, HIGH y
      CRITICAL; validación física confirmada por el propietario.
- [x] Confirmar durante las pruebas de campo que el zoom resulta cómodo y no
      interfiere con la lectura; validación física confirmada por el propietario.

## Regla de actualización de APK tester

La regeneración y distribución de FENRIR, ALTAMIRA y CHECHU requiere una orden
explícita del propietario. La orden de esta iteración autoriza la actualización
de las cuatro APK para integrar las nuevas capas de RADS y corregir TRACKER en
MAIN. Esta excepción no se extiende a cambios posteriores.

## Cierre de versión y del Sprint 032 — 2026-09-04

La firma de PIP-SuriOS pasa de `3.0` a `3.1` y el `versionCode` de Android pasa
de `10` a `11`. `test`, `lint` y la compilación de MAIN han terminado
correctamente. La validación física comunicada por el propietario confirma el
audio de RADS y el zoom manual de TRACKER.

La APK MAIN v3.1 se genera correctamente. La reinstalación del incremento de
versión en el Samsung A56 queda pendiente porque el dispositivo se desconectó
durante esa comprobación; las modificaciones funcionales ya habían sido
validadas físicamente por el propietario. Las APK de FENRIR, ALTAMIRA y CHECHU
permanecen congeladas en la distribución anterior por la política explícita
del proyecto; su actualización requerirá una orden expresa.

El feedback adicional de los testers se conserva como trabajo futuro y no
impide este cierre de versión.

Auditoría asociada: [AUDIT_SPRINT_032](../AUDIT_SPRINT_032.md).
