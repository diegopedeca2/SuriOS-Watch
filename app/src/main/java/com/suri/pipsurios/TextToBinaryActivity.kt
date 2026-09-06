package com.suri.pipsurios

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.binary.BinaryCodec
import com.suri.pipsurios.binary.BinaryTransmitter
import com.suri.pipsurios.ui.screens.TerminalFooter
import com.suri.pipsurios.ui.screens.TerminalOverlay
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TextToBinaryActivity : ComponentActivity() {
    private lateinit var transmitter: BinaryTransmitter
    private var transmissionJob: Job? = null
    private var cameraPermissionGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        transmitter = BinaryTransmitter(applicationContext)
        cameraPermissionGranted = transmitter.isAvailable
        if (!cameraPermissionGranted && transmitter.hasFlash) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }
        setContent {
            PIPSuriOSTheme {
                TerminalOverlay {
                    TextToBinaryScreen(
                        transmitter = transmitter,
                        flashAvailable = cameraPermissionGranted && transmitter.isAvailable,
                        currentJob = { transmissionJob },
                        setJob = { transmissionJob = it },
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            cameraPermissionGranted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onStop() {
        stopTransmission()
        super.onStop()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

    override fun onDestroy() {
        stopTransmission()
        super.onDestroy()
    }

    private fun stopTransmission() {
        transmissionJob?.cancel()
        transmissionJob = null
        if (::transmitter.isInitialized) transmitter.turnOff()
    }

    private fun hideStatusBar() {
        window.decorView.windowInsetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.statusBars())
        }
    }

    private companion object {
        const val CAMERA_PERMISSION_REQUEST = 53
    }
}

@Composable
private fun TextToBinaryScreen(
    transmitter: BinaryTransmitter,
    flashAvailable: Boolean,
    currentJob: () -> Job?,
    setJob: (Job?) -> Unit,
    onBack: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var signal by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("READY") }
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    fun convert() {
        signal = BinaryCodec.encode(input)
        status = "READY"
        keyboard?.hide()
    }

    fun stop() {
        currentJob()?.cancel()
        transmitter.turnOff()
        status = "READY"
    }

    DisposableEffect(Unit) {
        onDispose { stop() }
    }

    Box(Modifier.fillMaxSize().background(PipBlack).safeDrawingPadding()) {
        Text(
            "COMMS // BINARY TERMINAL",
            color = PipGreen,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 72.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("TEXT > BINARY", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text("INPUT", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            TextField(
                value = input,
                onValueChange = { value ->
                    input = value.uppercase().filter { it == ' ' || it in 'A'..'Z' || it in '0'..'9' }
                    signal = null
                },
                modifier = Modifier.fillMaxWidth().border(1.dp, PipGreen),
                textStyle = TextStyle(color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { convert() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PipBlack,
                    unfocusedContainerColor = PipBlack,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = PipGreen
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BinaryAction(
                    label = "DELETE",
                    color = if (input.isNotEmpty() && status != "TRANSMITTING") PipGreen else PipGreenDim,
                    enabled = input.isNotEmpty() && status != "TRANSMITTING"
                ) {
                    input = input.dropLast(1)
                    signal = null
                    status = "READY"
                }
                BinaryAction("CLEAR", PipGreen) {
                    stop()
                    input = ""
                    signal = null
                    status = "READY"
                }
            }
            signal?.let { encoded ->
                Text("SIGNAL // 8-BIT", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
                Text(
                    encoded.ifEmpty { "_" },
                    color = PipGreen,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                Text("STATUS", color = PipGreenDim, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
                Text(status, color = if (status == "ERROR") PipRed else PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
                if (status == "TRANSMITTING") {
                    BinaryAction("STOP", PipRed, onClick = ::stop)
                } else {
                    BinaryAction(
                        label = if (flashAvailable) "TRANSMIT // FLASH" else "FLASH UNAVAILABLE",
                        color = if (flashAvailable) PipGreen else PipGreenDim,
                        enabled = flashAvailable && encoded.isNotEmpty()
                    ) {
                        if (currentJob()?.isActive == true) return@BinaryAction
                        status = "TRANSMITTING"
                        val job = scope.launch {
                            try {
                                transmitter.transmit(encoded)
                                status = "READY"
                            } catch (_: CancellationException) {
                                status = "READY"
                            } catch (_: Exception) {
                                transmitter.turnOff()
                                status = "ERROR"
                            } finally {
                                setJob(null)
                            }
                        }
                        setJob(job)
                    }
                }
            }
        }
        TerminalFooter(onBack)
    }
}

@Composable
private fun BinaryAction(
    label: String,
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Text(
        label,
        color = color,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.border(1.dp, color)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}
