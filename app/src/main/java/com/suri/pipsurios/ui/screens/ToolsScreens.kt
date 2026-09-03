package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.prs.PrsOperatingMode
import kotlinx.coroutines.delay

@Composable
fun ToolsLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        LoadingGlitchText()
    }
}

@Composable
fun ProximityRadioScannerLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        LoadingGlitchText()
    }
}

@Composable
fun ProximityRadioScannerScreen(
    onSentrySelected: () -> Unit,
    onTrackerSelected: () -> Unit,
    onDevicesSelected: () -> Unit,
    onUserGuideSelected: () -> Unit,
    onBack: () -> Unit
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

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )

        Text(
            text = "PIP-SuriOS v2.9",
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
    onBack: () -> Unit
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
            Text(
                text = "> PIP + PROBE",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onPipProbeSelected)
            )
        }

        Text(
            text = "SURVEILLANCE // ALL DETECTED NODES",
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
            text = "PIP-SuriOS v2.9",
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
    onBack: () -> Unit
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
            Text(
                text = "> ${PrsOperatingMode.SCAN_PROBE.displayName}",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onScanProbeSelected)
            )
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
            text = "PIP-SuriOS v2.9",
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
    title: String = "P.R.S. v4.0"
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
                text = "> ONLY PIP-BOY",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onOnlyPipBoySelected)
            )
            Text(
                text = "> PIP-BOY + PROBE",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onPipBoyProbeSelected)
            )
        }

        Text(
            text = "STEP 1 // IDENTIFY TARGET + LOCATION",
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
            text = "PIP-SuriOS v2.9",
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
                text = "> PROXIMITY RADIO SCANNER",
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
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )

        Text(
            text = "PIP-SuriOS v2.9",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}
