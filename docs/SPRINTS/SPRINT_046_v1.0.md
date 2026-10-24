# Sprint 046 - SuriOS Watch: refinamiento visual y verificación de ELY

---
document: SPRINT
project: SuriOS Watch / Ely
version: 1.0
status: CLOSED
closure_date: 2026-10-24
closure_type: DOCUMENTAL
play_console_status: ACCOUNT_CREATED_PENDING_GOOGLE_VALIDATION
publication_status: PENDING
submission_status: PENDING
owner: Diego Perez de Camino
date: 2026-10-24
predecessor: Sprint 045
watchface_name: Ely
watchface_package: com.suri.surioswatch.ely
watchface_version: 2.0
watchface_version_code: 5
watchface_format: 2
watch_device: Galaxy Watch Ultra
watch_model: SM-L705F
wear_os_api: 36
adb_endpoint: 192.168.1.63:39459
---

## Objetivo

Abrir una iteración específica de ELY para eliminar los restos visuales de la
referencia estática, preparar una preview coherente y completar la validación
del modo ambiente en el Galaxy Watch Ultra. La esfera oficial
`com.suri.surioswatch` queda fuera del alcance.

## Auditoría inicial

- [x] Confirmar que `watch/watchfaceely` es un módulo independiente de la esfera
      oficial.
- [x] Confirmar que `192.168.1.63:39459` responde por ADB como `SM-L705F`.
- [x] Confirmar pantalla física `480x480`, Wear OS API 36 y estado `device`.
- [x] Confirmar Ely instalada como `com.suri.surioswatch.ely`, versión `2.0`,
      `versionCode 5`, `minSdk 34` y `targetSdk 37`.
- [x] Ejecutar `:watchfaceely:assembleDebug` y `:watchfaceely:lintDebug`.
- [x] Sustituir el fondo por `PICTURES/WATCHFACE_REFERENCES/ely 2.png` y
      eliminar la composición anterior.
- [x] Integrar Cormorant Garamond SemiBold para hora, fecha, batería y pasos.
- [x] Crear los recuadros táctiles transparentes sobre ambos libros.
- [x] Probar Wallet; el reloj abre Google Wallet y muestra su aviso de NFC.
- [x] Probar `Music`; el reloj resuelve el reproductor mediante
      `MUSIC_PLAYER`.
- [x] Confirmar la captura real de ELY activa y revisar la referencia frente a
      la composición renderizada.
- [x] Revisar la implementación XML, los recursos y el manifest WFF 2.
- [ ] Completar una prueba de ambiente fuera del cargador y durante una sesión
      prolongada. La prueba de hoy quedó condicionada por la pantalla de carga
      inalámbrica del dispositivo.

## Pendientes del sprint

- [x] Limpiar la imagen base: `ely 2.png` no contiene valores históricos ni
      texto `SPOTIFY`.
- [x] Mantener la preview del selector apuntando al nuevo `ely_background` limpio.
- [x] Validar que los recuadros táctiles no dibujan bordes ni rellenos visibles.
- [ ] Validar visualmente la esfera en modo ambiente con el reloj fuera del
      cargador, incluyendo legibilidad, transición y estabilidad prolongada.
- [ ] Validar visualmente el llenado del icono de batería en varios niveles,
      incluyendo batería baja, media y alta.
- [ ] Versionar y documentar el estado final de ELY antes del cierre; el módulo,
      sus recursos y la documentación del Sprint 045 siguen fuera de `HEAD` en
      el momento de abrir este sprint.

## Release y distribucion

- [x] Preparar la variante `release`, la firma de subida y el AAB para Play
      Console.
- [x] Ejecutar Lint de release, validacion WFF v2 y evaluacion oficial de
      memoria.
- [ ] Esperar la validacion de la cuenta de Google Play Console.
- [ ] Publicar la prueba interna y enviar la aplicacion a Google Play.

## Criterio de cierre

Nota de cierre documental: el Sprint 046 se cierra con la esfera activa y
validada, el release listo y la distribucion externa pendiente. La publicacion
y el envio quedan fuera del cierre porque dependen de la validacion de Google.

El Sprint 046 se cierra documentalmente con la esfera activa y validada, la
preview coherente, la compilación y Lint correctos, los accesos Wallet/Music
operativos y el release listo para Play Console. La publicación y el envío
quedan fuera del cierre porque dependen de la validación externa de Google.

Las validaciones físicas de ambiente/batería y la trazabilidad final en Git se
conservan como deuda técnica no bloqueante para el siguiente ciclo.

## Cierre documental

- Estado: `CLOSED`.
- Entrega: ELY WFF v2.0, `versionCode 5`.
- Artefacto: `watchfaceely-release.aab`, firmado y verificado.
- Validaciones: `lintRelease`, WFF v2 y memoria oficial superadas.
- Cuenta Play Console: creada, pendiente de validación por Google.
- Publicación: pendiente.
- Envío de la app: pendiente.
