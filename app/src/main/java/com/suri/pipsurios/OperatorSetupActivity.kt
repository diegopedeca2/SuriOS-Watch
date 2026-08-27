package com.suri.pipsurios

import android.app.Activity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.data.OperatorField
import com.suri.pipsurios.data.OperatorProfile
import com.suri.pipsurios.data.OperatorProfileRepository
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

class OperatorSetupActivity : ComponentActivity() {
    private val repository by lazy { OperatorProfileRepository.from(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finishWithBack()
        })
        setContent {
            PIPSuriOSTheme {
                OperatorSetupScreen(
                    initialProfile = repository.load(),
                    focusField = intent.getStringExtra(EXTRA_FOCUS_FIELD)
                        ?.let { value -> OperatorField.entries.firstOrNull { it.name == value } },
                    onSave = { profile ->
                        repository.save(profile)
                        setResult(RESULT_OK)
                        finish()
                    },
                    onBack = ::finishWithBack
                )
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

    private fun finishWithBack() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun hideStatusBar() {
        window.decorView.windowInsetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.statusBars())
        }
    }

    companion object {
        const val RESULT_OK = Activity.RESULT_OK
        const val EXTRA_FOCUS_FIELD = "operator_focus_field"
    }
}

@Composable
private fun OperatorSetupScreen(
    initialProfile: OperatorProfile,
    focusField: OperatorField?,
    onSave: (OperatorProfile) -> Unit,
    onBack: () -> Unit
) {
    var profile by remember { mutableStateOf(initialProfile) }
    val focusRequesters = remember {
        OperatorField.entries.associateWith { FocusRequester() }
    }
    LaunchedEffect(focusField) {
        focusField?.let { focusRequesters[it]?.requestFocus() }
    }
    val fields = listOf(
        OperatorField.ID to "SURI-14",
        OperatorField.NAME to "DIEGO PEREZ",
        OperatorField.CALLSIGN to "SURI",
        OperatorField.NUMBER to "01",
        OperatorField.COUNTRY to "SPAIN",
        OperatorField.TEAM to "PIP-SURI"
    )

    OperatorInputFrame(
        title = "SET-UP - OPERATOR",
        onBack = onBack,
        onSave = { onSave(profile.normalized()) }
    ) {
        fields.forEach { (field, placeholder) ->
            OperatorTextField(
                label = field.name,
                value = profile.valueFor(field),
                placeholder = placeholder,
                modifier = Modifier.focusRequester(focusRequesters.getValue(field)),
                onValueChange = { value ->
                    profile = profile.update(field, value.take(MAX_FIELD_LENGTH))
                }
            )
        }
    }
}

@Composable
private fun OperatorInputFrame(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    fields: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack).imePadding()) {
        Text(
            text = title,
            color = PipGreen,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(20.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp, bottom = 90.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = fields
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OperatorAction("BACK", PipGreenDim, onBack)
            OperatorAction("SAVE", PipGreen, onSave)
        }
        Text(
            text = "PIP-SuriOS v2.3",
            color = PipGreenDim,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

@Composable
private fun OperatorTextField(
    label: String,
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = PipGreen, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth().border(1.dp, PipGreen),
            textStyle = TextStyle(color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace),
            placeholder = { Text(placeholder, color = PipGreenDim, fontFamily = FontFamily.Monospace) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PipBlack,
                unfocusedContainerColor = PipBlack,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PipGreen
            )
        )
    }
}

@Composable
private fun OperatorAction(label: String, color: Color, onClick: () -> Unit) {
    Text(
        text = label,
        color = color,
        fontSize = 17.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .border(1.dp, color)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    )
}

private const val MAX_FIELD_LENGTH = 80
