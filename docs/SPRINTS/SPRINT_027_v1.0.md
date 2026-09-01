# Sprint 027 — Limpieza del proyecto y detalles menores

---
document: SPRINT
project: SuriOS Ecosystem
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-08-31
close_date: 2026-09-01
predecessor: Sprint 026
---

## Estado

El Sprint 026 está cerrado. El Sprint 027 se ha cerrado el 2026-09-01 y se
dedica a limpieza documental, pequeños ajustes de coherencia y retorno al
diseño original de PIP-SuriOS.

## Objetivo

Dejar el proyecto fácil de entender y alineado con su estado real, eliminando
referencias antiguas o rotas y aclarando las deudas técnicas que se conservan.

## Alcance

### 1. BACKLOG

- Explicar `BL-005` con lenguaje sencillo y mantenerlo planificado: es una
  futura comprobación automática de los XML de las watchfaces.
- Eliminar `BL-014`, ya que no hay previsión de distribución externa a medio
  plazo.
- Marcar `AUD-023-08` como implementado, porque sus dependencias y validaciones
  se cerraron en Sprint 025.
- Explicar la deuda de `SYNC_TO_DEVICE` sin resolverla en este Sprint.

### 2. Documentación

- Corregir o retirar enlaces desactualizados o inexistentes en
  `PROJECT_GUIDE_v1.1.md`.
- Actualizar `WFPRD_v1.5.md` para reflejar que el emblema, Spotify y Google
  Wallet forman parte del estado actual de SuriOS Watch.
- Sincronizar `ACTIVE_SPRINT.md` y `SPRINT_HISTORY v1.3.md` para identificar
  Sprint 027 como cerrado y sin abrir un Sprint posterior.

### 3. Diseño original

- Mantener únicamente la identidad visual original de PIP-SuriOS.
- Retirar la navegación, paletas, emblemas, recursos, pruebas y textos de
  identidades visuales que ya no forman parte del proyecto.
- Hacer que el arranque llegue a `HOMESCREEN` tras la identificación y
  `LOADING`, sin selector de identidad visual.

### 4. Identificación de arranque

- Insertar una pantalla intermedia con un lector visual ficticio y el texto
  `IDENTIFICATION`.
- Dar al lector una presentación visual de terminal PIP-BOY, con marco técnico,
  retícula, anillos y línea de escaneo animada, sin añadir textos nuevos.
- Continuar a `LOADING` al pulsar el lector, sin usar lectura biométrica,
  permisos ni almacenamiento de datos personales.
- Escribir las líneas de `LOADING` con efecto de máquina de escribir y usar el
  ID de `SET-UP - OPERATOR` en el inicio y el cierre del menú.
- Si no existe un ID de operador, mostrar `PLEASE SET YOUR USER` y abrir
  `SET-UP - OPERATOR` al terminar la secuencia.

### 5. Cartografía TERRAIN

- Parametrizar el generador HOME-style para reutilizarlo con diferentes mapas,
  fuentes y capas opcionales.
- Crear el mapa offline `OFFICE`, centrado en
  `40.43717182620207, -3.620425636696507`, con edificios y carreteras reales
  de OpenStreetMap.
- Incorporar `OFFICE` al catálogo de `MAP - TERRAIN` sin cambiar las acciones
  de edición, GPS, brújula ni overlays existentes.

### 6. STATUS

- Reorganizar visualmente STATUS alrededor de una silueta T-45 provisional,
  conectando HEADGEAR, PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES, FRONT
  PANEL y UNIFORM con sus zonas correspondientes.
- En la pantalla principal, mostrar únicamente la pieza elegida o `N/A` si no
  está configurada; mantener solo la palabra `ACCESORIES` como elemento
  pulsable de acceso al detalle, sin recuadro ni valor adicional.
- Hacer que únicamente `ACCESORIES` abra un submenú de detalle.
- Mantener sin cambios las rutas y funciones de `DON'T FORGET`, `INPUT
  OPERATION`, `LOG` y `STATISTICS`.

### 7. P.R.S. externo del Z Flip 6

- Reorganizar la edición `prsOnlyDebug` en los tres menús `SCAN`, `GRID` y
  `DEVICES`.
- Hacer que `SCAN` muestre la lista completa de dispositivos detectados y
  conserve el seguimiento individual.
- Hacer que `GRID` muestre únicamente el GRID, ocupando el máximo espacio
  posible y sin marco exterior; el objetivo seleccionado en `SCAN` debe
  reflejarse en él.
- Mantener sin cambios la funcionalidad interna de `DEVICES`.
- Hacer que BACK vuelva al menú anterior y adaptar los márgenes a la pantalla
  exterior para que no se corten líneas ni controles.

## Fuera de alcance

- Cambios en módulos operativos no relacionados con el arranque y la identidad
  visual original.
- Implementar la automatización de `BL-005`.
- Sustituir `SYNC_TO_DEVICE` por otra fuente.
- Preparar una distribución pública o modificar las condiciones de uso del
  emblema.
- Reescribir documentos históricos conservados en `docs/SPRINTS/OLD/`.

## Criterios de aceptación

- No quedan enlaces rotos en los documentos canónicos revisados.
- El BACKLOG refleja únicamente temas vigentes y comprensibles.
- El WFPRD describe correctamente los componentes actuales de SuriOS Watch.
- `ACTIVE_SPRINT.md` y `SPRINT_HISTORY v1.3.md` señalan Sprint 027 como cerrado
  y no señalan ningún Sprint posterior como activo.
- No se modifican los módulos operativos existentes.
- `OFFICE` aparece en `MAP - TERRAIN`, carga su MBTiles íntegro y queda
  centrado en las coordenadas solicitadas.
- STATUS muestra el nuevo esquema visual, conserva `N/A` y abre el detalle de
  ACCESORIES sin alterar DON'T FORGET ni DATA.
- `prsOnlyDebug` ofrece un menú principal con `SCAN`, `GRID` y `DEVICES`;
  `SCAN` conserva todos los dispositivos detectados y el seguimiento elegido
  se refleja en el GRID, que no muestra marco exterior.
- `git diff --check` no detecta errores de espacios ni de formato; los cambios
  del Sprint quedan preparados para su revisión o commit posterior.

## Cierre

El Sprint 027 se cierra el 2026-09-01 tras completar el alcance autorizado y
recibir la confirmación física del propietario sobre la pantalla exterior del
Z Flip 6.

La validación física confirma que `prsOnlyDebug` ofrece el menú principal
`SCAN`, `GRID` y `DEVICES`; `SCAN` muestra los dispositivos detectados, `GRID`
presenta la visualización sin marco y `DEVICES` conserva sus controles visibles.
BACK vuelve al menú anterior sin cerrar la aplicación.

También quedan validados el lector ficticio de identificación con estética
PIP-BOY, `LOADING`, el mapa `OFFICE`, la reorganización visual de `STATUS` y
las correcciones documentales del Sprint. Las comprobaciones automatizadas de
`prsOnlyDebug` terminaron correctamente. En el momento de este cierre no se
abre un Sprint nuevo; posteriormente se autoriza la apertura del Sprint 028.
