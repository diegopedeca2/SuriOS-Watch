# Auditoría Sprint 046 - Refinamiento de la esfera Ely

---
document: AUDIT
project: SuriOS Watch / Ely
sprint: 046
closure_status: CLOSED_DOCUMENTALLY
play_console_status: ACCOUNT_CREATED_PENDING_GOOGLE_VALIDATION
publication_status: PENDING
submission_status: PENDING
version: 1.0
date: 2026-10-24
status: CERRADO DOCUMENTALMENTE; publicacion y envio en Play pendientes
watchface_name: Ely
watchface_package: com.suri.surioswatch.ely
watchface_version: 2.0
watchface_version_code: 5
watchface_format: 2
watch_model: SM-L705F
wear_os_api: 36
adb_endpoint: 192.168.1.63:39459
---

## Resultado

La reconstrucción de ELY v2.0 está técnicamente operativa: el módulo
independiente compila, pasa Lint, está instalado y se muestra activo en el
Galaxy Watch Ultra. El fondo nuevo elimina los valores históricos de la
referencia anterior y los dos libros tienen zonas de toque transparentes. No se
detecta un bloqueo de compilación, instalación, registro WFF o interacción.

## Evidencia revisada

| Comprobación | Resultado |
|---|---|
| Módulo | `watch/watchfaceely`, separado de `com.suri.surioswatch` |
| ADB solicitado | `192.168.1.63:39459` responde en estado `device` |
| Dispositivo | Galaxy Watch Ultra `SM-L705F`, Wear OS API 36 |
| Pantalla | `480x480`, densidad física `340` |
| Paquete | `com.suri.surioswatch.ely` instalado |
| Versión | `versionName 2.0`, `versionCode 5`, `minSdk 34`, `targetSdk 37` |
| Compilación | `:watchfaceely:assembleDebug`: correcto |
| Lint | `:watchfaceely:lintDebug`: correcto |
| XML | `watchface.xml` parseado correctamente; recurso `ely_background`, fuente personalizada y parámetros dinámicos presentes |
| Datos dinámicos | Hora, fecha, batería y pasos definidos en WFF |
| Fondo | `ely_background.png` sustituido por `PICTURES/WATCHFACE_REFERENCES/ely 2.png` |
| Tipografía | `cormorant_garamond_semibold.ttf`, Cormorant Garamond SemiBold, aplicada a los datos numéricos |
| Wallet | Zona transparente sobre el libro izquierdo; abre Google Wallet y muestra el aviso de NFC del reloj |
| Música | Zona transparente sobre el libro derecho; `Launch target="MUSIC_PLAYER"` resuelve el reproductor del reloj |
| Captura activa | ELY visible sin duplicados de hora, fecha, batería ni pasos de la imagen anterior |
| Ambiente | No concluyente: el dispositivo estaba en carga inalámbrica y la captura de la transición mostró la pantalla de carga |
| Whitespace | `git diff --check`: sin errores en los cambios controlados |

## Evidencia de release

- AAB firmado: `watchfaceely-release.aab`.
- `lintRelease`: correcto.
- Firma: verificada con `jarsigner`.
- WFF v2: validado con el validador oficial.
- Memoria oficial: `maxActiveBytes 1861232` y `maxAmbientBytes 1861232`;
  ambos dentro de los limites de 100 MB y 10 MB.

## Hallazgos pendientes

1. **ELY-046-03 — Ambiente sin validación física completa.** El bloque
   `ely_ambient` está implementado en WFF, pero la prueba física de hoy no pudo
   observarlo porque el reloj estaba en el cargador. Hay que repetirla fuera de
   carga y comprobar transición, legibilidad y duración.

2. **ELY-046-04 — Indicador de batería pendiente de validación visual.** La
   implementación usa `[BATTERY_PERCENT]` para variar el ancho del relleno, pero
   aún falta observarlo en niveles bajo, medio y alto y confirmar que la lectura
   visual se mantiene correcta.

3. **OPS-046-01 — Trazabilidad de entrega.** `watch/watchfaceely`, los assets de
   ELY y la documentación de los Sprints 045/046 aparecen todavía como cambios
   no versionados en el árbol de trabajo. No es un fallo del APK instalado,
   pero impide considerar la entrega reproducible hasta preparar el commit.

## Pendientes de distribucion

4. **REL-046-01 - Validacion de cuenta Play Console.** La cuenta ha sido
   creada y queda pendiente de validacion por Google.

5. **REL-046-02 - Publicacion y envio.** La aplicacion esta preparada con su
   AAB firmado, pero la creacion de la pista, el envio del artefacto y la
   publicacion quedan pendientes de ejecutar en Play Console.

## Cierre documental

- Sprint: `046`.
- Estado: `CLOSED` documentalmente.
- Entrega tecnica: ELY WFF v2.0, `versionCode 5`.
- Deudas no bloqueantes: ambiente, bateria y trazabilidad Git.
- Bloqueo externo: validacion de la cuenta de Google Play Console.
- Proximo paso: publicar y enviar la app cuando Google valide la cuenta.

## Notas operativas

- `adb devices -l` muestra tanto el endpoint explícito `192.168.1.63:39459`
  como un anuncio mDNS del mismo reloj. Las comprobaciones de este sprint se
  ejecutaron siempre con `-s 192.168.1.63:39459`.
- El acceso `Music` no fija Spotify en el XML; Spotify es únicamente el
  reproductor predeterminado que resolvió el reloj durante la prueba.
- No se modificó la esfera oficial `com.suri.surioswatch` durante esta
  auditoría.

## Decision de cierre

El Sprint 046 queda cerrado documentalmente. La publicacion y el envio de la
app permanecen pendientes de la validacion de la cuenta de Google Play Console.

## Decisión

El Sprint 046 queda **CLOSED DOCUMENTALLY** con la reconstruccion v2.0
desplegada y validada. Permanecen abiertos como deudas no bloqueantes la
validacion prolongada de ambiente, la bateria y la trazabilidad final en Git.
La validacion de Play Console, la publicacion y el envio de la app quedan
pendientes por dependencia externa.
