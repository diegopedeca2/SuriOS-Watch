# Análisis de vulnerabilidades — PIP-SuriOS v2.3

**Fecha:** 2026-08-28  
**Alcance:** `:app`, SET-UP, almacenamiento local, P.R.S., P.R.S. TESTING, `:remoteprobe` y `:watchface`.

## Resultado ejecutivo

La revisión no encontró secretos, claves privadas ni credenciales incrustadas en el código del sprint. SET-UP no añade red, componentes exportados ni ejecución dinámica. Sí amplía la información persistente del dispositivo con el perfil de operador y la configuración de loadout, por lo que la exposición de backups documentada en v2.2 sigue aplicando y se registra explícitamente como SEC-06.

El sistema continúa sin ser adecuado para una red no confiable o distribución pública. El transporte vigente del PROBE usa Wear OS Data Layer, pero su revisión de seguridad específica no está cerrada; los riesgos documentados del gateway HTTP pertenecen a la implementación histórica.

## Método y límites

- Revisión estática de manifests, permisos, componentes, almacenamiento, red, parsing y exportación.
- Búsqueda de patrones de secretos en el árbol del proyecto.
- Revisión de los informes de compilación y lint.
- Tests unitarios y validación manual en A56 y emulador.
- No se declara una certificación SAST/SCA: no estaban disponibles semgrep, OSV-Scanner, Trivy, Grype, Gitleaks ni OWASP Dependency-Check.
- No se realizó pentest de red real ni fuzzing del gateway.

## Hallazgos

### SEC-01 y SEC-02 — Riesgos heredados del gateway HTTP de REMOTE PROBE

Los hallazgos de gateway local sin autenticación, HTTP en claro y límites
insuficientes de entrada proceden de [SECURITY_AUDIT_V2.2](SECURITY_AUDIT_V2.2.md)
y quedan como antecedentes de la implementación retirada.

Estado: históricos; requieren una auditoría específica si se reintroduce un
gateway o si el transporte vigente demuestra un riesgo equivalente.

### SEC-03 a SEC-05 — Datos internos, telemetría BLE y exportación CSV

Se mantienen abiertos los riesgos de backup de datos internos, telemetría BLE
identificable e inyección de fórmulas en CSV. Su vigencia y mitigación deben
revisarse sobre el código actual antes de cualquier distribución pública.

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
- El transporte actual del Watch usa Data Layer; su revisión específica queda
  pendiente y no se extrapolan automáticamente los hallazgos del gateway antiguo.

## Recomendaciones antes de uso público

1. Auditar el transporte actual de Wear OS/Data Layer y su frontera de confianza.
2. Si se reintroduce un gateway, exigir autenticación, cifrado, límites de
   tamaño, rate limiting y tests de entradas grandes o malformadas.
3. Excluir o cifrar el perfil, loadout y telemetría en backups.
4. Definir una política de retención y anonimización de identificadores BLE.
5. Aplicar sanitización segura del CSV y análisis SCA/SAST automatizado.

## Conclusión

PIP-SuriOS v2.3 es apta para las pruebas experimentales previstas en una red local controlada. No debe considerarse una versión segura para redes compartidas, uso operativo o distribución pública hasta revisar el transporte vigente y cerrar o aceptar formalmente SEC-03, SEC-04, SEC-05 y SEC-06.

## Addendum de revisión de alcance — 2026-08-30

La revisión del Sprint 016 confirma que los hallazgos SEC-01 y SEC-02
describen el gateway HTTP de la implementación histórica de `REMOTE PROBE`.
Ese gateway y sus módulos antiguos ya no forman parte del árbol actual, que usa
los módulos `:probe`, `:probeprotocol` y el Data Layer de Wear OS. Por tanto,
SEC-01 y SEC-02 quedan como hallazgos históricos pendientes de una auditoría
específica del transporte vigente, no como evidencia directa del código actual.

SEC-03, SEC-04, SEC-05 y SEC-06 se mantienen abiertos hasta revisar backup,
retención, exportación y tratamiento de datos de usuario y telemetría BLE. Este
addendum no constituye una aprobación para distribución pública.
