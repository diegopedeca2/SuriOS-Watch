\# SPRINT\_003.md



\# Sprint 003 – Implementación de Ambient Mode



\---



\## Proyecto



SuriOS Watch



\---



\## Tipo de Sprint



Funcional



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



Implementar el comportamiento de Ambient Mode de SuriOS Watch conforme a la arquitectura definida en ADR-002.



Este Sprint incorpora exclusivamente el funcionamiento de Ambient Mode.



No modifica la distribución de la esfera.



No añade nuevos componentes.



No altera la identidad visual del producto.



\---



\## Justificación



Ambient Mode constituye el siguiente paso natural tras completar la arquitectura técnica del repositorio.



Su implementación permitirá disponer de una Watch Face preparada para un uso continuo respetando la identidad visual de SuriOS y estableciendo la base energética para los siguientes Sprint.



\---



\## Documentación de referencia



\- ADR-002 – Estrategia de Ambient Mode.

\- PROJECT\_GUIDE.

\- EDL.

\- WFPRD v1.3.



\---



\## Alcance



Incluido:



\- Implementación de Ambient Mode.

\- Adaptación de la Watch Face al estado ambiente.

\- Conservación de la identidad visual.

\- Configuración necesaria para el cambio entre modo activo y modo ambiente.

\- Validación del comportamiento en ambos estados.



No incluido:



\- Indicador de batería.

\- Indicador de pasos.

\- Spotify.

\- Google Wallet.

\- Nuevos componentes gráficos.

\- Reorganización del proyecto.

\- Optimizaciones finales.



\---



\## Restricciones



No modificar:



\- Distribución general de la esfera.

\- Posición de la hora.

\- Posición de la fecha.

\- Colores del modo activo.

\- Tipografía.

\- Recursos gráficos.

\- Arquitectura definida en ADR-002.



Toda modificación deberá limitarse exclusivamente al comportamiento del modo ambiente.



\---



\## Archivos previsiblemente afectados



Archivos de configuración de la Watch Face.



watchface.xml



Archivos estrictamente necesarios para implementar Ambient Mode.



No modificar archivos funcionales ajenos al alcance autorizado.



\---



\## Criterios de aceptación



Se considerará completado cuando:



\- Ambient Mode funcione correctamente.

\- La transición entre ambos modos sea estable.

\- La Watch Face conserve la identidad visual aprobada.

\- No existan errores de representación.

\- El modo activo permanezca sin modificaciones.

\- No existan regresiones respecto al Sprint 001.

\- La compilación sea correcta.

\- La instalación sea correcta.

\- La validación en Xiaomi Watch 2 sea satisfactoria.



\---



\## Validaciones



\- Compilación limpia.

\- Compilación incremental.

\- Validación del modo activo.

\- Validación de Ambient Mode.

\- Comparación con Sprint 001.

\- Validación visual.

\- Validación física en Xiaomi Watch 2.

\- Verificación de ausencia de regresiones.



\---



\## Riesgos



\- Representación incorrecta en Ambient Mode.

\- Diferencias entre emulador y dispositivo físico.

\- Consumo energético superior al esperado.

\- Cambios accidentales en el modo activo.

\- Limitaciones propias del formato Watch Face.



\### Mitigación



\- Mantener el alcance estrictamente limitado.

\- Validar continuamente en emulador.

\- Realizar validación física antes del cierre.

\- No modificar componentes fuera del modo ambiente.



\---



\## Entregables



\- Ambient Mode implementado.

\- Compilación satisfactoria.

\- Validación en emulador.

\- Validación en Xiaomi Watch 2.

\- Documentación mínima del Sprint.



\---



\## Definition of Done



El Sprint estará finalizado cuando:



\- Todos los criterios de aceptación sean correctos.

\- No existan regresiones.

\- El modo activo continúe funcionando igual que en Sprint 001.

\- Ambient Mode funcione correctamente.

\- El propietario apruebe el resultado.

\- Se autorice el commit.



\---



\## Observaciones



Este Sprint implementa exclusivamente Ambient Mode.



No incorpora nuevas funcionalidades visibles para el usuario fuera del comportamiento propio del modo ambiente.



La implementación deberá respetar íntegramente la estrategia definida en ADR-002.



El siguiente Sprint previsto será:



SPRINT 004 – Indicadores de batería y pasos.

