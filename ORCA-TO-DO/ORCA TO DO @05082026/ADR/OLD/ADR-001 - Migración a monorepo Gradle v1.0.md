ADR-001 — Migración a un único build Gradle multiproyecto

Estado

Aprobado

Fecha

2026-08-05

Propietario de la decisión

Diego Pérez de Camino

Proyecto afectado

Ecosistema SuriOS

Proyectos afectados:

PIP-SuriOS

SuriOS Watch

Contexto

El repositorio actual contiene dos proyectos Gradle independientes:

El proyecto principal de PIP-SuriOS, situado en la raíz del repositorio.

El proyecto de SuriOS Watch, situado dentro de la carpeta watch/.

Cada proyecto dispone actualmente de su propia configuración Gradle, incluyendo:

settings.gradle.kts

build.gradle.kts

catálogo de versiones

Gradle Wrapper

propiedades Gradle

configuración de Android Studio

Esta estructura ha permitido iniciar ambos proyectos de forma separada y ha sido suficiente durante el desarrollo inicial de SuriOS Watch.

Sin embargo, ambos proyectos pertenecen al mismo ecosistema, comparten repositorio, documentación, identidad visual y planificación general.

Mantener dos builds Gradle completamente independientes aumenta la duplicación de archivos y el mantenimiento necesario.

Problema

La estructura actual obliga a mantener por separado:

dos Gradle Wrappers;

dos catálogos de versiones;

dos configuraciones raíz;

dos conjuntos de propiedades Gradle;

dos puntos de apertura y compilación;

versiones potencialmente diferentes de plugins y herramientas.

Esto puede provocar con el tiempo:

divergencias entre versiones;

errores de configuración;

mayor complejidad para abrir el repositorio;

duplicación de mantenimiento;

dificultad para ejecutar validaciones conjuntas;

confusión sobre cuál es la raíz técnica del proyecto.

Además, una futura integración entre PIP-SuriOS y SuriOS Watch resultaría más difícil si ambos proyectos continúan utilizando infraestructuras Gradle separadas.

Objetivo

Migrar el repositorio a un único build Gradle multiproyecto que permita administrar PIP-SuriOS y SuriOS Watch desde una sola raíz.

La migración debe ser exclusivamente técnica y no debe modificar el comportamiento, diseño ni alcance funcional de ninguno de los dos productos.

Alternativas consideradas

Alternativa A — Mantener los dos builds Gradle independientes

Consiste en conservar la estructura actual.

Ventajas:

No requiere migración.

Reduce el riesgo inmediato.

Cada proyecto puede configurarse de forma totalmente independiente.

Inconvenientes:

Mantiene la duplicación.

Aumenta el mantenimiento futuro.

Facilita la divergencia de versiones.

Obliga a ejecutar compilaciones por separado.

Dificulta una futura integración entre proyectos.

Esta alternativa se descarta como estructura definitiva.

Alternativa B — Utilizar builds compuestos de Gradle

Consiste en conservar ambos builds independientes y relacionarlos mediante un composite build.

Ventajas:

Mantiene cierta independencia.

Permite conectar builds distintos.

Puede ser útil para proyectos desarrollados o publicados de forma separada.

Inconvenientes:

Añade complejidad innecesaria para el tamaño actual del ecosistema.

Mantiene wrappers y configuraciones duplicadas.

Resulta más difícil de entender y mantener para el propietario.

No aporta una ventaja clara frente a un build multiproyecto sencillo.

Esta alternativa no se adopta en la fase actual.

Alternativa C — Crear un único build Gradle multiproyecto

Consiste en utilizar una sola raíz Gradle con varios módulos.

Ventajas:

Un único Gradle Wrapper.

Un único catálogo de versiones.

Una sola configuración raíz.

Apertura conjunta desde Android Studio.

Posibilidad de compilar y validar todos los módulos desde una sola orden.

Menor duplicación.

Mayor coherencia técnica.

Mejor preparación para futuros recursos o módulos compartidos.

Inconvenientes:

Requiere una migración controlada.

Puede afectar rutas y configuraciones.

Debe validarse que ambos módulos mantienen su funcionamiento.

Un error en la configuración raíz puede afectar a los dos proyectos.

Esta es la alternativa aprobada.

Decisión

Se adopta un único build Gradle multiproyecto para el repositorio SuriOS.

El repositorio mantendrá dos productos diferenciados, pero ambos serán administrados desde la misma raíz Gradle.

Los módulos mínimos serán:

app para PIP-SuriOS.

watchface para SuriOS Watch.

La estructura objetivo será conceptualmente similar a:

WristOS/

├── app/

├── watchface/

├── docs/

├── gradle/

├── build.gradle.kts

├── settings.gradle.kts

├── gradle.properties

├── gradlew

├── gradlew.bat

└── .gitignore

La estructura exacta podrá ajustarse durante la preparación técnica del Sprint 002, siempre que respete esta decisión y no vuelva a crear dos builds Gradle independientes.

Principios obligatorios de la migración

La migración debe cumplir las siguientes reglas:

Debe existir un único settings.gradle.kts en la raíz técnica del repositorio.

Debe existir un único Gradle Wrapper.

Debe existir un único catálogo principal de versiones.

PIP-SuriOS y SuriOS Watch deben continuar siendo módulos funcionalmente independientes.

La migración no debe introducir dependencias innecesarias entre ambos módulos.

No se compartirán recursos o código únicamente para justificar la nueva estructura.

La Watch Face debe conservar exactamente el resultado aprobado en el Sprint 001.

PIP-SuriOS debe mantener su comportamiento anterior.

No se implementarán funcionalidades nuevas durante la migración.

No se modificarán decisiones visuales ni requisitos de producto.

No se iniciará Ambient Mode, batería, pasos, Spotify ni Google Wallet.

La migración debe poder revertirse mediante Git si falla.

Alcance técnico autorizado

La implementación de esta decisión podrá afectar a:

configuración Gradle raíz;

settings.gradle.kts;

build.gradle.kts;

gradle.properties;

catálogo de versiones;

Gradle Wrapper;

rutas de módulos;

nombres técnicos de módulos;

archivos .gitignore;

configuración necesaria para importar el proyecto en Android Studio;

comandos de compilación documentados.

Podrá ser necesario mover la carpeta del módulo Watch Face desde:

watch/watchface/

a una ubicación dependiente directamente de la raíz, por ejemplo:

watchface/

La ruta definitiva deberá definirse en el Sprint 002 antes de ejecutar el movimiento.

Fuera de alcance

Esta decisión no autoriza:

modificaciones funcionales;

cambios visuales;

cambios en watchface.xml;

actualización del recurso preview.png;

incorporación de Ambient Mode;

incorporación de batería o pasos;

incorporación de Spotify o Google Wallet;

refactorización del código de PIP-SuriOS;

creación de módulos compartidos sin una necesidad aprobada;

cambios de identificadores de aplicación sin necesidad técnica;

actualización indiscriminada de dependencias;

modificación del EDL o de los requisitos del producto;

eliminación de archivos históricos sin revisión.

Consecuencias positivas

Después de la migración:

Android Studio podrá abrir el ecosistema desde una sola raíz.

Ambos módulos podrán compilarse desde el mismo Gradle Wrapper.

Se reducirá la duplicación de configuración.

Las versiones de Gradle y Android Gradle Plugin serán coherentes.

Será más sencillo ejecutar comprobaciones generales.

Se facilitará la incorporación futura de módulos compartidos cuando exista una necesidad real.

La estructura será más comprensible para colaboradores técnicos.

El repositorio quedará mejor preparado para crecer.

Consecuencias negativas

La nueva estructura también implica:

mayor impacto potencial de errores en la configuración raíz;

necesidad de validar ambos módulos después de cada cambio importante de Gradle;

posible necesidad de adaptar rutas en documentación o herramientas;

mayor cuidado al diferenciar tareas propias de cada módulo;

una migración inicial que debe realizarse de forma controlada.

Estas consecuencias se consideran aceptables frente a las ventajas de mantenimiento.

Riesgos

Pérdida o modificación accidental de archivos

La migración implicará mover o eliminar configuraciones duplicadas.

Mitigación:

trabajar sobre un repositorio Git limpio;

crear un punto estable previo;

no borrar archivos hasta verificar la nueva estructura;

revisar el diff antes del commit.

Rotura de rutas Gradle

Los módulos pueden dejar de ser reconocidos si las rutas no se actualizan correctamente.

Mitigación:

definir las rutas en settings.gradle.kts;

compilar los módulos individualmente;

ejecutar una compilación conjunta.

Divergencia de versiones

Los dos proyectos podrían depender actualmente de configuraciones diferentes.

Mitigación:

comparar ambos catálogos de versiones;

conservar las versiones ya validadas;

no actualizar dependencias salvo que sea imprescindible para unificar el build.

Alteración funcional accidental

La migración podría incluir cambios no relacionados.

Mitigación:

prohibir cambios funcionales y visuales;

limitar los archivos afectados;

comparar el comportamiento antes y después;

validar en emulador y dispositivo cuando corresponda.

Configuración local de Android Studio

Los archivos .idea pueden provocar diferencias entre equipos.

Mitigación:

definir una política clara de versionado;

ignorar configuraciones locales no necesarias;

validar la apertura desde la raíz Gradle y no desde archivos locales del IDE.

Criterios de aceptación

La decisión se considerará correctamente implementada cuando:

Existe un único build Gradle en el repositorio.

Existe un único Gradle Wrapper.

Existe un único settings.gradle.kts raíz.

Existe un catálogo principal de versiones.

Los módulos app y watchface están incluidos en el build.

Android Studio puede abrir ambos módulos desde la raíz del repositorio.

PIP-SuriOS compila correctamente.

SuriOS Watch compila correctamente.

Puede ejecutarse una compilación conjunta desde la raíz.

El APK de SuriOS Watch se genera correctamente.

La Watch Face puede instalarse y conserva el resultado del Sprint 001.

No existen cambios visuales.

No existen cambios funcionales.

No se han incorporado funcionalidades de Sprints posteriores.

Los archivos duplicados de Gradle se han eliminado únicamente después de validar la nueva estructura.

El estado final de Git contiene exclusivamente los cambios autorizados de la migración.

El propietario ha aprobado el resultado.

Existe un commit específico y reversible para la migración.

Validaciones obligatorias

La migración deberá comprobarse mediante:

revisión de la estructura final;

sincronización Gradle;

compilación limpia de PIP-SuriOS;

compilación limpia de SuriOS Watch;

compilación conjunta desde la raíz;

generación del APK de ambos módulos cuando proceda;

instalación de la Watch Face en emulador;

comprobación visual de la esfera;

comparación con el Sprint 001;

revisión del estado Git;

verificación de ausencia de cambios funcionales.

La prueba física en Xiaomi Watch 2 podrá realizarse como validación adicional si el propietario lo considera necesario.

Plan de reversión

Antes de iniciar la migración debe existir un commit estable previo.

Si la migración falla o introduce regresiones:

No se realizarán correcciones funcionales para compensarla.

Se documentará el fallo.

Se detendrá la operación.

Se restaurará el último commit estable.

Se revisará el ADR o el Sprint antes de intentar una nueva migración.

No se permitirá dejar el repositorio en un estado intermedio parcialmente migrado.

Impacto documental

Esta decisión requiere actualizar o crear:

PROJECT\_GUIDE, para reflejar la nueva estructura y el modelo de trabajo.

WFPRD, únicamente en las referencias técnicas y roadmap que procedan.

SPRINT\_HISTORY, para incorporar el nuevo orden de Sprints.

SPRINT\_002, como documento operativo de la migración.

documentación técnica de compilación.

referencias a rutas antiguas de watch/, cuando dejen de ser válidas.

Los documentos históricos no deben reescribirse para fingir que la estructura nueva existía anteriormente.

Relación con el roadmap

La ejecución de esta decisión corresponde al:

Sprint 002 — Migración a un único build Gradle multiproyecto

El Sprint 002 será exclusivamente técnico.

Después de completar y aprobar la migración, el roadmap continuará con:

Sprint 003 — Ambient Mode.

Sprint 004 — Batería y pasos.

Sprint 005 — Spotify y Google Wallet.

Sprint 006 — Optimización, regresión y cierre.

Relación con otros ADR

Este ADR establece únicamente la arquitectura Gradle del repositorio.

No define:

el comportamiento de Ambient Mode;

la arquitectura de batería o pasos;

la integración de Spotify o Google Wallet;

recursos compartidos;

sincronización entre PIP-SuriOS y SuriOS Watch.

Estas decisiones deberán documentarse, cuando proceda, en ADR independientes.

Resultado esperado

Al finalizar el Sprint 002, el ecosistema SuriOS conservará dos productos diferenciados, pero podrá administrarse, abrirse, compilarse y validarse desde una única raíz Gradle.

La migración no cambiará lo que hacen los productos.

Cambiará únicamente cómo se organiza y mantiene su infraestructura técnica.

