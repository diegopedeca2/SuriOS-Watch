package com.suri.pipsurios

import android.app.Activity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.suri.pipsurios.ui.screens.InformationAcknowledgementsScreen
import com.suri.pipsurios.ui.screens.InformationDisclaimersScreen
import com.suri.pipsurios.ui.screens.InformationScreen
import com.suri.pipsurios.ui.screens.InformationTestersScreen
import com.suri.pipsurios.ui.screens.TerminalOverlay
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme

class InformationActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContent {
            PIPSuriOSTheme {
                TerminalOverlay {
                    InformationApp(onExit = ::finishWithBack)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

    private fun finishWithBack() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun hideStatusBar() {
        window.decorView.windowInsetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.statusBars())
        }
    }
}

private enum class InformationDestination {
    ROOT,
    ACKNOWLEDGEMENTS,
    DISCLAIMERS,
    TESTERS
}

@Composable
private fun InformationApp(onExit: () -> Unit) {
    var destination by remember { mutableStateOf(InformationDestination.ROOT) }

    BackHandler {
        when (destination) {
            InformationDestination.ROOT -> onExit()
            else -> destination = InformationDestination.ROOT
        }
    }

    when (destination) {
        InformationDestination.ROOT -> InformationScreen(
            onAcknowledgementsSelected = {
                destination = InformationDestination.ACKNOWLEDGEMENTS
            },
            onDisclaimersSelected = {
                destination = InformationDestination.DISCLAIMERS
            },
            onTestersSelected = {
                destination = InformationDestination.TESTERS
            },
            onBack = onExit
        )
        InformationDestination.ACKNOWLEDGEMENTS -> InformationAcknowledgementsScreen(
            onBack = { destination = InformationDestination.ROOT }
        )
        InformationDestination.DISCLAIMERS -> InformationDisclaimersScreen(
            onBack = { destination = InformationDestination.ROOT }
        )
        InformationDestination.TESTERS -> InformationTestersScreen(
            onBack = { destination = InformationDestination.ROOT }
        )
    }
}
