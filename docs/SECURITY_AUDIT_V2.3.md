# Análisis de vulnerabilidades — PIP-SuriOS v2.3

**Fecha:** 2026-08-28  
**Alcance:** `:app`, SET-UP, almacenamiento local, P.R.S., P.R.S. TESTING, `:remoteprobe` y `:watchface`.

## Resultado ejecutivo

La revisión no encontró secretos, claves privadas ni credenciales incrustadas en el código del sprint. SET-UP no añade red, componentes exportados ni ejecución dinámica. Sí amplía la información persistente del dispositivo con el perfil de operador y la configuración de loadout, por lo que la exposición de backups documentada en v2.2 sigue aplicando y se registra explícitamente como SEC-06.

El sistema continúa sin ser adecuado para una red no confiable o distribución pública. El gateway experimental del Watch 2 sigue utilizando transporte local sin autenticación ni cifrado, únicamente aceptable en una red de prueba aislada.

## Método y límites

- Revisión estática de manifests, permisos, componentes, almacenamiento, red, parsing y exportación.
- Búsqueda de patrones de secretos en el árbol del proyecto.
- Revisión de los informes de compilación y lint.
- Tests unitarios y validación manual en A56 y emulador.
- No se declara una certificación SAST/SCA: no estaban disponibles semgrep, OSV-Scanner, Trivy, Grype, Gitleaks ni OWASP Dependency-Check.
- No se realizó pentest de red real ni fuzzing del gateway.

## Hallazgos

### SEC-01 a SEC-05 — Riesgos heredados de P.R.S. REMOTE PROBE

Se mantienen sin cambios los hallazgos de [SECURITY_AUDIT_V2.2](SECURITY_AUDIT_V2.2.md): gateway local sin autenticación y HTTP en claro, límites insuficientes de entrada, backup de datos internos, telemetría BLE identificable y posible inyección de fórmulas en CSV.

Estado: abiertos y aceptados únicamente para pruebas privadas y controladas.

### SEC-06 — Persistencia de perfil y loadout introducidos por el usuario

**Severidad:** Media de privacidad.  
**Estado:** Abierto, aceptado temporalmente.

`OperatorProfileRepository` y `LoadoutConfigurationRepository` guardan en `SharedPreferences` los campos de OPERATOR y la configuración de SET-UP. El almacenamiento está protegido por el sandbox normal de Android, pero los datos pueden quedar incluidos en backup o transferencia del dispositivo según la configuración del terminal.

Impacto: exposición del nombre, callsign, país, equipo y nombres de réplicas si el backup autorizado se restaura o comparte fuera del dispositivo previsto.

Mitigación recomendada: excluir estos datos en builds de campo, cifrarlos con Android Keystore o documentar una política explícita de retención y borrado antes de cualquier distribución.

## Controles positivos verificados

- SET-UP no expone actividades nuevas fuera de la aplicación (`SetUpActivity` no exportada).
- No se añadieron permisos de red, WebView, código nativo ni reflexión peligrosa.
- No se encontraron secretos o claves privadas en el código revisado.
- La compatibilidad con armas catalogadas no se elimina al añadir texto libre.
- `WATCH 2` se incorpora como dato de catálogo y no modifica el perímetro de red.
- El gateway y el servicio del Watch mantienen las restricciones ya documentadas.

## Recomendaciones antes de uso público

1. Autenticación de sesión y transporte cifrado para el gateway.
2. Límites de tamaño, rate limiting y tests de entradas grandes o malformadas.
3. Exclusión o cifrado del perfil, loadout y telemetría en backups.
4. Política de retención y anonimización de identificadores BLE.
5. Sanitización segura del CSV y análisis SCA/SAST automatizado.

## Conclusión

PIP-SuriOS v2.3 es apta para las pruebas experimentales previstas en una red local controlada. No debe considerarse una versión segura para redes compartidas, uso operativo o distribución pública hasta cerrar SEC-01, SEC-02, SEC-03 y SEC-06.

