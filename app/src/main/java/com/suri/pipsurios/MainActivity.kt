package com.suri.pipsurios

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.view.KeyEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.suri.pipsurios.ui.theme.PipGreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import com.suri.pipsurios.BuildConfig
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.screens.HomeOperationScreen
import com.suri.pipsurios.InformationActivity
import com.suri.pipsurios.ui.screens.ToolsLoadingScreen
import com.suri.pipsurios.ui.screens.ToolsScreen
import com.suri.pipsurios.ui.screens.ProximityRadioScannerLoadingScreen
import com.suri.pipsurios.ui.screens.ProximityRadioScannerScreen
import com.suri.pipsurios.ui.screens.ProximityRadioScannerSentryScreen
import com.suri.pipsurios.ui.screens.ProximityRadioScannerV3Screen
import com.suri.pipsurios.ui.screens.ProximityRadioScannerV4Screen
import com.suri.pipsurios.ui.screens.ProximityRadioScannerGuideScreen
import com.suri.pipsurios.ui.screens.PrsUserGuideScreen
import com.suri.pipsurios.ui.screens.PrsDevicesScreen
import com.suri.pipsurios.ui.screens.PrsTrackingScreen
import com.suri.pipsurios.ui.screens.IndividualTrackingMenuScreen
import com.suri.pipsurios.ui.screens.IndividualTrackingTargetScreen
import com.suri.pipsurios.ui.screens.IndividualTrackingTrackerScreen
import com.suri.pipsurios.individualtracking.IndividualTrackingSelection
import com.suri.pipsurios.ui.screens.PrsOnlyApp
import com.suri.pipsurios.prs.PrsOperatingMode
import com.suri.pipsurios.prs.PrsV4Mode
import com.suri.pipsurios.ui.screens.GeigerCounterLoadingScreen
import com.suri.pipsurios.ui.screens.GeigerCounterScreen
import com.suri.pipsurios.ui.screens.DataLoadingScreen
import com.suri.pipsurios.ui.screens.DataSavedScreen
import com.suri.pipsurios.ui.screens.DataDeletedScreen
import com.suri.pipsurios.ui.screens.DataScreen
import com.suri.pipsurios.ui.screens.DataStatisticsScreen
import com.suri.pipsurios.ui.screens.PrimaryWeaponStatisticsScreen
import com.suri.pipsurios.ui.screens.SecondaryWeaponStatisticsScreen
import com.suri.pipsurios.ui.screens.LocationStatisticsScreen
import com.suri.pipsurios.ui.screens.HeadgearStatisticsScreen
import com.suri.pipsurios.ui.screens.UniformStatisticsScreen
import com.suri.pipsurios.ui.screens.OperationEditLoadoutScreen
import com.suri.pipsurios.ui.screens.OperationEditConfirmScreen
import com.suri.pipsurios.ui.screens.DataModifiedScreen
import com.suri.pipsurios.ui.screens.DataLogDetailScreen
import com.suri.pipsurios.ui.screens.DataLogScreen
import com.suri.pipsurios.ui.screens.OperationLoadoutScreen
import com.suri.pipsurios.ui.screens.OperationConfirmScreen
import com.suri.pipsurios.data.OperationConsumables
import com.suri.pipsurios.data.OperationDraft
import com.suri.pipsurios.data.OperationEditDraft
import com.suri.pipsurios.data.OperationInputValidator
import com.suri.pipsurios.data.OperationLoadoutSnapshot
import com.suri.pipsurios.data.OperationLog
import com.suri.pipsurios.data.OperationLogEntry
import com.suri.pipsurios.data.OperationRepository
import com.suri.pipsurios.data.DeleteOperationResult
import com.suri.pipsurios.data.UpdateOperationResult
import com.suri.pipsurios.data.PercentageDistribution
import com.suri.pipsurios.data.SaveOperationResult
import com.suri.pipsurios.data.LoadoutConfigurationRepository
import com.suri.pipsurios.data.OperatorField
import com.suri.pipsurios.data.OperatorProfileRepository
import com.suri.pipsurios.data.StatisticsCalculator
import com.suri.pipsurios.geiger.VolumeKeyController
import com.suri.pipsurios.ui.screens.InventoryCategoryScreen
import com.suri.pipsurios.ui.screens.InventoryDetailsScreen
import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.InventoryLoadingScreen
import com.suri.pipsurios.ui.screens.InventoryModeSelectionScreen
import com.suri.pipsurios.ui.screens.InventoryScreen
import com.suri.pipsurios.ui.screens.InventoryVisualMenuScreen
import com.suri.pipsurios.ui.screens.StorageGroupScreen
import com.suri.pipsurios.ui.screens.StorageItemScreen
import com.suri.pipsurios.storage.StorageCatalog
import com.suri.pipsurios.storage.StorageCalculator
import com.suri.pipsurios.storage.StorageItem
import com.suri.pipsurios.storage.StorageRepository
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
import com.suri.pipsurios.ui.screens.WeaponReplicaCatalog
import com.suri.pipsurios.ui.screens.HeadgearCatalog
import com.suri.pipsurios.ui.screens.CurrentGearLoadingScreen
import com.suri.pipsurios.ui.screens.CurrentGearScreen
import com.suri.pipsurios.ui.screens.PrimaryWeaponScreen
import com.suri.pipsurios.ui.screens.SecondaryWeaponScreen
import com.suri.pipsurios.ui.screens.AccesoriesScreen
import com.suri.pipsurios.ui.screens.HeadgearScreen
import com.suri.pipsurios.ui.screens.FrontPanelScreen
import com.suri.pipsurios.ui.screens.UniformScreen
import com.suri.pipsurios.ui.screens.StatusLoadingScreen
import com.suri.pipsurios.ui.screens.StatusScreen
import com.suri.pipsurios.ui.screens.StatusAccessoriesScreen
import com.suri.pipsurios.ui.screens.ComplementsScreen
import com.suri.pipsurios.ui.screens.DontForgetScreen
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import com.suri.pipsurios.ui.screens.LoadingScreen
import com.suri.pipsurios.ui.screens.CivTakLoadingScreen
import com.suri.pipsurios.ui.screens.CommsLoadingScreen
import com.suri.pipsurios.ui.screens.CommsScreen
import com.suri.pipsurios.ui.screens.CommsModeSelectionScreen
import com.suri.pipsurios.ui.screens.MorseModeSelectionScreen
import com.suri.pipsurios.ui.screens.MorseToTextInputScreen
import com.suri.pipsurios.ui.screens.MorseToTextOutputScreen
import com.suri.pipsurios.ui.screens.GoogleMapsLoadingScreen
import com.suri.pipsurios.ui.screens.MapLoadingScreen
import com.suri.pipsurios.ui.screens.MapModeSelectionScreen
import com.suri.pipsurios.ui.screens.MapOperationScreen
import com.suri.pipsurios.ui.screens.MapTerrainScreen
import com.suri.pipsurios.ui.screens.IdentificationScreen
import com.suri.pipsurios.ui.screens.TerminalPanel
import com.suri.pipsurios.ui.screens.TerminalOverlay
import com.suri.pipsurios.ui.screens.TerminalScreen
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme
import androidx.compose.foundation.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val volumeKeyController = VolumeKeyController()

    companion object {
        const val EXTRA_START_DESTINATION = "com.suri.pipsurios.extra.START_DESTINATION"
        const val START_DESTINATION_STATISTICS = "statistics"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        val startDestination = if (
            intent.getStringExtra(EXTRA_START_DESTINATION) == START_DESTINATION_STATISTICS
        ) {
            PIPSuriOSDestination.DataStatistics
        } else {
            PIPSuriOSDestination.Splash
        }

        setContent {
            PIPSuriOSTheme {
                PIPSuriOSApp(volumeKeyController, startDestination)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean =
        if (volumeKeyController.handle(event)) true else super.dispatchKeyEvent(event)

    private fun hideStatusBar() {
        window.decorView.windowInsetsController?.apply {
            systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.statusBars())
        }
    }
}

private enum class PIPSuriOSDestination {
    Splash,
    Identification,
    Loading,
    HomeOperation,
    ToolsLoading,
    Tools,
    ProximityRadioScannerLoading,
    ProximityRadioScanner,
    ProximityRadioScannerSentry,
    ProximityRadioScannerTracker,
    ProximityRadioScannerUserGuide,
    ProximityRadioScannerV3,
    ProximityRadioScannerV4,
    ProximityRadioScannerGuide,
    PrsV4Target,
    PrsV4Grid,
    PrsTrackerTarget,
    PrsTrackerGrid,
    PrsDevices,
    PrsLocalScan,
    PrsScanProbe,
    PrsSentryPip,
    PrsSentryPipProbe,
    IndividualTracker,
    IndividualTrackerTarget,
    IndividualTrackerTracker,
    GeigerCounterLoading,
    GeigerCounter,
    DataLoading,
    Data,
    DataLog,
    DataLogDetail,
    DataStatistics,
    DataStatisticsPrimaryWeapon,
    DataStatisticsSecondaryWeapon,
    DataStatisticsLocation,
    DataStatisticsHeadgear,
    DataStatisticsUniform,
    OperationEditLoadout,
    OperationEditConfirm,
    OperationLoadout,
    OperationConfirm,
    DataSaved,
    DataDeleted,
    DataModified,
    InventoryLoading,
    InventoryModeSelection,
    InventoryArmory,
    InventorySniper,
    InventoryAssault,
    InventoryDemolition,
    InventoryHandgun,
    InventoryAccesories,
    InventoryComplements,
    InventoryDetails,
    InventoryStorage,
    InventoryStorageBbs,
    InventoryStorageGrenades,
    InventoryStorageGas,
    InventoryStorageItem,
    InventoryLoadouts,
    InventoryLoadoutsHeadgear,
    InventoryHeadgearSuri14,
    InventoryHeadgearBrotherhood,
    InventoryLoadoutsFrontPanel,
    InventoryLoadoutsUniform,
    InventoryFrontPanelSniperAssault,
    InventoryFrontPanelLightAssault,
    InventoryFrontPanelDemolition,
    CurrentGearLoading,
    CurrentGear,
    CurrentGearPrimaryWeapon,
    CurrentGearSecondaryWeapon,
    CurrentGearAccesories,
    CurrentGearHeadgear,
    CurrentGearFrontPanel,
    CurrentGearUniform,
    StatusLoading,
    Status,
    StatusAccessories,
    StatusDontForget,
    CommsLoading,
    CommsModeSelection,
    CommsFrequencies,
    MorseModeSelection,
    MorseToTextInput,
    MorseToTextOutput,
    MapLoading,
    MapModeSelection,
    MapTerrain,
    MapOperation,
    CivTakLoading,
    GoogleMapsLoading
}

private enum class VerticalOperationStep {
    DATE_LOCATION, CONSUMABLES, EDIT_DATE_LOCATION, EDIT_CONSUMABLES
}

private fun destinationUsesTerminalOverlay(destination: PIPSuriOSDestination): Boolean =
    when (destination) {
        PIPSuriOSDestination.Splash,
        PIPSuriOSDestination.Identification,
        PIPSuriOSDestination.Loading,
        PIPSuriOSDestination.HomeOperation,
        PIPSuriOSDestination.ProximityRadioScannerLoading,
        PIPSuriOSDestination.ProximityRadioScannerSentry,
        PIPSuriOSDestination.ProximityRadioScannerTracker,
        PIPSuriOSDestination.ProximityRadioScannerUserGuide,
        PIPSuriOSDestination.ProximityRadioScannerGuide,
        PIPSuriOSDestination.PrsV4Target,
        PIPSuriOSDestination.PrsV4Grid,
        PIPSuriOSDestination.PrsTrackerTarget,
        PIPSuriOSDestination.PrsTrackerGrid,
        PIPSuriOSDestination.PrsDevices,
        PIPSuriOSDestination.PrsLocalScan,
        PIPSuriOSDestination.PrsScanProbe,
        PIPSuriOSDestination.IndividualTracker,
        PIPSuriOSDestination.IndividualTrackerTarget,
        PIPSuriOSDestination.IndividualTrackerTracker,
        PIPSuriOSDestination.MapLoading,
        PIPSuriOSDestination.MapTerrain,
        PIPSuriOSDestination.CivTakLoading,
        PIPSuriOSDestination.GoogleMapsLoading -> false
        else -> true
    }

@Composable
private fun PIPSuriOSApp(
    volumeKeyController: VolumeKeyController,
    initialDestination: PIPSuriOSDestination = PIPSuriOSDestination.Splash
) {
    val context = LocalContext.current
    if (BuildConfig.PRS_ONLY) {
        PrsOnlyApp()
        return
    }
    var destination by remember { mutableStateOf(initialDestination) }
    val operatorProfileRepository = remember(context) {
        OperatorProfileRepository.from(context.applicationContext)
    }
    val operatorSetupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val operatorConfigured = operatorProfileRepository.load().id.trim().isNotEmpty()
        destination = if (result.resultCode == OperatorSetupActivity.RESULT_OK && operatorConfigured) {
            PIPSuriOSDestination.HomeOperation
        } else {
            PIPSuriOSDestination.Splash
        }
    }

    fun launchInitialOperatorSetup() {
        operatorSetupLauncher.launch(
            Intent(context, OperatorSetupActivity::class.java).apply {
                putExtra(OperatorSetupActivity.EXTRA_FOCUS_FIELD, OperatorField.ID.name)
            }
        )
    }

    var individualTrackingSelection by remember { mutableStateOf<IndividualTrackingSelection?>(null) }
    var prsV4Mode by remember { mutableStateOf(PrsV4Mode.ONLY_PIP_BOY) }
    var prsV4Selection by remember { mutableStateOf<IndividualTrackingSelection?>(null) }
    var selectedInventoryItem by remember { mutableStateOf(InventoryItem.L96) }
    var selectedStorageItem by remember { mutableStateOf<StorageItem?>(null) }
    var morseInput by remember { mutableStateOf("") }
    var morseOutput by remember { mutableStateOf("") }
    val loadoutConfigurationRepository = remember(context) {
        LoadoutConfigurationRepository.from(context.applicationContext)
    }
    val initialSetupLoadout = remember(loadoutConfigurationRepository) {
        loadoutConfigurationRepository.load()
    }
    var draftLoadout by remember { mutableStateOf(initialSetupLoadout) }
    var activeLoadout by remember { mutableStateOf(initialSetupLoadout) }
    var setupLoadout by remember { mutableStateOf(initialSetupLoadout) }
    var operationDraft by remember { mutableStateOf(OperationDraft()) }
    var operationSaveError by remember { mutableStateOf<String?>(null) }
    var operationSaving by remember { mutableStateOf(false) }
    var operationLogEntries by remember { mutableStateOf(emptyList<OperationLogEntry>()) }
    var operationLogUnreadableCount by remember { mutableIntStateOf(0) }
    var operationLogsLoading by remember { mutableStateOf(false) }
    var primaryWeaponStatistics by remember {
        mutableStateOf<PercentageDistribution<String>?>(null)
    }
    var primaryWeaponStatisticsLoading by remember { mutableStateOf(false) }
    var secondaryWeaponStatistics by remember {
        mutableStateOf<PercentageDistribution<String>?>(null)
    }
    var secondaryWeaponStatisticsLoading by remember { mutableStateOf(false) }
    var locationStatistics by remember {
        mutableStateOf<PercentageDistribution<String>?>(null)
    }
    var locationStatisticsLoading by remember { mutableStateOf(false) }
    var headgearStatistics by remember { mutableStateOf<PercentageDistribution<String>?>(null) }
    var headgearStatisticsLoading by remember { mutableStateOf(false) }
    var uniformStatistics by remember { mutableStateOf<PercentageDistribution<String>?>(null) }
    var uniformStatisticsLoading by remember { mutableStateOf(false) }
    var selectedOperationLog by remember { mutableStateOf<OperationLogEntry?>(null) }
    var operationEditDraft by remember { mutableStateOf<OperationEditDraft?>(null) }
    var operationEditSaving by remember { mutableStateOf(false) }
    var operationEditError by remember { mutableStateOf<String?>(null) }
    var operationLogDeleting by remember { mutableStateOf(false) }
    var operationLogDeleteError by remember { mutableStateOf<String?>(null) }
    var pendingVerticalStep by remember { mutableStateOf<VerticalOperationStep?>(null) }
    val operationRepository = remember(context) { OperationRepository.from(context.applicationContext) }
    val storageRepository = remember(context) { StorageRepository.from(context.applicationContext) }
    val operationScope = rememberCoroutineScope()

    val operationInputLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (pendingVerticalStep) {
            VerticalOperationStep.DATE_LOCATION -> when (result.resultCode) {
                OperationInputActivity.RESULT_NEXT -> {
                    val data = result.data
                    operationDraft = operationDraft.copy(
                        date = data?.getStringExtra(OperationInputActivity.EXTRA_DATE).orEmpty(),
                        location = data?.getStringExtra(OperationInputActivity.EXTRA_LOCATION).orEmpty()
                    )
                    operationSaveError = null
                    destination = PIPSuriOSDestination.OperationLoadout
                }
                OperationInputActivity.RESULT_BACK -> destination = PIPSuriOSDestination.Data
            }
            VerticalOperationStep.CONSUMABLES -> when (result.resultCode) {
                OperationInputActivity.RESULT_NEXT -> {
                    val values = OperationInputActivity.CONSUMABLE_KEYS.map { key ->
                        result.data?.getStringExtra(key)?.let(OperationInputValidator::parseDecimal)
                    }
                    if (values.all { it != null }) {
                        operationDraft = operationDraft.copy(
                            consumables = OperationConsumables(
                                primaryMag = values[0]!!,
                                secondaryMag = values[1]!!,
                                grenades40mm = values[2]!!,
                                grenades9mm = values[3]!!,
                                grenadesCo2 = values[4]!!,
                                primaryHpa = values[5]!!,
                                secondaryHpa = values[6]!!
                            )
                        )
                        operationSaveError = null
                        destination = PIPSuriOSDestination.OperationConfirm
                    }
                }
                OperationInputActivity.RESULT_BACK -> destination = PIPSuriOSDestination.OperationLoadout
            }
            VerticalOperationStep.EDIT_DATE_LOCATION -> when (result.resultCode) {
                OperationInputActivity.RESULT_NEXT -> {
                    operationEditDraft = operationEditDraft?.copy(
                        date = result.data?.getStringExtra(OperationInputActivity.EXTRA_DATE).orEmpty(),
                        location = result.data?.getStringExtra(OperationInputActivity.EXTRA_LOCATION).orEmpty()
                    )
                    destination = PIPSuriOSDestination.OperationEditLoadout
                }
                OperationInputActivity.RESULT_BACK -> {
                    operationEditDraft = null
                    operationEditError = null
                    destination = PIPSuriOSDestination.DataLogDetail
                }
            }
            VerticalOperationStep.EDIT_CONSUMABLES -> when (result.resultCode) {
                OperationInputActivity.RESULT_NEXT -> {
                    val values = OperationInputActivity.CONSUMABLE_KEYS.map { key ->
                        result.data?.getStringExtra(key)?.let(OperationInputValidator::parseDecimal)
                    }
                    if (values.all { it != null }) {
                        operationEditDraft = operationEditDraft?.copy(
                            consumables = OperationConsumables(
                                values[0]!!, values[1]!!, values[2]!!, values[3]!!,
                                values[4]!!, values[5]!!, values[6]!!
                            )
                        )
                        destination = PIPSuriOSDestination.OperationEditConfirm
                    }
                }
                OperationInputActivity.RESULT_BACK -> destination = PIPSuriOSDestination.OperationEditLoadout
            }
            null -> Unit
        }
        pendingVerticalStep = null
    }

    val setUpLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val previous = setupLoadout
        val updated = loadoutConfigurationRepository.load()
        setupLoadout = updated
        if (draftLoadout == previous) draftLoadout = updated
        if (activeLoadout == previous) activeLoadout = updated
    }

    fun launchSetUp() {
        setUpLauncher.launch(Intent(context, SetUpActivity::class.java))
    }

    fun launchInformation() {
        context.startActivity(Intent(context, InformationActivity::class.java))
    }

    fun launchDateLocation() {
        pendingVerticalStep = VerticalOperationStep.DATE_LOCATION
        operationInputLauncher.launch(
            OperationInputActivity.dateLocationIntent(
                context,
                operationDraft.date,
                operationDraft.location
            )
        )
    }

    fun launchConsumables() {
        pendingVerticalStep = VerticalOperationStep.CONSUMABLES
        operationInputLauncher.launch(
            OperationInputActivity.consumablesIntent(context, operationDraft.consumables)
        )
    }

    fun launchEditDateLocation() {
        val draft = operationEditDraft ?: return
        pendingVerticalStep = VerticalOperationStep.EDIT_DATE_LOCATION
        operationInputLauncher.launch(
            OperationInputActivity.editDateLocationIntent(context, draft.date, draft.location)
        )
    }

    fun launchEditConsumables() {
        val draft = operationEditDraft ?: return
        pendingVerticalStep = VerticalOperationStep.EDIT_CONSUMABLES
        operationInputLauncher.launch(
            OperationInputActivity.editConsumablesIntent(context, draft.consumables)
        )
    }

    fun openOperationLogs() {
        destination = PIPSuriOSDestination.DataLog
        operationLogsLoading = true
        operationScope.launch {
            val collection = withContext(Dispatchers.IO) { operationRepository.loadAll() }
            operationLogEntries = collection.entries
            operationLogUnreadableCount = collection.unreadableFileCount
            operationLogsLoading = false
        }
    }

    fun openPrimaryWeaponStatistics() {
        destination = PIPSuriOSDestination.DataStatisticsPrimaryWeapon
        primaryWeaponStatisticsLoading = true
        operationScope.launch {
            val distribution = withContext(Dispatchers.IO) {
                val primaryWeapons = WeaponReplicaCatalog.primary.map { it.displayName }
                val recordedWeapons = operationRepository.loadAll().entries.map { entry ->
                    entry.log.loadout.primaryWeapon
                }
                StatisticsCalculator.percentageDistribution(primaryWeapons, recordedWeapons)
            }
            primaryWeaponStatistics = distribution
            primaryWeaponStatisticsLoading = false
        }
    }

    fun openSecondaryWeaponStatistics() {
        destination = PIPSuriOSDestination.DataStatisticsSecondaryWeapon
        secondaryWeaponStatisticsLoading = true
        operationScope.launch {
            val distribution = withContext(Dispatchers.IO) {
                val secondaryWeapons = WeaponReplicaCatalog.secondary.map { it.displayName }
                val recordedWeapons = operationRepository.loadAll().entries.map { entry ->
                    entry.log.loadout.secondaryWeapon
                }
                StatisticsCalculator.percentageDistribution(secondaryWeapons, recordedWeapons)
            }
            secondaryWeaponStatistics = distribution
            secondaryWeaponStatisticsLoading = false
        }
    }

    fun openLocationStatistics() {
        destination = PIPSuriOSDestination.DataStatisticsLocation
        locationStatisticsLoading = true
        operationScope.launch {
            val distribution = withContext(Dispatchers.IO) {
                val locations = operationRepository.loadAll().entries.map { entry ->
                    entry.log.location
                }
                StatisticsCalculator.locationDistribution(locations)
            }
            locationStatistics = distribution
            locationStatisticsLoading = false
        }
    }

    fun openHeadgearStatistics() {
        destination = PIPSuriOSDestination.DataStatisticsHeadgear
        headgearStatisticsLoading = true
        operationScope.launch {
            val distribution = withContext(Dispatchers.IO) {
                val values = operationRepository.loadAll().entries.map { it.log.loadout.headgear }
                StatisticsCalculator.percentageDistribution(HeadgearCatalog.profiles, values)
            }
            headgearStatistics = distribution
            headgearStatisticsLoading = false
        }
    }

    fun openUniformStatistics() {
        destination = PIPSuriOSDestination.DataStatisticsUniform
        uniformStatisticsLoading = true
        operationScope.launch {
            val distribution = withContext(Dispatchers.IO) {
                val values = operationRepository.loadAll().entries.map { it.log.loadout.uniform }
                StatisticsCalculator.percentageDistribution(setupLoadout.uniformOptions, values)
            }
            uniformStatistics = distribution
            uniformStatisticsLoading = false
        }
    }

    fun startOperationEdit(entry: OperationLogEntry) {
        val draft = OperationEditDraft.from(entry)
        operationEditDraft = draft
        operationEditError = null
        pendingVerticalStep = VerticalOperationStep.EDIT_DATE_LOCATION
        operationInputLauncher.launch(
            OperationInputActivity.editDateLocationIntent(context, draft.date, draft.location)
        )
    }

    fun saveOperationEdit() {
        val draft = operationEditDraft ?: return
        operationEditSaving = true
        operationEditError = null
        operationScope.launch {
            val result = withContext(Dispatchers.IO) {
                operationRepository.update(draft.originalFilename, draft.toOperationLog())
            }
            operationEditSaving = false
            when (result) {
                is UpdateOperationResult.Updated -> {
                    withContext(Dispatchers.IO) {
                        storageRepository.reconcile(operationRepository.loadAll().entries.map { it.log })
                    }
                    selectedOperationLog = null
                    operationEditDraft = null
                    destination = PIPSuriOSDestination.DataModified
                }
                is UpdateOperationResult.Conflict -> {
                    operationEditError = "LOG ${result.filename.removeSuffix(".json")} ALREADY EXISTS"
                }
                UpdateOperationResult.OriginalNotFound -> {
                    operationEditError = "UPDATE FAILED: ORIGINAL LOG NOT FOUND"
                }
                is UpdateOperationResult.Failure -> {
                    operationEditError = "UPDATE FAILED: ${result.message}"
                }
            }
        }
    }

    fun deleteSelectedOperationLog() {
        val entry = selectedOperationLog ?: return
        operationLogDeleting = true
        operationLogDeleteError = null
        operationScope.launch {
            val result = withContext(Dispatchers.IO) {
                operationRepository.delete(entry.filename)
            }
            operationLogDeleting = false
            when (result) {
                DeleteOperationResult.Deleted -> {
                    withContext(Dispatchers.IO) {
                        storageRepository.reconcile(operationRepository.loadAll().entries.map { it.log })
                    }
                    selectedOperationLog = null
                    destination = PIPSuriOSDestination.DataDeleted
                }
                DeleteOperationResult.NotFound -> {
                    operationLogDeleteError = "DELETE FAILED: LOG NOT FOUND"
                }
                is DeleteOperationResult.Failure -> {
                    operationLogDeleteError = "DELETE FAILED: ${result.message}"
                }
            }
        }
    }

    val renderDestination: @Composable () -> Unit = {
        when (destination) {
            PIPSuriOSDestination.Splash -> PIPSuriOSScreen(
                onFinished = { destination = PIPSuriOSDestination.Identification }
            )

            PIPSuriOSDestination.Identification -> IdentificationScreen(
                onAuthenticated = { destination = PIPSuriOSDestination.Loading }
            )

            PIPSuriOSDestination.Loading -> LoadingScreen(
                operatorId = operatorProfileRepository.load().id,
                onFinished = { destination = PIPSuriOSDestination.HomeOperation },
                onOperatorMissing = ::launchInitialOperatorSetup
            )

            PIPSuriOSDestination.HomeOperation -> HomeOperationScreen(
                onInventorySelected = { destination = PIPSuriOSDestination.InventoryLoading },
                onDataSelected = { destination = PIPSuriOSDestination.DataLoading },
                onCurrentGearSelected = { destination = PIPSuriOSDestination.CurrentGearLoading },
                onSetUpSelected = ::launchSetUp,
                onStatusSelected = { destination = PIPSuriOSDestination.StatusLoading },
                onToolsSelected = { destination = PIPSuriOSDestination.ToolsLoading },
                onInformationSelected = ::launchInformation
            )

            PIPSuriOSDestination.ToolsLoading -> ToolsLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.Tools }
            )

            PIPSuriOSDestination.Tools -> ToolsScreen(
                onMapSelected = { destination = PIPSuriOSDestination.MapLoading },
                onCommsSelected = { destination = PIPSuriOSDestination.CommsLoading },
                onGeigerCounterSelected = {
                    destination = PIPSuriOSDestination.GeigerCounterLoading
                },
                onProximityRadioScannerSelected = {
                    destination = PIPSuriOSDestination.ProximityRadioScannerLoading
                },
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
            )

            PIPSuriOSDestination.ProximityRadioScannerLoading -> ProximityRadioScannerLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )

            PIPSuriOSDestination.ProximityRadioScanner -> ProximityRadioScannerScreen(
                onSentrySelected = { destination = PIPSuriOSDestination.ProximityRadioScannerSentry },
                onTrackerSelected = { destination = PIPSuriOSDestination.ProximityRadioScannerTracker },
                onDevicesSelected = { destination = PIPSuriOSDestination.PrsDevices },
                onUserGuideSelected = { destination = PIPSuriOSDestination.ProximityRadioScannerUserGuide },
                onBack = { destination = PIPSuriOSDestination.Tools }
            )
            PIPSuriOSDestination.ProximityRadioScannerSentry -> ProximityRadioScannerSentryScreen(
                onPipSelected = { destination = PIPSuriOSDestination.PrsSentryPip },
                onPipProbeSelected = { destination = PIPSuriOSDestination.PrsSentryPipProbe },
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )
            PIPSuriOSDestination.ProximityRadioScannerTracker -> ProximityRadioScannerV4Screen(
                onOnlyPipBoySelected = {
                    prsV4Mode = PrsV4Mode.ONLY_PIP_BOY
                    prsV4Selection = null
                    destination = PIPSuriOSDestination.PrsTrackerTarget
                },
                onPipBoyProbeSelected = {
                    prsV4Mode = PrsV4Mode.PIP_BOY_PROBE
                    prsV4Selection = null
                    destination = PIPSuriOSDestination.PrsTrackerTarget
                },
                title = "P.R.S. / TRACKER",
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )
            PIPSuriOSDestination.ProximityRadioScannerUserGuide -> PrsUserGuideScreen(
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )
            PIPSuriOSDestination.ProximityRadioScannerV3 -> ProximityRadioScannerV3Screen(
                onLocalScanSelected = { destination = PIPSuriOSDestination.PrsLocalScan },
                onScanProbeSelected = { destination = PIPSuriOSDestination.PrsScanProbe },
                onDevicesSelected = { destination = PIPSuriOSDestination.PrsDevices },
                onIndividualTrackerSelected = { destination = PIPSuriOSDestination.IndividualTracker },
                onGuideSelected = { destination = PIPSuriOSDestination.ProximityRadioScannerGuide },
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )
            PIPSuriOSDestination.ProximityRadioScannerV4 -> ProximityRadioScannerV4Screen(
                onOnlyPipBoySelected = {
                    prsV4Mode = PrsV4Mode.ONLY_PIP_BOY
                    prsV4Selection = null
                    destination = PIPSuriOSDestination.PrsV4Target
                },
                onPipBoyProbeSelected = {
                    prsV4Mode = PrsV4Mode.PIP_BOY_PROBE
                    prsV4Selection = null
                    destination = PIPSuriOSDestination.PrsV4Target
                },
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )
            PIPSuriOSDestination.PrsV4Target -> IndividualTrackingTargetScreen(
                mode = prsV4Mode.operatingMode,
                modeLabel = prsV4Mode.displayName,
                title = "P.R.S. v4.0 / STEP 1",
                locationStepLabel = "STEP 1 // SELECT LOCATION",
                targetStepLabel = "STEP 1 // IDENTIFY TARGET",
                splitLayout = true,
                onTargetSelected = {
                    prsV4Selection = it
                    destination = PIPSuriOSDestination.PrsV4Grid
                },
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerV4 }
            )
            PIPSuriOSDestination.PrsTrackerTarget -> IndividualTrackingTargetScreen(
                mode = prsV4Mode.operatingMode,
                modeLabel = prsV4Mode.displayName,
                title = "P.R.S. / TRACKER / STEP 1",
                locationStepLabel = "STEP 1 // SELECT LOCATION",
                targetStepLabel = "STEP 1 // IDENTIFY TARGET",
                splitLayout = true,
                onTargetSelected = {
                    prsV4Selection = it
                    destination = PIPSuriOSDestination.PrsTrackerGrid
                },
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerTracker }
            )
            PIPSuriOSDestination.PrsV4Grid -> IndividualTrackingTrackerScreen(
                selection = prsV4Selection,
                mode = prsV4Mode.operatingMode,
                modeLabel = prsV4Mode.displayName,
                title = "P.R.S. v4.0 / STEP 2 // GRID",
                onSelectTarget = { destination = PIPSuriOSDestination.PrsV4Target },
                onBack = { destination = PIPSuriOSDestination.PrsV4Target }
            )
            PIPSuriOSDestination.PrsTrackerGrid -> IndividualTrackingTrackerScreen(
                selection = prsV4Selection,
                mode = prsV4Mode.operatingMode,
                modeLabel = prsV4Mode.displayName,
                title = "P.R.S. / TRACKER / STEP 2 // GRID",
                onSelectTarget = { destination = PIPSuriOSDestination.PrsTrackerTarget },
                onBack = { destination = PIPSuriOSDestination.PrsTrackerTarget }
            )
            PIPSuriOSDestination.ProximityRadioScannerGuide -> ProximityRadioScannerGuideScreen(
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerV3 }
            )

            PIPSuriOSDestination.PrsDevices -> PrsDevicesScreen(
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScanner }
            )

            PIPSuriOSDestination.PrsSentryPip -> PrsTrackingScreen(
                mode = PrsOperatingMode.LOCAL_SCAN,
                modeLabel = "PIP",
                subtitle = "A56 ONLY // SURVEILLANCE",
                allowTargetSelection = false,
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerSentry }
            )

            PIPSuriOSDestination.PrsSentryPipProbe -> PrsTrackingScreen(
                mode = PrsOperatingMode.SCAN_PROBE,
                modeLabel = "PIP + PROBE",
                subtitle = "A56 + WATCH 2 // SURVEILLANCE",
                allowTargetSelection = false,
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerSentry }
            )

            PIPSuriOSDestination.PrsLocalScan -> PrsTrackingScreen(
                mode = PrsOperatingMode.LOCAL_SCAN,
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerV3 }
            )

            PIPSuriOSDestination.PrsScanProbe -> PrsTrackingScreen(
                mode = PrsOperatingMode.SCAN_PROBE,
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerV3 }
            )

            PIPSuriOSDestination.IndividualTracker -> IndividualTrackingMenuScreen(
                selection = individualTrackingSelection,
                onTargetSelected = { destination = PIPSuriOSDestination.IndividualTrackerTarget },
                onTrackerSelected = { destination = PIPSuriOSDestination.IndividualTrackerTracker },
                onBack = { destination = PIPSuriOSDestination.ProximityRadioScannerV3 }
            )

            PIPSuriOSDestination.IndividualTrackerTarget -> IndividualTrackingTargetScreen(
                onTargetSelected = {
                    individualTrackingSelection = it
                    destination = PIPSuriOSDestination.IndividualTracker
                },
                onBack = { destination = PIPSuriOSDestination.IndividualTracker }
            )

            PIPSuriOSDestination.IndividualTrackerTracker -> IndividualTrackingTrackerScreen(
                selection = individualTrackingSelection,
                onSelectTarget = { destination = PIPSuriOSDestination.IndividualTrackerTarget },
                onBack = { destination = PIPSuriOSDestination.IndividualTracker }
            )

            PIPSuriOSDestination.GeigerCounterLoading -> GeigerCounterLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.GeigerCounter }
            )

            PIPSuriOSDestination.GeigerCounter -> GeigerCounterScreen(
                volumeKeyController = volumeKeyController,
                onBack = { destination = PIPSuriOSDestination.Tools }
            )

            PIPSuriOSDestination.DataLoading -> DataLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.Data }
            )

            PIPSuriOSDestination.Data -> DataScreen(
                onInputOperation = {
                    operationDraft = OperationDraft()
                    operationSaveError = null
                    launchDateLocation()
                },
                onLog = ::openOperationLogs,
                onStatistics = { destination = PIPSuriOSDestination.DataStatistics },
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
            )

            PIPSuriOSDestination.DataLog -> DataLogScreen(
                entries = operationLogEntries,
                unreadableFileCount = operationLogUnreadableCount,
                loading = operationLogsLoading,
                onEntrySelected = {
                    selectedOperationLog = it
                    operationLogDeleteError = null
                    destination = PIPSuriOSDestination.DataLogDetail
                },
                onBack = { destination = PIPSuriOSDestination.Data }
            )

            PIPSuriOSDestination.DataLogDetail -> selectedOperationLog?.let { entry ->
                DataLogDetailScreen(
                    log = entry.log,
                    deleting = operationLogDeleting,
                    deleteError = operationLogDeleteError,
                    onEdit = { startOperationEdit(entry) },
                    onDelete = ::deleteSelectedOperationLog,
                    onBack = { destination = PIPSuriOSDestination.DataLog }
                )
            } ?: DataLogScreen(
                entries = operationLogEntries,
                unreadableFileCount = operationLogUnreadableCount,
                loading = false,
                onEntrySelected = {
                    selectedOperationLog = it
                    operationLogDeleteError = null
                    destination = PIPSuriOSDestination.DataLogDetail
                },
                onBack = { destination = PIPSuriOSDestination.Data }
            )

            PIPSuriOSDestination.DataStatistics -> DataStatisticsScreen(
                onPrimaryWeapon = ::openPrimaryWeaponStatistics,
                onSecondaryWeapon = ::openSecondaryWeaponStatistics,
                onLocation = ::openLocationStatistics,
                onHeadgear = ::openHeadgearStatistics,
                onUniform = ::openUniformStatistics,
                onBack = { destination = PIPSuriOSDestination.Data }
            )

            PIPSuriOSDestination.DataStatisticsPrimaryWeapon -> PrimaryWeaponStatisticsScreen(
                distribution = primaryWeaponStatistics,
                loading = primaryWeaponStatisticsLoading,
                onBack = { destination = PIPSuriOSDestination.DataStatistics }
            )

            PIPSuriOSDestination.DataStatisticsSecondaryWeapon -> SecondaryWeaponStatisticsScreen(
                distribution = secondaryWeaponStatistics,
                loading = secondaryWeaponStatisticsLoading,
                onBack = { destination = PIPSuriOSDestination.DataStatistics }
            )

            PIPSuriOSDestination.DataStatisticsLocation -> LocationStatisticsScreen(
                distribution = locationStatistics,
                loading = locationStatisticsLoading,
                onBack = { destination = PIPSuriOSDestination.DataStatistics }
            )

            PIPSuriOSDestination.DataStatisticsHeadgear -> HeadgearStatisticsScreen(
                distribution = headgearStatistics,
                loading = headgearStatisticsLoading,
                onBack = { destination = PIPSuriOSDestination.DataStatistics }
            )

            PIPSuriOSDestination.DataStatisticsUniform -> UniformStatisticsScreen(
                distribution = uniformStatistics,
                loading = uniformStatisticsLoading,
                onBack = { destination = PIPSuriOSDestination.DataStatistics }
            )

            PIPSuriOSDestination.OperationEditLoadout -> operationEditDraft?.let { draft ->
                OperationEditLoadoutScreen(
                    loadout = draft.loadout,
                    onLoadoutChanged = { operationEditDraft = draft.copy(loadout = it) },
                    onNext = ::launchEditConsumables,
                    onBack = ::launchEditDateLocation
                )
            }

            PIPSuriOSDestination.OperationEditConfirm -> operationEditDraft?.let { draft ->
                OperationEditConfirmScreen(
                    draft = draft,
                    saveError = operationEditError,
                    saving = operationEditSaving,
                    onConfirm = ::saveOperationEdit,
                    onBack = ::launchEditConsumables
                )
            }

            PIPSuriOSDestination.OperationLoadout -> OperationLoadoutScreen(
                activeLoadout = activeLoadout,
                loadoutConfirmed = operationDraft.loadout != null,
                onConfirmLoadout = {
                    operationDraft = operationDraft.copy(
                        loadout = OperationLoadoutSnapshot.from(activeLoadout)
                    )
                    operationSaveError = null
                },
                onNext = ::launchConsumables,
                onBack = ::launchDateLocation
            )

            PIPSuriOSDestination.OperationConfirm -> OperationConfirmScreen(
                draft = operationDraft,
                saveError = operationSaveError,
                saving = operationSaving,
                onEdit = ::launchDateLocation,
                onConfirm = {
                    val loadout = operationDraft.loadout
                    val consumables = operationDraft.consumables
                    if (loadout != null && consumables != null) {
                        operationScope.launch {
                            operationSaving = true
                            val result = withContext(Dispatchers.IO) {
                                operationRepository.save(
                                    OperationLog(
                                        date = operationDraft.date,
                                        location = operationDraft.location,
                                        loadout = loadout,
                                        consumables = consumables
                                    )
                                )
                            }
                            operationSaving = false
                            when (result) {
                                is SaveOperationResult.Saved -> {
                                    withContext(Dispatchers.IO) {
                                        storageRepository.reconcile(operationRepository.loadAll().entries.map { it.log })
                                    }
                                    operationSaveError = null
                                    destination = PIPSuriOSDestination.DataSaved
                                }
                                is SaveOperationResult.AlreadyExists -> {
                                    operationSaveError = "LOG ${result.file.nameWithoutExtension} ALREADY EXISTS"
                                }
                                is SaveOperationResult.Failure -> {
                                    operationSaveError = "SAVE FAILED: ${result.message}"
                                }
                            }
                        }
                    }
                },
                onBack = ::launchConsumables
            )

            PIPSuriOSDestination.DataSaved -> DataSavedScreen(
                onFinished = {
                    operationDraft = OperationDraft()
                    operationSaveError = null
                    destination = PIPSuriOSDestination.Data
                }
            )

            PIPSuriOSDestination.DataDeleted -> DataDeletedScreen(
                onFinished = ::openOperationLogs
            )

            PIPSuriOSDestination.DataModified -> DataModifiedScreen(
                onFinished = ::openOperationLogs
            )

            PIPSuriOSDestination.InventoryLoading -> InventoryLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.InventoryModeSelection }
            )

            PIPSuriOSDestination.InventoryModeSelection -> InventoryModeSelectionScreen(
                onBack = { destination = PIPSuriOSDestination.HomeOperation },
                onArmorySelected = { destination = PIPSuriOSDestination.InventoryArmory },
                onConsumablesSelected = { destination = PIPSuriOSDestination.InventoryStorage },
                onLoadoutsSelected = { destination = PIPSuriOSDestination.InventoryLoadouts }
            )

            PIPSuriOSDestination.InventoryArmory -> InventoryScreen(
                onBack = { destination = PIPSuriOSDestination.InventoryModeSelection },
                onSniperSelected = { destination = PIPSuriOSDestination.InventorySniper },
                onAssaultSelected = { destination = PIPSuriOSDestination.InventoryAssault },
                onDemolitionSelected = { destination = PIPSuriOSDestination.InventoryDemolition },
                onHandgunSelected = { destination = PIPSuriOSDestination.InventoryHandgun },
                onAccesoriesSelected = { destination = PIPSuriOSDestination.InventoryAccesories },
                onComplementsSelected = { destination = PIPSuriOSDestination.InventoryComplements }
            )

            PIPSuriOSDestination.InventorySniper -> InventoryCategoryScreen(
                title = "INVENTORY - SNIPER",
                entries = PrimaryWeaponRole.SNIPER.weapons.map { "> ${it.displayName}" },
                entryActions = PrimaryWeaponRole.SNIPER.weapons.associate { item ->
                    "> ${item.displayName}" to {
                        selectedInventoryItem = item
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                },
                onBack = { destination = PIPSuriOSDestination.InventoryArmory }
            )

            PIPSuriOSDestination.InventoryAssault -> InventoryCategoryScreen(
                title = "INVENTORY - ASSAULT",
                entries = PrimaryWeaponRole.ASSAULT.weapons.map { "> ${it.displayName}" },
                entryActions = PrimaryWeaponRole.ASSAULT.weapons.associate { item ->
                    "> ${item.displayName}" to {
                        selectedInventoryItem = item
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                },
                onBack = { destination = PIPSuriOSDestination.InventoryArmory }
            )

            PIPSuriOSDestination.InventoryDemolition -> InventoryCategoryScreen(
                title = "INVENTORY - DEMOLITION",
                entries = PrimaryWeaponRole.DEMOLITION.weapons.map { "> ${it.displayName}" },
                entryActions = PrimaryWeaponRole.DEMOLITION.weapons.associate { item ->
                    "> ${item.displayName}" to {
                        selectedInventoryItem = item
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                },
                onBack = { destination = PIPSuriOSDestination.InventoryArmory }
            )

            PIPSuriOSDestination.InventoryHandgun -> InventoryCategoryScreen(
                title = "INVENTORY - HANDGUN",
                entries = listOf("> DESERT EAGLE", "> AAP-01C"),
                entryActions = mapOf(
                    "> DESERT EAGLE" to {
                        selectedInventoryItem = InventoryItem.DESERT_EAGLE
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> AAP-01C" to {
                        selectedInventoryItem = InventoryItem.AAP_01C
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryArmory }
            )

            PIPSuriOSDestination.InventoryAccesories -> InventoryCategoryScreen(
                title = "INVENTORY - ACCESORIES",
                entries = listOf(
                    "> DETON-A", "> THUNDER B", "> TANTO", "> MINI KNIFE", "> VOLCANO", "> WATCH 2"
                ),
                entryActions = mapOf(
                    "> DETON-A" to {
                        selectedInventoryItem = InventoryItem.DETON_A
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> THUNDER B" to {
                        selectedInventoryItem = InventoryItem.THUNDER_B
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> TANTO" to {
                        selectedInventoryItem = InventoryItem.TANTO
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> MINI KNIFE" to {
                        selectedInventoryItem = InventoryItem.MINI_KNIFE
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> WATCH 2" to {
                        selectedInventoryItem = InventoryItem.WATCH_2
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryArmory }
            )

            PIPSuriOSDestination.InventoryComplements -> ComplementsScreen(
                onBack = { destination = PIPSuriOSDestination.InventoryArmory }
            )

            PIPSuriOSDestination.InventoryDetails -> InventoryDetailsScreen(
                item = selectedInventoryItem,
                onBack = {
                    destination = when (selectedInventoryItem) {
                        InventoryItem.L96, InventoryItem.LEVAR_15 ->
                            PIPSuriOSDestination.InventorySniper
                        InventoryItem.MCX, InventoryItem.APC_9K ->
                            PIPSuriOSDestination.InventoryAssault
                        InventoryItem.MGL, InventoryItem.VOLCANO ->
                            PIPSuriOSDestination.InventoryDemolition
                        InventoryItem.DESERT_EAGLE, InventoryItem.AAP_01C ->
                            PIPSuriOSDestination.InventoryHandgun
                        InventoryItem.DETON_A,
                        InventoryItem.THUNDER_B,
                        InventoryItem.TANTO,
                        InventoryItem.MINI_KNIFE,
                        InventoryItem.WATCH_2 ->
                            PIPSuriOSDestination.InventoryAccesories
                    }
                }
            )

            PIPSuriOSDestination.InventoryStorage -> InventoryVisualMenuScreen(
                title = "INVENTORY - STORAGE",
                entries = listOf("> BBs", "> GRENADES", "> GAS"),
                entryActions = mapOf(
                    "> BBs" to { destination = PIPSuriOSDestination.InventoryStorageBbs },
                    "> GRENADES" to { destination = PIPSuriOSDestination.InventoryStorageGrenades },
                    "> GAS" to { destination = PIPSuriOSDestination.InventoryStorageGas }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryModeSelection }
            )

            PIPSuriOSDestination.InventoryStorageBbs -> StorageGroupScreen(
                title = "STORAGE - BBs", items = StorageCatalog.bbs,
                onItem = { selectedStorageItem = it; destination = PIPSuriOSDestination.InventoryStorageItem },
                onBack = { destination = PIPSuriOSDestination.InventoryStorage }
            )

            PIPSuriOSDestination.InventoryStorageGrenades -> StorageGroupScreen(
                title = "STORAGE - GRENADES", items = StorageCatalog.grenades,
                onItem = { selectedStorageItem = it; destination = PIPSuriOSDestination.InventoryStorageItem },
                onBack = { destination = PIPSuriOSDestination.InventoryStorage }
            )

            PIPSuriOSDestination.InventoryStorageGas -> StorageGroupScreen(
                title = "STORAGE - GAS", items = StorageCatalog.gas,
                onItem = { selectedStorageItem = it; destination = PIPSuriOSDestination.InventoryStorageItem },
                onBack = { destination = PIPSuriOSDestination.InventoryStorage }
            )

            PIPSuriOSDestination.InventoryStorageItem -> selectedStorageItem?.let { item ->
                val logs = remember(item.stableId) { operationRepository.loadAll().entries.map { it.log } }
                fun loadStorageBalance() = StorageCalculator.balance(
                    item, storageRepository.reconcile(logs)[item.stableId], logs
                )
                var balance by remember(item.stableId) { mutableStateOf(loadStorageBalance()) }
                StorageItemScreen(
                    balance = balance,
                    onPurchase = {
                        if (storageRepository.purchase(item.stableId, logs)) {
                            balance = balance.copy(
                                purchase = balance.purchase + java.math.BigDecimal.ONE,
                                total = balance.total + java.math.BigDecimal.ONE
                            )
                        }
                    },
                    onUsed = {
                        if (storageRepository.use(item.stableId, logs)) {
                            balance = balance.copy(
                                used = balance.used + java.math.BigDecimal.ONE,
                                total = balance.total - java.math.BigDecimal.ONE
                            )
                        }
                    },
                    onBack = {
                        destination = when (item.group) {
                            com.suri.pipsurios.storage.StorageGroup.BBS -> PIPSuriOSDestination.InventoryStorageBbs
                            com.suri.pipsurios.storage.StorageGroup.GRENADES -> PIPSuriOSDestination.InventoryStorageGrenades
                            com.suri.pipsurios.storage.StorageGroup.GAS -> PIPSuriOSDestination.InventoryStorageGas
                        }
                    }
                )
            }

            PIPSuriOSDestination.InventoryLoadouts -> InventoryVisualMenuScreen(
                title = "INVENTORY - LOADOUTS",
                entries = listOf("> HEADGEAR", "> FRONT PANEL", "> UNIFORM"),
                entryActions = mapOf(
                    "> HEADGEAR" to { destination = PIPSuriOSDestination.InventoryLoadoutsHeadgear },
                    "> FRONT PANEL" to { destination = PIPSuriOSDestination.InventoryLoadoutsFrontPanel },
                    "> UNIFORM" to { destination = PIPSuriOSDestination.InventoryLoadoutsUniform }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryModeSelection }
            )

            PIPSuriOSDestination.InventoryLoadoutsHeadgear -> InventoryVisualMenuScreen(
                title = "LOADOUTS - HEADGEAR",
                entries = listOf("> SURI-14", "> BROTHERHOOD"),
                entryActions = mapOf(
                    "> SURI-14" to { destination = PIPSuriOSDestination.InventoryHeadgearSuri14 },
                    "> BROTHERHOOD" to { destination = PIPSuriOSDestination.InventoryHeadgearBrotherhood }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadouts }
            )

            PIPSuriOSDestination.InventoryHeadgearSuri14 -> InventoryVisualMenuScreen(
                title = "HEADGEAR - SURI-14",
                entries = listOf("> VYPER", "> DYE MASK"),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadoutsHeadgear }
            )

            PIPSuriOSDestination.InventoryHeadgearBrotherhood -> InventoryVisualMenuScreen(
                title = "HEADGEAR - BROTHERHOOD",
                entries = listOf("> HELMET", "> NVG", "> GAS MASK", "> SECURITY GOGGLES"),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadoutsHeadgear }
            )

            PIPSuriOSDestination.InventoryLoadoutsFrontPanel -> InventoryVisualMenuScreen(
                title = "LOADOUTS - FRONT PANEL",
                entries = listOf("> SNIPER - ASSAULT", "> LIGHT ASSAULT", "> DEMOLITION"),
                entryActions = mapOf(
                    "> SNIPER - ASSAULT" to { destination = PIPSuriOSDestination.InventoryFrontPanelSniperAssault },
                    "> LIGHT ASSAULT" to { destination = PIPSuriOSDestination.InventoryFrontPanelLightAssault },
                    "> DEMOLITION" to { destination = PIPSuriOSDestination.InventoryFrontPanelDemolition }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadouts }
            )

            PIPSuriOSDestination.InventoryLoadoutsUniform -> InventoryVisualMenuScreen(
                title = "LOADOUTS - UNIFORM",
                entries = setupLoadout.uniformOptions.map { "> $it" },
                onBack = { destination = PIPSuriOSDestination.InventoryLoadouts }
            )

            PIPSuriOSDestination.InventoryFrontPanelSniperAssault -> InventoryVisualMenuScreen(
                title = "FRONT PANEL - SNIPER - ASSAULT",
                entries = listOf("> L96", "> LevAR-15", "> MCX"),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadoutsFrontPanel }
            )

            PIPSuriOSDestination.InventoryFrontPanelLightAssault -> InventoryVisualMenuScreen(
                title = "FRONT PANEL - LIGHT ASSAULT",
                entries = listOf("> APC-9K"),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadoutsFrontPanel }
            )

            PIPSuriOSDestination.InventoryFrontPanelDemolition -> InventoryVisualMenuScreen(
                title = "FRONT PANEL - DEMOLITION",
                entries = listOf("> MGL", "> VOLCANO"),
                onBack = { destination = PIPSuriOSDestination.InventoryLoadoutsFrontPanel }
            )

            PIPSuriOSDestination.CurrentGearLoading -> CurrentGearLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGear -> CurrentGearScreen(
                onPrimaryWeaponSelected = { destination = PIPSuriOSDestination.CurrentGearPrimaryWeapon },
                onSecondaryWeaponSelected = { destination = PIPSuriOSDestination.CurrentGearSecondaryWeapon },
                onAccesoriesSelected = { destination = PIPSuriOSDestination.CurrentGearAccesories },
                onHeadgearSelected = { destination = PIPSuriOSDestination.CurrentGearHeadgear },
                onFrontPanelSelected = { destination = PIPSuriOSDestination.CurrentGearFrontPanel },
                onUniformSelected = { destination = PIPSuriOSDestination.CurrentGearUniform },
                isApplied = draftLoadout == activeLoadout,
                onApply = {
                    activeLoadout = draftLoadout.copy(accesories = draftLoadout.accesories.toSet())
                },
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
            )

            PIPSuriOSDestination.CurrentGearPrimaryWeapon -> PrimaryWeaponScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                catalogMode = true,
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearSecondaryWeapon -> SecondaryWeaponScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                catalogMode = true,
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearAccesories -> AccesoriesScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                accesoryOptions = draftLoadout.accesoryOptions,
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearHeadgear -> HeadgearScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearFrontPanel -> FrontPanelScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                frontPanelOptions = draftLoadout.frontPanelOptions,
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearUniform -> UniformScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                uniformOptions = draftLoadout.uniformOptions,
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.StatusLoading -> StatusLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.Status }
            )

            PIPSuriOSDestination.Status -> StatusScreen(
                activeLoadout = activeLoadout,
                onDontForgetSelected = { destination = PIPSuriOSDestination.StatusDontForget },
                onAccessoriesSelected = { destination = PIPSuriOSDestination.StatusAccessories },
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
            )

            PIPSuriOSDestination.StatusAccessories -> StatusAccessoriesScreen(
                activeLoadout = activeLoadout,
                onBack = { destination = PIPSuriOSDestination.Status }
            )

            PIPSuriOSDestination.StatusDontForget -> DontForgetScreen(
                activeLoadout = activeLoadout,
                onBack = { destination = PIPSuriOSDestination.Status }
            )

            PIPSuriOSDestination.CommsLoading -> CommsLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.CommsModeSelection }
            )

            PIPSuriOSDestination.CommsModeSelection -> CommsModeSelectionScreen(
                onFrequenciesSelected = { destination = PIPSuriOSDestination.CommsFrequencies },
                onMorseSelected = { destination = PIPSuriOSDestination.MorseModeSelection },
                onBack = { destination = PIPSuriOSDestination.Tools }
            )

            PIPSuriOSDestination.CommsFrequencies -> CommsScreen(
                onBack = { destination = PIPSuriOSDestination.CommsModeSelection }
            )

            PIPSuriOSDestination.MorseModeSelection -> MorseModeSelectionScreen(
                onTextToMorseSelected = {
                    context.startActivity(Intent(context, TextToMorseActivity::class.java))
                },
                onMorseToTextSelected = {
                    morseInput = ""
                    destination = PIPSuriOSDestination.MorseToTextInput
                },
                onBack = { destination = PIPSuriOSDestination.CommsModeSelection }
            )

            PIPSuriOSDestination.MorseToTextInput -> MorseToTextInputScreen(
                input = morseInput,
                onInputChanged = { morseInput = it },
                onConvert = {
                    morseOutput = com.suri.pipsurios.morse.MorseCodec.decode(morseInput)
                    destination = PIPSuriOSDestination.MorseToTextOutput
                },
                onBack = { destination = PIPSuriOSDestination.MorseModeSelection }
            )

            PIPSuriOSDestination.MorseToTextOutput -> MorseToTextOutputScreen(
                output = morseOutput,
                onBack = {
                    morseInput = ""
                    morseOutput = ""
                    destination = PIPSuriOSDestination.MorseToTextInput
                }
            )

            PIPSuriOSDestination.MapLoading -> MapLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.MapModeSelection }
            )

            PIPSuriOSDestination.MapModeSelection -> MapModeSelectionScreen(
                onTerrainSelected = { destination = PIPSuriOSDestination.MapTerrain },
                onOperationSelected = { destination = PIPSuriOSDestination.MapOperation },
                onBack = { destination = PIPSuriOSDestination.Tools }
            )

            PIPSuriOSDestination.MapTerrain -> MapTerrainScreen(
                onBack = { destination = PIPSuriOSDestination.MapModeSelection }
            )

            PIPSuriOSDestination.MapOperation -> MapOperationScreen(
                onBack = { destination = PIPSuriOSDestination.MapModeSelection },
                onLaunch = { destination = PIPSuriOSDestination.CivTakLoading }
            )

            PIPSuriOSDestination.CivTakLoading -> CivTakLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.GoogleMapsLoading },
                onExternalLaunch = { destination = PIPSuriOSDestination.MapModeSelection }
            )

            PIPSuriOSDestination.GoogleMapsLoading -> GoogleMapsLoadingScreen(
                onExternalLaunch = { destination = PIPSuriOSDestination.MapModeSelection }
            )
        }
    }

    if (destinationUsesTerminalOverlay(destination)) {
        TerminalOverlay { renderDestination() }
    } else {
        renderDestination()
    }
}

@Composable
fun PIPSuriOSScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3_000)
        onFinished()
    }

    TerminalScreen {
        TerminalPanel(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "PIP-BOY by RobCo",
                    color = PipGreen,
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "PIP-SuriOS v2.9",
                    color = PipGreenDim,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "Brotherhood of Steel",
                    color = PipGreenDim,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "INITIALIZING...",
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
