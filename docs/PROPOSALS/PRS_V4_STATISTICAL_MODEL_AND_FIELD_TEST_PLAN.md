# P.R.S. v4.0 — Modelo estadístico y plan de pruebas físicas

**Documento de trabajo editable — SuriOS / PIP-SuriOS**
**Fecha inicial:** 2026-09-02
**Estado:** borrador técnico para revisar durante esta semana
**Ámbito:** P.R.S. v4.0, con las opciones `ONLY PIP-BOY` y `PIP-BOY + PROBE`

---

## 1. Para qué sirve este documento

Este documento une tres cosas que deben avanzar juntas:

1. El modelo estadístico que queremos desarrollar.
2. El funcionamiento real que tiene hoy la aplicación.
3. Las pruebas físicas necesarias para comprobar que el modelo funciona fuera del laboratorio.

No se considera todavía una especificación definitiva. Durante el sprint podremos cambiar las fórmulas, los datos que se guardan, los umbrales y la presentación en pantalla cuando las pruebas aporten evidencia nueva.

La regla principal es separar siempre:

- **lo que el teléfono mide**;
- **lo que la aplicación calcula a partir de esas medidas**;
- **lo que el modelo estima o muestra como probabilidad**.

Una estimación del modelo no debe presentarse como una coordenada exacta si los sensores utilizados no pueden medirla directamente.

## 2. Funcionamiento real actual de la aplicación

### 2.1 Versiones disponibles

P.R.S. conserva dos ramas de funcionamiento:

| Rama | Función actual |
|---|---|
| `v3.0` | Flujo anterior de P.R.S.: escaneo local, escaneo con PROBE, dispositivos, seguimiento individual y guía de operación. |
| `v4.0` | Nuevo flujo preparado sobre mapa, con selección de ubicación y objetivo en STEP 1, seguido de GRID sobre el mapa en STEP 2. |

En `v4.0` hay dos opciones:

- **ONLY PIP-BOY:** el A56 es el único receptor y fuente de observaciones BLE.
- **PIP-BOY + PROBE:** el A56 sigue funcionando igual y se añade el Watch 2 PROBE como baliza remota. Se conserva la comunicación y la telemetría actuales del PROBE.

El PROBE no se elimina de la reedición. Sus datos deben entrar en el modelo identificados como datos de un segundo nodo, no como si fueran una medición tomada por el A56.

### 2.2 Flujo actual de v4.0

```text
P.R.S. v4.0
    |
    +--> ONLY PIP-BOY
    |        |
    |        +--> STEP 1: ubicación + lista de dispositivos del A56
    |                 |
    |                 +--> seleccionar TARGET
    |                          |
    |                          +--> STEP 2: mapa + GRID
    |
    +--> PIP-BOY + PROBE
             |
             +--> STEP 1: ubicación + lista de dispositivos del A56/PROBE
                      |
                      +--> seleccionar TARGET
                               |
                               +--> STEP 2: mapa + GRID + estado del PROBE
```

### 2.3 STEP 1: ubicación y selección del TARGET

La pantalla se divide en dos paneles con estética `TERMINAL`:

- **Mitad izquierda:** selección de la ubicación, modo activo y navegación.
- **Mitad derecha:** lista de dispositivos detectados.

La lista de la derecha puede mostrar:

- nombre BLE anunciado, si existe;
- identificador técnico observado, normalmente la dirección BLE cuando Android la expone;
- RSSI RAW actual;
- origen de la observación: `A56` o `WATCH 2 PROBE`;
- categoría aproximada cuando el clasificador reconoce señales de teléfono, reloj, audio, TV u ordenador;
- estado de dispositivo guardado.

La selección del objetivo se mantiene bloqueada hasta elegir una ubicación. Así se evita abrir el GRID sin saber sobre qué mapa debe trabajar la sesión.

#### Dispositivos guardados

Sí, los dispositivos guardados pueden remarcarse en STEP 1.

En la implementación actual del nuevo STEP 1:

- un dispositivo guardado y seleccionable aparece resaltado en **ámbar**;
- muestra `SAVED DEVICE` y `SAVED DEVICE // TARGET READY`;
- sigue siendo seleccionable si la regla está desactivada;
- un dispositivo cuya regla está activada se omite del análisis y muestra el estado de omisión en lugar de quedar disponible como objetivo;
- los dispositivos no guardados mantienen el color normal verde de terminal.

La aplicación guarda una regla por **dirección BLE** o por **nombre BLE anunciado**. La dirección es la opción preferida cuando es estable. El nombre es una alternativa cuando el accesorio usa una dirección privada o rotatoria, pero puede coincidir con más de un dispositivo físico.

### 2.4 STEP 2: mapa y GRID

Después de elegir ubicación y TARGET, la aplicación abre el mapa offline correspondiente y dibuja el GRID encima.

El centro inicial del mapa utiliza la ubicación GPS del A56 cuando está disponible. El mapa puede seguir la ubicación del A56 durante la sesión.

En el estado actual, el GRID es una representación visual relativa de la evidencia disponible. Todavía no es un mapa estadístico completo del objetivo. En concreto, no debe interpretarse como:

- una distancia exacta en metros;
- un rumbo o bearing medido;
- una coordenada garantizada del dispositivo;
- una precisión GPS del TARGET.

La primera fase del nuevo modelo debe funcionar con un objetivo estático. Después se podrá estudiar el caso de objetivo móvil.

### 2.5 Datos que maneja hoy P.R.S.

La aplicación ya separa conceptualmente tres capas:

#### Datos medidos

- RSSI RAW de cada anuncio BLE.
- instante de observación.
- identificador técnico observado.
- nombre anunciado, si existe.
- parte de los datos del anuncio BLE cuando están disponibles.
- tipo o clase Bluetooth cuando Android la proporciona.
- origen de la observación: A56 o PROBE.
- ubicación y calidad de ubicación del nodo, cuando están disponibles.
- orientación y contexto de movimiento del A56, cuando están disponibles.

#### Datos procesados

- RSSI suavizado.
- RSSI medio.
- variación respecto a lecturas anteriores.
- histórico temporal.
- número de muestras.
- agrupación de observaciones por contacto.

#### Inferencias actuales

- tendencia: `APPROACHING`, `STABLE`, `MOVING AWAY` o espera por falta de datos;
- banda relativa: `NEAR`, `MEDIUM`, `FAR` o desconocida;
- nube radial de incertidumbre usada por el GRID actual;
- confianza de la nube actual.

La lógica actual no convierte RSSI directamente en metros. Esto es importante: el RSSI cambia por obstáculos, orientación, cuerpo humano, potencia del emisor, interferencias y muchas otras razones.

## 3. Modelo estadístico teórico adaptado a la aplicación real

### 3.1 Objetivo del modelo

El objetivo de v4.0 no es afirmar “el TARGET está exactamente en este punto”. El objetivo es producir una distribución de probabilidad sobre las celdas del GRID:

> “Con las observaciones recogidas hasta ahora, estas celdas son más compatibles con la evidencia que las demás.”

La salida debe poder cambiar gradualmente cuando llegan nuevas observaciones y debe poder expresar incertidumbre. Si la evidencia es mala o contradictoria, el modelo debe mostrar una probabilidad repartida y una confianza baja.

### 3.2 Unidad de cálculo: la celda del GRID

El mapa de la ubicación se divide en celdas. Para cada celda válida `c`, el modelo guarda un peso o probabilidad:

```text
P(c | datos observados)
```

La suma de todas las celdas válidas debe ser 1, salvo durante un instante interno de cálculo. Las zonas que no pertenecen al campo, son desconocidas o no tienen datos de mapa deben marcarse como inválidas y no recibir probabilidad útil.

La resolución del GRID debe ser configurable. No conviene fijarla definitivamente hasta medir:

- tamaño real de la pantalla;
- tamaño del mapa mostrado;
- precisión de ubicación del A56;
- estabilidad práctica de las observaciones BLE;
- tiempo disponible para recoger muestras.

### 3.3 Distribución inicial

Para una sesión nueva, la opción más segura es un **prior uniforme** dentro de las celdas válidas:

```text
P inicial(c) = 1 / número de celdas válidas
```

Más adelante se puede estudiar un prior informado por:

- zonas transitables;
- edificios o muros conocidos;
- el punto de inicio de la búsqueda;
- información previa de una misión.

No se debe introducir un prior especial solo porque una celda sea visualmente más cómoda o esté en el centro de la pantalla.

### 3.4 Observación y verosimilitud

Cada observación aporta una evidencia. Para una celda candidata `c`, se puede calcular una verosimilitud aproximada `L(c)`.

En lenguaje sencillo:

> Una celda recibe más peso si lo que se ha observado sería razonable suponiendo que el TARGET estuviera allí.

La primera versión puede combinar estas evidencias:

1. **Nivel relativo de RSSI.** Se compara con el historial de la misma sesión, no con una tabla universal de metros.
2. **Cambio temporal.** Se observa si el RSSI tiende a subir, bajar o permanecer estable mientras el A56 se mueve.
3. **Movimiento del A56.** Se relaciona la posición aproximada del receptor con el cambio de señal.
4. **Barrido por sectores.** Se registra desde qué sectores de observación aparece una señal relativamente mejor o más estable.
5. **Calidad de la muestra.** Se penalizan lecturas escasas, discontinuas o con mucha variación.
6. **Continuidad temporal.** Se da más valor a una pauta repetida que a un único pico de RSSI.

Una forma teórica de combinar las evidencias es:

```text
P(c | datos) ∝ P(c) ×
                L_rssi(c)^w_rssi ×
                L_temporal(c)^w_temporal ×
                L_movimiento(c)^w_movimiento ×
                L_sector(c)^w_sector ×
                L_calidad(c)^w_calidad
```

Los pesos `w_*` son parámetros de calibración. No deben escogerse solo “a ojo”: deben ajustarse con datos de las pruebas físicas.

Para evitar problemas numéricos, la implementación puede hacer la suma en logaritmos:

```text
log P(c) = log P_inicial(c) + Σ w_i × log L_i(c)
```

Después se normaliza el resultado para que la suma sea 1.

### 3.5 Tratamiento de PROBE

En `ONLY PIP-BOY`, el modelo solo usa evidencias del nodo A56.

En `PIP-BOY + PROBE`:

- el A56 y el Watch 2 PROBE son nodos de observación distintos;
- cada observación mantiene su origen, hora, calidad y posición conocida del nodo;
- el PROBE aporta una segunda perspectiva, no una coordenada inventada del TARGET;
- la evidencia del PROBE puede tener un peso propio `w_probe` que se calibre por separado;
- una lectura del PROBE no debe copiarse en el histórico del A56;
- si la conexión, la hora, la identidad o la posición del PROBE no son fiables, su evidencia debe reducirse o descartarse;
- si el PROBE pierde conexión, el modelo debe seguir funcionando con la evidencia válida del A56 y avisar de la pérdida de cobertura.

Una formulación inicial, todavía teórica, sería:

```text
P(c | datos) ∝ P(c) × L_A56(c)^w_A56 × L_PROBE(c)^w_probe
```

Cada `L_*` se puede descomponer a su vez en RSSI, tiempo, movimiento y calidad. No se debe tratar el PROBE como un medidor de bearing salvo que exista un sensor y una prueba específica que lo demuestren.

### 3.6 Confianza y convergencia

La pantalla debe enseñar dos conceptos diferentes:

- **probabilidad:** cómo se reparte el peso entre las celdas;
- **confianza:** cuánto nos fiamos de esa distribución.

La confianza puede aumentar cuando:

- hay suficientes muestras;
- las muestras son recientes y continuas;
- el RSSI es relativamente estable o su cambio se explica por el movimiento;
- las observaciones de A56 y PROBE son compatibles;
- la ubicación del nodo tiene buena calidad;
- el resultado se repite en más de un recorrido.

La confianza debe disminuir cuando:

- hay pocos datos;
- el RSSI oscila mucho sin patrón;
- el GPS tiene mala precisión o es antiguo;
- el PROBE se desconecta;
- las dos fuentes ofrecen evidencias incompatibles;
- varias celdas siguen siendo igual de probables.

Una distribución muy concentrada no debe producir confianza alta automáticamente. El modelo debe comprobar también la calidad y la cantidad de evidencia.

### 3.7 Salidas visibles en la interfaz

La primera implementación debería mostrar como mínimo:

- GRID con intensidad por celda;
- celda o zona de mayor probabilidad, si existe una diferencia suficiente;
- confianza global;
- número de muestras y antigüedad de la última muestra;
- fuente o fuentes activas: A56, PROBE o ambas;
- estado de GPS y calidad de la posición del nodo;
- advertencia cuando la evidencia sea insuficiente o contradictoria.

El texto debe evitar afirmaciones absolutas como `TARGET CONFIRMED` mientras el modelo no tenga una validación física que lo justifique. Son preferibles estados como `LOW CONFIDENCE`, `DISTRIBUTION STABLE` o `MORE DATA REQUIRED`.

## 4. Lo que queda fuera de la primera versión

Para mantener el alcance controlado, la primera versión del modelo no debe prometer:

- conversión universal de RSSI a distancia;
- dirección o bearing con un solo receptor BLE;
- coordenada exacta del TARGET;
- identificación infalible por nombre BLE;
- seguimiento de un objetivo móvil sin una fase previa de calibración;
- precisión superior a la del GPS del nodo;
- resultados válidos para todos los edificios, accesorios y entornos sin recalibración.

El seguimiento de objetivos móviles puede ser una fase posterior. Primero necesitamos demostrar que el modelo describe correctamente un objetivo estático en un entorno controlado.

## 5. Preparación de las pruebas físicas

### 5.1 Material recomendado

Para `ONLY PIP-BOY`:

- A56 con la versión de prueba instalada.
- Un dispositivo BLE que actúe como TARGET.
- Un segundo dispositivo BLE opcional para comprobar que la lista contiene varios contactos.
- Cinta métrica o distanciómetro para conocer la distancia real durante la prueba.
- Conos, cinta o marcas para señalar posiciones y sectores.
- Hoja de registro o plantilla de este documento.

Para `PIP-BOY + PROBE`, añadir:

- Watch 2 PROBE cargado.
- Conexión y emparejamiento comprobados.
- Una posición fija y conocida para el PROBE.
- Registro de batería, conexión y hora de cada prueba.

La cinta métrica sirve como **verdad de campo para evaluar el resultado**, no como dato que se deba introducir en el modelo durante la búsqueda.

### 5.2 Condiciones que deben anotarse

Antes de cada sesión registrar:

- fecha y hora;
- versión instalada;
- modo usado;
- ubicación o campo seleccionado;
- interior o exterior;
- obstáculos relevantes;
- número de personas presentes;
- orientación del A56;
- orientación del TARGET;
- orientación y posición del PROBE, si se usa;
- nivel aproximado de batería;
- si había otros dispositivos BLE cercanos;
- precisión GPS mostrada por el A56;
- resultado esperado y posición real del TARGET, guardada aparte.

## 6. Plan de pruebas por fases

Las primeras pruebas deben ser pequeñas y repetibles. No conviene cambiar la fórmula y la disposición física al mismo tiempo.

### Fase 0 — Arranque y registro

**Objetivo:** comprobar que una sesión puede comenzar y que todos los datos necesarios quedan visibles o registrables.

**Pasos:**

1. Abrir P.R.S. v4.0.
2. Ejecutar `ONLY PIP-BOY`.
3. Comprobar que STEP 1 muestra ubicación a la izquierda y dispositivos a la derecha.
4. Seleccionar una ubicación.
5. Seleccionar el TARGET.
6. Confirmar que se abre STEP 2 con el GRID sobre el mapa.
7. Repetir con `PIP-BOY + PROBE`.

**Registrar:** tiempos de carga, mensajes, errores, estado de Bluetooth, estado de GPS y estado de PROBE.

**Criterio de paso:** no hay cierre inesperado, la selección es clara y se puede volver atrás sin perder el control de la sesión.

### Fase 1 — Identidad y dispositivos guardados

**Objetivo:** asegurarse de que se elige el dispositivo correcto antes de evaluar el modelo.

**Casos:**

1. TARGET con dirección BLE visible y nombre claro.
2. TARGET sin nombre BLE útil.
3. Dos dispositivos con nombres parecidos.
4. Dispositivo guardado por dirección.
5. Dispositivo guardado por nombre.
6. Regla guardada desactivada.
7. Regla guardada activada.
8. Dispositivo con dirección privada o rotatoria.

**Criterio de paso:** el dispositivo guardado se reconoce visualmente en ámbar con `SAVED DEVICE`; si está desactivado se puede seleccionar; si está activado se omite y la interfaz explica por qué.

### Fase 2 — Línea base de RSSI con TARGET estático

**Objetivo:** medir la variabilidad natural del RSSI antes de usarlo en un modelo espacial.

**Montaje:** colocar el TARGET en posiciones conocidas de aproximadamente 0,5 m, 1 m, 2 m, 5 m y 10 m, si el espacio lo permite.

**Procedimiento:**

1. Mantener el A56 quieto durante 60 segundos en cada posición.
2. Repetir cada posición al menos tres veces.
3. Repetir con el A56 en orientación normal y girado.
4. Repetir con el TARGET en dos orientaciones.
5. No cambiar de fórmula ni mover obstáculos entre repeticiones de una misma serie.

**Registrar:** RSSI RAW, RSSI suavizado, número de muestras, variación, pérdidas de anuncios, orientación y distancia real.

**Qué buscamos:** no buscamos una tabla universal “RSSI = metros”. Buscamos conocer la dispersión y comprobar si el historial de una sesión es suficientemente estable para utilizar cambios relativos.

### Fase 3 — Movimiento del A56 alrededor de un TARGET estático

**Objetivo:** comprobar si el movimiento del receptor aporta información útil.

**Recorridos mínimos:**

- acercamiento en línea recta;
- alejamiento en línea recta;
- recorrido lateral izquierdo-derecho;
- recorrido lateral derecho-izquierdo;
- recorrido diagonal;
- vuelta parcial alrededor del TARGET.

Repetir cada recorrido tres veces, empezando desde una posición distinta cuando sea posible.

**Registrar:** trayectoria aproximada del A56, tiempo, RSSI, tendencia que muestra la aplicación y posición real del TARGET.

**Criterio de paso:** el modelo no debe reaccionar a una sola lectura. Una tendencia debe aparecer solo después de varias observaciones coherentes.

### Fase 4 — Ubicación, mapa y GRID

**Objetivo:** comprobar que la capa estadística se dibuja sobre el mapa correcto y que la incertidumbre del GPS no se confunde con la del BLE.

**Casos:**

- GPS con buena precisión;
- GPS con precisión baja;
- GPS esperando ubicación;
- A56 quieto;
- A56 caminando;
- salida y regreso a la aplicación;
- cambio de ubicación antes de elegir TARGET.

**Registrar:** precisión GPS, centro del mapa, posición del A56, tamaño de las celdas, celdas válidas, centro de la distribución y confianza.

**Criterio de paso:** el GRID no presenta una precisión mayor que la evidencia disponible y diferencia claramente `GPS`, `BLE` y `MODELO`.

### Fase 5 — PROBE como segundo nodo

**Objetivo:** comprobar que el PROBE continúa funcionando como baliza remota y que sus datos se integran sin falsificar el origen.

**Casos:**

1. PROBE conectado y con batería suficiente.
2. PROBE conectado pero sin posición válida.
3. Pérdida temporal de conexión.
4. Reconexión durante una sesión.
5. A56 y PROBE observando el mismo TARGET.
6. A56 observando el TARGET y PROBE fuera de cobertura.
7. PROBE en una posición fija conocida y separada del A56.

**Registrar:** estado de enlace, batería, hora de cada telemetría, posición y precisión del PROBE, número de observaciones por fuente, retraso aproximado y resultado del GRID.

**Criterio de paso:** las observaciones del PROBE aparecen como `WATCH 2 PROBE`, no se mezclan silenciosamente con las del A56 y la pérdida del PROBE no bloquea una sesión que aún tenga datos válidos del A56.

### Fase 6 — Barrido por sectores

**Objetivo:** comprobar si un procedimiento repetible de orientación y recorrido aporta una diferencia estadística útil.

Dividir el espacio de observación en sectores, por ejemplo:

```text
FAR LEFT | LEFT | CENTER-LEFT | CENTER | CENTER-RIGHT | RIGHT | FAR RIGHT
```

Para cada sector:

1. colocar el A56 en la marca;
2. mantenerlo quieto durante el mismo tiempo;
3. registrar la mediana y la dispersión del RSSI;
4. repetir al menos diez veces;
5. variar el orden de los sectores en una segunda serie.

**Criterio de paso:** una diferencia solo se considera útil si se repite al cambiar el orden. Si desaparece al repetir, se trata como ruido o efecto del procedimiento.

### Fase 7 — Prueba completa con TARGET estático oculto

**Objetivo:** evaluar el flujo completo sin permitir que el operador sepa la respuesta mientras utiliza el GRID.

**Procedimiento:**

1. Una persona coloca el TARGET en una posición conocida.
2. El operador no ve esa posición.
3. El operador selecciona la ubicación y el TARGET en STEP 1.
4. Se realizan tres recorridos predefinidos.
5. Se guardan capturas o lecturas del GRID en tiempos fijos: inicio, 30 s, 60 s, 120 s y final.
6. Se anota la celda de máxima probabilidad y la confianza.
7. Al terminar se compara con la posición real.

Repetir con al menos tres posiciones del campo, dos orientaciones del TARGET y, si procede, los dos modos de v4.0.

**Criterio de paso inicial:** el resultado debe mejorar respecto a una distribución uniforme o explicar con una confianza baja por qué no puede localizar mejor. No se debe contar como éxito una coincidencia aislada.

### Fase 8 — Robustez en condiciones reales

Repetir una selección de las pruebas con:

- una pared u obstáculo;
- el cuerpo del operador entre A56 y TARGET;
- varias personas moviéndose;
- otros dispositivos BLE cercanos;
- batería baja del TARGET;
- cambios de orientación;
- distancia entre A56 y PROBE variable;
- interrupción y reanudación del escaneo.

El objetivo no es que el resultado nunca empeore. El objetivo es que el sistema reduzca la confianza o indique incertidumbre en vez de inventar seguridad.

### Fase 9 — Repetibilidad

**Objetivo:** comprobar que el resultado no depende únicamente de una persona o de una sesión concreta.

Repetir el mismo protocolo:

- otro día;
- con otro operador;
- con el TARGET recargado;
- con distinta orientación inicial;
- si es posible, en otra zona del mismo tipo.

Comparar distribución, confianza, tasa de pérdida del objetivo y falsos máximos.

## 7. Plantilla de registro de una prueba

Copiar este bloque para cada ensayo:

```text
ID DE PRUEBA:
FECHA / HORA:
VERSIÓN DE LA APP:
MODO: ONLY PIP-BOY / PIP-BOY + PROBE
OPERADOR:
UBICACIÓN / MAPA:
TARGET REAL:
TARGET GUARDADO POR: DIRECCIÓN / NOMBRE / NO GUARDADO
POSICIÓN REAL (SOLO PARA COMPARAR DESPUÉS):

INTERIOR / EXTERIOR:
OBSTÁCULOS:
PERSONAS CERCANAS:
ORIENTACIÓN DEL A56:
ORIENTACIÓN DEL TARGET:
POSICIÓN DEL PROBE:
BATERÍA A56:
BATERÍA TARGET:
BATERÍA PROBE:

GPS A56: ACTIVO / ESPERANDO / PRECISIÓN BAJA
GPS PROBE: ACTIVO / ESPERANDO / PRECISIÓN BAJA / NO USADO
ENLACE PROBE: ACTIVO / PERDIDO / RECONEXIÓN / NO USADO

RECORRIDO REALIZADO:
DURACIÓN:
MUESTRAS A56:
MUESTRAS PROBE:
RSSI MÍNIMO / MEDIO / MÁXIMO:
VARIACIÓN OBSERVADA:
CELDA O ZONA DE MÁXIMA PROBABILIDAD:
CONFIANZA INICIAL:
CONFIANZA FINAL:
RESULTADO VISUAL:
FALLOS O INTERRUPCIONES:
OBSERVACIONES:
```

La posición real del TARGET debe mantenerse fuera de la vista del operador durante la prueba completa. Solo se usa al final para evaluar el resultado.

## 8. Métricas que debemos calcular

Al terminar una serie de pruebas conviene calcular, como mínimo:

- porcentaje de anuncios recibidos;
- número de muestras por minuto;
- mediana y dispersión del RSSI;
- tiempo hasta que aparece el TARGET;
- tiempo hasta que el GRID cambia de forma visible;
- confianza media y final;
- distancia entre la celda máxima y la posición real, solo para evaluación;
- porcentaje de pruebas en que la posición real queda dentro de una zona de probabilidad alta;
- número de falsos máximos;
- número de sesiones con pérdida de TARGET;
- diferencia entre modo A56 solo y modo A56 + PROBE;
- número de veces que la aplicación expresa incertidumbre correctamente.

No fijar todavía un único porcentaje de éxito como requisito definitivo. Primero necesitamos una línea base real. Después podemos definir objetivos, por ejemplo, por entorno interior y exterior por separado.

## 9. Pruebas de software que acompañan a las pruebas físicas

Antes de interpretar resultados físicos, la aplicación debe superar estas comprobaciones:

- una sesión nueva empieza con una distribución válida;
- la suma de probabilidades se normaliza correctamente;
- no aparecen `NaN`, infinitos o celdas con valores imposibles;
- una observación repetida produce el mismo resultado en una reproducción determinista;
- una observación del A56 no aparece como PROBE;
- una observación del PROBE no aparece como A56;
- una desconexión del PROBE reduce cobertura, pero no corrompe la sesión;
- cambiar de TARGET reinicia el estado que pertenece al objetivo anterior;
- cambiar de ubicación no conserva accidentalmente la distribución anterior;
- las reglas de dispositivos guardados se aplican de forma consistente;
- un dispositivo guardado desactivado puede seleccionarse en STEP 1;
- un dispositivo guardado activado se omite y lo explica en pantalla;
- el GRID permanece legible cuando hay pocos datos;
- el sistema no muestra confianza alta con una sola lectura aislada.

## 10. Guía de usuario: cómo identificar un dispositivo

### Método recomendado: identificarlo desde la lista en directo

1. Enciende el TARGET y activa su Bluetooth.
2. Acércalo al A56 durante la identificación.
3. Abre `TOOLS` → `PROXIMITY RADIO SCANNER` → `v3.0` → `DEVICES` → `IDENTIFY DEVICE`.
4. Espera a que aparezca en la lista.
5. Comprueba el nombre, el identificador, el RSSI y la categoría mostrada.
6. Pulsa `SAVE DEVICE` en la fila correcta.
7. Para usarlo en v4.0, abre la opción deseada y selecciona la ubicación antes del TARGET.
8. En STEP 1, el dispositivo guardado se verá en ámbar como `SAVED DEVICE`.

Este método es el más fácil porque permite comparar varios datos a la vez.

### Método por dirección BLE

La dirección BLE es un identificador con un formato parecido a:

```text
AA:BB:CC:DD:EE:FF
```

Es la mejor opción cuando permanece estable. Se puede guardar desde la fila observada o introducirla manualmente en el flujo de dispositivos. También se aceptan guiones en la entrada manual.

Ventaja: normalmente distingue mejor una unidad concreta.
Limitación: algunos dispositivos usan direcciones privadas o rotatorias, que pueden cambiar.

### Método por nombre BLE anunciado

Si Android no ofrece una dirección estable, se puede guardar el nombre BLE exacto, por ejemplo el nombre que aparece en la fila de identificación.

Ventaja: funciona como alternativa para direcciones rotatorias.
Limitación: dos dispositivos del mismo modelo pueden anunciar el mismo nombre. En ese caso el nombre no identifica de forma segura una unidad concreta.

### Usar RSSI como ayuda, no como identidad

RSSI indica la intensidad de la señal que recibe el A56 o el PROBE en ese momento. Puede ayudar a comprobar que el dispositivo está cerca, pero no identifica por sí solo un dispositivo.

El RSSI cambia por:

- distancia;
- orientación;
- paredes y muebles;
- cuerpo humano;
- interferencias;
- batería y potencia del accesorio;
- posición del A56 o del PROBE.

No guardes un dispositivo solo porque tenga el RSSI más alto. Confirma primero el nombre y el identificador.

### Usar la categoría del dispositivo

La aplicación puede añadir una categoría aproximada, como teléfono, reloj, audio, TV u ordenador, cuando los datos BLE lo permiten.

La categoría sirve para filtrar visualmente la lista. No sustituye al identificador y puede quedar sin clasificar o ser incorrecta.

### Guardar, desactivar y eliminar

- **Guardar:** crea una regla persistente para reconocer el dispositivo.
- **Desactivar:** conserva la regla, pero permite que el dispositivo aparezca en el análisis y pueda seleccionarse.
- **Activar:** vuelve a aplicar la regla de omisión para ese dispositivo.
- **Eliminar:** borra la regla guardada.

Si un dispositivo guardado no aparece como objetivo, revisa `DEVICES` → `SAVED DEVICES` y desactiva temporalmente la regla. Una regla activada está diseñada para omitir el contacto, no para resaltarlo como objetivo.

### Lista rápida para identificar sin equivocarse

Antes de seleccionar un TARGET, comprueba:

```text
[ ] El dispositivo correcto está encendido.
[ ] Solo hay un dispositivo con ese nombre cerca, si es posible.
[ ] El nombre anunciado coincide.
[ ] El identificador BLE coincide o es estable.
[ ] El origen es el esperado: A56 o WATCH 2 PROBE.
[ ] El RSSI es razonable, pero no se usa como única prueba.
[ ] La fila está guardada o marcada como corresponde.
[ ] La regla no está activada por error.
```

## 11. Decisiones pendientes para esta semana

Marcar cada punto cuando haya evidencia suficiente:

- [ ] Definir resolución inicial de las celdas del GRID.
- [ ] Definir qué zonas del mapa son válidas.
- [ ] Definir el sistema de coordenadas local del mapa.
- [ ] Definir cómo se transforma la posición del PROBE al mismo marco del mapa.
- [ ] Definir el tamaño mínimo de muestra para calcular una tendencia.
- [ ] Definir los pesos iniciales del A56 y del PROBE.
- [ ] Definir cómo se penaliza una señal con mucha variación.
- [ ] Definir los límites de confianza baja, media y alta.
- [ ] Definir cuánto tiempo puede tener un dato antes de considerarse antiguo.
- [ ] Definir si el objetivo móvil queda fuera de v4.0 inicial.
- [ ] Definir el formato de exportación de una sesión para poder repetirla.
- [ ] Definir el criterio de éxito para interior y exterior por separado.

## 12. Registro de cambios del documento

| Fecha | Cambio | Motivo o evidencia |
|---|---|---|
| 2026-09-02 | Documento inicial. Se incorporan los modos A56 solo y A56 + PROBE, STEP 1 dividido y resaltado de dispositivos guardados. | Inicio del sprint y estado real de la aplicación. |
|  |  |  |
|  |  |  |
|  |  |  |

## 13. Resumen breve para compartir con otro chatbot

P.R.S. v4.0 debe estimar una distribución de probabilidad sobre un GRID dibujado en un mapa offline. El A56 observa anuncios BLE y conserva RSSI RAW, histórico, RSSI suavizado, tendencia, banda relativa y calidad temporal. La aplicación no debe convertir RSSI directamente en metros ni presentar bearing o coordenadas exactas sin una medición que lo permita.

Hay dos modos. En `ONLY PIP-BOY`, el A56 es el único nodo. En `PIP-BOY + PROBE`, el A56 y el Watch 2 PROBE son nodos separados: el PROBE conserva su lógica de comunicación y telemetría, sus observaciones mantienen el origen `WATCH 2 PROBE` y solo se fusionan si identidad, tiempo, posición y calidad son válidos. La distribución puede expresarse como un prior por celda multiplicado por verosimilitudes de RSSI relativo, evolución temporal, movimiento del nodo, barrido por sectores y calidad de datos. La confianza debe reflejar cantidad, continuidad y coherencia de la evidencia.

El desarrollo debe empezar con un TARGET estático. Antes de ajustar pesos hay que realizar pruebas de identidad, línea base de RSSI, recorridos del A56, mapa/GPS, PROBE conectado y desconectado, barrido por sectores, prueba ciega con TARGET oculto, condiciones con obstáculos y repetición en otro día u operador. La posición real se usa solo para evaluar el resultado al final. El sistema debe expresar incertidumbre cuando los datos no sean suficientes.
