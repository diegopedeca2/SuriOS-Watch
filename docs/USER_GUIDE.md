# PIP-SuriOS - User Guide

## Introducción

PIP-SuriOS es una aplicación móvil de apoyo operativo inspirada en una interfaz de terminal. Su objetivo es reunir en un único lugar el equipo personal, el inventario, las comunicaciones, los accesos cartográficos y el estado del loadout.

El proyecto prioriza una experiencia directa, legible y utilizable sin conexión. La información, las conversiones Morse y la navegación interna funcionan completamente offline. Sólo requieren servicios externos las funciones que abren otras aplicaciones, como CivTAK o Google Maps.

La aplicación está diseñada y validada principalmente para Samsung Galaxy A56 y Pixel 8 Emulator. La interfaz general se utiliza en orientación horizontal; TEXT > MORSE es la excepción y funciona en orientación vertical para facilitar el uso del teclado.

## Flujo general

Al iniciar PIP-SuriOS se muestra la siguiente secuencia:

Splash → carga del sistema → selección de modo → HOME → módulos disponibles.

En la selección de modo puede elegirse OPERATION o CIVILIAN. El modo OPERATION contiene los módulos funcionales descritos en esta guía. Algunas funciones y el modo CIVILIAN continúan en desarrollo.

## HOME

HOME OPERATION permite acceder a:

- **CURRENT GEAR:** prepara el equipo de la sesión y permite aplicarlo como Loadout Activo.
- **INVENTORY:** consulta ARMORY, CONSUMABLES, LOADOUTS y COMPLEMENTS.
- **MAP:** abre los accesos cartográficos TERRAIN y OPERATION.
- **COMMS:** ofrece la tabla PMR y MORSE TERMINAL.
- **STATUS:** muestra el resumen del Loadout Activo y su checklist DON'T FORGET.
- **TOOLS:** reúne GEIGER COUNTER y SONAR.

Utilice `< BACK` para regresar al nivel anterior.

## CURRENT GEAR

CURRENT GEAR permite preparar cinco apartados:

- PRIMARY WEAPON
- SECONDARY WEAPON
- ACCESORIES
- HEADGEAR
- FRONT PANEL

Los selectores dependientes muestran únicamente opciones compatibles. Cuando se cambia una categoría principal, una selección incompatible se elimina automáticamente. ACCESORIES admite varios elementos simultáneos.

Las selecciones realizadas forman un borrador. Para convertir ese borrador en el **Loadout Activo**, pulse **APPLY** en el menú principal de CURRENT GEAR. STATUS sólo muestra la última configuración aplicada; los cambios que no se hayan aplicado no alteran STATUS.

## INVENTORY

INVENTORY se divide en tres ramas principales:

- **ARMORY:** consulta el armamento por SNIPER, ASSAULT, DEMOLITION, HANDGUN y ACCESORIES. También contiene COMPLEMENTS.
- **CONSUMABLES:** consulta las categorías BBs, GRENADES y GAS.
- **LOADOUTS:** muestra las ramas HEADGEAR y FRONT PANEL.

### COMPLEMENTS

COMPLEMENTS permite seleccionar ROLE y después WEAPON o ITEM. Muestra la munición, correas, fundas, cargadores y otros complementos asociados cuando existe información disponible. Las definiciones de esta pantalla son las mismas que utiliza STATUS - DON'T FORGET.

Las opciones marcadas como `UNDER CONSTRUCTION` todavía no tienen información definida.

## MAP

MAP ofrece dos modos:

- **TERRAIN:** acceso cartográfico general.
- **OPERATION:** intenta abrir CivTAK para el uso operativo.

Cuando CivTAK no está disponible, PIP-SuriOS utiliza Google Maps como alternativa. Estas aplicaciones externas pueden necesitar conexión, servicios de ubicación o configuración propia. Al regresar, PIP-SuriOS conserva su navegación interna.

## COMMS

COMMS permite elegir entre:

- **FREQUENCIES:** tabla informativa de canales PMR.
- **MORSE:** acceso a MORSE TERMINAL.

MORSE TERMINAL dispone de dos modos:

### TEXT > MORSE

Escriba el mensaje mediante el teclado y pulse **ENTER** para convertirlo. La conversión no se realiza mientras escribe. **DELETE** elimina el último carácter y **CLEAR** limpia la entrada y la señal generada.

Pulse **TRANSMIT // FLASH** para reproducir el mensaje con la linterna. Durante la transmisión, **STOP** cancela inmediatamente la reproducción y apaga la linterna. Salir de la pantalla también cancela la transmisión y garantiza el apagado del flash.

### MORSE > TEXT

Construya el mensaje mediante los botones de punto, raya y separación. Pulse **CONVERT** para ver el texto resultante. La conversión admite inicialmente letras A-Z y números 0-9.

## STATUS

STATUS es una pantalla de consulta. Resume exclusivamente el **Loadout Activo** confirmado mediante APPLY. Los apartados sin configurar muestran `NOT CONFIGURED`.

### DON'T FORGET

DON'T FORGET genera automáticamente un checklist a partir de los complementos del Loadout Activo. Cada línea comienza desmarcada:

`[ ] ELEMENTO`

Pulse una línea para alternar entre `[ ]` y `[X]`. Marcar un elemento sólo cambia el estado visual del checklist: no modifica CURRENT GEAR, INVENTORY, COMPLEMENTS ni el Loadout Activo.

## TOOLS

TOOLS reúne herramientas inmersivas y experimentales. Ninguna sustituye instrumentos profesionales ni debe interpretarse como una medición científica.

### GEIGER COUNTER

GEIGER COUNTER simula un contador Geiger con medidor analógico y sonido. Mantenga pulsado **VOLUME UP** para elevar progresivamente el nivel; al soltarlo, la aguja y la frecuencia de los clics regresan lentamente a `BACKGROUND`.

Mientras esta pantalla está activa, **VOLUME UP** controla exclusivamente la simulación y no modifica el volumen multimedia. La herramienta no utiliza sensores ni mide radiación real.

### SONAR

SONAR realiza un escaneo de señales Bluetooth Low Energy cercanas permitido por Android. El Galaxy A56 se representa como `SURI-14` en el centro del radar y los contactos aparecen en cuatro categorías aproximadas:

- VERY CLOSE
- CLOSE
- MEDIUM
- FAR

Las categorías proceden de RSSI suavizado. No representan metros, dirección física ni posición real; el ángulo de cada punto es únicamente una posición visual estable.

Pulse **CALIBRATE** para observar el entorno y registrar los contactos presentes como `BACKGROUND`. Los contactos que aparezcan después del baseline se muestran como `NEW`. El barrido reproduce un pulso general y avisos diferenciados al atravesar contactos `BACKGROUND` o `NEW`.

SONAR necesita Bluetooth activo y permisos de escaneo. No muestra nombres ni direcciones MAC y no conserva contactos al abandonar la sesión.

## Limitaciones actuales

- No existe persistencia permanente.
- CURRENT GEAR y el Loadout Activo se pierden al cerrar la aplicación o reiniciar su proceso.
- El checklist se reinicia al regenerarse tras un cambio de Loadout y al reiniciar la aplicación.
- No existen cantidades dinámicas, consumo automático de inventario ni sincronización de loadouts.
- Varias opciones continúan siendo informativas o permanecen en desarrollo.
- Las aplicaciones externas dependen de su propia instalación, permisos, configuración y conectividad.
- SONAR sólo estima proximidad mediante RSSI BLE; obstáculos, orientación, potencia de emisión y condiciones ambientales pueden alterar la categoría.

## Roadmap

PIP-SuriOS continuará ampliándose en futuras versiones. Esta guía constituye el manual oficial de uso y se actualizará conforme se incorporen nuevas funciones. El inicio y alcance de cada Sprint futuro requerirán autorización específica.
