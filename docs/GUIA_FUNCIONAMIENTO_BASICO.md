# Guía de funcionamiento de SuriOS — nivel BÁSICO

> Documento vivo. Última revisión: 2026-09-03. Se actualizará junto con los
> cambios de pantallas, funcionamiento, parámetros y pruebas. Si el código
> cambia y esta guía no cambia con él, la guía queda pendiente de revisión.

## 1. Visión general

SuriOS es una aplicación Android construida como un conjunto de pantallas y
herramientas. Cada pantalla muestra información o recoge una acción del
operador. La aplicación conserva algunos datos de configuración y de
operaciones, mientras que otras sesiones experimentales, como TRACKER, viven
solo mientras la pantalla está abierta.

Las partes principales son:

| Área | Función |
|---|---|
| SET-UP | Configuración del operador y del equipo |
| CURRENT GEAR | Selección del equipo utilizado |
| INVENTORY | Consulta del material disponible |
| STATUS | Resumen del estado del equipo |
| DATA | Registro y estadísticas de operaciones |
| TOOLS | Herramientas operativas |
| MAP/TERRAIN | Mapa offline y posición del operador |
| P.R.S. | Detección y análisis experimental de señales BLE |

La interfaz usa Jetpack Compose. En términos sencillos, esto significa que la
pantalla se vuelve a dibujar cuando cambian los datos que está mostrando.

## 2. P.R.S. tiene dos usos diferentes

### SENTRY

SENTRY es una vigilancia general. Muestra todos los contactos BLE que no estén
excluidos por las reglas de `DEVICES`.

Sus modos son:

- `PIP`: escaneo del A56.
- `PIP + PROBE`: escaneo del A56 y del Watch 2.

No se selecciona un único objetivo para el análisis principal.

### TRACKER

TRACKER es un seguimiento experimental de un único objetivo seleccionado. El
flujo actual es:

1. Elegir `ONLY PIP-BOY` o `PIP-BOY + PROBE`.
2. Elegir un mapa TERRAIN.
3. Esperar a que aparezca el dispositivo en la lista BLE.
4. Tocar ese dispositivo.
5. Observar la pantalla TRACKER.

La pantalla de selección ya empieza a escanear automáticamente. La pantalla
final también inicia automáticamente sus recursos.

## 3. Funcionamiento actual de TRACKER

Tu expectativa de una sesión manual sería:

```text
START → adquirir lecturas → STOP → calcular resultado
```

El programa actual funciona así:

```text
entrar en TARGET/TRACKER
        ↓
iniciar automáticamente BLE, GPS, brújula y, si procede, PROBE
        ↓
recibir lecturas durante toda la pantalla
        ↓
actualizar datos sencillos inmediatamente
        ↓
evaluar historial aproximadamente cada 3 segundos
        ↓
salir con BACK y detener recursos
```

Por tanto, no hay una fase posterior de cálculo. El cálculo es incremental:
se repite durante la marcha y se va corrigiendo con los datos nuevos.

## 4. Diferencia entre lectura y cálculo

Esta diferencia explica por qué la herramienta puede parecer continua, pero no
reacciona a cada instante exactamente igual.

### Lectura

`BleScanner` utiliza el escáner BLE del Android. Cada anuncio recibido incluye,
entre otros datos:

- identificador del dispositivo;
- nombre anunciado, si existe;
- RSSI RAW;
- hora de observación;
- datos anunciados;
- tipo de dispositivo y tipo de dirección cuando Android los proporciona.

El último anuncio recibido sustituye al anterior como lectura actual del
contacto. El contador de muestras sigue aumentando.

### Cálculo

`PrsContactTracker` mantiene un estado separado para cada contacto. No cambia
la tendencia con cada callback BLE. Cada aproximadamente 3 segundos:

1. toma la lectura más reciente;
2. calcula un RSSI suavizado;
3. añade un punto al historial;
4. conserva como máximo 8 puntos;
5. calcula la variación del historial;
6. actualiza la tendencia y la banda relativa;
7. genera la nube de densidad que dibuja la interfaz.

Esto es deliberado: el RSSI puede variar aunque el objetivo no se haya movido.
La cadencia controlada intenta evitar conclusiones demasiado rápidas.

## 5. Valores de análisis que usa la versión actual

Los valores por defecto de `PrsTuning.DEFAULT` son:

| Valor | Uso |
|---|---:|
| Intervalo de evaluación | 3 segundos |
| Suavizado | 0,35 |
| Tamaño máximo del historial | 8 evaluaciones |
| Muestras mínimas para tendencia | 4 evaluaciones |
| Tiempo mínimo de tendencia | 9 segundos |
| Cambio significativo | 4,5 dB |
| Variación considerada estable | 2,0 dB |
| Confirmaciones para cambiar tendencia | 2 evaluaciones |
| Expiración sin recibir señal | 15 segundos |

La tendencia no aparece inmediatamente. Como orientación, primero hacen falta
varias evaluaciones durante unos segundos y después confirmaciones adicionales.
En condiciones normales puede tardar aproximadamente entre 12 y 15 segundos en
mostrar una tendencia confirmada, dependiendo de cuándo apareció el objetivo.

Las bandas son relativas:

- `NEAR`: RSSI suavizado igual o superior a `-76 dBm`.
- `MEDIUM`: desde `-88 dBm` hasta menos de `-76 dBm`.
- `FAR`: por debajo de `-88 dBm`.

Estos números no equivalen a metros y no se deben interpretar como una regla
universal de distancia.

## 6. Qué ocurre al caminar

Si TRACKER está abierto y el Bluetooth funciona:

- el A56 sigue escuchando señales BLE;
- el RSSI RAW puede cambiar con cada lectura nueva;
- el RSSI suavizado se actualiza al ritmo de evaluación;
- la tendencia necesita historial y confirmación;
- el GPS mueve el centro del mapa siguiendo al operador;
- la brújula puede girar la vista del mapa;
- la nube de densidad representa incertidumbre, no una posición real del
  objetivo.

En `PIP-BOY + PROBE`, el Watch 2 también puede aportar muestras BLE y su propia
posición. La posición que se dibuja del Watch 2 representa al PROBE, no al
objetivo seleccionado.

## 7. Controles y ciclo de vida

En el TRACKER de mapa actual, `< BACK` es la salida de la sesión. Al salir se
detienen BLE, GPS, brújula y PROBE si estaba activo.

En la pantalla general de P.R.S. existen controles con nombres parecidos, pero
no significan lo mismo que una sesión manual:

- `CLEAR CONTACTS` vacía los contactos que están en memoria; el escaneo sigue
  activo y pueden aparecer otra vez.
- `STOP TRACKING` quita la selección visual del objetivo; no es un botón global
  de parada del escáner.
- `TRY AGAIN` vuelve a intentar permisos, Bluetooth o el enlace con PROBE.

El objetivo y el historial de la sesión de TRACKER no se guardan como una
operación DATA al salir.

## 8. Qué mide realmente el mapa

El mapa combina tres cosas distintas:

1. **Medido**: posición y precisión GPS del A56; posición del Watch 2 si se usa
   PROBE; RSSI recibido del objetivo.
2. **Procesado**: RSSI suavizado, historial y variación temporal.
3. **Inferido**: tendencia, banda relativa y nube de densidad.

Separar estas tres capas es importante. El mapa no transforma RSSI en metros,
no calcula el rumbo Bluetooth del objetivo y no triangula su posición.

## 9. Otras áreas de la aplicación

- Los datos de configuración, como el operador y el equipo, se guardan para
  poder reutilizarlos.
- DATA trabaja con registros de operaciones y estadísticas.
- TERRAIN carga mapas MBTiles incluidos en la variante instalada.
- `DEVICES` permite guardar reglas por dirección o nombre BLE para excluir
  dispositivos antes de que entren en P.R.S.
- Las variantes MAIN, FENRIR, ALTAMIRA y CHECHU pueden compartir código y
  diferenciarse por recursos y mapas de distribución.

## 10. Conclusión

TRACKER actual es un monitor continuo con análisis periódico. No espera a que
el operador pulse START, ni acumula una sesión para calcularla únicamente al
final. Si se desea el comportamiento manual `START → STOP → CALCULATE`, será un
cambio funcional nuevo, no una simple modificación del texto de la pantalla.
