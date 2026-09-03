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
fun PrsUserGuideScreen(onBack: () -> Unit) {
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
                body = "SENTRY es el modo de vigilancia. PIP utiliza el escáner del A56. PIP + PROBE combina el A56 con el Watch 2 PROBE. Ambos modos muestran todos los nodos detectados y no permiten seleccionar ni seguir un dispositivo concreto."
            )
            PrsGuideBlock(
                title = "TRACKER",
                body = "TRACKER conserva el flujo de la versión anterior: elige ONLY PIP-BOY o PIP-BOY + PROBE, selecciona primero una ubicación del terreno y después identifica un objetivo. Para guardar un dispositivo y poder rastrearlo, vincula previamente los dos dispositivos. El resultado se muestra en el GRID sobre el mapa."
            )
            PrsGuideBlock(
                title = "DEVICES",
                body = "Aquí se identifican y guardan dispositivos que deben omitirse. Las reglas activas se aplican automáticamente tanto a SENTRY como a TRACKER. Comprueba el nombre, el identificador o la dirección observada antes de guardar una regla; el RSSI solo es una referencia relativa."
            )
            PrsGuideBlock(
                title = "PERMISOS Y PROBE",
                body = "Para escanear se necesitan Bluetooth y ubicación. PIP + PROBE necesita además el Watch 2 emparejado y la conexión de PROBE disponible. Si algo falla, concede los permisos o usa TRY AGAIN / RETRY según la pantalla."
            )
            PrsGuideBlock(
                title = "LECTURA DE LA PANTALLA",
                body = "RAW es la última señal observada. SMOOTH es una señal suavizada. NEAR, MEDIUM y FAR son bandas relativas. APPROACHING y MOVING AWAY indican una tendencia estimada; WAITING significa que todavía no hay suficientes muestras."
            )
            PrsGuideBlock(
                title = "BUENAS PRÁCTICAS",
                body = "Mantén el Bluetooth activo, espera a que lleguen varias muestras y evita identificar un dispositivo usando un único dato. Revisa DEVICES antes de una sesión para no ocultar accidentalmente el equipo que quieres observar."
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
