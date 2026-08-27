package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

@Composable
fun ProximityRadioScannerGuideScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
            .padding(24.dp)
    ) {
        Text(
            text = "P.R.S. OPERATION GUIDE",
            color = PipGreen,
            fontSize = 28.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 62.dp, bottom = 42.dp),
            horizontalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            GuideColumn(
                modifier = Modifier.weight(1f),
                title = "A. PREPARE + START",
                steps = listOf(
                    GuideStep(
                        "01",
                        "CHECK EQUIPMENT",
                        "A56: Bluetooth + location + Wi-Fi",
                        "Watch 2: Bluetooth + location + Wi-Fi",
                        "Both devices on the same local network"
                    ),
                    GuideStep(
                        "02",
                        "START A56 / OPERATOR",
                        "TOOLS > PROXIMITY RADIO SCANNER",
                        "Open P.R.S. v2.0 and keep this screen open",
                        "Wait for the local gateway to start"
                    ),
                    GuideStep(
                        "03",
                        "START WATCH / PROBE",
                        "Open P.R.S. REMOTE PROBE, not only PIW",
                        "First launch: grant Bluetooth, location and local network permissions",
                        "It starts automatically: STATUS: ACTIVE / SCANNING..."
                    ),
                    GuideStep(
                        "04",
                        "LINK CHECK",
                        "Watch: LINK: CONNECTED",
                        "A56: PROBE: CONNECTED",
                        "If disconnected, check Wi-Fi before deploying"
                    ),
                    GuideStep(
                        "05",
                        "DEPLOY",
                        "A = Galaxy A56 / operator",
                        "B = Xiaomi Watch 2 / remote probe",
                        "C = BLE test device at the third position"
                    )
                )
            )

            GuideColumn(
                modifier = Modifier.weight(1f),
                title = "B. CAPTURE + FINISH",
                steps = listOf(
                    GuideStep(
                        "06",
                        "CAPTURE",
                        "Keep A56 and Watch 2 active for at least 30 seconds",
                        "Use START CLOSE SCAN, then START WIDE SCAN if required",
                        "Check REMOTE and MATCHED counters on the A56"
                    ),
                    GuideStep(
                        "07",
                        "FIELD NOTES",
                        "Record positions A / B / C and the time",
                        "Note walls, people, bags, orientation and movement",
                        "Do not interpret RSSI as metres"
                    ),
                    GuideStep(
                        "08",
                        "RECOVER + CLOSE",
                        "Recover the Watch physically",
                        "Press STOP / RETRIEVE PROBE on the Watch",
                        "A56 saves the session internally; note its session ID"
                    ),
                    GuideStep(
                        "09",
                        "P.R.S. TESTING",
                        "Use TESTING first for A56 calibration samples",
                        "Select STATIC (30 s) or MOVEMENT",
                        "Export its CSV separately, then return to P.R.S. v2.0"
                    ),
                    GuideStep(
                        "10",
                        "LIMITS",
                        "No LTE / no coordinates / no triangulation",
                        "MATCHED: 0 is not proof of absence",
                        "Private BLE addresses may prevent correlation"
                    )
                )
            )
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clickable(onClick = onBack)
                .padding(vertical = 6.dp)
        )

        Text(
            text = "PIP-SuriOS v2.2",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

private data class GuideStep(
    val number: String,
    val title: String,
    val lineOne: String,
    val lineTwo: String,
    val lineThree: String
)

@Composable
private fun GuideColumn(
    modifier: Modifier,
    title: String,
    steps: List<GuideStep>
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, color = PipAmber, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
        steps.forEach { step ->
            Row {
                Text(step.number, color = PipGreen, fontSize = 17.sp, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(step.title, color = Color.White, fontSize = 17.sp, fontFamily = FontFamily.Monospace)
                    Text("> ${step.lineOne}", color = Color.LightGray, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text("> ${step.lineTwo}", color = Color.LightGray, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text("> ${step.lineThree}", color = Color.LightGray, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
