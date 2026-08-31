# SuriOS Watch — WFPRD v1.5

---

document: WFPRD
project: SuriOS Watch
version: 1.5
status: Aprobado y vigente
implementation_status: Aprobado
replaces: WFPRD v1.4
owner: Diego Pérez de Camino
date: 2026-08-06

---

# 0. Control documental

## 0.1 Propósito de la versión

WFPRD v1.5 constituye la especificación maestra vigente de SuriOS Watch.

Sustituye a WFPRD v1.4 e incorpora:

- cierre documental de Sprint 003;
- cierre documental de Sprint 004;
- reorganización oficial del roadmap;
- incorporación del emblema institucional como componente oficial del producto;
- integración de BROTHERHOOD_EMBLEM_ASSET_SPEC;
- integración de WFPRD_BROTHERHOOD_EMBLEM;
- consolidación de WATCHFACE_LAYOUT v1.3 como referencia geométrica oficial.

Las versiones anteriores permanecen exclusivamente como documentación histórica y no deberán utilizarse como referencia normativa.

---

## 0.2 Jerarquía documental

La interpretación obligatoria del proyecto será:

1. PROJECT_GUIDE v1.1
2. EDL v0.6
3. WFPRD v1.5
4. ADR
5. WFPRD especializados
6. WATCHFACE_LAYOUT v1.3
7. Sprint activo autorizado
8. Implementación
9. Validación física

WATCHFACE_LAYOUT define exclusivamente:

- geometría;
- composición;
- jerarquía visual.

Nunca podrá modificar:

- funcionalidades;
- comportamiento;
- reglas de implementación.

Los WFPRD especializados regulan exclusivamente el comportamiento de cada componente.

---

## 0.3 Documentación normativa

Este documento se complementa con:

- PROJECT_GUIDE v1.1
- EDL v0.6
- MRPD v1.1.1
- ADR-001 v1.2
- ADR-002 v1.0
- ADR-003 v1.0
- WFPRD_AMBIENT_MODE v1.1
- WFPRD_BATTERY_INDICATOR v1.2
- WFPRD_STEP_INDICATOR v1.1
- WFPRD_BROTHERHOOD_EMBLEM v1.3
- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3
- WATCHFACE_LAYOUT v1.3
- ACTIVE_SPRINT
- SPRINT_HISTORY
- BACKLOG v1.3

Todos ellos forman parte del marco documental oficial del proyecto.

---

## 0.4 Referencia visual

La referencia visual oficial del proyecto es:

Figura 1 — Referencia visual conceptual vigente

almacenada en:

docs/WATCHFACE_LAYOUT/assets/SURIOS_WATCH_REFERENCE_v1.png

Su finalidad es representar la intención visual del propietario.

En caso de discrepancia prevalecerán siempre:

- este WFPRD;
- los WFPRD especializados;
- WATCHFACE_LAYOUT;
- las coordenadas aprobadas;
- las validaciones físicas.

Las imágenes conceptuales nunca sustituyen a la normativa.

---

# 1. Visión y propósito

SuriOS Watch constituye la esfera oficial del ecosistema SuriOS para Xiaomi Watch 2 sobre Wear OS.

Su propósito es proporcionar información inmediata, técnica y fácilmente interpretable manteniendo una identidad visual propia y coherente con el resto del ecosistema.

El producto se desarrolla siguiendo los principios de:

- simplicidad;
- estabilidad;
- bajo consumo;
- evolución incremental;
- trazabilidad documental;
- validación física;
- ausencia de funcionalidad no aprobada.

Cada Sprint deberá aportar una mejora claramente delimitada sin introducir regresiones sobre funcionalidades previamente validadas.

---

# 2. Alcance

## 2.1 Incluido

Forma parte del alcance oficial:

- Watch Face principal del perfil CIVILIAN;
- Ambient Mode;
- hora;
- fecha;
- indicadores de batería;
- indicadores de pasos;
- emblema oficial de la Hermandad del Acero;
- componentes visuales institucionales aprobados;
- accesos directos a Spotify;
- accesos directos a Google Wallet;
- validación mediante Wear OS Large Round;
- validación física mediante Xiaomi Watch 2.

---

## 2.2 Excluido actualmente

Permanecen fuera del alcance:

- perfil OPERATION;
- Tiles;
- aplicación Wear OS independiente;
- pantallas secundarias;
- batería del teléfono;
- biometría;
- sincronización funcional con PIP-SuriOS;
- complicaciones configurables;
- configuraciones avanzadas;
- cualquier funcionalidad no aprobada documentalmente.

---

## 2.3 Perfiles

Actualmente el proyecto contempla:

### CIVILIAN

Perfil oficial del proyecto.

Desarrollo incremental.

Prioridad alta.

### OPERATION

Pendiente de definición.

Fuera del roadmap vigente.

---

# 3. Principios de diseño

Toda evolución de la esfera deberá respetar:

- prioridad absoluta de la hora;
- fecha como información secundaria;
- simetría antes que ocupación del espacio;
- lectura inmediata;
- ausencia de elementos innecesarios;
- fondo PipBlack;
- PipGreen como color principal;
- PipGreenDim como color secundario;
- ausencia de degradados;
- ausencia de sombras;
- ausencia de animaciones no aprobadas;
- predominio de la información sobre cualquier elemento decorativo.

El emblema oficial de la Hermandad del Acero constituye un elemento institucional subordinado a la información funcional.

Nunca podrá reducir la legibilidad de la esfera.

---

# 4. Lienzo y geometría

El sistema de referencia oficial utiliza un lienzo lógico de:

450 × 450

Origen:

(0,0)

Centro:

(225,225)

Eje principal:

X = 225

Todas las coordenadas del proyecto se expresan respecto a este sistema.

---

## 4.1 Geometría validada físicamente

Los siguientes componentes constituyen la geometría física validada del proyecto:

| Componente | X | Y | Width | Height | Tamaño |
|------------|--:|--:|------:|-------:|--------:|
| Hora | 55 | 42 | 340 | 92 | 82 |
| Fecha | 65 | 138 | 320 | 38 | 28 |

Estos valores constituyen la fuente de verdad del proyecto.

No podrán modificarse sin una nueva decisión documental aprobada.

---

## 4.2 Geometría objetivo

El resto de componentes utilizarán las coordenadas definidas en WATCHFACE_LAYOUT v1.3.

Hasta su validación física dichas coordenadas tendrán consideración de objetivo de diseño.

Los ajustes derivados de pruebas reales deberán:

- mantener la jerarquía visual;
- conservar la simetría general;
- documentarse;
- aprobarse antes de consolidarse como referencia.

# 5. Sistema de componentes

## 5.1 Hora

**Estado documental**

Aprobado.

**Estado de implementación**

Implementada y validada físicamente durante Sprint 001.

**Formato**

HH:MM

24 horas.

**Geometría**

La definida en el apartado 4.1.

**Interacción**

Ninguna.

**Ambient Mode**

Visible desde Sprint 003.

La geometría validada de este componente constituye una referencia permanente del proyecto y no podrá modificarse sin aprobación documental expresa.

---

## 5.2 Fecha

**Estado documental**

Aprobada.

**Estado de implementación**

Implementada y validada físicamente durante Sprint 001.

**Formato**

DD/MM/AAAA.

**Geometría**

La definida en el apartado 4.1.

**Interacción**

Ninguna.

**Ambient Mode**

Visible desde Sprint 003.

Al igual que la hora, su geometría constituye una referencia física consolidada.

---

## 5.3 Día de la semana

**Estado documental**

Aprobado como componente visual.

**Estado de implementación**

Pendiente.

**Función**

Mostrar el día completo de la semana.

Ejemplo:

LUNES

**Posición**

Zona superior de la esfera, subordinada a la hora y la fecha.

**Sprint previsto**

Pendiente de asignación.

**Ambient Mode**

Oculto.

La geometría y el comportamiento definitivo deberán aprobarse antes de su implementación.

---

## 5.4 Indicador de batería

**Estado documental**

Aprobado.

**Estado de implementación**

Implementado y validado durante Sprint 004.

**Estado físico**

Validado correctamente en:

- Wear OS Large Round.
- Xiaomi Watch 2.

**Fuente**

Batería del reloj.

**Modo activo**

- icono horizontal de diez niveles;
- porcentaje entero;
- sin barra.

**Carga**

Mientras el reloj permanezca conectado al cargador, el contenido será sustituido por:

RECHARGING

conforme a la implementación declarativa aprobada.

**Ambient Mode**

Desde Sprint 004:

- porcentaje únicamente;
- sin icono;
- sin barra.

El comportamiento durante la carga queda definido por:

WFPRD_BATTERY_INDICATOR v1.2

**Interacción**

Ninguna.

---

## 5.5 Indicador de pasos

**Estado documental**

Aprobado.

**Estado de implementación**

Implementado y validado durante Sprint 004.

**Estado físico**

Validado correctamente en:

- Wear OS Large Round.
- Xiaomi Watch 2.

**Fuente**

Contador de pasos del reloj.

**Representación**

0

↓

--

1–999

↓

Número completo.

1 000–999 999

↓

Número con separación mediante espacio.

Más de 999 999

↓

999 999+

**Modo activo**

Número únicamente.

**Ambient Mode**

Número únicamente.

**Interacción**

Ninguna.

No existirán:

- barras;
- iconos;
- porcentajes;
- objetivos diarios.

Toda la normativa funcional queda regulada por:

WFPRD_STEP_INDICATOR v1.1

---

## 5.6 Spotify

**Estado documental**

Objetivo aprobado.

**Estado de implementación**

Pendiente.

**Sprint previsto**

Sprint 006.

**Función prevista**

Acceso directo mediante toque único.

No se autoriza actualmente:

- controles;
- carátulas;
- listas;
- información de reproducción.

Toda ampliación funcional requerirá un WFPRD específico.

**Ambient Mode**

Oculto.

---

## 5.7 Google Wallet

**Estado documental**

Objetivo aprobado.

**Estado de implementación**

Pendiente.

**Sprint previsto**

Sprint 006.

**Función prevista**

Acceso directo mediante toque único.

La implementación dependerá de la viabilidad técnica real de Wear OS.

No se autorizarán soluciones alternativas no documentadas.

**Ambient Mode**

Oculto.

---

## 5.8 Emblema oficial de la Hermandad del Acero

**Estado documental**

Aprobado.

**Estado de implementación**

Previsto para Sprint 005.

**Función**

Elemento institucional de identidad visual.

Su presencia deberá reforzar la identidad del ecosistema SuriOS sin interferir con la lectura de la información funcional.

La normativa completa queda delegada en:

- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3
- WFPRD_BROTHERHOOD_EMBLEM v1.3

Este documento únicamente reconoce el componente como parte oficial del producto.

**Ambient Mode**

Su comportamiento será definido exclusivamente por WFPRD_BROTHERHOOD_EMBLEM.

La implementación física del componente se realizará exclusivamente durante Sprint 005.

---

## 5.9 Identificación SuriOS Watch

**Estado documental**

Aprobado.

**Estado de implementación**

Pendiente.

**Contenido**

SuriOS Watch.

**Jerarquía**

Subordinada a todos los indicadores funcionales.

**Sprint previsto**

Pendiente de asignación.

**Ambient Mode**

Oculto.

---

## 5.10 Identificación del perfil

**Estado documental**

Aprobado.

**Estado de implementación**

Pendiente.

**Contenido**

Identificación del perfil CIVILIAN o el texto que apruebe expresamente el propietario.

No podrá decidirse durante la implementación.

**Sprint previsto**

Pendiente de asignación.

**Ambient Mode**

Oculto.

---

## 5.11 Separadores gráficos

**Estado documental**

Aprobados cuando sean necesarios para reproducir la composición oficial.

**Estado de implementación**

Pendiente.

Deberán ser:

- discretos;
- funcionales;
- coherentes con el EDL;
- subordinados al resto de componentes.

Nunca podrán competir visualmente con:

- la hora;
- la fecha;
- la batería;
- los pasos;
- el emblema institucional.

**Ambient Mode**

Ocultos.

# 6. Ambient Mode

## Objetivo

Ambient Mode constituye la representación de bajo consumo de SuriOS Watch.

Su finalidad es preservar la información esencial reduciendo el consumo energético y respetando las recomendaciones de Wear OS.

---

## Sprint 003

Se incorporó:

- hora;
- fecha.

La transición entre modo activo y ambiente quedó implementada mediante Variant de Watch Face Format v1.

---

## Sprint 004

Se incorporó:

- porcentaje de batería;
- contador de pasos.

Se mantienen visibles:

- hora;
- fecha.

El icono de batería permanece oculto.

No existen:

- barras;
- elementos decorativos;
- animaciones;
- recursos gráficos adicionales.

---

## Sprint 005

El comportamiento del emblema institucional será definido exclusivamente por:

WFPRD_BROTHERHOOD_EMBLEM v1.3

Este documento no regula:

- presencia;
- ausencia;
- opacidad;
- intensidad;
- tamaño;
- representación en Ambient Mode.

---

## Componentes ocultos

En Ambient Mode permanecerán ocultos:

- Spotify;
- Google Wallet;
- identificación SuriOS Watch;
- identificación del perfil;
- separadores gráficos;
- cualquier otro elemento no aprobado específicamente.

---

## Principios

Ambient Mode deberá mantener:

- lectura inmediata;
- geometría validada;
- fondo PipBlack;
- ausencia de elementos innecesarios;
- consumo reducido.

---

# 7. Requisitos no funcionales

La esfera deberá cumplir permanentemente:

- funcionamiento completamente declarativo mediante Watch Face Format v1;
- ausencia de código Kotlin o Java para la lógica visual;
- ausencia de servicios residentes;
- ausencia de dependencias adicionales;
- ausencia de permisos innecesarios;
- estabilidad durante cambios entre modo activo y ambiente;
- mantenimiento de la geometría validada;
- ausencia de regresiones funcionales;
- compatibilidad con Xiaomi Watch 2;
- compatibilidad con Wear OS Large Round.

Las validaciones físicas prevalecerán sobre cualquier representación conceptual.

---

# 8. Restricciones

No estará permitido:

- modificar la geometría validada de hora y fecha;
- alterar la jerarquía visual aprobada;
- introducir barras de batería o pasos;
- añadir animaciones no aprobadas;
- incorporar recursos gráficos fuera de BROTHERHOOD_EMBLEM_ASSET_SPEC;
- modificar el comportamiento definido por los WFPRD especializados;
- introducir funcionalidades fuera del Sprint autorizado;
- implementar elementos experimentales sin documentación previa;
- utilizar imágenes conceptuales como normativa.

Toda modificación deberá quedar documentada antes de su implementación.

---

# 9. Roadmap

| Sprint | Estado | Alcance |
|---------|---------|---------|
| Sprint 001 | Completado | Hora y fecha |
| Sprint 002 | Completado | Base declarativa WFF v1 |
| Sprint 003 | Completado | Ambient Mode |
| Sprint 004 | Completado | Batería y pasos |
| Sprint 005 | Previsto | Emblema oficial de la Hermandad del Acero |
| Sprint 006 | Previsto | Spotify y Google Wallet |

La ejecución de cada Sprint requerirá autorización expresa.

No podrá iniciarse un Sprint únicamente por aparecer en este roadmap.

Los Sprint indicados representan la planificación vigente y no constituyen autorización para su inicio.

---

# 10. Criterios de aceptación por Sprint

## Sprint 001

Completado.

Se validó:

- hora;
- fecha;
- geometría inicial.

---

## Sprint 002

Completado.

Se validó:

- arquitectura declarativa;
- compatibilidad con Watch Face Format v1;
- estabilidad del proyecto.

---

## Sprint 003

Completado.

Se validó:

- Ambient Mode;
- transición activo ↔ ambiente;
- conservación de hora y fecha.

---

## Sprint 004

Completado.

Se validó:

- batería;
- porcentaje;
- diez niveles;
- RECHARGING;
- pasos;
- cuatro rangos de representación;
- integración en Ambient Mode;
- validación física mediante Xiaomi Watch 2;
- ausencia de regresiones.

---

## Sprint 005

Deberá validar:

- integración del emblema oficial;
- cumplimiento de BROTHERHOOD_EMBLEM_ASSET_SPEC;
- cumplimiento de WFPRD_BROTHERHOOD_EMBLEM;
- geometría definitiva;
- intensidad visual;
- validación física;
- ausencia de impacto sobre la legibilidad.

---

## Sprint 006

Deberá validar:

- acceso a Spotify;
- acceso a Google Wallet;
- comportamiento táctil;
- integración visual;
- compatibilidad con Ambient Mode;
- ausencia de regresiones;
- estabilidad general de la esfera.

La aceptación de cada Sprint requerirá:

- compilación correcta;
- validación documental;
- validación física cuando corresponda;
- ausencia de regresiones sobre funcionalidades previamente aprobadas.

# 11. Recursos

Los recursos gráficos utilizados por SuriOS Watch deberán cumplir las siguientes normas:

- pertenecer al repositorio oficial del proyecto;
- disponer de una especificación documental vigente;
- mantener trazabilidad documental;
- utilizar exclusivamente la paleta definida por el EDL;
- ser compatibles con Watch Face Format v1.

---

## Emblema institucional

Todos los recursos gráficos oficiales del emblema de la Hermandad del Acero quedan regulados exclusivamente por:

- BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3

Este documento define:

- archivo maestro;
- variantes oficiales;
- reglas de color;
- intensidad;
- transparencia;
- escalado;
- ubicación dentro del repositorio;
- restricciones de uso.

El comportamiento funcional del emblema queda definido exclusivamente por:

- WFPRD_BROTHERHOOD_EMBLEM v1.3.

Este WFPRD no duplica dichas reglas.

---

## Recursos futuros

Cualquier nuevo recurso gráfico deberá disponer, como mínimo, de:

- especificación documental;
- ubicación permanente en el repositorio;
- control de versiones;
- aprobación expresa antes de su utilización.

---

# 12. Definition of Done

Una versión funcional de SuriOS Watch únicamente podrá considerarse completada cuando:

- compile correctamente;
- supere todas las validaciones declarativas;
- mantenga la compatibilidad con Watch Face Format v1;
- no introduzca regresiones;
- conserve la geometría validada;
- respete el EDL;
- mantenga la jerarquía visual aprobada;
- preserve la estabilidad durante las transiciones entre modo activo y Ambient Mode;
- supere la validación física correspondiente;
- mantenga coherencia con la documentación vigente.
- mantener coherencia con los documentos normativos vigentes.

El producto final deberá incorporar únicamente componentes aprobados documentalmente.

Los componentes institucionales deberán cumplir además las especificaciones de:

- BROTHERHOOD_EMBLEM_ASSET_SPEC;
- WFPRD_BROTHERHOOD_EMBLEM.

---

# 13. Trazabilidad

## Evolución reciente

| Versión | Cambios principales |
|----------|--------------------|
| v1.4 → v1.5 | Actualización completa tras Sprint 003 y Sprint 004. |
| Sprint 003 | Ambient Mode completado. |
| Sprint 004 | Indicadores de batería y pasos implementados y validados. |
| Roadmap | Reorganización oficial de Sprint 005 y Sprint 006. |
| Emblema | Incorporación de BROTHERHOOD_EMBLEM_ASSET_SPEC v1.3 y WFPRD_BROTHERHOOD_EMBLEM v1.3. |
| WATCHFACE_LAYOUT | Actualización a la referencia geométrica v1.3. |
| Estado del proyecto | Sprint 001–004 completados; ningún Sprint activo. |

---

## Componentes implementados

Actualmente se consideran implementados y validados:

- hora;
- fecha;
- Ambient Mode;
- batería;
- pasos.

Pendientes:

- emblema oficial;
- Spotify;
- Google Wallet;
- resto de componentes previstos en el roadmap.

---

# 14. Historial

| Versión | Estado | Descripción |
|----------|--------|-------------|
| 1.1 | Histórica | Primera consolidación del WFPRD. |
| 1.2 | Histórica | Ajustes derivados de la evolución del proyecto y reorganización documental inicial. |
| 1.3 | Histórica | Consolidación del modelo declarativo y reorganización de componentes. |
| 1.4 | Histórica | Preparación documental para Sprint 003 y Sprint 004. |
| 1.5 | Aprobada y vigente | Actualiza el estado del proyecto tras el cierre de los Sprint 003 y 004, incorpora la reorganización oficial del roadmap, integra la documentación del emblema institucional, consolida WATCHFACE_LAYOUT v1.3 como referencia geométrica vigente y mantiene la separación entre geometría, comportamiento y recursos gráficos. |

---

# 15. Estado del documento

WFPRD v1.5 constituye la especificación funcional maestra vigente de SuriOS Watch.

Situación actual del proyecto:

- Sprint 001 completado.
- Sprint 002 completado.
- Sprint 003 completado.
- Sprint 004 completado.
- Sprint 005 previsto para la implementación del emblema oficial de la Hermandad del Acero.
- Sprint 006 previsto para la integración de Spotify y Google Wallet.

Actualmente no existe ningún Sprint activo.

La apertura de un nuevo Sprint requerirá autorización expresa y deberá quedar reflejada en:

- ACTIVE_SPRINT;
- SPRINT_HISTORY;
- documentación específica correspondiente.

Este documento permanecerá como referencia funcional principal hasta la aprobación de una versión posterior.

Toda modificación posterior de la esfera deberá mantener la trazabilidad documental establecida en este WFPRD.
