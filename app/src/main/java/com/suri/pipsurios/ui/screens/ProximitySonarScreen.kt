package com.suri.pipsurios.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.sonar.BleScanStatus
import com.suri.pipsurios.sonar.BleScanner
import com.suri.pipsurios.sonar.ContactState
import com.suri.pipsurios.sonar.ContactTracker
import com.suri.pipsurios.sonar.ProximityCategory
import com.suri.pipsurios.sonar.SonarContact
import com.suri.pipsurios.sonar.SonarSnapshot
import com.suri.pipsurios.sonar.SonarSweepAudio
import com.suri.pipsurios.sonar.SonarTuning
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProximitySonarScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scanner = remember(context) { BleScanner(context.applicationContext) }
    val sweepAudio = remember(context) { SonarSweepAudio(context.applicationContext) }
    val tracker = remember { ContactTracker() }
    val scope = rememberCoroutineScope()
    var snapshot by remember { mutableStateOf(tracker.snapshot()) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var retryVersion by remember { mutableIntStateOf(0) }
    val lifecycleOwner = context as LifecycleOwner
    var audioActive by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
    }
    val sweepTransition = rememberInfiniteTransition(label = "SonarSweep")
    val sweepAngle by sweepTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(SonarTuning.SWEEP_DURATION_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SonarSweepAngle"
    )
    val currentContacts by rememberUpdatedState(snapshot.contacts)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
        retryVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions()) {
            permissionLauncher.launch(REQUIRED_BLUETOOTH_PERMISSIONS)
        }
    }

    DisposableEffect(permissionVersion, retryVersion) {
        scanStatus = scanner.start(
            onObservation = { observation ->
                tracker.observe(observation)
                snapshot = tracker.snapshot()
            },
            onStatusChanged = { scanStatus = it }
        )
        onDispose { scanner.stop() }
    }

    DisposableEffect(scanner) {
        onDispose { scanner.releaseSession() }
    }

    DisposableEffect(lifecycleOwner, sweepAudio) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> audioActive = true
                Lifecycle.Event.ON_STOP -> audioActive = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sweepAudio.release()
        }
    }

    LaunchedEffect(audioActive, sweepAudio) {
        if (audioActive) {
            val alertedThisSweep = mutableSetOf<String>()
            var previousAngle = sweepAngle
            sweepAudio.playSweepPulse()
            try {
                snapshotFlow { sweepAngle }.collect { currentAngle ->
                    val wrapped = currentAngle < previousAngle
                    if (wrapped) {
                        alertedThisSweep.clear()
                        sweepAudio.playSweepPulse()
                    }

                    val crossedContacts = currentContacts.filter { contact ->
                        contact.temporaryId !in alertedThisSweep &&
                            angleWasCrossed(previousAngle, currentAngle, contact.visualAngleDegrees)
                    }
                    val selectedContact = crossedContacts.firstOrNull { it.state == ContactState.NEW }
                        ?: crossedContacts.firstOrNull()
                    if (selectedContact != null) {
                        alertedThisSweep += crossedContacts.map { it.temporaryId }
                        if (selectedContact.state == ContactState.NEW) {
                            sweepAudio.playNewContact()
                        } else {
                            sweepAudio.playBackgroundContact()
                        }
                    }
                    previousAngle = currentAngle
                }
            } finally {
                sweepAudio.stop()
            }
        }
    }

    LaunchedEffect(tracker) {
        while (true) {
            delay(500)
            tracker.expire(android.os.SystemClock.elapsedRealtime())
            snapshot = tracker.snapshot()
        }
    }

    ProximitySonarContent(
        snapshot = snapshot,
        sweepAngle = sweepAngle,
        scanStatus = if (snapshot.isCalibrating) "CALIBRATING" else scanStatus.displayText(),
        onCalibrate = {
            if (scanStatus == BleScanStatus.SCANNING && !snapshot.isCalibrating) {
                tracker.startCalibration()
                snapshot = tracker.snapshot()
                scope.launch {
                    delay(SonarTuning.CALIBRATION_DURATION_MILLIS)
                    tracker.finishCalibration()
                    snapshot = tracker.snapshot()
                }
            }
        },
        onGrantPermission = {
            permissionLauncher.launch(REQUIRED_BLUETOOTH_PERMISSIONS)
        },
        onRetry = { retryVersion++ },
        onBack = onBack
    )
}

@Composable
private fun ProximitySonarContent(
    snapshot: SonarSnapshot,
    sweepAngle: Float,
    scanStatus: String,
    onCalibrate: () -> Unit,
    onGrantPermission: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "SONAR",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        SonarRadar(
            contacts = snapshot.contacts,
            sweepAngle = sweepAngle,
            modifier = Modifier.align(Alignment.Center)
        )

        SonarContactsPanel(
            snapshot = snapshot,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp)
        )

        val controlText = when (scanStatus) {
            "PERMISSION REQUIRED" -> "> GRANT PERMISSION"
            "BLUETOOTH OFF", "ERROR" -> "> RETRY"
            else -> if (snapshot.isCalibrating) "> CALIBRATING..." else "> CALIBRATE"
        }
        val controlAction = when (scanStatus) {
            "PERMISSION REQUIRED" -> onGrantPermission
            "BLUETOOTH OFF", "ERROR" -> onRetry
            else -> onCalibrate
        }
        SonarScanPanel(
            snapshot = snapshot,
            scanStatus = scanStatus,
            controlText = controlText,
            onControl = controlAction,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
        )

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.1",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun SonarRadar(
    contacts: List<SonarContact>,
    sweepAngle: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(300.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension * 0.46f

            val proximityRings = listOf(
                ProximityCategory.VERY_CLOSE,
                ProximityCategory.CLOSE,
                ProximityCategory.MEDIUM,
                ProximityCategory.FAR
            )
            proximityRings.forEach { proximity ->
                drawCircle(
                    color = PipGreenDim,
                    radius = maxRadius * proximity.radiusFraction(),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
            drawLine(
                color = PipGreenDim,
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = PipGreenDim,
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.dp.toPx()
            )

            val sweepRadians = Math.toRadians(sweepAngle.toDouble())
            drawLine(
                color = PipGreen.copy(alpha = 0.75f),
                start = center,
                end = Offset(
                    center.x + cos(sweepRadians).toFloat() * maxRadius,
                    center.y + sin(sweepRadians).toFloat() * maxRadius
                ),
                strokeWidth = 2.dp.toPx()
            )

            contacts.forEach { contact ->
                val radius = maxRadius * contact.proximity.radiusFraction()
                val angleRadians = Math.toRadians(contact.visualAngleDegrees.toDouble())
                val point = Offset(
                    center.x + cos(angleRadians).toFloat() * radius,
                    center.y + sin(angleRadians).toFloat() * radius
                )
                drawCircle(
                    color = if (contact.state == ContactState.NEW) PipAmber else PipGreenDim,
                    radius = if (contact.state == ContactState.NEW) 6.dp.toPx() else 4.dp.toPx(),
                    center = point
                )
            }

            drawCircle(PipGreen, radius = 7.dp.toPx(), center = center)
        }

        Text(
            text = "SURI-14",
            color = PipGreen,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center).padding(top = 38.dp)
        )

        ProximityCategory.entries.forEach { proximity ->
            val radius = 138.dp * proximity.radiusFraction()
            Text(
                text = proximity.displayLabel(),
                color = PipGreenDim.copy(alpha = 0.78f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = radius * 2f, start = 10.dp)
            )
        }
    }
}

@Composable
private fun SonarContactsPanel(
    snapshot: SonarSnapshot,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(210.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("CONTACTS", color = PipGreenDim, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            SonarValue("CURRENT", snapshot.contacts.size.toString())
            SonarValue("NEW", snapshot.newContactCount.toString(), PipAmber)
        }
        ProximityCategory.entries.forEach { proximity ->
            SonarValue(proximity.displayLabel(), snapshot.contactCount(proximity).toString())
        }
    }
}

@Composable
private fun SonarScanPanel(
    snapshot: SonarSnapshot,
    scanStatus: String,
    controlText: String,
    onControl: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(240.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("SCAN", color = PipGreenDim, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        SonarValue("SCAN STATUS", scanStatus)
        SonarValue("BASELINE", if (snapshot.hasBaseline) "READY" else "PENDING")
        SonarAction(
            text = controlText,
            onClick = onControl,
            enabled = when (scanStatus) {
                "PERMISSION REQUIRED", "BLUETOOTH OFF", "ERROR" -> true
                else -> scanStatus == "SCANNING" && !snapshot.isCalibrating
            }
        )
    }
}

@Composable
private fun SonarValue(label: String, value: String, valueColor: Color = PipGreen) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(label, color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = valueColor, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun SonarAction(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = if (enabled) PipGreen else PipGreenDim,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        modifier = if (enabled) modifier.clickable(onClick = onClick) else modifier
    )
}

private fun BleScanStatus.displayText(): String = name.replace('_', ' ')

private fun ProximityCategory.radiusFraction(): Float = when (this) {
    ProximityCategory.VERY_CLOSE -> SonarTuning.VERY_CLOSE_RADIUS_FRACTION
    ProximityCategory.CLOSE -> SonarTuning.CLOSE_RADIUS_FRACTION
    ProximityCategory.MEDIUM -> SonarTuning.MEDIUM_RADIUS_FRACTION
    ProximityCategory.FAR -> SonarTuning.FAR_RADIUS_FRACTION
}

private fun ProximityCategory.displayLabel(): String = name.replace('_', ' ')

private fun angleWasCrossed(previous: Float, current: Float, target: Float): Boolean =
    if (current >= previous) {
        target > previous && target <= current
    } else {
        target > previous || target <= current
    }

private val REQUIRED_BLUETOOTH_PERMISSIONS = arrayOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT
)
