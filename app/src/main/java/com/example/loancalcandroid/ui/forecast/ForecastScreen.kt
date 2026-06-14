package com.example.loancalcandroid.ui.forecast

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters
import java.util.Calendar
import java.util.Date

private val ForecastSegmentSelected = Color(0xFF83D7E9)
private val ForecastSegmentUnselected = Color(0xFFE9E9E9)

@OptIn(ExperimentalMaterial3Api::class)
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
                modifier = Modifier.padding(bottom = 8.dp),
            )

            if (uiState.forecastEnabled) {
                ForecastInputRow(
                    label = stringResource(R.string.forecast_monthly_payment),
                    value = uiState.monthlyPayment,
                    onValueChange = viewModel::updateMonthlyPayment,
                    keyboardType = KeyboardType.Decimal,
                    error = uiState.monthlyPaymentError,
                )
                ForecastInputRow(
                    label = stringResource(R.string.forecast_days_before),
                    value = uiState.daysBeforePayment,
                    onValueChange = viewModel::updateDaysBeforePayment,
                    keyboardType = KeyboardType.Number,
                    error = uiState.daysError,
                    fieldWidth = 80.dp,
                )
                ForecastDateRow(
                    label = stringResource(R.string.forecast_start_date),
                    value = uiState.startDate,
                    onValueChange = viewModel::updateStartDate,
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.forecast_extra_type),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                ForecastTypeSegmentedControl(
                    decreaseAmount = uiState.decreaseAmount,
                    onSelectAmount = { viewModel.setDecreaseAmount(true) },
                    onSelectTerm = { viewModel.setDecreaseAmount(false) },
                )
                Spacer(modifier = Modifier.height(12.dp))

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
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            uiState.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onScheduleClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.quick_schedule))
            }
        }
    }
}

@Composable
private fun ForecastInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    fieldWidth: androidx.compose.ui.unit.Dp = 140.dp,
    error: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
                modifier = Modifier.widthIn(min = fieldWidth, max = fieldWidth),
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
private fun ForecastDateRow(
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
            modifier = Modifier.widthIn(min = 150.dp),
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
private fun ForecastTypeSegmentedControl(
    decreaseAmount: Boolean,
    onSelectAmount: () -> Unit,
    onSelectTerm: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ForecastSegment(
            text = stringResource(R.string.forecast_type_amount),
            selected = decreaseAmount,
            onClick = onSelectAmount,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(2.dp))
        ForecastSegment(
            text = stringResource(R.string.forecast_type_term),
            selected = !decreaseAmount,
            onClick = onSelectTerm,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ForecastSegment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) ForecastSegmentSelected else ForecastSegmentUnselected)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
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
