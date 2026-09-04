# ACTIVE_SPRINT

---

document: ACTIVE_SPRINT
status: Registro operativo canonico
last_updated: 2026-09-05

---

## Sprint activo

No hay un Sprint activo. El Sprint 033 queda cerrado documental y técnicamente
el 2026-09-05.

[Sprint 033 v1.0](SPRINT_033_v1.0.md) — **CLOSED**

[Sprint 032 v1.0](SPRINT_032_v1.0.md) — **CLOSED**

[Auditoría Sprint 032](../AUDIT_SPRINT_032.md) — **APROBADA PARA CIERRE**

## Estado final del Sprint 033 - 2026-09-05

- [x] Se adopta la estrategia híbrida de recursos definida en ADR-004.
- [x] Los mapas e iconos tester se leen desde rutas versionadas con Git LFS.
- [x] Gradle usa `directories` y no `setSrcDirs`.
- [x] El empaquetador exige una autorizacion explicita para crear una nueva
      distribucion tester.
- [x] Se elimina el código antiguo de cadencia de RADS y sus tests históricos.
- [x] Los gestos de pantalla quedan sujetos a validación física.
- [x] Las APK tester quedan fijadas y el empaquetador elimina sus artefactos
      anteriores cuando genera una nueva versión.
- [ ] Mejorar el tiempo de carga de MBTiles en una futura versión.
- [x] Cerrar el Sprint 033 tras la revisión final y autorización del propietario.

## Cierre documental del Sprint 033 - 2026-09-05

La auditoría final no detecta incidencias graves ni bloqueantes. La mejora del
tiempo de carga de MBTiles se traslada a una futura versión y no bloquea este
cierre. Las APK tester existentes no se han regenerado ni modificado.

## Cierre documental del Sprint 032 - 2026-09-04

- [x] RADS usa `assets/sounds/1.mp3`, `2.mp3` y `3.mp3` en las APK generadas.
- [x] RADS deja el nivel 0 en silencio y solapa las capas en los niveles 3 y
      6; el volumen de cada pista permanece fijo.
- [x] RADS revisa el nivel cada 40 ms para reducir el retraso entre la aguja y
      el cambio de pista.
- [x] TRACKER usa una nube de probabilidad tipo niebla de guerra.
- [x] TRACKER admite zoom manual mediante pellizco de dos dedos, manteniendo
      la lectura y la niebla activas.
- [x] SENTRY y la superficie v4.0 conservan el GRID anterior.
- [x] Se generaron y verificaron las cuatro APK v3.0 de MAIN, FENRIR, ALTAMIRA
      y CHECHU en `output/SPRINT_032_APK`.
- [x] La validación física previa de las APK tester recorrió RADS y TRACKER en
      el A56 sin cierres; corresponde a la compilación anterior a estos audios.
- [x] Las cuatro APK finales de esta iteración se instalaron en el A56 con
      resultado `Success`.
- [x] MAIN se recorrió hasta `P.R.S. / TRACKER / STEP 2 // FOG` y mostró
      `DISPLAY: PROBABILITY FOG`; RADS también abrió sin cierres.
- [x] ALTAMIRA llegó hasta `FIELD: TESTING` y `DISPLAY: PROBABILITY FOG` con
      un objetivo BLE detectado.
- [x] Valorar auditivamente las tres capas y los solapes de RADS en LOW, HIGH y
      CRITICAL; validación física confirmada por el propietario.
- [x] Confirmar en una prueba manual de campo que el pellizco de zoom resulta
      cómodo y no interfiere con la lectura; validación física confirmada por
      el propietario.
- [x] La actualización de las cuatro APK de esta iteración fue autorizada de
      forma explícita; las APK tester no se actualizarán en iteraciones futuras
      salvo nueva orden explícita.
- [ ] Recibir feedback adicional de los testers; se traslada al siguiente
      Sprint y no bloquea este cierre.

El Sprint 032 se abrió el 2026-09-04 después del cierre documental, técnico y
físico del Sprint 031. Su objetivo se amplió para integrar y validar las
modificaciones de audio de RADS y la niebla y el zoom de TRACKER.

[Sprint 031 v1.0](SPRINT_031_v1.0.md) — **CLOSED**

[Sprint 030 v1.0](SPRINT_030_v1.0.md) — **CLOSED**

El Sprint 026 se ha cerrado tras validar el acceso `STATUS` y conservar el
comportamiento AMBIENT anterior en el Watch Ultra.

El Sprint 027 se cerró el 2026-09-01 tras completar la limpieza del proyecto,
los detalles documentales menores y el retorno al diseño original de
PIP-SuriOS.

El Sprint 028 se ha cerrado el 2026-09-01 tras actualizar la versión y aplicar
las mejoras visuales de terminal autorizadas, con validación física en el
Samsung A56.

El Sprint 029 se ha abierto el 2026-09-02 por autorización expresa del
propietario. Su alcance incluye la primera distribución Alpha, la creación de
formularios manuales para PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES,
FRONT PANEL, UNIFORM y HEADGEAR en dos pasos, la combinación de P.R.S. v3.0 y
v4.0 en los menús SENTRY, TRACKER, DEVICES y USER GUIDE, y la corrección del
sonido de RADS.

[Sprint 029 v1.0](SPRINT_029_v1.0.md) — **CLOSED**

[Sprint 028 v1.0](SPRINT_028_v1.0.md) — **CLOSED**

En el momento de este registro histórico no había ningún Sprint pendiente de
cierre. El Sprint 032 quedó cerrado; el Sprint 033 se abrió posteriormente.

## Validación y cierre del Sprint 031

La comprobación física se realizó el 2026-09-04 en un Samsung A56, modelo
`SM_A566B`, identificador ADB `RZGYC07H0EX`.

- [x] Se instalaron las APK Alpha de FENRIR, ALTAMIRA y CHECHU.
- [x] Se confirmó el nombre, el icono y la versión `v3.0` de cada APK.
- [x] Se probaron permisos, apertura, navegación, SENTRY y TRACKER.
- [x] TRACKER mostró lectura automática al entrar en el objetivo y terminó al
      usar `BACK`.
- [x] Las APK tester no mostraron ni activaron PROBE.
- [x] Se registró la incidencia conocida `AUD-031-01`: FENRIR muestra la
      cartografía base de `TESTING`; ALTAMIRA y CHECHU cargan el campo y la
      cuadrícula, pero muestran la cartografía base vacía. Los mapas están
      incluidos en las APK. La incidencia queda pausada para feedback Alpha.
- [x] El resultado quedó registrado en la auditoría y en el documento del
      Sprint.
- [x] El Sprint 031 se marcó `CLOSED` y se preparó su commit y push.

El Sprint 031 queda cerrado con `AUD-031-01` pausada por decisión del
propietario. El Sprint 032 recoge el trabajo posterior y el feedback de los
testers.

## Cierre Sprint 029

Sprint 029 cerrado el 2026-09-03. La pendiente no crítica de las coordenadas
de CHECHU quedó resuelta durante Sprint 030 con la generación de su mapa
`TESTING`.

## Último Sprint cerrado

[Sprint 031 v1.0](SPRINT_031_v1.0.md) — **CLOSED**

[Sprint 030 v1.0](SPRINT_030_v1.0.md) — **CLOSED**

[Sprint 029 v1.0](SPRINT_029_v1.0.md) — **CLOSED**

[Sprint 028 v1.0](SPRINT_028_v1.0.md) — **CLOSED**

[Sprint 027 v1.0](SPRINT_027_v1.0.md) — **CLOSED**

[Sprint 026 v1.0](SPRINT_026_v1.0.md) — **CLOSED**

[Sprint 025 v1.0](SPRINT_025_v1.0.md) — **CLOSED**

El Sprint 025 se cerró tras la validación física correcta en el Samsung A56.

El alcance incluyó el nuevo menú `INFORMATION`, la recolocación de
`ACKNOWLEDGEMENTS`, la incorporación de `DISCLAIMERS` y la actualización
controlada de dependencias.

[Sprint 024 v1.0](SPRINT_024_v1.0.md) — **CLOSED**

El alcance cerrado fue el endurecimiento de la integración A56–Watch 2 de
PIP-SuriOS/PROBE y el cierre de las deudas AUD-023-01 a AUD-023-09.

[Sprint 023 v1.0](SPRINT_023_v1.0.md) — **CLOSED**

Los SPRINT 20 y 21 se mantuvieron en procesos independientes. El SPRINT 21 ha
sido confirmado como cerrado sin regresiones observables.

Sprint 019 queda cerrado. [Sprint 019 v1.0](SPRINT_019_v1.0.md) — **CLOSED**

## Estado

Sprint 015: **CLOSED**
Sprint 016: **CLOSED**
Sprint 017: **CLOSED**
Sprint 018: **CLOSED**
Sprint 019: **CLOSED**
Sprint 020: **CLOSED**
Sprint 021: **CLOSED**
Sprint 022: **CLOSED**
Sprint 023: **CLOSED**
Sprint 024: **CLOSED**
Sprint 025: **CLOSED**
Sprint 026: **CLOSED**
Sprint 027: **CLOSED**
Sprint 028: **CLOSED**
Sprint 029: **CLOSED**
Sprint 030: **CLOSED**
Sprint 031: **CLOSED**
Sprint 032: **CLOSED**
Sprint 033: **ACTIVE**

Sprint 020 regeneró NAVY7 desde QGIS, corrigió el centro del campo y estableció
CHOOSE LOCATION como selección inicial de TERRAIN. Los mapas persistentes se
presentan alfabéticamente después de esa opción excepcional.

Sprint 019 audita el monorepo, actualiza PIP-SuriOS a v2.5, desinstala P.R.S.
//COVER del A56 y documenta el cierre de seguridad y validación.

La firma visible y técnica de PIP-SuriOS queda actualizada a v3.1 para el
cierre del Sprint 032, sobre la base completada y cerrada técnica, funcional y
documentalmente a fecha 2026-09-04.
El P.R.S. activo usa
`LOCAL SCAN`, `SCAN + PROBE`, `DEVICES` y `TRACK TARGET`, con histórico RSSI,
suavizado y nubes de densidad sin posicionamiento sintético. PIW-SuriOS Watch
v2.0 conserva el modo ambiente con el emblema girando sobre su eje vertical y
el PROBE operativo permanece separado como módulo Wear OS.

La edición `prsOnlyDebug` añade una superficie compacta para la pantalla
externa: `P.R.S.` centrado, radar a la izquierda y nombres a la derecha. La
edición completa y la reducida pueden añadir al nombre una categoría inferida
(`[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` o `[COMPUTER]`) cuando existe evidencia
reconocible; los dispositivos no identificables no reciben sufijo.

Sprint 016 queda cerrado de forma independiente. Sprint 017 queda cerrado tras
la auditoría técnica y el despliegue validado; cualquier trabajo ajeno a su
alcance requiere autorización expresa.

Los mapas TERRAIN y la lógica RAD ZONE/Geiger quedan incluidos en la auditoría
de Sprint 019. Sprint 020 modificó únicamente la cobertura de NAVY7, el estado
inicial y el orden del selector de mapas; HOME conserva su configuración.

## Cierre de Sprint 017 de SuriOS Ecosystem / PIP-SuriOS

[Sprint 017 v1.0](SPRINT_017_v1.0.md), abierto y cerrado el 2026-08-30.

La iteración incorpora la edición compacta de P.R.S. para la pantalla externa
del Z Flip 6 y la inferencia de categorías de dispositivo en la edición reducida
y en PIP-SuriOS. La aceptación física queda como continuidad posterior; el
cierre técnico y documental de Sprint 017 está completado.

## Contexto del Sprint 015 de PIP-SuriOS

[Sprint 015 v1.0](SPRINT_015_v1.0.md), cierre documental finalizado el 2026-08-30.

**Sprint 015 - Reconstrucción P.R.S. y cierre PIW/PROBE-SuriOS**

Se valida el comportamiento de PIW en modo ambiente, se separa la esfera
`PROBE-SuriOS` de la lógica de PIW y se cierra la reconstrucción de P.R.S. con
adquisición BLE, análisis temporal, `TRACK TARGET`, `DEVICES` y GRID de
densidad. La auditoría deja como riesgos aceptados el transporte local sin
cifrado/autenticación y las limitaciones inherentes a RSSI/BLE.

## Roadmap de PIP-SuriOS

- Sprint 015 queda cerrado.
- Sprint 028 queda cerrado tras actualizar la firma y la estética de terminal.
- Sprint 029 queda cerrado tras preparar la distribución Alpha y personalizar con
  cuadros de texto PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES, FRONT PANEL,
  UNIFORM y el flujo de HEADGEAR.
- Sprint 030 queda cerrado tras generar e integrar los mapas `TESTING` de
  ALTAMIRA y CHECHU, actualizar la documentación de P.R.S. y preparar la
  campaña empírica para testers.
- La guía imprimible del P.R.S. queda disponible en `output/pdf/PRS_CALIBRATION_GUIDE_SPRINT_015.pdf`.
- Se entregan las variantes `fullDebug` y `prsOnlyDebug` de P.R.S.
- Sprint 016 queda cerrado tras la revisión documentada de temas abiertos y el
  saneamiento de estado.
- Sprint 017 queda cerrado tras el refinamiento compacto de P.R.S. y la
  incorporación de categorías inferidas de dispositivo.
- Sprint 018 queda cerrado como registro histórico de una revisión visual
  posteriormente retirada.
- Sprint 019 queda cerrado tras la auditoría, seguridad y consolidación v2.5.
- Sprint 021 queda cerrado tras la implementación y regresión de la herramienta
  experimental INDIVIDUAL TRACKING, combinando TERRAIN con P.R.S.; la prueba
  física y la calibración estadística quedan como continuidad independiente.
- Sprint 022 queda cerrado tras incorporar y validar el apartado de
  agradecimientos de solo lectura dentro de SET-UP, manteniendo independientes
  los procesos de los SPRINT 20 y 21.
- Sprint 023 queda cerrado tras la auditoría completa, la reconstrucción
  HOME-style de NAVY7 y la validación instrumentada del asset offline en A56.

## Cierre de Sprint 022 de PIP-SuriOS

[Sprint 022 v1.0](SPRINT_022_v1.0.md), abierto el 2026-08-30 y cerrado el
2026-08-31.

El visualizador `SET-UP > ACKNOWLEDGEMENTS` muestra los seis agradecimientos
iniciales, admite desplazamiento vertical y no ofrece edición ni persistencia.
La regresión de la aplicación posterior a la incidencia del SPRINT 21 quedó
validada con tests JVM, lint, ensamblados, 2/2 tests instrumentados en el A56 y
comprobación manual de la ruta en el dispositivo.

## Addendum histórico — 2026-08-29

La auditoría final incorpora la actualización de `P.R.S. TESTING`: modo dual por defecto, línea base obligatoria de 30 s, estados de evidencia de ubicación y CSV de 30 columnas sin coordenadas GPS en bruto. La compilación de los cuatro módulos, las pruebas unitarias y Lint vuelven a estar correctos.

En el corte de esta fecha el Watch 2 permanece conectado por ADB inalámbrico en `192.168.1.56:5555` y `RemoteProbeService` está activo. El A56 no aparece actualmente en `adb devices`, por lo que queda como acción de continuidad reconectar el teléfono y repetir la validación de despliegue móvil. El emulador está conectado, pero la aplicación móvil requiere API 35 y el emulador actual usa API 34.

El sprint quedó cerrado documentalmente con esas limitaciones abiertas y explícitas. Posteriormente se abrió el Sprint 027 para limpieza documental.

## Addendum de cierre — 2026-08-30

El cierre de hoy consolida el estado operativo real del repositorio:

- `DEVICES` queda dividido en `IDENTIFY DEVICE` y `SAVED DEVICES`, con reglas
  persistentes que pueden habilitarse, deshabilitarse o eliminarse.
- La edición completa conserva `LOCAL SCAN`, `SCAN + PROBE`, el subgrid de
  PROBE y la orientación horizontal del P.R.S. en el A56.
- La variante `prsOnlyDebug` arranca directamente en `SCAN`, conserva
  `DEVICES` y no expone `SCAN + PROBE` ni registra el servicio PROBE.
- Se validó en la variante compacta el ciclo `SAVE → DISABLE → ENABLE → REMOVE`
  y no queda ninguna regla de prueba persistida.
- Se ejecutaron tests, ensamblados y lint de `fullDebug` y `prsOnlyDebug` con
  resultado `BUILD SUCCESSFUL`; `git diff --check` queda limpio.
- Durante la validación de despliegue, el A56 (`RZGYC07H0EX`) y el Watch 2
  (`192.168.1.56:5555`) figuraron conectados. La APK completa quedó restaurada
  e instalada en el A56 y la orientación se confirmó como
  `SCREEN_ORIENTATION_LANDSCAPE`.
- En la re-comprobación posterior al reinicio del daemon ADB, el Watch 2 pudo
  reconectarse y el A56 no volvió a aparecer en ese corte. Queda como acción
  operativa de continuidad, sin impacto en el resultado de los builds.

La campaña física de P.R.S. y el ajuste de `PrsTuning.DEFAULT` quedan
cancelados por decisión expresa del propietario. La futura integración de
movimiento/orientación tampoco forma parte del Sprint 016. El resto de temas
abiertos queda trasladado a una futura autorización. El Sprint 027 quedó
cerrado el 2026-09-01 y el Sprint 028 queda registrado como último Sprint
cerrado; ese registro corresponde a un momento anterior al Sprint 031.

## Addendum de cierre de SuriOS Watch y del AVD — 2026-08-30

La auditoría de esta terminal queda registrada en
[AUDIT_SPRINT_016.md](../AUDIT_SPRINT_016.md). El AVD adicional
`Galaxy_Watch_Ultra_2025` permanece disponible junto a los AVD anteriores y la
esfera OFICIAL `com.suri.surioswatch` queda validada en `v1.1` sobre Wear OS 5 /
API 34, 480 x 480, 320 dpi, Play Store y x86_64.

La esfera se dejó visible en `emulator-5554` con el marco circular, emblema,
progreso de pasos, batería sin porcentaje, firma `v1.1` y accesos CAPS/STATUS/
RADIO. La instalación en el Watch Ultra físico quedó validada posteriormente
en Sprint 026. La integración Orca-QGIS permanece como continuidad futura.

## Referencias

- [Sprint 014 v1.0](SPRINT_014_v1.0.md)
- [Sprint 015 v1.0](SPRINT_015_v1.0.md)
- [Sprint 016 v1.1](SPRINT_016_v1.1.md)
- [Sprint 017 v1.0](SPRINT_017_v1.0.md) — cerrado
- [Auditoría Sprint 016](../AUDIT_SPRINT_016.md)
- [Auditoría Sprint 017](../AUDIT_SPRINT_017.md)
- [Auditoría Sprint 015](../AUDIT_SPRINT_015.md)
- [Sprint 013 v1.0](SPRINT_013_v1.0.md)
- [Sprint 012 v1.0](SPRINT_012_v1.0.md)
- [Sprint 011 v1.0](SPRINT_011_v1.0.md)
- [User Guide](../USER_GUIDE.md)
- [SPRINT_HISTORY v1.3](<SPRINT_HISTORY v1.3.md>)
- [PROJECT_GUIDE v1.1](../PROJECT_GUIDE/PROJECT_GUIDE_v1.1.md)
- [EDL](../EDL/EDL.md)
- [MRPD](../MRPD/MRPD.md)
