package com.suri.pipsurios.ui.screens

import com.suri.pipsurios.PipSuriOsVersion
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.prs.PrsOperatingMode
import com.suri.pipsurios.prs.PrsWatch2Role
import kotlinx.coroutines.delay

@Composable
fun ToolsLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    TerminalScreen {
        LoadingGlitchText(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ProximityRadioScannerLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    TerminalScreen {
        LoadingGlitchText(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ProximityRadioScannerScreen(
    onSentrySelected: () -> Unit,
    onTrackerSelected: () -> Unit,
    onProbeSelected: () -> Unit,
    onDevicesSelected: () -> Unit,
    onUserGuideSelected: () -> Unit,
    onBack: () -> Unit,
    watch2Role: PrsWatch2Role = PrsWatch2Role.REMOTE_BEACON
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S.",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "> SENTRY",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onSentrySelected)
            )
            Text(
                text = "> TRACKER",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onTrackerSelected)
            )
            Text(
                text = "> PROBE",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onProbeSelected)
            )
            Text(
                text = "> DEVICES",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onDevicesSelected)
            )
            Text(
                text = "> USER GUIDE",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onUserGuideSelected)
            )
        }

        Text(
            text = "WATCH 2 // MODO: ${watch2Role.displayName}",
            color = PipGreenDim,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center).padding(top = 260.dp)
        )

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )

        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun ProximityRadioScannerProbeScreen(
    selectedRole: PrsWatch2Role,
    onRemoteProbeSelected: () -> Unit,
    onLocalDeviceSelected: () -> Unit,
    onBack: () -> Unit,
    probeAvailable: Boolean = true
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. / PROBE",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (selectedRole == PrsWatch2Role.REMOTE_BEACON) {
                    "> WATCH 2 // BALIZA REMOTA [SELECCIONADA]"
                } else {
                    "> WATCH 2 // BALIZA REMOTA"
                },
                color = if (probeAvailable) PipGreen else PipGreenDim,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(enabled = probeAvailable, onClick = onRemoteProbeSelected)
            )
            Text(
                text = if (selectedRole == PrsWatch2Role.LOCAL_DEVICE) {
                    "> WATCH 2 // DISPOSITIVO LOCAL [SELECCIONADO]"
                } else {
                    "> WATCH 2 // DISPOSITIVO LOCAL"
                },
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onLocalDeviceSelected)
            )
        }

        Text(
            text = if (probeAvailable) {
                "SELECCIONA UN MODO // REMOTA = PROBE // LOCAL = CONTACTO BLE"
            } else {
                "BALIZA REMOTA NO DISPONIBLE // CONTACTO BLE LOCAL DISPONIBLE"
            },
            color = if (probeAvailable) PipGreenDim else PipAmber,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center).padding(top = 150.dp)
        )

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )

        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun ProximityRadioScannerSentryScreen(
    onPipSelected: () -> Unit,
    onPipProbeSelected: () -> Unit,
    onBack: () -> Unit,
    showProbe: Boolean = true
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. / SENTRY",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "> PIP",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onPipSelected)
            )
            if (showProbe) {
                Text(
                    text = "> PIP + PROBE",
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = onPipProbeSelected)
                )
            }
        }

        Text(
            text = "VIGILANCIA // TODOS LOS NODOS DETECTADOS",
            color = PipGreenDim,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center).padding(top = 150.dp)
        )

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )

        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun ProximityRadioScannerV3Screen(
    onLocalScanSelected: () -> Unit,
    onScanProbeSelected: () -> Unit,
    onDevicesSelected: () -> Unit,
    onIndividualTrackerSelected: () -> Unit,
    onGuideSelected: () -> Unit,
    onBack: () -> Unit,
    showProbe: Boolean = true
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. v3.0",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "> ${PrsOperatingMode.LOCAL_SCAN.displayName}",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onLocalScanSelected)
            )
            if (showProbe) {
                Text(
                    text = "> ${PrsOperatingMode.SCAN_PROBE.displayName}",
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = onScanProbeSelected)
                )
            }
            Text(
                text = "> DEVICES",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onDevicesSelected)
            )
            Text(
                text = "> INDIVIDUAL TRACKER",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onIndividualTrackerSelected)
            )
            Text(
                text = "> OPERATION GUIDE",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onGuideSelected)
            )
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )

        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun ProximityRadioScannerV4Screen(
    onOnlyPipBoySelected: () -> Unit,
    onPipBoyProbeSelected: () -> Unit,
    onBack: () -> Unit,
    title: String = "P.R.S. v4.0",
    showProbe: Boolean = true
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = title,
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "> PIP",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onOnlyPipBoySelected)
            )
            if (showProbe) {
                Text(
                    text = "> PIP + PROBE",
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = onPipBoyProbeSelected)
                )
            }
        }

        Text(
            text = "PASO 1 // IDENTIFICAR OBJETIVO + UBICACIÓN",
            color = PipGreenDim,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center).padding(top = 150.dp)
        )

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )

        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun ToolsScreen(
    onMapSelected: () -> Unit,
    onCommsSelected: () -> Unit,
    onGeigerCounterSelected: () -> Unit,
    onProximityRadioScannerSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "TOOLS",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "> COMMS",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onCommsSelected)
            )
            Text(
                text = "> MAP",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onMapSelected)
            )
            Text(
                text = "> P.R.S.",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onProximityRadioScannerSelected)
            )
            Text(
                text = "> RADS",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onGeigerCounterSelected)
            )
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).navigationBarsPadding().padding(24.dp).clickable(onClick = onBack)
        )

        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}
