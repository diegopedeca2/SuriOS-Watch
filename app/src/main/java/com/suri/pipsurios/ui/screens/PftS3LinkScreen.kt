package com.suri.pipsurios.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suri.pipsurios.detector.RadarPacketCodec
import com.suri.pipsurios.pft.PftS3BleClient
import com.suri.pipsurios.pft.PftS3ConnectionState
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed

private val PFT_BLE_PERMISSIONS = arrayOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.ACCESS_FINE_LOCATION
)

@Composable
fun PftS3LinkScreen(onBack: () -> Unit, onDetectorSelected: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val client = remember(context) { PftS3BleClient(context) }
    val status by client.status.collectAsStateWithLifecycle()
    val messages = remember { mutableStateListOf<String>() }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var lastAction by remember { mutableStateOf<String?>(null) }
    var detectorSummary by remember { mutableStateOf("RADAR: --") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionVersion++ }

    LaunchedEffect(permissionVersion) {
        if (client.hasRequiredPermissions()) {
            client.start()
        }
    }

    LaunchedEffect(Unit) {
        if (!client.hasRequiredPermissions()) {
            permissionLauncher.launch(PFT_BLE_PERMISSIONS)
        }
    }

    LaunchedEffect(client) {
        client.messages.collect { message ->
            messages.add(message)
            while (messages.size > 6) messages.removeAt(0)
        }
    }

    LaunchedEffect(client) {
        client.packets.collect { bytes ->
            val packet = runCatching { RadarPacketCodec.decode(bytes) }.getOrNull()
            if (packet != null) {
                val active = packet.targets.filter { it.visible }
                val targets = active.joinToString(" ") {
                    "T${it.slot}=(${it.xMm},${it.yMm})"
                }
                detectorSummary = "RADAR seq=${packet.sequence} targets=${active.size}" +
                    if (targets.isNotEmpty()) " $targets" else ""
            }
        }
    }

    DisposableEffect(client) {
        onDispose { client.close() }
    }

    val statusColor = when (status.state) {
        PftS3ConnectionState.CONNECTED -> PipGreen
        PftS3ConnectionState.ERROR -> PipRed
        else -> PipAmber
    }

    fun send(command: String) {
        lastAction = if (client.sendCommand(command)) {
            "TX $command"
        } else {
            "TX FAILED $command"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("PFT LINK // S3", color = PipGreen, fontSize = 28.sp, fontFamily = FontFamily.Monospace)
        Text(
            "${status.state.name} ${status.detail.orEmpty()}",
            color = statusColor,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )

        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            LinkCommand("PING") { send("PING") }
            LinkCommand("STATUS") { send("GET,STATUS") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            LinkCommand("DETECTOR") { send("TOOL,DETECTOR") }
            LinkCommand("MAP") { send("TOOL,MAP") }
            LinkCommand("P.R.S.") { send("TOOL,PRS") }
        }
        LinkCommand("OPEN DETECTOR // S3", onClick = onDetectorSelected)

        Text("LAST: ${lastAction.orEmpty()}", color = PipGreenDim, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text(detectorSummary, color = PipGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text("EVENTS", color = PipAmber, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
        messages.forEach { message ->
            Text(message, color = PipGreenDim, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        }

        Text(
            "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable(onClick = onBack)
        )
    }
}

@Composable
private fun LinkCommand(label: String, onClick: () -> Unit) {
    Text(
        text = "> $label",
        color = PipGreen,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.clickable(onClick = onClick)
    )
}
