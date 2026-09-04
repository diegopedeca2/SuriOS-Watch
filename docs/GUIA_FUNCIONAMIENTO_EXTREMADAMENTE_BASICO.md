# Guía de funcionamiento de SuriOS — nivel EXTREMADAMENTE BÁSICO

> Documento vivo. Última revisión: 2026-09-04. Se actualizará junto con los
> cambios de pantallas, funcionamiento, parámetros y pruebas. Si el código
> cambia y esta guía no cambia con él, la guía queda pendiente de revisión.

## Para qué sirve este documento

Este documento explica SuriOS como si fuera un equipo con varias herramientas.
No hace falta saber programar para leerlo.

La idea principal es esta:

> SuriOS recibe datos de los sensores del teléfono y los convierte en
> información que podemos leer en pantalla.

## 1. Qué es SuriOS

SuriOS es una aplicación Android pensada para utilizarse principalmente en el
Samsung A56. Tiene varias zonas:

- `SET-UP`: configura la identidad del operador y el equipo.
- `CURRENT GEAR`: permite elegir el equipo que se lleva en la operación.
- `INVENTORY`: muestra el inventario.
- `STATUS`: resume información del equipo.
- `DATA`: guarda y consulta registros de operaciones.
- `TOOLS`: reúne herramientas como mapas, comunicaciones, RADS y P.R.S.

La aplicación tiene también versiones personalizadas para los testers FENRIR,
ALTAMIRA y CHECHU. Estas versiones comparten la misma aplicación, pero pueden
llevar mapas diferentes.

## 2. Qué hace P.R.S.

P.R.S. significa `PROXIMITY RADIO SCANNER`.

El teléfono escucha anuncios de dispositivos Bluetooth cercanos. Un anuncio es
una pequeña señal que un dispositivo emite repetidamente para decir: “sigo
estando aquí”.

El A56 mide la fuerza de esa señal. Esa fuerza se llama RSSI y se expresa en
`dBm`.

Importante: una señal Bluetooth no dice por sí sola dónde está exactamente una
persona o un dispositivo. Por eso P.R.S. muestra una proximidad aproximada y
una nube de incertidumbre, no una coordenada exacta.

## 3. Cómo funciona TRACKER hoy

El recorrido actual es parecido a este:

```text
TOOLS
  > P.R.S.
    > TRACKER
      > elegir modo
        > elegir campo/mapa
          > elegir dispositivo objetivo
            > TRACKER empieza automáticamente
```

### Paso 1: elegir el modo

Hay dos posibilidades:

- `ONLY PIP-BOY`: el A56 hace el escaneo.
- `PIP-BOY + PROBE`: el A56 escanea y también pide al Watch 2 que haga de
  sensor adicional.

### Paso 2: elegir el campo

Se elige un mapa TERRAIN. El mapa puede funcionar sin Internet porque se ha
guardado dentro de la aplicación.

### Paso 3: elegir el objetivo

La aplicación empieza a escuchar Bluetooth mientras muestra la lista de
dispositivos detectados. Se toca uno para convertirlo en el objetivo de esta
sesión.

### Paso 4: observar TRACKER

Al abrir la pantalla final, el teléfono vuelve a iniciar automáticamente el
escaneo. Desde ese momento:

- el Bluetooth sigue recibiendo lecturas;
- el GPS intenta seguir la posición del operador;
- la brújula intenta seguir la orientación del teléfono;
- el mapa se actualiza alrededor del operador;
- la información del objetivo se refresca mientras llegan nuevas señales.

En la versión actual, el antiguo dibujo de círculos se ha sustituido por una
niebla de probabilidad. Las zonas donde el objetivo parece menos probable se
van haciendo más visibles; las zonas que todavía tienen más incertidumbre
conservan más niebla. Esto sigue siendo una ayuda visual, no una coordenada
exacta.

En la pantalla del mapa puedes hacer el gesto de pellizcar con dos dedos para
acercar o alejar la vista. El seguimiento sigue funcionando mientras cambias
el zoom.

Los gestos de pantalla se comprueban físicamente en el dispositivo. Un emulador
o una prueba mediante ADB no sustituyen esa comprobación.

No hay que pulsar un botón de inicio en la versión actual.

## 4. ¿Las lecturas y los cálculos son fluidos mientras camino?

La respuesta corta es: sí, pero con un matiz importante.

Las lecturas Bluetooth llegan de forma continua mientras la pantalla está
abierta. Sin embargo, SuriOS no recalcula todo con cada pequeña señal recibida.

Hace dos trabajos distintos:

1. Guarda la lectura más reciente y actualiza datos sencillos.
2. Aproximadamente cada 3 segundos procesa la señal, actualiza el historial y
   revisa si el dispositivo parece acercarse, alejarse o permanecer estable.

Por tanto, al caminar el sistema sí trabaja de manera continua, pero la parte
de análisis tiene un ritmo controlado. Esto evita reaccionar exageradamente a
cada pequeño rebote de la señal.

## 5. Qué información calcula

SuriOS muestra varias capas de información:

- `RAW RSSI`: la última fuerza de señal recibida.
- `SMOOTH RSSI`: la misma señal después de suavizar sus cambios bruscos.
- `SAMPLES`: cuántas lecturas se han recibido del objetivo.
- `TREND`: posible dirección del cambio: acercándose, alejándose, estable o
  todavía sin datos suficientes.
- `BAND`: grupo de proximidad relativo: cerca, medio o lejos.
- `CONF`: confianza experimental basada en la cantidad de historial.

Al principio suele aparecer `WAITING`, porque todavía no hay suficiente
historial para afirmar una tendencia.

## 6. Qué significa “parar” ahora

En la pantalla final de TRACKER, el control disponible es `< BACK`.

Al pulsarlo:

- se cierra la sesión visual;
- se detiene el escaneo Bluetooth;
- se detienen GPS y brújula;
- si se estaba usando PROBE, se envía la orden de detenerlo.

Actualmente no existe este flujo:

```text
START READING → caminar → STOP READING → CALCULATE
```

Tampoco existe un informe final separado. Los cálculos se van haciendo durante
la sesión y desaparecen al salir, porque esta sesión de TRACKER se mantiene en
memoria y no se guarda como un archivo de operación.

## 7. Lo que TRACKER no puede afirmar

TRACKER no puede decir de forma fiable:

- “el objetivo está a 12 metros”;
- “el objetivo está exactamente al norte”;
- “el objetivo está en esta coordenada del mapa”.

El GPS indica dónde está el operador, no dónde está el objetivo Bluetooth. La
señal puede cambiar por paredes, cuerpos, árboles, ropa, orientación del
dispositivo y otros aparatos.

## 8. Palabras básicas

- **BLE/Bluetooth**: sistema de comunicación inalámbrica de corto alcance.
- **RSSI**: número que representa la fuerza de una señal recibida.
- **GPS**: sistema que calcula la posición del teléfono.
- **PROBE**: el Watch 2 utilizado como sensor adicional.
- **TARGET**: el dispositivo que hemos elegido observar.
- **MAP/TERRAIN**: el mapa offline del campo elegido.
- **Historial**: conjunto de lecturas anteriores usadas para comparar el cambio.

## 9. RADS

`RADS` es el contador visual de radiación. Usa tres audios según el nivel
visible de 0 a 10: el audio 1 cubre 1–2, el audio 2 cubre 4–5 y el audio 3
cubre 7–10. El nivel 0 es silencioso. En los niveles de transición 3 y 6 se
superponen dos audios: 1+2 en 3 y 2+3 en 6. El volumen no se cambia de forma
deliberada.

## Resumen final

Tu interpretación era parcialmente correcta: TRACKER sí toma lecturas mientras
te mueves y sí actualiza el análisis durante el recorrido. Lo que no existe aún
es el control manual de una sesión con `START`, `STOP` y cálculo final. Hoy la
sesión comienza al entrar en TRACKER y termina al volver atrás.
