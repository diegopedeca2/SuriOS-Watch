# PIP-SuriOS - User Guide

Manual oficial de usuario — PIP-SuriOS v2.0

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

La navegación interna, el inventario, CURRENT GEAR, STATUS, las conversiones Morse y RADS funcionan sin conexión. Algunas acciones dependen de funciones del sistema Android: MAP puede abrir aplicaciones externas, MORSE puede usar la linterna y SONAR necesita Bluetooth y sus permisos correspondientes.

## 2. Inicio de la aplicación

Al abrir PIP-SuriOS aparece una secuencia de arranque automática:

1. `LOADING...`
2. `LOG-IN ID: SURI-14 VERIFIED`
3. Los módulos de HOME se inicializan en el orden INVENTORY, MAP, COMMS, DATA, CURRENT GEAR, STATUS y TOOLS. Cada línea muestra primero `LOADING MÓDULO.....` y después `READY`.
4. `SYSTEM READY`
5. `SELECT MODE`

Después puede elegirse uno de estos modos:

- **CIVILIAN:** abre la pantalla civil. Actualmente está en construcción y no contiene módulos funcionales.
- **OPERATION:** abre el HOME operativo y permite acceder a todas las funciones descritas en este manual.

## 3. HOME

### CIVILIAN

La pantalla `CIVILIAN - HOMESCREEN` muestra `UNDER CONSTRUCTION`. Utilice `< BACK` para volver a la selección de modo.

### OPERATION

La pantalla `OPERATION - HOMESCREEN` es el punto principal de acceso. Contiene:

- **INVENTORY:** consulta de armas, consumibles, equipamiento y complementos.
- **MAP:** selección de los modos cartográficos TERRAIN y OPERATION.
- **COMMS:** tabla de frecuencias PMR y MORSE TERMINAL.
- **DATA:** registro permanente, consulta, edición, borrado y estadísticas de operaciones.
- **CURRENT GEAR:** preparación del equipo de la sesión.
- **STATUS:** consulta del Loadout Activo y checklist DON'T FORGET.
- **TOOLS:** acceso a RADS y SONAR.

Algunos módulos muestran brevemente `LOADING...` antes de abrirse. Utilice `< BACK` para regresar al nivel anterior.

## 4. MAP

Desde HOME OPERATION, pulse **MAP** y elija uno de sus dos modos.

### MAP TERRAIN

Abre `MAP - TERRAIN`. Esta pantalla está actualmente marcada como `UNDER CONSTRUCTION` y todavía no ofrece funciones cartográficas.

### MAP OPERATION

Abre `MAP - OPERATION`. Pulse **LAUNCH** para iniciar el acceso cartográfico operativo.

PIP-SuriOS comprueba si CivTAK está disponible:

- Si CivTAK está instalado, lo abre automáticamente.
- Si CivTAK no está disponible, intenta abrir Google Maps como alternativa.

La aplicación externa puede solicitar su propia configuración, conexión o permisos de ubicación. Al volver desde CivTAK o Google Maps, PIP-SuriOS conserva el flujo de navegación de MAP.

## 5. COMMS

Desde HOME OPERATION, pulse **COMMS**. Después de la pantalla de carga puede elegir **FREQUENCIES** o **MORSE**.

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

Desde HOME OPERATION, pulse **INVENTORY**. El módulo se divide en tres apartados.

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

CURRENT GEAR permite preparar el equipo que se utilizará durante la sesión. Desde HOME OPERATION, pulse **CURRENT GEAR** y configure los apartados necesarios:

- PRIMARY WEAPON
- SECONDARY WEAPON
- ACCESORIES
- HEADGEAR
- FRONT PANEL
- UNIFORM

UNIFORM permite seleccionar `MCBCK - SUMMER`, `MCBCK - LONG` o `DESERT` con el mismo estilo de selector que el resto del equipo.

Las opciones disponibles se adaptan a las elecciones anteriores. Si cambia una categoría principal, cualquier selección que deje de ser compatible puede eliminarse automáticamente. ACCESORIES permite seleccionar varios elementos.

Mientras realiza cambios está editando una preparación temporal. Cuando termine, vuelva al menú principal de CURRENT GEAR y pulse **APPLY**. Esa acción convierte la preparación actual en el **Loadout Activo**.

Si sale sin pulsar APPLY, STATUS continúa mostrando el último Loadout Activo confirmado. La configuración y el Loadout Activo se conservan solamente durante la sesión de la aplicación.

## 8. STATUS

Desde HOME OPERATION, pulse **STATUS** para consultar el Loadout Activo confirmado mediante APPLY.

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

Desde HOME OPERATION, pulse **TOOLS**. Tras `LOADING...` puede elegir RADS o SONAR.

Estas herramientas tienen una finalidad inmersiva o experimental. No sustituyen instrumentos de medición profesionales.

### RADS

RADS simula el comportamiento de un contador Geiger mediante un medidor analógico de aguja, estados visuales y clics de audio. La escala incluye `LOW`, `HIGH` y `CRITICAL`.

Al entrar, la aguja comienza en el mínimo y el estado es `BACKGROUND`. Mantenga pulsado el botón físico **VOLUME UP** para elevar progresivamente el nivel. La aguja avanza hacia `HIGH`, cambia el estado mostrado y aumenta la frecuencia de los clics.

Al soltar VOLUME UP, la aguja y el sonido regresan lentamente al mínimo. El descenso no es instantáneo.

Mientras RADS está abierto, VOLUME UP controla la simulación y no modifica el volumen multimedia. Fuera de esta pantalla vuelve a comportarse normalmente.

Pulse **VOLUME DOWN** para alternar silenciosamente entre `RADS` y `RADS.`. El punto identifica el modo de inclinación. En `RADS.` la aguja utiliza el Rotation Vector del teléfono: unos 45° constituyen la referencia baja y acercar el teléfono a la horizontal eleva la lectura hasta CRITICAL. El suavizado reduce vibraciones y el sensor se libera al salir de la pantalla.

RADS no mide radiación real. Su modo `RADS.` utiliza orientación únicamente como control inmersivo.

### SONAR

SONAR busca señales Bluetooth Low Energy cercanas y utiliza su intensidad para ofrecer una estimación aproximada de proximidad. Antes de entrar, active Bluetooth y conceda el permiso solicitado por Android.

`SURI-14` representa el dispositivo del usuario en el centro del radar. Cada señal activa aparece como un punto situado en uno de estos anillos:

- **VERY CLOSE:** señal muy intensa.
- **CLOSE:** señal intensa.
- **MEDIUM:** señal intermedia.
- **FAR:** señal débil.

La posición alrededor del círculo es únicamente una representación visual estable. **No indica la dirección física del dispositivo.** SONAR tampoco calcula metros ni muestra una distancia exacta: sólo utiliza la intensidad de la señal BLE para estimar proximidad.

El panel CONTACTS muestra los totales CURRENT y NEW, además del reparto activo entre VERY CLOSE, CLOSE, MEDIUM y FAR. El panel SCAN muestra el estado del escaneo, el baseline y el acceso a CALIBRATE.

#### CALIBRATE

Pulse **CALIBRATE** antes de realizar el barrido de una zona nueva. SONAR observará durante unos segundos las señales que ya forman parte del entorno.

- **BACKGROUND:** contacto presente durante la calibración y considerado conocido.
- **NEW:** contacto detectado después de la calibración que no formaba parte del entorno conocido.

Los contactos que dejan de recibirse desaparecen automáticamente tras un breve periodo. La calibración y la lista de contactos sólo existen durante la sesión actual de SONAR.

#### Sonidos de SONAR

- **Pulso de barrido:** suena al comenzar cada vuelta del radar.
- **Aviso BACKGROUND:** pitido breve y discreto cuando el barrido atraviesa un contacto conocido.
- **Aviso NEW:** pitido más fuerte y perceptible cuando atraviesa un contacto nuevo.

Cada contacto genera como máximo un aviso en cada pasada. El sonido se detiene al salir de SONAR o enviar la aplicación a segundo plano.

SONAR no muestra nombres de dispositivos, direcciones MAC ni información personal. Detectar una señal electrónica no significa detectar o localizar a una persona.

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
| **CALIBRATE** | Registra como BACKGROUND el entorno BLE actual de SONAR. |
| **TRANSMIT // FLASH** | Reproduce un mensaje Morse mediante la linterna. |

Las opciones precedidas por `>` pueden pulsarse para abrir una pantalla o ejecutar la acción indicada.

## 12. Limitaciones conocidas

- CIVILIAN y MAP TERRAIN permanecen en construcción.
- CURRENT GEAR, el Loadout Activo, el checklist y la calibración de SONAR no se guardan permanentemente. Se pierden al cerrar o reiniciar la aplicación.
- STORAGE sí conserva PURCHASE y USED permanentemente; BBs y los formatos individuales de GAS todavía no tienen consumo automático.
- INVENTORY es informativo: no descuenta consumibles ni actualiza cantidades automáticamente.
- MAP OPERATION depende de que CivTAK o Google Maps estén instalados y correctamente configurados.
- TRANSMIT // FLASH depende de que el dispositivo tenga una linterna compatible.
- RADS es una simulación y no mide radiación.
- SONAR es experimental y sólo detecta señales BLE que Android y los dispositivos cercanos permitan descubrir.
- La intensidad BLE puede variar por paredes, obstáculos, orientación, interferencias, potencia de emisión o posición del teléfono.
- Las categorías de SONAR indican proximidad aproximada; no representan metros ni dirección física.
- SONAR necesita Bluetooth activo y permisos de escaneo.
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

PIP-SuriOS v2.0 incorpora la evolución funcional consolidada durante Sprint 010.

## 14. Consejos de uso

### Preparación antes de la partida

Antes de comenzar:

- Cargue completamente el teléfono.
- Active Bluetooth y compruebe que PIP-SuriOS dispone de los permisos necesarios para SONAR.
- Compruebe que la linterna funciona si piensa utilizar TRANSMIT // FLASH.
- Revise y prepare su equipamiento en CURRENT GEAR.
- Pulse **APPLY** para confirmar el Loadout Activo antes de empezar.
- Abra STATUS y compruebe que muestra la configuración correcta.
- Complete el checklist DON'T FORGET para verificar los complementos necesarios.

### Uso de MAP

Utilice CivTAK siempre que esté disponible para el acceso cartográfico operativo. Si CivTAK no está instalado, PIP-SuriOS intentará abrir Google Maps automáticamente como alternativa.

Compruebe antes de la partida que la aplicación cartográfica elegida está instalada, configurada y preparada para funcionar en la zona de juego.

### Uso de COMMS

Use PMR Frequencies como referencia rápida para consultar los canales y sus frecuencias.

MORSE TERMINAL puede resultar útil cuando necesite transmitir una señal visual silenciosa mediante la linterna. Antes de utilizarlo, asegúrese de que el destello no pueda confundirse con una señal de seguridad o emergencia establecida por la organización de la partida.

### Uso de SONAR

Para obtener una referencia más útil del entorno:

- Pulse **CALIBRATE** antes de comenzar la partida.
- Vuelva a calibrar si cambia significativamente de zona o si el entorno electrónico es diferente.
- Interprete VERY CLOSE, CLOSE, MEDIUM y FAR únicamente como categorías aproximadas de proximidad.
- Recuerde que SONAR detecta señales de dispositivos Bluetooth Low Energy compatibles, no personas.
- Utilice SONAR como apoyo situacional y nunca como única fuente de información antes de entrar en una zona.

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
