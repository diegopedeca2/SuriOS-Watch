ADR-003 — Arquitectura de batería y pasos

Identificador

ADR-003

Estado

Aprobado

Fecha

2026-08-05

Propietario de la decisión

Diego Pérez de Camino

Proyecto afectado

Ecosistema SuriOS

Productos afectados

SuriOS Watch

1\. Contexto

SuriOS Watch evolucionará mediante una serie de Sprint incrementales definidos en el WFPRD.

Tras completar la esfera base y establecer la estrategia de Ambient Mode, el siguiente conjunto de funcionalidades previsto corresponde a la incorporación de dos indicadores permanentes:

nivel de batería del reloj;

contador diario de pasos.

Ambos elementos forman parte de la información esencial que la Watch Face ofrecerá al usuario y estarán presentes durante la mayor parte del ciclo de vida del producto.

Antes de definir su diseño concreto resulta necesario establecer una arquitectura común que regule su comportamiento y su integración con el resto de la esfera.

2\. Problema

Implementar batería y pasos como componentes independientes, sin reglas comunes, puede provocar:

criterios visuales distintos;

comportamientos incoherentes;

duplicación de lógica documental;

diferentes métodos de validación;

dificultades para mantener la identidad visual del ecosistema.

Además, ambos indicadores dependerán de fuentes de datos del sistema, por lo que resulta conveniente establecer previamente una arquitectura uniforme.

3\. Motivación

Los indicadores de batería y pasos constituyen información permanente de la Watch Face.

Su comportamiento debe ser homogéneo, predecible y fácilmente mantenible durante toda la evolución del proyecto.

Definir una arquitectura común antes de implementar cada componente permitirá reducir decisiones repetidas y garantizar la coherencia del producto.

4\. Objetivo

Definir los principios arquitectónicos que regularán la incorporación de los indicadores de batería y pasos.

Este ADR no implementa ninguno de los dos componentes.

Tampoco define:

posición;

tamaño;

colores específicos;

iconografía;

representación gráfica;

comportamiento visual detallado.

Estas decisiones continuarán correspondiendo al WFPRD y al Sprint autorizado.

5\. Alternativas consideradas

Alternativa A — Componentes completamente independientes

Cada indicador define su propio comportamiento, validación y representación.

Ventajas

máxima libertad de implementación.

Inconvenientes

duplicación de criterios;

mantenimiento más complejo;

pérdida de coherencia.

Descartada.

Alternativa B — Arquitectura común con implementaciones específicas

Los dos indicadores comparten principios arquitectónicos, pero cada uno conserva su lógica funcional.

Ventajas

coherencia;

mantenimiento sencillo;

reutilización documental;

evolución independiente cuando sea necesario.

Esta alternativa queda aprobada.

6\. Decisión

Batería y pasos se consideran una única familia de componentes informativos.

Compartirán la misma filosofía de diseño, validación y mantenimiento.

Cada indicador conservará su propia fuente de datos y representación funcional.

Principio fundamental

Todos los indicadores permanentes deberán seguir una arquitectura común antes de incorporar decisiones particulares.

7\. Principios obligatorios

Toda implementación deberá cumplir:

Utilizar datos procedentes del sistema.

No duplicar información ya visible en Wear OS.

Mantener la identidad visual de SuriOS.

Ser claramente legible.

Mantener un comportamiento estable.

No interferir con la lectura de la hora.

Compartir criterios de validación.

Compartir criterios documentales.

Mantener independencia funcional entre ambos indicadores.

Poder evolucionar individualmente sin romper la arquitectura común.

8\. Alcance técnico autorizado

Podrán definirse exclusivamente:

arquitectura general;

responsabilidades de cada indicador;

principios comunes;

integración con la esfera;

criterios generales de validación.

9\. No objetivos

Este ADR no pretende:

decidir posiciones;

decidir tamaños;

elegir iconos;

definir colores concretos;

determinar porcentajes;

establecer animaciones;

implementar la lectura de sensores;

modificar Ambient Mode.

10\. Fuera de alcance

No autoriza:

Spotify;

Google Wallet;

cambios en la esfera principal;

modificaciones del roadmap;

optimizaciones generales;

decisiones de diseño gráfico.

11\. Beneficios

Arquitectura uniforme.

Mayor mantenibilidad.

Menor duplicación documental.

Validaciones comunes.

Evolución independiente de cada indicador.

Mayor coherencia visual.

12\. Inconvenientes

Mayor trabajo inicial de planificación.

Necesidad de documentar principios antes de implementar.

Posibles revisiones futuras si Wear OS modifica sus capacidades.

13\. Riesgos

Fuentes de datos

Las APIs disponibles pueden imponer restricciones.

Mitigación

Validar técnicamente la viabilidad antes de implementar.

Legibilidad

Los nuevos indicadores pueden reducir el espacio disponible.

Mitigación

Validar el diseño durante el Sprint correspondiente.

Consumo energético

La obtención de datos puede afectar a la autonomía.

Mitigación

Respetar la estrategia definida en ADR-002.

14\. Criterios de aceptación

La arquitectura se considerará correctamente aplicada cuando:

ambos indicadores compartan los mismos principios;

exista una única estrategia documental;

la implementación sea coherente con el EDL;

no existan dependencias innecesarias entre ambos componentes;

el comportamiento permanezca estable;

compile correctamente;

supere las validaciones del Sprint correspondiente.

15\. Validaciones obligatorias

revisión documental;

validación técnica;

compilación;

validación en emulador;

validación física en Xiaomi Watch 2.

16\. Plan de reversión

Si alguno de los indicadores introduce regresiones:

detener el Sprint;

restaurar el último commit estable;

documentar el problema;

revisar este ADR antes de una nueva implementación.

17\. Impacto documental

Este ADR afectará a:

WFPRD;

EDL;

Sprint correspondiente;

documentación técnica de validación.

18\. Relación con el roadmap

Este ADR regula el Sprint destinado a la incorporación de batería y pasos.

No modifica:

Sprint 001;

Sprint 002;

ADR-001;

ADR-002.

19\. Relación con otros ADR

Complementa:

ADR-001 — Arquitectura del repositorio.

ADR-002 — Estrategia de Ambient Mode.

No establece decisiones relativas a:

Spotify;

Google Wallet;

componentes interactivos.

20\. Revisión futura

Este ADR permanecerá vigente mientras batería y pasos continúen formando parte de la familia de indicadores permanentes de SuriOS Watch.

Toda revisión requerirá aprobación expresa del propietario del proyecto y deberá documentarse mediante una nueva versión o mediante un ADR que sustituya explícitamente a este.

21\. Resultado esperado

Al finalizar el Sprint correspondiente, SuriOS Watch incorporará los indicadores de batería y pasos siguiendo una arquitectura común, coherente con la identidad visual del ecosistema y alineada con el resto de decisiones arquitectónicas aprobadas.

La implementación deberá mantener una separación clara entre la arquitectura definida en este ADR y las decisiones funcionales y de diseño que correspondan al WFPRD y al Sprint autorizado.

