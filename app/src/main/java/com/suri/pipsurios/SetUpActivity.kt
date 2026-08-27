package com.suri.pipsurios

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.suri.pipsurios.data.LoadoutConfigurationRepository
import com.suri.pipsurios.data.OperatorField
import com.suri.pipsurios.data.OperatorProfileRepository
import com.suri.pipsurios.ui.screens.AccesoriesScreen
import com.suri.pipsurios.ui.screens.FrontPanelScreen
import com.suri.pipsurios.ui.screens.HeadgearScreen
import com.suri.pipsurios.ui.screens.PrimaryWeaponScreen
import com.suri.pipsurios.ui.screens.SecondaryWeaponScreen
import com.suri.pipsurios.ui.screens.SetUpDataScreen
import com.suri.pipsurios.ui.screens.SetUpInputScreen
import com.suri.pipsurios.ui.screens.SetUpScreen
import com.suri.pipsurios.ui.screens.UniformScreen
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme

class SetUpActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContent {
            PIPSuriOSTheme {
                SetUpApp(onExit = ::finishWithBack)
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

private enum class SetUpDestination {
    ROOT,
    INPUT,
    DATA,
    PRIMARY_WEAPON,
    SECONDARY_WEAPON,
    ACCESORIES,
    HEADGEAR,
    FRONT_PANEL,
    UNIFORM
}

@Composable
private fun SetUpApp(onExit: () -> Unit) {
    val context = LocalContext.current
    val loadoutRepository = remember(context) {
        LoadoutConfigurationRepository.from(context.applicationContext)
    }
    val operatorRepository = remember(context) {
        OperatorProfileRepository.from(context.applicationContext)
    }
    var destination by remember { mutableStateOf(SetUpDestination.ROOT) }
    var setupLoadout by remember { mutableStateOf(loadoutRepository.load()) }
    var operatorProfile by remember { mutableStateOf(operatorRepository.load()) }
    var primaryWeaponReturnDestination by remember {
        mutableStateOf(SetUpDestination.INPUT)
    }

    val operatorLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == OperatorSetupActivity.RESULT_OK) {
            operatorProfile = operatorRepository.load()
        }
    }

    fun openOperator(field: OperatorField? = null) {
        val intent = Intent(context, OperatorSetupActivity::class.java)
        field?.let { intent.putExtra(OperatorSetupActivity.EXTRA_FOCUS_FIELD, it.name) }
        operatorLauncher.launch(intent)
    }

    fun updateSetupLoadout(updated: com.suri.pipsurios.ui.state.LoadoutConfiguration) {
        setupLoadout = updated.copy(accesories = updated.accesories.toSet())
        loadoutRepository.save(setupLoadout)
    }

    fun deleteOperatorField(field: OperatorField) {
        val updated = operatorProfile.update(field, "").normalized()
        operatorRepository.save(updated)
        operatorProfile = updated
    }

    fun openPrimaryWeapon(returnTo: SetUpDestination) {
        primaryWeaponReturnDestination = returnTo
        destination = SetUpDestination.PRIMARY_WEAPON
    }

    BackHandler {
        when (destination) {
            SetUpDestination.ROOT -> onExit()
            SetUpDestination.INPUT,
            SetUpDestination.DATA -> destination = SetUpDestination.ROOT
            SetUpDestination.PRIMARY_WEAPON -> destination = primaryWeaponReturnDestination
            else -> destination = SetUpDestination.INPUT
        }
    }

    when (destination) {
        SetUpDestination.ROOT -> SetUpScreen(
            onInputSelected = { destination = SetUpDestination.INPUT },
            onDataSelected = { destination = SetUpDestination.DATA },
            onBack = onExit
        )

        SetUpDestination.INPUT -> SetUpInputScreen(
            onOperatorSelected = { openOperator() },
            onPrimaryWeaponSelected = { openPrimaryWeapon(SetUpDestination.INPUT) },
            onSecondaryWeaponSelected = { destination = SetUpDestination.SECONDARY_WEAPON },
            onAccesoriesSelected = { destination = SetUpDestination.ACCESORIES },
            onHeadgearSelected = { destination = SetUpDestination.HEADGEAR },
            onFrontPanelSelected = { destination = SetUpDestination.FRONT_PANEL },
            onUniformSelected = { destination = SetUpDestination.UNIFORM },
            onBack = { destination = SetUpDestination.ROOT }
        )

        SetUpDestination.DATA -> SetUpDataScreen(
            operatorProfile = operatorProfile,
            setupLoadout = setupLoadout,
            onEditOperatorField = ::openOperator,
            onDeleteOperatorField = ::deleteOperatorField,
            onEditPrimaryWeapon = { openPrimaryWeapon(SetUpDestination.DATA) },
            onDeletePrimaryRole = {
                updateSetupLoadout(
                    setupLoadout.copy(
                        primaryRole = null,
                        primaryWeapon = null,
                        primaryWeaponText = null
                    )
                )
            },
            onDeletePrimaryWeapon = {
                updateSetupLoadout(
                    setupLoadout.copy(primaryWeapon = null, primaryWeaponText = null)
                )
            },
            onBack = { destination = SetUpDestination.ROOT }
        )

        SetUpDestination.PRIMARY_WEAPON -> PrimaryWeaponScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            customWeaponInput = true,
            onBack = { destination = primaryWeaponReturnDestination }
        )

        SetUpDestination.SECONDARY_WEAPON -> SecondaryWeaponScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            verticalLayout = true,
            onBack = { destination = SetUpDestination.INPUT }
        )

        SetUpDestination.ACCESORIES -> AccesoriesScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            verticalLayout = true,
            onBack = { destination = SetUpDestination.INPUT }
        )

        SetUpDestination.HEADGEAR -> HeadgearScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            verticalLayout = true,
            onBack = { destination = SetUpDestination.INPUT }
        )

        SetUpDestination.FRONT_PANEL -> FrontPanelScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            verticalLayout = true,
            onBack = { destination = SetUpDestination.INPUT }
        )

        SetUpDestination.UNIFORM -> UniformScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            onBack = { destination = SetUpDestination.INPUT }
        )
    }
}
