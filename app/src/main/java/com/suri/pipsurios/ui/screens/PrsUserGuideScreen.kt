package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipNeutral

@Composable
fun PrsUserGuideScreen(onBack: () -> Unit, showProbe: Boolean = true) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 62.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "P.R.S. / USER GUIDE",
                color = PipGreen,
                fontSize = 26.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "GUÍA RÁPIDA DEL SISTEMA DE VIGILANCIA Y SEGUIMIENTO",
                color = PipAmber,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            PrsGuideBlock(
                title = "OBJETIVO",
                body = "P.R.S. detecta anuncios Bluetooth Low Energy (BLE) cercanos y los presenta como nodos. La información sirve para observar presencia, actividad y tendencia relativa de la señal. No convierte la señal en metros ni proporciona una dirección exacta."
            )
            PrsGuideBlock(
                title = "SENTRY",
                body = if (showProbe) {
                    "SENTRY es el modo de vigilancia. PIP utiliza el escáner del A56. PIP + PROBE combina el A56 con el Watch 2 PROBE. Ambos modos muestran todos los nodos detectados y no permiten seleccionar ni seguir un dispositivo concreto."
                } else {
                    "SENTRY es el modo de vigilancia. PIP utiliza el escáner del A56 y muestra todos los nodos detectados. No permite seleccionar ni seguir un dispositivo concreto."
                }
            )
            PrsGuideBlock(
                title = "TRACKER",
                body = if (showProbe) {
                    "Elige PIP o PIP + PROBE, selecciona primero el terreno y después el objetivo. Al entrar en la pantalla del objetivo la lectura empieza automáticamente. BLE recibe datos de forma continua mientras la pantalla está abierta. El análisis se revisa aproximadamente cada 3 s. Espera 12–15 s antes de valorar una tendencia. TRACKER muestra sobre el mapa un área estimada del objetivo con líneas rojas finas e intermitentes. El área se recalcula en cada lectura y sustituye a la anterior: no acumula niebla ni líneas históricas. La inferencia usa el rumbo del móvil y la variación de RSSI para estimar si el objetivo está a la izquierda o a la derecha. Cada ciclo de lectura emite un sonar. Puedes pellizcar con dos dedos para acercar o alejar el mapa mientras la lectura continúa. BACK termina la sesión; todavía no hay STOP ni cálculo final manual."
                } else {
                    "Elige PIP, selecciona primero el terreno y después el objetivo. Al entrar en la pantalla del objetivo la lectura empieza automáticamente. BLE recibe datos de forma continua mientras la pantalla está abierta. El análisis se revisa aproximadamente cada 3 s. Espera 12–15 s antes de valorar una tendencia. TRACKER muestra sobre el mapa un área estimada del objetivo con líneas rojas finas e intermitentes. El área se recalcula en cada lectura y sustituye a la anterior: no acumula niebla ni líneas históricas. La inferencia usa el rumbo del móvil y la variación de RSSI para estimar si el objetivo está a la izquierda o a la derecha. Cada ciclo de lectura emite un sonar. Puedes pellizcar con dos dedos para acercar o alejar el mapa mientras la lectura continúa. BACK termina la sesión; todavía no hay STOP ni cálculo final manual."
                }
            )
            PrsGuideBlock(
                title = "CONSIDERACIONES",
                body = """
                    MODELO ESTADÍSTICO
                    P.R.S. no convierte RSSI en distancia exacta. Cada ciclo de 3 s toma el RSSI suavizado que mantiene el seguimiento. La suavización aplicada por el rastreador es S_t = S_(t-1) + 0.35 x (RSSI_t - S_(t-1)); en la primera medición, S_1 = RSSI_1. El estimador de zona conserva como máximo 12 lecturas evaluadas del objetivo.

                    La confianza temporal es C = clamp(n / 8, 0, 1), donde n es el número de lecturas guardadas para construir la zona. El resultado no es una probabilidad geográfica calibrada: indica cuánto respaldo temporal tiene la hipótesis. Las lecturas antiguas se vuelven menos influyentes mediante el peso w_i = 2^((S_i - S_max) / 6), donde S_max es el RSSI suavizado más fuerte.

                    LADO SEGÚN RUMBO Y RSSI
                    P.R.S. compara el giro del móvil (delta H) con el cambio de señal (delta S). Si ambos tienen el mismo signo, la lectura vota DERECHA; si tienen signo contrario, vota IZQUIERDA. Solo se usa una votación cuando |delta H| >= 10 grados y |delta S| >= 1.5 dB. La memoria de votos es Score_t = 0.70 x Score_(t-1) + voto x evidencia. IZQUIERDA o DERECHA se muestra cuando |Score| >= 0.25. Es una inferencia experimental: el BLE normal no mide directamente el ángulo del objetivo y una antena omnidireccional puede producir lecturas ambiguas.

                    ÁREA PROBABLE DEL OBJETIVO
                    Cada lectura genera una hipótesis delante del móvil, desplazada 65 grados hacia el lado estimado. La distancia de referencia depende de la banda: CERCA 25 m, MEDIA 75 m, LEJOS 150 m y DESCONOCIDA 120 m. El centro actual es el promedio ponderado en coordenadas locales: X_objetivo = sum(w_i x_i) / sum(w_i), y lo mismo para el eje norte.

                    El radio de la zona se calcula como R = max(R_min, R_banda x (1 - 0.60 x C) + 0.35 x dispersión + 0.50 x precisión_GPS), con un límite superior de 450 m. R_banda vale 40/90/180/180 m para CERCA/MEDIA/LEJOS/DESCONOCIDA; R_min vale 15/28/55/65 m. El área equivalente sería A = pi x R^2, pero no debe interpretarse como una probabilidad real en m2. En pantalla solo se dibuja la zona actual con líneas rojas discontinuas; cada nueva estimación reemplaza la anterior y no deja sombreado acumulado.

                    BANDAS Y TIEMPO MÍNIMO
                    CERCA: S >= -76 dBm. MEDIA: -88 dBm <= S < -76 dBm. LEJOS: S < -88 dBm. DESCONOCIDA aparece antes de disponer de historial útil. Son umbrales iniciales y pueden variar por cuerpo, orientación, obstáculos y entorno.

                    La primera lectura se evalúa aproximadamente a los 3 s. Para una lectura mínimamente fiable, espera al menos 4 evaluaciones (unos 12 s) y, para que el lado se confirme con mayor estabilidad, 12–15 s. Lo recomendable es mantener 15–20 s si hay movimiento u obstáculos. Si no llega una señal nueva durante 30 s, el contacto puede caducar. El objetivo se supone estático; si se mueve, las zonas superpuestas dejan de representar una única ubicación.
                """.trimIndent()
            )
            PrsGuideBlock(
                title = "LECTURA Y TIEMPO",
                body = "SEÑAL BRUTA es la última señal observada y puede cambiar de inmediato. SEÑAL SUAVIZADA, el historial y la tendencia necesitan varias observaciones y se actualizan por ciclos. ESPERANDO significa que aún no hay suficiente historial. MUESTRAS y CONFIANZA ayudan a saber si la lectura ya es estable."
            )
            PrsGuideBlock(
                title = "DEVICES",
                body = "Aquí se identifican y guardan dispositivos que deben omitirse. Las reglas activas se aplican automáticamente tanto a SENTRY como a TRACKER. Comprueba el nombre, el identificador o la dirección observada antes de guardar una regla; el RSSI solo es una referencia relativa."
            )
            PrsGuideBlock(
                title = if (showProbe) "PERMISOS Y PROBE" else "PERMISOS",
                body = if (showProbe) {
                    "PIP usa el A56 y no necesita otro dispositivo. PIP + PROBE necesita el Watch 2 emparejado y conectado. Bluetooth y los permisos de escaneo/conexión son necesarios. La posición de PROBE es la del Watch 2 receptor, no la del objetivo. Si algo falla, concede permisos o usa REINTENTAR."
                } else {
                    "Esta edición utiliza únicamente el A56. Bluetooth y los permisos de escaneo/conexión son necesarios. Si algo falla, concede permisos o usa REINTENTAR."
                }
            )
            PrsGuideBlock(
                title = "LECTURA DE LA PANTALLA",
                body = "SEÑAL BRUTA es la última señal observada. SEÑAL SUAVIZADA es un valor filtrado. CERCA, MEDIA y LEJOS son bandas relativas. ACERCÁNDOSE y ALEJÁNDOSE indican una tendencia estimada; ESPERANDO significa que todavía no hay suficientes muestras."
            )
            PrsGuideBlock(
                title = "BUENAS PRÁCTICAS",
                body = "Mantén Bluetooth activo, espera varias muestras y no identifiques un dispositivo usando un único dato. Revisa DEVICES antes de la sesión. Para una prueba de campo anota distancia real aproximada, obstáculos, señal bruta, señal suavizada, tendencia, banda, muestras y confianza en la plantilla CSV. La distancia real es una referencia externa: P.R.S. no calcula metros."
            )
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
    }
}

@Composable
private fun PrsGuideBlock(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipGreenDim.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(title, color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
        Text(body, color = PipNeutral, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}
