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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.extraFormViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.model.ExtraType

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
    onSaved: () -> Unit,
) {
    val viewModel: ExtraFormViewModel = extraFormViewModel(viewModelStoreOwner, loanId, extraId, category, prefill) { app, handle, eId, cat, pre ->
        ExtraFormViewModel(app, handle, eId, cat, pre)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
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

            DatePickerField(
                label = stringResource(R.string.extra_payment_date),
                value = uiState.date,
                onValueChange = viewModel::updateDate,
            )
            uiState.dateError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            val amountLabel = if (uiState.selectedType == ExtraType.CHANGE_RATE) {
                stringResource(R.string.extra_payment_rate)
            } else {
                stringResource(R.string.extra_payment_amount)
            }
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text(amountLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
            )

            uiState.saveError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))
            ExtraRulesSection(type = uiState.selectedType)
        }
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

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = ExtraTypeUtils.label(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.extra_type)) },
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
