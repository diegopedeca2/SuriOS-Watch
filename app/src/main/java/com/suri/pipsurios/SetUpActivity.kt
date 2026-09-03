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
import com.suri.pipsurios.ui.screens.SetUpCustomListsScreen
import com.suri.pipsurios.ui.screens.SetUpInputScreen
import com.suri.pipsurios.ui.screens.SetUpScreen
import com.suri.pipsurios.ui.screens.SetUpWeaponReplicasScreen
import com.suri.pipsurios.ui.screens.TerminalOverlay
import com.suri.pipsurios.ui.screens.UniformScreen
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme

class SetUpActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContent {
            PIPSuriOSTheme {
                TerminalOverlay {
                    SetUpApp(onExit = ::finishWithBack)
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

private enum class SetUpDestination {
    ROOT,
    INPUT,
    DATA,
    WEAPON_REPLICAS,
    CUSTOM_LISTS,
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
    var secondaryWeaponReturnDestination by remember {
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

    fun editAccessory(oldValue: String, newValue: String) {
        if (newValue in setupLoadout.accesoryOptions && newValue != oldValue) return
        val oldBuiltIn = com.suri.pipsurios.ui.screens.InventoryItem.entries
            .firstOrNull { it.displayName == oldValue }
        val newBuiltIn = com.suri.pipsurios.ui.screens.InventoryItem.entries
            .firstOrNull { it.displayName == newValue }
        val selected = oldBuiltIn?.let { it in setupLoadout.accesories }
            ?: (oldValue in setupLoadout.customAccesories)
        updateSetupLoadout(
            setupLoadout.copy(
                accesoryOptions = setupLoadout.accesoryOptions.map { if (it == oldValue) newValue else it },
                accesories = setupLoadout.accesories
                    .let { values -> if (oldBuiltIn != null) values - oldBuiltIn else values }
                    .let { values -> if (selected && newBuiltIn != null) values + newBuiltIn else values },
                customAccesories = setupLoadout.customAccesories
                    .minus(oldValue)
                    .let { values -> if (selected && newBuiltIn == null) values + newValue else values }
            )
        )
    }

    fun deleteAccessory(value: String) {
        val builtIn = com.suri.pipsurios.ui.screens.InventoryItem.entries
            .firstOrNull { it.displayName == value }
        updateSetupLoadout(
            setupLoadout.copy(
                accesoryOptions = setupLoadout.accesoryOptions - value,
                accesories = builtIn?.let { setupLoadout.accesories - it } ?: setupLoadout.accesories,
                customAccesories = setupLoadout.customAccesories - value
            )
        )
    }

    fun editFrontPanel(oldValue: String, newValue: String) {
        if (newValue in setupLoadout.frontPanelOptions && newValue != oldValue) return
        updateSetupLoadout(
            setupLoadout.copy(
                frontPanelOptions = setupLoadout.frontPanelOptions.map { if (it == oldValue) newValue else it },
                frontPanelRole = if (setupLoadout.frontPanelRole == oldValue) newValue else setupLoadout.frontPanelRole
            )
        )
    }

    fun deleteFrontPanel(value: String) {
        updateSetupLoadout(
            setupLoadout.copy(
                frontPanelOptions = setupLoadout.frontPanelOptions - value,
                frontPanelRole = setupLoadout.frontPanelRole?.takeUnless { it == value }
            )
        )
    }

    fun editUniform(oldValue: String, newValue: String) {
        if (newValue in setupLoadout.uniformOptions && newValue != oldValue) return
        updateSetupLoadout(
            setupLoadout.copy(
                uniformOptions = setupLoadout.uniformOptions.map { if (it == oldValue) newValue else it },
                uniform = if (setupLoadout.uniform == oldValue) newValue else setupLoadout.uniform
            )
        )
    }

    fun deleteUniform(value: String) {
        updateSetupLoadout(
            setupLoadout.copy(
                uniformOptions = setupLoadout.uniformOptions - value,
                uniform = setupLoadout.uniform?.takeUnless { it == value }
            )
        )
    }

    fun editPrimaryWeaponOption(oldValue: String, newValue: String) {
        val cleanValue = newValue.trim()
        if (cleanValue.isEmpty() ||
            setupLoadout.primaryWeaponOptions.any { it.equals(cleanValue, ignoreCase = true) && it != oldValue }
        ) return
        val base = setupLoadout.copy(
            primaryWeaponOptions = setupLoadout.primaryWeaponOptions.map {
                if (it == oldValue) cleanValue else it
            }
        )
        updateSetupLoadout(
            if (setupLoadout.primaryWeaponDisplayName() == oldValue) {
                base.withPrimaryWeaponOption(cleanValue)
            } else {
                base
            }
        )
    }

    fun deletePrimaryWeaponOption(value: String) {
        updateSetupLoadout(
            if (setupLoadout.primaryWeaponDisplayName() == value) {
                setupLoadout.copy(
                    primaryWeaponOptions = setupLoadout.primaryWeaponOptions - value,
                    primaryRole = null,
                    primaryWeapon = null,
                    primaryRoleText = null,
                    primaryModelText = null
                )
            } else {
                setupLoadout.copy(primaryWeaponOptions = setupLoadout.primaryWeaponOptions - value)
            }
        )
    }

    fun editSecondaryWeaponOption(oldValue: String, newValue: String) {
        val cleanValue = newValue.trim()
        if (cleanValue.isEmpty() ||
            setupLoadout.secondaryWeaponOptions.any { it.equals(cleanValue, ignoreCase = true) && it != oldValue }
        ) return
        val base = setupLoadout.copy(
            secondaryWeaponOptions = setupLoadout.secondaryWeaponOptions.map {
                if (it == oldValue) cleanValue else it
            }
        )
        updateSetupLoadout(
            if (setupLoadout.secondaryWeaponDisplayName() == oldValue) {
                base.withSecondaryWeaponOption(cleanValue)
            } else {
                base
            }
        )
    }

    fun deleteSecondaryWeaponOption(value: String) {
        updateSetupLoadout(
            if (setupLoadout.secondaryWeaponDisplayName() == value) {
                setupLoadout.copy(
                    secondaryWeaponOptions = setupLoadout.secondaryWeaponOptions - value,
                    secondaryType = null,
                    secondaryWeapon = null,
                    secondaryTypeText = null,
                    secondaryModelText = null
                )
            } else {
                setupLoadout.copy(secondaryWeaponOptions = setupLoadout.secondaryWeaponOptions - value)
            }
        )
    }

    fun openPrimaryWeapon(returnTo: SetUpDestination) {
        primaryWeaponReturnDestination = returnTo
        destination = SetUpDestination.PRIMARY_WEAPON
    }

    fun openSecondaryWeapon(returnTo: SetUpDestination) {
        secondaryWeaponReturnDestination = returnTo
        destination = SetUpDestination.SECONDARY_WEAPON
    }

    BackHandler {
        when (destination) {
            SetUpDestination.ROOT -> onExit()
            SetUpDestination.INPUT,
            SetUpDestination.DATA -> destination = SetUpDestination.ROOT
            SetUpDestination.WEAPON_REPLICAS -> destination = SetUpDestination.DATA
            SetUpDestination.CUSTOM_LISTS -> destination = SetUpDestination.DATA
            SetUpDestination.PRIMARY_WEAPON -> destination = primaryWeaponReturnDestination
            SetUpDestination.SECONDARY_WEAPON -> destination = secondaryWeaponReturnDestination
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
            onSecondaryWeaponSelected = { openSecondaryWeapon(SetUpDestination.INPUT) },
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
            onWeaponReplicasSelected = { destination = SetUpDestination.WEAPON_REPLICAS },
            onCustomListsSelected = { destination = SetUpDestination.CUSTOM_LISTS },
            onBack = { destination = SetUpDestination.ROOT }
        )

        SetUpDestination.WEAPON_REPLICAS -> SetUpWeaponReplicasScreen(
            setupLoadout = setupLoadout,
            onEditPrimaryWeapon = ::editPrimaryWeaponOption,
            onDeletePrimaryWeapon = ::deletePrimaryWeaponOption,
            onEditSecondaryWeapon = ::editSecondaryWeaponOption,
            onDeleteSecondaryWeapon = ::deleteSecondaryWeaponOption,
            onBack = { destination = SetUpDestination.DATA }
        )

        SetUpDestination.CUSTOM_LISTS -> SetUpCustomListsScreen(
            setupLoadout = setupLoadout,
            onEditAccessory = ::editAccessory,
            onDeleteAccessory = ::deleteAccessory,
            onEditFrontPanel = ::editFrontPanel,
            onDeleteFrontPanel = ::deleteFrontPanel,
            onEditUniform = ::editUniform,
            onDeleteUniform = ::deleteUniform,
            onBack = { destination = SetUpDestination.DATA }
        )

        SetUpDestination.PRIMARY_WEAPON -> PrimaryWeaponScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            catalogMode = true,
            onBack = { destination = primaryWeaponReturnDestination }
        )

        SetUpDestination.SECONDARY_WEAPON -> SecondaryWeaponScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            catalogMode = true,
            onBack = { destination = secondaryWeaponReturnDestination }
        )

        SetUpDestination.ACCESORIES -> AccesoriesScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            verticalLayout = true,
            accesoryOptions = setupLoadout.accesoryOptions,
            catalogMode = true,
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
            frontPanelOptions = setupLoadout.frontPanelOptions,
            onBack = { destination = SetUpDestination.INPUT }
        )

        SetUpDestination.UNIFORM -> UniformScreen(
            configuration = setupLoadout,
            onConfigurationChanged = ::updateSetupLoadout,
            titlePrefix = "SET-UP",
            uniformOptions = setupLoadout.uniformOptions,
            onBack = { destination = SetUpDestination.INPUT }
        )
    }
}
