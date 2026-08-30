\# 10. Ambient Mode



\## 10.1 Objetivo



Ambient Mode permitirá reducir el consumo energético de la Watch Face manteniendo la identidad visual de SuriOS.



El cambio entre modo activo y modo ambiente deberá ser transparente para el usuario.



Ambient Mode no constituye una Watch Face diferente.



Es un estado alternativo de la misma esfera.



\---



\## 10.2 Objetivos funcionales



Ambient Mode deberá:



\- Reducir el consumo energético.

\- Mantener la hora permanentemente visible.

\- Mantener una lectura rápida de la información.

\- Conservar la identidad visual de SuriOS.

\- Evitar modificaciones funcionales respecto al modo activo.



\---



\## 10.3 Componentes visibles



Durante Ambient Mode permanecerán visibles únicamente:



\- Hora.

\- Fecha.



No se mostrarán:



\- batería;

\- pasos;

\- Spotify;

\- Google Wallet;

\- futuros indicadores;

\- elementos decorativos adicionales.



\---



\## 10.4 Distribución



La posición de todos los elementos será exactamente la misma que en modo activo.



No se modificarán:



\- coordenadas;

\- tamaños;

\- alineaciones;

\- márgenes.



Ambient Mode únicamente modificará la representación visual autorizada.



\---



\## 10.5 Colores



Modo activo



Hora



PipGreen



Fecha



PipGreenDim



Fondo



PipBlack



Modo ambiente



Los colores deberán seguir la estrategia definida en ADR-002.



No se aprobarán colores distintos sin autorización expresa del propietario.



\---



\## 10.6 Tipografía



Se utilizará exactamente la misma tipografía aprobada para el modo activo.



Ambient Mode no introduce cambios tipográficos.



\---



\## 10.7 Comportamiento



Al entrar en Ambient Mode:



\- la transición deberá realizarse correctamente;

\- no deberán aparecer elementos inesperados;

\- la hora continuará actualizándose según las capacidades del formato Watch Face;

\- la fecha permanecerá disponible.



Al volver al modo activo:



\- la Watch Face recuperará inmediatamente su representación completa;

\- no existirán diferencias respecto al Sprint 001.



\---



\## 10.8 Restricciones



Ambient Mode no podrá:



\- modificar la distribución;

\- modificar el diseño;

\- modificar la identidad visual;

\- incorporar nuevas funcionalidades;

\- alterar el comportamiento de la Watch Face fuera del estado ambiente.



\---



\## 10.9 Validación



La implementación deberá validarse mediante:



\- compilación correcta;

\- instalación correcta;

\- validación en emulador;

\- validación física en Xiaomi Watch 2;

\- comprobación del paso entre ambos estados;

\- ausencia de regresiones respecto al Sprint 001.



\---



\## 10.10 Criterios de aceptación



Ambient Mode se considerará aprobado cuando:



\- funcione correctamente;

\- la transición entre estados sea estable;

\- la Watch Face mantenga la identidad visual aprobada;

\- no existan regresiones;

\- el modo activo permanezca inalterado;

\- el propietario apruebe la validación física.



\---



\## 10.11 Dependencias



La implementación depende de:



\- ADR-002 – Estrategia de Ambient Mode.

\- SPRINT\_003.



No depende de:



\- batería;

\- pasos;

\- Spotify;

\- Google Wallet.



Estas funcionalidades serán desarrolladas en Sprint posteriores.



\---



\## 10.12 Observaciones



Ambient Mode constituye exclusivamente una mejora del comportamiento energético de la Watch Face.



No modifica el producto desde el punto de vista funcional.



Toda ampliación futura deberá aprobarse mediante una nueva versión del WFPRD antes de iniciar el Sprint correspondiente.

