# Sprint 031 — Auditoría externa y preparación Beta

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Cerrado
owner: Diego Pérez de Camino
date: 2026-09-04
predecessor: Sprint 030
---

## Objetivo

Revisar el proyecto como un agente externo antes de distribuir nuevas APK a
testers. El foco es descubrir duplicidades, dependencias locales y situaciones
que puedan causar problemas futuros.

## Alcance realizado

- auditoría del repositorio y de una copia limpia de `HEAD`;
- revisión de Gradle, recursos, mapas, módulos Wear OS y servicios P.R.S.;
- revisión de duplicidades de código y recursos, manteniendo las diferencias
  de SENTRY y TRACKER;
- revisión de pruebas, documentación, scripts y configuración de seguridad;
- validación de `test` y `lint` para MAIN;
- desactivación de PROBE en las APK de testers;
- decisión sobre la preparación de las APK Alpha y sus ZIP.

## Resultado

La auditoría ha encontrado una incidencia de reproducibilidad, que queda
pausada para recibir feedback de los testers. No es un fallo de compilación en
MAIN. El detalle completo está en
[AUDIT_SPRINT_031](../AUDIT_SPRINT_031.md).

Por decisión del propietario, se continúa con una Alpha controlada. En este
Sprint:

- se mantiene la diferencia funcional entre SENTRY y TRACKER;
- se desactiva PROBE en FENRIR, ALTAMIRA y CHECHU;
- se centraliza la firma visible de versión;
- se actualiza la guía Alpha con la incidencia conocida;
- se generan paquetes individuales para cada tester.

## Validación física y cierre

El 2026-09-04 se instalaron las APK v3.0 de FENRIR, ALTAMIRA y CHECHU en el
Samsung A56 `SM_A566B` (`RZGYC07H0EX`). Se confirmó la identidad de cada perfil,
la navegación, los permisos, SENTRY y TRACKER.

TRACKER inició la lectura automáticamente al entrar en el objetivo y terminó
al usar `BACK`. Las APK tester no mostraron ni activaron PROBE.

FENRIR mostró la cartografía base de `TESTING`. ALTAMIRA y CHECHU cargaron el
campo y la cuadrícula, pero dejaron vacía la cartografía base, aunque el recurso
de mapa está incluido en sus APK. Este resultado queda registrado como
`AUD-031-01`, pausado para recibir feedback Alpha.

No se observaron cierres de la aplicación durante las pruebas. El Sprint 031
queda cerrado y su siguiente trabajo se traslada al Sprint 032.

## Cierre

Sprint 031 cerrado el 2026-09-04 tras completar la validación física, el
registro documental y la preparación de la entrega Alpha. La incidencia
`AUD-031-01` permanece abierta únicamente como incidencia conocida pausada.

## Hallazgos principales

| ID | Tema | Nivel | Próxima acción |
|---|---|---:|---|
| AUD-031-01 | Recursos generados de perfiles no versionados | Alto | Pausada; el tester debe informar si faltan mapas o iconos |
| AUD-031-02 | Lógica P.R.S. repetida en dos pantallas | Medio | Aceptada por ahora para conservar diferencias de SENTRY y TRACKER |
| AUD-031-03 | Confianza del Data Layer | Medio | Fuera de APK tester; reservado para MAIN |
| AUD-031-04 | Sesión PROBE | Medio | Fuera de APK tester; reservado para MAIN |
| AUD-031-05 | Firma de versión repetida | Bajo | Resuelta con `AppVersion.kt` |
| AUD-031-06 | Recursos compartidos duplicados | Bajo | Omitida por decisión |
| AUD-031-07 | Cobertura instrumentada limitada | Bajo/medio | Omitida; se espera feedback manual |
| AUD-031-08 | Scripts con rutas locales | Bajo/medio | Parametrizados y acompañados de empaquetado Alpha |

## Criterio para reabrir el empaquetado beta

La distribución Alpha controlada queda autorizada aunque `AUD-031-01` siga
pausada. Cada ZIP debe incluir la APK correspondiente, la guía actualizada, la
plantilla CSV y un hash SHA-256. Si un tester recibe una APK sin mapas, con mapa
vacío o con icono incorrecto, debe anotarlo como feedback.
