# Auditoría técnica — PIP-SuriOS v2.2

**Fecha:** 2026-08-27  
**Alcance:** aplicación Android del A56, P.R.S., P.R.S. TESTING, módulo `remoteprobe` de Wear OS y watchface existente.

## Resultado ejecutivo

La compilación debug v2.2 es apta para continuar las pruebas experimentales del sábado.

- Sin errores de compilación.
- Tests unitarios correctos.
- Lint correcto: 0 errores.
- APK móvil instalada y verificada en Galaxy A56 y emulador.
- Galaxy A56, Xiaomi Watch 2 y emulador visibles por ADB durante el despliegue.
- `versionName=2.2`, `versionCode=2`.
- Watchface `PIW-SuriOS`: `versionName=2.0`, `versionCode=2`.
- Las firmas visibles de la aplicación ya no contienen `PIP-SuriOS v2.1`.

Esto no constituye una certificación de producción: el Remote Probe sigue siendo experimental y depende del comportamiento BLE, Wi-Fi y Wear OS del entorno de campo.

La auditoría se realizó sobre un working tree con cambios acumulados de los Sprints 012 y 013, incluyendo trabajo previo del proyecto. No se hizo limpieza, reset ni eliminación de esos cambios; los resultados describen el estado real disponible.

## Arquitectura revisada

```text
app                         Aplicación principal del Galaxy A56
  sonar/                    Escaneo BLE y seguimiento de contactos
  sonartesting/             Calibración y muestras del nodo OPERATOR
  remoteprobe/              Gateway, almacenamiento y comparación de dos nodos
watch/remoteprobe           App Wear OS REMOTE PROBE para Xiaomi Watch 2
watch/watchface             Watch Face Format existente, independiente
```

La separación del watchface y la app `remoteprobe` es correcta para la prueba: el watchface no se modifica ni se utiliza como proceso de escaneo.

## Verificaciones realizadas

### Versión y despliegue

Se verificó en los dispositivos:

```text
Galaxy A56:  versionCode=2, versionName=2.2
Emulador:    versionCode=2, versionName=2.2
Watch 2:     app P.R.S. REMOTE PROBE instalada como módulo independiente
```

La metadata del módulo Wear sigue siendo independiente (`remoteprobe 0.1`) porque no es la versión de la aplicación móvil.

### Compilación y pruebas

Comandos ejecutados correctamente:

```text
:app:testDebugUnitTest
:app:assembleDebug
:remoteprobe:assembleDebug
:watchface:assembleDebug
:app:lintDebug
:remoteprobe:lintDebug
:watchface:lintDebug
```

La cobertura existente incluye almacenamiento, Morse, RADS, mapas, Sonar, P.R.S. TESTING, `PresenceScanner` y `RemoteProbeComparator`. No existe una prueba automatizada de radio BLE real ni de conectividad Wi-Fi entre dos dispositivos.

## Hallazgos

### H1 — HTTP local sin cifrado ni autenticación (riesgo medio/alto)

El gateway del A56 escucha en el puerto `28771` y las dos apps permiten cleartext HTTP. Las lecturas podrían ser observadas o inyectadas por otro dispositivo de la misma red.

Es aceptable únicamente para la prueba privada del sábado en una red controlada. Antes de uso operativo se debe añadir autenticación de sesión y transporte protegido, o restringir el servicio a una red local explícitamente confiable.

### H2 — El gateway depende de mantener abierta una pantalla experimental (riesgo medio)

El gateway se inicia mientras está abierta `P.R.S. v2.0` o `P.R.S. TESTING` en modo dual. Al salir de esas pantallas, el gateway se detiene y el Watch puede pasar a `DISCONNECTED`.

Por ello, la captura dual debe mantenerse dentro de una de esas pantallas durante toda la toma. `P.R.S. TESTING` exporta su CSV de calibración; `P.R.S. v2.0` conserva sus observaciones internamente y no ofrece exportación CSV.

### H3 — Identificador BLE no permanente (riesgo medio)

La correlación utiliza la dirección observada por Android/Wear OS. Las direcciones privadas o rotatorias pueden impedir que A56 y Watch correlacionen el mismo dispositivo. No hay una identidad permanente inventada, lo cual es correcto, pero puede producir `MATCHED: 0` durante una prueba.

### H4 — Restricciones de escaneo con pantalla apagada (riesgo medio)

El Watch utiliza foreground service y `LOW_LATENCY`, pero el escaneo sin filtros puede verse limitado al apagar completamente la pantalla. La primera medición debe hacerse con pantalla activa o Ambient Mode y después repetir la prueba con pantalla apagada, registrando el resultado.

### H5 — Advertencias de lint no bloqueantes (riesgo bajo)

Lint queda en cero errores, pero informa de:

- dependencias AndroidX/Kotlin con versiones más recientes disponibles;
- orientaciones fijas en actividades, incluida la app Wear;
- `scheduleAtFixedRate` en el cliente del Watch;
- constructor de custom View y `performClick` pendientes para accesibilidad;
- iconos o recursos sin uso en módulos existentes.

No bloquean la prueba del sábado. La accesibilidad del botón y `scheduleWithFixedDelay` son las mejoras prioritarias del siguiente ciclo técnico.

## Recomendación para el sábado

Usar el flujo documentado en [MANUAL_WATCH2_REMOTE_PROBE.md](MANUAL_WATCH2_REMOTE_PROBE.md):

1. Activar `P.R.S. REMOTE PROBE` en el Watch 2.
2. Abrir `P.R.S. TESTING` en modo dual o `P.R.S. v2.0` y mantenerla visible.
3. Confirmar `PROBE: CONNECTED`, `MATCHED` y los contadores de ambos nodos.
4. Capturar las posiciones A/B/C y conservar el CSV de Testing si se usa ese flujo.
5. Cerrar la toma en el Watch y anotar el identificador de sesión.
6. Revisar timestamps, identificadores y RSSI sin convertirlos en metros.

### H6 — Riesgos de seguridad del prototipo

El transporte local HTTP sin autenticación, el backup de datos de la app móvil y la falta de límites de tamaño en el gateway quedan analizados en [SECURITY_AUDIT_V2.2](../SECURITY_AUDIT_V2.2.md). Son aceptables únicamente para una prueba privada y controlada; no debe exponerse el gateway a una red no confiable.

## Conclusión

La versión v2.2 queda lista para una prueba de campo controlada. Los puntos funcionales prioritarios —escaneo BLE en ambos nodos, transmisión Wi-Fi local, identificación del nodo, registro bruto y comparación conservadora— están cubiertos y fueron desplegados en los dispositivos disponibles.

Los hallazgos H1–H4 deben considerarse límites explícitos de esta versión experimental; H5 queda como deuda técnica no bloqueante.
