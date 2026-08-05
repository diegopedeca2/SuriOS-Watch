\# WFPRD – Indicador de batería v1.1



\# WFPRD

\## Indicador de batería



\---



\## Estado documental



Aprobado



\---



\## Estado de implementación



Pendiente



\---



\## Versión



1.1



\---



\## Proyecto



SuriOS Watch



\---



\# Objetivo



Definir el comportamiento funcional y visual del indicador de batería de SuriOS Watch.



Este documento describe la información mostrada, su comportamiento, representación gráfica, estados y criterios de aceptación.



La distribución visual del componente se encuentra definida en WATCHFACE\_LAYOUT.



\---



\# Alcance



Este documento regula exclusivamente el indicador de batería de la Watch Face.



No describe la implementación técnica.



No modifica la distribución visual definida en WATCHFACE\_LAYOUT.



No sustituye al Sprint correspondiente.



\---



\# Fuente de datos



El indicador utilizará exclusivamente la batería del reloj.



No utilizará información procedente del teléfono móvil.



No utilizará información procedente de auriculares.



No utilizará información procedente de otros dispositivos conectados.



\---



\# Información mostrada



\## Modo activo



Se mostrarán:



\- icono de batería;

\- porcentaje de batería.



No se mostrará barra de progreso.



\---



\## Ambient Mode



Se mostrará únicamente:



\- porcentaje de batería.



No se mostrará iconografía.



No se mostrarán elementos adicionales.



El comportamiento completo del Ambient Mode se define en ADR-002.



\---



\# Representación visual



La posición, tamaño, alineación y dimensiones del indicador serán las definidas en WATCHFACE\_LAYOUT.



El componente mantendrá la simetría respecto al indicador de pasos.



La estética seguirá el Ecosystem Design Language (EDL).



El icono de batería utilizará diez niveles gráficos de llenado.



El porcentaje continuará mostrando la capacidad con precisión del 1%.



\---



\# Representación gráfica



\## Nivel 10



██████████



100% – 91%



\---



\## Nivel 9



█████████□



90% – 81%



\---



\## Nivel 8



████████□□



80% – 71%



\---



\## Nivel 7



███████□□□



70% – 61%



\---



\## Nivel 6



██████□□□□



60% – 51%



\---



\## Nivel 5



█████□□□□□



50% – 41%



\---



\## Nivel 4



████□□□□□□



40% – 31%



\---



\## Nivel 3



███□□□□□□□



30% – 21%



\---



\## Nivel 2



██□□□□□□□□



20% – 11%



\---



\## Nivel 1



█□□□□□□□□□



10% – 1%



\---



\# Estados del indicador



\## Funcionamiento normal



Se mostrarán:



\- icono de batería;

\- porcentaje.



El porcentaje variará en incrementos del 1%.



El icono cambiará únicamente cuando el porcentaje alcance el siguiente intervalo definido en la representación gráfica.



\---



\## Cargando



Cuando el reloj entre en estado de carga:



\- desaparecerán el icono y el porcentaje;

\- se mostrará únicamente el texto:



RECHARGING



El texto ocupará exactamente el espacio reservado al indicador.



No se mostrarán elementos adicionales.



\---



\# Actualización



El indicador se actualizará únicamente cuando:



\- el nivel de batería cambie un 1%;

\- el reloj comience a cargarse;

\- el reloj deje de cargarse.



No se realizarán actualizaciones periódicas innecesarias.



\---



\# Colores



Todos los elementos utilizarán la paleta definida por el EDL.



No cambiarán de color en función del nivel de batería.



No existirán estados en rojo, amarillo u otros colores.



\---



\# Tipografía



Se utilizará la tipografía aprobada oficialmente para el proyecto.



Mientras no exista una aprobación definitiva permanecerá la solución temporal autorizada.



\---



\# Interacción



El indicador de batería no tendrá interacción.



No responderá al toque.



No abrirá ajustes.



No mostrará información adicional.



Su función será exclusivamente informativa.



\---



\# Casos especiales



\## Batería al 100%



Se mostrará:



\- icono completamente lleno;

\- porcentaje 100%.



\---



\## Batería entre 99% y 1%



El porcentaje se actualizará en incrementos del 1%.



El icono representará el intervalo correspondiente definido en la representación gráfica.



\---



\## Batería al 0%



No se contempla este estado como parte de la interfaz.



Se considera que el reloj dejará de mostrar la Watch Face antes de alcanzar un estado operativo del 0%.



\---



\## Carga iniciada



Se sustituirán inmediatamente el icono y el porcentaje por:



RECHARGING



\---



\## Carga finalizada



El indicador volverá automáticamente al modo normal mostrando:



\- icono;

\- porcentaje actualizado.



\---



\## Sin datos



No se contempla este estado.



Si el sistema no proporciona información de batería, la Watch Face no deberá representar un estado alternativo.



\---



\## Error



No se contempla un estado específico de error.



Se considera que un fallo en la obtención del nivel de batería impide el funcionamiento normal del sistema y queda fuera del alcance de este componente.



\---



\# Restricciones



No modificar la posición definida en WATCHFACE\_LAYOUT.



No modificar la jerarquía visual.



No introducir animaciones.



No introducir cambios de color.



No introducir información distinta al porcentaje.



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



\- obtenga la batería exclusivamente del reloj;

\- muestre correctamente el porcentaje;

\- actualice el porcentaje en incrementos del 1%;

\- represente correctamente los diez niveles gráficos definidos en este documento;

\- sustituya correctamente el indicador por el texto RECHARGING durante la carga;

\- mantenga la posición y dimensiones definidas en WATCHFACE\_LAYOUT;

\- respete la identidad visual establecida por el EDL;

\- no introduzca interacción;

\- no modifique colores en función del nivel de batería.



\---



\# Observaciones



La representación visual del componente se encuentra definida en WATCHFACE\_LAYOUT.



El porcentaje constituye la información principal del componente.



El icono actúa como apoyo visual y utiliza diez niveles de representación para facilitar una lectura rápida sin sustituir la precisión del porcentaje.



Las figuras incluidas en este documento representan conceptualmente el nivel de llenado del icono. No constituyen el recurso gráfico definitivo, que será diseñado siguiendo el estilo visual aprobado para SuriOS Watch.



Las decisiones de implementación técnica corresponden exclusivamente al Sprint autorizado.



Toda modificación funcional futura deberá aprobarse previamente mediante actualización de este documento antes de incorporarse al Sprint correspondiente.

