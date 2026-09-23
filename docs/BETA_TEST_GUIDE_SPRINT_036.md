# PIP-SuriOS — Guía BETA de pruebas — Sprint 036

## Qué recibe cada tester

Cada tester recibe una APK identificada con su perfil:

- FENRIR
- ALTAMIRA
- CHECHU
- JAVI

No se debe intercambiar una APK entre perfiles. El nombre, icono y perfil deben
coincidir antes de empezar.

## Instalación

1. Copiar la APK al teléfono Android.
2. Instalarla solo si procede de la carpeta BETA indicada.
3. Comprobar que el nombre de la aplicación corresponde al perfil recibido.
4. Conceder permisos de Bluetooth, ubicación y cámara si Android los solicita.
5. No hace falta un reloj ni una baliza externa para esta prueba.

## Comprobación rápida de MAP

1. Abrir `TOOLS > MAP > TERRAIN`.
2. Abrir el mapa `NAVY7` y después el mapa `TESTING` del perfil.
3. Comprobar que el mapa se carga sin quedarse vacío.
4. Probar el zoom mediante pellizco.
5. Si el perfil muestra GRID o puntos del mapa, comprobar que se leen con luz
   natural.

JAVI debe mostrar el icono `PIP-J` y su mapa `TESTING` debe corresponder a la
zona centrada en `40.431175043353754, -3.638558623703386`.

## Comprobación rápida de P.R.S.

1. Abrir `TOOLS > PROXIMITY RADIO SCANNER`.
2. Probar `SENTRY` y confirmar que aparece el estado de Bluetooth.
3. Abrir `TRACKER` y comprobar que la lista de dispositivos muestra datos
   útiles: nombre o alias, RSSI, identificador corto y dirección.
4. Probar la búsqueda por nombre, alias o identificador.
5. Usar `BACK` para salir de la lectura y confirmar que la pantalla no se
   queda bloqueada.

## Cómo informar de un problema

Registrar:

- perfil de la APK;
- modelo del teléfono y versión de Android;
- pantalla exacta donde ocurrió;
- pasos realizados;
- texto visible del error;
- si la aplicación se cerró o solo dejó de responder;
- captura de pantalla si es posible.

No hace falta interpretar el problema. Es suficiente describir lo que se vio.
La posición GPS, la brújula y Bluetooth pueden comportarse de forma diferente
en interiores, junto a vehículos o cerca de objetos metálicos.
