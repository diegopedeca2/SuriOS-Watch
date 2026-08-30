# Cuestionario de creación de una nueva skin para PIP-SuriOS

Versión: 1.0
Proyecto: PIP-SuriOS para Android
Estado: Plantilla de definición
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

- **Nombre oficial de la skin [OBLIGATORIO]:**
- **Identificador técnico sugerido [OBLIGATORIO]:**
- **Descripción breve en una frase [OBLIGATORIO]:**
- **Descripción ampliada:**
- **Autor o responsable de la propuesta [OBLIGATORIO]:**
- **Fecha de solicitud [OBLIGATORIO]:**
- **Versión inicial propuesta:**
- **Estado deseado:** `CONCEPT`, `DESIGN`, `IMPLEMENTATION`, `REVIEW`, `APPROVED`

### 1.2 Inspiración

- **Universo, facción, producto o estética de inspiración [OBLIGATORIO]:**
- **Qué elementos deben reconocerse inmediatamente:**
- **Qué elementos deben evitarse:**
- **Nivel de fidelidad deseado:**
  - [ ] Solo inspiración general
  - [ ] Homenaje visual reconocible
  - [ ] Recreación muy cercana
  - [ ] Otro:
- **Debe considerarse una skin original de SuriOS aunque parta de una inspiración externa? [OBLIGATORIO]:**
- **¿Existen nombres, símbolos o diseños protegidos implicados? [OBLIGATORIO]:**

### 1.3 Personalidad visual

Seleccionar hasta cinco rasgos principales:

- [ ] Militar
- [ ] Industrial
- [ ] Retro-futurista
- [ ] Tecnológica
- [ ] Mecánica
- [ ] Robótica
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

-
-
-

---

## 2. Alcance funcional y de producto

- **¿La skin se aplicará a toda la aplicación Android? [OBLIGATORIO]:** Sí / No
- **¿Debe aplicarse también a pantallas de arranque y selección? [OBLIGATORIO]:** Sí / No / Solo algunas
- **¿Debe tener presencia en la watch face o en otros proyectos del ecosistema? [OBLIGATORIO]:** Sí / No / Futuro
- **¿Qué módulos deben recibir tratamiento visual específico?:**
  - [ ] Splash / Loading
  - [ ] SELECT SKIN
  - [ ] SET-UP
  - [ ] HOME
  - [ ] CURRENT GEAR
  - [ ] INVENTORY
  - [ ] STATUS / RADS
  - [ ] DATA / STATS
  - [ ] MAP / MAP TERRAIN
  - [ ] COMMS / MORSE
  - [ ] RADIO
  - [ ] TOOLS
  - [ ] P.R.S. / REMOTE PROBE
  - [ ] Todos
  - [ ] Otros:
- **¿Debe conservar exactamente la distribución actual de componentes? [OBLIGATORIO]:** Sí / No / Con excepciones
- **Excepciones de distribución autorizadas:**
- **¿Debe conservarse la orientación horizontal? [OBLIGATORIO]:** Sí / No
- **¿Debe funcionar completamente sin conexión permanente a Internet? [OBLIGATORIO]:** Sí / No

### 2.1 Límites funcionales

- Funciones que no deben cambiar nunca:
- Acciones, rutas o pantallas que no deben alterarse:
- Datos que deben permanecer compatibles con otras skins:
- ¿La skin necesita algún recurso visual condicionado por un estado funcional?:

---

## 3. Dirección artística

### 3.1 Referencia general

- **Descripción del aspecto deseado [OBLIGATORIO]:**
- **Materiales visuales sugeridos:**
  - [ ] Metal
  - [ ] Vidrio
  - [ ] CRT
  - [ ] Pantalla industrial
  - [ ] Papel / dossier
  - [ ] Circuitería
  - [ ] Holograma
  - [ ] Piedra / cerámica
  - [ ] Otro:
- **Nivel de desgaste visual:** Ninguno / Bajo / Medio / Alto
- **Uso de ruido, grano o scanlines:** No / Sutil / Visible / Otro
- **¿Se permiten texturas de fondo?:** Sí / No
- **¿Se permiten degradados?:** Sí / No. Justificación:
- **¿Se permiten sombras o brillos?:** Sí / No. Justificación:
- **¿Se permite una interfaz más decorativa que la actual?:** Sí / No. Límites:

### 3.2 Composición

- **Estructura visual preferida:**
  - [ ] Terminal limpia
  - [ ] Paneles modulares
  - [ ] HUD táctico
  - [ ] Instrumentación técnica
  - [ ] Fichas o dossiers
  - [ ] Otra:
- **Densidad de información:** Baja / Media / Alta
- **Simetría deseada:** Baja / Media / Alta
- **Elemento visual dominante:**
- **Elemento visual secundario:**
- **Elementos que nunca deben competir con los datos:**

---

## 4. Paleta de color

El EDL actual utiliza `PipBlack #000000`, `PipGreen #66FF66`, `PipGreenDim #3FAF5A`, `PipAmber #FFC857` y `PipRed #FF4D4D`. Cualquier color nuevo debe aprobarse e incorporarse al lenguaje visual correspondiente.

### 4.1 Paleta propuesta

| Token visual | Nombre del color | HEX | RGB/HSL opcional | Uso previsto | ¿Sustituye al token EDL? |
|---|---|---|---|---|---|
| Fondo principal |  |  |  |  |  |
| Texto principal / activo |  |  |  |  |  |
| Texto secundario |  |  |  |  |  |
| Realce |  |  |  |  |  |
| Advertencia |  |  |  |  |  |
| Error / crítico |  |  |  |  |  |
| Desactivado |  |  |  |  |  |
| Enlace remoto / P.R.S. |  |  |  |  |  |
| Borde / línea |  |  |  |  |  |
| Fondo de panel |  |  |  |  |  |

### 4.2 Reglas de color

- **Color principal de la skin [OBLIGATORIO]:**
- **Color secundario [OBLIGATORIO]:**
- **Color de advertencia [OBLIGATORIO]:**
- **Color crítico [OBLIGATORIO]:**
- **¿Se mantienen diferenciados los estados normal, advertencia y crítico? [OBLIGATORIO]:** Sí / No
- **¿Se conserva el negro puro para AMOLED? [OBLIGATORIO]:** Sí / No. Justificación:
- **¿Hay colores reservados para módulos concretos?:**
- **¿Se necesitan variantes de alto brillo y baja intensidad?:**
- **¿Se requiere modo nocturno o de baja luminancia?:**
- **Contraste mínimo esperado o criterio de legibilidad:**
- **Muestras de color adjuntas:** Sí / No

### 4.3 Validación

- ¿El texto principal se lee correctamente sobre el fondo?: Sí / No
- ¿Los botones y bordes se distinguen sin sombras?: Sí / No
- ¿Los estados críticos siguen siendo reconocibles sin depender solo del color?: Sí / No
- ¿La paleta ha sido revisada en una pantalla AMOLED?: Sí / No
- Observaciones:

---

## 5. Tipografía y texto

- **Familia tipográfica principal [OBLIGATORIO]:**
- **¿Se conserva una fuente monoespaciada? [OBLIGATORIO]:** Sí / No
- **Fuentes alternativas o de respaldo:**
- **Tratamiento de títulos:**
- **Tratamiento de datos numéricos:**
- **Tratamiento de etiquetas y botones:**
- **Uso de mayúsculas:** Siempre / Parcial / Normal
- **Espaciado entre letras:**
- **Peso tipográfico:** Normal / Medio / Negrita / Variable
- **¿Se permiten símbolos o glifos especiales?:** Sí / No. Lista:
- **¿Se modifica el idioma o la terminología actual?:** Sí / No
- **Textos nuevos necesarios:**
- **Textos que deben permanecer exactamente iguales:**

### 5.1 Legibilidad

- Tamaño mínimo de texto aceptable:
- ¿La skin debe ser legible a distancia de uso?: Sí / No
- ¿La skin debe ser legible con brillo bajo?: Sí / No
- ¿Se ha comprobado el aspecto en dispositivos de distintas densidades?: Sí / No
- Riesgos de truncamiento, solapamiento o pérdida de caracteres:

---

## 6. Componentes de interfaz

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

## 7. Iconografía, emblemas y símbolos

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

## 8. Assets gráficos

### 8.1 Inventario

| Asset | Descripción | Formato | Resolución | Fondo transparente | Variante | Estado |
|---|---|---|---:|---:|---|---|
|  |  | PNG / SVG / WEBP / Otro |  | Sí / No |  | Pendiente / Listo |
|  |  | PNG / SVG / WEBP / Otro |  | Sí / No |  | Pendiente / Listo |
|  |  | PNG / SVG / WEBP / Otro |  | Sí / No |  | Pendiente / Listo |

- **Ubicación propuesta dentro del repositorio [OBLIGATORIO]:**
- **¿Los assets se derivan de un recurso maestro?:** Sí / No
- **Recurso maestro y ubicación:**
- **¿Deben existir variantes para diferentes densidades?:** Sí / No
- **¿Deben existir variantes para Ambient Mode?:** Sí / No
- **¿Se necesitan recursos para splash, icono de aplicación o launcher?:** Sí / No
- **¿Se requieren animaciones rasterizadas?:** Sí / No
- **Restricciones de tamaño de archivo:**
- **Assets que todavía deben crearse:**

### 8.2 Tratamiento visual

- Transparencia permitida: Sí / No
- Recortes permitidos: Sí / No
- Escalado permitido: Sí / No
- Recoloreado permitido: Sí / No
- Texturas o grano permitidos: Sí / No
- Desenfoque permitido: Sí / No
- Sombras permitidas: Sí / No
- Observaciones:

---

## 9. Audio, vibración y animación

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

## 10. Estados y comportamiento visual

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

## 11. Rendimiento, batería y compatibilidad Android

- **Dispositivos objetivo [OBLIGATORIO]:**
- **Versión mínima de Android [OBLIGATORIO]:**
- **Resoluciones y relaciones de aspecto a validar:**
- **¿Debe soportar Z Flip 6 u otra pantalla exterior?:** Sí / No / Futuro
- **¿Debe optimizarse para AMOLED?:** Sí / No
- **Consumo adicional máximo aceptable:**
- **Tamaño máximo de APK o recursos:**
- **¿Se permite cargar assets grandes en memoria?:** Sí / No
- **¿Se permite renderizado continuo?:** Sí / No
- **¿Debe funcionar con escalado de fuente del sistema?:** Sí / No
- **¿Debe funcionar con alto contraste del sistema?:** Sí / No
- **Riesgos técnicos conocidos:**

---

## 12. Compatibilidad con la arquitectura actual

- **¿La skin utiliza únicamente el sistema visual existente?:** Sí / No
- **¿Necesita nuevos tokens de color?:** Sí / No
- **¿Necesita nuevos componentes Compose?:** Sí / No
- **¿Necesita nuevos recursos Android?:** Sí / No
- **¿Necesita cambiar el modelo `SkinId`?:** Sí / No
- **¿Necesita persistir la selección?:** Sí / No
- **¿Necesita una configuración editable desde la aplicación?:** Sí / No
- **¿Debe coexistir con otras skins instaladas?:** Sí / No
- **¿Hay lógica funcional que alguien propone modificar? [OBLIGATORIO]:** Sí / No
- **Si la respuesta anterior es Sí, justificar y separar esa petición del alcance de la skin:**

### 12.1 Compartición funcional

Confirmar que la skin no crea bifurcaciones de:

- [ ] Navegación
- [ ] Modelos de datos
- [ ] Repositories
- [ ] Persistencia
- [ ] BLE
- [ ] GPS
- [ ] MAP
- [ ] SONAR
- [ ] RADS
- [ ] DATA
- [ ] STORAGE
- [ ] P.R.S.
- [ ] Estadísticas
- [ ] Permisos

Excepciones aprobadas:

-

---

## 13. Referencias públicas

Utilizar este apartado para aportar páginas web, capturas, documentación, vídeos o repositorios que definan la dirección visual. Las referencias son inspiración o documentación; no autorizan automáticamente copiar ni redistribuir sus recursos.

### 13.1 Enlaces principales

| URL [OBLIGATORIO] | Título de la página | Autor / propietario | Tipo de referencia | Qué debe estudiarse | Qué no debe copiarse |
|---|---|---|---|---|---|
|  |  |  | Web / Imagen / Vídeo / Repositorio / Documentación |  |  |
|  |  |  | Web / Imagen / Vídeo / Repositorio / Documentación |  |  |
|  |  |  | Web / Imagen / Vídeo / Repositorio / Documentación |  |  |
|  |  |  | Web / Imagen / Vídeo / Repositorio / Documentación |  |  |
|  |  |  | Web / Imagen / Vídeo / Repositorio / Documentación |  |  |

### 13.2 Clasificación de cada referencia

Para cada URL, indicar:

- **Referencia de color:**
- **Referencia de tipografía:**
- **Referencia de composición/layout:**
- **Referencia de iconografía:**
- **Referencia de emblemas o símbolos:**
- **Referencia de animación:**
- **Referencia de audio:**
- **Referencia de interacción:**
- **Referencia de materiales/texturas:**
- **Referencia de accesibilidad o legibilidad:**

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

- **Referencia principal [OBLIGATORIO]:**
- **Referencia secundaria:**
- **Referencia técnica:**
- **Referencia que debe descartarse si contradice el EDL:**
- **Conflictos entre referencias detectados:**

---

## 14. Derechos, licencias y atribución

- **¿La skin está basada en una propiedad intelectual externa? [OBLIGATORIO]:** Sí / No
- **Propietario o titulares relevantes:**
- **¿Los nombres utilizados son originales de SuriOS?:** Sí / No
- **¿Los emblemas, tipografías, sonidos e imágenes tienen licencia compatible?:** Sí / No / Pendiente
- **Tipo de licencia de cada recurso:**
- **Atribución requerida:**
- **¿Se permite incluir los recursos en una APK?:** Sí / No / Pendiente
- **¿La skin se distribuirá públicamente?:** Sí / No / Pendiente
- **¿Debe crearse una variante original que evite usar marcas o símbolos externos?:** Sí / No
- **Riesgos o decisiones pendientes:**

---

## 15. Entregables solicitados

Marcar los materiales que debe producir el diseño:

- [ ] Documento de identidad visual
- [ ] Ficha de paleta
- [ ] Ficha tipográfica
- [ ] Moodboard
- [ ] Wireframes
- [ ] Capturas de referencia anotadas
- [ ] Pantalla de selección actualizada
- [ ] Pantallas de ejemplo
- [ ] Assets maestros
- [ ] Variantes de assets
- [ ] Iconos
- [ ] Sonidos
- [ ] Patrones hápticos
- [ ] Especificación de animaciones
- [ ] Guía de implementación
- [ ] Criterios de aceptación
- [ ] Matriz de compatibilidad Android
- [ ] Registro de licencias

Formato de entrega preferido:

-

Ubicación propuesta en el repositorio:

-

---

## 16. Criterios de aceptación

La skin podrá considerarse lista para revisión cuando:

- [ ] Tiene nombre e identificador técnico aprobados.
- [ ] Su paleta está completa y documentada.
- [ ] Los estados normal, activo, advertencia, error y crítico son distinguibles.
- [ ] Mantiene legibilidad en horizontal y en los dispositivos objetivo.
- [ ] No rompe la navegación ni cambia funciones compartidas.
- [ ] Mantiene una ruta de vuelta clara.
- [ ] Los assets tienen formato, tamaño, ubicación y licencia documentados.
- [ ] Las referencias públicas están clasificadas y justificadas.
- [ ] Se han resuelto las discrepancias con el EDL.
- [ ] Se ha validado el comportamiento en fondo AMOLED.
- [ ] Se ha probado con datos reales y estados vacíos/error.
- [ ] Se ha revisado el rendimiento y el consumo.
- [ ] Se han ejecutado las pruebas automatizadas existentes.
- [ ] Se ha realizado revisión visual manual.
- [ ] Se ha aprobado la implementación final.

### 16.1 Dispositivos de validación

| Dispositivo / emulador | Android | Resolución | Orientación | Resultado | Incidencias |
|---|---|---:|---|---|---|
|  |  |  | Horizontal | Pendiente |  |
|  |  |  | Horizontal | Pendiente |  |

### 16.2 Estados que deben probarse

- [ ] Arranque
- [ ] Selección de skin
- [ ] HOME con datos completos
- [ ] HOME sin datos
- [ ] Carga
- [ ] Error
- [ ] Advertencia
- [ ] Estado crítico
- [ ] Sin permisos
- [ ] Sin conexión
- [ ] Dispositivo BLE detectado
- [ ] P.R.S. activo
- [ ] MAP sin cobertura
- [ ] Pantalla con texto largo
- [ ] Pantalla con valores numéricos extremos
- [ ] Brillo bajo
- [ ] Modo ahorro de batería

---

## 17. Aprobación

- **Decisión final:** `APPROVED` / `CHANGES_REQUIRED` / `REJECTED`
- **Aprobador:**
- **Fecha:**
- **Versión aprobada:**
- **Commit o referencia de implementación:**
- **Observaciones finales:**

Firma o confirmación:

```text
Nombre:
Fecha:
Confirmación:
```
