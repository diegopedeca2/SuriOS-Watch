# Sprint 005 — Implementación de MORSE TERMINAL

---

document: SPRINT
sprint: 005
version: 1.0
project: PIP-SuriOS
type: Funcional
document_status: Aprobado y cerrado
implementation_status: Completado
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-09

---

## 1. Objetivo

Implementar el módulo `COMMS // MORSE TERMINAL` dentro de PIP-SuriOS, respetando la arquitectura Compose existente, la identidad visual EDL y la funcionalidad previa de COMMS.

## 2. Funcionalidades implementadas

- Nuevo selector `COMMS SELECT MODE`.
- Separación entre `FREQUENCIES` y `MORSE`.
- Conservación íntegra de la tabla PMR existente en `FREQUENCIES`.
- Selector `COMMS // MORSE TERMINAL`.
- Conversión offline `TEXT > MORSE`.
- Conversión offline `MORSE > TEXT`.
- Soporte inicial para A-Z y 0-9 mediante un codec ampliable.
- Sintaxis Morse manual con `.` para punto, `-` para raya, `_` para separación entre letras y `__` para separación entre palabras.
- Transmisión real mediante linterna con `TRANSMIT // FLASH`.
- Estado `TRANSMITTING` y botón `STOP`.
- Controles `CLEAR` y `DELETE` en ambos modos de entrada.
- Cancelación inmediata, exclusión de transmisiones simultáneas y apagado seguro del flash al detener, limpiar o abandonar la pantalla.
- Modo inmersivo con barra superior oculta y navegación inferior conservada.
- Activity vertical exclusiva para `TEXT > MORSE` con teclado Android.
- Temporización Morse basada exclusivamente en una constante centralizada y configurable.
- Secuencia de carga ampliada con `LOG-IN ID: SURI-14 VERIFIED` antes de `SYSTEM READY`.

## 3. Arquitectura

La navegación horizontal permanece integrada en `MainActivity` mediante el enum de destinos y `Crossfade` existentes.

Se añadieron pantallas Compose para:

- selector de modo COMMS;
- selector de modo Morse;
- entrada `MORSE > TEXT`;
- salida `MORSE > TEXT`.

`TEXT > MORSE` utiliza `TextToMorseActivity`, declarada como no exportada y fijada en orientación vertical. Esta separación evita recrear la navegación horizontal principal y aísla el teclado y el ciclo de vida de la transmisión.

El control del flash utiliza `CameraManager.setTorchMode()` y selecciona una cámara con flash disponible. No se añadió permiso de cámara porque no se abre la cámara ni se capturan imágenes.

El modo inmersivo utiliza `WindowInsetsController`, `WindowInsets.Type.statusBars()` y revelado transitorio mediante gesto. La ocultación se reaplica al recuperar el foco.

## 4. Seguridad del flash

- Solo puede existir una transmisión activa.
- `STOP` cancela la coroutine y apaga inmediatamente la linterna.
- `CLEAR` cancela una transmisión activa antes de limpiar el estado.
- La salida de Compose, `onStop()`, `onDestroy()` y el bloque `finally` fuerzan el apagado.
- Los dispositivos sin flash no permiten iniciar la transmisión.

## 5. Validaciones

Validación superada en:

- Samsung Galaxy A56.
- Pixel 8 Emulator.

Se verificaron:

- navegación completa de COMMS;
- tabla `FREQUENCIES` sin regresiones;
- conversión Texto → Morse;
- conversión Morse → Texto;
- soporte A-Z y 0-9;
- teclado Android y orientación vertical;
- transmisión real mediante flash;
- `STOP` y apagado seguro;
- salida durante transmisión;
- ausencia de transmisiones simultáneas;
- `CLEAR` y `DELETE`;
- reset de la entrada al regresar desde OUTPUT;
- modo inmersivo y conservación de la navegación inferior;
- retorno estable desde Activities y aplicaciones externas;
- pruebas unitarias del codec;
- `:app:assembleDebug`;
- compilación incremental;
- `:app:lintAnalyzeDebug`;
- `git diff --check`.

## 6. Archivos funcionales

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/suri/pipsurios/MainActivity.kt`
- `app/src/main/java/com/suri/pipsurios/TextToMorseActivity.kt`
- `app/src/main/java/com/suri/pipsurios/morse/MorseCodec.kt`
- `app/src/main/java/com/suri/pipsurios/morse/MorseTransmitter.kt`
- `app/src/main/java/com/suri/pipsurios/ui/screens/CommsMorseScreens.kt`
- `app/src/main/java/com/suri/pipsurios/ui/screens/LoadingScreen.kt`
- `app/src/test/java/com/suri/pipsurios/MorseCodecTest.kt`

## 7. Commit técnico

`84a2c42e6a82f2fa1a3b863deff0eb9b362f60ba` — **Sprint 005 - Implementación de MORSE TERMINAL**

## 8. Roadmap y cierre

MORSE TERMINAL queda implementado dentro del módulo COMMS.

- Sprint 005 queda cerrado.
- No existe Sprint activo.
- Sprint 006 de PIP-SuriOS no se ha iniciado.
- No se modificaron PIW-SuriOS ni la watchface.

## 9. Historial

| Versión | Estado | Descripción |
|---|---|---|
| 1.0 | Aprobada y cerrada | Documento completo de implementación, validación y cierre de MORSE TERMINAL. |
