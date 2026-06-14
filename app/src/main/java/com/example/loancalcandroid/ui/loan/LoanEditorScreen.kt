package com.example.loancalcandroid.ui.loan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.review.RequestRuStoreReviewEffect
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.common.LoanDecimalOutlinedTextField
import com.example.loancalcandroid.ui.common.LoanNumberOutlinedTextField
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
import com.example.loancalcandroid.ui.loanEditorViewModel
import ru.kredit.calculator.data.model.LoanType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEditorScreen(
    loanId: Long?,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
) {
    val viewModel: LoanEditorViewModel = loanEditorViewModel(loanId) { app, id ->
        LoanEditorViewModel(app, id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RequestRuStoreReviewEffect(uiState.reviewRequestTrigger)

    LaunchedEffect(uiState.savedLoanId) {
        uiState.savedLoanId?.let { onSaved(it) }
    }

    val title = if (uiState.isEditMode) {
        stringResource(R.string.action_edit_loan)
    } else {
        stringResource(R.string.add_loan)
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
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text(stringResource(R.string.loan_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            LoanDecimalOutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text(stringResource(R.string.loan_editor_amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
            )
            LoanDecimalOutlinedTextField(
                value = uiState.rate,
                onValueChange = viewModel::updateRate,
                label = { Text(stringResource(R.string.loan_editor_rate)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.rateError != null,
                supportingText = uiState.rateError?.let { { Text(it) } },
            )
            LoanNumberOutlinedTextField(
                value = uiState.term,
                onValueChange = viewModel::updateTerm,
                label = { Text(stringResource(R.string.loan_editor_term)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.termError != null,
                supportingText = uiState.termError?.let { { Text(it) } },
            )
            LoanTermPresetChips(
                selectedTermMonths = uiState.term.trim().toIntOrNull(),
                onTermMonthsSelected = { viewModel.updateTerm(it.toString()) },
            )

            Text(text = stringResource(R.string.loan_type), style = MaterialTheme.typography.titleSmall)
            LoanTypeRow(LoanType.ANNUITY, uiState.loanType, viewModel::updateLoanType, stringResource(R.string.loan_type_annuity))
            LoanTypeRow(LoanType.GRADE, uiState.loanType, viewModel::updateLoanType, stringResource(R.string.loan_type_graded))

            DatePickerField(
                label = stringResource(R.string.loan_first_payment_date),
                value = uiState.firstPaymentDate,
                onValueChange = viewModel::updateFirstPaymentDate,
            )
            uiState.dateError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            DatePickerField(
                label = stringResource(R.string.loan_editor_issue_date),
                value = uiState.dateOfIssue,
                onValueChange = viewModel::updateDateOfIssue,
            )
            Text(
                text = stringResource(R.string.loan_editor_issue_date_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            TextButton(
                onClick = viewModel::toggleAdvanced,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (uiState.showAdvanced) {
                        stringResource(R.string.hide_advanced)
                    } else {
                        stringResource(R.string.show_advanced)
                    },
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            if (uiState.showAdvanced) {
                SwitchRow(stringResource(R.string.option_consider_days_off), uiState.considerDaysOff, viewModel::toggleConsiderDaysOff)
                SwitchRow(stringResource(R.string.option_last_day_of_month), uiState.payOnLastDayOfMonth, viewModel::togglePayOnLastDayOfMonth)
                SwitchRow(stringResource(R.string.option_apply_extras_immediately), uiState.applyExtrasImmediately, viewModel::toggleApplyExtrasImmediately)
                SwitchRow(stringResource(R.string.option_sberbank_balance), uiState.calculateExtrasByBalanceLikeSberbank, viewModel::toggleCalculateExtrasByBalanceLikeSberbank)
                SwitchRow(stringResource(R.string.option_ignore_passed_periods), uiState.ignorePassedPeriodsAfterRateChange, viewModel::toggleIgnorePassedPeriodsAfterRateChange)
                SwitchRow(stringResource(R.string.option_extra_day_in_month), uiState.extraDayInMonth, viewModel::toggleExtraDayInMonth)
            }

            uiState.saveError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
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

@Composable
private fun LoanTypeRow(
    type: LoanType,
    selected: LoanType,
    onSelect: (LoanType) -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected == type, onClick = { onSelect(type) })
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
