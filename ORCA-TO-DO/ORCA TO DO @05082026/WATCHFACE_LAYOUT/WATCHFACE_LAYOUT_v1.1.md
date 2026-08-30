\# WATCHFACE\_LAYOUT\_v1.1



\# WATCHFACE\_LAYOUT

\## Diseño visual de la Watch Face



\---



\## Estado documental



Aprobado



\---



\## Estado de implementación



En desarrollo



\---



\## Versión



1.1



\---



\## Proyecto



SuriOS Watch



\---



\# Objetivo



Definir la distribución visual definitiva de la Watch Face de SuriOS Watch.



Este documento constituye el plano maestro de la interfaz.



No describe la implementación técnica.



No sustituye al WFPRD.



No sustituye a los Sprint.



Los Sprint únicamente implementarán los componentes ya definidos en este documento.



\---



\# Filosofía de diseño



La esfera deberá poder interpretarse de un vistazo.



El usuario no deberá buscar información.



Cada elemento tendrá una prioridad visual claramente definida.



La estética deberá recordar a un terminal PIP-Boy moderno, manteniendo un aspecto limpio, técnico y minimalista.



La simetría tendrá prioridad sobre la ocupación completa del espacio.



\---



\# Lienzo



Resolución de referencia



450 × 450 px



Origen de coordenadas



(0,0) esquina superior izquierda.



Centro geométrico



X = 225



Y = 225



Todas las coordenadas de este documento constituyen una referencia de diseño.



Durante la implementación podrán ajustarse ligeramente para conseguir una alineación perfecta.



\---



\# Jerarquía visual



Prioridad 1



Hora



Prioridad 2



Fecha



Prioridad 3



Batería



Prioridad 4



Pasos



Prioridad 5



Spotify



Prioridad 6



Google Wallet



Prioridad 7



Elementos decorativos



\---



\# Distribución general



&#x20;               Día de la semana



&#x20;                    Hora



&#x20;                   Fecha



&#x20;     Pasos                     Batería





&#x20;     Wallet                 Spotify





&#x20;     Brotherhood of Steel



&#x20;           SuriOS Watch



\---



\# Componentes



\## Día de la semana



Estado



Pendiente



Posición



Centro superior



Centro X



225



Centro Y



78



Ancho recomendado



180



Alto recomendado



24



Alineación



Centro



Formato



Texto completo.



Ejemplo



LUNES



\---



\## Hora



Estado



Implementada



Centro X



225



Centro Y



125



Ancho recomendado



220



Alto recomendado



70



Formato



HH:MM



24 horas



Prioridad



Máxima



\---



\## Fecha



Estado



Implementada



Centro X



225



Centro Y



165



Ancho recomendado



180



Alto recomendado



28



Formato



DD/MM/AAAA



\---



\## Pasos



Estado



Pendiente



Centro X



105



Centro Y



225



Diámetro recomendado



88



Información



Número de pasos



Icono



Barra de progreso



Prioridad



Media



\---



\## Batería



Estado



Pendiente



Centro X



345



Centro Y



225



Diámetro recomendado



88



Información



Porcentaje



Icono



Barra de estado



Prioridad



Media



\---



\## Google Wallet



Estado



Pendiente



Centro X



145



Centro Y



335



Diámetro recomendado



64



Tipo



Acceso directo



No mostrará información adicional.



\---



\## Spotify



Estado



Pendiente



Centro X



305



Centro Y



335



Diámetro recomendado



64



Tipo



Acceso directo



No mostrará controles.



No mostrará carátulas.



No mostrará información de reproducción.



\---



\## Emblema Hermandad del Acero



Estado



Pendiente



Centro X



225



Centro Y



235



Tamaño recomendado



190 × 190



Opacidad



Muy baja.



Debe servir únicamente como elemento de identidad.



Nunca deberá dificultar la lectura.



\---



\## Identificación SuriOS



Estado



Pendiente



Centro X



225



Centro Y



390



Ancho recomendado



170



Alto recomendado



30



Contenido



Nombre del proyecto.



Lema del perfil.



Separadores gráficos.



\---



\# Simetría



Los indicadores de batería y pasos deberán ser perfectamente simétricos respecto al eje vertical.



Spotify y Wallet deberán mantener exactamente el mismo tamaño.



La distancia entre ambos indicadores deberá ser idéntica.



Toda la interfaz deberá respetar el eje central X=225.



\---



\# Paleta



Fondo



PipBlack



Texto principal



PipGreen



Texto secundario



PipGreenDim



No utilizar colores fuera del EDL.



\---



\# Tipografía



La tipografía será la aprobada oficialmente.



Mientras tanto permanecerá la solución temporal autorizada.



\---



\# Iconografía



Todos los iconos deberán compartir el mismo estilo.



Línea fina.



Estética terminal.



Sin rellenos innecesarios.



\---



\# Interacción



Spotify



Touch único.



Abrirá Spotify.



Google Wallet



Touch único.



Abrirá Google Wallet.



Hora



Sin interacción.



Fecha



Sin interacción.



Pasos



Sin interacción.



Batería



Sin interacción.



\---



\# Ambient Mode



Se implementará durante Sprint 003.



No modificará la distribución.



Únicamente modificará el comportamiento visual autorizado en ADR-002.



\---



\# Restricciones



No modificar:



\- posición de la hora;

\- posición de la fecha;

\- jerarquía visual;

\- simetría;

\- identidad SuriOS.



No introducir nuevos componentes sin aprobación documental.



\---



\# Dependencias



EDL



ADR-002



ADR-003



WFPRD



Cada Sprint implementará únicamente los componentes autorizados.



\---



\# Criterios de aceptación



La Watch Face deberá:



\- poder interpretarse en menos de un segundo;

\- mantener equilibrio visual;

\- mantener simetría;

\- respetar la paleta oficial;

\- mantener la identidad SuriOS;

\- permitir futuras ampliaciones sin reorganizar la interfaz.



\---



\# Observaciones



Este documento constituye el plano maestro de la interfaz de SuriOS Watch.



Las coordenadas aquí indicadas representan el diseño objetivo obtenido durante la fase de definición visual.



Durante la implementación podrán realizarse pequeños ajustes de alineación si son necesarios, siempre que no alteren la distribución, la jerarquía visual ni la identidad del diseño aprobadas en este documento.



Toda modificación visual futura deberá aprobarse primero en este documento antes de reflejarse en el WFPRD o en un Sprint.

