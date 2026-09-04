# SuriOS - Ecosystem Design Language (EDL)

Versión 0.6  
Manual de diseño, arquitectura y desarrollo del ecosistema SuriOS  
Documento completo  
Estado: Lenguaje de diseño del ecosistema SuriOS

## 1\. Propósito

El EDL define las normas visuales, funcionales y de interacción comunes a todos los proyectos del ecosistema SuriOS. Ningún proyecto duplicará estas reglas; todos los proyectos las referenciarán desde su documentación específica.

## 2\. Proyectos del ecosistema

• PIP-SuriOS (MRPD)  
• SuriOS Watch (WFRPD)  
• PIP-SuriOS Launcher (futuro)  
• Cualquier proyecto futuro del ecosistema SuriOS.

## 3\. Filosofía del ecosistema

• Utilidad antes que decoración.  
• Inspiración RobCo y Brotherhood of Steel, no copia literal.  
• Coherencia entre dispositivos.  
• Diseño modular y reutilizable.  
• Toda interfaz debe tener un propósito claro.  
• Ningún elemento ocupa espacio sin aportar información o utilidad.

## 4\. Identidad visual

### 4.1 Paleta oficial

PipBlack #000000  
PipGreen #66FF66  
PipGreenDim #3FAF5A  
PipAmber #FFC857  
PipRed #FF4D4D  
<br/>Todo nuevo color deberá aprobarse e incorporarse previamente al EDL.

### 4.2 Tipografía

Fuente oficial: Consolas.  
Monoespaciada, excelente legibilidad, estética terminal y amplia compatibilidad.

### 4.3 Jerarquía tipográfica

Nivel 1: Títulos.  
Nivel 2: Información principal.  
Nivel 3: Información secundaria.  
Nivel 4: Información contextual.

### 4.4 Reglas de contraste

Información principal → PipGreen.  
Información secundaria → PipGreenDim.  
Advertencias → PipAmber.  
Estados críticos → PipRed.  
Fondo → PipBlack.  
La jerarquía visual se consigue mediante contraste, tamaño y posición.

### 4.5 Compatibilidad AMOLED

El fondo oficial será siempre negro puro (#000000), optimizando el consumo en pantallas AMOLED.

### 4.6 Fichas técnicas de color

#### PipBlack

HEX: #000000  
Uso: Fondo principal  
No utilizar para: Fondos degradados

#### PipGreen

HEX: #66FF66  
Uso: Texto principal y elementos activos  
No utilizar para: Advertencias

#### PipGreenDim

HEX: #3FAF5A  
Uso: Texto secundario  
No utilizar para: Información crítica

#### PipAmber

HEX: #FFC857  
Uso: Advertencias  
No utilizar para: Uso general

#### PipRed

HEX: #FF4D4D  
Uso: Errores y estados críticos  
No utilizar para: Información normal

### 4.7 Colores auxiliares aprobados

Los siguientes colores forman parte de la implementación técnica del ecosistema y no sustituyen la jerarquía principal de PipGreen, PipGreenDim, PipAmber y PipRed.

#### PipGreenBright

HEX: #66FF99
Uso: Realces puntuales y elementos destacados
No utilizar para: Advertencias o errores

#### PipBlue

HEX: #33AAFF
Uso: Identificación visual del nodo remoto P.R.S.
No utilizar para: Representar distancia, dirección o una clasificación de proximidad

#### PipGray

HEX: #5A5A5A
Uso: Elementos desactivados o no disponibles
No utilizar para: Información activa o estados críticos

## 5\. Componentes de interfaz

### 5.1 Biblioteca oficial

Todo componente reutilizable deberá definirse primero en el EDL antes de utilizarse en cualquier proyecto.

### 5.2 Botón estándar

Rectangular, esquinas rectas, borde fino verde, fondo negro, inversión de colores al pulsar. Tamaños oficiales S, M y L.

### 5.3 Cabecera estándar

Todas las pantallas utilizarán el encabezado institucional BROTHERHOOD OF STEEL seguido del nombre del módulo.

### 5.4 Iconografía

Monocroma, outline, sin sombras, sin degradados y con grosor uniforme. No mezclar familias.

### 5.5 Indicadores

Siempre combinarán indicador gráfico y valor numérico. Se permiten barras, agujas, indicadores circulares, sectores u otros justificados.

### 5.6 Paneles

Fondo negro, líneas verdes, sin rellenos ni sombras.

### 5.7 Listas

La navegación utilizará el cursor ► para indicar el elemento seleccionado.

### 5.8 Cursor y selección

► para navegación; inversión de colores para elemento activo o confirmado.

### 5.9 Espaciado

Cuadrícula base de 8 px, adaptada proporcionalmente en Wear OS.

### 5.10 Patrones de interacción

Acciones importantes con confirmación, respuesta inmediata, animaciones funcionales y sensación de terminal técnico.

## 6\. Arquitectura de la interfaz

### 6.1 Estructura estándar

Cabecera institucional → Nombre del módulo → Contenido principal → Zona de acciones.  
Todas las pantallas deberán seguir esta estructura salvo justificación documentada.

### 6.2 Jerarquía visual

1\. Cabecera institucional.  
2\. Nombre del módulo.  
3\. Información principal.  
4\. Información secundaria.  
5\. Acciones.

### 6.3 Navegación

• Máximo tres niveles de profundidad recomendados.  
• Siempre debe existir una forma clara de volver.  
• El usuario nunca debe perder el contexto.

### 6.4 Distribución

Información prioritaria en la parte superior, acciones al final y, cuando sea posible, indicadores en posiciones laterales consistentes.

### 6.5 Consistencia

Una pantalla nueva debe ser reconocible como parte del ecosistema SuriOS aunque el usuario nunca la haya visto.

### 6.6 Densidad de información

Se prioriza mostrar mucha información sin saturar la interfaz.  
El desplazamiento (scroll) deberá evitarse cuando sea posible, pero está permitido cuando la naturaleza de la información lo requiera (por ejemplo, historiales como MISSION LOG).

### 6.7 Filosofía de navegación

El usuario siempre pulsa; nunca debe adivinar.  
No habrá gestos ocultos ni navegación confusa.

## 7\. Guía para desarrolladores e IA

### 7.1 Principio de implementación

Todo desarrollo dentro del ecosistema SuriOS deberá seguir este orden obligatorio:  
<br/>EDL → PRD específico → Sprint → Implementación → Pruebas → Commit.  
<br/>El diseño y las decisiones comunes se consultan primero en el EDL. Los requisitos funcionales y técnicos se consultan después en el documento específico del proyecto (MRPD, WFRPD u otro PRD). La implementación se divide en sprints pequeños y verificables. No se debe invertir este orden ni comenzar a programar una funcionalidad que todavía no esté documentada.

### 7.2 Fuente de verdad y jerarquía documental

La jerarquía oficial de referencia es:  
<br/>1\. EDL: reglas comunes de identidad, interacción, arquitectura visual y método de trabajo.  
2\. PRD específico: requisitos, alcance, prioridades y decisiones particulares de cada proyecto.  
3\. Código: materialización técnica de lo aprobado en los documentos anteriores.  
<br/>Si existe una discrepancia, el código deberá adaptarse al PRD y el PRD deberá respetar el EDL. Una diferencia técnica no autoriza a modificar el diseño o los requisitos de forma automática. Toda excepción deberá documentarse y aprobarse antes de aplicarse.

### 7.3 Ciclo oficial de trabajo

Cada nueva funcionalidad seguirá el ciclo siguiente:  
<br/>1\. Idea.  
2\. Discusión y análisis de utilidad, encaje y complejidad.  
3\. Asignación de prioridad cuando corresponda.  
4\. Aprobación.  
5\. Actualización del EDL si la idea modifica una regla común del ecosistema.  
6\. Actualización del PRD específico.  
7\. Definición del sprint.  
8\. Implementación.  
9\. Pruebas en emulador y, cuando sea posible, en dispositivo real.  
10\. Commit de Git.  
11\. Actualización documental si durante la implementación se aprobó algún cambio.  
<br/>Ninguna fase posterior deberá utilizarse para evitar una fase anterior. Por ejemplo, una limitación técnica debe documentarse y revisarse antes de alterar la especificación.

### 7.4 Normas para asistentes de IA

Orca, Codex y cualquier asistente de IA empleado en proyectos SuriOS deberán respetar estas normas:  
<br/>• Leer el EDL vigente antes del PRD específico.  
• No añadir funcionalidades que no estén documentadas y aprobadas.  
• No modificar el diseño, la navegación, la terminología ni las prioridades sin autorización.  
• Trabajar únicamente sobre el sprint solicitado.  
• Explicar de forma sencilla qué cambiará antes de aplicar código adicional cuando el propietario esté siguiendo el proceso paso a paso.  
• Ante una limitación técnica, detenerse, documentarla y proponer alternativas; no escoger una alternativa por iniciativa propia.  
• No eliminar código funcional sin una justificación clara y una aprobación previa.  
• Mantener el código comentado y comprensible para un propietario con conocimientos iniciales de programación.  
• Evitar refactorizaciones amplias que no sean necesarias para completar el sprint actual.  
• Mantener coherencia con el EDL y con la última versión completa del PRD correspondiente.

### 7.5 Gestión documental y versionado

Todos los documentos del ecosistema SuriOS seguirán estas reglas:  
<br/>• Cada nueva versión será completa y autocontenida.  
• Una versión nueva partirá íntegramente de la anterior y añadirá, corregirá o reorganizará únicamente lo aprobado.  
• Nunca se entregará una versión que contenga solo un resumen de cambios.  
• Nunca se sobrescribirá una versión anterior.  
• Las versiones anteriores podrán conservarse como copias de seguridad e histórico.  
• La última versión deberá poder entregarse directamente a Orca sin necesidad de consultar documentos anteriores.  
• Los cambios relevantes se reflejarán en un historial de versiones o registro de cambios.  
• El estado documental y el estado de implementación podrán avanzar de forma independiente y deberán indicarse por separado cuando corresponda.

### 7.6 Gestión de Git

La gestión de Git seguirá estas normas comunes:  
<br/>• Crear un commit cuando una funcionalidad o sprint alcance un estado estable y verificable.  
• Utilizar mensajes breves y descriptivos.  
• No mezclar funcionalidades diferentes en un mismo commit salvo que formen una única unidad inseparable.  
• Comprobar que el proyecto compila y funciona antes del commit.  
• Mantener alineados el número de versión visible, el título del commit y el historial documental cuando se trate de un hito de versión.  
• Utilizar Git como punto de restauración local; el uso futuro de GitHub añadirá una copia remota mediante push, sin sustituir los commits locales.  
• No realizar cambios destructivos en el historial sin comprender y aprobar previamente sus consecuencias.

### 7.7 Definición oficial de sprint terminado

Un sprint solo se considerará terminado cuando cumpla todos los criterios aplicables:  
<br/>• La funcionalidad definida para el sprint está implementada.  
• El proyecto compila sin errores bloqueantes.  
• La funcionalidad se ha probado en el emulador o entorno disponible.  
• Cuando sea viable, se ha probado también en el dispositivo real.  
• No se han añadido funciones no solicitadas.  
• Se ha creado un commit estable y descriptivo.  
• La documentación se ha actualizado si durante el desarrollo cambió alguna decisión aprobada.  
• Los problemas pendientes están claramente documentados y no se ocultan bajo la consideración de "completado".

### 7.8 Validación de gestos y distribuciones tester

Las funcionalidades que dependan de gestos sobre la pantalla, como pellizcos,
arrastres multitáctiles u otras interacciones de varios dedos, solo se
considerarán verificadas mediante una prueba física en el dispositivo objetivo.
El emulador o ADB pueden servir para comprobar que la aplicación arranca, pero
no sustituyen esa verificación del gesto.

Las APK de FENRIR, ALTAMIRA y CHECHU son versiones fijas de tester. No se
actualizarán por cambios de la aplicación principal salvo orden expresa. Al
generar una nueva versión tester, se eliminarán los artefactos de distribución
de la anterior.

## 8\. Animaciones

Solo si aportan información. Cortas, discretas y nunca decorativas.

## 9\. Lenguaje del ecosistema

Terminología oficial: BROTHERHOOD OF STEEL, OPERATION, INITIALIZING, LOADING PROFILE.
La denominación CIVILIAN queda retirada y no identifica ningún perfil activo.

## 10\. Recursos compartidos

Biblioteca común del ecosistema:  
/assets  
/logos  
/icons  
/backgrounds  
/fonts  
/sounds  
/animations  
/wireframes  
/mockups  
<br/>Se documentarán convenciones de nombres, formatos, versiones, recursos generados por IA, recursos externos y criterios de licencia cuando proceda.

## 11\. Principios de evolución

• Nunca romper la compatibilidad visual sin una razón justificada.  
• Los cambios del EDL deben beneficiar al conjunto del ecosistema.  
• Las excepciones deberán documentarse.  
• Todo componente nuevo se incorporará primero al EDL y después a los PRD específicos.

## 12\. Roadmap

v0.x Definición de principios.  
v1.0 Guía consolidada.  
v2.0 Biblioteca completa de componentes reutilizables.

# 13\. Biblioteca de recursos

## 13.1 Estructura del repositorio

/assets  
/logos  
/icons  
/backgrounds  
/fonts  
/animations  
/wireframes  
/mockups  
/references

## 13.2 Convención de nombres

Todos los recursos seguirán una nomenclatura consistente (ej.: ICON_SPOTIFY_v1.svg, WF_SURIOS_WATCH_REFERENCE_v1.png). Se evitarán nombres ambiguos.

## 13.3 Recursos creados por IA

Cada recurso generado por IA documentará la herramienta utilizada, fecha, versión, estado (Conceptual, Aprobado, Implementado o Reemplazado) y, cuando aporte valor, el prompt empleado.

## 13.4 Recursos externos

Todo recurso externo identificará su procedencia y uso previsto para mantener la trazabilidad y facilitar su futura sustitución.

## 13.5 Recursos oficiales del ecosistema

Listado vivo de activos oficiales: logotipo SuriOS (cuando exista), paleta, tipografía, iconografía, fondos institucionales, wireframes aprobados y demás recursos comunes.

## 13.6 Wireframes oficiales

Los wireframes aprobados pasarán a formar parte de la biblioteca oficial del ecosistema. La Figura 1 vigente del WFPRD constituye la referencia visual oficial.

## 13.7 Recursos pendientes

Pendientes: logotipo oficial de SuriOS, iconografía propia, biblioteca de indicadores, fondo institucional y animaciones oficiales.

# 14\. Interacción háptica

No se define una biblioteca común de sonidos por el momento. RADS usa recursos
específicos de la herramienta (`assets/sounds/1.mp3`, `2.mp3` y `3.mp3`) con
selección por capas y solape en los niveles de transición. Como comportamiento
común del ecosistema se priorizará la respuesta háptica (vibración) para
confirmar acciones importantes cuando el dispositivo lo permita.

# Anexo A - Glosario del ecosistema

• EDL: Manual de diseño, arquitectura y desarrollo del ecosistema SuriOS.

• MRPD: Documento maestro de requisitos de PIP-SuriOS.

• WFRPD: Documento maestro de requisitos de SuriOS Watch.

• Sprint: Unidad mínima de desarrollo aceptada.

• Componente: Elemento reutilizable definido en el EDL.

• Activo: Recurso gráfico, documental o háptico reutilizable.
