package com.suri.pipsurios

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.data.OperationConsumables
import com.suri.pipsurios.data.OperationInputValidator
import com.suri.pipsurios.ui.theme.PIPSuriOSTheme
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed

class OperationInputActivity : ComponentActivity() {
    private val step: String by lazy { intent.getStringExtra(EXTRA_STEP) ?: STEP_DATE_LOCATION }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finishWithBack()
        })
        setContent {
            PIPSuriOSTheme {
                when (step) {
                    STEP_CONSUMABLES -> ConsumablesInputScreen(
                        initialValues = CONSUMABLE_KEYS.map { intent.getStringExtra(it).orEmpty() },
                        onNext = ::finishConsumables,
                        onBack = ::finishWithBack
                    )
                    else -> DateLocationInputScreen(
                        initialDate = intent.getStringExtra(EXTRA_DATE).orEmpty(),
                        initialLocation = intent.getStringExtra(EXTRA_LOCATION).orEmpty(),
                        onNext = ::finishDateLocation,
                        onBack = ::finishWithBack
                    )
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

    private fun finishDateLocation(date: String, location: String) {
        setResult(RESULT_NEXT, Intent().putExtra(EXTRA_DATE, date).putExtra(EXTRA_LOCATION, location))
        finish()
    }

    private fun finishConsumables(values: OperationConsumables) {
        val data = Intent()
        values.asList().forEachIndexed { index, value ->
            data.putExtra(CONSUMABLE_KEYS[index], OperationInputValidator.formatDecimal(value))
        }
        setResult(RESULT_NEXT, data)
        finish()
    }

    private fun finishWithBack() {
        setResult(RESULT_BACK)
        finish()
    }

    private fun hideStatusBar() {
        window.decorView.windowInsetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.statusBars())
        }
    }

    companion object {
        const val RESULT_NEXT = RESULT_FIRST_USER + 9
        const val RESULT_BACK = RESULT_FIRST_USER + 10
        const val EXTRA_DATE = "operation_date"
        const val EXTRA_LOCATION = "operation_location"

        private const val EXTRA_STEP = "operation_step"
        private const val STEP_DATE_LOCATION = "date_location"
        private const val STEP_CONSUMABLES = "consumables"

        val CONSUMABLE_KEYS = listOf(
            "primary_mag", "secondary_mag", "grenades_40mm", "grenades_9mm",
            "grenades_co2", "primary_hpa", "secondary_hpa"
        )

        fun dateLocationIntent(context: Context, date: String, location: String): Intent =
            Intent(context, OperationInputActivity::class.java)
                .putExtra(EXTRA_STEP, STEP_DATE_LOCATION)
                .putExtra(EXTRA_DATE, date)
                .putExtra(EXTRA_LOCATION, location)

        fun consumablesIntent(context: Context, values: OperationConsumables?): Intent =
            Intent(context, OperationInputActivity::class.java)
                .putExtra(EXTRA_STEP, STEP_CONSUMABLES)
                .also { intent ->
                    values?.asList()?.forEachIndexed { index, value ->
                        intent.putExtra(CONSUMABLE_KEYS[index], OperationInputValidator.formatDecimal(value))
                    }
                }
    }
}

@Composable
private fun DateLocationInputScreen(
    initialDate: String,
    initialLocation: String,
    onNext: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var dateField by remember {
        mutableStateOf(TextFieldValue(initialDate, TextRange(initialDate.length)))
    }
    val date = dateField.text
    var location by remember { mutableStateOf(initialLocation) }
    val validDate = OperationInputValidator.isValidDate(date)
    val validLocation = OperationInputValidator.isValidLocation(location)
    VerticalInputFrame(
        title = "INPUT OPERATION - DATE & LOCATION",
        onBack = onBack,
        nextEnabled = validDate && validLocation,
        onNext = { onNext(date, location.trim()) }
    ) {
        OperationDateField(
            value = dateField,
            onValueChange = { value ->
                val formatted = OperationInputValidator.formatDateInput(value.text)
                dateField = TextFieldValue(formatted, TextRange(formatted.length))
            },
            isError = date.isNotEmpty() && !validDate
        )
        OperationTextField(
            label = "LOCATION",
            value = location,
            onValueChange = { location = it.take(80) },
            placeholder = "VALLE ARENA",
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Characters
        )
    }
}

@Composable
private fun ConsumablesInputScreen(
    initialValues: List<String>,
    onNext: (OperationConsumables) -> Unit,
    onBack: () -> Unit
) {
    val labels = listOf(
        "PRIMARY MAG", "SECONDARY MAG", "40mm GRENADES", "9mm GRENADES",
        "CO2 GRENADES", "PRIMARY HPA", "SECONDARY HPA"
    )
    var values by remember { mutableStateOf(initialValues) }
    val parsed = values.map(OperationInputValidator::parseDecimal)
    VerticalInputFrame(
        title = "INPUT OPERATION - CONSUMABLES",
        onBack = onBack,
        nextEnabled = parsed.all { it != null },
        onNext = {
            onNext(
                OperationConsumables(
                    primaryMag = parsed[0]!!,
                    secondaryMag = parsed[1]!!,
                    grenades40mm = parsed[2]!!,
                    grenades9mm = parsed[3]!!,
                    grenadesCo2 = parsed[4]!!,
                    primaryHpa = parsed[5]!!,
                    secondaryHpa = parsed[6]!!
                )
            )
        }
    ) {
        labels.forEachIndexed { index, label ->
            OperationTextField(
                label = label,
                value = values[index],
                onValueChange = { candidate ->
                    val filtered = candidate.filter { it.isDigit() || it == ',' || it == '.' }.take(8)
                    if (filtered.isEmpty() || filtered.matches(Regex("^\\d*(?:[.,]\\d{0,2})?$"))) {
                        values = values.toMutableList().also { it[index] = filtered }
                    }
                },
                placeholder = "0",
                keyboardType = KeyboardType.Decimal,
                isError = values[index].isNotEmpty() && parsed[index] == null
            )
        }
    }
}

@Composable
private fun VerticalInputFrame(
    title: String,
    onBack: () -> Unit,
    nextEnabled: Boolean,
    onNext: () -> Unit,
    fields: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack).imePadding()) {
        Text(
            title,
            color = PipGreen,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(20.dp)
        )
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp, bottom = 90.dp)
                .fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = fields
        )
        Row(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            VerticalAction("BACK", PipGreenDim, onBack)
            VerticalAction("NEXT", if (nextEnabled) PipGreen else PipGreenDim, onNext, nextEnabled)
        }
        Text(
            "PIP-SuriOS v1.7",
            color = PipGreenDim,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

@Composable
private fun OperationTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    isError: Boolean = false
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = if (isError) PipRed else PipGreen, fontSize = 16.sp,
            fontFamily = FontFamily.Monospace)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().border(1.dp, if (isError) PipRed else PipGreen),
            textStyle = TextStyle(color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace),
            placeholder = { Text(placeholder, color = PipGreenDim, fontFamily = FontFamily.Monospace) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                keyboardType = keyboardType,
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
private fun OperationDateField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            "DATE",
            color = if (isError) PipRed else PipGreen,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().border(1.dp, if (isError) PipRed else PipGreen),
            textStyle = TextStyle(color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace),
            placeholder = { Text("DD/MM/AAAA", color = PipGreenDim, fontFamily = FontFamily.Monospace) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
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
private fun VerticalAction(
    label: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Text(
        label,
        color = color,
        fontSize = 17.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.border(1.dp, color).clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    )
}

private fun OperationConsumables.asList() = listOf(
    primaryMag, secondaryMag, grenades40mm, grenades9mm, grenadesCo2, primaryHpa, secondaryHpa
)
