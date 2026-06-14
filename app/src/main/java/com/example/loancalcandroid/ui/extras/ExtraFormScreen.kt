package com.example.loancalcandroid.ui.extras

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.extraFormViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.model.ExtraType
import java.util.Calendar
import java.util.Date

private val ExtraRulesHeaderColor = Color(0xFFE8F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraFormScreen(
    loanId: Long,
    extraId: Long?,
    category: ExtraCategory,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: ExtraFormViewModel = extraFormViewModel(loanId, extraId, category) { app, lId, eId, cat ->
        ExtraFormViewModel(app, lId, eId, cat)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    val title = when {
        uiState.isEditMode -> stringResource(R.string.title_edit_extra_payment)
        category == ExtraCategory.COMMISSION -> stringResource(R.string.add_commission)
        else -> stringResource(R.string.title_add_extra_payment)
    }

    LoanCalcScaffold(
        title = title,
        onBack = onBack,
        actions = {
            TextButton(
                onClick = viewModel::save,
                enabled = !uiState.isSaving && !uiState.isLoading,
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
            return@LoanCalcScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ExtraDocumentNumberField(
                value = uiState.documentNumber,
                onValueChange = viewModel::updateDocumentNumber,
            )

            ExtraTypeDropdown(
                selected = uiState.selectedType,
                types = uiState.allowedTypes,
                onSelect = viewModel::selectType,
            )

            ExtraDateRow(
                label = stringResource(R.string.extra_payment_date),
                value = uiState.date,
                onValueChange = viewModel::updateDate,
            )
            uiState.dateError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            val amountLabel = if (uiState.selectedType == ExtraType.CHANGE_RATE) {
                stringResource(R.string.extra_payment_rate)
            } else {
                stringResource(R.string.extra_payment_amount)
            }
            ExtraInputRow(
                label = amountLabel,
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                keyboardType = KeyboardType.Decimal,
                error = uiState.amountError,
            )

            uiState.saveError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))
            ExtraRulesSection(type = uiState.selectedType)
        }
    }
}

@Composable
private fun ExtraDocumentNumberField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium,
            decorationBox = { innerTextField ->
                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.extra_document_number),
                                style = MaterialTheme.typography.titleMedium,
                                color = LoanTextSecondary,
                            )
                        }
                        innerTextField()
                    }
                    HorizontalDivider(color = LoanTextSecondary.copy(alpha = 0.5f))
                }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtraTypeDropdown(
    selected: ExtraType,
    types: List<ExtraType>,
    onSelect: (ExtraType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.extra_type),
            style = MaterialTheme.typography.bodyMedium,
            color = LoanTextSecondary,
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .clickable { expanded = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = ExtraTypeUtils.label(selected),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(ExtraTypeUtils.label(type)) },
                        onClick = {
                            onSelect(type)
                            expanded = false
                        },
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun ExtraInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    error: String? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.widthIn(min = 140.dp, max = 140.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                decorationBox = { innerTextField ->
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            innerTextField()
                        }
                        HorizontalDivider(
                            color = if (error != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        )
                    }
                },
            )
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtraDateRow(
    label: String,
    value: Date?,
    onValueChange: (Date) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { showDialog = true },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = Formatters.date(value),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 140.dp),
        )
    }
    HorizontalDivider()

    if (showDialog) {
        val initialMillis = value?.time ?: System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onValueChange(millis.toLocalDate())
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

@Composable
private fun ExtraRulesSection(type: ExtraType) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExtraRulesHeaderColor)
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.extra_payment_rules),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = LoanTextSecondary,
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = ExtraTypeUtils.description(type),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            )
        }
    }
}

private fun Long.toLocalDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
