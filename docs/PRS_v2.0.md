# P.R.S. v2.0 — ARCHIVE

> This document describes the retired two-position presence experiment. It is
> no longer the active P.R.S. implementation. See
> `docs/OLD VERSIONS/v3.0/PRS_v3.0.md` for the
> current BLE contact-list, temporal RSSI and density-cloud architecture.

## Objetivo

P.R.S. v2.0 ayuda a obtener indicios antes de aproximarse a una estancia sin visibilidad. El objetivo técnico es comparar las señales Bluetooth Low Energy (BLE) observadas en una posición de referencia con las observadas junto a la puerta cerrada.

El resultado es deliberadamente un **indicador de señales de dispositivos**, no un detector de personas. La aplicación no autoriza el acceso ni sustituye una comprobación visual, comunicación por radio, procedimiento de equipo u otro medio seguro.

## Funcionamiento implementado

1. `START REFERENCE` captura durante 6 segundos los identificadores BLE observados en el pasillo o zona de referencia.
2. `START DOOR SCAN` captura durante 10 segundos la posición junto a la puerta.
3. Se consideran señales nuevas las observadas junto a la puerta que no estaban en la referencia.
4. Una señal es estable cuando aparece al menos cuatro veces durante el escaneo de puerta.
5. El resultado se presenta como:
   - `NO DEVICE SIGNAL`: no hay señales nuevas respecto a la referencia.
   - `POSSIBLE SIGNAL`: hay alguna señal nueva, pero no es estable.
   - `PROBABLE SIGNAL`: hay varias señales nuevas estables o una señal nueva estable con RSSI alto.
6. `SIGNAL INDEX` resume la cantidad y estabilidad de la evidencia; no es una probabilidad estadística.

La sesión no muestra nombres ni direcciones MAC. Los datos se mantienen en memoria y se descartan al salir de P.R.S. v2.0.

## Evaluación del enfoque BLE

El enfoque es razonable como **cribado de bajo coste** porque muchos teléfonos, relojes, auriculares y accesorios anuncian BLE y el A56 dispone de Bluetooth 5.3. Sin embargo, una señal BLE no equivale a una persona: puede proceder de una estancia contigua, del pasillo, de un objeto abandonado o de otra planta.

El Galaxy A56 incorpora acelerómetro, giroscopio y sensor geomagnético, pero esos sensores sólo describen el movimiento y la orientación del teléfono; no permiten ver a través de una puerta. La plataforma Android entrega RSSI de los anuncios BLE, no una distancia ni una dirección física precisa. Al usar la comparación espacial, la app solicita `ACCESS_FINE_LOCATION` además de los permisos Bluetooth.

Por ello, P.R.S. v2.0 usa una comparación de dos posiciones y ventanas temporales en lugar de convertir RSSI directamente en “personas dentro”. Es una mejora frente a contar señales instantáneas, pero sigue siendo heurística.

## Limitaciones conocidas

- El escáner actual es BLE; no descubre todos los dispositivos Bluetooth clásicos ni los que no estén anunciando.
- Android y los dispositivos pueden aleatorizar direcciones. El mismo dispositivo puede aparecer como una señal nueva.
- Paredes, puertas, metal, cuerpos, orientación, potencia de transmisión e interferencias cambian el RSSI.
- Un dispositivo apagado, en modo avión, sin anuncios BLE o fuera de cobertura no se detectará.
- Un resultado negativo no demuestra que la estancia esté vacía y uno positivo no demuestra que haya una persona.
- La exploración continua en `SCAN_MODE_LOW_LATENCY` debe limitarse a sesiones cortas en primer plano para evitar consumo innecesario.

## Alternativas y evolución recomendada

Con el hardware del A56 no hay una ruta integrada fiable para detectar ocupación a través de una puerta. La evolución con mejor relación utilidad/privacidad sería:

1. Mantener BLE como indicador secundario y registrar varias lecturas desde el mismo punto.
2. Añadir una comprobación manual de “referencia vacía” y un historial de lecturas para reducir falsos positivos del entorno.
3. Permitir identificar voluntariamente dispositivos propios del equipo mediante un identificador BLE cooperativo, sin intentar identificar teléfonos ajenos.
4. Si se necesita una decisión operativa real, usar un sensor externo cooperativo en la estancia o una confirmación humana/por radio.

Wi‑Fi puede aportar puntos de acceso, pero sus escaneos están limitados por Android y tampoco prueban presencia. Cámara, micrófono o ultrasonidos exigirían permisos y condiciones controladas, y no ofrecen una garantía fiable a través de una puerta; además, aumentan las implicaciones de privacidad.

## Validación

- Modelo de análisis cubierto por `PresenceScannerTest`.
- Compilación y pruebas unitarias ejecutadas con Gradle.
- La pantalla se integra en `TOOLS > PROXIMITY RADIO SCANNER > P.R.S. v2.0` y conserva el estilo, carga y firma visible de PIP-SuriOS.

## Revision operativa: CLOSE + WIDE y mapa 2D

La pantalla de P.R.S. v2.0 usa ahora un procedimiento mas directo:

1. `START CLOSE SCAN`: mantenga el telefono cerca. Esta pasada conserva las lecturas fuertes para crear una referencia de corto alcance.
2. `START WIDE SCAN`: alejese un poco y escuche durante una ventana mas amplia. Los puntos que no estaban en la primera pasada se resaltan en ambar.
3. El grid 2D coloca el telefono en el centro. El radio de cada punto se calcula a partir de la intensidad recibida y su angulo se mantiene estable solo para que la imagen no salte. Es una visualizacion aproximada, no una direccion fisica.

La pantalla se ha despejado: el encabezado visible es solo `P.R.S.`, el grid ocupa el area principal y el panel lateral muestra unicamente la fase, los controles, el estado y los contadores `CLOSE` y `NEW`. Se retiraron el subtitulo, la leyenda de colores, la guia de uso y el mensaje grande de resultado.

Las ventanas de `CLOSE` y `WIDE` del código actual son de 6 y 10 segundos respectivamente. Esta duración prevalece sobre cualquier referencia anterior a 8 y 12 segundos.

## P.R.S. TESTING para pruebas de campo

`P.R.S. TESTING` permite registrar cada muestra con mas contexto, sin cambiar automaticamente los umbrales de P.R.S. v1.0:

- objetivo medido, incluyendo `EXTRA 1` y `EXTRA 2`;
- tipo de prueba `STATIC` o `MOVEMENT`;
- posicion en metros mediante los ejes NORTH/SOUTH/EAST/WEST;
- condicion del entorno: `OPEN FIELD`, `WALL / DOOR`, `PERSON BLOCKING`, `BAG / POCKET` o `CUSTOM`;
- colocacion del objetivo: mano, bolsillo, mochila, muneca u otra;
- orientacion del objetivo respecto al telefono;
- notas libres y exportacion CSV con RSSI RAW, RSSI suavizado, categoria, perdidas y recuperaciones.

Para una sesion de campo se recomienda repetir cada punto al menos tres veces, mantener el telefono quieto durante las muestras estaticas y anotar cualquier cambio de orientacion, obstaculo o movimiento en `EXTRA NOTES`.

El perfil actual de `P.R.S. TESTING` esta alineado con P.R.S. v2.0 y PROBE:

- La pantalla inicia por defecto en `A56 + WATCH 2 / DUAL NODE`; `A56 ONLY / WITHOUT WATCH` queda disponible como control.
- Tras abrir o reiniciar la prueba se captura una linea base de 30 segundos. `IDENTIFY TARGET` permanece bloqueado hasta que aparece `BASELINE: READY`.
- Cada muestra registra el modo de evidencia de ubicacion: `GPS_RELATIVE_FILTERED`, `BLE_RANGE_ONLY`, `LINK_ONLY` o `A56_ONLY`.
- `GPS_RELATIVE_FILTERED` solo se usa con fixes recientes y con precision maxima de 25 m en ambos nodos; la posicion relativa se suaviza antes de guardarse.
- `BLE_RANGE_ONLY` indica cercania por BLE, pero no direccion ni metros fiables. `LINK_ONLY` confirma el enlace sin una lectura relativa utilizable.
- El CSV actual tiene 30 columnas: anade calidad GPS, este/norte/distancia relativos y estado del fix del Watch, sin exportar coordenadas GPS en bruto.
- El perfil de campo recomendado coincide con P.R.S. v2.0: `CLOSE 6s` y `WIDE 10s`.
