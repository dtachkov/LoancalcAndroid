package com.example.loancalcandroid.ui.forecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.FeatureTypeSegmentedControl
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary

@Composable
fun ForecastScreen(
    loanId: Long,
    onBack: () -> Unit,
    onScheduleClick: () -> Unit,
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.forecast_enable),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = uiState.forecastEnabled,
                    onCheckedChange = viewModel::setForecastEnabled,
                )
            }
            Text(
                text = stringResource(R.string.forecast_enable_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )

            if (uiState.forecastEnabled) {
                OutlinedTextField(
                    value = uiState.monthlyPayment,
                    onValueChange = viewModel::updateMonthlyPayment,
                    label = { Text(stringResource(R.string.forecast_monthly_payment)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = uiState.monthlyPaymentError != null,
                    supportingText = uiState.monthlyPaymentError?.let { { Text(it) } },
                )
                OutlinedTextField(
                    value = uiState.daysBeforePayment,
                    onValueChange = viewModel::updateDaysBeforePayment,
                    label = { Text(stringResource(R.string.forecast_days_before)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.daysError != null,
                    supportingText = uiState.daysError?.let { { Text(it) } },
                )
                DatePickerField(
                    label = stringResource(R.string.forecast_start_date),
                    value = uiState.startDate,
                    onValueChange = viewModel::updateStartDate,
                )

                Text(
                    text = stringResource(R.string.forecast_extra_type),
                    style = MaterialTheme.typography.bodyMedium,
                )
                FeatureTypeSegmentedControl(
                    decreaseAmountLabel = stringResource(R.string.forecast_type_amount),
                    decreaseTermLabel = stringResource(R.string.forecast_type_term),
                    decreaseAmount = uiState.decreaseAmount,
                    onSelectAmount = { viewModel.setDecreaseAmount(true) },
                    onSelectTerm = { viewModel.setDecreaseAmount(false) },
                )

                Button(
                    onClick = viewModel::calculateForecast,
                    enabled = !uiState.isCalculating,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    if (uiState.isCalculating) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    }
                    Text(
                        text = stringResource(R.string.forecast_calculate).uppercase(),
                        fontWeight = FontWeight.Medium,
                    )
                }

                Text(
                    text = stringResource(R.string.forecast_detailed_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                )
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            uiState.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onScheduleClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.quick_schedule))
            }
        }
    }
}
