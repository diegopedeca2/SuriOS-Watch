# P.R.S. REMOTE PROBE — ARCHIVE

> The Remote Probe experiment has been retired. This file is kept as a
> historical field record only; the active P.R.S. is A56-only and is described
> in `docs/OLD VERSIONS/v3.0/PRS_v3.0.md`.

## Implementación

La esfera PIW y la esfera `PROBE-SuriOS` son módulos Watch Face Format independientes. La prueba usa la app Wear OS independiente `:remoteprobe`, instalada con el nombre `P.R.S. REMOTE PROBE`; ninguna de las esferas es el canal de datos del P.R.S.

- `app`: el Galaxy A56 mantiene el escáner BLE actual como `OPERATOR`.
- `app/remoteprobe`: el A56 inicia un gateway HTTP/TCP local en el puerto `28771`, anuncia `_pipprs._tcp` por NSD/mDNS y responde al descubrimiento UDP local en `28772` como respaldo.
- `watch/remoteprobe`: el Xiaomi Watch 2 escanea BLE desde su propia posición y envía lotes JSON por la Wi-Fi local.
- Ambos lados guardan datos brutos. El A56 escribe `observations.csv`; el reloj conserva `observations.ndjson` como copia local.

No se usa LTE, Internet, triangulación, coordenadas ni conversión de RSSI a metros.

## Procedimiento mínimo

1. Conectar el A56 a la red Wi-Fi o activar su hotspot.
2. Abrir `TOOLS > PROXIMITY RADIO SCANNER > P.R.S. v2.0` en el A56. El gateway arranca mientras esta pantalla está abierta.
3. Abrir `P.R.S. REMOTE PROBE` en el Watch 2 y conceder Bluetooth, ubicación y red local si se solicitan.
4. Esperar `STATUS: ACTIVE` y `LINK: CONNECTED` en el reloj. En el A56 debe aparecer `PROBE: CONNECTED`.
5. Colocar A56 en A, Watch 2 en B y el dispositivo de prueba en una tercera posición.
6. Mantener ambos nodos activos durante la ventana de prueba.
7. Pulsar `STOP / RETRIEVE PROBE` en el reloj al recuperarlo.
8. En el A56, anotar el identificador de sesión; los datos se guardan automáticamente.

Las sesiones del A56 están en `filesDir/remote-probe/RPR-.../observations.csv`. La copia local del reloj está en `filesDir/remote-probe/observations.ndjson`. El puerto ADB inalámbrico del reloj (por ejemplo `5555` o `39083`) solo sirve para instalar y depurar; no es el puerto de P.R.S.

## Identificación y comparación

Cada registro contiene timestamp epoch, nodo, identificador observado, RSSI, nombre anunciado, advertising data y tipo Bluetooth cuando Android los proporciona.

La clave primaria de correlación es `ScanResult.device.address`, tal como la entrega Android/Wear OS. No se fabrica una identidad permanente. Una dirección privada/rotatoria puede hacer que el mismo dispositivo aparezca como dos contactos o que no se pueda emparejar entre nodos; esos casos deben quedar como no correlacionados.

La comparación experimental solo se muestra cuando ambos nodos han observado exactamente el mismo identificador en una ventana de 8 segundos:

- diferencia de 10 dB o más a favor del A56: `NEAR OPERATOR`;
- diferencia de 10 dB o más a favor del Watch: `NEAR PROBE`;
- diferencia de hasta 6 dB: `BETWEEN`;
- resto: `UNCERTAIN`.

Estos nombres son indicativos y no representan distancia ni certeza física. La ausencia de una lectura en un nodo no se interpreta por sí sola como proximidad al otro.

## Limitaciones para el sábado

- NSD/mDNS depende de que el hotspot permita el tráfico multicast. Si el Watch permanece en `LINK: DISCONNECTED`, comprobar primero que ambos están en la misma red; el fallback de IP manual todavía no está implementado.
- El escaneo sin filtros puede verse limitado por el sistema al apagar completamente la pantalla. El Watch usa un foreground service y modo de baja latencia, pero conviene probar primero con pantalla activa/Ambient Mode y medir el comportamiento con pantalla apagada.
- El enlace HTTP es local y sin cifrado/autenticación; `usesCleartextTraffic` está habilitado solo para estas apps experimentales. Es aceptable únicamente para esta prueba privada en la red controlada del sábado.
- El almacenamiento del Watch es una copia de seguridad local; la sesión que se debe analizar es el CSV recibido por el A56. Si el enlace se corta, el reloj conserva el NDJSON para recuperación posterior por ADB.

## Referencias técnicas

- [BluetoothLeScanner](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeScanner)
- [Network Service Discovery](https://developer.android.com/develop/connectivity/wifi/use-nsd)
- [Local network permission](https://developer.android.com/privacy-and-security/local-network-permission)
- [Wear OS network communication](https://developer.android.com/training/wearables/data/network-communication)
