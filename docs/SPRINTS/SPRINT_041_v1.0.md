# Sprint 041 — SuriOS Watch: limpieza de temas abiertos

---
document: SPRINT
project: SuriOS Watch
version: 1.0
status: Activo
owner: Diego Pérez de Camino
date: 2026-10-18
predecessor: Sprint 040
watchface_version: 1.6
watchface_version_code: 10
watchface_format: 2
radar_app_version: 0.1
radar_app_version_code: 1
radar_app_module: radarwatch
adb_endpoint: 192.168.1.63:32815
---

## Objetivo

Revisar, validar y cerrar o trasladar de forma explícita los temas abiertos
detectados en la auditoría de la esfera v1.5 antes de iniciar nuevas mejoras.

## Preparación

La versión funcional anterior se conserva en:

- `Previous Versions/watchface-v1.5-sprint-041/`

La copia contiene el código fuente, los recursos y la configuración de la
esfera v1.5, sin carpetas `build` ni salidas temporales.

La esfera activa se identifica ahora como v1.6, `versionCode 10`, manteniendo
Watch Face Format v2.

## Primera tarea: limpieza de temas abiertos

- [x] `AUD-039-01`: modo ambiente validado por el propietario.
- [x] `AUD-039-02`: primer ciclo de pasos validado por el propietario.
- [x] Revisar si cada tema debe cerrarse, mantenerse como comprobación
      operativa o trasladarse a otro Sprint.
- [x] Registrar la decisión y la evidencia inicial en este documento.

### Resultado de la limpieza

- `AUD-039-01` queda **cerrado**: el modo ambiente ha sido validado en el
  Watch Ultra.
- `AUD-039-02` queda **cerrado**: el ciclo de pasos ha sido validado en el
  Watch Ultra.

Conclusión: los dos asuntos abiertos heredados del Sprint 039 quedan cerrados
en el Sprint 041 por confirmación del propietario. No quedan incidencias
técnicas abiertas dentro del alcance de la esfera.

## Objetivo funcional: SuriOS Radar

El Sprint incorpora una aplicacion Wear OS standalone para el Galaxy Watch
Ultra. Recibe las notificaciones BLE del periferico `SuriOS Radar` y dibuja
los objetivos T1, T2 y T3 en una vista polar con cono de 120 grados y anillos
de distancia cada metro, desde 1 hasta 6 metros.

- [x] Crear el modulo Gradle `:radarwatch` sin dependencia de telefono.
- [x] Separar cliente BLE, decodificador de 17 bytes y pantalla Compose.
- [x] Implementar modo `SIM` para probar la pantalla sin el ESP32.
- [x] Implementar modo `REAL` con escaneo, GATT, notificaciones y reconexion.
- [x] Anadir un menu inicial para elegir REAL o SIM y retirar esos controles
      del grid.
- [x] Aplicar la paleta oficial de PIP-SuriOS y el cono radar sectorial.
- [x] Usar rojo PIP para todos los marcadores y etiquetas de objetivos para
      mejorar el contraste.
- [x] Retirar leyendas y coordenadas del grid, dejando solo T1/T2/T3.
- [x] Extender los radios al limite del area de deteccion y dividirlos en seis
      tramos con crecimiento lineal.
- [x] Reutilizar el marco dentado, el emblema de la Hermandad y la franja de
      refresco de la esfera principal, manteniendo el emblema atenuado al fondo.
- [x] Mostrar estados BLE, antiguedad, secuencia y paquetes perdidos.
- [x] Anadir pruebas unitarias del formato little-endian y de la secuencia.
- [x] Usar el mismo icono de la version principal de PIP-SuriOS.
- [x] Compilar e instalar en el Watch Ultra por ADB Wi-Fi.
- [x] Validar recepcion real y reconexion tras apagar y encender Bluetooth.
- [x] Suavizar el borde horizontal del nivel de progreso de pasos.
- [x] Sustituir las celdas pequenas por un maximo de cinco nucleos separados,
      con un signo `+` al superar el 500% y armadura vacia en ese caso.
- [x] Mejorar Ambient Mode con hora mas visible, emblema atenuado, linea de
      terminal y marco de rueda dentada.
- [x] Simular el giro vertical del emblema mediante compresion horizontal 2D.
- [x] Hacer pulsable el centro de la armadura para abrir `SURIOS RADAR`.

No se modifica el cableado, el firmware del ESP32 ni el protocolo BLE.

## Temas fuera del alcance inmediato

- La mejora de carga de mapas MBTiles pertenece a la aplicación móvil y no se
  incorpora a este Sprint de la esfera.
- Los riesgos SEC-03 a SEC-06 pertenecen a la aplicación móvil y siguen
  documentados como riesgos abiertos o aceptados; no se modifican desde este
  Sprint sin autorización específica.

## Estado inicial

- El repositorio parte limpio en Git y sin marcadores de conflicto.
- El Watch Ultra responde por ADB Wi-Fi en `192.168.1.63:32815`.
- La versión instalada antes de este Sprint era v1.5, `versionCode 9`.

## Validación de la preparación

- [x] `gradlew.bat test lint :watchface:assembleDebug` — correcto.
- [x] Instalación de la esfera en el Watch Ultra — `Success`.
- [x] Paquete instalado — `com.suri.surioswatch`, v1.6, `versionCode 10`.
- [x] Comprobación visual de la esfera activa — firma visible `v1.6`.
- [x] Pulsación del centro probada en el Watch Ultra — abre
      `com.suri.suriosradar/.MainActivity`.

## Validaciones visuales pendientes

La implementación está compilada e instalada, pero estas comprobaciones
dependen de que aparezca el estado correspondiente en el reloj:

- [ ] Ver el borde suavizado durante un cambio real del progreso de pasos.
- [ ] Ver de 1 a 5 celdas al superar sucesivamente el objetivo.
- [ ] Ver el signo `+` al superar el 500% y confirmar la armadura vacía.
- [ ] Ver en Ambient Mode el emblema atenuado, la hora grande, la línea
      terminal, el marco dentado y el giro vertical simulado.

Estas comprobaciones quedan pendientes de validación visual en el caso real;
no son incidencias técnicas de compilación ni de instalación.
