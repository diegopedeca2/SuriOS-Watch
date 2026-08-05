\# WFPRD – Indicador de pasos v1.0



\# WFPRD

\## Indicador de pasos



\---



\## Estado documental



Aprobado



\---



\## Estado de implementación



Pendiente



\---



\## Versión



1.0



\---



\## Proyecto



SuriOS Watch



\---



\# Objetivo



Definir el comportamiento funcional y visual del indicador de pasos de SuriOS Watch.



Este documento describe la información mostrada, su comportamiento, actualización, representación y criterios de aceptación.



La distribución visual del componente se encuentra definida en WATCHFACE\_LAYOUT.



\---



\# Alcance



Este documento regula exclusivamente el indicador de pasos de la Watch Face.



No describe la implementación técnica.



No modifica la distribución visual definida en WATCHFACE\_LAYOUT.



No sustituye al Sprint correspondiente.



\---



\# Fuente de datos



El indicador utilizará exclusivamente el contador de pasos del reloj.



No utilizará información procedente del teléfono móvil.



No utilizará información procedente de aplicaciones externas.



No utilizará información procedente de otros dispositivos conectados.



\---



\# Información mostrada



\## Modo activo



Se mostrará únicamente:



\- número de pasos.



No se mostrará porcentaje.



No se mostrará objetivo diario.



No se mostrará barra de progreso.



No se mostrarán mensajes adicionales.



\---



\## Ambient Mode



Se mostrará únicamente:



\- número de pasos.



No se mostrarán elementos adicionales.



El comportamiento completo del Ambient Mode se define en ADR-002.



\---



\# Representación visual



La posición, tamaño, alineación y dimensiones del indicador serán las definidas en WATCHFACE\_LAYOUT.



El componente mantendrá la simetría respecto al indicador de batería.



La estética seguirá el Ecosystem Design Language (EDL).



\---



\# Formato de representación



El número de pasos se mostrará completo.



Se utilizará separación de miles mediante espacio.



Ejemplos:



0



987



1 234



12 345



123 456



No se utilizarán abreviaturas.



Ejemplos no permitidos:



1.2K



12K



125K



\---



\# Estados del indicador



\## Funcionamiento normal



El indicador mostrará exclusivamente el número de pasos proporcionado por el sistema.



No existirán estados visuales diferentes.



No existirán colores diferentes.



No existirán mensajes asociados.



No existirán objetivos diarios.



\---



\# Actualización



El indicador se actualizará cuando el sistema registre un nuevo paso.



Cada nuevo paso deberá reflejarse en la Watch Face.



No se realizarán actualizaciones periódicas innecesarias.



\---



\# Colores



Todos los elementos utilizarán la paleta definida por el EDL.



No cambiarán de color en función del número de pasos.



No existirán estados en rojo, amarillo u otros colores.



\---



\# Tipografía



Se utilizará la tipografía aprobada oficialmente para el proyecto.



Mientras no exista una aprobación definitiva permanecerá la solución temporal autorizada.



\---



\# Interacción



En la versión 1.0 el indicador no tendrá interacción.



No responderá al toque.



No abrirá aplicaciones.



No mostrará información adicional.



Su función será exclusivamente informativa.



\---



\# Evolución prevista



En una versión futura del proyecto se estudiará la posibilidad de que el indicador de pasos actúe como acceso directo a la aplicación nativa de estadísticas del reloj mediante doble pulsación.



Esta funcionalidad no forma parte del alcance de la versión actual.



No deberá implementarse hasta disponer de un ADR y un Sprint específicos que definan su comportamiento.



\---



\# Casos especiales



\## 0 pasos



El indicador mostrará:



\--



\---



\## Reinicio diario



Al comienzo de un nuevo día el contador diario se reiniciará automáticamente mostrando el nuevo valor proporcionado por el sistema.



\---



\## Sin permisos



El indicador mostrará:



N/A



\---



\## Error



El indicador mostrará:



N/A



\---



\## Valor elevado



El número continuará mostrándose completo utilizando separación de miles.



No se utilizarán abreviaturas.



\---



\# Restricciones



No modificar la posición definida en WATCHFACE\_LAYOUT.



No modificar la jerarquía visual.



No introducir animaciones.



No introducir cambios de color.



No introducir objetivos diarios.



No introducir porcentajes.



No introducir barras de progreso.



No añadir interacción.



\---



\# Dependencias



WATCHFACE\_LAYOUT



EDL



ADR-003



SPRINT\_004



\---



\# Criterios de aceptación



El componente será aceptado cuando:



\- obtenga los pasos exclusivamente del reloj;

\- muestre correctamente el número de pasos;

\- actualice el contador con cada nuevo paso detectado;

\- mantenga el formato numérico completo con separación de miles;

\- respete la posición y dimensiones definidas en WATCHFACE\_LAYOUT;

\- mantenga la simetría respecto al indicador de batería;

\- respete la identidad visual definida por el EDL;

\- no introduzca interacción;

\- no introduzca objetivos diarios;

\- no modifique colores en función del número de pasos.



\---



\# Observaciones



La representación visual del componente se encuentra definida en WATCHFACE\_LAYOUT.



El indicador mostrará únicamente el número de pasos registrado por el reloj.



La simplicidad del componente constituye una decisión de diseño deliberada para mantener la claridad visual de la Watch Face.



Las decisiones de implementación técnica corresponden exclusivamente al Sprint autorizado.



Toda modificación funcional futura deberá aprobarse previamente mediante actualización de este documento antes de incorporarse al Sprint correspondiente.

