package com.example.loancalcandroid.ui.extras

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.review.RequestRuStoreReviewEffect
import com.example.loancalcandroid.ui.common.FeatureDateRow
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.common.LoanDecimalOutlinedTextField
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
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
    prefill: ExtraFormPrefill = ExtraFormPrefill(),
    viewModelStoreOwner: ViewModelStoreOwner,
    onBack: () -> Unit,
    onSaved: (ExtraCategory) -> Unit,
    onExtraTypesHelpClick: () -> Unit = {},
) {
    val viewModel: ExtraFormViewModel = extraFormViewModel(viewModelStoreOwner, loanId, extraId, category, prefill) { app, handle, eId, cat, pre ->
        ExtraFormViewModel(app, handle, eId, cat, pre)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    RequestRuStoreReviewEffect(uiState.reviewRequestTrigger)

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved(uiState.category)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoanOutlinedTextField(
                value = uiState.documentNumber,
                onValueChange = viewModel::updateDocumentNumber,
                label = { Text(stringResource(R.string.extra_document_number)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            ExtraTypeDropdown(
                selected = uiState.selectedType,
                types = uiState.allowedTypes,
                onSelect = viewModel::selectType,
            )

            FeatureDateRow(
                label = stringResource(R.string.extra_payment_date),
                value = uiState.date,
                formattedValue = Formatters.shortDate(uiState.date),
                onClick = { showDatePicker = true },
            )
            uiState.dateError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            LoanDecimalOutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = {
                    Text(
                        stringResource(
                            if (uiState.selectedType == ExtraType.CHANGE_RATE) {
                                R.string.extra_payment_rate
                            } else {
                                R.string.extra_payment_amount
                            },
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
            )

            if (uiState.showInterestBreakdown) {
                ExtraBreakdownRow(
                    label = stringResource(R.string.extra_interest_to_pay),
                    value = Formatters.moneyFixed(uiState.interestToPay),
                    emphasized = false,
                )
                ExtraBreakdownRow(
                    label = stringResource(R.string.extra_net_amount),
                    value = if (uiState.netExtraIsError) {
                        "0.0"
                    } else {
                        Formatters.moneyFixed(uiState.netExtraAmount)
                    },
                    emphasized = true,
                    isError = uiState.netExtraIsError,
                )
            }

            uiState.saveError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))
            ExtraRulesSection(
                type = uiState.selectedType,
                onOpenFullHelp = onExtraTypesHelpClick,
            )
        }
    }

    if (showDatePicker) {
        val initialMillis = uiState.date?.time ?: System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.updateDate(millis.toLocalDate())
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ExtraBreakdownRow(
    label: String,
    value: String,
    emphasized: Boolean,
    isError: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Bold,
            ),
            color = when {
                isError -> MaterialTheme.colorScheme.error
                emphasized -> MaterialTheme.colorScheme.onSurface
                else -> LoanTextSecondary
            },
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp),
        )
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
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            LoanOutlinedTextField(
                value = ExtraTypeUtils.label(selected),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
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
    }
}

@Composable
private fun ExtraRulesSection(
    type: ExtraType,
    onOpenFullHelp: () -> Unit,
) {
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
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
                Text(
                    text = ExtraTypeUtils.description(type),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                )
                TextButton(onClick = onOpenFullHelp) {
                    Text(stringResource(R.string.label_extra_types_help))
                }
            }
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
