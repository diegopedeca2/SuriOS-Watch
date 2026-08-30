# Sprint 016 - Calibración física de P.R.S. y ajuste de parámetros

> Documento sustituido por [Sprint 016 v1.1](SPRINT_016_v1.1.md). Esta versión
> queda conservada como antecedente de planificación y no es el alcance activo.

## Estado

- Apertura: 2026-08-30.
- Cierre: pendiente.
- Estado: activo.
- Autorización: apertura expresa del propietario el 2026-08-30.
- Aplicación móvil de referencia: PIP-SuriOS v2.3 (`versionCode 3`).
- Sprint activo posterior: ninguno.

## Objetivo

Realizar una campaña controlada de calibración física del P.R.S. con
observaciones reales y trazables, evaluar los valores iniciales de
`PrsTuning.DEFAULT` y aprobar únicamente los ajustes respaldados por la
evidencia recogida.

La calibración debe mejorar la interpretación comparativa de RSSI y de las
tendencias temporales sin presentar BLE como una medición de distancia,
dirección o posición exacta.

## Alcance autorizado

### Campaña de campo

- Ejecutar el protocolo de la guía de calibración vigente en condiciones
  controladas y anotar dispositivo, entorno, orientación, obstáculos,
  posición de prueba y cualquier incidencia.
- Cubrir el control `A56 ONLY / WITHOUT WATCH` y, cuando esté disponible, el
  modo `A56 + WATCH 2 / DUAL NODE`.
- Repetir las tomas de cada condición conforme al protocolo y conservar los
  CSV y las notas de cada sesión sin sustituir datos por resultados resumidos.

### Evaluación y ajuste

- Evaluar con las muestras reales la cadencia de evaluación, el suavizado, la
  ventana histórica, los umbrales relativos, la confirmación de tendencias,
  la histéresis y la expiración de contactos.
- Comparar falsos cambios de estado, estabilidad y pérdida de contactos entre
  condiciones, identificando las limitaciones del hardware y del entorno.
- Proponer cambios a `PrsTuning.DEFAULT` sólo cuando exista trazabilidad entre
  observaciones, análisis y decisión.
- Implementar y probar los cambios aprobados, si los hubiera, manteniendo
  centralizados los parámetros de calibración.

### Documentación

- Registrar el procedimiento realizado, la evidencia disponible, los valores
  aceptados o rechazados y las limitaciones que permanezcan.
- Actualizar `PRS_v3.0.md`, `USER_GUIDE.md`, `OPERATION GUIDE` y el changelog
  únicamente cuando el resultado de campo justifique esas actualizaciones.
- Mantener separadas las mediciones, el procesamiento y las inferencias del
  P.R.S.; no convertir una observación de campo en una coordenada o distancia
  exacta.

## Entregables

- Registro de sesiones y CSV de la campaña, con sus condiciones de prueba.
- Informe de análisis y decisión sobre `PrsTuning.DEFAULT`.
- Cambios de código, pruebas y documentación que resulten aprobados.
- Evidencia de validación reproducible y relación de riesgos o deuda abierta.

## Fuera de alcance

- RSSI convertido en metros exactos, coordenadas X/Y, azimut BLE o localización
  física definitiva.
- Wi-Fi RTT, UWB, triangulación, machine learning o clasificación avanzada.
- Integración de acelerómetro, giroscopio, magnetómetro, heading o
  desplazamiento para reducir las nubes de densidad; queda reservada para un
  sprint posterior.
- Cambios de versión de PIP-SuriOS, PIW-SuriOS Watch o PROBE-SuriOS que no
  sean necesarios y estén aprobados expresamente.

## Criterios de finalización

- El protocolo de campo se ejecuta con las repeticiones previstas y las
  sesiones quedan conservadas junto con sus condiciones.
- Cada ajuste propuesto a `PrsTuning.DEFAULT` tiene evidencia y justificación;
  los parámetros sin evidencia permanecen sin cambios.
- Los cambios de código, si existen, pasan tests, ensamblado, lint y
  `git diff --check`.
- Las limitaciones y resultados quedan documentados, sin afirmaciones de
  precisión que el sistema no pueda medir.
- El propietario aprueba el resultado y autoriza el cierre y el commit estable.

## Reglas operativas

- Sólo se trabaja dentro de este Sprint activo.
- No se realizan commits sin autorización expresa.
- Una campaña incompleta o sin evidencia suficiente no se presenta como
  calibración final; se documenta como resultado parcial.
