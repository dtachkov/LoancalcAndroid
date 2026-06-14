package com.example.loancalcandroid.ui.bestdate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.FeatureCalculationProgress
import com.example.loancalcandroid.ui.common.FeatureDateRow
import com.example.loancalcandroid.ui.common.FeatureMoneyInputRow
import com.example.loancalcandroid.ui.common.FeatureResultTable
import com.example.loancalcandroid.ui.common.FeatureTypeSegmentedControl
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters
import java.util.Calendar
import java.util.Date

@Composable
fun BestDateScreen(
    loanId: Long,
    onBack: () -> Unit,
    onAddExtra: (amount: String, dateMillis: Long, extraType: String) -> Unit,
) {
    val viewModel: BestDateViewModel = loanViewModel(loanId, ::BestDateViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.menu_best_date),
        onBack = onBack,
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.best_date_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )

            FeatureMoneyInputRow(
                label = stringResource(R.string.extra_payment_amount),
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                error = uiState.amountError,
            )

            Text(
                text = stringResource(R.string.best_date_extra_type),
                style = MaterialTheme.typography.bodyMedium,
            )
            FeatureTypeSegmentedControl(
                decreaseAmountLabel = stringResource(R.string.forecast_type_amount),
                decreaseTermLabel = stringResource(R.string.forecast_type_term),
                decreaseAmount = uiState.decreaseAmount,
                onSelectAmount = { viewModel.setDecreaseAmount(true) },
                onSelectTerm = { viewModel.setDecreaseAmount(false) },
            )

            BestDateDatePickerRow(
                label = stringResource(R.string.best_date_start_date),
                value = uiState.startDate,
                onValueChange = viewModel::updateStartDate,
            )
            BestDateDatePickerRow(
                label = stringResource(R.string.best_date_end_date),
                value = uiState.endDate,
                onValueChange = viewModel::updateEndDate,
            )
            uiState.dateError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (uiState.isCalculating) {
                FeatureCalculationProgress(
                    progress = uiState.progress,
                    max = uiState.progressMax,
                    statusText = stringResource(R.string.best_date_progress, uiState.progressText),
                )
                OutlinedButton(
                    onClick = viewModel::stopCalculation,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.best_date_stop))
                }
            } else {
                Button(
                    onClick = viewModel::calculate,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.forecast_calculate).uppercase())
                }
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            uiState.bestDate?.let { bestDate ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.best_date_result),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = Formatters.date(bestDate),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                val selectedIndex = uiState.rows.indexOfFirst { it.date == bestDate }
                FeatureResultTable(
                    headers = listOf(
                        stringResource(R.string.best_date_table_date),
                        stringResource(R.string.best_date_table_overpay),
                    ),
                    rows = uiState.rows.map { row ->
                        listOf(
                            Formatters.date(row.date),
                            Formatters.money(row.overpayment),
                        )
                    },
                    selectedRowIndex = selectedIndex.takeIf { it >= 0 },
                    modifier = Modifier.padding(top = 8.dp),
                )

                Button(
                    onClick = {
                        onAddExtra(
                            uiState.amount,
                            bestDate.time,
                            viewModel.selectedExtraType().name,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.best_date_add_extra))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BestDateDatePickerRow(
    label: String,
    value: Date,
    onValueChange: (Date) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    FeatureDateRow(
        label = label,
        value = value,
        formattedValue = Formatters.date(value),
        onClick = { showDialog = true },
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = value.time)
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                androidx.compose.material3.TextButton(
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
                androidx.compose.material3.TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
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
