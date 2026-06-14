package com.example.loancalcandroid.ui.sumbypayment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.FeatureResultTable
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.theme.LoanTextSecondary

@Composable
fun SumByPaymentScreen(
    onBack: () -> Unit,
    viewModel: SumByPaymentViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.sum_by_payment),
        onBack = onBack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoanOutlinedTextField(
                value = uiState.monthlyPayment,
                onValueChange = viewModel::updateMonthlyPayment,
                label = { Text(stringResource(R.string.sum_by_payment_monthly)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoanOutlinedTextField(
                    value = uiState.startRate,
                    onValueChange = viewModel::updateStartRate,
                    label = { Text(stringResource(R.string.sum_by_payment_rate_from)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Text(
                    text = stringResource(R.string.sum_by_payment_range_to),
                    style = MaterialTheme.typography.bodyMedium,
                )
                LoanOutlinedTextField(
                    value = uiState.endRate,
                    onValueChange = viewModel::updateEndRate,
                    label = { Text(stringResource(R.string.sum_by_payment_rate_to)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoanOutlinedTextField(
                    value = uiState.startTerm,
                    onValueChange = viewModel::updateStartTerm,
                    label = { Text(stringResource(R.string.sum_by_payment_term_from)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Text(
                    text = stringResource(R.string.sum_by_payment_range_to),
                    style = MaterialTheme.typography.bodyMedium,
                )
                LoanOutlinedTextField(
                    value = uiState.endTerm,
                    onValueChange = viewModel::updateEndTerm,
                    label = { Text(stringResource(R.string.sum_by_payment_term_to)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }

            Text(
                text = stringResource(R.string.sum_by_payment_fields_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )

            uiState.validationError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            val rateRangeError = stringResource(R.string.sum_by_payment_error_rate)
            val rateZeroError = stringResource(R.string.sum_by_payment_error_rate_zero)
            val termRangeError = stringResource(R.string.sum_by_payment_error_term)
            val termZeroError = stringResource(R.string.sum_by_payment_error_term_zero)

            Button(
                onClick = {
                    viewModel.calculate(
                        rateRangeError = rateRangeError,
                        rateZeroError = rateZeroError,
                        termRangeError = termRangeError,
                        termZeroError = termZeroError,
                    )
                },
                enabled = !uiState.isCalculating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.sum_by_payment_calculate))
            }

            if (uiState.isCalculating) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.singleResultAmount?.let { amount ->
                Text(
                    text = amount,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                val term = uiState.singleResultTerm
                val rate = uiState.singleResultRate
                if (term != null && rate != null) {
                    Text(
                        text = stringResource(
                            R.string.sum_by_payment_max_sum_hint,
                            term,
                            "$rate%",
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            if (uiState.showTable && uiState.tableRows.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.sum_by_payment_table_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                FeatureResultTable(
                    headers = listOf(
                        stringResource(R.string.sum_by_payment_table_rate),
                        stringResource(R.string.sum_by_payment_table_term),
                        stringResource(R.string.sum_by_payment_table_max_sum),
                        stringResource(R.string.sum_by_payment_table_overpayment),
                    ),
                    rows = uiState.tableRows.map { row ->
                        listOf(
                            row.rate.toString(),
                            row.term.toString(),
                            row.maxSum,
                            row.overpayment,
                        )
                    },
                )
            }
        }
    }
}
