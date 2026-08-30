package com.suri.pipsurios.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.prs.BleObservation
import com.suri.pipsurios.prs.BleScanStatus
import com.suri.pipsurios.prs.BleScanner
import com.suri.pipsurios.prs.PrsDeviceRegistry
import com.suri.pipsurios.prs.PrsSavedDevice
import com.suri.pipsurios.prs.addressTypeLabel
import com.suri.pipsurios.prs.categorySuffix
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipNeutral
import com.suri.pipsurios.ui.theme.PipNeutralDim
import java.util.Locale

private enum class PrsDevicesPage {
    ROOT,
    IDENTIFY,
    SAVED,
    MAC_GUIDE
}

@Composable
fun PrsDevicesScreen(
    onBack: () -> Unit,
    compact: Boolean = false
) {
    val context = LocalContext.current
    val registry = remember(context) { PrsDeviceRegistry.from(context.applicationContext) }
    var page by remember { mutableStateOf(PrsDevicesPage.ROOT) }

    when (page) {
        PrsDevicesPage.ROOT -> PrsDevicesRootScreen(
            onIdentifySelected = { page = PrsDevicesPage.IDENTIFY },
            onSavedSelected = { page = PrsDevicesPage.SAVED },
            onMacGuideSelected = { page = PrsDevicesPage.MAC_GUIDE },
            onBack = onBack
        )

        PrsDevicesPage.IDENTIFY -> PrsIdentifyDeviceScreen(
            registry = registry,
            onBack = { page = PrsDevicesPage.ROOT }
        )

        PrsDevicesPage.SAVED -> PrsSavedDevicesScreen(
            registry = registry,
            compact = compact,
            onBack = { page = PrsDevicesPage.ROOT }
        )

        PrsDevicesPage.MAC_GUIDE -> PrsMacAddressGuideScreen(
            onBack = { page = PrsDevicesPage.ROOT }
        )
    }
}

@Composable
private fun PrsDevicesRootScreen(
    onIdentifySelected: () -> Unit,
    onSavedSelected: () -> Unit,
    onMacGuideSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. / DEVICES",
            color = PipGreen,
            fontSize = 26.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

            Column(
                modifier = Modifier
                .widthIn(max = 470.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "DEVICE FILTERS",
                color = PipAmber,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "IDENTIFY A CONTACT TO SAVE IT, THEN TOGGLE ITS RULE WHEN NEEDED.",
                color = PipGreenDim,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            PrsDevicesMenuRow(
                label = "> IDENTIFY DEVICE",
                detail = "LIVE BLE CONTACTS / SAVE ONE DEVICE",
                onClick = onIdentifySelected
            )
            PrsDevicesMenuRow(
                label = "> SAVED DEVICES",
                detail = "ENABLE, DISABLE OR REMOVE RULES",
                onClick = onSavedSelected
            )
            PrsDevicesMenuRow(
                label = "> MAC ADDRESS GUIDE",
                detail = "FIND, VERIFY AND SAVE A BLE ADDRESS",
                onClick = onMacGuideSelected
            )
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
    }
}

@Composable
private fun PrsMacAddressGuideScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. / MAC ADDRESS GUIDE",
            color = PipGreen,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(vertical = 72.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("HOW TO IDENTIFY A DEVICE", color = PipAmber, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            Text(
                "1. Keep the target device powered on, Bluetooth enabled and close to the A56.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "2. Open IDENTIFY DEVICE and wait for its BLE advertisement to appear.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "3. Verify the device name, RSSI and ID shown in the row. The ID is the observed BLE address when Android exposes one.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "4. Use SAVE DEVICE on that row. P.R.S. stores the address as the primary known-device rule.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text("MANUAL FORMAT", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            Text(
                "A valid address has 12 hexadecimal digits, for example AA:BB:CC:DD:EE:FF. Colons or hyphens are accepted in IDENTIFY DEVICE.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text("PRIVATE / ROTATING ADDRESS", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            Text(
                "Some phones, watches and BLE accessories do not expose a stable MAC. In that case save the exact advertised BLE name instead; a name can match more than one physical device.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "If a known rule is ENABLED, P.R.S. omits matching contacts. Set it to DISABLED in SAVED DEVICES before selecting it as an INDIVIDUAL TRACKER target.",
                color = PipAmber,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
    }
}

@Composable
private fun PrsDevicesMenuRow(
    label: String,
    detail: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipGreenDim.copy(alpha = 0.65f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Text(label, color = PipGreen, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        Text(detail, color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun PrsIdentifyDeviceScreen(
    registry: PrsDeviceRegistry,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scanner = remember(context) { BleScanner(context.applicationContext) }
    var liveDevices by remember { mutableStateOf(emptyList<BleObservation>()) }
    var savedDevices by remember { mutableStateOf(registry.snapshot()) }
    var manualIdentifier by remember { mutableStateOf("") }
    var actionStatus by remember { mutableStateOf<String?>(null) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var permissionVersion by remember { mutableIntStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions()) permissionLauncher.launch(prsPermissions())
    }

    DisposableEffect(permissionVersion) {
        scanStatus = scanner.start(
            onObservation = { observation ->
                val key = observation.deviceIdentifier.uppercase(Locale.US)
                liveDevices = (liveDevices
                    .filterNot { it.deviceIdentifier.uppercase(Locale.US) == key } + observation)
                    .sortedWith(
                        compareBy(
                            { it.deviceName?.uppercase(Locale.US) ?: "ZZZ" },
                            { it.deviceIdentifier }
                        )
                    )
                    .take(40)
            },
            onStatusChanged = { scanStatus = it }
        )
        onDispose { scanner.releaseSession() }
    }

    fun refreshSavedDevices() {
        savedDevices = registry.snapshot()
    }

    fun saveObservation(observation: BleObservation) {
        val saved = if (PrsDeviceRegistry.normalizeAddress(observation.deviceIdentifier) != null) {
            registry.saveAddress(observation.deviceIdentifier, observation.deviceName)
        } else {
            observation.deviceName?.let { registry.saveName(it, it) } ?: false
        }
        refreshSavedDevices()
        actionStatus = if (saved) "DEVICE SAVED" else "DEVICE ALREADY SAVED"
    }

    fun saveManualDevice() {
        val value = manualIdentifier.trim()
        if (value.isEmpty()) {
            actionStatus = "ENTER A MAC ADDRESS OR BLE NAME"
            return
        }
        val saved = if (PrsDeviceRegistry.normalizeAddress(value) != null) {
            registry.saveAddress(value)
        } else {
            registry.saveName(value, value)
        }
        if (saved) manualIdentifier = ""
        refreshSavedDevices()
        actionStatus = if (saved) "DEVICE SAVED" else "INVALID OR ALREADY SAVED"
    }

    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. / IDENTIFY DEVICE",
            color = PipGreen,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier
                .widthIn(max = 470.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(vertical = 72.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("LIVE BLE CONTACTS", color = PipAmber, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Text("${savedDevices.size} SAVED", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Text(
                "${scanStatus.name.replace('_', ' ')} // ${liveDevices.size} OBSERVED",
                color = if (scanStatus == BleScanStatus.SCANNING) PipGreen else PipAmber,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "ADDRESS IS PRIMARY. NAME IS FALLBACK FOR PRIVATE / ROTATING ADDRESSES.",
                color = PipGreenDim,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.65f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    if (manualIdentifier.isEmpty()) {
                        Text(
                            "MAC ADDRESS OR EXACT BLE NAME",
                            color = PipGreenDim,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = manualIdentifier,
                        onValueChange = { manualIdentifier = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = PipNeutral,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(
                    "> SAVE",
                    color = PipGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = ::saveManualDevice)
                )
            }

            actionStatus?.let {
                Text(it, color = PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                if (liveDevices.isEmpty()) {
                    Text(
                        "WAITING FOR BLE ADVERTISEMENTS...",
                        color = PipGreenDim,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    liveDevices.forEach { observation ->
                        ObservedDeviceRow(
                            observation = observation,
                            savedDevice = registry.savedDeviceFor(observation),
                            onSave = { saveObservation(observation) }
                        )
                    }
                }
            }
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
    }
}

@Composable
private fun PrsSavedDevicesScreen(
    registry: PrsDeviceRegistry,
    compact: Boolean,
    onBack: () -> Unit
) {
    var savedDevices by remember { mutableStateOf(registry.snapshot()) }

    fun refreshSavedDevices() {
        savedDevices = registry.snapshot()
    }

    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "P.R.S. / SAVED DEVICES",
            color = PipGreen,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier
                .widthIn(max = 470.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(vertical = 78.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SAVED DEVICES", color = PipAmber, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Text("${savedDevices.size} TOTAL", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Text(
                if (compact) {
                    "ENABLED RULES ARE OMITTED FROM LOCAL SCAN."
                } else {
                    "ENABLED RULES ARE OMITTED FROM LOCAL SCAN AND SCAN + PROBE."
                },
                color = PipGreenDim,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (savedDevices.isEmpty()) {
                    Text("NO SAVED DEVICES", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                } else {
                    savedDevices.forEach { device ->
                        SavedDeviceRow(
                            device = device,
                            onToggle = {
                                registry.setEnabled(device, !device.enabled)
                                refreshSavedDevices()
                            },
                            onRemove = {
                                registry.remove(device)
                                refreshSavedDevices()
                            }
                        )
                    }
                }
            }
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
    }
}

@Composable
private fun ObservedDeviceRow(
    observation: BleObservation,
    savedDevice: PrsSavedDevice?,
    onSave: () -> Unit
) {
    val name = (observation.deviceName?.takeIf { it.isNotBlank() } ?: "UNKNOWN BLE DEVICE") + observation.categorySuffix()
    val accent = when {
        savedDevice?.enabled == true -> PipAmber
        savedDevice != null -> PipGreenDim
        else -> PipGreenDim
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = 0.6f))
            .padding(7.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = accent, fontSize = 12.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Text(
                when {
                    savedDevice?.enabled == true -> "SAVED / ON"
                    savedDevice != null -> "SAVED / OFF"
                    else -> "UNSAVED"
                },
                color = accent,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Text("ID ${observation.deviceIdentifier} // ${observation.addressTypeLabel()}", color = PipNeutralDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text("RAW ${observation.rssi}   ADV ${observation.advertisingDataHex?.take(24) ?: "--"}", color = PipNeutralDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            when {
                savedDevice == null -> "> SAVE DEVICE"
                savedDevice.enabled -> "> SAVED / DISABLE IN LIST"
                else -> "> SAVED / ENABLE IN LIST"
            },
            color = PipGreen,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.clickable(enabled = savedDevice == null, onClick = onSave).padding(top = 3.dp)
        )
    }
}

@Composable
private fun SavedDeviceRow(
    device: PrsSavedDevice,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val accent = if (device.enabled) PipAmber else PipGreenDim
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = 0.65f))
            .padding(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                device.displayName ?: device.value,
                color = accent,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                if (device.enabled) "ENABLED" else "DISABLED",
                color = accent,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Text("${device.type.label} // ${device.value}", color = PipNeutralDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                if (device.enabled) "> DISABLE" else "> ENABLE",
                color = PipGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onToggle).padding(top = 5.dp)
            )
            Text(
                "> REMOVE",
                color = PipGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onRemove).padding(top = 5.dp)
            )
        }
    }
}
