package com.example.loancalcandroid.ui.forecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.util.Formatters

@Composable
fun ForecastScreen(
    loanId: Long,
    onBack: () -> Unit,
) {
    val viewModel: ForecastViewModel = loanViewModel(loanId, ::ForecastViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.menu_forecast),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.forecast_enable))
                Switch(
                    checked = uiState.forecastEnabled,
                    onCheckedChange = viewModel::setForecastEnabled,
                )
            }

            if (uiState.forecastEnabled) {
                OutlinedTextField(
                    value = uiState.monthlyPayment,
                    onValueChange = viewModel::updateMonthlyPayment,
                    label = { Text(stringResource(R.string.forecast_monthly_payment)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.monthlyPaymentError != null,
                    supportingText = uiState.monthlyPaymentError?.let { { Text(it) } },
                )
                OutlinedTextField(
                    value = uiState.daysBeforePayment,
                    onValueChange = viewModel::updateDaysBeforePayment,
                    label = { Text(stringResource(R.string.forecast_days_before)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.daysError != null,
                    supportingText = uiState.daysError?.let { { Text(it) } },
                )
                DatePickerField(
                    label = stringResource(R.string.forecast_start_date),
                    value = uiState.startDate,
                    onValueChange = viewModel::updateStartDate,
                )

                Text(text = stringResource(R.string.forecast_extra_type), style = MaterialTheme.typography.titleSmall)
                ForecastTypeRow(
                    selected = uiState.decreaseAmount,
                    onSelect = { viewModel.setDecreaseAmount(true) },
                    label = stringResource(R.string.forecast_type_amount),
                )
                ForecastTypeRow(
                    selected = !uiState.decreaseAmount,
                    onSelect = { viewModel.setDecreaseAmount(false) },
                    label = stringResource(R.string.forecast_type_term),
                )
            }

            uiState.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            uiState.message?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.primary)
            }

            uiState.lastCalculation?.let { calculation ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = LoanCardSurface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.forecast_result_title),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(stringResource(R.string.current_payment) + ": " + Formatters.money(calculation.currentPayment))
                        Text(stringResource(R.string.menu_extras_savings, Formatters.money(calculation.savedMoney)))
                        Text(stringResource(R.string.debt_label) + ": " + Formatters.money(calculation.owingAmount))
                    }
                }
            }

            Button(
                onClick = viewModel::calculateForecast,
                enabled = uiState.forecastEnabled && !uiState.isCalculating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isCalculating) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(stringResource(R.string.forecast_calculate))
            }
        }
    }
}

@Composable
private fun ForecastTypeRow(
    selected: Boolean,
    onSelect: () -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}
