package com.suri.pipsurios

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import com.suri.pipsurios.BuildConfig
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.screens.HomeCivilianScreen
import com.suri.pipsurios.ui.screens.HomeOperationScreen
import com.suri.pipsurios.ui.screens.InventoryCategoryScreen
import com.suri.pipsurios.ui.screens.InventoryDetailsScreen
import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.InventoryLoadingScreen
import com.suri.pipsurios.ui.screens.InventoryModeSelectionScreen
import com.suri.pipsurios.ui.screens.InventoryScreen
import com.suri.pipsurios.ui.screens.InventoryVisualMenuScreen
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
import com.suri.pipsurios.ui.screens.CurrentGearLoadingScreen
import com.suri.pipsurios.ui.screens.CurrentGearScreen
import com.suri.pipsurios.ui.screens.PrimaryWeaponScreen
import com.suri.pipsurios.ui.screens.SecondaryWeaponScreen
import com.suri.pipsurios.ui.screens.AccesoriesScreen
import com.suri.pipsurios.ui.screens.HeadgearScreen
import com.suri.pipsurios.ui.screens.FrontPanelScreen
import com.suri.pipsurios.ui.screens.StatusLoadingScreen
import com.suri.pipsurios.ui.screens.StatusScreen
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
import com.suri.pipsurios.ui.screens.ModeSelectionScreen
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme
import androidx.compose.foundation.Image
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()

        setContent {
            PIPSuriOSTheme {
                PIPSuriOSApp()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

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
    Loading,
    ModeSelection,
    HomeOperation,
    HomeCivilian,
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
    InventoryConsumables,
    InventoryConsumablesBbs,
    InventoryConsumablesGrenades,
    InventoryGrenadesCartridges,
    InventoryConsumablesGas,
    InventoryLoadouts,
    InventoryLoadoutsHeadgear,
    InventoryHeadgearSuri14,
    InventoryHeadgearBrotherhood,
    InventoryLoadoutsFrontPanel,
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
    StatusLoading,
    Status,
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

@Composable
private fun PIPSuriOSApp() {
    val context = LocalContext.current
    var destination by remember { mutableStateOf(PIPSuriOSDestination.Splash) }
    var selectedInventoryItem by remember { mutableStateOf(InventoryItem.L96) }
    var morseInput by remember { mutableStateOf("") }
    var morseOutput by remember { mutableStateOf("") }
    var draftLoadout by remember { mutableStateOf(LoadoutConfiguration()) }
    var activeLoadout by remember { mutableStateOf(LoadoutConfiguration()) }

    Crossfade(
        targetState = destination,
        animationSpec = tween(durationMillis = 300),
        label = "PIPSuriOSNavigation"
    ) { currentDestination ->
        when (currentDestination) {
            PIPSuriOSDestination.Splash -> PIPSuriOSScreen(
                onFinished = { destination = PIPSuriOSDestination.Loading }
            )

            PIPSuriOSDestination.Loading -> LoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.ModeSelection }
            )

            PIPSuriOSDestination.ModeSelection -> ModeSelectionScreen(
                onOperationSelected = { destination = PIPSuriOSDestination.HomeOperation },
                onCivilianSelected = { destination = PIPSuriOSDestination.HomeCivilian }
            )

            PIPSuriOSDestination.HomeOperation -> HomeOperationScreen(
                onBack = { destination = PIPSuriOSDestination.ModeSelection },
                onInventorySelected = { destination = PIPSuriOSDestination.InventoryLoading },
                onMapSelected = { destination = PIPSuriOSDestination.MapLoading },
                onCommsSelected = { destination = PIPSuriOSDestination.CommsLoading },
                onCurrentGearSelected = { destination = PIPSuriOSDestination.CurrentGearLoading },
                onStatusSelected = { destination = PIPSuriOSDestination.StatusLoading }
            )

            PIPSuriOSDestination.HomeCivilian -> HomeCivilianScreen(
                onBack = { destination = PIPSuriOSDestination.ModeSelection }
            )

            PIPSuriOSDestination.InventoryLoading -> InventoryLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.InventoryModeSelection }
            )

            PIPSuriOSDestination.InventoryModeSelection -> InventoryModeSelectionScreen(
                onBack = { destination = PIPSuriOSDestination.HomeOperation },
                onArmorySelected = { destination = PIPSuriOSDestination.InventoryArmory },
                onConsumablesSelected = { destination = PIPSuriOSDestination.InventoryConsumables },
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
                entries = listOf("> DETON-A", "> THUNDER B", "> TANTO", "> MINI KNIFE", "> VOLCANO"),
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
                        InventoryItem.MINI_KNIFE ->
                            PIPSuriOSDestination.InventoryAccesories
                    }
                }
            )

            PIPSuriOSDestination.InventoryConsumables -> InventoryVisualMenuScreen(
                title = "INVENTORY - CONSUMABLES",
                entries = listOf("> BBs", "> GRENADES", "> GAS"),
                entryActions = mapOf(
                    "> BBs" to { destination = PIPSuriOSDestination.InventoryConsumablesBbs },
                    "> GRENADES" to { destination = PIPSuriOSDestination.InventoryConsumablesGrenades },
                    "> GAS" to { destination = PIPSuriOSDestination.InventoryConsumablesGas }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryModeSelection }
            )

            PIPSuriOSDestination.InventoryConsumablesBbs -> InventoryVisualMenuScreen(
                title = "CONSUMABLES - BBs",
                entries = listOf(
                    "> Random", "> 0,20", "> 0,20 TRACER", "> 0,28",
                    "> 0,30", "> 0,30 TRACER", "> 0,40", "> 0,45"
                ),
                scrollable = true,
                onBack = { destination = PIPSuriOSDestination.InventoryConsumables }
            )

            PIPSuriOSDestination.InventoryConsumablesGrenades -> InventoryVisualMenuScreen(
                title = "CONSUMABLES - GRENADES",
                entries = listOf("> 9mm CARTRIDGES", "> C02 VIALS", "> CASINGS"),
                entryActions = mapOf(
                    "> 9mm CARTRIDGES" to { destination = PIPSuriOSDestination.InventoryGrenadesCartridges }
                ),
                onBack = { destination = PIPSuriOSDestination.InventoryConsumables }
            )

            PIPSuriOSDestination.InventoryGrenadesCartridges -> InventoryVisualMenuScreen(
                title = "GRENADES - 9mm CARTRIDGES",
                entries = listOf("> SILVER", "> GOLD"),
                onBack = { destination = PIPSuriOSDestination.InventoryConsumablesGrenades }
            )

            PIPSuriOSDestination.InventoryConsumablesGas -> InventoryVisualMenuScreen(
                title = "CONSUMABLES - GAS",
                entries = listOf("> 06 KG", "> 08 KG", "> 10 KG", "> 12 KG", "> 14 KG"),
                onBack = { destination = PIPSuriOSDestination.InventoryConsumables }
            )

            PIPSuriOSDestination.InventoryLoadouts -> InventoryVisualMenuScreen(
                title = "INVENTORY - LOADOUTS",
                entries = listOf("> HEADGEAR", "> FRONT PANEL"),
                entryActions = mapOf(
                    "> HEADGEAR" to { destination = PIPSuriOSDestination.InventoryLoadoutsHeadgear },
                    "> FRONT PANEL" to { destination = PIPSuriOSDestination.InventoryLoadoutsFrontPanel }
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
                entries = listOf("> HELMET", "> NVG", "> GAS MASK", "> SECURITY GOGLES"),
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
                onApply = {
                    activeLoadout = draftLoadout.copy(accesories = draftLoadout.accesories.toSet())
                },
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
            )

            PIPSuriOSDestination.CurrentGearPrimaryWeapon -> PrimaryWeaponScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearSecondaryWeapon -> SecondaryWeaponScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.CurrentGearAccesories -> AccesoriesScreen(
                configuration = draftLoadout,
                onConfigurationChanged = { draftLoadout = it },
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
                onBack = { destination = PIPSuriOSDestination.CurrentGear }
            )

            PIPSuriOSDestination.StatusLoading -> StatusLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.Status }
            )

            PIPSuriOSDestination.Status -> StatusScreen(
                activeLoadout = activeLoadout,
                onDontForgetSelected = { destination = PIPSuriOSDestination.StatusDontForget },
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
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
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
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
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
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
}

@Composable
fun PIPSuriOSScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3_000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.brotherhood_emblem_pipgreen),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight(0.94f)
                .alpha(0.30f),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(PipGreenDim)
        )

        Column(
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
                text = "PIP-SuriOS v1.5",
                color = PipGreenDim,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "Brotherhood of Steel Mode",
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
