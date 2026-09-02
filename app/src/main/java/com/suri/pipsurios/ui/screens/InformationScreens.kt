package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

data class InformationAcknowledgement(
    val person: String,
    val contribution: String
)

private val INFORMATION_ACKNOWLEDGEMENTS = listOf(
    InformationAcknowledgement(
        person = "FENRIR",
        contribution = "Por darme la idea de crear la app gracias a la partida FALLOUT_SOFT."
    ),
    InformationAcknowledgement(
        person = "JESÚS",
        contribution = "Por enseñarme el funcionamiento de los agentes de IA, Orca y Android Studio."
    ),
    InformationAcknowledgement(
        person = "LUIS",
        contribution = "Por ayudarme con algunos elementos estéticos."
    ),
    InformationAcknowledgement(
        person = "JAIME",
        contribution = "Por la idea inicial del sónar."
    ),
    InformationAcknowledgement(
        person = "EQUIPO DE NAVY7",
        contribution = "Por dejarme ir a probar y aportarme ideas de nuevos modos de uso."
    ),
    InformationAcknowledgement(
        person = "MI PADRE",
        contribution = "Por regalarme el Watch 2, que me dio las ideas para la baliza remota del sónar y que estoy utilizando para ello."
    ),
    InformationAcknowledgement(
        person = "CAINSHARK",
        contribution = "Por ayudarme con algunos elementos estéticos."
    )
)

private val INFORMATION_DISCLAIMERS = listOf(
    "PROPIEDAD INTELECTUAL",
    "Las imágenes, emblemas y demás elementos identificativos de la Hermandad del Acero pertenecen a Bethesda Softworks LLC y/o a sus licenciantes. SuriOS no reclama la titularidad de dichos elementos ni está afiliado, patrocinado, autorizado o respaldado por Bethesda.",
    "Determinados nombres, términos y referencias utilizados en la aplicación se inspiran en el universo FALLOUT, cuyos derechos corresponden a Bethesda Softworks LLC y/o a sus licenciantes.",
    "La interfaz de SuriOS se inspira en el concepto PIP-BOY de FALLOUT, pero constituye un desarrollo independiente y no pretende reproducir ni presentarse como una copia del producto original.",
    "DESARROLLO",
    "El código de esta aplicación se ha desarrollado con asistencia del agente de inteligencia artificial Codex. La revisión, integración, validación y responsabilidad final del código corresponden al desarrollador.",
    "El desarrollo de SuriOS se ha realizado desde cero. Los elementos externos utilizados se identifican en la documentación correspondiente, junto con el reconocimiento de sus titulares y las condiciones de uso conocidas.",
    "PRIVACIDAD",
    "El lector de huellas de la pantalla de inicio es ficticio. No se realiza ninguna lectura de huellas ni se almacenan datos personales.",
    "USO Y DISTRIBUCIÓN",
    "Esta aplicación se ha desarrollado sin fines comerciales. Cualquier uso lucrativo, distribución o explotación posterior será responsabilidad exclusiva de quien lo realice y deberá respetar la normativa aplicable y los derechos de terceros.",
    "La aplicación se distribuye para uso privado. Si se ha obtenido por un medio distinto del desarrollador, no se garantiza su autenticidad, integridad, seguridad, funcionamiento o compatibilidad. El usuario será responsable de verificar su procedencia, de los efectos de su instalación en el dispositivo y del cumplimiento de la normativa aplicable en materia de propiedad intelectual."
)

@Composable
fun InformationScreen(
    onAcknowledgementsSelected: () -> Unit,
    onDisclaimersSelected: () -> Unit,
    onTestersSelected: () -> Unit,
    onBack: () -> Unit
) {
    InformationLayout(title = "INFORMATION", onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            listOf(
                "> ACKNOWLEDGEMENTS" to onAcknowledgementsSelected,
                "> DISCLAIMERS" to onDisclaimersSelected,
                "> TESTERS" to onTestersSelected
            ).forEach { (entry, action) ->
                Text(
                    text = entry,
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = action)
                )
            }
        }
    }
}

@Composable
fun InformationTestersScreen(onBack: () -> Unit) {
    InformationLayout(title = "INFORMATION - TESTERS", onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(
                text = "> ALPHA",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "> BETA",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun InformationAcknowledgementsScreen(onBack: () -> Unit) {
    InformationLayout(title = "INFORMATION - ACKNOWLEDGEMENTS", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            INFORMATION_ACKNOWLEDGEMENTS.forEach { acknowledgement ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = acknowledgement.person,
                        color = PipGreen,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = acknowledgement.contribution,
                        color = PipGreen,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun InformationDisclaimersScreen(onBack: () -> Unit) {
    InformationLayout(title = "INFORMATION - DISCLAIMERS", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            INFORMATION_DISCLAIMERS.forEach { text ->
                Text(
                    text = text,
                    color = if (text == text.uppercase()) PipGreen else PipGreenDim,
                    fontSize = if (text == text.uppercase()) 22.sp else 17.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun InformationLayout(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = title,
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        Box(modifier = Modifier.align(Alignment.Center)) { content() }
        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.8",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}
