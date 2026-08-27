# PIP-SuriOS - User Guide

Manual oficial de usuario — PIP-SuriOS v2.3

## Índice

1. [Introducción](#1-introducción)
2. [Inicio de la aplicación](#2-inicio-de-la-aplicación)
3. [HOME](#3-home)
4. [MAP](#4-map)
5. [COMMS](#5-comms)
6. [INVENTORY](#6-inventory)
7. [CURRENT GEAR](#7-current-gear)
8. [STATUS](#8-status)
9. [DATA](#9-data)
10. [TOOLS](#10-tools)
11. [Controles](#11-controles)
12. [Limitaciones conocidas](#12-limitaciones-conocidas)
13. [Historial de versiones](#13-historial-de-versiones)
14. [Consejos de uso](#14-consejos-de-uso)

## 1. Introducción

PIP-SuriOS es una aplicación móvil de apoyo operativo con una estética inspirada en terminales retrofuturistas. Reúne en una sola interfaz el inventario, la preparación del equipo, las comunicaciones, los accesos cartográficos, el estado del loadout y varias herramientas inmersivas.

Está pensada principalmente para utilizarse en un Samsung Galaxy A56 en orientación horizontal. La pantalla TEXT > MORSE utiliza orientación vertical para facilitar la escritura con el teclado.

La navegación interna, el inventario, CURRENT GEAR, STATUS, las conversiones Morse y RADS funcionan sin conexión. Algunas acciones dependen de funciones del sistema Android: MAP puede abrir aplicaciones externas, MORSE puede usar la linterna y P.R.S. v1.0/P.R.S. v2.0 necesitan Bluetooth y sus permisos correspondientes. La guía de campo de P.R.S. está disponible en `TOOLS > PROXIMITY RADIO SCANNER > OPERATION GUIDE`.

## 2. Inicio de la aplicación

Al abrir PIP-SuriOS aparece una secuencia de arranque automática:

1. `LOADING...`
2. `LOG-IN ID: SURI-14 VERIFIED`
3. Los módulos de HOME se inicializan en el orden SET-UP, CURRENT GEAR, INVENTORY, STATUS, DATA y TOOLS. Cada línea muestra primero `LOADING MÓDULO.....` y después `READY`.
4. `SYSTEM READY`
5. `SELECT SKIN`

Después puede elegirse una skin visual:

- **BROTHERHOOD OF STEEL:** skin funcional disponible; abre el HOME operativo y todas las funciones descritas en este manual.
- **SALAMANDER**, **IRON HAND**, **ADEPTUS MECHANICUS**, **NECRON** y **MANDALORIAN:** aparecen como `UNDER CONSTRUCTION`. Pulse `< BACK` para regresar a `SELECT SKIN`.

La selección no se recuerda al cerrar la aplicación. Las skins cambian únicamente la presentación; las funciones y los datos permanecen compartidos.

## 3. HOME

### HOMESCREEN

La pantalla `HOMESCREEN` es el punto principal de acceso. Contiene:

- **SET-UP:** configuración personal y preferencias de equipamiento.
- **CURRENT GEAR:** preparación del equipo utilizado en la partida más reciente.
- **INVENTORY:** consulta de armas, consumibles, equipamiento y complementos.
- **STATUS:** consulta del Loadout Activo y checklist DON'T FORGET.
- **DATA:** registro permanente, consulta, edición, borrado y estadísticas de operaciones.
- **TOOLS:** acceso a COMMS, MAP, PROXIMITY RADIO SCANNER y RADS.

MAP y COMMS se abren desde TOOLS. Algunos módulos muestran brevemente `LOADING...` antes de abrirse. Utilice `< BACK` para regresar al nivel anterior.

### SET-UP

SET-UP se abre en orientación vertical y se divide en dos submenús:

- **INPUT:** introducción de OPERATOR, PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES, HEADGEAR, FRONT PANEL y UNIFORM.
- **DATA:** consulta de los datos guardados, con `EDIT` y `DELETE` para cada campo disponible.

En **OPERATOR** se pueden introducir ID, NAME, CALLSIGN, NUMBER, COUNTRY y TEAM. En **PRIMARY WEAPON**, ROLE continúa siendo un selector desplegable y WEAPON es un campo de texto libre para escribir cualquier réplica o combinación personalizada. Los cambios se guardan al introducirlos y se reutilizan como base de CURRENT GEAR.

ACCESORIES incluye `WATCH 2` junto a los accesorios existentes. Su ficha de INVENTORY permanece informativa y marcada como `UNDER CONSTRUCTION`.

## 4. MAP

Desde HOMESCREEN, pulse **TOOLS > MAP** y elija uno de sus dos modos.

### MAP TERRAIN

Abre el mapa topográfico **NAVY7**, incluido completamente offline en la aplicación. El mapa no necesita conexión a Internet.

- Arrastre con un dedo para desplazarse.
- Use pinch con dos dedos para ampliar o reducir. No hay botones de zoom.
- La orientación heading-up gira el mundo cartográfico según la orientación del teléfono, mientras los textos y botones permanecen derechos.
- El marcador GPS muestra la posición actual cuando Android concede permiso y existe un fix disponible.
- **ADD RESPAWN** crea varios marcadores persistentes. Pulse uno, después `DELETE` y finalmente `CONFIRM` para eliminarlo.
- **ADD RAD ZONE** permite definir varios polígonos persistentes. Pulse una zona, después `CLEAR` y `CONFIRM` para eliminarla.
- Un doble toque sobre una zona despejada ofrece `EMPTY MAP`; la acción solo elimina RESPawns y RAD ZONES después de `CONFIRM`, nunca el mapa base.

Al aproximarse al borde de una RAD ZONE, la cadencia Geiger aumenta progresivamente. Dentro de la zona alcanza el nivel de exposición directa. El umbral aproximado y el GPS no constituyen una medición de precisión.

### MAP OPERATION

Abre `MAP - OPERATION`. Pulse **LAUNCH** para iniciar el acceso cartográfico operativo.

PIP-SuriOS comprueba si CivTAK está disponible:

- Si CivTAK está instalado, lo abre automáticamente.
- Si CivTAK no está disponible, intenta abrir Google Maps como alternativa.

La aplicación externa puede solicitar su propia configuración, conexión o permisos de ubicación. Al volver desde CivTAK o Google Maps, PIP-SuriOS conserva el flujo de navegación de MAP.

## 5. COMMS

Desde HOMESCREEN, pulse **TOOLS > COMMS**. Después de la pantalla de carga puede elegir **FREQUENCIES** o **MORSE**.

### PMR Frequencies

FREQUENCIES muestra una tabla informativa con los 16 canales PMR y su frecuencia correspondiente. Desplácese por la lista para consultar todos los canales.

La tabla sirve como referencia. PIP-SuriOS no transmite por radio ni cambia la configuración de un equipo PMR.

### MORSE TERMINAL

MORSE TERMINAL ofrece dos modos de conversión.

#### TEXT > MORSE

1. Escriba un mensaje mediante el teclado.
2. Pulse **ENTER** para convertir el texto.
3. Consulte la señal Morse generada.
4. Pulse **TRANSMIT // FLASH** para transmitirla mediante la linterna.

Durante la transmisión, la linterna reproduce los puntos, rayas y pausas del mensaje. Pulse **STOP** para cancelar inmediatamente y apagar el flash. Salir de la pantalla también detiene la transmisión y apaga la linterna.

Controles disponibles:

- **DELETE:** elimina el último carácter escrito.
- **CLEAR:** borra la entrada y el resultado.
- **ENTER:** realiza la conversión.
- **TRANSMIT // FLASH:** inicia la señal luminosa.
- **STOP:** interrumpe la transmisión activa.

Si el dispositivo no dispone de una linterna compatible, la transmisión aparece como no disponible.

#### MORSE > TEXT

1. Introduzca la señal mediante los botones de punto, raya y separación.
2. Use **DELETE** para corregir el último símbolo o **CLEAR** para empezar de nuevo.
3. Pulse **CONVERT** para mostrar el texto resultante.

La conversión admite letras A-Z y números 0-9.

#### Temporización Morse

La transmisión luminosa sigue las proporciones estándar de Morse:

- un punto dura una unidad;
- una raya dura tres unidades;
- la pausa entre símbolos dura una unidad;
- la pausa entre letras dura tres unidades;
- la pausa entre palabras dura siete unidades.

## 6. INVENTORY

Desde HOMESCREEN, pulse **INVENTORY**. El módulo se divide en tres apartados.

### ARMORY

Permite consultar fichas de armamento y accesorios organizadas por:

- SNIPER
- ASSAULT
- DEMOLITION
- HANDGUN
- ACCESORIES

Cada ficha muestra la información disponible del elemento seleccionado, como munición, cargadores, unidades o accesorios compatibles.

ARMORY también contiene **COMPLEMENTS**. En esta sección puede seleccionar un rol y después un arma o elemento para consultar sus complementos asociados: munición, correas, fundas, cargadores y otros recordatorios. Esta misma información se utiliza para generar DON'T FORGET.

### STORAGE

STORAGE convierte el antiguo catálogo de consumibles en un inventario persistente organizado en:

- BBs
- GRENADES
- GAS

Seleccione una categoría y un elemento para consultar `PURCHASE`, `USED`, `CONSUMED` y `TOTAL`.

Cada pulsación de **PURCHASE** registra una unidad adquirida. Cada pulsación de **USED** registra una unidad retirada fuera de un LOG, por ejemplo por uso externo, pérdida, descarte o caducidad. `CONSUMED` es de solo lectura y procede automáticamente de operaciones registradas cuando existe una correspondencia inequívoca. `TOTAL` también es de solo lectura, representa la existencia actual calculada como `PURCHASE - USED - CONSUMED` y el sistema conserva su último valor coherente.

TOTAL se conserva para recuperar rápidamente el último estado conocido, pero siempre se recalcula a partir de PURCHASE, USED y CONSUMED. Editar o eliminar un LOG recalcula CONSUMED y TOTAL sin crear operaciones inversas.

BBs conserva preparada toda su infraestructura, pero todavía no descuenta consumo automáticamente. GAS permite PURCHASE y USED, pero no reparte el consumo entre `06 KG`, `08 KG`, `10 KG`, `12 KG` y `14 KG` porque INPUT OPERATION aún no distingue esos formatos. STORAGE GRENADES contiene `9mm GRENADES` y `CO2 GRENADES`, que se descuentan desde los campos equivalentes del LOG. `40mm GRENADES` continúa disponible en INPUT OPERATION y el historial DATA, pero no forma parte de STORAGE.

### LOADOUTS

Reúne las referencias de equipamiento personal en:

- HEADGEAR
- FRONT PANEL
- UNIFORM

Seleccione una opción para consultar los perfiles disponibles y su contenido.

UNIFORM muestra las referencias `MCBCK - SUMMER`, `MCBCK - LONG` y `DESERT`.

Las entradas marcadas como `UNDER CONSTRUCTION` todavía no tienen información disponible.

## 7. CURRENT GEAR

CURRENT GEAR permite consultar y preparar el equipo utilizado en la partida más reciente. Desde HOMESCREEN, pulse **CURRENT GEAR** y configure los apartados necesarios:

- PRIMARY WEAPON
- SECONDARY WEAPON
- ACCESORIES
- HEADGEAR
- FRONT PANEL
- UNIFORM

UNIFORM permite seleccionar `MCBCK - SUMMER`, `MCBCK - LONG` o `DESERT` con el mismo estilo de selector que el resto del equipo.

Las opciones disponibles se adaptan a las elecciones anteriores. Si cambia una categoría principal, cualquier selección que deje de ser compatible puede eliminarse automáticamente. ACCESORIES permite seleccionar varios elementos.

Mientras realiza cambios está editando una preparación temporal. Cuando termine, vuelva al menú principal de CURRENT GEAR y pulse **APPLY**. Esa acción convierte la preparación actual en el **Loadout Activo**.

Si sale sin pulsar APPLY, STATUS continúa mostrando el último Loadout Activo confirmado. El Loadout Activo sigue siendo una confirmación de sesión; los valores base de SET-UP se conservan de forma persistente.

## 8. STATUS

Desde HOMESCREEN, pulse **STATUS** para consultar el Loadout Activo confirmado mediante APPLY.

STATUS muestra:

- PRIMARY WEAPON
- SECONDARY WEAPON
- ACCESORIES
- HEADGEAR
- FRONT PANEL
- UNIFORM

Los apartados aún no configurados aparecen como `NOT CONFIGURED`. STATUS es una pantalla de consulta: los cambios de equipo se realizan desde CURRENT GEAR.

### DON'T FORGET

Pulse **DON'T FORGET** para abrir un checklist generado a partir de los complementos del Loadout Activo.

Cada recordatorio comienza sin marcar:

`[ ] ELEMENTO`

Pulse una línea para alternar entre `[ ]` y `[X]`. Marcar un elemento sólo cambia el checklist; no modifica INVENTORY, CURRENT GEAR ni el Loadout Activo.

Después de cambiar el equipo y pulsar APPLY, STATUS y DON'T FORGET se actualizan con la nueva configuración.

Las líneas de DON'T FORGET se deduplican y se muestran en orden alfabético sin perder su estado `[ ]` o `[X]`.

## 9. DATA

DATA conserva un historial permanente de operaciones y permite consultarlo, editarlo y analizarlo.

### INPUT OPERATION

El alta de una operación sigue cuatro pasos:

1. **DATE & LOCATION:** introduzca la fecha en formato `DD/MM/AAAA` y una ubicación.
2. **CONFIRM LOADOUT:** revise y capture una copia del Loadout Activo, incluido UNIFORM. Los campos sin selección aparecen como `NOT CONFIGURED`.
3. **CONSUMABLES:** indique cargadores, granadas y HPA utilizados. Se admiten hasta dos decimales, con coma o punto.
4. **CONFIRM DATA:** revise fecha, ubicación, loadout y consumibles antes de guardar.

El LOG guarda un snapshot histórico. Cambiar posteriormente CURRENT GEAR no modifica operaciones anteriores.

### LOG y LOG DETAIL

**LOG** enumera las operaciones guardadas. Abra una entrada para consultar **LOG DETAIL**, donde aparecen la fecha, ubicación, HEADGEAR, UNIFORM, el resto del loadout y los consumibles registrados.

Los LOG creados antes de incorporar UNIFORM continúan siendo legibles y muestran ese apartado como `NOT CONFIGURED`.

### EDIT

**EDIT** abre el flujo de la operación con sus valores precargados. Puede modificar DATE & LOCATION, LOADOUT —incluidos HEADGEAR y UNIFORM— y CONSUMABLES. Revise el resultado y pulse **CONFIRM MODIFICATIONS** para guardarlo. Salir mediante BACK antes de confirmar no modifica el archivo.

Si cambia la fecha y ya existe un LOG para la nueva fecha, PIP-SuriOS informa del conflicto y conserva el registro original.

### DELETE

**DELETE** solicita confirmación antes de eliminar permanentemente el LOG abierto. **CANCEL** vuelve al detalle sin borrar; **CONFIRM DELETE** elimina únicamente ese registro.

### STATISTICS

STATISTICS calcula porcentajes directamente desde los LOG persistentes. Incluye:

- **PRIMARY WEAPON:** distribución entre todas las armas primarias admitidas.
- **SECONDARY WEAPON:** distribución entre todas las armas secundarias admitidas.
- **LOCATION:** ubicaciones registradas, normalizadas y ordenadas alfabéticamente.
- **HEADGEAR:** distribución únicamente entre los perfiles `SURI-14` y `BROTHERHOOD`.
- **UNIFORM:** distribución entre `MCBCK - SUMMER`, `MCBCK - LONG` y `DESERT`.

Los valores vacíos o históricos que no correspondan a una opción válida no intervienen en el porcentaje. Si no hay datos válidos se muestra `NO DATA`.

## 10. TOOLS

Desde HOMESCREEN, pulse **TOOLS**. Tras `LOADING...` puede elegir **COMMS**, **MAP**, **PROXIMITY RADIO SCANNER** o **RADS** en orden alfabético. Dentro de PROXIMITY RADIO SCANNER están P.R.S. v2.0, P.R.S. v1.0, P.R.S. TESTING y OPERATION GUIDE.

Estas herramientas tienen una finalidad inmersiva o experimental. No sustituyen instrumentos de medición profesionales.

### RADS

RADS simula el comportamiento de un contador Geiger mediante un medidor analógico de aguja, estados visuales y clics de audio. La escala incluye `LOW`, `HIGH` y `CRITICAL`.

Al entrar, la aguja comienza en el mínimo y el estado es `BACKGROUND`. Mantenga pulsado el botón físico **VOLUME UP** para elevar progresivamente el nivel. La aguja avanza hacia `HIGH`, cambia el estado mostrado y aumenta la frecuencia de los clics.

Al soltar VOLUME UP, la aguja y el sonido regresan lentamente al mínimo. El descenso no es instantáneo.

Mientras RADS está abierto, VOLUME UP controla la simulación y no modifica el volumen multimedia. Fuera de esta pantalla vuelve a comportarse normalmente.

Pulse **VOLUME DOWN** para alternar silenciosamente entre `RADS` y `RADS.`. El punto identifica el modo de inclinación. En `RADS.` la aguja utiliza el Rotation Vector del teléfono: unos 45° constituyen la referencia baja y acercar el teléfono a la horizontal eleva la lectura hasta CRITICAL. El suavizado reduce vibraciones y el sensor se libera al salir de la pantalla.

RADS no mide radiación real. Su modo `RADS.` utiliza orientación únicamente como control inmersivo.

### P.R.S. v1.0

P.R.S. v1.0 (Proximity Radio Scanner) busca señales Bluetooth Low Energy cercanas y utiliza su intensidad para ofrecer una estimación aproximada de proximidad. Entre en **TOOLS > PROXIMITY RADIO SCANNER > P.R.S. v1.0**, active Bluetooth y conceda el permiso solicitado por Android.

`SURI-14` representa el dispositivo del usuario en el centro del radar. Cada señal activa aparece como un punto situado en uno de estos anillos:

- **VERY CLOSE:** señal muy intensa.
- **CLOSE:** señal intensa.
- **MEDIUM:** señal intermedia.
- **FAR:** señal débil.

La posición alrededor del círculo es únicamente una representación visual estable. **No indica la dirección física del dispositivo.** P.R.S. v1.0 tampoco calcula metros ni muestra una distancia exacta: sólo utiliza la intensidad de la señal BLE para estimar proximidad.

El panel CONTACTS muestra los totales CURRENT y NEW, además del reparto activo entre VERY CLOSE, CLOSE, MEDIUM y FAR. El panel SCAN muestra el estado del escaneo, el baseline y el acceso a CALIBRATE.

#### CALIBRATE

Pulse **CALIBRATE** antes de realizar el barrido de una zona nueva. P.R.S. v1.0 observará durante unos segundos las señales que ya forman parte del entorno.

- **BACKGROUND:** contacto presente durante la calibración y considerado conocido.
- **NEW:** contacto detectado después de la calibración que no formaba parte del entorno conocido.

Los contactos que dejan de recibirse desaparecen automáticamente tras un breve periodo. La calibración y la lista de contactos sólo existen durante la sesión actual de P.R.S. v1.0.

#### Sonidos de P.R.S. v1.0

- **Pulso de barrido:** suena al comenzar cada vuelta del radar.
- **Aviso BACKGROUND:** pitido breve y discreto cuando el barrido atraviesa un contacto conocido.
- **Aviso NEW:** pitido más fuerte y perceptible cuando atraviesa un contacto nuevo.

Cada contacto genera como máximo un aviso en cada pasada. El sonido se detiene al salir de P.R.S. v1.0 o enviar la aplicación a segundo plano.

P.R.S. v1.0 no muestra nombres de dispositivos, direcciones MAC ni información personal. Detectar una señal electrónica no significa detectar o localizar a una persona.

### P.R.S. v2.0

P.R.S. v2.0 es un cribado de indicios de dispositivos BLE pensado para comprobar una puerta antes de acceder a una estancia sin visibilidad. Se abre desde **TOOLS > PROXIMITY RADIO SCANNER > P.R.S. v2.0** y requiere Bluetooth activo, ubicación activada y los permisos de Bluetooth/ubicación solicitados por Android.

El procedimiento compara dos posiciones consecutivas:

1. Sitúese en el pasillo o zona de referencia, mantenga el teléfono quieto y pulse **START REFERENCE**. La referencia dura aproximadamente 8 segundos.
2. Sin cambiar de sesión, sitúese junto a la puerta cerrada, mantenga el teléfono en una posición estable y pulse **START DOOR SCAN**. El escaneo dura aproximadamente 12 segundos.
3. Consulte `NEW SIGNALS`, `STABLE SIGNALS`, `STRONGEST RSSI` y `SIGNAL INDEX` junto al resultado final. Si el Watch 2 esta activo, `WATCH` y `MATCHED` muestran el estado de la segunda lectura y el punto azul identifica el nodo de enlace, no una coordenada.

Los resultados se expresan deliberadamente como evidencia radioeléctrica:

- **NO DEVICE SIGNAL:** no aparecieron señales nuevas respecto a la referencia.
- **POSSIBLE SIGNAL:** apareció al menos una señal nueva, pero con poca estabilidad.
- **PROBABLE SIGNAL:** aparecieron señales nuevas estables o una señal nueva estable con intensidad alta.

`SIGNAL INDEX` es un índice heurístico de apoyo, no una probabilidad estadística. P.R.S. v2.0 **no detecta personas, no confirma que una señal esté al otro lado de la puerta y no debe utilizarse como único criterio para entrar**. Un resultado `NO DEVICE SIGNAL` no demuestra que la estancia esté vacía.

### P.R.S. TESTING

#### Revision de campo

En la pantalla de preparacion se pueden describir las condiciones de una prueba para facilitar la calibracion posterior: objetivo, posicion NORTH/SOUTH/EAST/WEST, entorno (`OPEN FIELD`, `WALL / DOOR`, `PERSON BLOCKING`, `BAG / POCKET` o `CUSTOM`), colocacion, orientacion y notas libres. Se puede elegir `A56 ONLY / WITHOUT WATCH` como control o `A56 + WATCH 2 / DUAL NODE` para la lectura doble. Esa informacion se guarda en el CSV junto con los metadatos de la sonda.

Para el grid de P.R.S. v2.0 use primero `START CLOSE SCAN` y despues `START WIDE SCAN`. El telefono aparece en el centro; los puntos verdes son lecturas de la pasada corta y los puntos ambar son lecturas nuevas de la pasada amplia.

P.R.S. TESTING es una herramienta experimental para recoger datos de calibración BLE; no modifica automáticamente P.R.S. v1.0 ni sus umbrales. Se abre desde **TOOLS > PROXIMITY RADIO SCANNER > P.R.S. TESTING**.

1. En `SET TEST`, seleccione TARGET, tipo de prueba, posición física NORTH/SOUTH/EAST/WEST y notas opcionales.
2. En `IDENTIFY TARGET`, coloque temporalmente el objetivo junto a SURI-14 e inicie la identificación guiada. Confirme el candidato encontrado o elija entre los candidatos mostrados si existe ambigüedad.
3. Coloque el objetivo en la posición indicada y pulse `START SAMPLE`. Las muestras estáticas duran aproximadamente 30 segundos; MOVEMENT continúa hasta `STOP MOVEMENT`.
4. `RESULT` resume observaciones, RSSI RAW y suavizado, categorías y pérdidas/recuperaciones.

`NEXT SAMPLE` conserva temporalmente el target identificado y permite cambiar la posición. `RE-IDENTIFY TARGET` repite la identificación cuando se pierde el contacto. `RESET TEST` limpia únicamente el estado temporal, sin borrar sesiones anteriores ni el contador `CAL-###`.

`EXPORT CSV` de `P.R.S. TESTING` genera un archivo UTF-8 y abre el Sharesheet de Android. `P.R.S. v2.0` no ofrece exportacion CSV; guarda la sesion de forma interna. El usuario decide donde guardar o compartir el CSV de Testing. Las sesiones pueden conservar el identificador observado de BLE para correlacionar los nodos y no deben interpretarse como identidad permanente.

## 11. Controles

Los controles principales de PIP-SuriOS son:

| Control | Función |
|---|---|
| `< BACK` | Regresa a la pantalla anterior. |
| **ENTER** | Convierte el texto escrito a Morse. |
| **APPLY** | Confirma la preparación de CURRENT GEAR como Loadout Activo. |
| **CLEAR** | Borra por completo la entrada actual. |
| **DELETE** | Elimina el último carácter o símbolo introducido. |
| **STOP** | Cancela una transmisión Morse y apaga la linterna. |
| **CALIBRATE** | Registra como BACKGROUND el entorno BLE actual de P.R.S. v1.0. |
| **START REFERENCE** | Captura durante unos segundos las señales del pasillo o posición de referencia de P.R.S. v2.0. |
| **START DOOR SCAN** | Captura la posición junto a la puerta y compara las señales nuevas con la referencia. |
| **RESET** | Borra la encuesta temporal de P.R.S. v2.0 y permite comenzar otra. |
| **TRANSMIT // FLASH** | Reproduce un mensaje Morse mediante la linterna. |

Las opciones precedidas por `>` pueden pulsarse para abrir una pantalla o ejecutar la acción indicada.

## 12. Limitaciones conocidas

- Las skins SALAMANDER, IRON HAND, ADEPTUS MECHANICUS, NECRON y MANDALORIAN permanecen en construcción.
- MAP TERRAIN está técnicamente aceptado, pero su alineación GPS, heading, ergonomía, Geiger y consumo requieren validación física exterior en NAVY7.
- Las modificaciones temporales de CURRENT GEAR, el Loadout Activo, el checklist y la calibración de P.R.S. v1.0 no se guardan permanentemente. La configuración base de SET-UP sí se conserva.
- STORAGE sí conserva PURCHASE y USED permanentemente; BBs y los formatos individuales de GAS todavía no tienen consumo automático.
- INVENTORY es informativo: no descuenta consumibles ni actualiza cantidades automáticamente.
- MAP OPERATION depende de que CivTAK o Google Maps estén instalados y correctamente configurados.
- TRANSMIT // FLASH depende de que el dispositivo tenga una linterna compatible.
- RADS es una simulación y no mide radiación.
- P.R.S. v1.0 es experimental y sólo detecta señales BLE que Android y los dispositivos cercanos permitan descubrir.
- P.R.S. v2.0 sólo analiza anuncios Bluetooth Low Energy; no descubre todas las conexiones Bluetooth clásicas ni dispositivos que no anuncien en ese momento.
- Al comparar posiciones físicas, Android puede exigir `ACCESS_FINE_LOCATION` y que el servicio de ubicación esté activo; P.R.S. v2.0 solicita ese permiso junto con los permisos Bluetooth.
- La identificación por dirección BLE puede cambiar por aleatorización de direcciones, por lo que un mismo dispositivo puede aparecer como una señal nueva.
- P.R.S. v2.0 no puede separar de forma fiable una señal del pasillo de otra situada dentro de la estancia: paredes, puertas, obstáculos, potencia, orientación e interferencias alteran RSSI.
- P.R.S. v2.0 es un indicador de apoyo situacional y no un sistema de detección de ocupación ni un mecanismo de seguridad.
- La intensidad BLE puede variar por paredes, obstáculos, orientación, interferencias, potencia de emisión o posición del teléfono.
- Las categorías de P.R.S. v1.0 indican proximidad aproximada; no representan metros ni dirección física.
- P.R.S. v1.0 necesita Bluetooth activo y permisos de escaneo.
- Un emulador puede mostrar la interfaz y el audio, pero normalmente no reproduce un entorno BLE físico comparable al de un teléfono real.

## 13. Historial de versiones

| Versión | Hitos principales |
|---|---|
| **v1.0** | Incorporación funcional de MAP, COMMS e INVENTORY y consolidación del HOME operativo. |
| **v1.4** | Ampliación de INVENTORY e incorporación funcional de CURRENT GEAR. |
| **v1.5** | Incorporación de STATUS, Loadout Activo, COMPLEMENTS y DON'T FORGET. |
| **v1.7** | Incorporación de TOOLS con el contador posteriormente denominado RADS y SONAR, junto con sus sonidos y refinamientos visuales. |
| **v1.9** | Incorporación del historial DATA con alta, consulta, edición, borrado y estadísticas; UNIFORM en todo el flujo; RADS; nueva disposición de SONAR y refinamientos de arranque y DON'T FORGET. |
| **v2.0** | RADS V2 con control progresivo y modo de inclinación; STORAGE persistente con PURCHASE, USED, CONSUMED y TOTAL dinámico enlazado al historial operativo. |
| **v2.1** | P.R.S. TESTING experimental, P.R.S. v1.0/v2.0, MAP TERRAIN offline NAVY7, selección inicial de skins y reorganización de TOOLS. |
| **v2.2** | P.R.S. REMOTE PROBE experimental para Xiaomi Watch 2, recepción Wi-Fi local en A56, almacenamiento bruto y comparación conservadora entre nodos. |
| **v2.3** | SET-UP vertical con INPUT/DATA, perfil de operador persistente, réplicas primarias de texto libre y WATCH 2 en ACCESORIES. |

PIP-SuriOS v2.3 incorpora la configuración personal persistente de SET-UP y mantiene la funcionalidad experimental de P.R.S. REMOTE PROBE y P.R.S. TESTING.

## 14. Consejos de uso

### Preparación antes de la partida

Antes de comenzar:

- Cargue completamente el teléfono.
- Active Bluetooth y compruebe que PIP-SuriOS dispone de los permisos necesarios para P.R.S. v1.0 y P.R.S. v2.0.
- Compruebe que la linterna funciona si piensa utilizar TRANSMIT // FLASH.
- Revise y prepare su equipamiento en CURRENT GEAR.
- Pulse **APPLY** para confirmar el Loadout Activo antes de empezar.
- Abra STATUS y compruebe que muestra la configuración correcta.
- Complete el checklist DON'T FORGET para verificar los complementos necesarios.

### Uso de MAP

Use MAP TERRAIN para NAVY7 offline y MAP OPERATION cuando necesite el flujo externo CivTAK/Google Maps. En MAP TERRAIN confirme las acciones destructivas y recuerde que la precisión depende del GPS y la orientación reales del dispositivo.

Compruebe antes de la partida que la aplicación cartográfica elegida está instalada, configurada y preparada para funcionar en la zona de juego.

### Uso de COMMS

Use PMR Frequencies como referencia rápida para consultar los canales y sus frecuencias.

MORSE TERMINAL puede resultar útil cuando necesite transmitir una señal visual silenciosa mediante la linterna. Antes de utilizarlo, asegúrese de que el destello no pueda confundirse con una señal de seguridad o emergencia establecida por la organización de la partida.

### Uso de P.R.S.

Para obtener una referencia más útil del entorno:

- Pulse **CALIBRATE** antes de comenzar la partida.
- Vuelva a calibrar si cambia significativamente de zona o si el entorno electrónico es diferente.
- Interprete VERY CLOSE, CLOSE, MEDIUM y FAR únicamente como categorías aproximadas de proximidad.
- Recuerde que P.R.S. v1.0 detecta señales de dispositivos Bluetooth Low Energy compatibles, no personas.
- Utilice P.R.S. v1.0 como apoyo situacional y nunca como única fuente de información antes de entrar en una zona.
- Para una puerta, use P.R.S. v2.0 con una referencia en el pasillo y una segunda lectura junto a la puerta, manteniendo el teléfono quieto durante ambas ventanas.
- Trate `POSSIBLE SIGNAL` y `PROBABLE SIGNAL` como motivos para obtener más información por medios seguros, no como autorización automática de acceso.

Paredes, obstáculos, interferencias y la posición del teléfono pueden cambiar la categoría mostrada aunque el dispositivo detectado no se haya movido.

### Uso de RADS

RADS tiene una finalidad exclusivamente inmersiva. Mantenga pulsado **VOLUME UP** para el control continuo o cambie con **VOLUME DOWN** al modo `RADS.` controlado por inclinación.

La herramienta no representa niveles reales de radiación y no debe utilizarse como instrumento de seguridad.

### Durante la partida

- Revise periódicamente STATUS para recordar qué Loadout está activo.
- Utilice DON'T FORGET como lista rápida de comprobación del equipo y sus complementos.
- Si cambia de equipamiento, actualice CURRENT GEAR y vuelva a pulsar **APPLY** para que STATUS refleje la nueva configuración.
- Evite manipular la aplicación cuando hacerlo pueda distraerle o comprometer su seguridad y respete siempre las normas de la organización.

### Final de la partida

- Revise los consumibles utilizados y compárelos con la información disponible en INVENTORY.
- Reponga o anote externamente las existencias cuando corresponda. INVENTORY es informativo y no descuenta consumibles automáticamente.
- Prepare en CURRENT GEAR el Loadout previsto para la siguiente partida y pulse **APPLY** cuando desee dejarlo como Loadout Activo durante la sesión actual.
