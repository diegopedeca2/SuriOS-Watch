\# SPRINT\_002.md



\# Sprint 002 – Migración a un único build Gradle



\---



\## Proyecto



SuriOS Watch



\---



\## Tipo de Sprint



Infraestructura



\---



\## Estado documental



Aprobado



\---



\## Estado de implementación



Pendiente



\---



\## Prioridad



Alta



\---



\## Objetivo



Migrar la estructura actual del proyecto desde dos builds Gradle independientes a un único build Gradle multiproyecto, sin modificar el comportamiento funcional de la aplicación PIP-SuriOS ni de SuriOS Watch.



Este Sprint es exclusivamente técnico.



No introduce nuevas funcionalidades.



No modifica el diseño.



No altera requisitos del producto.



\---



\## Justificación



Actualmente el repositorio contiene dos proyectos Gradle completamente independientes:



\- PIP-SuriOS

\- SuriOS Watch



Ambos pertenecen al mismo ecosistema y comparten documentación, identidad visual y planificación.



Mantener dos wrappers, dos catálogos de versiones y dos configuraciones Gradle incrementa el mantenimiento y dificulta la evolución futura.



El objetivo es disponer de un único build Gradle con varios módulos.



\---



\## Alcance



Incluido:



\- Crear un único build Gradle.

\- Integrar el módulo Watch Face dentro del proyecto principal.

\- Mantener ambos módulos independientes funcionalmente.

\- Compartir wrapper Gradle.

\- Compartir catálogo de versiones.

\- Compartir configuración raíz.

\- Verificar compilación independiente de ambos módulos.

\- Verificar apertura correcta desde Android Studio.



No incluido:



\- Cambios funcionales.

\- Cambios visuales.

\- Ambient Mode.

\- Batería.

\- Pasos.

\- Spotify.

\- Wallet.

\- Optimización.



\---



\## Restricciones



No modificar:



\- WFPRD.

\- EDL.

\- Diseño.

\- Recursos gráficos.

\- XML de la Watch Face.

\- Código funcional.

\- Colores.

\- Posiciones.

\- Tamaños.



La migración debe ser transparente para el producto.



\---



\## Archivos previsiblemente afectados



Configuración Gradle.



settings.gradle.kts



build.gradle.kts



gradle/



gradlew



gradlew.bat



watch/



Estructura de módulos.



No modificar archivos funcionales salvo necesidad técnica demostrable.



\---



\## Criterios de aceptación



Se considerará completado cuando:



\- Existe un único build Gradle.

\- Existe un único wrapper Gradle.

\- Android Studio abre correctamente el proyecto.

\- PIP-SuriOS compila correctamente.

\- SuriOS Watch compila correctamente.

\- La Watch Face continúa funcionando exactamente igual.

\- No existen cambios funcionales.

\- No existen cambios visuales.

\- El APK generado es válido.

\- El proyecto puede abrirse desde una única raíz.



\---



\## Validaciones



Compilación limpia.



Compilación incremental.



Instalación de la Watch Face.



Comprobación visual.



Comparación con Sprint 001.



Verificación de ausencia de regresiones.



\---



\## Riesgos



Pérdida de configuración Gradle.



Errores de rutas.



Dependencias rotas.



Cambios accidentales en módulos.



Configuración incorrecta de Android Studio.



\---



\## Entregables



Proyecto reorganizado.



Build Gradle único.



Compilación satisfactoria.



Documentación técnica mínima de la migración.



\---



\## Definition of Done



El Sprint estará finalizado cuando:



\- Todos los criterios de aceptación sean correctos.

\- No existan regresiones.

\- Se haya validado la compilación.

\- Se haya validado la ejecución.

\- El propietario apruebe el resultado.

\- Se autorice el commit.



\---



\## Observaciones



Este Sprint no modifica el producto.



Su único objetivo es mejorar la arquitectura técnica del repositorio antes de continuar con el desarrollo funcional.



El siguiente Sprint será:



SPRINT 003 – Ambient Mode.

