# Sprint 016 - Revisión de temas abiertos y saneamiento documental

---

document: SPRINT
sprint: 016
version: 1.1
project: SuriOS Ecosystem / PIP-SuriOS
document_status: Cerrado
implementation_status: Cerrado con deuda abierta y validacion fisica pendiente
priority: Media
replaces: Sprint 016 v1.0
---

## Estado

- Apertura: 2026-08-30.
- Cierre: 2026-08-30.
- Estado: cerrado.
- Autorización: apertura expresa del propietario el 2026-08-30.
- Aplicación móvil de referencia: PIP-SuriOS v2.3 (`versionCode 3`).
- Sprint activo posterior: Sprint 017, en curso.

## Cambio de alcance

La campaña física de P.R.S. y el ajuste de `PrsTuning.DEFAULT` quedan
eliminados del Sprint 016 por decisión expresa del propietario. No se
realizarán tomas de campo, análisis de calibración ni cambios de parámetros
con ese objetivo.

La versión 1.1 sustituye el alcance anterior por una revisión controlada de
los temas abiertos del repositorio y por la decisión de abordarlos, mantenerlos
como deuda o cancelarlos.

## Objetivo

Dejar un inventario fiable de las cuestiones abiertas de SuriOS, retirar las
actividades que ya no forman parte del producto y separar las deudas reales de
las referencias históricas o inconsistentes.

## Alcance autorizado

### Revisión documental

- Revisar `BACKLOG_v1.2`, las auditorías de P.R.S. y seguridad, `ACTIVE_SPRINT`,
  `SPRINT_HISTORY`, `PRS_v3.0`, `USER_GUIDE` y `ORCA-TO-DO`.
- Reconciliar referencias que describan como vigente una funcionalidad
  retirada, especialmente el gateway HTTP antiguo frente al flujo actual del
  repositorio.
- Mantener separados los documentos activos, los antecedentes históricos y
  las copias de respaldo; no borrar material histórico sin autorización.

### Decisiones adoptadas

| Tema | Decisión | Tratamiento en este Sprint |
|---|---|---|
| Campaña física de P.R.S. | Cancelada | Eliminarla del roadmap activo y no generar datos de campo. |
| Ajuste de `PrsTuning.DEFAULT` | Cancelado | Mantener los valores actuales sin presentarlos como calibrados. |
| Fusión de movimiento/orientación en las nubes | Cancelada como línea de trabajo | La interfaz opcional puede permanecer como extensión técnica, sin desarrollo previsto. |
| `OPERATION GUIDE` de campo | Cancelado como entregable actual | No redactar un procedimiento que dependa de una campaña cancelada. |
| Reconexión ADB del A56 | Cerrada como incidencia de continuidad | No bloquea el producto ni forma parte del Sprint 016. |
| Hallazgos de seguridad del gateway HTTP antiguo | Revisar y reconciliar | Separar hallazgos obsoletos de riesgos que sigan aplicando al código actual; no declarar seguridad pública. |
| Privacidad de backups, telemetría BLE y datos de usuario | Mantener abierta | Requiere decisión de mitigación antes de una distribución pública. |
| Coherencia de documentación e historial | Abordar | Actualizar los registros canónicos y retirar contradicciones de estado. |

### Backlog

- Abordar en este Sprint la limpieza documental de BL-003, BL-007, BL-008 y
  BL-010: estado de versionado, política de `.idea`, función del CHANGELOG y
  determinación del EDL canónico.
- Mantener como deuda condicionada a una decisión posterior BL-001
  (tipografía definitiva) y BL-014 (licencia y procedencia del emblema); no
  introducir cambios funcionales para resolverlos.
- Cancelar como alcance vigente de SuriOS BL-002, BL-005, BL-006, BL-011,
  BL-015 y BL-016 por obsolescencia, redundancia o ausencia de requisito
  aprobado. Los registros del BACKLOG no se eliminan: su estado se conservará
  en la próxima versión documental que autorice el propietario.
- Mantener BL-004, BL-012 y BL-013 fuera de este Sprint por tratarse de política
  externa, archivo histórico o normalización no prioritaria.

## Entregables

- Matriz de decisión de temas abiertos, con evidencia y estado.
- Documentación canónica coherente con el alcance actual de P.R.S. y del
  ecosistema.
- Disposición documentada de los hallazgos de seguridad que ya no correspondan
  al código actual y de los que permanezcan abiertos.
- Relación de deuda aceptada y de asuntos cancelados sin borrar su trazabilidad.

## Fuera de alcance

- Toda campaña física, calibración, toma de CSV y ajuste de `PrsTuning.DEFAULT`.
- Desarrollo de la fusión de sensores, movimiento, orientación o heading.
- RSSI convertido en metros exactos, coordenadas X/Y, azimut BLE,
  triangulación, Wi-Fi RTT, UWB o machine learning.
- Cifrado, autenticación, rate limiting u otra remediación de seguridad no
  descrita y aprobada como tarea independiente.
- Cambios de versión de PIP-SuriOS, PIW-SuriOS Watch o PROBE-SuriOS.

## Criterios de finalización

- Cada tema revisado queda clasificado como abordado, deuda mantenida,
  cancelado o fuera del Sprint, con motivo y referencia.
- No quedan referencias activas a la campaña física ni al ajuste de
  `PrsTuning.DEFAULT`.
- Las auditorías no atribuyen al código actual componentes ya retirados sin
  indicarlo como antecedente.
- Los documentos canónicos no se contradicen sobre el Sprint activo ni sobre
  el estado de P.R.S.
- No se realizan commits sin autorización expresa del propietario.

---

## Addendum de cierre de la terminal - 2026-08-30

La continuación del trabajo incorporó, por autorización expresa del propietario,
la preparación y validación en emulador de la esfera OFICIAL de SuriOS Watch.
El detalle técnico y la evidencia reproducible quedan en
[AUDIT_SPRINT_016.md](../AUDIT_SPRINT_016.md).

Se auditó y conservó el AVD adicional `Galaxy_Watch_Ultra_2025`, basado en
`wearos_xl_round (Google)`, 480 x 480, 320 dpi, Wear OS 5 / API 34,
Play Store, x86_64. Los AVD anteriores permanecen disponibles.

La esfera OFICIAL `com.suri.surioswatch` queda en `v1.1` (`versionCode 5`),
con marco circular, emblema, progreso de pasos sin cifra, batería sin
porcentaje, firma `v1.1` y accesos CAPS/STATUS/RADIO. La esfera PROBE sigue
separada y no se instaló ni modificó el Watch 2 en esta iteración.

La validación final de tests, ensamblados y lint de `fullDebug` y `prsOnlyDebug`,
junto con `:watchface:assembleDebug`, terminó en `BUILD SUCCESSFUL`.
STATUS abrió `DATA - STATISTICS` en el emulador. Google Pay y Spotify no se
declaran validados end-to-end porque sus paquetes no estaban instalados en el
AVD.

El cierre no incluye la instalación en el Watch Ultra físico ni la integración
Orca-QGIS. Las deudas de privacidad y documentación, y los elementos pendientes
del BACKLOG, quedan explícitamente mantenidos para una autorización posterior.

## Nota histórica de atribución — cambios P.R.S. reubicados en Sprint 017

Por decisión posterior del propietario, el siguiente bloque de refinamiento de
P.R.S. se registra como trabajo de Sprint 017. Sprint 016 conserva aquí su
trazabilidad documental, pero la auditoría y el cierre de esos cambios están
en [Sprint 017 v1.0](SPRINT_017_v1.0.md) y [AUDIT_SPRINT_017](../AUDIT_SPRINT_017.md).

La edición reducida de P.R.S. queda ajustada para lectura rápida: `P.R.S.`
centrado, radar en la mitad izquierda y centrado en el eje vertical, y lista
de nombres en la mitad derecha. Se retiraron de esa superficie los estados,
contadores y textos auxiliares sin utilidad operativa inmediata.

La inferencia de tipo de dispositivo queda integrada en la edición reducida y
en PIP-SuriOS. Cuando es identificable, el nombre puede incorporar `[PHONE]`,
`[WATCH]`, `[TV]`, `[AUDIO]` o `[COMPUTER]`; si no es identificable no aparece
ningún sufijo. No se muestra margen de confianza ni signos de interrogación.

La auditoría específica de esta modificación, incluyendo builds, tests, lint,
despliegue ADB y pendientes físicos, queda en
[AUDIT_SPRINT_017](../AUDIT_SPRINT_017.md). Las pruebas físicas de aceptación
en moto quedan fuera de este cierre y pendientes de ejecución.
