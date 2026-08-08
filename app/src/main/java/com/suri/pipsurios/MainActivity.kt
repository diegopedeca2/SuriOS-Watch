package com.suri.pipsurios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.suri.pipsurios.ui.screens.InventoryScreen
import com.suri.pipsurios.ui.screens.LoadingScreen
import com.suri.pipsurios.ui.screens.CivTakLoadingScreen
import com.suri.pipsurios.ui.screens.CommsLoadingScreen
import com.suri.pipsurios.ui.screens.CommsScreen
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

        setContent {
            PIPSuriOSTheme {
                PIPSuriOSApp()
            }
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
    Inventory,
    InventorySniper,
    InventoryAssault,
    InventoryDemolition,
    InventoryHandgun,
    InventoryAccesories,
    InventoryDetails,
    CommsLoading,
    Comms,
    MapLoading,
    MapModeSelection,
    MapTerrain,
    MapOperation,
    CivTakLoading,
    GoogleMapsLoading
}

@Composable
private fun PIPSuriOSApp() {
    var destination by remember { mutableStateOf(PIPSuriOSDestination.Splash) }
    var selectedInventoryItem by remember { mutableStateOf(InventoryItem.L96) }

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
                onCommsSelected = { destination = PIPSuriOSDestination.CommsLoading }
            )

            PIPSuriOSDestination.HomeCivilian -> HomeCivilianScreen(
                onBack = { destination = PIPSuriOSDestination.ModeSelection }
            )

            PIPSuriOSDestination.InventoryLoading -> InventoryLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.Inventory }
            )

            PIPSuriOSDestination.Inventory -> InventoryScreen(
                onBack = { destination = PIPSuriOSDestination.HomeOperation },
                onSniperSelected = { destination = PIPSuriOSDestination.InventorySniper },
                onAssaultSelected = { destination = PIPSuriOSDestination.InventoryAssault },
                onDemolitionSelected = { destination = PIPSuriOSDestination.InventoryDemolition },
                onHandgunSelected = { destination = PIPSuriOSDestination.InventoryHandgun },
                onAccesoriesSelected = { destination = PIPSuriOSDestination.InventoryAccesories }
            )

            PIPSuriOSDestination.InventorySniper -> InventoryCategoryScreen(
                title = "INVENTORY - SNIPER",
                entries = listOf("> L96", "> LevAR-15"),
                entryActions = mapOf(
                    "> L96" to {
                        selectedInventoryItem = InventoryItem.L96
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> LevAR-15" to {
                        selectedInventoryItem = InventoryItem.LEVAR_15
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                ),
                onBack = { destination = PIPSuriOSDestination.Inventory }
            )

            PIPSuriOSDestination.InventoryAssault -> InventoryCategoryScreen(
                title = "INVENTORY - ASSAULT",
                entries = listOf("> MCX", "> APC-9K"),
                entryActions = mapOf(
                    "> MCX" to {
                        selectedInventoryItem = InventoryItem.MCX
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> APC-9K" to {
                        selectedInventoryItem = InventoryItem.APC_9K
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                ),
                onBack = { destination = PIPSuriOSDestination.Inventory }
            )

            PIPSuriOSDestination.InventoryDemolition -> InventoryCategoryScreen(
                title = "INVENTORY - DEMOLITION",
                entries = listOf("> MGL", "> VOLCANO"),
                entryActions = mapOf(
                    "> MGL" to {
                        selectedInventoryItem = InventoryItem.MGL
                        destination = PIPSuriOSDestination.InventoryDetails
                    },
                    "> VOLCANO" to {
                        selectedInventoryItem = InventoryItem.VOLCANO
                        destination = PIPSuriOSDestination.InventoryDetails
                    }
                ),
                onBack = { destination = PIPSuriOSDestination.Inventory }
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
                onBack = { destination = PIPSuriOSDestination.Inventory }
            )

            PIPSuriOSDestination.InventoryAccesories -> InventoryCategoryScreen(
                title = "INVENTORY - ACCESORIES",
                entries = listOf("> DETON-A", "> THUNDER B", "> TANTO", "> MINI KNIFE"),
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
                onBack = { destination = PIPSuriOSDestination.Inventory }
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

            PIPSuriOSDestination.CommsLoading -> CommsLoadingScreen(
                onFinished = { destination = PIPSuriOSDestination.Comms }
            )

            PIPSuriOSDestination.Comms -> CommsScreen(
                onBack = { destination = PIPSuriOSDestination.HomeOperation }
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
                text = "PIP-SuriOS v1.0",
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
