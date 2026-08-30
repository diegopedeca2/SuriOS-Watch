ADR-001 — Migración a un único build Gradle multiproyecto
Identificador
ADR-001
Estado
Aprobado
Fecha
2026-08-05
Propietario de la decisión
Diego Pérez de Camino
Proyecto afectado
Ecosistema SuriOS
Productos afectados
PIP-SuriOS
SuriOS Watch
1. Contexto
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
2. Problema
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
3. Motivación
El ecosistema SuriOS está concebido para crecer mediante varios productos independientes que compartirán una misma identidad visual, documentación, principios arquitectónicos y procesos de desarrollo.
Aunque actualmente PIP-SuriOS y SuriOS Watch evolucionan como proyectos separados, ambos forman parte del mismo ecosistema y previsiblemente compartirán herramientas, recursos y procesos de validación.
Adoptar desde una fase temprana una arquitectura Gradle multiproyecto evita consolidar una estructura duplicada que, en el futuro, sería más costosa de mantener y migrar.
4. Objetivo
Migrar el repositorio a un único build Gradle multiproyecto que permita administrar PIP-SuriOS y SuriOS Watch desde una única raíz técnica.
La migración será exclusivamente de infraestructura.
No modificará el comportamiento funcional, el diseño ni el alcance de ninguno de los productos.
5. Alternativas consideradas
(Se mantiene exactamente el contenido del ADR v1.0.)
Alternativa A — Mantener dos builds independientes.
Alternativa B — Composite Build.
Alternativa C — Monorepo Gradle multiproyecto (aprobada).
6. Decisión
Se adopta un único build Gradle multiproyecto para el ecosistema SuriOS.
El repositorio continuará manteniendo dos productos diferenciados, pero ambos serán administrados desde una única infraestructura Gradle.
Los módulos mínimos serán:
app
watchface
La estructura objetivo será:
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
La organización definitiva podrá ajustarse durante el Sprint 002 siempre que se respete esta decisión y no vuelvan a existir dos builds Gradle independientes.
Principio fundamental
Esta decisión no modifica el comportamiento funcional de ninguno de los productos.
Únicamente modifica la forma en que el ecosistema SuriOS se organiza, compila y mantiene.
Todo cambio funcional continuará gestionándose mediante los correspondientes WFPRD y Sprint.
7. Principios obligatorios
(Se mantiene el contenido del ADR v1.0.)
8. Alcance técnico autorizado
(Se mantiene el contenido del ADR v1.0.)
9. No objetivos
Este ADR no pretende:
modificar el diseño de la Watch Face;
introducir nuevas funcionalidades;
reorganizar la documentación histórica;
cambiar el roadmap funcional;
modificar la identidad visual;
crear módulos compartidos sin necesidad demostrada;
actualizar dependencias por motivos ajenos a la migración;
modificar el comportamiento de PIP-SuriOS.
10. Fuera de alcance
(Se mantiene exactamente el contenido del ADR v1.0.)
11. Beneficios
Tras la migración:
Android Studio podrá abrir el ecosistema completo desde una sola raíz.
Existirá un único Gradle Wrapper.
Existirá una única configuración principal.
Existirá un único catálogo de versiones.
Se reducirá el mantenimiento duplicado.
Será más sencilla la validación conjunta.
Se preparará el repositorio para el crecimiento futuro.
Se facilitará el trabajo de colaboradores técnicos.
12. Inconvenientes
La migración también implica:
mayor impacto potencial de errores en la configuración raíz;
necesidad de validar ambos módulos después de cambios importantes;
posible adaptación de rutas;
una migración inicial controlada.
Estos inconvenientes se consideran aceptables frente a los beneficios obtenidos.
13. Riesgos
(Se mantiene el contenido del ADR v1.0.)
14. Criterios de aceptación
(Se mantiene el contenido del ADR v1.0.)
15. Validaciones obligatorias
(Se mantiene el contenido del ADR v1.0.)
16. Plan de reversión
(Se mantiene el contenido del ADR v1.0.)
17. Impacto documental
(Se mantiene el contenido del ADR v1.0.)
18. Relación con el roadmap
Sprint 002 — Migración a un único build Gradle multiproyecto.
Tras su aprobación:
Sprint 003 — Ambient Mode.
Sprint 004 — Batería y pasos.
Sprint 005 — Spotify y Google Wallet.
Sprint 006 — Optimización, regresión y cierre.
19. Relación con otros ADR
Este ADR define exclusivamente la arquitectura Gradle del repositorio.
No establece decisiones sobre:
Ambient Mode;
batería;
pasos;
Spotify;
Google Wallet;
recursos compartidos;
sincronización funcional entre productos.
Cada una de estas decisiones deberá documentarse mediante su propio ADR cuando resulte necesario.
20. Revisión futura
Esta decisión permanecerá vigente mientras continúe siendo la arquitectura más sencilla y mantenible para el ecosistema SuriOS.
Solo deberá revisarse si ocurre alguna de las siguientes circunstancias:
Gradle introduce cambios incompatibles con el modelo multiproyecto.
El ecosistema requiere una arquitectura distribuida distinta.
Existen limitaciones técnicas demostrables.
Se aprueba un nuevo ADR que sustituya expresamente a ADR-001.
Mientras ninguna de estas circunstancias se produzca, ADR-001 seguirá siendo la referencia arquitectónica oficial del ecosistema SuriOS.
21. Resultado esperado
Al finalizar el Sprint 002, el ecosistema SuriOS conservará dos productos diferenciados, pero podrá administrarse, abrirse, compilarse y validarse desde una única raíz Gradle.
La migración no modificará lo que hacen los productos.
Modificará únicamente la infraestructura técnica necesaria para desarrollarlos, compilarlos y mantenerlos de forma coherente.
Con ello se establece la base arquitectónica sobre la que evolucionará el ecosistema SuriOS durante los siguientes Sprint.