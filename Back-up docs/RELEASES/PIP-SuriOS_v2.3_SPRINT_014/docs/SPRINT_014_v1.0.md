# Sprint 014 - SET-UP vertical, datos persistentes y WATCH 2

## Estado

- Apertura: 2026-08-28.
- Cierre: 2026-08-28.
- Estado: cerrado.
- Version movil consolidada: PIP-SuriOS v2.3 (`versionCode 3`).
- Version de la watchface: PIW-SuriOS v2.0 (`versionCode 2`).
- App experimental del reloj: P.R.S. REMOTE PROBE (`versionName 0.1`).
- Sprint activo posterior: ninguno.

## Objetivo

Reorganizar SET-UP para que sea un flujo de configuracion vertical y reutilizable, permitiendo que cada usuario introduzca sus propios datos de operador y sus combinaciones de roles y replicas.

## Alcance implementado

### SET-UP

- SET-UP pasa a una actividad independiente bloqueada en orientacion vertical.
- El nivel raiz contiene unicamente `INPUT` y `DATA`.
- `INPUT` contiene OPERATOR, PRIMARY WEAPON, SECONDARY WEAPON, ACCESORIES, HEADGEAR, FRONT PANEL y UNIFORM.
- `DATA` permite consultar los datos guardados y ofrece `EDIT` y `DELETE` para los campos de operador y la configuracion primaria.
- OPERATOR conserva los campos ID, NAME, CALLSIGN, NUMBER, COUNTRY y TEAM.
- ROLE permanece como selector desplegable.
- WEAPON pasa a ser un campo de texto libre para introducir cualquier replica.

### Persistencia y CURRENT GEAR

- Los datos de operador se guardan mediante `OperatorProfileRepository`.
- La configuracion de SET-UP se guarda mediante `LoadoutConfigurationRepository`.
- Se mantiene compatibilidad con las armas catalogadas existentes.
- `CURRENT GEAR`, STATUS, DATA y los snapshots de operaciones utilizan el nombre libre de la replica cuando existe.
- Las modificaciones de SET-UP se recargan al volver a la actividad principal sin alterar el flujo de APPLY del Loadout Activo.

### WATCH 2 en ACCESORIES

- Se incorpora `WATCH 2` al catalogo de accesorios junto a los elementos existentes, incluido MINI KNIFE.
- La entrada aparece en INVENTORY, CURRENT GEAR, SET-UP, edicion de operaciones y COMPLEMENTS.
- La ficha de inventario queda marcada `UNDER CONSTRUCTION` hasta disponer de contenido especifico.

## Fuera de alcance

- No se modifica la logica de escaneo, gateway, comparacion o persistencia de P.R.S. REMOTE PROBE.
- No se cambia la version de PIW-SuriOS Watch ni del modulo independiente REMOTE PROBE.
- No se implementan skins nuevas ni una refactorizacion general de navegacion.

## Validacion

Comandos ejecutados correctamente:

```text
gradlew test :app:assembleDebug :remoteprobe:assembleDebug :watchface:assembleDebug :app:lintAnalyzeDebug
git diff --check
```

Validacion manual:

- Galaxy A56: SET-UP abre en vertical (`rotation=0`), muestra INPUT/DATA, permite editar la replica libre y presenta EDIT/DELETE sin desbordamiento.
- Galaxy A56: WATCH 2 aparece en la lista desplegable de ACCESORIES y permanece accesible mediante desplazamiento.
- Emulador Pixel: APK instalada y HOME operativo.
- Galaxy A56 y emulador: `versionCode=3`, `versionName=2.3`.
- No se observaron `FATAL EXCEPTION` ni `AndroidRuntime` en los ultimos registros revisados.

## Riesgos y deuda aceptada

- La configuracion de SET-UP contiene datos personales y nombres introducidos por el usuario; la politica de backup de Android sigue siendo un punto de privacidad heredado de v2.2.
- El modulo P.R.S. REMOTE PROBE conserva los riesgos de red local y correlacion BLE documentados en la auditoria de seguridad anterior.
- La ficha de WATCH 2 permanece informativa y sin contenido tecnico detallado.

## Cierre

Sprint 014 queda cerrado tecnica, funcional y documentalmente. PIP-SuriOS v2.3 queda preparada para continuar las pruebas sobre la base de configuracion personal persistente. No existe un Sprint activo posterior.
