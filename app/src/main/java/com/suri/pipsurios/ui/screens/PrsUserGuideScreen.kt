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
                    "Elige ONLY PIP-BOY o PIP-BOY + PROBE, selecciona primero el terreno y después el objetivo. Al entrar en la pantalla del objetivo la lectura empieza automáticamente: no hay START. BLE recibe datos de forma continua mientras la pantalla está abierta. El análisis se revisa aproximadamente cada 3 s. Espera 12–15 s antes de valorar una tendencia. BACK termina la sesión; todavía no hay STOP ni cálculo final manual."
                } else {
                    "Elige ONLY PIP-BOY, selecciona primero el terreno y después el objetivo. Al entrar en la pantalla del objetivo la lectura empieza automáticamente: no hay START. BLE recibe datos de forma continua mientras la pantalla está abierta. El análisis se revisa aproximadamente cada 3 s. Espera 12–15 s antes de valorar una tendencia. BACK termina la sesión; todavía no hay STOP ni cálculo final manual."
                }
            )
            PrsGuideBlock(
                title = "LECTURA Y TIEMPO",
                body = "RAW es la última señal observada y puede cambiar de inmediato. SMOOTH, el historial y la tendencia necesitan varias observaciones y se actualizan por ciclos. WAITING significa que aún no hay suficiente historial. SAMPLES y CONFIDENCE ayudan a saber si la lectura ya es estable."
            )
            PrsGuideBlock(
                title = "DEVICES",
                body = "Aquí se identifican y guardan dispositivos que deben omitirse. Las reglas activas se aplican automáticamente tanto a SENTRY como a TRACKER. Comprueba el nombre, el identificador o la dirección observada antes de guardar una regla; el RSSI solo es una referencia relativa."
            )
            PrsGuideBlock(
                title = if (showProbe) "PERMISOS Y PROBE" else "PERMISOS",
                body = if (showProbe) {
                    "ONLY PIP-BOY usa el A56 y no necesita otro dispositivo. PIP-BOY + PROBE necesita el Watch 2 emparejado y conectado. Bluetooth y los permisos de escaneo/conexión son necesarios. La posición de PROBE es la del Watch 2 receptor, no la del objetivo. Si algo falla, concede permisos o usa TRY AGAIN / RETRY."
                } else {
                    "Esta edición utiliza únicamente el A56. Bluetooth y los permisos de escaneo/conexión son necesarios. Si algo falla, concede permisos o usa TRY AGAIN / RETRY."
                }
            )
            PrsGuideBlock(
                title = "LECTURA DE LA PANTALLA",
                body = "RAW es la última señal observada. SMOOTH es una señal suavizada. NEAR, MEDIUM y FAR son bandas relativas. APPROACHING y MOVING AWAY indican una tendencia estimada; WAITING significa que todavía no hay suficientes muestras."
            )
            PrsGuideBlock(
                title = "BUENAS PRÁCTICAS",
                body = "Mantén Bluetooth activo, espera varias muestras y no identifiques un dispositivo usando un único dato. Revisa DEVICES antes de la sesión. Para una prueba de campo anota distancia real aproximada, obstáculos, RAW, SMOOTH, TREND, banda, SAMPLES y CONFIDENCE en la plantilla CSV. La distancia real es una referencia externa: P.R.S. no calcula metros."
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
