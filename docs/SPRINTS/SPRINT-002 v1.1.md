# SPRINT_002.md

# Sprint 002 – Migración a un único build Gradle

---

## Proyecto

SuriOS Watch

---

## Tipo de Sprint

Infraestructura

---

## Estado documental

Aprobado

---

## Estado de implementación

Pendiente

---

## Prioridad

Alta

---

## Objetivo

Migrar la estructura actual del proyecto desde dos builds Gradle independientes a un único build Gradle multiproyecto, sin modificar el comportamiento funcional de la aplicación PIP-SuriOS ni de SuriOS Watch.

Este Sprint es exclusivamente técnico.

No introduce nuevas funcionalidades.

No modifica el diseño.

No altera requisitos del producto.

---

## Justificación

Actualmente el repositorio contiene dos proyectos Gradle completamente independientes:

- PIP-SuriOS
- SuriOS Watch

Ambos pertenecen al mismo ecosistema y comparten documentación, identidad visual y planificación.

Mantener dos wrappers, dos catálogos de versiones y dos configuraciones Gradle incrementa el mantenimiento y dificulta la evolución futura.

El objetivo es disponer de un único build Gradle con varios módulos.

Esta migración implementa la decisión arquitectónica aprobada en el ADR-001.

---

## Documentación de referencia

- ADR-001 – Migración a monorepo Gradle.
- PROJECT_GUIDE.
- EDL.
- WFPRD v1.3.

---

## Alcance

Incluido:

- Crear un único build Gradle.
- Integrar el módulo Watch Face dentro del proyecto principal.
- Mantener ambos módulos independientes funcionalmente.
- Compartir wrapper Gradle.
- Compartir catálogo de versiones.
- Compartir configuración raíz.
- Verificar compilación independiente de ambos módulos.
- Verificar apertura correcta desde Android Studio.

No incluido:

- Cambios funcionales.
- Cambios visuales.
- Ambient Mode.
- Batería.
- Pasos.
- Spotify.
- Wallet.
- Optimización.

---

## Restricciones

No modificar:

- WFPRD.
- EDL.
- Diseño.
- Recursos gráficos.
- XML de la Watch Face.
- Código funcional.
- Colores.
- Posiciones.
- Tamaños.

La migración deberá ser completamente transparente para el producto final.

---

## Archivos previsiblemente afectados

Configuración Gradle del proyecto raíz.

Configuración Gradle del módulo Watch.

settings.gradle.kts

build.gradle.kts

gradle/

gradlew

gradlew.bat

Archivos estrictamente necesarios para integrar el módulo Watch dentro del nuevo build multiproyecto.

No modificar archivos funcionales salvo necesidad técnica demostrable.

---

## Criterios de aceptación

Se considerará completado cuando:

- Exista un único build Gradle.
- Exista un único wrapper Gradle.
- Android Studio abra correctamente el proyecto.
- PIP-SuriOS compile correctamente.
- SuriOS Watch compile correctamente.
- La Watch Face continúe funcionando exactamente igual.
- No existan cambios funcionales.
- No existan cambios visuales.
- El APK generado sea válido.
- El proyecto pueda abrirse desde una única raíz.
- La estructura final del proyecto coincida con la arquitectura aprobada en ADR-001.

---

## Validaciones

- Compilación limpia.
- Compilación incremental.
- Instalación de la Watch Face.
- Comprobación visual.
- Comparación con Sprint 001.
- Verificación de ausencia de regresiones.

---

## Riesgos

- Pérdida de configuración Gradle.
- Errores de rutas.
- Dependencias rotas.
- Cambios accidentales en módulos.
- Configuración incorrecta de Android Studio.
- Pérdida accidental del historial Git durante la reorganización de directorios.

### Mitigación

- Realizar únicamente movimientos compatibles con Git.
- No reescribir el historial.
- Mantener commits pequeños y fácilmente reversibles.

---

## Entregables

- Proyecto reorganizado.
- Build Gradle único.
- Compilación satisfactoria de ambos módulos.
- Documentación técnica mínima de la migración.

---

## Definition of Done

El Sprint estará finalizado cuando:

- Todos los criterios de aceptación sean correctos.
- No existan regresiones.
- Se haya validado la compilación.
- Se haya validado la ejecución.
- El propietario apruebe el resultado.
- Se autorice el commit.

---

## Observaciones

Este Sprint no modifica el producto.

Su único objetivo es mejorar la arquitectura técnica del repositorio antes de continuar con el desarrollo funcional.

La implementación deberá seguir estrictamente la arquitectura definida en ADR-001.

El siguiente Sprint previsto será:

SPRINT 003 – Ambient Mode.