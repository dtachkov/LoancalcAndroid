package com.example.loancalcandroid.ui.extras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanExtraViewModel
import ru.kredit.calculator.data.model.ExtraType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraFormScreen(
    loanId: Long,
    extraId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: ExtraFormViewModel = loanExtraViewModel(loanId, extraId ?: 0L) { app, lId, eId ->
        ExtraFormViewModel(app, lId, extraId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    val title = if (uiState.isEditMode) {
        stringResource(R.string.edit_extra)
    } else {
        stringResource(R.string.quick_early_payment)
    }

    LoanCalcScaffold(title = title, onBack = onBack) { innerPadding ->
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
            ExtraTypeDropdown(
                selected = uiState.selectedType,
                types = uiState.allowedTypes,
                onSelect = viewModel::selectType,
            )

            DatePickerField(
                label = stringResource(R.string.extra_date),
                value = uiState.date,
                onValueChange = viewModel::updateDate,
            )
            uiState.dateError?.let { error ->
                Text(text = error, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = {
                    Text(
                        if (uiState.selectedType == ExtraType.CHANGE_RATE) {
                            stringResource(R.string.extra_new_rate)
                        } else {
                            stringResource(R.string.extra_amount)
                        },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
            )

            uiState.saveError?.let { error ->
                Text(text = error, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(stringResource(R.string.save))
            }
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
    ) {
        OutlinedTextField(
            value = ExtraTypeLabels.label(selected),
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
                    text = { Text(ExtraTypeLabels.label(type)) },
                    onClick = {
                        onSelect(type)
                        expanded = false
                    },
                )
            }
        }
    }
}
