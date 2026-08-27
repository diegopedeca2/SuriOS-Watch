# Auditoría técnica — PIP-SuriOS v2.3

**Fecha:** 2026-08-28  
**Alcance:** aplicación Android del A56, SET-UP, CURRENT GEAR, DATA, INVENTORY, P.R.S., módulo `remoteprobe` de Wear OS y watchface.

## Resultado ejecutivo

PIP-SuriOS v2.3 queda técnicamente preparada para continuar las pruebas experimentales.

- `versionName=2.3`, `versionCode=3` en la aplicación móvil.
- PIW-SuriOS Watch se mantiene en `versionName=2.0`, `versionCode=2`.
- El módulo P.R.S. REMOTE PROBE se mantiene independiente en `versionName=0.1`.
- SET-UP queda separado en INPUT y DATA y funciona en vertical mediante una actividad dedicada.
- OPERATOR y la configuración de loadout se guardan en repositorios locales de la aplicación.
- PRIMARY WEAPON acepta una réplica libre y mantiene ROLE como selector.
- WATCH 2 está incluido en los catálogos de ACCESORIES sin alterar la lógica de P.R.S.
- La auditoría no detectó errores de compilación, tests unitarios fallidos ni errores de lint bloqueantes.

La versión sigue siendo experimental. Los riesgos de red local, privacidad de telemetría BLE y límites del entorno Wear OS descritos en [SECURITY_AUDIT_V2.3](SECURITY_AUDIT_V2.3.md) permanecen vigentes.

## Arquitectura revisada

```text
app                         Aplicación principal del Galaxy A56
  data/                     Perfil de operador y configuración persistente
  ui/screens/               SET-UP, CURRENT GEAR e INVENTORY
  remoteprobe/              Gateway y comparación de dos nodos
watch/remoteprobe           P.R.S. REMOTE PROBE para Xiaomi Watch 2
watch/watchface             PIW-SuriOS Watch, independiente del escaneo
```

SET-UP se desacopla de la actividad principal mediante `SetUpActivity`, declarada en vertical. `LoadoutConfigurationRepository` conserva los valores de selección y `primaryWeaponText` permite compatibilidad simultánea con armas catalogadas y réplicas introducidas manualmente.

## Verificaciones realizadas

### Código y catálogo

- Se revisaron las rutas HOME → SET-UP → INPUT/DATA.
- Se revisó la navegación de retorno de SET-UP y el retorno desde la actividad de operador.
- Se verificó que CURRENT GEAR, STATUS y los snapshots usan el nombre libre cuando existe.
- Se revisaron las listas de ACCESORIES en INVENTORY, CURRENT GEAR, SET-UP, edición de operaciones y COMPLEMENTS.
- Se comprobó que WATCH 2 conserva una ficha informativa `UNDER CONSTRUCTION`.
- Se revisó que PIW-SuriOS y REMOTE PROBE no reciben cambios funcionales en este sprint.

### Compilación y tests

```text
gradlew test :app:assembleDebug :remoteprobe:assembleDebug :watchface:assembleDebug :app:lintAnalyzeDebug
git diff --check
```

Resultado: correcto.

### Despliegue y prueba manual

- Samsung Galaxy A56 conectado como `RZGYC07H0EX`: APK instalada, `versionCode=3`, `versionName=2.3`.
- Emulador Pixel conectado como `emulator-5554`: APK instalada y HOME operativo.
- SET-UP comprobado en el A56 con `rotation=0`.
- PRIMARY WEAPON comprobado con ROLE desplegable y WEAPON como `EditText`.
- DATA comprobado con acciones EDIT y DELETE y sin desbordamiento horizontal.
- WATCH 2 comprobado en ACCESORIES, incluida la lista desplazable.
- No se observaron `FATAL EXCEPTION` ni `AndroidRuntime` en los registros revisados.

## Limitaciones aceptadas

- El guardado local de SET-UP no sincroniza perfiles entre dispositivos.
- Los datos introducidos por el usuario quedan en almacenamiento privado de Android y están sujetos a la política de backup del dispositivo.
- RSSI no representa metros ni posición real.
- Las direcciones BLE privadas o rotatorias pueden impedir la correlación entre nodos.
- El transporte experimental del Remote Probe conserva las restricciones de seguridad documentadas.
- La ficha de WATCH 2 no contiene todavía una especificación de inventario completa.

## Conclusión

El alcance de Sprint 014 está implementado y validado. PIP-SuriOS v2.3 queda lista para el siguiente ciclo de pruebas sobre configuración personal persistente, manteniendo P.R.S. REMOTE PROBE y PIW-SuriOS Watch sin regresiones conocidas.

