# SuriOS Radar para Galaxy Watch Ultra

## Objetivo

Aplicacion Wear OS standalone que recibe por BLE los objetivos detectados por
un HLK-LD2450 conectado a un ESP32-WROOM-32U. No necesita telefono movil y no
modifica el firmware, el cableado ni el protocolo del radar.

## Interfaz

La aplicacion muestra primero un menu para elegir `REAL` o `SIM`. El grid no
incluye controles de cambio de modo; se vuelve al menu con el boton atras del
reloj.

La vista radar usa la paleta oficial de PIP-SuriOS:

- fondo negro
- verde PIP para el radar y T1
- azul PIP para el cono y los anillos
- rojo PIP para los objetivos y sus etiquetas
- gris para la informacion secundaria

El cono parte de las 6 en punto, tiene una apertura inicial de 120 grados y
marca los rangos de 1 a 6 metros. Todos los objetivos y sus etiquetas se
representan en rojo PIP. Los objetivos retenidos (`flags=3`) aparecen
atenuados y con anillo discontinuo.

El grid no muestra leyenda de metros ni coordenadas escritas. Cada objetivo se
identifica solo como `T1`, `T2` o `T3`; los radios laterales tienen seis tramos
iguales y la curva superior marca el limite del area de deteccion.

## Estetica de la esfera

La pantalla reutiliza los mismos recursos visuales de la esfera principal:

- marco exterior de rueda dentada en verde PIP;
- emblema de la Hermandad en segundo plano, atenuado con alpha `88/255`;
- franja horizontal de refresco tipo terminal, animada cada 6 segundos.

El emblema queda detras del grid y de los objetivos para conservar la lectura.

## Modulo

- Gradle: `:radarwatch`
- Ruta: `watch/radarwatch`
- Paquete: `com.suri.suriosradar`
- Version inicial: `0.1`, codigo `1`
- Dispositivo: Samsung Galaxy Watch Ultra, Wear OS API 34 o superior

## BLE

La aplicacion busca el periferico `SuriOS Radar`, descubre el servicio y la
caracteristica indicados y activa el descriptor estandar CCCD para recibir
notificaciones.

- Servicio: `9e5d0001-7b3a-4c0f-91c2-245000000001`
- Datos: `9e5d0002-7b3a-4c0f-91c2-245000000001`
- Paquete: 17 bytes, little-endian
- Estados: `0` oculto, `1` movimiento normal, `3` retenido/atenuado

La capa BLE, el decodificador y la pantalla estan separados. Si se pierde la
conexion, el cliente vuelve a buscar automaticamente `SuriOS Radar`.

La actividad fija su orientacion ignorando el sensor del reloj. Ademas, volver
a primer plano no reinicia una conexion BLE que ya este activa. La desconexion
de ADB no forma parte de la conexion BLE; si el proceso de la aplicacion es
terminado por el sistema, la app debe volver a abrirse y seleccionar `REAL`.

## Modo simulado

El modo `SIM` genera tres objetivos de prueba. El modo `REAL` escanea y recibe
los datos del ESP32. Ambos modos usan la misma representacion del radar.

## Compilar e instalar

Desde `D:\WristOS`:

```powershell
.\gradlew.bat :radarwatch:test :radarwatch:lint :radarwatch:assembleDebug
```

Con el Galaxy Watch Ultra conectado por ADB Wi-Fi:

```powershell
adb -s 192.168.1.63:45843 install -r watch\radarwatch\build\outputs\apk\debug\radarwatch-debug.apk
```

El modo `REAL` solicita `BLUETOOTH_SCAN` y `BLUETOOTH_CONNECT`. No se solicita
ubicacion porque las coordenadas proceden del radar y no calculan la posicion
del usuario.

## Limitaciones conocidas

Los valores son posiciones y estados producidos por el radar. No son
mediciones medicas, no representan una distancia garantizada y no deben
interpretarse como una funcion de seguridad o vigilancia certificada.

## Referencias

- [Permisos Bluetooth de Android](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions)
- [Notificaciones GATT](https://developer.android.com/develop/connectivity/bluetooth/ble/transfer-ble-data)
- [Aplicaciones Wear OS standalone](https://developer.android.com/training/wearables/apps/standalone-apps)
- [Compose para Wear OS](https://developer.android.com/training/wearables/compose)
