package com.suri.pipsurios.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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

    BackHandler {
        if (page == PrsDevicesPage.ROOT) onBack() else page = PrsDevicesPage.ROOT
    }

    when (page) {
        PrsDevicesPage.ROOT -> PrsDevicesRootScreen(
            compact = compact,
            onIdentifySelected = { page = PrsDevicesPage.IDENTIFY },
            onSavedSelected = { page = PrsDevicesPage.SAVED },
            onMacGuideSelected = { page = PrsDevicesPage.MAC_GUIDE },
            onBack = onBack
        )

        PrsDevicesPage.IDENTIFY -> PrsIdentifyDeviceScreen(
            registry = registry,
            compact = compact,
            onBack = { page = PrsDevicesPage.ROOT }
        )

        PrsDevicesPage.SAVED -> PrsSavedDevicesScreen(
            registry = registry,
            compact = compact,
            onBack = { page = PrsDevicesPage.ROOT }
        )

        PrsDevicesPage.MAC_GUIDE -> PrsMacAddressGuideScreen(
            compact = compact,
            onBack = { page = PrsDevicesPage.ROOT }
        )
    }
}

@Composable
private fun PrsDevicesRootScreen(
    compact: Boolean,
    onIdentifySelected: () -> Unit,
    onSavedSelected: () -> Unit,
    onMacGuideSelected: () -> Unit,
    onBack: () -> Unit
) {
    val outerPadding = if (compact) 12.dp else 24.dp
    val titleSize = if (compact) 18.sp else 26.sp
    val rowSpacing = if (compact) 8.dp else 12.dp

    Box(modifier = Modifier.fillMaxSize().background(PipBlack).safeDrawingPadding()) {
        Text(
            text = "P.R.S. / DEVICES",
            color = PipGreen,
            fontSize = titleSize,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(outerPadding)
        )

        Column(
            modifier = Modifier
                .widthIn(max = 470.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = if (compact) 12.dp else 0.dp)
                .offset(y = if (compact) (-18).dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(rowSpacing)
        ) {
            Text(
                "IDENTIFICA UN CONTACTO PARA GUARDARLO Y DARLE UN NOMBRE LOCAL.",
                color = PipGreenDim,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            PrsDevicesMenuRow(
                label = "> IDENTIFY DEVICES",
                detail = "CONTACTOS BLE DEL A56 / GUARDAR + NOMBRE LOCAL",
                onClick = onIdentifySelected,
                compact = compact
            )
            PrsDevicesMenuRow(
                label = "> SAVED DEVICES",
                detail = "ACTIVAR, DESACTIVAR, RENOMBRAR O ELIMINAR REGLAS",
                onClick = onSavedSelected,
                compact = compact
            )
            PrsDevicesMenuRow(
                label = "> GUIA DE DIRECCION MAC",
                detail = "BUSCAR, COMPROBAR Y GUARDAR UNA DIRECCIÓN BLE",
                onClick = onMacGuideSelected,
                compact = compact
            )
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(outerPadding)
        )
    }
}

@Composable
private fun PrsMacAddressGuideScreen(
    compact: Boolean,
    onBack: () -> Unit
) {
    val outerPadding = if (compact) 12.dp else 24.dp
    Box(modifier = Modifier.fillMaxSize().background(PipBlack).safeDrawingPadding()) {
        Text(
            text = "P.R.S. / GUÍA DE DIRECCIÓN MAC",
            color = PipGreen,
            fontSize = if (compact) 18.sp else 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(outerPadding)
        )
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(vertical = if (compact) 48.dp else 72.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("GUIA PARA IDENTIFICAR UN DISPOSITIVO", color = PipAmber, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            Text(
                "1. Mantén el dispositivo objetivo encendido, con Bluetooth activo y cerca del A56.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "2. Abre IDENTIFY DEVICES y espera a que aparezca su anuncio BLE.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "3. Comprueba el nombre, el RSSI y el ID mostrados. El ID es la dirección BLE observada cuando Android la proporciona.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "4. Usa GUARDAR en esa fila. Puedes introducir antes un nombre local; P.R.S. guardará la dirección como regla principal.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "5. Para cambiar el nombre local más adelante, abre SAVED DEVICES y elige RENOMBRAR. La dirección BLE y la regla no cambian.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text("FORMATO MANUAL", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            Text(
                "Una dirección válida tiene 12 dígitos hexadecimales, por ejemplo AA:BB:CC:DD:EE:FF. En IDENTIFY DEVICES se aceptan dos puntos o guiones.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text("DIRECCION PRIVADA / ROTATORIA", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            Text(
                "Algunos teléfonos, relojes y accesorios BLE no muestran una MAC estable. En ese caso, guarda el nombre BLE anunciado exacto; un mismo nombre puede coincidir con más de un dispositivo físico.",
                color = PipGreenDim,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Si una regla conocida está ACTIVADA, SENTRY omite los contactos coincidentes; INDIVIDUAL TRACKER puede seleccionar el dispositivo guardado aunque esté desconectado.",
                color = PipAmber,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(outerPadding)
        )
    }
}

@Composable
private fun PrsDevicesMenuRow(
    label: String,
    detail: String,
    onClick: () -> Unit,
    compact: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipGreenDim.copy(alpha = 0.65f))
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) 10.dp else 14.dp, vertical = if (compact) 8.dp else 11.dp)
    ) {
        Text(label, color = PipGreen, fontSize = if (compact) 14.sp else 16.sp, fontFamily = FontFamily.Monospace)
        Text(detail, color = PipGreenDim, fontSize = if (compact) 9.sp else 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun PrsIdentifyDeviceScreen(
    registry: PrsDeviceRegistry,
    compact: Boolean,
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
        actionStatus = if (saved) "DISPOSITIVO GUARDADO // RENÓMBRALO EN SAVED DEVICES" else "EL DISPOSITIVO YA ESTÁ GUARDADO"
    }

    fun saveManualDevice() {
        val value = manualIdentifier.trim()
        if (value.isEmpty()) {
            actionStatus = "INTRODUCE UNA DIRECCIÓN MAC O UN NOMBRE BLE"
            return
        }
        val saved = if (PrsDeviceRegistry.normalizeAddress(value) != null) {
            registry.saveAddress(value)
        } else {
            registry.saveName(value, value)
        }
        if (saved) manualIdentifier = ""
        refreshSavedDevices()
        actionStatus = if (saved) "DISPOSITIVO GUARDADO" else "INVÁLIDO O YA GUARDADO"
    }

    val outerPadding = if (compact) 12.dp else 24.dp
    val view = LocalView.current
    val cutout = view.rootWindowInsets?.displayCutout
    val cameraOnRight = (cutout?.safeInsetRight ?: 0) > (cutout?.safeInsetLeft ?: 0)

    Box(modifier = Modifier.fillMaxSize().background(PipBlack).safeDrawingPadding()) {
        Row(
            modifier = Modifier.fillMaxSize().padding(outerPadding)
        ) {
            val infoModifier = Modifier.weight(1f).fillMaxHeight()
            val listModifier = Modifier.weight(2f).fillMaxHeight()
            if (cameraOnRight) {
                PrsIdentifyContactListPanel(
                    modifier = listModifier,
                    compact = compact,
                    liveDevices = liveDevices,
                    registry = registry,
                    onSave = ::saveObservation
                )
                PrsIdentifyInfoPanel(
                    modifier = infoModifier,
                    compact = compact,
                    scanStatus = scanStatus,
                    liveCount = liveDevices.size,
                    savedCount = savedDevices.size,
                    manualIdentifier = manualIdentifier,
                    onManualIdentifierChanged = { manualIdentifier = it },
                    onSaveManual = ::saveManualDevice,
                    actionStatus = actionStatus,
                    onBack = onBack
                )
            } else {
                PrsIdentifyInfoPanel(
                    modifier = infoModifier,
                    compact = compact,
                    scanStatus = scanStatus,
                    liveCount = liveDevices.size,
                    savedCount = savedDevices.size,
                    manualIdentifier = manualIdentifier,
                    onManualIdentifierChanged = { manualIdentifier = it },
                    onSaveManual = ::saveManualDevice,
                    actionStatus = actionStatus,
                    onBack = onBack
                )
                PrsIdentifyContactListPanel(
                    modifier = listModifier,
                    compact = compact,
                    liveDevices = liveDevices,
                    registry = registry,
                    onSave = ::saveObservation
                )
            }
        }
    }
}

@Composable
private fun PrsIdentifyInfoPanel(
    modifier: Modifier,
    compact: Boolean,
    scanStatus: BleScanStatus,
    liveCount: Int,
    savedCount: Int,
    manualIdentifier: String,
    onManualIdentifierChanged: (String) -> Unit,
    onSaveManual: () -> Unit,
    actionStatus: String?,
    onBack: () -> Unit
) {
    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    end = if (compact) 10.dp else 22.dp,
                    bottom = 60.dp
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp)
        ) {
            Text(
                text = "P.R.S. / IDENTIFY DEVICES",
                color = PipGreen,
                fontSize = if (compact) 17.sp else 23.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "ESCANEO BLE LOCAL DEL A56",
                color = PipAmber,
                fontSize = if (compact) 12.sp else 15.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "${prsDeviceScanStatusLabel(scanStatus)} // $liveCount OBSERVADOS // $savedCount GUARDADOS",
                color = if (scanStatus == BleScanStatus.SCANNING) PipGreen else PipAmber,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "WATCH 2 PROBE ES REMOTO. NO APARECE EN ESTA LISTA BLE LOCAL.",
                color = PipAmber,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "GUARDA UN CONTACTO DE LA LISTA. RENÓMBRALO DESPUÉS EN SAVED DEVICES.",
                color = PipGreenDim,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "GUARDADO MANUAL",
                color = PipAmber,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Row(
                modifier = Modifier.fillMaxWidth().border(1.dp, PipGreenDim.copy(alpha = 0.65f)).padding(7.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    if (manualIdentifier.isEmpty()) {
                        Text(
                            "MAC O NOMBRE BLE EXACTO",
                            color = PipGreenDim,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = manualIdentifier,
                        onValueChange = onManualIdentifierChanged,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = PipNeutral,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(
                    "> GUARDAR",
                    color = PipGreen,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = onSaveManual)
                )
            }
            actionStatus?.let {
                Text(it, color = PipAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }
        PrsBackButton(onBack = onBack, modifier = Modifier.align(Alignment.BottomStart))
    }
}

@Composable
private fun PrsIdentifyContactListPanel(
    modifier: Modifier,
    compact: Boolean,
    liveDevices: List<BleObservation>,
    registry: PrsDeviceRegistry,
    onSave: (BleObservation) -> Unit
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .padding(vertical = if (compact) 8.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("CONTACTOS", color = PipAmber, fontSize = if (compact) 12.sp else 15.sp, fontFamily = FontFamily.Monospace)
                Text("${liveDevices.size}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                    .padding(6.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (liveDevices.isEmpty()) {
                    Text(
                        "ESPERANDO BLE...",
                        color = PipGreenDim,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    liveDevices.forEach { observation ->
                        ObservedDeviceRow(
                            observation = observation,
                            savedDevice = registry.savedDeviceFor(observation),
                            onSave = { onSave(observation) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrsSavedDevicesScreen(
    registry: PrsDeviceRegistry,
    compact: Boolean,
    onBack: () -> Unit
) {
    var savedDevices by remember { mutableStateOf(registry.snapshot()) }
    var actionStatus by remember { mutableStateOf<String?>(null) }

    fun refreshSavedDevices() {
        savedDevices = registry.snapshot()
    }

    val outerPadding = if (compact) 12.dp else 24.dp
    Box(modifier = Modifier.fillMaxSize().background(PipBlack).safeDrawingPadding()) {
        Text(
            text = "P.R.S. / SAVED DEVICES",
            color = PipGreen,
            fontSize = if (compact) 18.sp else 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(outerPadding)
        )

        Column(
            modifier = Modifier
                .widthIn(max = 470.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(vertical = if (compact) 50.dp else 78.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SAVED DEVICES", color = PipAmber, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Text("${savedDevices.size} EN TOTAL", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Text(
                if (compact) {
                    "LAS REGLAS ACTIVADAS SE OMITEN DEL ESCANEO LOCAL."
                } else {
                    "LAS REGLAS ACTIVADAS SE OMITEN DEL ESCANEO LOCAL Y SCAN + PROBE."
                },
                color = PipGreenDim,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (savedDevices.isEmpty()) {
                    Text("NO HAY DISPOSITIVOS GUARDADOS", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
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
                            },
                            onRename = { localName ->
                                val renamed = registry.rename(device, localName)
                                refreshSavedDevices()
                                actionStatus = if (renamed) "NOMBRE LOCAL GUARDADO" else "NOMBRE LOCAL SIN CAMBIOS"
                            }
                        )
                    }
                }
            }
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(outerPadding)
        )
    }
}

@Composable
private fun ObservedDeviceRow(
    observation: BleObservation,
    savedDevice: PrsSavedDevice?,
    onSave: () -> Unit
) {
    val name = (observation.deviceName?.takeIf { it.isNotBlank() } ?: "DISPOSITIVO BLE DESCONOCIDO") + observation.categorySuffix()
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
                    savedDevice?.enabled == true -> "GUARDADO / ACTIVO"
                    savedDevice != null -> "GUARDADO / INACTIVO"
                    else -> "NO GUARDADO"
                },
                color = accent,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Text("ID ${observation.deviceIdentifier}", color = PipNeutralDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text("RSSI ${observation.rssi} dBm // ${observation.addressTypeLabel()}", color = PipNeutralDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(
            when {
                savedDevice == null -> "> GUARDAR DISPOSITIVO"
                savedDevice.enabled -> "> GUARDADO / DESACTIVAR EN ESCANEO"
                else -> "> GUARDADO / ACTIVAR EN ESCANEO"
            },
            color = PipGreen,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .clickable(enabled = savedDevice == null, onClick = onSave)
                .padding(top = 3.dp)
        )
    }
}

@Composable
private fun SavedDeviceRow(
    device: PrsSavedDevice,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onRename: (String) -> Unit
) {
    var editingName by remember(device) { mutableStateOf(false) }
    var draftName by remember(device) { mutableStateOf(device.displayName.orEmpty()) }
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
                if (device.enabled) "ACTIVADO" else "DESACTIVADO",
                color = accent,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Text("${device.type.label} // ${device.value}", color = PipNeutralDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (editingName) {
            BasicTextField(
                value = draftName,
                onValueChange = { draftName = it },
                singleLine = true,
                textStyle = TextStyle(
                    color = PipNeutral,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "> GUARDAR NOMBRE",
                    color = PipGreen,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable {
                        onRename(draftName)
                        editingName = false
                    }.padding(top = 5.dp)
                )
                Text(
                    "> CANCELAR",
                    color = PipGreenDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { editingName = false }.padding(top = 5.dp)
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                if (device.enabled) "> DESACTIVAR" else "> ACTIVAR",
                color = PipGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onToggle).padding(top = 5.dp)
            )
            Text(
                "> RENOMBRAR",
                color = PipGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    draftName = device.displayName.orEmpty()
                    editingName = true
                }.padding(top = 5.dp)
            )
            Text(
                "> ELIMINAR",
                color = PipGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onRemove).padding(top = 5.dp)
            )
        }
    }
}

private fun prsDeviceScanStatusLabel(status: BleScanStatus): String = when (status) {
    BleScanStatus.IDLE -> "EN ESPERA"
    BleScanStatus.SCANNING -> "ESCANEANDO"
    BleScanStatus.PERMISSION_REQUIRED -> "PERMISO NECESARIO"
    BleScanStatus.BLUETOOTH_OFF -> "BLUETOOTH DESACTIVADO"
    BleScanStatus.UNSUPPORTED -> "NO COMPATIBLE"
    BleScanStatus.ERROR -> "ERROR"
}
