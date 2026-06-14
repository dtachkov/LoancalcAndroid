package com.example.loancalcandroid.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.R

@Composable
fun NotificationSettingsDialog(
    days: Int,
    hour: Int,
    minute: Int,
    onDismiss: () -> Unit,
    onConfirm: (days: Int, hour: Int, minute: Int) -> Unit,
) {
    var daysText by remember(days) { mutableStateOf(days.toString()) }
    var hourText by remember(hour) { mutableStateOf(hour.toString()) }
    var minuteText by remember(minute) { mutableStateOf(minute.toString()) }

    val daysError = daysText.toIntOrNull()?.let { null }
        ?: stringResource(R.string.error_required_field)
    val hourValue = hourText.toIntOrNull()
    val hourError = when {
        hourText.isBlank() -> stringResource(R.string.error_invalid_hour)
        hourValue == null || hourValue !in 0..23 -> stringResource(R.string.error_invalid_hour)
        else -> null
    }
    val minuteValue = minuteText.toIntOrNull()
    val minuteError = when {
        minuteText.isBlank() -> stringResource(R.string.error_invalid_minute)
        minuteValue == null || minuteValue !in 0..59 -> stringResource(R.string.error_invalid_minute)
        else -> null
    }

    val isValid = daysText.isNotBlank() &&
        daysText.toIntOrNull() != null &&
        hourError == null &&
        minuteError == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_notification_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(stringResource(R.string.label_remind_of))
                    OutlinedTextField(
                        value = daysText,
                        onValueChange = { daysText = it.filter { ch -> ch.isDigit() } },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = daysError != null && daysText.isNotBlank(),
                    )
                    Text(stringResource(R.string.label_days))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(stringResource(R.string.label_at))
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { hourText = it.filter { ch -> ch.isDigit() } },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = hourError != null,
                        supportingText = hourError?.let { { Text(it) } },
                    )
                    Text(stringResource(R.string.label_hours))
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { minuteText = it.filter { ch -> ch.isDigit() } },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = minuteError != null,
                        supportingText = minuteError?.let { { Text(it) } },
                    )
                    Text(stringResource(R.string.label_minutes))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        daysText.toInt(),
                        hourValue ?: 0,
                        minuteValue ?: 0,
                    )
                },
                enabled = isValid,
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
