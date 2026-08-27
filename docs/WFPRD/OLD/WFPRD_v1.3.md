# SuriOS Watch — WFPRD v1.3

**Watch Face Product Requirements Document**  
**Proyecto:** SuriOS Watch  
**Ecosistema:** SuriOS  
**Versión:** 1.3  
**Estado documental:** Aprobado y vigente  
**Documento de origen:** WFPRD v1.2

---

# 0. Control documental

## 0.1 Estado del documento

WFPRD v1.3 es la evolución completa y autocontenida de WFPRD v1.2. Conserva todas las decisiones vigentes de la versión anterior, amplía su definición y las reorganiza dentro de una estructura estable. No constituye un documento nuevo ni invalida el historial previo.

Esta versión ha sido aprobada expresamente por el propietario y se encuentra vigente. El estado documental y el estado de implementación son independientes: una decisión puede estar aprobada y pendiente de implementación, y una base técnica puede funcionar sin que todas sus decisiones futuras estén cerradas.

Los estados de cada capítulo describen su madurez documental, no su grado de implementación.

El **Contrato de interpretación para agentes de IA** definido en el apartado 18.8 forma parte del contenido normativo del WFPRD y es de obligado cumplimiento para cualquier IA que participe en el desarrollo del proyecto.

## 0.2 Propietario

**Propietario:** Diego Pérez de Camino.

El propietario mantiene la autoridad final sobre visión, alcance, prioridades, diseño, recursos, excepciones, versiones, Sprints y aceptación. Ninguna propuesta de una persona o IA se considerará aprobada sin su validación expresa.

## 0.3 Documento padre

El **PROJECT_GUIDE** gobierna la metodología. El **EDL** es el documento padre de diseño. WFPRD concreta esas reglas para SuriOS Watch. La secuencia obligatoria es:

1. PROJECT_GUIDE.
2. EDL.
3. WFPRD.
4. Sprint aprobado.
5. Implementación.
6. Pruebas.
7. Commit.

Una limitación técnica no autoriza a modificar diseño o requisitos. La discrepancia se documentará, se propondrán alternativas y el propietario aprobará la resolución.

## 0.4 Documentos relacionados

- **PROJECT_GUIDE:** flujo documental, roles, versiones, Git y Definition of Done.
- **EDL:** identidad, paleta, tipografía, componentes, interacción y reglas para IA.
- **MRPD:** fuente de verdad de PIP-SuriOS; aplicable a decisiones compartidas del ecosistema.
- **GLOSSARY:** terminología oficial.
- **ACTIVE_SPRINT y SPRINT_HISTORY:** planificación vigente e historial operativo.
- **WFPRD v1.2:** versión inmediatamente anterior y origen íntegro de esta versión.
- **Figura 4.1 de WFPRD v1.1:** referencia visual oficial vigente; su ubicación material debe confirmarse antes de aprobar v1.3.

## 0.5 Historial resumido

| Versión | Estado | Descripción |
|---|---|---|
| 1.1 | Aprobada | Incluyó la Figura 4.1, que continúa como referencia visual oficial. |
| 1.2 | Sustituida por v1.3 | Definió alcance, perfil CIVILIAN, distribución, Ambient Mode, Sprints, aceptación, riesgos y backlog. |
| 1.3 | Aprobada y vigente | Reorganiza y amplía v1.2 sin eliminar requisitos; incorpora arquitectura documental y trazabilidad. |

## Estado

**Aprobado y vigente.** La implementación del producto continúa de forma incremental.

---

# 1. Introducción

## 1.1 Propósito

SuriOS Watch es una Watch Face para Xiaomi Watch 2, basada en Wear OS, integrada en SuriOS y alineada con el EDL. El WFPRD define el producto, sus reglas visuales, sus componentes, sus límites y los criterios utilizados para diseñarlo, implementarlo y validarlo.

No es un tutorial, una especificación de código ni un registro de tareas. Una función descrita solo será implementable cuando esté suficientemente definida y forme parte de un Sprint aprobado.

## 1.2 Contexto

SuriOS Watch traslada la filosofía del ecosistema a un dispositivo circular de consulta inmediata. Comparte identidad con PIP-SuriOS, pero es un producto independiente y no hereda automáticamente sus módulos.

La inspiración RobCo y Brotherhood of Steel se expresa mediante orden, sobriedad, alto contraste y lenguaje técnico, sin copia literal de interfaces o recursos de terceros.

## 1.3 Audiencia

Este documento está dirigido al propietario, ChatGPT, Codex, Orca, futuras IA, diseñadores, desarrolladores y revisores. Debe poder utilizarse sin consultar conversaciones anteriores.

## 1.4 Terminología

- **SuriOS:** ecosistema completo.
- **SuriOS Watch:** proyecto de esfera para Wear OS.
- **WFPRD:** Watch Face Product Requirements Document.
- **EDL:** Ecosystem Design Language.
- **CIVILIAN:** perfil cotidiano y prioritario.
- **OPERATION:** perfil táctico pendiente de una fase posterior.
- **Ambient Mode:** estado de bajo consumo.
- **Componente:** unidad visual o funcional definida.
- **Safe Area:** región segura frente al borde circular.
- **Decisión pendiente:** aspecto no autorizado como solución definitiva.

## Estado

**Completado.** Propósito, contexto, audiencia y terminología definidos.

---

# 2. Objetivos del producto

## 2.1 Objetivo general

Crear una esfera útil, legible, estable, mantenible y reconocible como parte de SuriOS. Debe mostrar información esencial de un vistazo sin sacrificar claridad, rendimiento o consumo por decoración.

## 2.2 Objetivos específicos

- Mostrar hora y fecha con máxima claridad.
- Integrar progresivamente batería, pasos, Spotify y Google Wallet.
- Adaptar la composición a pantalla circular.
- Compartir identidad con el ecosistema sin replicar PIP-SuriOS.
- Preparar una arquitectura modular y verificable.
- Mantener un Ambient Mode reconocible y contenido.
- Validar en emulador y, para el cierre CIVILIAN, en Xiaomi Watch 2.

## 2.3 Principios del producto

1. Utilidad antes que decoración.
2. Legibilidad antes que fidelidad estética.
3. Información esencial antes que densidad.
4. Coherencia antes que variedad.
5. Mantenibilidad antes que velocidad.
6. Evolución incremental y documentada.
7. Inspiración sin copia literal.
8. Ningún elemento ocupa espacio sin utilidad.
9. Ninguna limitación técnica cambia el diseño automáticamente.
10. Toda decisión no aprobada se identifica como pendiente.

## Estado

**Completado.** Objetivos y principios definidos.

---

# 3. Alcance

## 3.1 Funcional

Incluye únicamente Main Watch Face y Ambient Mode. El perfil activo prioritario es CIVILIAN.

## 3.2 Visual

Comprende fondo, hora, fecha, jerarquía, tipografía, paleta, distribución circular, símbolo institucional, indicadores, accesos, estados y recursos aprobados.

## 3.3 Incluido

- Hora `HH:MM`.
- Fecha `DD/MM/AAAA`.
- Batería del reloj.
- Pasos.
- Acceso a Spotify.
- Acceso a Google Wallet.
- Símbolo institucional cuando exista recurso aprobado.
- Ambient Mode con hora y fecha.
- Compilación, instalación y validación.

La inclusión no autoriza implementación fuera del Sprint correspondiente.

## 3.4 Excluido

- Tiles.
- Aplicación Wear OS independiente.
- Pantallas secundarias.
- Configuración avanzada.
- Complicaciones configurables.
- Sincronización con PIP-SuriOS.
- Cambio automático de perfil.
- Batería del teléfono.
- Biometría.
- Funciones sociales, publicidad, compras, rankings o puntuaciones.

Los elementos también presentes en backlog permanecen fuera del alcance actual.

## 3.5 Restricciones

- Plataforma Wear OS y dispositivo inicial Xiaomi Watch 2.
- Pantalla circular y compatibilidad AMOLED.
- Fondo negro puro.
- Cumplimiento de PROJECT_GUIDE, EDL y WFPRD.
- Dependencia mínima de conectividad permanente.
- Desarrollo declarativo mediante Watch Face Format mientras sea la base aprobada.
- Recursos externos sujetos a procedencia, licencia y validación.
- Integraciones sujetas a las capacidades reales de Wear OS.

## 3.6 Perfiles

### CIVILIAN

Prioridad alta. En desarrollo. Orientado al uso cotidiano y objeto principal de esta versión.

### OPERATION

Pendiente. Se desarrollará en una fase posterior y no cuenta todavía con definición suficiente para implementación.

## Estado

**En desarrollo.** Alcance consolidado; integraciones y perfil OPERATION pendientes.

---

# 4. Filosofía de diseño

## 4.1 Principios

La esfera es un instrumento de consulta. Un elemento solo es admisible cuando comunica información, permite una acción, establece jerarquía o refuerza identidad sin perjudicar la lectura. Se priorizan sobriedad, claridad, modularidad y adaptación al contexto circular.

## 4.2 Jerarquía visual

1. Hora.
2. Fecha.
3. Información funcional prioritaria.
4. Indicadores.
5. Accesos.
6. Identidad institucional.
7. Contenido futuro aprobado.

La jerarquía se expresa mediante tamaño, posición, contraste, espaciado y densidad; no exclusivamente mediante color.

## 4.3 Legibilidad

La información debe leerse en el tamaño real del dispositivo. Se prohíben superposiciones, contraste insuficiente, formatos ambiguos, contenido crítico junto al borde, tipografía decorativa y densidad excesiva.

## 4.4 Coherencia

Todos los componentes compartirán sistema de coordenadas, paleta, escala, alineación, iconografía, terminología y estados predecibles. La esfera activa y Ambient Mode deben percibirse como estados del mismo producto.

Toda decisión de jerarquía, legibilidad y coherencia debe alinearse obligatoriamente con el EDL.

## Estado

**Completado.** Filosofía y reglas generales definidas.

---

# 5. Identidad visual

## 5.1 Identidad SuriOS

La identidad se construye mediante PipBlack, verde terminal, tipografía monoespaciada, composición técnica, iconografía coherente y ornamentación mínima. Debe reconocerse incluso sin símbolo institucional.

## 5.2 Lenguaje visual

Debe transmitir precisión, utilidad, instrumentación, sobriedad y fiabilidad. No se permiten sombras, degradados, brillos decorativos, texturas que reduzcan legibilidad, mezcla arbitraria de estilos ni copia literal de terceros.

## 5.3 Elementos distintivos

- Paleta Pip.
- Tipografía monoespaciada.
- Distribución radial.
- Equilibrio entre batería y pasos.
- Equilibrio entre Spotify y Wallet.
- Símbolo de la Brotherhood of Steel difuminado y subordinado.
- Figura 4.1 de v1.1 como referencia visual oficial vigente.

El recurso, licencia, tamaño, posición, opacidad y tratamiento definitivo del símbolo siguen pendientes.

## 5.4 Consistencia

La paleta, tipografía, iconografía, estructura y terminología deben mantenerse entre componentes y estados. Toda excepción identificará la regla afectada, justificará la necesidad, evaluará alternativas y requerirá aprobación previa.

La identidad visual y sus aplicaciones deben alinearse obligatoriamente con el EDL.

## Estado

**En desarrollo.** Base definida; tipografía final, iconografía y símbolo pendientes.

---

# 6. Arquitectura de la esfera

## 6.1 Estructura

La escena se organiza en componentes independientes, identificables y modificables sin efectos colaterales innecesarios.

## 6.2 Capas

1. Fondo.
2. Identidad institucional subordinada.
3. Información principal.
4. Información secundaria.
5. Indicadores y accesos.
6. Estados contextuales aprobados.

## 6.3 Regiones

- 12 h: hora `HH:MM` y fecha `DD/MM/AAAA`.
- 3 h: batería.
- 5 h: Spotify.
- 7 h: Google Wallet.
- 9 h: pasos.
- Centro: símbolo institucional futuro.

La reserva conceptual del centro no requiere estructuras vacías.

## 6.4 Jerarquía

La hora domina; la fecha la complementa. Batería y pasos mantienen equilibrio lateral. Spotify y Wallet forman un par inferior. El símbolo no compite con la información.

## 6.5 Adaptación a pantallas circulares

El contenido crítico permanecerá dentro de áreas seguras. Se evitarán textos largos en los laterales, recortes y uso rectangular del lienzo circular.

## Estado

**En desarrollo.** Arquitectura base definida; componentes futuros pendientes.

---

# 7. Diseño de interfaz

## 7.1 Composición

La composición combina lectura vertical superior y distribución radial secundaria. La Figura 4.1 de v1.1 continúa siendo referencia visual oficial. Su ubicación material debe verificarse antes de aprobar esta versión.

## 7.2 Retícula

El lienzo lógico actual es 450 × 450. Se aplica la cuadrícula base de 8 unidades del EDL, adaptada proporcionalmente cuando el centrado o la tipografía lo exijan.

## 7.3 Safe Areas

El área segura varía según la altura y anchura del componente. No se fija un margen global hasta completar la validación física.

## 7.4 Espaciados

La fecha permanece vinculada a la hora; el bloque superior deja espacio al centro; los pares laterales e inferiores conservan separación y equilibrio.

## 7.5 Estados

Se contemplan esfera activa, Ambient Mode y estados interactivos definidos por componente. No se inventarán estados genéricos sin función.

## Estado

**En desarrollo.** Composición base aprobada; validación física pendiente.

---

# 8. Tipografía

## 8.1 Fuente principal

El EDL establece Consolas. Su uso definitivo requiere validar compatibilidad, disponibilidad, licencia y legibilidad en Xiaomi Watch 2.

## 8.2 Fuente alternativa

No existe alternativa oficial aprobada. La fuente sincronizada con el dispositivo utilizada en la base técnica no es automáticamente definitiva.

## 8.3 Escala tipográfica

- Hora: 82 unidades.
- Fecha: 28 unidades.
- Indicadores y etiquetas: pendientes.

## 8.4 Pesos

Pendientes. Deben evitar trazos finos y mantener la jerarquía.

## 8.5 Reglas de uso

No se mezclarán familias, deformarán caracteres ni usarán estilos decorativos. Los formatos numéricos deben ser inequívocos.

## Estado

**En desarrollo.** Tipografía definitiva y pesos pendientes.

---

# 9. Paleta de colores

## 9.1 Colores principales

- PipBlack `#000000`: fondo.
- PipGreen `#66FF66`: información principal y elementos activos.

## 9.2 Colores secundarios

- PipGreenDim `#3FAF5A`: información secundaria.

## 9.3 Colores funcionales

- PipAmber `#FFC857`: advertencias.
- PipRed `#FF4D4D`: errores y estados críticos.

## 9.4 Contraste

El color no será el único medio para comunicar un estado. Toda combinación se validará en pantalla real y Ambient Mode.

## 9.5 Reglas

Fondo negro puro, sin degradados. No se añadirán colores sin aprobación previa en el EDL. PipAmber y PipRed no son decorativos.

## Estado

**Completado.** Paleta heredada del EDL.

---

# 10. Design Tokens

## 10.1 Colores oficiales

Los tokens conceptuales son PipBlack, PipGreen, PipGreenDim, PipAmber y PipRed. Su nomenclatura técnica definitiva queda pendiente.

## 10.2 Tipografía

Familia prevista: Consolas. Alternativa pendiente. Hora 82; fecha 28.

## 10.3 Tamaños

| Componente | Ancho | Alto |
|---|---:|---:|
| Hora | 340 | 92 |
| Fecha | 320 | 38 |

## 10.4 Espaciados

Cuadrícula base: 8 unidades adaptables. Espaciados futuros pendientes.

## 10.5 Márgenes

Hora `x=55`; fecha `x=65`. Márgenes globales y de componentes futuros pendientes.

## 10.6 Radios

No existen radios aprobados. No se añadirán por defecto.

## 10.7 Opacidades

No existen valores oficiales aprobados. La opacidad del símbolo permanece pendiente.

## 10.8 Animaciones

No existen animaciones aprobadas. Solo se admitirán si son breves, discretas, funcionales y energéticamente justificadas.

## Estado

**En desarrollo.** Tokens parciales; valores futuros pendientes.

---

# 11. Sistema de componentes

## 11.1 Hora

- **Objetivo:** consulta temporal inmediata.
- **Función:** hora de 24 horas en formato `HH:MM`.
- **Tamaño:** 340 × 92; tipografía 82.
- **Posición:** `x=55`, `y=42`.
- **Estados:** activo y Ambient Mode.
- **Reglas:** elemento dominante, superior, legible y estable.
- **No permitido:** segundos, otro formato, desplazamiento o superposición sin aprobación.

## 11.2 Fecha

- **Objetivo:** contexto de calendario.
- **Función:** fecha `DD/MM/AAAA`.
- **Tamaño:** 320 × 38; tipografía 28.
- **Posición:** `x=65`, `y=138`.
- **Estados:** activo y Ambient Mode.
- **Reglas:** inmediatamente debajo de la hora y con jerarquía secundaria.
- **No permitido:** formato ambiguo, competencia con la hora o invasión del centro.

## 11.3 Símbolo institucional

- **Objetivo:** reforzar identidad.
- **Función:** elemento institucional subordinado y difuminado.
- **Tamaño y posición:** pendientes; región central.
- **Estados:** activo y Ambient Mode pendientes.
- **Reglas:** solo con recurso, licencia y tratamiento aprobados.
- **No permitido:** contenedor vacío, recurso provisional o interferencia con información.

## 11.4 Indicadores

### Batería

- **Objetivo:** permitir la consulta del nivel de batería del reloj.
- **Función:** mostrar la batería del reloj mediante un indicador gráfico y un valor numérico.
- **Tamaño:** Pendiente de aprobación.
- **Posición:** 3 h.
- **Estados:** Pendiente de aprobación.
- **Reglas de uso:** debe equilibrarse visualmente con pasos; los umbrales, la representación y el método de validación quedan pendientes de aprobación.
- **Casos no permitidos:** mostrar la batería del teléfono; inventar tamaños, estados, umbrales, fuentes de datos o métodos de validación no aprobados.

### Pasos

- **Objetivo:** permitir la consulta del recuento de pasos.
- **Función:** mostrar el recuento de pasos.
- **Tamaño:** Pendiente de aprobación.
- **Posición:** 9 h.
- **Estados:** Pendiente de aprobación.
- **Reglas de uso:** debe mostrar datos correctos y equilibrarse visualmente con batería; la fuente de datos, la representación y el método de validación quedan pendientes de aprobación.
- **Casos no permitidos:** incorporar rankings, puntuaciones o competición; inventar tamaños, estados, fuentes de datos, umbrales o métodos de validación no aprobados.

## 11.5 Accesos funcionales

### Spotify

- **Objetivo:** proporcionar acceso a Spotify cuando exista una solución aprobada.
- **Función:** Pendiente de aprobación.
- **Tamaño:** Pendiente de aprobación.
- **Posición:** 5 h.
- **Estados:** Pendiente de aprobación.
- **Reglas de uso:** debe respetar licencias y marca; el destino, el área táctil y el mecanismo técnico quedan pendientes de aprobación.
- **Casos no permitidos:** implementar un destino, área táctil o mecanismo técnico no aprobado; usar recursos sin licencia o aprobación.

### Google Wallet

- **Objetivo:** proporcionar acceso a Google Wallet cuando exista una solución aprobada.
- **Función:** Pendiente de aprobación.
- **Tamaño:** Pendiente de aprobación.
- **Posición:** 7 h.
- **Estados:** Pendiente de aprobación.
- **Reglas de uso:** debe respetar las restricciones de Wear OS y las reglas de interacción aprobadas; el destino, el área táctil y el mecanismo técnico quedan pendientes de aprobación.
- **Casos no permitidos:** implementar un destino, área táctil o mecanismo técnico no aprobado; asumir capacidades de Wear OS no validadas.

## 11.6 Componentes futuros

OPERATION, sincronización, batería del teléfono, Tiles, complicaciones configurables, biometría y otros componentes permanecen fuera del alcance activo.

## Estado

**En desarrollo.** Hora y fecha definidas; resto pendiente.

---

# 12. Distribución de la información

## 12.1 Prioridades

Hora, fecha, información funcional, indicadores, accesos, identidad y contenido futuro, en ese orden.

## 12.2 Zonas

Superior: hora y fecha. Derecha: batería. Izquierda: pasos. Inferior derecha: Spotify. Inferior izquierda: Wallet. Centro: símbolo futuro.

## 12.3 Densidad

Cada incorporación demostrará necesidad, frecuencia de uso, adecuación espacial, impacto energético y ausencia de solapamiento.

## 12.4 Relaciones

Hora y fecha forman un bloque; batería y pasos un par lateral; Spotify y Wallet un par inferior; el símbolo no es una acción.

## 12.5 Adaptación

La adaptación conservará prioridad y relaciones. No se moverán componentes sin revisión y aprobación.

## Estado

**En desarrollo.** Distribución general aprobada.

---

# 13. Ambient Mode

## 13.1 Objetivos

Mantener información temporal esencial, identidad y bajo consumo. Su prioridad en la planificación vigente es baja para esta fase.

## 13.2 Composición

Debe mantener continuidad con la esfera activa y simplificarla según las restricciones energéticas.

## 13.3 Elementos visibles

- Hora `HH:MM`.
- Fecha `DD/MM/AAAA`.

Batería, pasos, accesos y símbolo no están aprobados para este modo.

## 13.4 Tratamiento del color

Fondo PipBlack. Uso exacto de PipGreen, PipGreenDim, brillo u opacidad pendiente de pruebas.

## 13.5 Consumo objetivo

Debe usar fondo negro, evitar animaciones innecesarias y reducir elementos. No existe todavía una métrica cuantitativa aprobada.

## Estado

**En desarrollo.** Contenido definido; tratamiento y medición pendientes.

---

# 14. Interacción

## 14.1 Modelo

El usuario pulsa y recibe una respuesta predecible. No se utilizarán gestos ocultos.

## 14.2 Áreas táctiles

Spotify y Wallet tendrán áreas suficientes, no solapadas y compatibles con el borde circular. Dimensiones pendientes.

## 14.3 Feedback

La respuesta será inmediata. Feedback visual personalizado y háptica específica permanecen pendientes.

## 14.4 Estados

Los estados disponible, no disponible, pulsado y error se definirán solo cuando exista comportamiento verificable.

## 14.5 Personalización

Configuración avanzada, cambio automático de perfil y complicaciones configurables están excluidos actualmente.

## Estado

**En desarrollo.** Modelo definido; áreas y estados pendientes.

---

# 15. Rendimiento

## 15.1 Objetivos

Compilación estable, instalación correcta, renderizado fiable, consulta inmediata y compatibilidad con Xiaomi Watch 2.

## 15.2 Consumo

Se favorecerá AMOLED mediante PipBlack. Actualizaciones y elementos dinámicos se limitarán a su necesidad funcional.

## 15.3 Optimización

Se optimizarán imágenes, evitarán duplicados, limitarán animaciones y mantendrán componentes declarativos simples.

## 15.4 Restricciones

Son riesgos conocidos los cambios en plantillas de Android Studio, restricciones de Wear OS, limitaciones de Spotify o Wallet y cambios en APIs de pasos o batería. Ante una limitación se documentará el problema y se propondrán alternativas antes de alterar el diseño.

## Estado

**En desarrollo.** Objetivos cualitativos definidos; métricas pendientes.

---

# 16. Accesibilidad

## 16.1 Legibilidad

Se validarán claridad, separación, contraste, escala real, ausencia de recortes y ambos modos de la esfera.

## 16.2 Tamaños mínimos

Hora y fecha constituyen la referencia inicial. Los mínimos de componentes futuros requieren validación física.

## 16.3 Contraste

Los estados no dependerán solo del color. Los indicadores combinarán representación y valor cuando proceda.

## 16.4 Compatibilidad

La compatibilidad con tecnologías de accesibilidad se documentará únicamente cuando haya sido comprobada.

## 16.5 Validación

Incluye emulador, escala real, borde circular, formatos completos, Ambient Mode, áreas táctiles y Xiaomi Watch 2 cuando sea viable.

## Estado

**En desarrollo.** Criterios definidos; validación pendiente.

---

# 17. Recursos gráficos

## 17.1 Inventario

Cada recurso registrará nombre, función, procedencia, formato, dimensiones, versión, estado, licencia y ubicación. Incluye preview, símbolo, iconos de Spotify y Wallet, batería y pasos.

La Figura 4.1 de v1.1 sigue siendo referencia oficial, pero debe localizarse materialmente antes de aprobar v1.3.

## 17.2 Formatos

Deben ser compatibles con Watch Face Format. La elección raster/vector depende de compatibilidad, calidad, consumo y licencia.

## 17.3 Resoluciones

Se ajustarán al tamaño real; no se usarán recursos sobredimensionados sin justificación.

## 17.4 Organización

Se usarán nombres claros, sin duplicados ni temporales. Los recursos compartidos seguirán la biblioteca del EDL.

## 17.5 Versionado

Los recursos generados por IA documentarán herramienta, fecha, versión, estado y prompt cuando aporte trazabilidad.

## 17.6 Optimización

Se comprobarán calidad, transparencia, recorte, peso, contraste, compatibilidad, licencia e impacto energético.

## Estado

**En desarrollo.** Inventario y recursos definitivos pendientes.

---

# 18. Guía de implementación

Las reglas técnicas de este capítulo deben interpretarse conjuntamente con el **Contrato de interpretación para agentes de IA** del apartado 18.8.

## 18.1 Reglas para Codex

Leer la jerarquía documental; limitarse al Sprint; no inventar diseño; no alterar elementos fuera de alcance; detenerse ante limitaciones; compilar y verificar; no hacer commit sin autorización.

## 18.2 Reglas para ChatGPT

Diferenciar propuesta y aprobación, preservar terminología, no convertir limitaciones en decisiones, no ampliar alcance y mantener trazabilidad.

## 18.3 Reglas para futuras IA

Diferenciar propuesta y aprobación, preservar terminología, no convertir limitaciones en decisiones, no ampliar alcance y mantener trazabilidad.

## 18.4 Reglas específicas para Orca

Implementar únicamente lo descrito en WFPRD, consultar siempre el EDL, no modificar diseño sin aprobación, trabajar Sprint a Sprint y realizar commits pequeños y descriptivos.

## 18.5 Convenciones XML

Usar nombres descriptivos, separar componentes, evitar contenedores vacíos, usar grupos solo con contenido real, conservar coordenadas documentadas y no cambiar atributos fuera del alcance aprobado.

## 18.6 Recursos y herramientas

Solo se integrarán recursos aprobados. Android Studio forma parte expresa del proceso de implementación y validación. Git y el emulador Wear OS son herramientas del proceso. APK, cachés, informes, builds y archivos locales del IDE no se versionarán.

## 18.7 Validación

Revisión del requisito, inspección, compilación, instalación, emulador, dispositivo cuando sea viable, revisión visual, Git, aprobación y commit estable.

## 18.8 Contrato de interpretación para agentes de IA

Las siguientes reglas son normativas y obligatorias para Codex, Orca, ChatGPT y cualquier futura IA que participe en el desarrollo del proyecto:

1. Un requisito marcado como **Pendiente** nunca podrá implementarse sin aprobación expresa del propietario.
2. La ausencia de información en el WFPRD no autoriza a completar detalles por iniciativa propia.
3. Ninguna limitación técnica permite modificar automáticamente el diseño, la arquitectura o los requisitos.
4. Si dos requisitos parecen entrar en conflicto, el agente deberá detener la implementación y documentar el conflicto, sin resolverlo unilateralmente.
5. El Sprint limita el trabajo autorizado; el WFPRD define el producto. Ningún requisito fuera del Sprint podrá implementarse.
6. Toda propuesta realizada por una IA deberá identificarse claramente como propuesta y nunca pasará a formar parte del producto hasta ser aprobada por el propietario.
7. Ninguna IA podrá sustituir un recurso pendiente por uno alternativo sin aprobación expresa.
8. Ningún componente podrá cambiar de posición, tamaño, jerarquía o comportamiento salvo que el WFPRD lo autorice explícitamente.
9. Ante cualquier duda de interpretación prevalecerá siempre la jerarquía documental: **PROJECT_GUIDE → EDL → WFPRD → Sprint aprobado**.
10. Si un requisito no puede implementarse exactamente como está definido, el agente deberá documentar el problema y proponer alternativas, pero nunca modificar el requisito por iniciativa propia.

## Estado

**Completado.** Reglas normativas definidas.

---

# 19. Roadmap visual

## 19.1 Evolución

1. Fondo, colores, tipografía, hora y fecha.
2. Pasos y batería.
3. Spotify y Wallet.
4. Ambient Mode.
5. Optimización.

## 19.2 Hitos

Base funcional; composición superior; símbolo aprobado; indicadores; accesos; Ambient Mode; validación física.

## 19.3 Dependencias

Recursos definitivos, tipografía, APIs de batería y pasos, accesos externos, Watch Face Format y dispositivo objetivo.

## 19.4 Sprint asociado

ACTIVE_SPRINT determina el trabajo vigente. Este WFPRD no cambia el Sprint actual.

## 19.5 Prioridades

CIVILIAN tiene prioridad alta. Ambient Mode conserva prioridad baja para la fase indicada por v1.2. OPERATION queda para una fase posterior.

## 19.6 Riesgos

- Cambios en plantillas de Android Studio.
- Restricciones de Wear OS para accesos rápidos.
- Limitaciones de Spotify o Google Wallet.
- Cambios en APIs de pasos o batería.

Toda limitación se documentará y se presentarán alternativas antes de modificar diseño o requisitos.

## 19.7 Backlog

- Perfil OPERATION.
- Sincronización con PIP-SuriOS.
- Cambio automático de perfil.
- Batería del teléfono.
- Tiles.
- Complicaciones configurables.
- Biometría.
- Sonidos y vibración.

## Estado

**En desarrollo.** Secuencia conservada; hitos futuros pendientes.

---

# 20. Criterios de aceptación

## 20.1 Visuales

Fondo negro, colores EDL, jerarquía clara, tipografía correcta, márgenes circulares, ausencia de solapamientos y recursos aprobados.

## 20.2 Funcionales

Hora y fecha correctas; indicadores con datos correctos; Spotify y Wallet abren correctamente; Main Watch Face y Ambient Mode funcionan.

## 20.3 Rendimiento

Compilación sin errores bloqueantes, instalación correcta, renderizado estable, recursos optimizados y bajo consumo en Ambient Mode.

## 20.4 Accesibilidad

Legibilidad, contraste, áreas táctiles correctas, escala real y estados no comunicados solo por color.

## 20.5 Validación final

Cumplimiento de EDL, WFPRD y Sprint; emulador; Xiaomi Watch 2 para cierre CIVILIAN; ausencia de cambios ajenos; aprobación y commit estable.

## 20.6 Criterios de aceptación por Sprint

### Sprint 1

- Fondo negro.
- Colores EDL.
- Tipografía correcta.
- Hora visible.
- Fecha visible.
- Compila.
- Funciona en emulador.
- Commit.

### Sprint 2

- Batería.
- Pasos.
- Simetría visual.
- Datos correctos.
- Commit.

### Sprint 3

- Spotify funcional.
- Wallet funcional.
- Áreas táctiles correctas.
- Commit.

### Sprint 4

- Ambient Mode.
- Hora.
- Fecha.
- Bajo consumo.
- Commit.

### Sprint 5

- Prueba en Xiaomi Watch 2.
- Sin errores de compilación.
- Versión estable.

## 20.7 Definition of Done

La Watch Face CIVILIAN solo estará finalizada cuando hora y fecha funcionen; batería y pasos funcionen; Spotify y Wallet abran correctamente; Main Watch Face y Ambient Mode funcionen; respete el EDL; se pruebe en Xiaomi Watch 2; no existan errores bloqueantes; y exista un commit estable.

## Estado

**En desarrollo.** Criterios conservados; ejecución futura pendiente.

---

# 21. Historial de versiones

## 21.1 Versiones

| Versión | Estado | Relación |
|---|---|---|
| 1.1 | Aprobada | Contiene la referencia visual Figura 4.1. |
| 1.2 | Sustituida por v1.3 | Base íntegra de requisitos de v1.3. |
| 1.3 | Aprobada y vigente | Ampliación autocontenida y trazable. |

## 21.2 Cambios

v1.3 conserva la totalidad de v1.2, amplía control documental, objetivos, diseño, componentes, accesibilidad, recursos y reglas para IA, y registra decisiones pendientes sin resolverlas unilateralmente.

Se incorpora el **Contrato de interpretación para agentes de IA** como mecanismo para evitar interpretaciones automáticas del diseño y de los requisitos.

### Hitos históricos completados

- Creación inicial del proyecto independiente SuriOS Watch.
- Compilación correcta de la plantilla base antes de personalizarla.
- Primera implementación funcional con hora y fecha.
- Primera compilación satisfactoria de esa implementación.
- Primer commit estable: `10160d9`, “Sprint 001 - Watch face base funcional”.

### Sprint 1: requisitos originales

- Fondo negro.
- Colores EDL.
- Tipografía correcta.
- Hora visible.
- Fecha visible.
- Compilación correcta.
- Funcionamiento en emulador.
- Commit estable.

### Sprint 1: criterios ya cumplidos

- Fondo negro y colores EDL incorporados en la primera implementación funcional.
- Hora y fecha visibles y funcionales.
- Compilación satisfactoria.
- Primer commit estable registrado como `10160d9`, “Sprint 001 - Watch face base funcional”.

### Sprint 1: trabajo pendiente

- Tipografía definitiva: Pendiente de aprobación.
- Validación física en Xiaomi Watch 2: Pendiente de aprobación.
- Cualquier otra validación física exigida para el cierre: Pendiente de aprobación.

El Sprint 1 no se considera completado mientras no se cumplan todos sus criterios documentales.

## 21.3 Estado de la versión

Estado documental: aprobado y vigente. Estado de producto: desarrollo incremental en curso.

## 21.4 Responsables

| Responsabilidad | Responsable |
|---|---|
| Propiedad y aprobación | Diego Pérez de Camino |
| Diseño y documentación asistida | ChatGPT |
| Implementación asistida | Codex, Orca o agente autorizado |
| Validación final | Diego Pérez de Camino |

## Estado

**Aprobado y vigente.** El desarrollo incremental del producto permanece en curso.

---

# Anexo A. Matriz de trazabilidad de requisitos v1.2 → v1.3

## A.1 Convención de identificadores

- **REQ:** requisito general, funcional, documental o de alcance.
- **VIS:** requisito visual, compositivo o de identidad.
- **SPR:** planificación o requisito asociado a Sprint.
- **ACC:** criterio de aceptación o Definition of Done.

## A.2 Matriz

| ID | Tipo | Estado | Sección original v1.2 | Contenido o decisión que debe conservarse | Capítulo y apartado de destino v1.3 | Tipo de tratamiento | Riesgo de pérdida o ambigüedad | Observaciones |
|---|---|---|---|---|---|---|---|---|
| REQ-001 | Documental | Vigente | Encabezado y versión | SuriOS Watch es el proyecto cubierto por el documento maestro | 0.1; 1.1 | Ampliado | Bajo | Mantiene identidad documental. |
| REQ-002 | Documental | Vigente | Encabezado y versión | La versión de origen es 1.2 | 0.1; 21.1 | Conservado literalmente | Alto | v1.3 debe mostrar continuidad directa. |
| REQ-003 | Documental | Vigente | Encabezado y versión | El documento debe estar completo para implementación con Orca | 1.3; 18.4 | Ampliado | Bajo | Se amplía a Codex y futuras IA. |
| REQ-004 | Producto | Vigente | 1. Propósito | Desarrollar una Watch Face | 1.1; 2.1 | Conservado literalmente | Bajo | Objetivo base. |
| REQ-005 | Plataforma | Vigente | 1. Propósito | Dispositivo objetivo Xiaomi Watch 2 | 1.2; 3.5 | Conservado literalmente | Bajo | Objetivo inicial. |
| REQ-006 | Plataforma | Vigente | 1. Propósito | Plataforma Wear OS | 1.2; 3.5 | Conservado literalmente | Bajo | Restricción técnica. |
| REQ-007 | Ecosistema | Vigente | 1. Propósito | Integración en SuriOS | 1.2; 2.2; 5.1 | Ampliado | Bajo | No hereda funciones automáticamente. |
| REQ-008 | Ecosistema | Vigente | 1. Propósito | Alineación obligatoria con el EDL | 0.3; 4.4; 5.4 | Convertido en regla normativa | Medio | Jerarquía explícita. |
| REQ-009 | Alcance | Vigente | 2. Alcance | Incluir Main Watch Face | 3.1; 3.3 | Conservado literalmente | Bajo | Alcance activo. |
| REQ-010 | Alcance | Vigente | 2. Alcance | Incluir Ambient Mode | 3.1; 3.3; 13.1; 13.2; 13.3 | Conservado literalmente | Bajo | Alcance activo. |
| REQ-011 | Exclusión | Vigente | 2. Alcance | Excluir Tiles | 3.4; 19.7 | Reorganizado | Medio | Excluido actualmente y presente en backlog. |
| REQ-012 | Exclusión | Vigente | 2. Alcance | Excluir app Wear OS independiente | 3.4 | Conservado literalmente | Bajo | Sin destino futuro aprobado. |
| REQ-013 | Exclusión | Vigente | 2. Alcance | Excluir pantallas secundarias | 3.4 | Conservado literalmente | Bajo | Límite funcional. |
| REQ-014 | Exclusión | Vigente | 2. Alcance | Excluir configuración avanzada | 3.4; 14.5 | Ampliado | Medio | Alcance exacto se desarrolla sin autorizar funciones. |
| REQ-015 | Perfil | Vigente | 3. Perfiles | CIVILIAN tiene prioridad alta | 3.6; 19.5 | Conservado literalmente | Bajo | Prioridad explícita. |
| REQ-016 | Perfil | Vigente | 3. Perfiles | CIVILIAN está en desarrollo | 3.6; 21.3 | Ampliado | Medio | El historial conserva su estado en v1.2. |
| REQ-017 | Perfil | Pendiente | 3. Perfiles | OPERATION queda para una fase posterior | 3.6; 19.7 | Conservado literalmente | Bajo | No implementable todavía. |
| REQ-018 | Producto | Vigente | 4. CIVILIAN | Mostrar información esencial de un vistazo | 2.2; 7.1; 12.1 | Ampliado | Bajo | Principio verificable. |
| VIS-001 | Composición | Aprobado | 4. CIVILIAN | Hora situada a las 12 h | 6.3; 11.1; 12.2 | Conservado literalmente | Bajo | Posición conceptual vigente. |
| VIS-002 | Formato | Aprobado | 4. CIVILIAN | Hora en formato HH:MM | 11.1; 13.3 | Conservado literalmente | Bajo | Formato inequívoco. |
| VIS-003 | Composición | Aprobado | 4. CIVILIAN | Fecha situada a las 12 h junto a la hora | 6.3; 11.2; 12.2 | Conservado literalmente | Bajo | Debajo de la hora según composición aprobada. |
| VIS-004 | Formato | Aprobado | 4. CIVILIAN | Fecha en formato DD/MM/AAAA | 11.2; 13.3 | Conservado literalmente | Bajo | Formato inequívoco. |
| VIS-005 | Composición | Aprobado | 4. CIVILIAN | Batería situada a las 3 h | 6.3; 11.4; 12.2 | Conservado literalmente | Bajo | Detalles pendientes. |
| VIS-006 | Composición | Aprobado | 4. CIVILIAN | Spotify situado a las 5 h | 6.3; 11.5; 12.2 | Conservado literalmente | Medio | Mecanismo técnico pendiente. |
| VIS-007 | Composición | Aprobado | 4. CIVILIAN | Google Wallet situado a las 7 h | 6.3; 11.5; 12.2 | Conservado literalmente | Alto | Viabilidad técnica pendiente. |
| VIS-008 | Composición | Aprobado | 4. CIVILIAN | Pasos situados a las 9 h | 6.3; 11.4; 12.2 | Conservado literalmente | Bajo | Fuente de datos pendiente. |
| VIS-009 | Color | Aprobado | 4. CIVILIAN | Fondo negro | 5.2; 9.1; 10.1 | Convertido en regla normativa | Bajo | Se concreta como PipBlack. |
| VIS-010 | Color | Aprobado | 4. CIVILIAN | Verde terminal conforme al EDL | 5.2; 9; 10.1 | Ampliado | Bajo | Se diferencia PipGreen/PipGreenDim. |
| VIS-011 | Tipografía | Pendiente parcial | 4. CIVILIAN | Tipografía monoespaciada | 8; 10.2 | Ampliado | Medio | Consolas pendiente de validación técnica. |
| VIS-012 | Identidad | Pendiente parcial | 4. CIVILIAN | Símbolo de la Brotherhood of Steel | 5.3; 11.3 | Ampliado | Alto | Recurso y licencia pendientes. |
| VIS-013 | Identidad | Pendiente parcial | 4. CIVILIAN | El símbolo debe mostrarse difuminado | 5.3; 11.3 | Ampliado | Alto | Opacidad pendiente. |
| VIS-014 | Composición | Aprobado | 4. CIVILIAN | Simetría visual entre batería y pasos | 6.4; 11.4; 12.4 | Convertido en regla normativa | Medio | Se entiende como equilibrio visual. |
| VIS-015 | Referencia | Vigente | 4. CIVILIAN | Figura 4.1 de v1.1 continúa como referencia oficial | 5.3; 7.1; 17.1 | Conservado literalmente | Alto | Recurso debe localizarse. |
| REQ-019 | Prioridad | Vigente | 5. Ambient Mode | Ambient Mode tiene prioridad baja para esta fase | 13.1; 19.5 | Conservado literalmente | Medio | Sigue incluido. |
| VIS-016 | Ambient Mode | Aprobado | 5. Ambient Mode | Mostrar hora en Ambient Mode | 13.2; 13.3 | Conservado literalmente | Bajo | Elemento obligatorio. |
| VIS-017 | Ambient Mode | Aprobado | 5. Ambient Mode | Hora de Ambient Mode en formato HH:MM | 13.3 | Conservado literalmente | Bajo | Formato obligatorio. |
| VIS-018 | Ambient Mode | Aprobado | 5. Ambient Mode | Mostrar fecha en Ambient Mode | 13.2; 13.3 | Conservado literalmente | Bajo | Elemento obligatorio. |
| VIS-019 | Ambient Mode | Aprobado | 5. Ambient Mode | Fecha de Ambient Mode en formato DD/MM/AAAA | 13.3 | Conservado literalmente | Bajo | Formato obligatorio. |
| VIS-020 | Ambient Mode | Vigente | 5. Ambient Mode | Mantener identidad visual del EDL | 13.4 | Ampliado | Medio | Tratamiento exacto pendiente. |
| REQ-020 | Rendimiento | Vigente | 5. Ambient Mode | Priorizar ahorro energético | 13.5; 15.2 | Convertido en regla normativa | Alto | Falta métrica cuantitativa. |
| REQ-021 | Integración | Vigente | 6. Integración | Compartir EDL con PIP-SuriOS | 0.4; 5.1 | Convertido en regla normativa | Bajo | Integración explícita. |
| REQ-022 | Integración | Vigente | 6. Integración | Compartir EDL con el resto de proyectos SuriOS | 0.4; 5.1 | Convertido en regla normativa | Bajo | Regla común del ecosistema. |
| REQ-023 | Herramienta | Vigente | 7. Guía | Utilizar Android Studio | 18.6 | Reorganizado | Bajo | Contexto operativo, no tutorial. |
| REQ-024 | Herramienta | Vigente | 7. Guía | Utilizar Git | 18.6; 20.5 | Convertido en regla normativa | Bajo | Conforme a PROJECT_GUIDE. |
| REQ-025 | Herramienta | Vigente | 7. Guía | Utilizar Orca | 1.3; 18.4 | Ampliado | Bajo | Se mantienen reglas específicas. |
| REQ-026 | Validación | Vigente | 7. Guía | Utilizar emulador Wear OS | 16.5; 18.6; 18.7; 20.5 | Convertido en regla normativa | Bajo | Validación mínima. |
| REQ-027 | Histórico | Completado | 7. Guía | Crear proyecto Wear OS Watch Face | 21.2 | Reorganizado | Alto | Hito cumplido, no tarea futura. |
| REQ-028 | Histórico | Completado | 7. Guía | Compilar proyecto base sin modificaciones | 21.2 | Reorganizado | Alto | Hito cumplido. |
| REQ-029 | Histórico | Completado | 7. Guía | Realizar primer commit estable: 10160d9, “Sprint 001 - Watch face base funcional” | 21.2 | Reorganizado | Alto | Hito cumplido. |
| SPR-001 | Plan de Sprint | Vigente | 7. Plan | Sprint 1: fondo | 19.1; 20.6 | Conservado literalmente | Bajo | Primera fase. |
| SPR-002 | Plan de Sprint | Vigente | 7. Plan | Sprint 1: colores | 19.1; 20.6 | Conservado literalmente | Bajo | Primera fase. |
| SPR-003 | Plan de Sprint | Vigente | 7. Plan | Sprint 1: tipografía | 19.1; 20.6 | Conservado literalmente | Medio | Fuente definitiva pendiente. |
| SPR-004 | Plan de Sprint | Vigente | 7. Plan | Sprint 1: hora | 19.1; 20.6 | Conservado literalmente | Bajo | Primera fase. |
| SPR-005 | Plan de Sprint | Vigente | 7. Plan | Sprint 1: fecha | 19.1; 20.6 | Conservado literalmente | Bajo | Primera fase. |
| SPR-006 | Plan de Sprint | Pendiente | 7. Plan | Sprint 2: pasos | 19.1; 20.6 | Conservado literalmente | Bajo | Segunda fase. |
| SPR-007 | Plan de Sprint | Pendiente | 7. Plan | Sprint 2: batería | 19.1; 20.6 | Conservado literalmente | Bajo | Segunda fase. |
| SPR-008 | Plan de Sprint | Pendiente | 7. Plan | Sprint 3: Spotify | 19.1; 20.6 | Conservado literalmente | Medio | Técnica pendiente. |
| SPR-009 | Plan de Sprint | Pendiente | 7. Plan | Sprint 3: Google Wallet | 19.1; 20.6 | Conservado literalmente | Alto | Técnica pendiente. |
| SPR-010 | Plan de Sprint | Pendiente | 7. Plan | Sprint 4: Ambient Mode | 19.1; 20.6 | Conservado literalmente | Bajo | Cuarta fase. |
| SPR-011 | Plan de Sprint | Pendiente | 7. Plan | Sprint 5: optimización | 19.1; 20.6 | Conservado literalmente | Bajo | Quinta fase. |
| ACC-001 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Fondo negro | 20.1; 20.6 | Conservado literalmente | Bajo | Criterio individual. |
| ACC-002 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Colores EDL | 20.1; 20.6 | Ampliado | Bajo | Vinculado a tokens oficiales. |
| ACC-003 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Tipografía correcta | 20.1; 20.6 | Ampliado | Medio | Decisión final pendiente. |
| ACC-004 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Hora visible | 20.1; 20.2; 20.6 | Conservado literalmente | Bajo | Criterio individual. |
| ACC-005 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Fecha visible | 20.1; 20.2; 20.6 | Conservado literalmente | Bajo | Criterio individual. |
| ACC-006 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Compila | 20.3; 20.6 | Convertido en regla normativa | Bajo | Condición mínima. |
| ACC-007 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Funciona en emulador | 20.5; 20.6 | Convertido en regla normativa | Bajo | No sustituye dispositivo real. |
| ACC-008 | Aceptación Sprint 1 | Vigente | 8. Sprint 1 | Existe commit | 20.5; 20.6 | Convertido en regla normativa | Bajo | Conforme a PROJECT_GUIDE. |
| ACC-009 | Aceptación Sprint 2 | Pendiente | 8. Sprint 2 | Batería implementada | 20.2; 20.6 | Ampliado | Bajo | Requiere datos correctos. |
| ACC-010 | Aceptación Sprint 2 | Pendiente | 8. Sprint 2 | Pasos implementados | 20.2; 20.6 | Ampliado | Bajo | Requiere datos correctos. |
| ACC-011 | Aceptación Sprint 2 | Pendiente | 8. Sprint 2 | Simetría visual | 20.1; 20.6 | Ampliado | Medio | Equilibrio batería/pasos. |
| ACC-012 | Aceptación Sprint 2 | Pendiente | 8. Sprint 2 | Datos correctos | 20.2; 20.6 | Convertido en regla normativa | Medio | Método de comprobación pendiente. |
| ACC-013 | Aceptación Sprint 2 | Pendiente | 8. Sprint 2 | Existe commit | 20.5; 20.6 | Convertido en regla normativa | Bajo | Criterio individual. |
| ACC-014 | Aceptación Sprint 3 | Pendiente | 8. Sprint 3 | Spotify funcional | 20.2; 20.6 | Conservado literalmente | Medio | Depende de plataforma. |
| ACC-015 | Aceptación Sprint 3 | Pendiente | 8. Sprint 3 | Wallet funcional | 20.2; 20.6 | Conservado literalmente | Alto | Depende de plataforma. |
| ACC-016 | Aceptación Sprint 3 | Pendiente | 8. Sprint 3 | Áreas táctiles correctas | 14.2; 20.4; 20.6 | Ampliado | Medio | Dimensiones pendientes. |
| ACC-017 | Aceptación Sprint 3 | Pendiente | 8. Sprint 3 | Existe commit | 20.5; 20.6 | Convertido en regla normativa | Bajo | Criterio individual. |
| ACC-018 | Aceptación Sprint 4 | Pendiente | 8. Sprint 4 | Ambient Mode implementado | 13; 20.2; 20.6 | Ampliado | Bajo | Estado real de la esfera. |
| ACC-019 | Aceptación Sprint 4 | Pendiente | 8. Sprint 4 | Hora visible | 13.3; 20.6 | Conservado literalmente | Bajo | Criterio individual. |
| ACC-020 | Aceptación Sprint 4 | Pendiente | 8. Sprint 4 | Fecha visible | 13.3; 20.6 | Conservado literalmente | Bajo | Criterio individual. |
| ACC-021 | Aceptación Sprint 4 | Pendiente | 8. Sprint 4 | Bajo consumo | 13.5; 15.2; 20.3 | Ampliado | Alto | Sin métrica cuantitativa. |
| ACC-022 | Aceptación Sprint 4 | Pendiente | 8. Sprint 4 | Existe commit | 20.5; 20.6 | Convertido en regla normativa | Bajo | Criterio individual. |
| ACC-023 | Aceptación Sprint 5 | Pendiente | 8. Sprint 5 | Prueba en Xiaomi Watch 2 | 16.5; 20.5; 20.6 | Convertido en regla normativa | Bajo | Cierre físico. |
| ACC-024 | Aceptación Sprint 5 | Pendiente | 8. Sprint 5 | Sin errores de compilación | 20.3; 20.6 | Convertido en regla normativa | Bajo | No cubre errores visuales. |
| ACC-025 | Aceptación Sprint 5 | Pendiente | 8. Sprint 5 | Versión estable | 20.5; 20.6 | Ampliado | Medio | Vinculada a DoD. |
| ACC-026 | Definition of Done | Pendiente | 9. DoD | Hora y fecha funcionan | 20.2; 20.7 | Conservado literalmente | Bajo | Condición acumulativa. |
| ACC-027 | Definition of Done | Pendiente | 9. DoD | Batería y pasos funcionan | 20.2; 20.7 | Conservado literalmente | Bajo | Condición acumulativa. |
| ACC-028 | Definition of Done | Pendiente | 9. DoD | Spotify abre correctamente | 20.2; 20.7 | Conservado literalmente | Medio | Viabilidad técnica pendiente. |
| ACC-029 | Definition of Done | Pendiente | 9. DoD | Google Wallet abre correctamente | 20.2; 20.7 | Conservado literalmente | Alto | Viabilidad técnica pendiente. |
| ACC-030 | Definition of Done | Pendiente | 9. DoD | Main Watch Face funciona | 20.2; 20.7 | Conservado literalmente | Bajo | Condición acumulativa. |
| ACC-031 | Definition of Done | Pendiente | 9. DoD | Ambient Mode funciona | 20.2; 20.7 | Conservado literalmente | Bajo | Condición acumulativa. |
| ACC-032 | Definition of Done | Pendiente | 9. DoD | Respeta el EDL | 20.1; 20.7 | Convertido en regla normativa | Bajo | Revisión obligatoria. |
| ACC-033 | Definition of Done | Pendiente | 9. DoD | Probada en Xiaomi Watch 2 | 20.5; 20.7 | Conservado literalmente | Bajo | Condición acumulativa. |
| ACC-034 | Definition of Done | Pendiente | 9. DoD | No existen errores bloqueantes | 20.5; 20.7 | Convertido en regla normativa | Bajo | Pendientes menores se documentan. |
| ACC-035 | Definition of Done | Pendiente | 9. DoD | Existe commit estable | 20.5; 20.7 | Convertido en regla normativa | Bajo | Condición acumulativa. |
| REQ-030 | Riesgo | Vigente | 10. Riesgos | Cambios en plantillas de Android Studio | 15.4; 19.6 | Reorganizado | Bajo | Riesgo técnico. |
| REQ-031 | Riesgo | Vigente | 10. Riesgos | Restricciones Wear OS para accesos rápidos | 14; 15.4; 19.6 | Ampliado | Alto | Afecta accesos. |
| REQ-032 | Riesgo | Vigente | 10. Riesgos | Limitaciones de Spotify | 11.5; 19.6 | Ampliado | Alto | No prometer mecanismo. |
| REQ-033 | Riesgo | Vigente | 10. Riesgos | Limitaciones de Google Wallet | 11.5; 19.6 | Ampliado | Alto | No prometer mecanismo. |
| REQ-034 | Riesgo | Vigente | 10. Riesgos | Cambios en API de pasos | 11.4; 15.4; 19.6 | Reorganizado | Medio | Dependencia externa. |
| REQ-035 | Riesgo | Vigente | 10. Riesgos | Cambios en API de batería | 11.4; 15.4; 19.6 | Reorganizado | Medio | Dependencia externa. |
| REQ-036 | Proceso | Vigente | 10. Riesgos | Documentar toda limitación técnica | 18.1; 18.2; 18.3; 19.6 | Convertido en regla normativa | Bajo | Obligatorio. |
| REQ-037 | Proceso | Vigente | 10. Riesgos | Proponer alternativas antes de modificar diseño | 18.1; 18.2; 18.3; 19.6 | Convertido en regla normativa | Bajo | Requiere aprobación. |
| REQ-038 | Backlog | Pendiente | 11. Backlog | Perfil OPERATION | 19.7 | Conservado literalmente | Bajo | Fuera de alcance. |
| REQ-039 | Backlog | Pendiente | 11. Backlog | Sincronización con PIP-SuriOS | 19.7 | Conservado literalmente | Bajo | Sin definición. |
| REQ-040 | Backlog | Pendiente | 11. Backlog | Cambio automático de perfil | 19.7 | Conservado literalmente | Bajo | Sin definición. |
| REQ-041 | Backlog | Pendiente | 11. Backlog | Batería del teléfono | 19.7 | Conservado literalmente | Bajo | Diferente del reloj. |
| REQ-042 | Backlog | Pendiente | 11. Backlog | Tiles | 19.7 | Conservado literalmente | Medio | También excluido. |
| REQ-043 | Backlog | Pendiente | 11. Backlog | Complicaciones configurables | 19.7 | Conservado literalmente | Bajo | Fuera de alcance. |
| REQ-044 | Backlog | Pendiente | 11. Backlog | Biometría | 19.7 | Conservado literalmente | Bajo | Sin funcionalidades inventadas. |
| REQ-045 | Backlog | Pendiente | 11. Backlog | Sonidos | 19.7 | Conservado literalmente | Medio | EDL no define sonidos oficiales. |
| REQ-046 | Backlog | Pendiente | 11. Backlog | Vibración | 14.3; 19.7 | Ampliado | Medio | EDL prioriza háptica. |
| REQ-047 | IA | Vigente | 12. Prompt | Orca implementa únicamente lo descrito en WFPRD | 18.4 | Convertido en regla normativa | Bajo | Obligatorio. |
| REQ-048 | IA | Vigente | 12. Prompt | Orca consulta siempre el EDL | 18.4 | Convertido en regla normativa | Bajo | Se inserta en jerarquía completa. |
| REQ-049 | IA | Vigente | 12. Prompt | Orca no modifica diseño sin aprobación | 18.4 | Convertido en regla normativa | Bajo | Obligatorio. |
| REQ-050 | IA | Vigente | 12. Prompt | Orca trabaja Sprint a Sprint | 18.4; 19.4 | Convertido en regla normativa | Bajo | Evita ampliar alcance. |
| REQ-051 | Git | Vigente | 12. Prompt | Realizar commits pequeños | 18.4; 20.5 | Convertido en regla normativa | Bajo | Una unidad estable. |
| REQ-052 | Git | Vigente | 12. Prompt | Utilizar mensajes de commit descriptivos | 18.4; 20.5 | Convertido en regla normativa | Bajo | Conforme a PROJECT_GUIDE. |
| REQ-053 | Versionado | Vigente | Historial y versión | v1.2 es origen directo de v1.3 | 0.1; 21.1 | Conservado literalmente | Alto | Garantiza continuidad. |
| REQ-054 | Versionado | Vigente | Historial y versión | v1.3 debe ser completa | 0.1; 21.2 | Convertido en regla normativa | Bajo | No es suplemento. |
| REQ-055 | Versionado | Vigente | Historial y versión | v1.3 debe ser autocontenida | 0.1; 21.2 | Convertido en regla normativa | Bajo | No requiere versiones previas. |
| REQ-056 | Versionado | Vigente | Historial y versión | Conservar todas las decisiones aprobadas de v1.2 | 0.5; 21.2; Anexo A | Convertido en regla normativa | Alto | La reorganización no elimina requisitos. |

## A.3 Decisiones pendientes de resolución

1. Localización y forma de incorporación de la Figura 4.1 de v1.1.
2. Clasificación técnica definitiva de Spotify y Google Wallet.
3. Validación de Consolas y elección de fuente alternativa si fuera necesaria.
4. Métrica cuantitativa de bajo consumo.
5. Recurso, licencia, tamaño, posición y opacidad del símbolo institucional.

## Estado

**Completado.** Todos los requisitos de v1.2 tienen identificador y destino en v1.3; las ambigüedades permanecen señaladas.
