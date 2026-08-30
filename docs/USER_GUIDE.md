# PIP-SuriOS - User Guide

> P.R.S. current implementation: v3.0 BLE contact tracking with the optional
> Watch 2 PROBE node. The active flow is `LOCAL SCAN`, `SCAN + PROBE`,
> `DEVICES` and `OPERATION GUIDE`. The in-app OPERATION GUIDE is intentionally
> empty; no physical field procedure is part of the current scope.

> The `prsOnlyDebug` edition is optimized for the Z Flip 6 external display:
> centered `P.R.S.`, radar on the left and a names-only list on the right.

Manual oficial de usuario — PIP-SuriOS v2.5

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

La navegación interna, el inventario, CURRENT GEAR, STATUS, las conversiones Morse y RADS funcionan sin conexión. Algunas acciones dependen de funciones del sistema Android: MAP puede abrir aplicaciones externas, MORSE puede usar la linterna y P.R.S. v3.0 necesita Bluetooth; los modos con PROBE necesitan además un Watch 2 emparejado mediante Wear OS/Data Layer. La guía de campo de P.R.S. está reservada en `TOOLS > PROXIMITY RADIO SCANNER > OPERATION GUIDE` y permanece vacía porque no forma parte del alcance actual.

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
- **ACKNOWLEDGEMENTS:** visualizador de solo lectura con la lista de personas y equipos que han prestado apoyo concreto al proyecto. Las ampliaciones se realizan mediante código.

En **OPERATOR** se pueden introducir ID, NAME, CALLSIGN, NUMBER, COUNTRY y TEAM. En **PRIMARY WEAPON**, ROLE continúa siendo un selector desplegable y WEAPON es un campo de texto libre para escribir cualquier réplica o combinación personalizada. Los cambios se guardan al introducirlos y se reutilizan como base de CURRENT GEAR.

ACCESORIES incluye `WATCH 2` junto a los accesorios existentes. Su ficha de INVENTORY permanece informativa y marcada como `UNDER CONSTRUCTION`.

## 4. MAP

Desde HOMESCREEN, pulse **TOOLS > MAP** y elija uno de sus dos modos.

### MAP TERRAIN

Al entrar muestra **CHOOSE LOCATION** y no carga ningún mapa automáticamente.
Dentro del selector, **CHOOSE LOCATION** permanece como primera opción; después
se muestran los mapas disponibles en orden alfabético: `HOME` y `NAVY7`.
NAVY7 es un mapa topográfico incluido completamente offline en la aplicación y
no necesita conexión a Internet.

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

Desde HOMESCREEN, pulse **TOOLS**. Tras `LOADING...` puede elegir **COMMS**, **MAP**, **PROXIMITY RADIO SCANNER** o **RADS** en orden alfabético. Dentro de PROXIMITY RADIO SCANNER están `LOCAL SCAN`, `SCAN + PROBE`, `DEVICES`, `INDIVIDUAL TRACKER` y `OPERATION GUIDE`.

Estas herramientas tienen una finalidad inmersiva o experimental. No sustituyen instrumentos de medición profesionales.

### RADS

RADS simula el comportamiento de un contador Geiger mediante un medidor analógico de aguja, estados visuales y clics de audio. La escala incluye `LOW`, `HIGH` y `CRITICAL`.

Al entrar, la aguja comienza en el mínimo y el estado es `BACKGROUND`. Mantenga pulsado el botón físico **VOLUME UP** para elevar progresivamente el nivel. La aguja avanza hacia `HIGH`, cambia el estado mostrado y aumenta la frecuencia de los clics.

Al soltar VOLUME UP, la aguja y el sonido regresan lentamente al mínimo. El descenso no es instantáneo.

Mientras RADS está abierto, VOLUME UP controla la simulación y no modifica el volumen multimedia. Fuera de esta pantalla vuelve a comportarse normalmente.

Pulse **VOLUME DOWN** para alternar silenciosamente entre `RADS` y `RADS.`. El punto identifica el modo de inclinación. En `RADS.` la aguja utiliza el Rotation Vector del teléfono: unos 45° constituyen la referencia baja y acercar el teléfono a la horizontal eleva la lectura hasta CRITICAL. El suavizado reduce vibraciones y el sensor se libera al salir de la pantalla.

RADS no mide radiación real. Su modo `RADS.` utiliza orientación únicamente como control inmersivo.

### P.R.S. v3.0

P.R.S. v3.0 es un sistema de reconocimiento de proximidad basado en anuncios
Bluetooth Low Energy. Su flujo actual es:

`BLE SCAN → CONTACT LIST → RSSI POR CONTACTO → HISTÓRICO TEMPORAL →
SUAVIZADO → TENDENCIA → REPRESENTACIÓN EN GRID`

Active Bluetooth y conceda los permisos que solicite Android. La aplicación
mantiene varios contactos simultáneamente; una lectura aislada no cambia por
sí sola de forma importante la representación ni la tendencia.

#### Modos de operación

- **LOCAL SCAN:** el A56 realiza el escaneo BLE local.
- **SCAN + PROBE:** el A56 escanea y coordina el nodo PROBE del Watch 2. Sus
  observaciones llegan a la misma lista de contactos, identificadas por su
  origen.
- **DEVICES:** abre la gestión de dispositivos conocidos a omitir.
- **OPERATION GUIDE:** reservado para un eventual procedimiento de campo y
  actualmente vacío; no forma parte del alcance actual.

La variante `prsOnlyDebug`, destinada a la pantalla externa del Z Flip 6,
arranca directamente en `SCAN`: `P.R.S.` queda centrado, el radar ocupa la
mitad izquierda y la lista de nombres ocupa la mitad derecha. No expone
`SCAN + PROBE`.

#### CONTACT LIST y TRACK TARGET

`CONTACT LIST` muestra los contactos BLE observados, su nombre anunciado cuando
existe, el identificador técnico, RSSI RAW, RSSI suavizado, proximidad relativa
y tendencia. Si no se anuncia un nombre utilizable, P.R.S. asigna una etiqueta
de sesión como `UNKNOWN 01`; el nombre no se usa como identificador interno
principal. Cuando la evidencia BLE permite inferir el tipo, el nombre añade
`[PHONE]`, `[WATCH]`, `[TV]`, `[AUDIO]` o `[COMPUTER]`. Si no es identificable,
no aparece ningún sufijo.

Pulse una fila para entrar en **TRACK TARGET**. El contacto seleccionado queda
resaltado en ámbar dentro del GRID y el panel muestra:

- nombre o identificador mostrado;
- RSSI actual y RSSI suavizado;
- tendencia `APPROACHING`, `STABLE` o `MOVING AWAY`;
- proximidad relativa `NEAR`, `MEDIUM` o `FAR`;
- histórico RSSI reciente, variación y explicación de la decisión.

Los demás contactos siguen siendo observados. Pulse **STOP TRACKING** o la fila
activa para dejar de seguir el objetivo sin reiniciar el escaneo. **CLEAR
CONTACTS** borra la sesión temporal completa.

#### DEVICES

En `DEVICES` hay tres submenús:

1. **IDENTIFY DEVICE:** muestra los anuncios BLE recibidos en directo. Pulse
   **SAVE DEVICE** en el contacto deseado. La dirección BLE se guarda como
   regla principal; también puede introducir manualmente una dirección MAC o
   un nombre BLE exacto.
2. **SAVED DEVICES:** muestra las reglas persistentes. **DISABLE** conserva la
   regla pero deja que el contacto aparezca; **ENABLE** vuelve a omitirlo del
   análisis; **REMOVE** elimina la regla.
3. **MAC ADDRESS GUIDE:** explica cómo localizar y verificar la dirección BLE
   observada antes de guardarla. Puede tener formato `AA:BB:CC:DD:EE:FF`; si el
   dispositivo usa una dirección privada o rotatoria, se puede guardar su
   nombre BLE exacto como alternativa, teniendo en cuenta que puede coincidir
   con varios dispositivos. Desactive la regla en **SAVED DEVICES** antes de
   seleccionarlo en **INDIVIDUAL TRACKER**.

Las direcciones privadas o rotatorias pueden cambiar. En ese caso, una regla
por nombre BLE exacto sirve como alternativa, aunque puede coincidir con más
de un dispositivo. Las reglas habilitadas se aplican tanto a `LOCAL SCAN` como
al flujo `SCAN + PROBE`.

#### INDIVIDUAL TRACKER

Esta herramienta experimental depende de P.R.S. y TERRAIN. En **TARGET** se
elige primero el campo TERRAIN y después un único dispositivo detectado por
**LOCAL SCAN** del A56. En **TRACKER** se muestra el mapa seleccionado con el
GRID de P.R.S. centrado en la posición GPS actual del A56 y únicamente la señal
del objetivo seleccionado. No usa PROBE, no calcula la posición del objetivo ni
convierte RSSI en metros; cualquier recorte futuro de la circunferencia se
definirá únicamente tras las pruebas físicas.

#### Análisis temporal

P.R.S. separa los datos medidos de los procesados y de las inferencias:

- **Medidos:** identificador técnico, nombre anunciado, advertising data, RSSI
  RAW y timestamp.
- **Procesados:** histórico evaluado, RSSI suavizado, media y variación.
- **Inferidos:** tendencia, banda de proximidad relativa y nube de densidad.

El tracker evalúa la señal con la cadencia configurada, aplica suavizado
exponencial y compara la ventana reciente de la señal de más antigua a más
nueva. Una intensidad que aumenta de forma sostenida puede producir
`APPROACHING`; una que disminuye puede producir `MOVING AWAY`; una variación
insuficiente produce `STABLE`. La ventana mínima, la confirmación temporal y
la histéresis evitan alternancias causadas por pequeñas fluctuaciones. Antes
de reunir suficiente evidencia se muestra `WAITING`.

Los valores iniciales de calibración están centralizados en
`PrsTuning.DEFAULT`: cadencia de evaluación, alpha de suavizado, tamaño de
histórico, muestras y duración mínima, variación significativa, banda estable,
histéresis, confirmaciones, expiración de contactos y umbrales de proximidad.
Son valores provisionales de representación relativa, no una calibración física
ni una conversión RSSI → metros.

#### Categoría del dispositivo

P.R.S. intenta clasificar cada contacto como móvil/tableta (`[PHONE]`), reloj o
dispositivo wearable (`[WATCH]`), televisión o receptor (`[TV]`), audio
(`[AUDIO]`) o equipo informático (`[COMPUTER]`). La inferencia usa el nombre
anunciado, la clase Bluetooth y BLE Appearance. Es una ayuda de lectura rápida,
no una identificación definitiva de fabricante o modelo; no muestra margen de
confianza ni signos de interrogación. La edición reducida y la completa usan
el mismo criterio.

#### GRID y nubes de densidad

Se conserva la estética GRID de P.R.S. v2.0 como superficie gráfica: rejilla,
líneas de escaneo, anillos, corchetes y emblema de fondo. La posición antigua
de puntos y sus ángulos sintéticos ya no tienen interpretación física.

Cada contacto se representa como una nube o área difusa anular. Una señal más
intensa puede ocupar una banda radial relativa distinta de una señal débil,
pero la nube cubre el azimut completo porque un único receptor BLE no mide la
dirección. El GRID comunica evidencia e incertidumbre, no coordenadas X/Y,
distancia exacta ni rumbo.

En `SCAN + PROBE`, el subgrid del Watch 2 representa la posición relativa de
los nodos cuando hay fixes válidos. Es una referencia visual del A56 y del
PROBE, no una dirección BLE ni una localización exacta del contacto.

#### Diagnóstico y limitaciones de interpretación

No existe un menú separado `DIAGNOSTICS`. La instrumentación está integrada en
la lista y en `TRACK TARGET`, donde pueden comprobarse RSSI RAW, suavizado,
histórico, timestamps, variación, tendencia y proximidad. El texto de
explicación indica si la decisión procede de una ventana insuficiente, una
variación estable o un cambio sostenido.

RSSI depende del dispositivo, orientación, obstáculos, potencia e
interferencias. P.R.S. no convierte RSSI en metros, no inventa un azimut y no
detecta personas. `APPROACHING` y `MOVING AWAY` describen evolución de señal y
son inferencias de proximidad, no una prueba aislada de movimiento físico.

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
| **CONTACT LIST** | Muestra los contactos BLE observados y permite seleccionar un objetivo. |
| **STOP TRACKING** | Deja de seguir el contacto seleccionado sin reiniciar el escaneo. |
| **CLEAR CONTACTS** | Borra los contactos y el histórico de la sesión actual. |
| **IDENTIFY DEVICE** | Escanea contactos en directo y permite guardar uno. |
| **ENABLE / DISABLE** | Activa o suspende una regla persistente de dispositivo guardado. |
| **REMOVE** | Elimina una regla de dispositivo guardado. |
| **TRANSMIT // FLASH** | Reproduce un mensaje Morse mediante la linterna. |

Las opciones precedidas por `>` pueden pulsarse para abrir una pantalla o ejecutar la acción indicada.

## 12. Limitaciones conocidas

- Las skins SALAMANDER, IRON HAND, ADEPTUS MECHANICUS, NECRON y MANDALORIAN permanecen en construcción.
- MAP TERRAIN está técnicamente aceptado, pero su alineación GPS, heading, ergonomía, Geiger y consumo requieren validación física exterior en NAVY7.
- Las modificaciones temporales de CURRENT GEAR, el Loadout Activo y el checklist no se guardan permanentemente. La configuración base de SET-UP sí se conserva.
- STORAGE sí conserva PURCHASE y USED permanentemente; BBs y los formatos individuales de GAS todavía no tienen consumo automático.
- INVENTORY es informativo: no descuenta consumibles ni actualiza cantidades automáticamente.
- MAP OPERATION depende de que CivTAK o Google Maps estén instalados y correctamente configurados.
- TRANSMIT // FLASH depende de que el dispositivo tenga una linterna compatible.
- RADS es una simulación y no mide radiación.
- P.R.S. sólo analiza anuncios Bluetooth Low Energy que Android y los dispositivos cercanos permitan descubrir; no descubre todas las conexiones Bluetooth clásicas.
- La primera ejecución puede exigir permisos Bluetooth y ubicación, además de Bluetooth activado.
- La identificación por dirección BLE puede cambiar por aleatorización de direcciones, por lo que un mismo dispositivo puede aparecer como una señal nueva.
- Los nombres BLE pueden faltar o ser compartidos por varios dispositivos; la dirección es el identificador técnico preferente cuando permanece estable.
- La categoría del dispositivo es una inferencia heurística y puede no aparecer
  o ser incorrecta si el anuncio aporta datos incompletos o ambiguos.
- Las nubes del GRID no proporcionan azimut, coordenadas X/Y ni distancia física.
- La intensidad BLE puede variar por paredes, obstáculos, orientación, interferencias, potencia de emisión o posición del teléfono.
- Las tendencias describen evolución de señal y no demuestran por sí solas que el contacto se haya movido.
- Las reglas habilitadas de DEVICES omiten contactos del análisis; una regla deshabilitada no los omite.
- Un contacto desaparece tras superar el tiempo de expiración configurado y puede reaparecer como una nueva observación.
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
| **v2.4** | Firma visible y versión técnica actualizadas; P.R.S. compacto para pantalla externa y categorías inferidas de dispositivo. |
| **v2.5** | Auditoría de Sprint 019, backup local desactivado y correcciones de cierre de RAD ZONE/Geiger. |
| **v3.0** | Reconstrucción de P.R.S.: LOCAL SCAN, SCAN + PROBE, histórico temporal RSSI, nubes de densidad, TRACK TARGET, DEVICES y categorías inferidas de dispositivo. |
| **v3.1** | INDIVIDUAL TRACKER experimental, MAC ADDRESS GUIDE y visualizador de agradecimientos de solo lectura en SET-UP. |

P.R.S. v3.0 incorpora el análisis temporal observable, la lista de contactos,
el seguimiento dinámico, el filtrado persistente de DEVICES y el nodo operativo
PROBE para Watch 2. Las implementaciones antiguas de posicionamiento se
consideran retiradas del flujo activo.

## 14. Consejos de uso

### Preparación antes de la partida

Antes de comenzar:

- Cargue completamente el teléfono.
- Active Bluetooth y compruebe que PIP-SuriOS dispone de los permisos necesarios para P.R.S.
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

1. Abra **TOOLS > PROXIMITY RADIO SCANNER** y seleccione `LOCAL SCAN` o
   `SCAN + PROBE`.
2. Espere a que aparezca `CONTACT LIST`. Consulte RAW, SMOOTHED, tendencia y
   proximidad relativa de cada contacto.
3. Pulse un contacto para activar `TRACK TARGET`. Observe la evolución del
   histórico mientras mueve el A56; utilice **STOP TRACKING** para cancelar el
   seguimiento sin reiniciar el escaneo.
4. Use **DEVICES > IDENTIFY DEVICE** para guardar un dispositivo conocido y
   **SAVED DEVICES** para activar, desactivar o eliminar su regla de omisión.
5. Interprete `APPROACHING`, `STABLE` y `MOVING AWAY` como tendencias de señal,
   no como una dirección o una medición física exacta.

Paredes, obstáculos, interferencias, orientación y la posición del teléfono
pueden cambiar la señal aunque el dispositivo detectado no se haya movido.

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
