# Análisis de vulnerabilidades — PIP-SuriOS v2.2

**Fecha:** 2026-08-27  
**Commit auditado:** `fcc4bb9`  
**Alcance:** `:app` en Galaxy A56, P.R.S. v1.0/v2.0, P.R.S. TESTING, `:remoteprobe` en Xiaomi Watch 2 y `:watchface`.

## Resultado ejecutivo

La revisión no encontró secretos, claves privadas ni credenciales incrustadas en el código versionado. Los componentes internos sensibles están correctamente restringidos (`RemoteProbeService` y `FileProvider` no están exportados) y la validación de nombres de sesión/archivos evita traversal en los repositorios revisados.

El sistema no debe considerarse seguro para una red no confiable. El enlace experimental Watch 2 ↔ A56 expone un gateway HTTP local sin autenticación ni cifrado, acepta entradas sin límites de tamaño suficientes y la app móvil permite backup de sus datos internos. Estos puntos son aceptables sólo para una prueba privada y controlada.

## Método y límites

- Revisión estática manual de manifests, permisos, componentes, almacenamiento, red, parsing y exportación.
- Búsqueda de secretos en el commit auditado mediante `git grep`.
- Revisión de dependencias Gradle resueltas y de los informes lint.
- Validación ejecutada: `gradlew test lint assembleDebug :app:connectedDebugAndroidTest`.
- No había `semgrep`, `osv-scanner`, `trivy`, `grype`, `gitleaks` ni OWASP Dependency-Check instalados; por tanto, no se declara una certificación SAST/SCA ni ausencia de CVE de dependencias.
- No se realizó una prueba de intrusión sobre una red real ni un fuzzing del gateway.

## Hallazgos

### SEC-01 — Gateway local sin autenticación y HTTP en claro

**Severidad:** Alta para una red no confiable; media para el hotspot privado del ensayo.  
**Estado:** Abierto, aceptado temporalmente para el prototipo.

**Evidencia:**

- `app/src/main/AndroidManifest.xml` habilita `android:usesCleartextTraffic="true"`.
- `watch/remoteprobe/src/main/AndroidManifest.xml` habilita el mismo comportamiento.
- `RemoteProbeGateway` escucha TCP en `28771`, responde a cualquier cliente que alcance el puerto y procesa `POST /prs/remote-probe/observations` sin token, firma ni identidad del Watch.
- El descubrimiento UDP/NSD tampoco autentica al host anunciado.

**Impacto:**

Cualquier equipo dentro de la red local puede leer o modificar el tráfico, enviar observaciones BLE falsas, contaminar las comparaciones `MATCHED`/RSSI y observar identificadores y advertising data. Un host malicioso también puede hacerse pasar por el gateway durante el descubrimiento.

**Mitigación recomendada:**

Introducir autenticación por sesión con secreto aleatorio, vincular el secreto al intercambio inicial y usar HTTPS/TLS o un canal autenticado equivalente. Restringir el listener a la interfaz de prueba y desactivar cleartext en builds operativas. Mientras tanto, utilizar sólo un hotspot aislado y no compartirlo con terceros.

### SEC-02 — Falta de límites robustos de entrada y recursos en el gateway

**Severidad:** Media.  
**Estado:** Abierto.

**Evidencia:**

- `RemoteProbeGateway` usa `Executors.newCachedThreadPool()` para clientes aceptados.
- El valor `Content-Length` se convierte directamente en una reserva de `ByteArray` mediante `readBytesExact`.
- La línea HTTP y las cabeceras se leen sin un límite explícito de longitud.
- `RemoteProbeProtocol.decodeBatch` crea un `JSONArray` sin límite explícito de bytes o número de elementos.

**Impacto:**

Un cliente de la LAN puede abrir muchas conexiones, anunciar cuerpos muy grandes o enviar JSON excesivo para provocar consumo de memoria, hilos, CPU o almacenamiento. No requiere ejecutar código en el A56: basta con alcanzar el puerto mientras el gateway está abierto.

**Mitigación recomendada:**

Limitar tamaño de línea/cabeceras/cuerpo, número de observaciones por lote, longitud de campos y total de observaciones por sesión; rechazar valores fuera de rango; usar un executor acotado y aplicar rate limit por cliente. Añadir tests de límites y fuzzing del parser.

### SEC-03 — Backup de datos internos de la app móvil

**Severidad:** Media de privacidad.  
**Estado:** Abierto.

**Evidencia:**

- `app/src/main/AndroidManifest.xml` establece `android:allowBackup="true"`.
- `data_extraction_rules.xml` y `backup_rules.xml` no excluyen `filesDir`.
- Se almacenan sesiones de Remote Probe, CSV de Testing, operaciones y otros datos locales en el almacenamiento privado de la app.

**Impacto:**

Los backups o transferencias de dispositivo pueden incluir identificadores BLE, advertising data, RSSI, notas de campo y registros de operaciones. El sandbox de Android protege el acceso normal en el dispositivo, pero no elimina la exposición derivada de un backup autorizado o restaurado en otro terminal.

**Mitigación recomendada:**

Excluir `remote-probe/`, `sonar-testing/` y cualquier registro sensible de Auto Backup/device transfer, o desactivar backup para builds de campo. Si se necesita conservarlos, cifrarlos con una clave protegida por Android Keystore y documentar retención/borrado.

### SEC-04 — Telemetría BLE identificable conservada y exportable

**Severidad:** Baja-media de privacidad.  
**Estado:** Abierto.

**Evidencia:**

`deviceIdentifier`, RSSI, timestamp, nombre y `advertisingDataHex` se guardan en CSV/NDJSON. `P.R.S. TESTING` comparte su CSV mediante `FileProvider`.

**Impacto:**

Aunque el identificador no se presenta como identidad permanente, un conjunto de capturas puede permitir correlacionar dispositivos durante una toma y revelar contexto espacial o temporal. El usuario puede compartir el CSV con aplicaciones externas.

**Mitigación recomendada:**

Aplicar retención limitada, consentimiento explícito y exportación bajo acción deliberada. Para uso real, cifrar el almacenamiento o permitir una opción de anonimización/hash con saltos por sesión. Mantener la limitación documentada de que las direcciones privadas pueden rotar.

### SEC-05 — Posible inyección de fórmulas en CSV compartido

**Severidad:** Baja.  
**Estado:** Abierto.

**Evidencia:**

El escape actual protege la sintaxis CSV, pero no neutraliza valores de texto que comiencen por `=`, `+`, `-` o `@`. `deviceName`, `notes` y otros campos pueden proceder de datos externos o de entrada del usuario.

**Impacto:**

Al abrir el CSV en algunas hojas de cálculo, un campo especialmente construido podría interpretarse como fórmula. El riesgo queda limitado al usuario que decide exportar y abrir el archivo.

**Mitigación recomendada:**

Sanitizar campos textuales al exportar (por ejemplo, anteponer apostrofo), o documentar el CSV como datos sin confianza y ofrecer una exportación segura para hojas de cálculo.

## Controles positivos verificados

- No se encontraron secretos ni claves privadas en el commit auditado.
- `RemoteProbeService` tiene `android:exported="false"`.
- `FileProvider` tiene `android:exported="false"` y sólo expone el árbol `sonar-testing/` mediante URI grants.
- Las actividades exportadas son únicamente los entry points launcher esperados.
- Los identificadores de sesión admitidos por los repositorios están restringidos mediante expresiones regulares.
- No hay WebView, código nativo, reflexión peligrosa ni ejecución de comandos del sistema en el alcance revisado.
- La app declara y comprueba `ACCESS_LOCAL_NETWORK` para Android 17/target 37 antes del tráfico local.
- Lint no reportó errores bloqueantes; las advertencias restantes son de mantenimiento, accesibilidad, orientación y API.

## Plan de remediación recomendado

1. Antes de cualquier uso fuera de una red controlada: autenticación de sesión + transporte cifrado.
2. Añadir límites y rate limiting al gateway y tests de entradas malformadas/grandes.
3. Excluir o cifrar las carpetas de telemetría en backups.
4. Definir política de retención y anonimización de identificadores BLE.
5. Sanitizar CSV y añadir SCA/SAST automatizado al pipeline.
6. Actualizar dependencias cuando se cierre el siguiente sprint técnico; lint identifica versiones más recientes disponibles, pero no ha demostrado una vulnerabilidad concreta.

## Conclusión

PIP-SuriOS v2.2 es apta para la prueba privada del sábado bajo una red local aislada y con el gateway expuesto sólo durante la toma. No es apta para una red compartida, uso operativo o distribución pública hasta cerrar SEC-01, SEC-02 y SEC-03.

## Referencias oficiales

- [Android: Cleartext communications](https://developer.android.com/privacy-and-security/risks/cleartext-communications)
- [Android: Network Security Configuration](https://developer.android.com/privacy-and-security/security-config)
- [Android: Backup security recommendations](https://developer.android.com/privacy-and-security/risks/backup-best-practices)
- [Android: Local network permission](https://developer.android.com/privacy-and-security/local-network-permission)
