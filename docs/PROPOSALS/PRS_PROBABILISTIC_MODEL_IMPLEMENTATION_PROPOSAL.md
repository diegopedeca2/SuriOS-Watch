# P.R.S. — Propuesta de implementación del modelo probabilístico

## Ficha del documento

- **Proyecto:** PIP-SuriOS, dentro de SuriOS Ecosystem.
- **Componente:** Proximity Radio Scanner (P.R.S.).
- **Punto de partida:** P.R.S. v3.0 real descrito en PRS_TECHNICAL_SUMMARY_CURRENT.md.
- **Destino:** evolución progresiva de P.R.S. v3.0 hacia un modelo probabilístico espacial.
- **Terminal operativo:** Samsung Galaxy A56.
- **Objetivo inicial:** seguimiento de un TARGET BLE conocido, seleccionado por el usuario, con TARGET estático.
- **Estado:** diseño técnico provisional; los parámetros deben calibrarse con datos físicos del A56 y del emisor TARGET.
- **No es:** una especificación de bearing BLE, un sistema de ranging, un localizador métrico ni una reconstrucción de P.R.S. v2.0.

---

## 1. Resumen ejecutivo

P.R.S. debe evolucionar desde una cadena basada principalmente en RSSI,
suavizado, tendencia y bandas relativas hacia un sistema que mantenga una
**distribución de probabilidad sobre un GRID 2D**.

El sistema no intentará calcular una coordenada exacta del TARGET. Mantendrá una
nube de densidad que se actualiza cuando el A56:

1. recibe anuncios BLE del TARGET conocido;
2. registra el RSSI RAW y el momento exacto de la recepción;
3. conoce su propia posición, desplazamiento y orientación con la calidad disponible;
4. procesa una ventana temporal de observaciones;
5. compara cada hipótesis de celda con likelihoods calibradas;
6. superpone de forma iterativa las evidencias mediante un mecanismo tipo Venn+;
7. actualiza la probabilidad por celda y calcula la confianza de la inferencia.

La arquitectura propuesta queda centrada exclusivamente en el A56:

~~~text
A56 BLE SCAN
    -> RAW SESSION
    -> IGNORE RULES / TARGET MATCH
    -> TARGET TRACKER
    -> RSSI + TEMPORAL FEATURES

A56 LOCATION + MOTION + ORIENTATION
    -> MOTION CONTEXT
    -> LOCAL 2D FRAME

TARGET TRACK + MOTION CONTEXT + CALIBRATION PROFILE
    -> CELL LIKELIHOODS
    -> VENN+ ITERATIVE FUSION
    -> BAYESIAN / WEIGHTED-LIKELIHOOD GRID
    -> DENSITY CLOUD + CONFIDENCE + CONVERGENCE
~~~

La nueva versión elimina del flujo de P.R.S.:

- PROBE;
- SCAN + PROBE;
- telemetría BLE recibida desde el Watch 2;
- subgrid de receptores;
- cualquier posición sintética procedente de la arquitectura histórica de v2.0;
- cualquier representación visual que parezca un bearing BLE o una coordenada medida.

Se conservan y se reutilizan:

- adquisición BLE del A56;
- identificación y selección de contactos;
- DEVICES;
- reglas persistentes de exclusión;
- tracking individual que siga siendo útil;
- historial temporal y suavizado como base de transición;
- PrsMovementContext como punto de extensión, promovido a contexto real del A56;
- GRID y renderer, modificándolos para representar una matriz de probabilidad real.

---

## 2. Decisiones de alcance y reglas no negociables

### 2.1 TARGET BLE conocido y seleccionable

P.R.S. no debe ejecutar el modelo espacial sobre todos los contactos al mismo
tiempo. La operación probabilística comienza cuando el usuario selecciona un
TARGET BLE conocido.

El TARGET puede proceder de:

- un dispositivo identificado y guardado desde DEVICES;
- un contacto observado en la sesión actual y confirmado explícitamente por el
  usuario;
- un perfil de TARGET previamente preparado para la partida o la calibración.

El nombre BLE es un dato de presentación y un fallback limitado. La identidad
preferida será, en este orden:

1. identificador técnico estable cuando Android lo proporcione;
2. fingerprint de advertising, por ejemplo fabricante, service UUID y datos
   relevantes;
3. combinación de campos de advertising y nombre;
4. nombre únicamente, solo con advertencia de posible ambigüedad.

Una regla DEVICES activa no puede ocultar accidentalmente el TARGET
seleccionado. La interfaz debe impedir esa contradicción o pedir al usuario que
desactive la regla antes de iniciar el seguimiento.

Los contactos que no sean TARGET pueden seguir apareciendo en un modo
diagnóstico, pero no alimentarán el posterior probabilístico del objetivo.

### 2.2 A56 como único nodo de adquisición y referencia

El A56 es el único receptor P.R.S. de la nueva arquitectura. Su ubicación,
orientación y desplazamiento sirven para comparar mediciones tomadas desde
puntos y posturas diferentes.

Esto no significa que el A56 mida la dirección de procedencia del BLE. El
teléfono aporta contexto del movimiento del receptor, no un bearing del emisor.

### 2.3 Separación obligatoria de datos

Cada dato debe pertenecer a una de estas capas:

| Capa | Ejemplos | ¿Qué significa? |
|---|---|---|
| **Medido** | RSSI RAW, timestamp monotónico, bytes de advertising, posición reportada, aceleración, orientación estimada por sensores | Dato recibido o calculado directamente a partir de una API de hardware/sistema, conservando su origen y calidad. |
| **Procesado** | RSSI mediano, EMA, pendiente, varianza/MAD, desplazamiento local, velocidad estimada, sectores temporales del barrido | Transformación reproducible de datos medidos. |
| **Inferido** | likelihood por celda, probabilidad del GRID, sector favorecido, APPROACHING, CONFIDENCE, CONVERGING | Resultado del modelo. Nunca debe presentarse como medición física del TARGET. |

La interfaz y los logs deben conservar esta clasificación. No se debe convertir
una inferencia en un campo llamado distance, bearing, targetX o targetY si el
valor no ha sido medido por un sistema que realmente lo proporcione.

### 2.4 RSSI no se convierte en metros

Las distancias conocidas durante la calibración física se utilizarán como
etiquetas de laboratorio para aprender distribuciones y bandas de compatibilidad.
Durante la operación:

- no se mostrará una distancia estimada;
- no se convertirá un RSSI en metros;
- no se afirmará que una celda está a una distancia real del TARGET;
- se dirá únicamente que determinadas celdas son más o menos compatibles con la
  evidencia observada.

### 2.5 No se afirma bearing BLE

El barrido izquierda-centro-derecha produce una firma estadística de sectores
relativos al movimiento y a la orientación del A56. No proporciona un ángulo de
llegada BLE.

Los sectores FAR LEFT, LEFT, FRONT, RIGHT y FAR RIGHT son hipótesis operativas
clasificadas a partir de patrones observados. No son una medición directa de la
dirección física del TARGET.

### 2.6 TARGET estático primero

La primera implementación debe suponer:

~~~text
TARGET_MODE = STATIC
motion_process_noise = 0
no hay expansión temporal de la nube por movimiento del TARGET
~~~

La arquitectura debe admitir posteriormente SLOW_MOVING y FAST_MOVING, pero no
se debe introducir complejidad móvil antes de demostrar que el modelo estático
converge correctamente en pruebas controladas.

---

## 3. Funcionamiento operativo propuesto

### 3.1 Flujo de una sesión

1. El usuario abre P.R.S. en modo A56.
2. El sistema valida Bluetooth y permisos requeridos.
3. BleScanner comienza a capturar anuncios BLE del A56.
4. El usuario identifica o selecciona un TARGET conocido.
5. El sistema comprueba identidad, reglas de DEVICES y perfil de calibración.
6. Se crea un PrsSessionManifest con versión de esquema, modelo y parámetros.
7. Cada callback válido se conserva como RAW antes del suavizado.
8. PrsContactTracker mantiene la historia del TARGET y de los contactos
   auxiliares que sigan siendo relevantes para diagnóstico.
9. El proveedor de movimiento del A56 registra posición, desplazamiento,
   orientación y calidad.
10. Cada ciclo de evaluación construye una ventana temporal.
11. El motor calcula las evidencias de RSSI, movimiento y barrido.
12. Las evidencias se fusionan por celdas sobre el GRID.
13. La pantalla muestra la nube de densidad, su evolución, CONFIDENCE y el estado
    de convergencia.
14. La sesión puede detenerse y reproducirse posteriormente sin el A56.

### 3.2 Maniobra operativa recomendada

La maniobra estándar para obtener datos comparables es:

1. mantener un rumbo;
2. detenerse o reducir el movimiento durante el barrido;
3. girar aproximadamente 90 grados a la izquierda;
4. barrer lentamente hasta aproximadamente 90 grados a la derecha;
5. volver al rumbo;
6. registrar la firma izquierda-centro-derecha;
7. avanzar una distancia operativa definida por el campo;
8. repetir.

El sistema puede recomendar RESCAN, MOVE FOR SAMPLE o SIGNAL AMBIGUOUS, pero no
debe ordenar una dirección física del TARGET como si hubiera sido medida.

---

## 4. Modelo de datos propuesto

Los nombres siguientes son conceptuales y pueden adaptarse a las convenciones
Kotlin del repositorio.

### 4.1 Muestra BLE RAW

PrsRawBleSample debe conservar, como mínimo:

| Campo | Capa | Fuente / uso |
|---|---|---|
| sessionId | Medido/contexto | Sesión P.R.S. |
| sequence | Procesado de captura | Orden local de llegada, sin sustituir al timestamp. |
| source | Medido | Siempre A56 en la nueva arquitectura. |
| technicalId | Medido | Identificador observado, cuando exista. |
| advertisedName | Medido | Nombre recibido, si existe. |
| advertisingHex | Medido | Payload conservado para replay y diagnóstico. |
| rssiRawDbm | Medido | RSSI exacto del callback. |
| elapsedRealtimeNanos o equivalente | Medido | Tiempo monotónico para ordenar muestras. |
| wallClockMillis | Medido | Correlación humana y exportación. |
| androidDeviceType | Medido | Dato entregado por Android, si existe. |
| bluetoothClass | Medido | Dato entregado por Android, si existe. |
| txPower | Medido | Solo si el anuncio lo aporta; nunca obligatorio. |
| primaryPhy / secondaryPhy | Medido | Solo si Android lo aporta. |
| targetMatchAtCapture | Procesado | Resultado versionado del matching en ese momento. |

La muestra RAW es inmutable. El suavizado, la clasificación y el modelo no
deben sobrescribirla.

### 4.2 Contexto del A56

PrsMotionSample debe distinguir datos de sensores de datos derivados:

| Campo | Capa | Regla |
|---|---|---|
| locationLat, locationLon, accuracy, provider, timestamp | Medido | Posición reportada al A56; puede faltar o ser de baja calidad. |
| orientationQuaternion o representación equivalente | Medido/derivado del sensor | Orientación estimada por el sistema de sensores. |
| accelerometer, gyroscope, magnetometer | Medido | Solo cuando estén disponibles y se haya decidido capturarlos. |
| localPositionX, localPositionY | Procesado | Transformación a un marco local de sesión. |
| displacementX, displacementY | Procesado | Diferencia entre posiciones válidas del A56. |
| speedEstimate | Procesado | Derivada de posición y tiempo, con calidad explícita. |
| heading | Procesado | Orientación operativa estimada del A56; no es bearing BLE. |
| motionQuality | Procesado | Confianza en la calidad de la trayectoria, no en el TARGET. |

Si la posición del A56 no es suficientemente precisa, el motor debe degradar o
desactivar el término de movimiento, no rellenarlo con una posición inventada.

### 4.3 Ventana temporal procesada

PrsObservationWindow puede contener:

- rawCount;
- RSSI mediano;
- RSSI EMA heredado de la transición;
- media y rango intercuartílico;
- MAD o desviación estándar robusta;
- pendiente temporal;
- variación respecto a la ventana anterior;
- edad de la última muestra;
- densidad de anuncios por segundo;
- huecos de recepción;
- posición y orientación del A56 asociadas al intervalo;
- calidad de movimiento;
- estado de barrido, si se completó.

La ventana no sustituye a RAW. Es una vista reproducible de RAW.

### 4.4 Estado probabilístico

PrsGridPosterior debe contener:

- definición de la malla y del marco local;
- probabilidad normalizada de cada celda;
- timestamp de actualización;
- contribuciones de cada evidencia;
- confianza de la inferencia;
- entropía y métricas de concentración;
- estado de convergencia;
- versión del modelo;
- identificador de perfil TARGET;
- parámetros usados en la actualización.

No debe contener un supuesto targetCoordinate salvo que sea un valor externo de
ground truth utilizado exclusivamente en pruebas, nunca en la sesión operativa.

---

## 5. GRID 2D y marco espacial

### 5.1 Marco local de la sesión

El GRID se expresará en un marco local 2D de la sesión:

- el origen representa la posición inicial o el centro del área de búsqueda;
- los ejes se fijan al iniciar la sesión según el marco elegido;
- la posición del A56 se transforma a ese marco;
- la orientación del A56 permite expresar candidatos en sectores relativos.

El marco local representa la geometría conocida del recorrido del receptor. No es
una medición de la posición del TARGET.

La transformación puede usar la posición proporcionada por TerrainLocation, si
su precisión y antigüedad cumplen los umbrales configurados. Si no hay una
trayectoria válida, el sistema debe mostrar MOTION DATA INSUFFICIENT y mantener
solo las evidencias que todavía sean válidas.

### 5.2 Inicialización

Para una búsqueda sin información previa:

$$
p_0(c) = \frac{1}{N}
$$

donde N es el número de celdas válidas.

Si el operador define una zona de búsqueda válida, las celdas fuera de esa zona
se excluyen del soporte. Esto es una restricción de operación, no una detección
del TARGET.

El GRID visual actual de 6 x 4 puede conservarse como representación compacta,
pero el modelo debe disponer de una malla interna independiente. Una malla
provisional de 24 x 24 o 32 x 32 celdas permite evitar que la resolución visual
limite la inferencia. El valor debe ser configurable y medirse su coste en el
A56.

### 5.3 Nube de densidad

La nube visual debe ser una proyección directa del posterior:

- opacidad proporcional a la probabilidad relativa;
- color o gradiente para distinguir baja, media y alta densidad;
- contorno de región que acumula una fracción configurable de probabilidad;
- marca del A56 como posición del receptor;
- indicador de última actualización y antigüedad de la evidencia.

No debe dibujar:

- una flecha hacia el TARGET;
- una chincheta de posición inferida;
- un anillo interpretado como distancia métrica;
- una nube centrada en una posición sintética;
- el subgrid del Watch 2.

---

## 6. Modelo matemático teórico provisional

El modelo es deliberadamente un punto de partida. Sus likelihoods deben
aprenderse o ajustarse con pruebas del TARGET y del A56; no deben tratarse como
leyes físicas universales.

### 6.1 Estado y observación

Sea G = {c_1, ..., c_N} el conjunto de celdas del GRID y x_c la posición
geométrica de una celda en el marco local.

El estado del sistema en el tiempo t es:

$$
\mathbf{p}_t = \{p_t(c)\}_{c \in G}, \qquad
\sum_{c \in G} p_t(c) = 1
$$

Una ventana observada se representa como:

$$
z_t =
\left(
r_t,\,
\Delta r_t,\,
\dot r_t,\,
\sigma_t,\,
n_t,\,
\mathbf{x}^{A56}_t,\,
\Delta \mathbf{x}^{A56}_t,\,
\theta_t,\,
s_t
\right)
$$

donde:

- r_t es la estadística robusta de RSSI;
- Δr_t es el cambio respecto a una ventana anterior;
- ṙ_t es la pendiente temporal;
- σ_t o MAD representa variabilidad;
- n_t es el número de anuncios;
- x_A56 es la posición del receptor;
- Δx_A56 es su desplazamiento;
- θ_t es su orientación estimada;
- s_t es la firma del barrido.

### 6.2 Evidencia radial de RSSI

Para cada celda se puede evaluar la compatibilidad de la observación con la
posición del A56:

$$
d_c = \left\| \mathbf{x}_c - \mathbf{x}^{A56}_t \right\|
$$

Esta magnitud es una relación geométrica entre una hipótesis de celda y el
receptor. No es una distancia estimada del TARGET que se deba mostrar al usuario.

El perfil calibrado puede dividir el espacio hipotético en bandas:

$$
B(c) = \operatorname{band}(d_c)
$$

y proporcionar una likelihood empírica:

$$
L_R(c) =
P\left(
r_t,\Delta r_t,\dot r_t,\sigma_t
\mid B(c),\text{TARGET profile},\text{context}
\right)
$$

La tabla se aprende con distancias conocidas en laboratorio, pero en operación
solo responde a la pregunta:

> ¿Qué celdas son más compatibles con la firma RSSI observada?

No responde:

> ¿A cuántos metros está el TARGET?

La tabla debe permitir solapamiento amplio entre bandas. Un RSSI aislado no debe
producir una corona estrecha ni dominar el posterior.

### 6.3 Evidencia de desplazamiento del A56

Para una hipótesis de celda c, el modelo puede comparar cómo habría cambiado la
compatibilidad si el TARGET estuviese en ella y el A56 hubiese realizado el
desplazamiento observado:

$$
L_M(c) =
P\left(
\Delta r_t,\dot r_t
\mid
\Delta d_c,\Delta \mathbf{x}^{A56}_t,\text{context}
\right)
$$

La cantidad Δd_c no se convierte en una distancia real del TARGET. Se usa para
comparar hipótesis: unas celdas explican mejor que otras el cambio de señal tras
el movimiento registrado del receptor.

Si la posición o el desplazamiento del A56 tienen mala calidad:

$$
L_M(c) = 1
$$

o se reduce su peso. Es preferible ignorar una evidencia no fiable a introducir
una trayectoria falsa.

### 6.4 Evidencia de barrido por sectores

Durante un barrido se agrupan las muestras por sectores relativos:

~~~text
FAR LEFT | LEFT | FRONT | RIGHT | FAR RIGHT
~~~

El analizador calcula una firma, por ejemplo:

- mediana RSSI por sector;
- diferencia izquierda-derecha;
- pendiente durante el giro;
- número de muestras por sector;
- consistencia entre repeticiones;
- velocidad y calidad del giro.

Para cada celda se obtiene entonces:

$$
L_S(c) =
P\left(
s_t
\mid
\operatorname{sector}(c,\theta_t),
\text{TARGET profile},
\text{maneuver quality}
\right)
$$

sector(c, θ_t) es una relación geométrica de la hipótesis de celda con la
orientación del A56. L_S es un clasificador estadístico de firma. No es una
medición de Angle of Arrival y no debe etiquetarse como bearing.

Si no existe suficiente calibración o el barrido es incompleto, L_S(c) debe ser
neutral o tener peso reducido.

### 6.5 Evidencia temporal y calidad de contacto

La edad, la densidad de anuncios y la estabilidad de la señal se pueden resumir
en una likelihood de calidad:

$$
L_Q(c) =
P\left(
\text{freshness},\text{sample density},\text{continuity}
\mid c,\text{session context}
\right)
$$

En muchos casos L_Q no necesitará una forma espacial propia; puede actuar como
factor de calidad sobre las demás evidencias.

### 6.6 Fusión Venn+

Cada evidencia genera una máscara difusa sobre el GRID:

~~~text
A_R = máscara compatible con RSSI y ventana temporal
A_M = máscara compatible con el desplazamiento del A56
A_S = máscara compatible con la firma del barrido
A_Q = máscara compatible con la calidad/frescura de la sesión
~~~

La superposición no debe implementarse como una intersección binaria que elimine
de golpe todas las celdas fuera de una frontera arbitraria. Venn+ significa:

- cada máscara admite grados de pertenencia;
- las máscaras se acumulan en cada iteración;
- la intersección fuerte acumula mayor peso;
- una evidencia débil no destruye una nube respaldada por varias observaciones;
- las likelihoods se suavizan y se limitan para evitar colapsos artificiales.

Una combinación provisional es:

$$
L_t(c) =
L_R(c)^{w_R}
\cdot
L_M(c)^{w_M}
\cdot
L_S(c)^{w_S}
\cdot
L_Q(c)^{w_Q}
$$

con pesos no negativos y configurables. En logaritmos:

$$
\ell_t(c) =
w_R \log \tilde L_R(c)
+ w_M \log \tilde L_M(c)
+ w_S \log \tilde L_S(c)
+ w_Q \log \tilde L_Q(c)
$$

donde cada likelihood tilde se limita a un intervalo seguro [ε, Lmax].

### 6.7 Actualización estática

Para TARGET_MODE = STATIC, la predicción previa no difunde la nube:

$$
p^-_t(c) = p_{t-1}(c)
$$

La actualización ponderada por confianza es:

$$
\tilde p_t(c) =
p^-_t(c)
\cdot
\exp\left(
\gamma_t \ell_t(c)
\right)
$$

y la normalización:

$$
p_t(c) =
\frac{\tilde p_t(c)}
{\sum_{j \in G} \tilde p_t(c_j)}
$$

γ_t es la confianza de la evidencia de la actualización, no la probabilidad de
que el TARGET esté en la celda:

$$
0 \leq \gamma_t \leq 1
$$

Con γ_t bajo, el sistema actualiza suavemente. Con γ_t alto, una evidencia
consistente tiene más influencia.

### 6.8 Extensión preparada para TARGET móvil

Para un modo futuro móvil, antes de incorporar la nueva evidencia se aplica una
transición:

$$
p^-_t(c) =
\sum_{j \in G}
K_t(c \mid c_j)\,p_{t-1}(c_j)
$$

K_t puede ser una difusión local limitada por:

$$
r_{\text{expansion}} =
v_{\max,\text{target}}\Delta t
+ r_{\text{margin}}
$$

La nube se expande y las observaciones antiguas pierden peso mediante un factor
temporal configurable. El sistema no debe inventar una velocidad del TARGET:
v_max_target será una hipótesis operativa del modo seleccionado.

En esta primera versión estática, r_expansion = 0 y K_t es la identidad.

### 6.9 Estabilidad numérica

El motor debe:

- imponer una probabilidad mínima ε antes de usar logaritmos;
- limitar likelihoods extremas;
- normalizar después de cada actualización;
- detectar NaN, infinitos y suma no válida;
- revertir al posterior anterior si una actualización resulta inválida;
- conservar una pequeña masa de exploración configurable para no cerrar el GRID
  irreversiblemente por una única secuencia errónea;
- registrar qué componentes tuvieron peso neutral por falta de datos.

---

## 7. Confidence y convergencia

### 7.1 Confidence de inferencia

La interfaz debe usar el nombre CONFIDENCE o INFERENCE CONFIDENCE, dejando claro
que expresa confianza en la calidad y coherencia de la inferencia.

Una formulación inicial puede ser:

$$
C_t =
\operatorname{clip}_{[0,1]}
\left(
C_{\text{data}}^{a}
\cdot
C_{\text{motion}}^{b}
\cdot
C_{\text{sweep}}^{c}
\cdot
C_{\text{consistency}}^{d}
\right)
$$

Los factores representan:

- C_data: número, frescura y continuidad de anuncios;
- C_motion: precisión de posición, orientación y desplazamiento del A56;
- C_sweep: completitud y repetibilidad del barrido;
- C_consistency: acuerdo entre actualizaciones consecutivas y componentes del
  modelo.

Una sesión con una nube estrecha pero con datos contradictorios no debe mostrar
automáticamente una confianza alta.

### 7.2 Métricas de concentración

Además de max(p_t), se deben calcular:

$$
H_t =
-\frac{\sum_c p_t(c)\log p_t(c)}{\log N}
$$

como entropía normalizada, y el número mínimo de celdas necesario para acumular
una masa α:

$$
A_{\alpha,t} =
\min\left\{
|S|:
\sum_{c \in S}p_t(c)\geq\alpha
\right\}
$$

Estas métricas permiten describir la convergencia sin afirmar una precisión
física exacta.

### 7.3 Estados de operación

Los estados provisionales son:

- DATA INSUFFICIENT: no hay suficiente historia, calidad o identidad.
- SEARCHING: probabilidad repartida y sin región dominante.
- CONVERGING: la concentración mejora de forma sostenida.
- HIGH PROBABILITY AREA: existe una región dominante, pero debe seguir
  considerándose una hipótesis.
- AREA LOCK: el modelo está suficientemente concentrado para orientar la
  siguiente maniobra de búsqueda, no para garantizar que el TARGET esté allí.
- SIGNAL AMBIGUOUS: las evidencias se contradicen o no son repetibles.
- CONTACT LOST: no se reciben muestras recientes del TARGET.

Los umbrales deben depender de la calibración y no de nombres históricos como
NEAR o MEDIUM. Estas bandas pueden continuar en el tracker como indicadores
relativos, pero no deben confundirse con estados de convergencia espacial.

---

## 8. Evolución de la arquitectura real

### 8.1 Componentes reutilizables

| Componente actual | Reutilización propuesta |
|---|---|
| app/src/main/java/com/suri/pipsurios/prs/BleScanner.kt | Mantener la adquisición A56, permisos, SCAN_MODE_LOW_LATENCY, ciclo de vida y campos RAW. Añadir salida inmutable al capturador RAW. |
| app/src/main/java/com/suri/pipsurios/prs/PrsDeviceRegistry.kt | Mantener DEVICES, reglas guardadas, estados ENABLED/DISABLED/REMOVE y migración local. Extenderlo con perfiles TARGET. |
| app/src/main/java/com/suri/pipsurios/prs/PrsDeviceCategory.kt | Mantener como ayuda orientativa de contactos. No usar su categoría como identidad del TARGET. |
| app/src/main/java/com/suri/pipsurios/prs/PrsContactTracker.kt | Mantener agrupación, expiración, historial temporal y estados relativos. Añadir modo TARGET, ventanas robustas y conexión al RAW store. |
| app/src/main/java/com/suri/pipsurios/prs/PrsTuning.kt | Mantener PrsTuning.DEFAULT como baseline de migración y ampliarlo con configuración probabilística, de GRID, captura y replay. |
| app/src/main/java/com/suri/pipsurios/prs/PrsModels.kt | Reutilizar tipos compatibles, pero separar explícitamente observaciones, features procesadas y posterior inferido. |
| app/src/main/java/com/suri/pipsurios/ui/screens/IndividualTrackingScreens.kt | Conservar el tracking A56-only que sea útil y sustituir la nube anular por el posterior del TARGET cuando se active el nuevo modo. |
| TerrainLocation y proveedor de ubicación existente | Reutilizar para la posición del A56, validando precisión, antigüedad y saltos. No usarlo para ubicar el TARGET. |

### 8.2 Componentes modificables

| Componente actual | Modificación necesaria |
|---|---|
| app/src/main/java/com/suri/pipsurios/ui/screens/PrsTrackingScreen.kt | Eliminar la orquestación de ProbeLink, ProbeTelemetryStore y modo SCAN + PROBE. Añadir selección de TARGET, sesión RAW, proveedor de movimiento, motor probabilístico y estado de replay. |
| app/src/main/java/com/suri/pipsurios/prs/PrsDensityEstimator.kt | Dejar de convertir una banda en una nube anular de azimut completo como estimador principal. Convertir el posterior por celda en densidad, contornos y métricas. Puede conservar un modo legacy solo durante la migración. |
| app/src/main/java/com/suri/pipsurios/ui/screens/PrsDensityGrid.kt | Renderizar matriz de probabilidad, A56, marco local, sectores inferidos con lenguaje prudente, leyenda de confianza y estado de convergencia. Eliminar el subgrid del PROBE. |
| app/src/main/java/com/suri/pipsurios/ui/screens/PrsDevicesScreen.kt | Añadir acción USE AS TARGET, perfil de calibración y comprobación de conflicto con reglas de exclusión. Mantener la guía de MAC y las advertencias sobre direcciones rotatorias. |
| app/src/main/java/com/suri/pipsurios/prs/PrsMovementContext | Convertirlo en un contrato real de contexto de movimiento. Debe incorporar calidad, timestamp, posición local, desplazamiento, orientación y procedencia. |
| Modo prsOnlyDebug | Mantener A56-only. Añadir, si el rendimiento lo permite, una vista reducida del GRID probabilístico sin reintroducir PROBE ni posiciones sintéticas. |

### 8.3 Componentes nuevos

Los nombres pueden variar, pero deben existir responsabilidades equivalentes:

| Componente nuevo | Responsabilidad |
|---|---|
| PrsTargetProfile | Identidad conocida, fingerprint, nombre, reglas de matching, perfil de calibración y modo estático/móvil permitido. |
| PrsTargetSelector | Selección explícita desde DEVICES o contactos A56, validación de ambigüedad y conflicto con ignore rules. |
| PrsRawSessionStore | Persistencia local e inmutable de anuncios RAW, metadatos de sesión y contexto requerido para replay. |
| PrsReplayRunner | Alimentación determinista del tracker y del motor desde RAW, a velocidad real o acelerada. |
| PrsMotionProvider | Captura A56 de posición, sensores y orientación con calidad y ciclo de vida controlado. |
| PrsLocalFrame | Transformación reproducible de posiciones A56 a coordenadas del GRID. |
| PrsObservationWindowBuilder | Construcción de features temporales a partir de RAW sin perder trazabilidad. |
| PrsCalibrationProfile | Distribuciones por TARGET, entorno, postura y maniobra; versionado y selección de perfil. |
| PrsLikelihoodModel | Cálculo de L_R, L_M, L_S y L_Q por celda. |
| PrsSweepAnalyzer | Segmentación izquierda-centro-derecha, calidad de maniobra y firma estadística de sectores. |
| PrsProbabilisticGrid | Estado normalizado, actualización estática, soporte futuro de transición móvil y métricas de concentración. |
| PrsInferenceEngine | Orquestación de features, likelihoods, fusión Venn+, confidence y convergencia. |
| PrsInferenceSnapshot | Estado serializable de cada ciclo para UI, auditoría y comparación en replay. |

### 8.4 Eliminación de PROBE del nuevo P.R.S.

La decisión funcional para esta evolución es:

~~~text
P.R.S. nuevo = A56-only
P.R.S. nuevo != PROBE
P.R.S. nuevo != SCAN + PROBE
~~~

Por tanto:

- se elimina SCAN + PROBE del menú completo;
- PrsTrackingScreen no crea ni registra ProbeLink;
- no se escuchan muestras de ProbeDataLayerService;
- ProbeTelemetryStore no alimenta PrsContactTracker ni el GRID;
- no se dibuja la posición relativa del Watch 2;
- no se mezclan fuentes A56 y PROBE_WATCH_2 en el nuevo posterior;
- no se diseñan nuevos contratos para telemetría de PROBE.

Los archivos:

- app/src/main/java/com/suri/pipsurios/prs/ProbeLink.kt;
- app/src/main/java/com/suri/pipsurios/prs/ProbeDataLayerService.kt;
- app/src/main/java/com/suri/pipsurios/prs/ProbeTelemetryStore.kt;
- watch/probeprotocol/src/main/java/com/suri/probeprotocol/ProbeProtocol.kt;

deben quedar fuera del camino de ejecución de P.R.S. Si otras funciones del
repositorio todavía los utilizan, se pueden mantener aislados hasta una
limpieza posterior; no deben reintroducirse como dependencia del modelo
probabilístico.

---

## 9. TARGET BLE, DEVICES y tracking

### 9.1 Flujo de selección

La pantalla puede conservar DEVICES y añadir:

~~~text
DEVICES
  -> IDENTIFY DEVICE
  -> SAVED DEVICES
  -> USE AS TARGET
  -> TARGET PROFILE
~~~

El flujo debe mostrar:

- nombre amigable;
- identificador técnico o fingerprint;
- datos que se usarán para hacer matching;
- si existe regla ENABLED que lo oculta;
- si hay más de un contacto compatible;
- si existe perfil de calibración;
- advertencia de identidad rotatoria o basada únicamente en nombre.

### 9.2 Contact tracker

PrsContactTracker seguirá siendo útil para:

- agrupación de callbacks;
- historial de cada contacto;
- expiración por ausencia;
- RSSI RAW, suavizado y tendencia relativa;
- densidad de anuncios;
- diagnóstico de contactos no TARGET.

El posterior espacial solo se actualizará para el TARGET validado. El estado
APPROACHING seguirá significando evolución de la señal, no movimiento físico del
dispositivo.

### 9.3 Datos de otros contactos

Los demás contactos podrán:

- aparecer en CONTACT LIST;
- quedar sujetos a DEVICES;
- servir para depurar interferencias y densidad de anuncios;
- conservarse en RAW según el modo de captura.

No deben crear nubes espaciales independientes que distraigan del TARGET ni
contaminar su posterior.

---

## 10. Conservación y replay de RAW data

### 10.1 Principios

- RAW se captura en el borde del escáner, antes del suavizado y del filtro
  probabilístico.
- RAW es inmutable.
- Cada sesión conserva el modelo, parámetros y perfil utilizados.
- Una reproducción no vuelve a consultar Bluetooth ni sensores en vivo.
- La reproducción debe producir el mismo resultado con el mismo esquema, orden,
  reloj lógico y configuración.
- El sistema debe mostrar si un snapshot proviene de una sesión real o de replay.

### 10.2 Paquete de sesión recomendado

La implementación puede usar almacenamiento interno versionado, por ejemplo:

~~~text
prs-sessions/
  <session-id>/
    manifest.json
    raw-ble.jsonl
    raw-motion.jsonl
    processed-windows.jsonl
    inference-snapshots.jsonl
    calibration-reference.json
~~~

Los nombres son orientativos. El requisito es conservar:

- manifest: versión de esquema, versión de app, versión de modelo, TARGET,
  perfil, tuning, modo y zona de búsqueda;
- raw-ble: muestras BLE originales y orden temporal;
- raw-motion: posición, orientación y sensores capturados, si el usuario
  habilitó su conservación;
- processed-windows: opcional, para inspección rápida;
- inference-snapshots: opcional, para reconstruir la evolución visual;
- referencia de calibración empleada.

### 10.3 Modos de captura

Se recomiendan dos modos:

| Modo | Uso |
|---|---|
| TARGET_SESSION | Operación normal; conserva al menos RAW del TARGET y el contexto A56 necesario para replay. |
| FULL_DEBUG_SESSION | Calibración y diagnóstico; conserva todos los anuncios observados antes de DEVICES, además de RAW de movimiento y metadatos disponibles. |

La captura persistente debe ser explícita, local y visible para el usuario. Las
direcciones BLE pueden ser datos identificativos; el sistema debe ofrecer
eliminación de sesiones y una política de retención razonable.

### 10.4 Replay determinista

PrsReplayRunner debe poder:

1. abrir una sesión por sessionId;
2. validar versión de esquema;
3. reproducir RAW respetando timestamps;
4. reconstruir ventanas temporales;
5. ejecutar tracker y motor con la misma configuración;
6. producir snapshots comparables;
7. acelerar, pausar o saltar a un intervalo;
8. comparar dos versiones del modelo sobre la misma sesión.

Los tests golden deben verificar que dos ejecuciones con la misma entrada no
produzcan nubes diferentes por orden no determinista de callbacks.

---

## 11. Parámetros configurables

Los siguientes valores son provisionales. No constituyen calibración universal.

### 11.1 Migración de valores actuales

| Parámetro actual | Valor de partida | Tratamiento |
|---|---:|---|
| Cadencia de evaluación | 3 s | Mantener inicialmente para comparar v3.0 y modelo nuevo. |
| Alpha EMA | 0,35 | Mantener como feature de transición; añadir mediana/MAD para no depender solo de EMA. |
| Historial procesado | 8 muestras | Mantener en modo legacy; el motor nuevo debe usar una ventana temporal configurable. |
| Muestras mínimas | 4 | Mantener como mínimo inicial, revisable mediante replay. |
| Duración mínima | 9 s | Mantener para la primera comparación de tendencias. |
| Variación significativa | 4,5 dB | No convertirla directamente en peso espacial; usarla como feature. |
| Histéresis | 1,5 dB | Mantener para estados de tendencia del tracker. |
| Confirmaciones | 2 | Mantener en transición para evitar oscilaciones de UI. |
| Expiración de contacto | 15 s | Mantener inicialmente y convertir la antigüedad en evidencia de frescura. |
| Umbrales NEAR/MEDIUM | -76 / -88 dBm | Mantener solo como bandas relativas legacy; no usarlos como distancia ni como posterior. |

### 11.2 Nuevos parámetros

| Grupo | Parámetro | Propósito |
|---|---|---|
| TARGET | targetMatchPolicy | Orden de prioridad entre identificador, fingerprint, advertising y nombre. |
| TARGET | allowNameOnlyTarget | Permitir o exigir confirmación adicional cuando solo hay nombre. |
| CAPTURE | rawCaptureMode | TARGET_SESSION o FULL_DEBUG_SESSION. |
| TEMPORAL | featureWindowDuration | Ventana de cálculo de mediana, dispersión y pendiente. |
| TEMPORAL | maxObservationGap | Penalización por huecos de anuncios. |
| TEMPORAL | slopeWindowCount | Número de ventanas para la pendiente robusta. |
| GRID | gridColumns, gridRows | Resolución interna del posterior. |
| GRID | validSearchMask | Zona de búsqueda permitida. |
| GRID | explorationFloor | Masa mínima para no eliminar celdas por una única evidencia. |
| LIKELIHOOD | rssiWeight, motionWeight, sweepWeight, qualityWeight | Peso de cada evidencia. |
| LIKELIHOOD | likelihoodFloor, likelihoodCeiling | Límites para estabilidad numérica. |
| LIKELIHOOD | confidenceExponent | Equivalente a γ_t. |
| MOTION | minLocationAccuracy | Calidad mínima para usar posición en el modelo. |
| MOTION | minDisplacementForUpdate | Evita interpretar ruido de posición como movimiento. |
| MOTION | headingQualityThreshold | Calidad mínima para usar orientación en sectores. |
| SWEEP | sectorCount | Inicialmente cinco sectores. |
| SWEEP | minSweepDuration | Duración mínima de izquierda a derecha. |
| SWEEP | minSamplesPerSector | Evita clasificar un sector vacío. |
| CONVERGENCE | entropyConvergingThreshold | Entrada en CONVERGING. |
| CONVERGENCE | massAreaThreshold | Masa necesaria para HIGH PROBABILITY AREA. |
| CONVERGENCE | posteriorStabilityWindow | Persistencia necesaria antes de AREA LOCK. |
| MOBILE | targetMode | Inicialmente STATIC; reservar SLOW_MOVING y FAST_MOVING. |
| MOBILE | targetMaxSpeedHypothesis | Radio de expansión futuro. |
| REPLAY | replaySpeed, deterministicOrdering | Reproducción temporal y orden estable. |

Los perfiles deben guardarse con la sesión. Cambiar un parámetro no debe hacer
imposible interpretar un RAW antiguo.

---

## 12. Fases de implementación

### Fase 0 — Contrato y baseline

- Crear modelos explícitos medido/procesado/inferido.
- Congelar una sesión v3.0 de referencia.
- Añadir tests de conservación de identidad, historial y reglas DEVICES.
- Definir el contrato de PrsInferenceSnapshot.

**Salida:** el cambio de modelo puede compararse con la cadena actual sin mezclar
capas ni perder la semántica de RSSI relativo.

### Fase 1 — A56-only y retirada de PROBE

- Eliminar SCAN + PROBE del menú P.R.S.
- Quitar listeners, enlaces y datos PROBE del flujo de PrsTrackingScreen.
- Eliminar el subgrid del Watch 2.
- Mantener los módulos PROBE solo si alguna otra función externa todavía los usa,
  pero sin dependencia del nuevo P.R.S.
- Verificar prsOnlyDebug como flujo A56-only.

**Salida:** una sesión P.R.S. nueva no tiene ninguna fuente ni estado PROBE.

### Fase 2 — TARGET BLE conocido y seleccionable

- Implementar PrsTargetProfile.
- Añadir selección desde DEVICES y desde el contacto A56 en vivo.
- Resolver matching por identidad técnica y fingerprint.
- Añadir advertencias para identidad rotatoria y nombre ambiguo.
- Impedir que una ignore rule activa oculte el TARGET seleccionado.

**Salida:** el tracker puede operar centrado en exactamente un TARGET validado.

### Fase 3 — RAW session y replay

- Capturar RAW en el callback del A56.
- Crear manifest versionado.
- Persistir sesiones locales según modo de captura.
- Implementar replay determinista.
- Añadir un primer golden test de sesión.

**Salida:** una sesión real puede reproducirse sin BLE ni sensores conectados.

### Fase 4 — Contexto de movimiento del A56

- Implementar PrsMotionProvider.
- Validar posición, antigüedad y precisión.
- Crear marco local reproducible.
- Registrar desplazamiento, orientación y calidad.
- Desactivar evidencias que dependan de datos ausentes o no fiables.

**Salida:** el sistema distingue trayectoria medida del A56 de inferencia sobre
el TARGET.

### Fase 5 — Posterior 2D estático

- Implementar PrsProbabilisticGrid.
- Inicializar distribución uniforme en la zona válida.
- Implementar likelihood radial calibrable sin salida de metros.
- Implementar likelihood temporal y de desplazamiento.
- Implementar actualización estática normalizada.
- Añadir métricas de entropía y concentración.

**Salida:** varias observaciones coherentes actualizan una nube en el GRID sin
posiciones sintéticas.

### Fase 6 — Venn+ y confidence

- Implementar máscaras difusas por componente.
- Fusionar por likelihood ponderada en logaritmos.
- Añadir γ_t según calidad.
- Añadir clamps, suelo exploratorio y rollback por estado inválido.
- Exponer contribuciones y calidad en replay.

**Salida:** las observaciones se superponen iterativamente y una muestra mala no
destruye todo el historial.

### Fase 7 — Barrido izquierda-centro-derecha

- Implementar PrsSweepAnalyzer.
- Segmentar sectores según orientación y timestamps del A56.
- Medir firma estadística, completitud y repetibilidad.
- Introducir L_S solo cuando exista perfil de calibración.
- Usar en UI SECTOR FAVORED, no BEARING.

**Salida:** el barrido puede inclinar la probabilidad hacia un sector hipótesis
sin afirmar una dirección BLE medida.

### Fase 8 — UI operacional y tracking

- Reutilizar INDIVIDUAL TRACKER para el A56.
- Añadir panel TARGET BLE, CONFIDENCE, CONVERGENCE, RAW AGE y MOTION QUALITY.
- Diferenciar visualmente RAW, procesado e inferido.
- Mostrar DATA INSUFFICIENT y SIGNAL AMBIGUOUS de forma explícita.
- Mantener lista de contactos y DEVICES sin alimentar nubes de no TARGET.

**Salida:** la pantalla explica el estado sin sobreprometer precisión.

### Fase 9 — Calibración y aceptación de TARGET estático

- Ejecutar el plan físico de la sección 14.
- Crear perfiles separados por TARGET, configuración del emisor y entorno.
- Separar sesiones de ajuste y validación.
- Comparar posterior, replay y métricas de concentración.

**Salida:** el modelo estático tiene evidencia de repetibilidad suficiente para
su uso experimental.

### Fase 10 — Preparación de TARGET móvil

- Añadir K_t como interfaz, desactivada por defecto.
- Implementar difusión limitada y decaimiento temporal.
- Añadir perfiles SLOW MOVING y FAST MOVING.
- Validar primero con replay sintético de transición de estado, no con una
  promesa de precisión física.

**Salida:** la arquitectura admite móvil sin contaminar el baseline estático.

---

## 13. Estrategia de tests

### 13.1 Tests unitarios deterministas

Cubrir como mínimo:

- matching de TARGET por identificador y fingerprint;
- fallback por nombre y detección de ambigüedad;
- aplicación de ignore rules;
- imposibilidad de ocultar el TARGET seleccionado;
- conservación exacta de RSSI RAW;
- construcción de mediana, EMA, MAD, pendiente y frescura;
- expiración de contacto;
- transformación de posición a marco local;
- rechazo de coordenadas no finitas o fixes caducados;
- segmentación de barrido;
- likelihood neutral si faltan datos;
- normalización del posterior;
- suma de probabilidades aproximadamente igual a 1;
- ausencia de NaN e infinitos;
- actualización estática sin expansión;
- expansión móvil limitada al radio configurado;
- sensibilidad de γ_t;
- rollback cuando una likelihood es inválida;
- entropía y masa de región;
- replay idéntico con la misma sesión y configuración.

### 13.2 Golden tests sobre RAW replay

Preparar sesiones controladas con:

- señal estable;
- señal con picos;
- señal creciente;
- señal decreciente;
- pérdida temporal del TARGET;
- barrido izquierda-centro-derecha repetible;
- barrido incompleto;
- movimiento con posición válida;
- movimiento con GPS ruidoso;
- evidencia contradictoria.

Guardar snapshots esperados de:

- features temporales;
- likelihoods por componente;
- posterior;
- confidence;
- estado de convergencia.

No se debe comprobar una coordenada exacta inventada. Se comprobará la forma de
la distribución, su normalización y las métricas de concentración.

### 13.3 Tests de integración Android

- permisos y Bluetooth apagado;
- creación y destrucción correcta de la sesión;
- pausa y reanudación de captura;
- ausencia de fugas de listeners;
- funcionamiento de LOCAL SCAN;
- selección de TARGET desde la lista;
- sesión RAW creada y cerrada correctamente;
- replay visible en la UI;
- prsOnlyDebug;
- rendimiento con callbacks de alta frecuencia;
- batería y frecuencia de sensores.

### 13.4 Tests de arquitectura

Añadir verificaciones que fallen si:

- el nuevo motor recibe una fuente PROBE;
- la pantalla probabilística instancia ProbeLink;
- aparece SCAN + PROBE en el menú nuevo;
- se generan coordenadas sintéticas de TARGET;
- se usa un campo llamado bearing para una inferencia de sectores;
- se presenta RSSI como distancia;
- una nube se crea a partir de una posición fija artificial.

### 13.5 Métricas de validación

En laboratorio se puede comparar contra ground truth conocido, pero solo como
etiqueta de evaluación:

- masa de probabilidad que cubre la celda o región real;
- cobertura de la región de alta probabilidad;
- tiempo hasta CONVERGING;
- porcentaje de sesiones con concentración falsa;
- recuperación después de evidencia contradictoria;
- estabilidad entre repeticiones;
- diferencia entre sesiones de ajuste y validación;
- coste de CPU, memoria y batería.

La aplicación operativa no debe mostrar ese ground truth ni convertirlo en una
medición automática del TARGET.

---

## 14. Plan de calibración física

La calibración debe utilizar siempre el mismo TARGET BLE cuando se pretenda crear
un perfil específico. Las distancias físicas conocidas sirven para etiquetar
experimentos, no para prometer una curva universal RSSI -> metros.

Cada registro debe incluir:

- TARGET y versión de su perfil;
- potencia e intervalo de advertising configurados;
- posición y orientación del TARGET;
- posición, orientación y trayectoria del A56;
- timestamp monotónico;
- RSSI RAW completo;
- payload de advertising;
- condiciones del entorno;
- obstáculos;
- operador y postura;
- versión de la aplicación;
- parámetros del modelo;
- etiqueta de ground truth de laboratorio.

Las sesiones deben separarse por día, recorrido o entorno entre ajuste y
validación. Reutilizar la misma secuencia para ambos produce una falsa sensación
de precisión.

### Fase física 1 — Caracterización básica del TARGET

TARGET fijo y A56 en posiciones conocidas, por ejemplo:

~~~text
0,25 m / 0,5 m / 1 m / 2 m / 5 m / 10 m / 15 m / 20 m / 30 m
~~~

En cada posición:

- registrar al menos 60 segundos;
- repetir cada posición tres veces;
- conservar RAW;
- calcular mediana, media, dispersión robusta, anuncios por segundo y pérdidas.

El resultado es una distribución por bandas, no una tabla que convierta un RSSI
en metros durante la partida.

### Fase física 2 — Repetibilidad

Repetir varias veces situaciones representativas, por ejemplo 1, 5, 10 y 20 m,
separadas en el tiempo. Medir cuánto se desplazan las distribuciones y ampliar
las likelihoods si el entorno introduce variabilidad.

### Fase física 3 — Orientación del TARGET

Mantener una situación controlada y repetir con el TARGET:

- frontal;
- girado 90 grados;
- girado 180 grados;
- girado 270 grados.

Esto permite separar la variabilidad de la antena del TARGET de la del A56.

### Fase física 4 — Orientación del A56

Con TARGET fijo, rotar el A56 en pasos de 45 grados, manteniendo posición y
postura lo más constantes posible. Medir el patrón A56 + operador, sin
interpretarlo todavía como dirección BLE.

### Fase física 5 — Barrido continuo

Con operador y TARGET estáticos:

- comenzar aproximadamente 90 grados a la izquierda;
- girar lentamente hasta aproximadamente 90 grados a la derecha;
- registrar orientación, timestamp y RSSI;
- realizar al menos 10 barridos;
- variar el orden de algunas repeticiones para detectar sesgo del operador.

Aceptar una firma de sectores solo si es repetible frente a un conjunto de
validación.

### Fase física 6 — Aproximación y alejamiento frontal

Realizar recorridos controlados de aproximación y alejamiento. El objetivo es
aprender cómo se comportan ΔRSSI, la pendiente y la variabilidad cuando el A56
se mueve respecto a un TARGET estático.

La salida es una likelihood de evolución, no un estimador de metros.

### Fase física 7 — Cruce lateral

Cruzar transversalmente delante del TARGET, registrando la secuencia completa.
Buscar patrones de aumento, máximo aproximado y disminución, y medir si son
repetibles al invertir el sentido del recorrido.

### Fase física 8 — Rutas oblicuas

Evaluar aproximaciones diagonales, alejamiento diagonal y paso cercano sin
aproximación frontal. Estas rutas son necesarias porque el uso real no será un
movimiento perfecto hacia el objetivo.

### Fase física 9 — Clasificación LEFT / FRONT / RIGHT

Colocar el TARGET en posiciones conocidas respecto a una ruta:

- claramente a la izquierda;
- ligeramente a la izquierda;
- delante;
- ligeramente a la derecha;
- claramente a la derecha.

Repetir la maniobra:

~~~text
avanzar -> detenerse -> barrer izquierda-centro-derecha -> continuar
~~~

Aprender probabilidades de clase de firma, sin llamarlas bearing.

### Fase física 10 — Obstáculos y entorno

Repetir un subconjunto de situaciones con:

- persona;
- vegetación;
- pared;
- esquina;
- edificio;
- mochila o equipamiento;
- cambios de orientación del operador.

Comparar degradación, dispersión y falsos locks. No mezclar perfiles de campo
abierto y entorno obstruido como si fueran la misma distribución.

### Fase física 11 — Ensayo completo con TARGET estático

Ocultar el TARGET en un punto conocido por el organizador, no por el operador.
Ejecutar:

~~~text
desplazamiento -> barrido -> actualización GRID -> decisión
-> desplazamiento -> barrido -> actualización GRID -> búsqueda visual
~~~

Registrar:

- trayectoria real del A56;
- posición real del TARGET como ground truth privado;
- RAW completo;
- snapshots del GRID;
- confidence;
- tiempo hasta convergencia;
- región de alta probabilidad;
- número de maniobras;
- falsos locks;
- pérdidas de contacto;
- recuperación después de una maniobra contradictoria.

### Fase física 12 — Validación cruzada

Repetir el ensayo en otro momento, con otra ruta y, si es posible, otro operador.
Un perfil solo debe promocionarse si conserva utilidad fuera de la secuencia
con la que fue ajustado.

### Fase posterior — TARGET móvil

Solo después de aceptar el modo estático:

- repetir con TARGET caminando a velocidades acotadas;
- medir cuánto se degrada la nube sin expansión;
- estimar parámetros de decaimiento;
- activar SLOW_MOVING;
- validar que la expansión evita lock falso sin volver uniforme todo el GRID;
- reservar FAST_MOVING para una fase posterior.

---

## 15. Criterios de aceptación de la primera versión probabilística

La primera versión puede considerarse lista para pruebas de campo cuando:

- el flujo P.R.S. es A56-only;
- no existe SCAN + PROBE en la experiencia nueva;
- DEVICES y sus ignore rules siguen funcionando;
- un TARGET conocido puede seleccionarse y verificarse;
- el tracker conserva RAW y construye features reproducibles;
- el contexto de movimiento indica su calidad y no inventa posiciones;
- el posterior permanece normalizado y finito;
- el modelo estático no expande artificialmente la nube;
- la nube cambia por acumulación de evidencia y no por puntos sintéticos;
- el barrido solo genera probabilidad de sectores hipótesis;
- la UI nunca muestra bearing BLE, coordenada BLE ni RSSI convertido a metros;
- una sesión se puede guardar y reproducir;
- los tests golden son deterministas;
- existe una ruta clara para distinguir DATA INSUFFICIENT, CONVERGING,
  SIGNAL AMBIGUOUS y CONTACT LOST;
- la calibración física demuestra al menos repetibilidad en un entorno controlado.

---

## 16. Riesgos y límites conocidos

El modelo seguirá limitado por:

- multipath y reflexiones;
- bloqueo por el cuerpo del operador;
- orientación y patrón de antena del TARGET;
- orientación y postura del A56;
- variabilidad del entorno;
- interferencia de otros anuncios BLE;
- direcciones privadas o rotatorias;
- pérdida intermitente de anuncios;
- precisión y antigüedad de la ubicación;
- errores de orientación del sensor;
- maniobras de barrido no repetibles;
- perfiles de calibración demasiado específicos.

Una nube concentrada puede ser una inferencia equivocada. Por eso el sistema debe
conservar la incertidumbre, mostrar confidence separada de probabilidad y
permitir replay de la evidencia que produjo el resultado.

P.R.S. continúa siendo una herramienta experimental de búsqueda para el escenario
del proyecto. No debe presentarse como herramienta de seguridad, vigilancia,
detección de personas, confirmación de ocupación ni localización garantizada.

---

## 17. Resumen para implementación

~~~text
IMPLEMENTAR:
  A56-only
  TARGET BLE conocido y seleccionable
  DEVICES + ignore rules
  tracking temporal de contactos
  RAW immutable session
  replay determinista
  posición / desplazamiento / orientación del A56
  GRID 2D probabilístico
  likelihood radial calibrada por TARGET sin RSSI -> metros
  likelihood de evolución temporal y movimiento
  barrido LEFT-CENTER-RIGHT como clasificación estadística de sectores
  Venn+ por máscaras difusas y acumulación iterativa
  actualización Bayesiana o likelihood ponderada por celda
  nube de densidad que converge
  confidence y métricas de concentración
  STATIC primero
  interfaz preparada para MOBILE con expansión y decaimiento

ELIMINAR DEL NUEVO P.R.S.:
  PROBE
  SCAN + PROBE
  telemetría Watch 2
  subgrid del Watch 2
  bearing BLE
  coordenadas sintéticas de v2.0
  RSSI convertido a metros
  posición puntual inventada del TARGET

RECORDAR SIEMPRE:
  medido != procesado != inferido
~~~
