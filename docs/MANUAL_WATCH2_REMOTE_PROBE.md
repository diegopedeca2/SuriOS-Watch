# Manual de uso — Xiaomi Watch 2 como baliza P.R.S.

## 1. Finalidad

El Xiaomi Watch 2 funciona temporalmente como un **P.R.S. REMOTE PROBE**: un segundo punto físico de lectura BLE.

- **Galaxy A56:** nodo `OPERATOR` y punto principal de control.
- **Xiaomi Watch 2:** nodo `PROBE` desplegable.
- **P.R.S.:** compara lecturas brutas de ambos nodos cuando el mismo identificador aparece en una ventana temporal cercana.

Esta función no calcula coordenadas, no mide distancias en metros y no es triangulación. El RSSI se conserva como señal de radio para análisis y calibración.

## 2. Acceso rápido desde PIP-SuriOS

La guía operativa está disponible directamente en el A56:

```text
TOOLS
└── PROXIMITY RADIO SCANNER
    └── OPERATION GUIDE
```

La pantalla integrada resume los pasos esenciales para iniciar, desplegar, capturar y cerrar una toma. Este documento contiene el procedimiento completo y las notas de diagnóstico.

## 3. Antes de empezar

Comprobar lo siguiente:

- A56 y Watch 2 encendidos y con batería suficiente.
- Ambos dispositivos conectados a la misma red Wi-Fi. Puede utilizarse el hotspot del A56 si permite comunicación local entre clientes.
- Bluetooth activado en ambos dispositivos.
- El Watch 2 con permisos de Bluetooth y ubicación concedidos.
- El A56 con permisos de Bluetooth y red local concedidos.
- Un dispositivo BLE de prueba que anuncie durante toda la medición.

El Watch 2 no utiliza LTE. La conexión de depuración inalámbrica de ADB sirve para instalar y diagnosticar, pero no es necesaria para la prueba de campo.

La comunicación de la prueba utiliza el gateway del A56 en TCP `28771`. El reloj intenta primero descubrirlo mediante NSD/mDNS y dispone de un descubrimiento UDP local de respaldo en `28772`. El puerto que aparezca en ADB inalámbrico —por ejemplo `192.168.1.56:39083`— no debe introducirse como puerto de P.R.S.

## 4. Activar el modo REMOTE PROBE

### En el Galaxy A56

1. Abrir PIP-SuriOS.
2. Entrar en `TOOLS`.
3. Seleccionar `PROXIMITY RADIO SCANNER`.
4. Seleccionar `P.R.S. v2.0`.
5. Mantener esta pantalla abierta durante toda la prueba.

La pantalla v2 mantiene activo el escáner del A56 y abre el gateway local que recibe los datos del Watch.

Estados importantes del A56:

- `PROBE: LISTENING`: el gateway está abierto, pero aún no ha recibido al Watch.
- `PROBE: CONNECTED`: el Watch ha conectado y está enviando heartbeat o lecturas.
- `A56: XX`: contactos BLE observados por el escáner del operador en la ventana activa.
- `WATCH: XX`: contactos BLE recibidos desde el Watch en la ventana activa.
- `TOTAL: XX`: unión de identificadores activos de ambos nodos; un contacto compartido se cuenta una sola vez.
- `MATCHED: XX`: identificadores observados recientemente por ambos nodos con la misma identidad disponible.

En el grid, el punto verde central identifica el A56 y el punto azul identifica el Watch 2 cuando el enlace está conectado. El punto azul es un marcador del nodo de enlace, no una coordenada ni la posición física del reloj.

### En el Xiaomi Watch 2

`PIW-SuriOS v2.0` es la esfera del reloj; no inicia el escaneo remoto. Para la prueba hay que abrir la aplicación independiente `P.R.S. REMOTE PROBE` desde el lanzador de aplicaciones.

1. Abrir `P.R.S. REMOTE PROBE`.
2. En el primer uso, conceder los permisos que solicite.
3. No hay que pulsar un botón `START`: tras abrirla y conceder permisos, la app inicia automáticamente el servicio y el escaneo.
4. Comprobar que aparece:

   ```text
   STATUS: ACTIVE
   LINK: CONNECTED
   SCANNING...
   ```

5. Dejar el reloj en la posición física B.
6. No pulsar `STOP / RETRIEVE PROBE` mientras la prueba esté en curso.

Al abrir la aplicación se inicia un servicio en primer plano. La pantalla puede apagarse, pero para la prueba inicial se recomienda comenzar con la pantalla activa o en Ambient Mode y comprobar después el comportamiento con pantalla apagada.

## 5. Prueba funcional del sábado

### Preparación de cada toma

Definir tres posiciones:

- **A:** Galaxy A56.
- **B:** Xiaomi Watch 2.
- **C:** dispositivo BLE de prueba.

Anotar en una hoja o en las notas del ensayo:

- número de toma;
- posición física de A, B y C;
- hora de inicio y final;
- obstáculos entre los nodos;
- si el dispositivo C está en la mano, bolsillo, mochila o carcasa;
- cualquier cambio de orientación.

### Captura

1. Abrir `P.R.S. v2.0` en el A56.
2. Abrir `P.R.S. REMOTE PROBE` en el Watch 2.
3. Esperar a que el A56 muestre `PROBE: CONNECTED`.
4. Esperar a que `WATCH`, `TOTAL` y `MATCHED` empiecen a recibir valores.
5. Colocar el dispositivo C en la posición de prueba.
6. Mantener la escena estable durante al menos 30 segundos por toma.
7. Para cada nueva posición, esperar unos segundos antes de mover el siguiente dispositivo.
8. Mantener abierta la pantalla v2 del A56. Si se abandona, el gateway se detiene y el enlace del Watch puede pasar a `DISCONNECTED`.

El resultado esperado es similar a:

```text
DEVICE X
OPERATOR RSSI: -82 dBm
PROBE RSSI: -54 dBm
RESULT: NEAR PROBE
```

La clasificación es experimental. La ausencia de `MATCHED` significa que no se ha podido correlacionar el identificador en ambos nodos; no significa automáticamente que el dispositivo esté cerca de uno de ellos.

### Finalizar y recuperar el Watch

1. Recuperar físicamente el Watch 2.
2. Pulsar `STOP / RETRIEVE PROBE`.
3. Confirmar que el reloj deja de mostrar `SCANNING...`.
4. Anotar el identificador de sesión y las notas de campo.

El botón del Watch detiene el escaneo y la transmisión. No hay exportación CSV desde `P.R.S. v2.0`: la sesión se guarda automáticamente en el almacenamiento interno del A56 para revisión técnica posterior. El reloj mantiene además una copia local NDJSON por si el enlace se interrumpe.

## 6. Uso de P.R.S. TESTING

`P.R.S. TESTING` es el flujo de calibración de campo. Permite medir el A56 en solitario como control y, prioritariamente, hacer una lectura doble A56 + Watch 2 del mismo objetivo.

### Acceso

Desde el A56:

```text
TOOLS
└── PROXIMITY RADIO SCANNER
    └── P.R.S. TESTING
```

### Flujo de una muestra

1. En `MEASUREMENT SET-UP`, seleccionar el objetivo.
2. Elegir `STATIC` para una muestra fija de 30 segundos o `MOVEMENT` para una prueba con desplazamiento.
3. En `DETECTION NODES`, elegir:
   - `A56 ONLY / WITHOUT WATCH` para la línea base o una prueba negativa;
   - `A56 + WATCH 2 / DUAL NODE` para la lectura doble.
4. Introducir la posición:
   - `NORTH`, `SOUTH`, `EAST` o `WEST`;
   - utilizar los botones rápidos `1M`, `2M`, `5M`, `10M`, `15M` o `20M` cuando sean adecuados.
5. No introducir simultáneamente norte y sur, ni este y oeste.
6. Rellenar `FIELD SITE` y seleccionar la condición:
   - `OPEN FIELD`;
   - `WALL / DOOR`;
   - `PERSON BLOCKING`;
   - `BAG / POCKET`;
   - `CUSTOM`.
7. Indicar la posición del dispositivo: `IN HAND`, `POCKET`, `BACKPACK`, `WRIST` u `OTHER`.
8. Indicar la orientación: `FACING PHONE`, `SIDEWAYS`, `BACK TO PHONE` u `UNKNOWN`.
9. Añadir notas de campo y pulsar `NEXT`.
10. Colocar el objetivo junto a `SURI-14` y pulsar `START IDENTIFICATION`.
11. Tras la identificación, pulsar `CONFIRM`.
12. En modo dual, abrir antes `P.R.S. REMOTE PROBE` en el Watch 2 y esperar `PROBE LINK: CONNECTED`. Si se quiere comprobar el caso negativo, dejar el reloj apagado o fuera de la red.
13. Colocar el objetivo en la posición seleccionada.
14. Pulsar `START SAMPLE` para una prueba estática o `START MOVEMENT` para una prueba móvil.
15. En la prueba estática, esperar a que termine la cuenta de 30 segundos. En movimiento, pulsar `STOP MOVEMENT` al terminar.
16. En modo dual, revisar `WATCH RSSI AVG`, `WATCH SAMPLES`, `MATCHED`, `PROBE LINK` y `LATEST DELTA / RESULT`, junto a `A56 RAW RSSI` y `A56 SMOOTHED`.
17. En `SAMPLE COMPLETE`, revisar los promedios de ambos nodos y pulsar `EXPORT CSV`.
18. Pulsar `NEXT SAMPLE` para mantener la sesión y añadir otra posición.

### Matriz recomendada para el sábado

1. Ejecutar una muestra `A56 ONLY / WITHOUT WATCH` en cada posición para obtener la línea base.
2. Repetir la misma posición con `A56 + WATCH 2 / DUAL NODE` y el reloj conectado.
3. Repetir una muestra dual con el reloj desconectado o apagado para validar el comportamiento negativo.
4. Repetir la muestra dual con el reloj en otra posición física y comparar `A56 RSSI`, `WATCH RSSI AVG`, `MATCHED` y `RESULT`.
5. Hacer al menos tres tomas por posición, manteniendo orientación, obstáculo y ubicación constantes.

`P.R.S. TESTING` conserva los registros brutos del A56 en `CAL-.../observations.csv` y vincula cada muestra dual con la sesión remota `RPR-...`, donde se guardan las observaciones brutas del Watch.

## 7. Identificación de contactos

Cada observación conserva:

```text
TIMESTAMP
NODE: OPERATOR / PROBE
DEVICE IDENTIFIER
RSSI
DEVICE NAME
ADVERTISING DATA
DEVICE TYPE
```

La correlación primaria utiliza la dirección que Android/Wear OS entrega en el escaneo. Algunos dispositivos BLE usan direcciones privadas o rotatorias. Por tanto:

- el mismo dispositivo puede aparecer con dos identificadores diferentes;
- dos nodos pueden no poder correlacionarlo aunque lo estén viendo;
- un nombre o los datos publicitarios ayudan a revisar el caso, pero no sustituyen automáticamente al identificador;
- no se debe inventar una identidad permanente ni convertir RSSI en distancia.

## 8. Qué guardar después de cada ensayo

Guardar juntos:

- sesión interna de `P.R.S. v2.0` identificada por `RPR-...` (esta pantalla no ofrece exportación CSV);
- CSV exportado desde `P.R.S. TESTING`, si se hizo calibración;
- número de toma y notas de posición;
- hora de inicio y fin;
- observación de `PROBE: CONNECTED` y valor de `MATCHED`;
- incidencias de Wi-Fi, pantalla apagada o pérdida de contacto.

Ubicaciones internas de referencia:

```text
A56:  filesDir/remote-probe/RPR-.../observations.csv
Watch: filesDir/remote-probe/observations.ndjson
A56:  filesDir/sonar-testing/CAL-.../...
```

## 9. Resolución rápida de problemas

### El Watch muestra `LINK: DISCONNECTED`

1. Confirmar que ambos dispositivos están en la misma Wi-Fi.
2. Confirmar que la pantalla del A56 está en `P.R.S. v2.0`.
3. Esperar entre 10 y 20 segundos para que NSD/mDNS vuelva a descubrir el gateway.
4. Si no reconecta, cerrar y volver a abrir `P.R.S. REMOTE PROBE`.
5. Confirmar que no se ha pulsado `STOP / RETRIEVE PROBE`.

### El A56 muestra `PROBE: LISTENING`

El gateway está activo, pero no ha recibido el heartbeat del Watch. Revisar Wi-Fi, permisos y la aplicación del Watch.

### `MATCHED: 0`

No sacar conclusiones de proximidad. Comprobar que el dispositivo de prueba anuncia continuamente, mantener ambos nodos activos durante unos segundos y revisar si su dirección BLE ha rotado.

### El Watch deja de detectar con la pantalla apagada

Repetir primero la toma con pantalla activa o Ambient Mode. Anotar el comportamiento y el nivel de batería; el escaneo BLE sin filtros puede estar limitado por el sistema cuando la pantalla se apaga completamente.

## 10. Reglas de interpretación

- `NEAR OPERATOR`: señal media más fuerte en el A56 por al menos 10 dB.
- `NEAR PROBE`: señal media más fuerte en el Watch por al menos 10 dB.
- `BETWEEN`: diferencia de hasta 6 dB.
- `UNCERTAIN`: cualquier caso intermedio o con evidencia insuficiente.

Estos umbrales son provisionales y no están calibrados. No representan metros,
coordenadas ni una posición garantizada.

## 11. Referencias

- [Prueba técnica P.R.S. REMOTE PROBE](PRS_REMOTE_PROBE_SATURDAY.md)
- [P.R.S. v2.0](PRS_v2.0.md)
- [BluetoothLeScanner de Android](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeScanner)
- [Network Service Discovery de Android](https://developer.android.com/develop/connectivity/wifi/use-nsd)
- [Comunicación de red en Wear OS](https://developer.android.com/training/wearables/data/network-communication)
