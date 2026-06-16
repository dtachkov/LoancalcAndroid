package com.example.loancalcandroid.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.loancalcandroid.R
import com.example.loancalcandroid.util.Formatters
import com.example.loancalcandroid.util.toDatePickerMillis
import com.example.loancalcandroid.util.toLocalDateFromDatePicker
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: Date?,
    onValueChange: (Date) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val displayText = Formatters.date(value)
    val interactionSource = remember { MutableInteractionSource() }

    LoanOutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        enabled = false,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = interactionSource,
            ) { showDialog = true },
        singleLine = true,
    )

    if (showDialog) {
        val initialMillis = value?.toDatePickerMillis() ?: Date().toDatePickerMillis()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onValueChange(millis.toLocalDateFromDatePicker())
                        }
                        showDialog = false
                    },
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
