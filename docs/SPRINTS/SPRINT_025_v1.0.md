# Sprint 025 — Dependencias, licencias y créditos

---

document: SPRINT
project: SuriOS Ecosystem
version: 1.0
status: Cerrado; completado y validado
owner: Diego Pérez de Camino
date: 2026-08-31
predecessor: Sprint 024

---

## Estado

El Sprint 025 ha sido activado por autorización expresa del propietario y queda
cerrado tras completar la implementación, los gates técnicos y la validación
física de la nueva ruta `HOMESCREEN > INFORMATION` en el Samsung A56.

## Objetivo

Reducir la deuda controlada de dependencias y dejar documentada la procedencia
de los recursos gráficos y los créditos del proyecto sin introducir cambios
funcionales no aprobados.

## Alcance previsto

### 1. Actualización de dependencias — AUD-023-08

- Inventariar las versiones fijadas y seleccionar una actualización objetivo por
  módulo.
- Actualizar de forma controlada, registrando incompatibilidades y retrocesos.
- Ejecutar la matriz de regresión en Samsung A56, Samsung Z Flip 6, Samsung
  Watch Ultra y Xiaomi Watch 2 según el módulo afectado.
- Repetir tests unitarios, lint, ensamblados y pruebas instrumentadas en A56
  cuando el cambio afecte a Android.
- Mantener `master` estable y publicar solo después de completar la validación.

### 2. Licencias y procedencia del emblema

- Documentar autoría, procedencia, licencia o permiso de uso del emblema y de
  cada variante distribuida en el repositorio.
- Revisar los recursos externos adicionales y registrar expresamente los que no
  tengan licencia o procedencia suficiente.
- No inferir una licencia ni distribuir fuera del entorno privado mientras el
  registro no esté completo.

### 3. INFORMATION: agradecimientos y disclaimers

- Crear `INFORMATION` en la columna derecha de `HOMESCREEN`, debajo de `TOOLS`.
- Mover la pantalla actual desde `SET-UP > ACKNOWLEDGEMENTS` a
  `INFORMATION > ACKNOWLEDGEMENTS`, conservando el contenido y el carácter de
  solo lectura.
- Incorporar `CAINSHARK` con su reconocimiento correspondiente.
- Añadir `INFORMATION > DISCLAIMERS` con los avisos formales de propiedad
  intelectual, desarrollo, uso y distribución.
- Actualizar la documentación de usuario y la especificación de INFORMATION.
- Validar el acceso y el retorno en el Samsung A56.

## Registro de ejecución

### Dependencias adoptadas

| Componente | Antes | Adoptado | Decisión |
|---|---:|---:|---|
| Android Gradle Plugin | 9.3.2 | 9.3.2 | Se mantiene la versión estable compatible. |
| Gradle Wrapper | 9.5.0 | 9.5.0 | Se mantiene la versión exigida por AGP 9.3.2. |
| Kotlin | 2.2.10 | 2.4.10 | Actualización estable; se corrigió una firma de callback afectada. |
| Compose BOM | 2026.02.01 | 2026.08.00 | Actualización coordinada del stack Compose. |
| core-ktx | 1.10.1 | 1.19.0 | Actualización estable. |
| lifecycle-runtime-ktx | 2.6.1 | 2.11.0 | Actualización estable. |
| activity-compose | 1.8.0 | 1.13.0 | Actualización estable. |
| AndroidX Test JUnit | 1.1.5 | 1.3.0 | Actualización estable. |
| Espresso | 3.5.1 | 3.7.0 | Actualización estable. |
| Play Services Location | 21.3.0 | 21.4.0 | Actualización estable. |
| Play Services Wearable | 20.0.1 | 20.0.1 | Ya estaba en la versión estable objetivo. |
| JUnit | 4.13.2 | 4.13.2 | Se mantiene. |

`compileSdk` y `targetSdk` permanecen en 37. El código mantiene `minSdk 34`.

### Resultado técnico

- `test`: correcto para los módulos con pruebas y sin pruebas declaradas.
- `lint`: correcto, 0 incidencias en `app` y sin incidencias en los módulos
  revisados.
- `assemble`: correcto para app completa, `prsOnly`, PROBE, protocolo y
  watchfaces.
- `git diff --check`: correcto.
- Validación física: correcta en el Samsung A56. Se comprobó la entrada en
  `HOMESCREEN`, el submenú `INFORMATION`, el desplazamiento de
  `ACKNOWLEDGEMENTS` hasta `CAINSHARK`, el desplazamiento completo de
  `DISCLAIMERS` y el retorno a `HOMESCREEN`.

## Fuera de alcance

- Modificar el EDL v0.6, salvo que aparezca una nueva propuesta formal.
- Reintroducir el perfil CIVILIAN.
- Incorporar doble pulsación.
- Actualizar `preview.png` o recuperar material histórico.
- Modificar la funcionalidad actual de PROBE, P.R.S. o TERRAIN salvo regresiones
  causadas directamente por una dependencia.

## Criterios de aceptación previstos

- Existe una matriz de dependencias antes de modificar versiones.
- Las versiones adoptadas compilan en las variantes soportadas y no introducen
  errores de Lint.
- Se ejecutan y registran las pruebas obligatorias de BL-006 para cada módulo
  afectado.
- Existe un registro verificable de licencias, autoría y procedencia del
  emblema y de los recursos externos revisados.
- `INFORMATION`, `ACKNOWLEDGEMENTS` y `DISCLAIMERS` están implementados,
  documentados y validados físicamente.
- El Sprint queda documentado y publicado solo tras el cierre técnico.

## Dependencias de decisión para activar

La matriz inicial de versiones objetivo queda definida en el registro de
dependencias del Sprint. La licencia no se completará con datos inventados si
la procedencia no puede acreditarse.
