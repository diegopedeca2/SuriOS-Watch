ADR-002 — Estrategia de Ambient Mode

Identificador

ADR-002

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

SuriOS Watch se desarrolla mediante Watch Face Format (WFF) y está diseñado para mostrar información de forma permanente en la pantalla del reloj.

Los relojes Wear OS disponen de un modo de funcionamiento de bajo consumo denominado Ambient Mode, cuyo objetivo es reducir el consumo energético mientras el usuario no interactúa con el dispositivo.

El Sprint 001 se centró exclusivamente en la esfera activa, dejando Ambient Mode expresamente fuera de alcance.

Antes de iniciar su implementación resulta necesario definir una estrategia arquitectónica única que establezca los principios que deberán seguir todas las futuras decisiones relacionadas con Ambient Mode.

2\. Problema

Implementar Ambient Mode sin una estrategia previa puede provocar:

inconsistencias visuales entre el modo activo y el modo ambiente;

consumo energético superior al previsto;

riesgo de burn-in en pantallas OLED;

pérdida de legibilidad;

duplicación de criterios entre componentes;

decisiones diferentes para cada Sprint.

Además, Ambient Mode afectará a todos los elementos futuros de la esfera, por lo que definirlo componente a componente generaría una arquitectura difícil de mantener.

3\. Motivación

Ambient Mode debe entenderse como un comportamiento global de la Watch Face y no como una característica individual de cada componente.

Definir una estrategia única antes de su implementación permitirá mantener la coherencia visual del ecosistema SuriOS y reducirá la necesidad de modificar componentes ya implementados.

4\. Objetivo

Definir los principios arquitectónicos que regularán el funcionamiento de Ambient Mode durante toda la evolución de SuriOS Watch.

Este ADR no implementa Ambient Mode.

Únicamente establece las reglas que deberán respetar los futuros Sprint.

5\. Alternativas consideradas

Alternativa A — Reproducir exactamente la esfera activa

Ventajas:

implementación sencilla;

apariencia idéntica.

Inconvenientes:

consumo elevado;

mayor riesgo de burn-in;

desaprovecha las capacidades de Ambient Mode.

Descartada.

Alternativa B — Crear una esfera completamente distinta

Ventajas:

máxima optimización.

Inconvenientes:

rompe la identidad visual;

duplica el mantenimiento;

mayor complejidad.

Descartada.

Alternativa C — Mantener la identidad visual simplificando la representación

Ventajas:

mantiene la personalidad de SuriOS;

reduce consumo;

facilita el mantenimiento;

conserva la experiencia de usuario.

Esta alternativa queda aprobada.

6\. Decisión

Ambient Mode conservará la identidad visual de SuriOS Watch, pero mostrará únicamente la información imprescindible para el usuario.

No será una segunda esfera independiente.

Será una representación simplificada de la esfera principal.

Principio fundamental

El objetivo prioritario de Ambient Mode será minimizar el consumo energético sin comprometer la legibilidad.

7\. Principios obligatorios

Toda implementación de Ambient Mode deberá cumplir:

Mantener la identidad visual de SuriOS.

Mostrar únicamente información esencial.

Eliminar elementos decorativos.

Evitar animaciones.

Evitar actualizaciones innecesarias.

Reducir el número de elementos visibles.

Mantener una lectura inmediata.

No modificar la distribución general de la esfera.

No introducir funcionalidades exclusivas.

Mantener el mismo comportamiento documental que el modo activo.

8\. Alcance técnico autorizado

Podrán modificarse exclusivamente:

visibilidad de componentes;

colores específicos de Ambient Mode;

frecuencia de actualización;

representación gráfica;

configuración propia de WFF relacionada con Ambient Mode.

9\. No objetivos

Este ADR no pretende:

rediseñar la esfera;

modificar la distribución;

cambiar tipografías;

introducir nuevos componentes;

optimizar batería mediante cambios ajenos a Ambient Mode.

10\. Fuera de alcance

No autoriza:

implementar batería;

implementar pasos;

Spotify;

Google Wallet;

modificar la esfera activa;

optimizaciones generales de rendimiento.

11\. Beneficios

Mayor autonomía.

Menor riesgo de burn-in.

Consistencia visual.

Arquitectura sencilla.

Reglas comunes para todos los Sprint.

12\. Inconvenientes

Menor cantidad de información visible.

Necesidad de mantener dos representaciones.

Validaciones adicionales.

13\. Riesgos

Consumo superior al esperado

Mitigación:

validar sobre dispositivo físico.

Burn-in

Mitigación:

utilizar únicamente las capacidades recomendadas por WFF.

Pérdida de legibilidad

Mitigación:

validar sobre Xiaomi Watch 2.

14\. Criterios de aceptación

Ambient Mode se considerará correctamente implementado cuando:

Mantenga la identidad visual.

Reduzca la cantidad de información visible.

No incorpore animaciones.

No aparezcan elementos exclusivos.

Sea claramente legible.

Compile correctamente.

Funcione en emulador.

Funcione en Xiaomi Watch 2.

No altere el funcionamiento del modo activo.

15\. Validaciones obligatorias

compilación;

validación en emulador;

validación física;

comprobación visual;

revisión documental.

16\. Plan de reversión

Si Ambient Mode produce regresiones:

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

Este ADR regula el futuro Sprint 003.

No modifica Sprint 001 ni Sprint 002.

19\. Relación con otros ADR

Complementa ADR-001.

No modifica decisiones relacionadas con:

arquitectura Gradle;

batería;

pasos;

Spotify;

Google Wallet.

20\. Revisión futura

Este ADR permanecerá vigente hasta que:

cambien las capacidades de WFF;

Wear OS introduzca un nuevo modelo de Ambient Mode;

un nuevo ADR sustituya expresamente esta decisión.

21\. Resultado esperado

Al finalizar el Sprint correspondiente, SuriOS Watch dispondrá de un Ambient Mode coherente con la identidad visual del ecosistema, optimizado para el uso continuado y alineado con las capacidades de Watch Face Format.

La implementación deberá reducir el consumo energético sin alterar la experiencia general del usuario ni introducir diferencias funcionales entre el modo activo y el modo ambiente.

