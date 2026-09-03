# PIP-SuriOS — Guía de usuario

Versión de la aplicación: **2.9**
Esta guía explica las funciones principales de PIP-SuriOS con palabras
sencillas. Los nombres de los botones se mantienen como aparecen en pantalla.

## Primer arranque

Al abrir la aplicación se muestra la pantalla de carga y después la pantalla de
identificación. Completa el `ID` del operador para continuar. El `ID` identifica
al operador en la aplicación; no es necesario introducir un `CALLSIGN` aparte.

Si Android solicita permisos, acepta los permisos necesarios para Bluetooth y
ubicación. Algunas herramientas no pueden funcionar si esos permisos están
desactivados.

## Pantalla principal

Desde `HOME OPERATION` puedes abrir:

- `INVENTORY`: consulta el inventario organizado por categorías.
- `DATA`: revisa datos guardados, estadísticas y registros.
- `CURRENT GEAR`: consulta y modifica el equipo activo.
- `SET-UP`: introduce o modifica los datos personales y del equipo.
- `STATUS`: consulta el estado y los recordatorios configurados.
- `TOOLS`: abre las herramientas de comunicación, mapa, P.R.S. y RADS.
- `INFORMATION`: consulta la información del proyecto y los agradecimientos.

Usa `< BACK` para volver a la pantalla anterior. Si un texto no cabe en la
pantalla, desliza verticalmente dentro del panel correspondiente.

## SET-UP

`SET-UP` permite guardar la configuración del operador y del equipo. Los
cambios se guardan cuando pulsas `APPLY` o cuando confirmas la pantalla que lo
solicita.

### Operator

En `SET-UP OPERATOR` se introduce el `ID` del operador. El campo `CALLSIGN` ya
no forma parte de este formulario porque sería redundante.

### Primary y secondary weapon

Estas dos secciones son listas creadas por el usuario:

1. Escribe el rol o tipo en el primer cuadro.
2. Escribe el modelo en el segundo cuadro.
3. Pulsa `APPLY`.

La combinación se guarda como una opción. Puedes repetir el proceso para
añadir más réplicas. En `SAVED OPTIONS` aparecen los nombres concretos de las
opciones guardadas, no etiquetas genéricas como `OPTION 1`.

Cada opción guardada puede editarse o borrarse. Si borras la opción que estaba
activa, el equipo vuelve a indicar que no hay una opción seleccionada.

### Accesories

En `SET-UP ACCESORIES` escribe el nombre de un accesorio y pulsa `APPLY`. El
accesorio queda guardado y el cuadro se vacía automáticamente para introducir
otro. Las opciones guardadas se pueden editar o borrar.

### Headgear

`HEADGEAR` tiene dos pasos:

1. Escribe el nombre del conjunto y pulsa `APPLY`.
2. Escribe cada componente en el cuadro de texto y pulsa `APPLY` para añadirlo.

Los componentes guardados aparecen como textos editables y también se pueden
borrar.

### Front panel y uniform

Escribe el equipo particular en el cuadro de texto y pulsa `APPLY`. Cada texto
se añade a su lista personalizable. Puedes editar o borrar los elementos
guardados.

### Data

Dentro de `SET-UP DATA` se encuentran los datos guardados, incluidos los
submenús de listas personalizables y réplicas de armas. Desde ahí puedes
revisar, editar o borrar las opciones sin volver a crearlas.

## CURRENT GEAR

`CURRENT GEAR` muestra el equipo que está activo en este momento. Las armas
primaria y secundaria aparecen con sus nombres concretos; no se muestran las
categorías fijas antiguas. Los accesorios, el `HEADGEAR`, el `FRONT PANEL` y el
uniforme se leen de las listas personalizadas de `SET-UP`.

## DATA y operaciones

`DATA` permite consultar estadísticas y registros de operaciones. Las
estadísticas se calculan a partir de los datos guardados. `DATA LOG` conserva
el historial de operaciones y permite abrir sus detalles.

Antes de cerrar una operación, revisa la fecha, la ubicación, los consumibles
y el equipo activo. Usa `SAVE` para conservarla o `DELETE` solo cuando quieras
eliminarla de forma deliberada.

## TOOLS

### P.R.S.

`P.R.S.` es el sistema de proximidad de PIP-SuriOS. El menú actual tiene cuatro
opciones:

#### SENTRY

`SENTRY` sirve para vigilancia general:

- `PIP` utiliza el Bluetooth del A56.
- `PIP + PROBE` utiliza el A56 y el Watch 2 PROBE.

Los dos modos muestran todos los nodos BLE detectados. No permiten elegir un
dispositivo concreto ni iniciar un seguimiento individual. La lista muestra
la señal en bruto (`RAW`), una señal suavizada (`SMOOTH`), la banda relativa de
proximidad y la tendencia cuando existen suficientes muestras.

#### TRACKER

`TRACKER` conserva el flujo de seguimiento sobre mapa. Sus dos modos mantienen
los nombres originales:

- `ONLY PIP-BOY`
- `PIP-BOY + PROBE`

Primero selecciona la ubicación del terreno. Después identifica el objetivo
entre los dispositivos detectados. El segundo paso muestra el `GRID` sobre el
mapa. El resultado es una estimación relativa de densidad; no debe interpretarse
como una coordenada exacta ni como una distancia en metros.

Para guardar un dispositivo en `TRACKER` y poder rastrearlo, vincula previamente
los dos dispositivos que van a participar en la sesión. Comprueba la conexión
antes de guardar el objetivo; si no están vinculados, el seguimiento puede no
iniciarse o no recibir las muestras esperadas.

#### DEVICES

`DEVICES` sirve para identificar y guardar dispositivos que deben omitirse.
Puedes consultar dispositivos detectados, guardar reglas, desactivarlas,
volver a activarlas o eliminarlas.

Las reglas activas se aplican automáticamente tanto a `SENTRY` como a
`TRACKER`. Antes de guardar una regla, compara el nombre BLE, el identificador
o la dirección observada. El RSSI solo ayuda a comparar señales y no debe ser
el único dato utilizado para identificar un equipo.

#### USER GUIDE

Esta pantalla contiene una explicación breve del P.R.S. y de sus límites de
uso. Puedes volver al menú anterior con `< BACK`.

### Comms

`COMMS` permite consultar las frecuencias configuradas y utilizar las
funciones de Morse. Introduce el texto en el campo disponible y sigue las
indicaciones de la pantalla para convertirlo.

### Map

`MAP` abre las funciones de mapa y navegación disponibles sin conexión. La
posición y la precisión dependen de los sensores y permisos del dispositivo.

### RADS

`RADS` abre el contador Geiger cuando el hardware compatible está disponible.
Los botones de volumen pueden utilizarse para controlar la función si la
pantalla lo indica.

## P.R.S. y permisos

Para `PIP` se necesitan Bluetooth y los permisos de escaneo y conexión. Para
`PIP + PROBE` también debe estar disponible el Watch 2 emparejado mediante
Wear OS/Data Layer. Si el estado indica un error, activa Bluetooth, revisa los
permisos y pulsa `TRY AGAIN` o `RETRY`.

La señal BLE cambia por obstáculos, orientación, personas y otros emisores.
Es normal que un nodo aparezca, desaparezca o cambie de tendencia. Espera a
tener varias muestras antes de sacar conclusiones.

## Information

`INFORMATION` contiene la versión, el estado del proyecto, los avisos y los
agradecimientos. La sección de testers agradece a FENRIR, CHECHU y ALTAMIRA su
atención al proyecto y que sean las primeras personas en probar la aplicación
y aportar feedback.

## Solución rápida de problemas

- Si no puedes pasar de la identificación, completa el `ID` y vuelve a guardar.
- Si P.R.S. no detecta nodos, activa Bluetooth y concede los permisos.
- Si `PIP + PROBE` no conecta, comprueba que el Watch 2 esté encendido,
  emparejado y disponible.
- Si falta un equipo esperado, revisa `DEVICES` por si existe una regla activa
  que lo esté omitiendo.
- Si una lista parece vacía, pulsa `APPLY` después de escribir y revisa la
  sección `SAVED OPTIONS` o el submenú de datos correspondiente.
