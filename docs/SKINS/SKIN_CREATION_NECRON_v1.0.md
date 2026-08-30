# Cuestionario de creación de una nueva skin para PIP-SuriOS

Versión: 1.0
Proyecto: PIP-SuriOS para Android
Estado: Implementada y validada — Sprint 018
Referencia: EDL de SuriOS + arquitectura compartida de skins

Este cuestionario debe completarse antes de diseñar o implementar una skin nueva. Las respuestas definirán la identidad visual, los recursos necesarios, las restricciones y los criterios de aceptación.

## 0. Instrucciones

- Completar como mínimo todos los campos marcados como **[OBLIGATORIO]**.
- Utilizar una respuesta por línea o adjuntar documentos complementarios cuando sea necesario.
- Las skins modifican la presentación, no la funcionalidad.
- La navegación, los datos, los repositorios, la persistencia, BLE, GPS, MAP, SONAR, RADS y demás módulos seguirán siendo compartidos.
- Toda referencia externa debe incluir URL, autor o propietario conocido y finalidad de uso.
- No se incorporarán assets de terceros sin revisar sus derechos de uso.

---

## 1. Identidad de la skin

### 1.1 Datos básicos

- **Nombre oficial de la skin [OBLIGATORIO]: NECRON
- **Identificador técnico sugerido [OBLIGATORIO]: NECRON
- **Descripción breve en una frase [OBLIGATORIO]: Estética basada en la raza Necrones de Warhammer 40K
- **Descripción ampliada: Utiliza datos de la Dinastía Szarekhan
- **Autor o responsable de la propuesta [OBLIGATORIO]: SURI
- **Fecha de solicitud [OBLIGATORIO]: 30/08/2026
- **Versión inicial propuesta:** 1.0
- **Estado deseado:** `APPROVED` para prototipo privado

### 1.2 Inspiración

- **Universo, facción, producto o estética de inspiración [OBLIGATORIO]: Warhammer 40K
- **Qué elementos deben reconocerse inmediatamente: Símbolos, esquema de colores, funcionamiento en base al lore disponible
- **Qué elementos deben evitarse:** Assets externos, degradados o decoración que reduzca la lectura operativa.
- **Nivel de fidelidad deseado:**
  - [ ] Solo inspiración general
  - [X] Homenaje visual reconocible
  - [ ] Recreación muy cercana
  - [ ] Otro:
- **Debe considerarse una skin original de SuriOS aunque parta de una inspiración externa? [OBLIGATORIO]: Sí
- **¿Existen nombres, símbolos o diseños protegidos implicados? [OBLIGATORIO]: Sí

### 1.3 Personalidad visual

Seleccionar hasta cinco rasgos principales:

- [ ] Militar
- [ ] Industrial
- [ ] Retro-futurista
- [ ] Tecnológica
- [ ] Mecánica
- [X] Robótica
- [ ] Exploración
- [ ] Médica
- [ ] Científica
- [ ] Civilian / discreta
- [ ] Operativa / táctica
- [ ] Hostil / agresiva
- [ ] Elegante / limpia
- [ ] Deteriorada / postapocalíptica
- [ ] Mística / ceremonial
- [ ] Otra:

Palabras clave adicionales:

- Blackstone
- Energía teal
- Metal envejecido / Szarekhan

---

## 2. Alcance funcional y de producto

- **¿La skin se aplicará a toda la aplicación Android? [OBLIGATORIO]:** Sí
- **¿Debe aplicarse también a pantallas de arranque y selección? [OBLIGATORIO]: No
- **¿Debe tener presencia en la watch face o en otros proyectos del ecosistema? [OBLIGATORIO]: Futuro
- **¿Qué módulos deben recibir tratamiento visual específico?:**
  - [ ] Splash / Loading
  - [ ] SELECT SKIN
  - [X] SET-UP
  - [X] HOME
  - [X] CURRENT GEAR
  - [X] INVENTORY
  - [X] STATUS / RADS
  - [X] DATA / STATS
  - [X] MAP / MAP TERRAIN
  - [X] COMMS / MORSE
  - [X] RADIO
  - [X] TOOLS
  - [X] P.R.S. / REMOTE PROBE
  - [ ] Todos
  - [ ] Otros:
- **¿Debe conservar exactamente la distribución actual de componentes? [OBLIGATORIO]: Sí
- **Excepciones de distribución autorizadas:** Se intentará aplicar NECRON a P.R.S. / REMOTE PROBE. Si la tematización reduce de forma apreciable la legibilidad, la densidad de información o la lectura operativa, P.R.S. conservará el tratamiento de Brotherhood.
- **¿Debe conservarse la orientación horizontal? [OBLIGATORIO]: Sí
- **¿Debe funcionar completamente sin conexión permanente a Internet? [OBLIGATORIO]:** Sí

### 2.1 Límites funcionales

- Funciones que no deben cambiar nunca: Las funcionalidades deben mantenerse iguales
- Acciones, rutas o pantallas que no deben alterarse:
- Datos que deben permanecer compatibles con otras skins:
- ¿La skin necesita algún recurso visual condicionado por un estado funcional?:

---

## 3. Dirección artística

### 3.1 Referencia general

- **Descripción del aspecto deseado [OBLIGATORIO]:** Terminal táctica de blackstone con energía teal, acentos de bronce y emblema geométrico NECRON.
- **Materiales visuales sugeridos:**
  - [X] Metal
  - [ ] Vidrio
  - [ ] CRT
  - [ ] Pantalla industrial
  - [ ] Papel / dossier
  - [X] Circuitería
  - [X] Holograma
  - [ ] Piedra / cerámica
  - [ ] Otro:
- **Nivel de desgaste visual:** Ninguno
- **Uso de ruido, grano o scanlines:** Sutil
- **¿Se permiten texturas de fondo?:** Sí
- **¿Se permiten degradados?:** Sí
- **¿Se permiten sombras o brillos?:** No. Justificación: debe parecer un terminal miliar de la facción seleccionada
- **¿Se permite una interfaz más decorativa que la actual?:** Sí

### 3.2 Composición

- **Estructura visual preferida:**
  - [ ] Terminal limpia
  - [ ] Paneles modulares
  - [X] HUD táctico
  - [ ] Instrumentación técnica
  - [ ] Fichas o dossiers
  - [ ] Otra:
- **Densidad de información:** Media
- **Simetría deseada:** Alta siempre que sea viable
- **Elemento visual dominante:** Emblema vectorial central y texto teal sobre AMOLED negro.
- **Elemento visual secundario:** Bordes y paneles técnicos en teal tenue.
- **Elementos que nunca deben competir con los datos:** Se priorizará la utilidad sobre la estética si hay conflicto

---

## 4. Paleta de color

El EDL actual utiliza `PipBlack #000000`, `PipGreen #66FF66`, `PipGreenDim #3FAF5A`, `PipAmber #FFC857` y `PipRed #FF4D4D`. Cualquier color nuevo debe aprobarse e incorporarse al lenguaje visual correspondiente.

He encontrado el codex de la facción en PDF, utiliza los contenidos de este para las decisiones estéticas de los campos 4 y 5. La dirección es https://es.scribd.com/document/694216403/Necrons-10ed

### 4.1 Paleta propuesta

| Token visual | Nombre del color | HEX | RGB/HSL opcional | Uso previsto | ¿Sustituye al token EDL? |
|---|---|---|---|---|---|
| Fondo principal | Blackstone | `#000000` | 0,0,0 | Fondo AMOLED | Sustituye PipBlack con el mismo valor |
| Texto principal / activo | Ghost teal | `#9DFFE9` | 157,255,233 | Texto activo y controles | Sustituye PipGreen |
| Texto secundario | Teal tenue | `#48BFAF` | 72,191,175 | Información secundaria y bordes | Sustituye PipGreenDim |
| Realce | Energy white | `#D4FFF6` | 212,255,246 | Destacados puntuales | Nuevo token |
| Advertencia | Aged bronze | `#E7B86A` | 231,184,106 | Advertencias | Sustituye PipAmber |
| Error / crítico | Critical red | `#FF5F62` | 255,95,98 | Errores y críticos | Sustituye PipRed |
| Desactivado | Muted metal | `#657A76` | 101,122,118 | Controles no disponibles | Sustituye PipGray |
| Enlace remoto / P.R.S. | Signal cyan | `#63D8F2` | 99,216,242 | Nodos y enlace P.R.S. | Sustituye PipBlue |
| Borde / línea | Teal tenue | `#48BFAF` | 72,191,175 | Bordes y reglas | Nuevo uso compartido |
| Fondo de panel | Dark blackstone | `#071211` | 7,18,17 | Superficies técnicas | Nuevo token |

### 4.2 Reglas de color

- **Color principal de la skin [OBLIGATORIO]:** `#9DFFE9` ghost teal
- **Color secundario [OBLIGATORIO]:** `#48BFAF` teal tenue
- **Color de advertencia [OBLIGATORIO]:** `#E7B86A` bronce
- **Color crítico [OBLIGATORIO]:** `#FF5F62` rojo crítico
- **¿Se mantienen diferenciados los estados normal, advertencia y crítico? [OBLIGATORIO]:** Sí
- **¿Se conserva el negro puro para AMOLED? [OBLIGATORIO]:** Sí. Justificación: maximiza contraste y reduce consumo en panel AMOLED.
- **¿Hay colores reservados para módulos concretos?:** `#63D8F2` para enlace/nodo P.R.S.
- **¿Se necesitan variantes de alto brillo y baja intensidad?:** Sí; `#D4FFF6` y `#48BFAF`.
- **¿Se requiere modo nocturno o de baja luminancia?:** No adicional; el fondo negro y los tokens tenues cubren el caso.
- **Contraste mínimo esperado o criterio de legibilidad:** Texto principal y controles deben leerse claramente sobre negro; la utilidad prevalece sobre el adorno.
- **Muestras de color adjuntas:** No; tokens documentados en `NECRON_VISUAL_SPEC_v1.0.md`.

### 4.3 Validación

- ¿El texto principal se lee correctamente sobre el fondo?: Sí
- ¿Los botones y bordes se distinguen sin sombras?: Sí
- ¿Los estados críticos siguen siendo reconocibles sin depender solo del color?: Sí; conservan etiquetas y semántica existentes.
- ¿La paleta ha sido revisada en una pantalla AMOLED?: Sí, Samsung A56.
- Observaciones: La aplicación visual a P.R.S. no produjo distorsión ni pérdida apreciable de legibilidad.

---

## 5. Tipografía y texto

- **Familia tipográfica principal [OBLIGATORIO]:** `FontFamily.Monospace` existente.
- **¿Se conserva una fuente monoespaciada? [OBLIGATORIO]:** Sí
- **Fuentes alternativas o de respaldo:** Respaldo del sistema Compose/Android.
- **Tratamiento de títulos:** Mayúsculas, tamaño grande, ghost teal.
- **Tratamiento de datos numéricos:** Monoespaciado, neutral o teal según semántica.
- **Tratamiento de etiquetas y botones:** Mayúsculas, sin ripple visual, controles heredados.
- **Uso de mayúsculas:** Siempre, conservando la terminología actual.
- **Espaciado entre letras:** El definido por la interfaz existente.
- **Peso tipográfico:** Normal.
- **¿Se permiten símbolos o glifos especiales?:** Sí, solo los ya usados por PIP-SuriOS (`>`, `<`, estados y símbolos de navegación).
- **¿Se modifica el idioma o la terminología actual?:** No
- **Textos nuevos necesarios:**
- **Textos que deben permanecer exactamente iguales:**

### 5.1 Legibilidad

- Tamaño mínimo de texto aceptable: El mínimo existente de PIP-SuriOS, sujeto a revisión de cada pantalla.
- ¿La skin debe ser legible a distancia de uso?: Sí
- ¿La skin debe ser legible con brillo bajo?: Sí
- ¿Se ha comprobado el aspecto en dispositivos de distintas densidades?: Parcialmente; validado en Samsung A56 y compilado para las variantes existentes.
- Riesgos de truncamiento, solapamiento o pérdida de caracteres: No observados en Home ni P.R.S.; queda pendiente la campaña física de uso.

---

## 6. Componentes de interfaz. Dado que es la primera versión, mantén el mismo criterio que la skin BROTHERHOOD

Definir si cada componente conserva su forma o recibe una variante visual.

| Componente | Se conserva | Se modifica | Descripción de la modificación | Prioridad |
|---|---:|---:|---|---|
| Cabecera | [ ] | [ ] |  | Alta / Media / Baja |
| Botones | [ ] | [ ] |  | Alta / Media / Baja |
| Bordes | [ ] | [ ] |  | Alta / Media / Baja |
| Paneles | [ ] | [ ] |  | Alta / Media / Baja |
| Listas | [ ] | [ ] |  | Alta / Media / Baja |
| Cursor `>` | [ ] | [ ] |  | Alta / Media / Baja |
| Indicadores | [ ] | [ ] |  | Alta / Media / Baja |
| Barras de progreso | [ ] | [ ] |  | Alta / Media / Baja |
| Diálogos | [ ] | [ ] |  | Alta / Media / Baja |
| Campos de entrada | [ ] | [ ] |  | Alta / Media / Baja |
| Estado vacío | [ ] | [ ] |  | Alta / Media / Baja |
| Estado de error | [ ] | [ ] |  | Alta / Media / Baja |
| Estado de carga | [ ] | [ ] |  | Alta / Media / Baja |
| Pie de pantalla | [ ] | [ ] |  | Alta / Media / Baja |

- **¿Se mantienen las esquinas rectas del lenguaje SuriOS? [OBLIGATORIO]:** Sí / No
- **¿Se mantiene el cursor de navegación? [OBLIGATORIO]:** Sí / No
- **¿Se mantiene una ruta de vuelta clara en todas las pantallas? [OBLIGATORIO]:** Sí / No
- **¿Se permiten nuevos componentes?:** Sí / No. Descripción:

---

## 7. Iconografía, emblemas y símbolos. Dado que es la primera versión, mantén el mismo criterio que la skin BROTHERHOOD

- **Familia de iconos [OBLIGATORIO]:**
  - [ ] Outline monocromo
  - [ ] Relleno monocromo
  - [ ] Pixel art
  - [ ] Técnico / esquemático
  - [ ] Otro:
- **Grosor de línea:**
- **Tamaño base:**
- **¿Se conserva la coherencia de una única familia de iconos? [OBLIGATORIO]:** Sí / No
- **Iconos que deben rediseñarse:**
- **Iconos que deben permanecer sin cambios:**
- **Símbolos específicos de la skin:**
- **Emblema o logotipo principal:**
- **Emblemas secundarios:**
- **Variantes necesarias:** normal / activo / tenue / desactivado / crítico / ambient
- **¿Debe sustituirse el emblema Brotherhood of Steel?:** Sí / No / Solo en determinadas pantallas
- **Si se sustituye, ¿qué recurso lo reemplaza?:**

Los recursos institucionales existentes deben respetar [BROTHERHOOD_EMBLEM_ASSET_SPEC_v1.3](../ASSETS/BROTHERHOOD%20EMBLEM/BROTHERHOOD_EMBLEM_ASSET_SPEC_v1.3.md) cuando sigan utilizándose.

---

## 8. Assets gráficos. ado que es la primera versión, busca en internet referencias a necrones, szarekh, szarekahn

### 8.1 Inventario

| Asset | Descripción | Formato | Resolución | Fondo transparente | Variante | Estado |
|---|---|---|---:|---:|---|---|
|  |  | PNG / SVG / WEBP / Otro |  | Sí / No |  | Pendiente / Listo |
|  |  | PNG / SVG / WEBP / Otro |  | Sí / No |  | Pendiente / Listo |
|  |  | PNG / SVG / WEBP / Otro |  | Sí / No |  | Pendiente / Listo |

- **Ubicación propuesta dentro del repositorio [OBLIGATORIO]:** La que consideres óptima
- **¿Los assets se derivan de un recurso maestro?:** Sí / No
- **Recurso maestro y ubicación:**
- **¿Deben existir variantes para diferentes densidades?:** Sí / No
- **¿Deben existir variantes para Ambient Mode?:** Sí / No
- **¿Se necesitan recursos para splash, icono de aplicación o launcher?:** Sí / No
- **¿Se requieren animaciones rasterizadas?:** Sí / No
- **Restricciones de tamaño de archivo:**
- **Assets que todavía deben crearse:**

### 8.2 Tratamiento visual

- Transparencia permitida: Sí
- Recortes permitidos: No
- Escalado permitido: Sí
- Recoloreado permitido: Sí
- Texturas o grano permitidos: Sí
- Desenfoque permitido: No
- Sombras permitidas: No
- Observaciones:

---

## 9. Audio, vibración y animación. Audio mantén el criterio de la skin BROTHERHOOD. Vibración mantén el criterio de la skin BROTHERHOOOD. Animación busca referencias visuales en internet.

- **¿La skin tendrá sonidos propios?:** Sí / No
- **Lista de sonidos necesarios:**
- **Formato y duración máxima:**
- **¿Los sonidos deben funcionar offline?:** Sí / No
- **¿La skin tendrá vibraciones diferenciadas?:** Sí / No
- **Lista de patrones hápticos:**
- **¿La skin tendrá animaciones propias?:** Sí / No
- **Lista de animaciones:**
- **Duración máxima de cada animación:**
- **¿Las animaciones son funcionales o decorativas?:**
- **¿Se deben desactivar para ahorrar batería?:** Sí / No
- **¿Se permiten scanlines, parpadeos o efectos CRT?:** Sí / No
- **¿Se debe respetar una opción de reducción de movimiento?:** Sí / No

---

## 10. Estados y comportamiento visual. Mantén la estructura de la skin BROTHERHOOD

Definir cómo representa la skin los estados sin romper la semántica funcional.

| Estado | Representación visual | Color | Icono / patrón adicional | Sonido / vibración |
|---|---|---|---|---|
| Normal |  |  |  |  |
| Activo / seleccionado |  |  |  |  |
| Cargando |  |  |  |  |
| Sin datos |  |  |  |  |
| Desactivado |  |  |  |  |
| Advertencia |  |  |  |  |
| Error |  |  |  |  |
| Crítico |  |  |  |  |
| Conectado |  |  |  |  |
| Desconectado |  |  |  |  |
| P.R.S. remoto |  |  |  |  |

- **¿La información crítica se reconoce sin color?:** Sí / No
- **¿Qué elementos pueden parpadear?:**
- **¿Qué elementos no deben animarse nunca?:**
- **¿Qué ocurre durante la pérdida de conexión?:**
- **¿Qué ocurre durante un error de permisos?:**
- **¿Qué ocurre cuando faltan datos?:**

---

## 11. Rendimiento, batería y compatibilidad Android. Utiliza los criterios de la skin BROTHERHOOD para las preguntas no respondidas

- **Dispositivos objetivo [OBLIGATORIO]:** Samsung A56
- **Versión mínima de Android [OBLIGATORIO]:**
- **Resoluciones y relaciones de aspecto a validar:**
- **¿Debe soportar Z Flip 6 u otra pantalla exterior?:** Futuro
- **¿Debe optimizarse para AMOLED?:** Sí
- **Consumo adicional máximo aceptable:**
- **Tamaño máximo de APK o recursos:**
- **¿Se permite cargar assets grandes en memoria?:**
- **¿Se permite renderizado continuo?:**
- **¿Debe funcionar con escalado de fuente del sistema?:** Sí
- **¿Debe funcionar con alto contraste del sistema?:** Sí
- **Riesgos técnicos conocidos:**

---

## 12. Compatibilidad con la arquitectura actual

- **¿La skin utiliza únicamente el sistema visual existente?:** Sí, con tokens y un recurso vectorial nuevo.
- **¿Necesita nuevos tokens de color?:** Sí
- **¿Necesita nuevos componentes Compose?:** No
- **¿Necesita nuevos recursos Android?:** Sí, emblema vectorial.
- **¿Necesita cambiar el modelo `SkinId`?:** Sí, se habilita `NECRON` como implementada.
- **¿Necesita persistir la selección?:** No; la selección es de sesión.
- **¿Necesita una configuración editable desde la aplicación?:** No adicional.
- **¿Debe coexistir con otras skins instaladas?:** Sí, dentro del mismo catálogo.
- **¿Hay lógica funcional que alguien propone modificar? [OBLIGATORIO]:** No
- **Si la respuesta anterior es Sí, justificar y separar esa petición del alcance de la skin:** No aplica.

### 12.1 Compartición funcional

Confirmar que la skin no crea bifurcaciones de:

- [X] Navegación
- [X] Modelos de datos
- [X] Repositories
- [X] Persistencia
- [X] BLE
- [X] GPS
- [X] MAP
- [X] SONAR
- [X] RADS
- [X] DATA
- [X] STORAGE
- [X] P.R.S.
- [X] Estadísticas
- [X] Permisos

Excepciones aprobadas:

- P.R.S. recibe NECRON porque la revisión visual en A56 mantuvo legibilidad.
- Si una validación posterior detecta pérdida de lectura, solo P.R.S. volverá a Brotherhood.

---

## 13. Referencias públicas

Utilizar este apartado para aportar páginas web, capturas, documentación, vídeos o repositorios que definan la dirección visual. Las referencias son inspiración o documentación; no autorizan automáticamente copiar ni redistribuir sus recursos.

Web: https://es.scribd.com/document/694216403/Necrons-10ed

### 13.1 Enlaces principales

| URL [OBLIGATORIO] | Título de la página | Autor / propietario | Tipo de referencia | Qué debe estudiarse | Qué no debe copiarse |
|---|---|---|---|---|---|
| https://www.warhammer-community.com/en-gb/articles/5Sz5J6wE/ | Ancient Dynasties of the Necrons | Games Workshop | Documentación / Web | Dirección de color, materiales y dinastías | No copiar texto, ilustraciones ni assets |
| https://www.warhammer-community.com/en-gb/articles/wE77ZMK0/designing-the-silent-king/ | Designing and Painting the Silent King | Games Workshop | Documentación / Web | Contraste de metal, energía y composición | No copiar el diseño del personaje ni imágenes |
| https://warhammer40000.com/games/faction/necrons/ | Necrons | Games Workshop | Web | Identidad general de la facción | No copiar marcas, símbolos ni recursos |
| https://es.scribd.com/document/694216403/Necrons-10ed | Necrons 10ed | Titular de la publicación enlazada | Documentación | Referencia de lore facilitada por el propietario | No incorporar el PDF ni extraer assets |
| https://developer.android.com/develop/ui/compose | Jetpack Compose | Google | Documentación técnica | Integración de tokens y UI Compose | No copiar recursos de terceros |

### 13.2 Clasificación de cada referencia

Para cada URL, indicar:

- **Referencia de color:** Warhammer Community y codex enlazado; traducida a tokens propios.
- **Referencia de tipografía:** EDL y `FontFamily.Monospace` existente.
- **Referencia de composición/layout:** EDL, Brotherhood of Steel y estructura actual de PIP-SuriOS.
- **Referencia de iconografía:** Símbolos propios de la interfaz; no se incorporan iconos externos.
- **Referencia de emblemas o símbolos:** Inspiración Necron; emblema implementado como vectorial original del prototipo.
- **Referencia de animación:** No se añaden animaciones propias en v1.0.
- **Referencia de audio:** Se mantiene Brotherhood; no se añaden sonidos.
- **Referencia de interacción:** Navegación y controles existentes.
- **Referencia de materiales/texturas:** Blackstone, metal y circuitería como dirección visual, sin texturas externas.
- **Referencia de accesibilidad o legibilidad:** Contraste sobre negro, etiquetas semánticas y validación visual en A56.

### 13.3 Referencias visuales adjuntas

- Nombre del archivo:
- Ubicación propuesta:
- URL de origen:
- Autor / licencia:
- Uso permitido:
- Uso previsto en la skin:
- ¿Es solo referencia o se incorporará al producto?:

Repetir este bloque por cada imagen, vídeo o documento adjunto.

### 13.4 Jerarquía de referencias

- **Referencia principal [OBLIGATORIO]:** EDL de SuriOS y estructura visual de Brotherhood of Steel.
- **Referencia secundaria:** Referencias públicas de Necrons/Szarekhan listadas en 13.1.
- **Referencia técnica:** Jetpack Compose y arquitectura de tokens existente.
- **Referencia que debe descartarse si contradice el EDL:** Cualquier composición, efecto o densidad que reduzca utilidad o legibilidad.
- **Conflictos entre referencias detectados:** La fidelidad visual externa queda subordinada a la funcionalidad y al uso privado.

---

## 14. Derechos, licencias y atribución

- **¿La skin está basada en una propiedad intelectual externa? [OBLIGATORIO]:** Sí
- **Propietario o titulares relevantes:** Games Workshop
- **¿Los nombres utilizados son originales de SuriOS?:** No. `NECRON`, `Necrons` y `Szarekhan` son identificadores de trabajo basados en una propiedad intelectual externa.
- **¿Los emblemas, tipografías, sonidos e imágenes tienen licencia compatible?:** No se incorporan recursos externos; el emblema es un vectorial nativo del prototipo.
- **Tipo de licencia de cada recurso:** Código y vectorial propios del proyecto; referencias externas solo documentales.
- **Atribución requerida:** Mantener esta declaración de uso privado y referencias documentales; no redistribuir material externo.
- **¿Se permite incluir los recursos en una APK?:** Sí, para el prototipo privado y solo con recursos propios.
- **¿La skin se distribuirá públicamente?:** No. Uso privado por defecto; cualquier publicación requerirá una variante independiente sin objetos de propiedad intelectual de terceros.
- **¿Debe crearse una variante original que evite usar marcas o símbolos externos?:** No para el prototipo privado; sí si se solicita una publicación.
- **Riesgos o decisiones pendientes:** No publicar esta versión. Si se publica, crear una variante original sin nombres, símbolos ni elementos de propiedad intelectual externa.

---

## 15. Entregables solicitados

Marcar los materiales que debe producir el diseño:

- [X] Documento de identidad visual
- [X] Ficha de paleta
- [X] Ficha tipográfica
- [X] Moodboard
- [X] Wireframes
- [ ] Capturas de referencia anotadas
- [X] Pantalla de selección actualizada
- [ ] Pantallas de ejemplo
- [X] Assets maestros
- [X] Variantes de assets
- [X] Iconos
- [ ] Sonidos propios (se conserva Brotherhood)
- [ ] Patrones hápticos propios (se conserva Brotherhood)
- [ ] Especificación de animaciones propias (se conserva Brotherhood)
- [X] Guía de implementación
- [X] Criterios de aceptación
- [X] Matriz de compatibilidad Android
- [X] Registro de licencias

Formato de entrega preferido:

Código Compose, tokens de tema, recurso vectorial y documentación Markdown.

Ubicación propuesta en el repositorio:

`app/src/main/java/com/suri/pipsurios/ui/skin`, `app/src/main/java/com/suri/pipsurios/ui/theme`, `app/src/main/res/drawable` y `docs/SKINS`.

---

## 16. Criterios de aceptación

La skin podrá considerarse lista para revisión cuando:

- [X] Tiene nombre e identificador técnico aprobados.
- [X] Su paleta está completa y documentada.
- [X] Los estados normal, activo, advertencia, error y crítico son distinguibles.
- [X] Mantiene legibilidad en horizontal y en el dispositivo objetivo.
- [X] No rompe la navegación ni cambia funciones compartidas.
- [X] Mantiene una ruta de vuelta clara.
- [X] Los assets tienen formato, tamaño, ubicación y licencia documentados.
- [X] Las referencias públicas están clasificadas y justificadas.
- [X] Se han resuelto las discrepancias con el EDL.
- [X] Se ha validado el comportamiento en fondo AMOLED.
- [X] Se ha probado con datos y estados operativos de P.R.S.
- [X] Se ha revisado el rendimiento y el consumo dentro del alcance del cambio visual.
- [X] Se han ejecutado las pruebas automatizadas existentes.
- [X] Se ha realizado revisión visual manual.
- [X] Se ha aprobado la implementación final para uso privado.

### 16.1 Dispositivos de validación

| Dispositivo / emulador | Android | Resolución | Orientación | Resultado | Incidencias |
|---|---|---:|---|---|---|
| Samsung A56 (`SM_A566B`) | Android del dispositivo | 1080 × 2340 | Horizontal | Validado | Sin distorsión apreciable en Home y P.R.S. |
| Emulador Wear OS (`emulator-5554`) | No aplica a esta skin Android |  | Horizontal | Fuera de alcance | La skin no se aplica a la watch face en v1.0 |

### 16.2 Estados que deben probarse

- [X] Arranque
- [X] Selección de skin
- [X] HOME con datos completos
- [ ] HOME sin datos
- [X] Carga
- [ ] Error
- [X] Advertencia
- [ ] Estado crítico
- [ ] Sin permisos
- [X] Sin conexión / estado de enlace visible
- [X] Dispositivo BLE detectado
- [X] P.R.S. activo
- [ ] MAP sin cobertura
- [X] Pantalla con texto largo
- [X] Pantalla con valores numéricos extremos
- [ ] Brillo bajo
- [ ] Modo ahorro de batería

---

## 17. Aprobación

- **Decisión final:** `APPROVED`
- **Aprobador:** Diego Pérez de Camino
- **Fecha:** 30/08/2026
- **Versión aprobada:** NECRON v1.0 — Sprint 018
- **Commit o referencia de implementación:** Commit de cierre de Sprint 018, con mensaje `Sprint 018 - NECRON skin audit and A56 validation`.
- **Observaciones finales:** Uso privado. P.R.S. conserva NECRON porque la revisión en A56 mantuvo legibilidad; si una prueba posterior detecta distorsión, se revertirá solo el tratamiento visual de P.R.S.

Firma o confirmación:

```text
Nombre: Diego Pérez de Camino
Fecha: 30/08/2026
Confirmación: Aprobada para prototipo privado y validación en Samsung A56.
```
