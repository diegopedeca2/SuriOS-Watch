# Sprint 028 — Versión 2.7 y estética común de terminal

---
document: SPRINT
project: SuriOS Ecosystem
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-09-01
close_date: 2026-09-01
predecessor: Sprint 027
---

## Estado

El Sprint 027 está cerrado. El Sprint 028 se ha cerrado el 2026-09-01 tras
completar el alcance visual y recibir validación física en el Samsung A56.

## Objetivo

Actualizar la firma de versión y aplicar una línea visual común inspirada en
una terminal, sin cambiar el funcionamiento de las pantallas existentes.

## Alcance

### 1. Firma de versión

- Actualizar la firma visible de `PIP-SuriOS v2.6` a `PIP-SuriOS v2.7`.
- Alinear el `versionName` técnico con `2.7` y aumentar el `versionCode`.

### 2. Estética común de terminal

- Aplicar una misma base visual a la pantalla de inicio, el lector ficticio de
  huellas, `LOADING` y `HOMESCREEN`.
- Usar fondo oscuro, retícula sutil, líneas de exploración y marcos técnicos
  coherentes con la identidad PIP-BOY/terminal.
- Extender la misma estética y el emblema al resto de menús y submenús de la
  aplicación. En MAP se excluyen TERRAIN y las pantallas de mapas externas;
  en P.R.S. se excluyen la carga, el seguimiento, DEVICES y la guía.
- Aplicar también la estética al selector de modo de MAP, a `MAP - OPERATION`
  y al primer selector de P.R.S.
- Girar automáticamente el emblema en las pantallas verticales.
- Mantener los textos funcionales, las acciones, la navegación y las
  animaciones ya existentes.
- Mantener en identificación únicamente el texto `IDENTIFICATION` y el
  comportamiento ficticio del lector.

## Progreso actual — 2026-09-01

- La firma visible se ha actualizado a `PIP-SuriOS v2.7` en las pantallas de la
  aplicación y el build técnico queda en `versionName 2.7`, `versionCode 7`.
- Se ha creado una base visual compartida con retícula, línea de exploración y
  marcos técnicos para inicio, identificación, `LOADING` y `HOMESCREEN`.
- La APK `fullDebug` se ha instalado y arrancado en el Samsung A56.
- La comprobación visual confirma el flujo de inicio, identificación, `LOADING`
  y llegada a `HOMESCREEN`.
- La decoración común se ha aplicado a los selectores solicitados de MAP y
  P.R.S.; las pantallas operativas de mapas y P.R.S. siguen sin cambios
  visuales.
- Tests unitarios, lint y ensamblados `fullDebug`/`prsOnlyDebug` han terminado
  correctamente.

## Fuera de alcance

- Cambiar la lógica de navegación o los tiempos de arranque.
- Introducir lectura biométrica real o almacenamiento de datos personales.
- Modificar la lógica funcional de los menús operativos posteriores a
  `HOMESCREEN`.

## Criterios de aceptación

- La firma visible principal y el `versionName` técnico muestran `2.7`.
- Las cuatro pantallas comparten la misma base estética de terminal.
- El lector continúa llevando a `LOADING` al pulsarlo.
- `LOADING` conserva sus tiempos, textos, escritura progresiva y decisión de
  apertura de `SET-UP OPERATOR` cuando no hay usuario configurado.
- `HOMESCREEN` conserva todos sus accesos y acciones.
- Tests, lint y ensamblado de la variante móvil terminan correctamente.

## Cierre

El Sprint 028 se cierra el 2026-09-01 tras completar el alcance autorizado.

La firma `PIP-SuriOS v2.7` queda aplicada junto con la base visual de terminal
en los menús incluidos. Se validaron físicamente en el Samsung A56 el selector
de modo de MAP, `MAP - OPERATION`, el selector inicial de P.R.S. y `SET-UP` en
orientación vertical, comprobando también el giro de 90° del emblema en esta
última.

Las pantallas excluidas de MAP y P.R.S. permanecen sin la carcasa visual
común. La navegación y las funciones existentes se conservan. Tests unitarios,
lint y ensamblados `fullDebug`/`prsOnlyDebug` terminaron correctamente.

No se abre un Sprint posterior en este cierre.
