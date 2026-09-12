package com.example.loancalcandroid.ui.forecast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.FeatureTypeSegmentedControl
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.common.LoanDecimalOutlinedTextField
import com.example.loancalcandroid.ui.common.LoanNumberOutlinedTextField
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanBlueDark
import com.example.loancalcandroid.ui.theme.LoanBlueStart
import com.example.loancalcandroid.ui.theme.LoanGreen
import com.example.loancalcandroid.ui.theme.LoanRed
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.OfferButtonBackground
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.calculation.ForecastComparison
import kotlin.math.roundToInt

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
                LoanDecimalOutlinedTextField(
                    value = uiState.monthlyPayment,
                    onValueChange = viewModel::updateMonthlyPayment,
                    label = { Text(stringResource(R.string.forecast_monthly_payment)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.monthlyPaymentError != null,
                    supportingText = uiState.monthlyPaymentError?.let { { Text(it) } },
                )
                LoanNumberOutlinedTextField(
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

                uiState.comparison?.let { comparison ->
                    ForecastComparisonCard(comparison = comparison)
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

@Composable
private fun ForecastComparisonCard(
    comparison: ForecastComparison,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = OfferButtonBackground,
        border = BorderStroke(1.dp, LoanBlueStart),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.forecast_closes_in),
                    tint = LoanBlueDark,
                    modifier = Modifier.size(44.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.forecast_closes_in),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoanTextSecondary,
                    )
                    Text(
                        text = formatDuration(comparison.remainingMonthsWithForecast),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(
                            R.string.forecast_instead_of,
                            formatDuration(comparison.remainingMonthsWithoutForecast),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LoanTextSecondary,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = LoanBlueStart.copy(alpha = 0.35f),
            )

            ForecastMoneyRow(
                label = stringResource(R.string.forecast_overpay),
                amount = comparison.overpayWithForecast,
                percent = comparison.overpayPercentDelta,
            )
            ForecastMoneyRow(
                label = stringResource(R.string.forecast_total_payout),
                amount = comparison.totalWithForecast,
                strikethroughAmount = comparison.totalWithoutForecast,
            )
        }
    }
}

@Composable
private fun ForecastMoneyRow(
    label: String,
    amount: Double,
    percent: Double? = null,
    strikethroughAmount: Double? = null,
) {
    val amountText = stringResource(R.string.forecast_money, Formatters.money(amount))
    val strikethroughText = strikethroughAmount?.let {
        stringResource(R.string.forecast_money, Formatters.money(it))
    }
    val percentText = percent?.let { formatSignedPercent(it) }
    val baseStyle = MaterialTheme.typography.bodyLarge
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        val gapPx = with(density) { 8.dp.toPx() }
        val badgeExtraPx = with(density) { 20.dp.toPx() }
        val numbersGapPx = with(density) { 6.dp.toPx() }
        val fontSize = remember(
            label,
            amountText,
            strikethroughText,
            percentText,
            constraints.maxWidth,
            baseStyle,
        ) {
            fittedFontSize(
                textMeasurer = textMeasurer,
                baseStyle = baseStyle,
                maxWidthPx = constraints.maxWidth,
                rowGapPx = gapPx,
                label = label,
                amountText = amountText,
                strikethroughText = strikethroughText,
                percentText = percentText,
                badgeExtraPx = badgeExtraPx,
                numbersGapPx = numbersGapPx,
            )
        }
        val textStyle = baseStyle.copy(fontSize = fontSize)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f, fill = true),
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = amountText,
                    style = textStyle.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    softWrap = false,
                )
                if (percent != null && percentText != null) {
                    ForecastPercentBadge(text = percentText, percent = percent, fontSize = fontSize)
                }
                if (strikethroughText != null) {
                    Text(
                        text = strikethroughText,
                        style = textStyle.copy(color = LoanTextSecondary),
                        textDecoration = TextDecoration.LineThrough,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun ForecastPercentBadge(
    text: String,
    percent: Double,
    fontSize: TextUnit,
) {
    val color = deltaColor(percent)
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize),
            fontWeight = FontWeight.Medium,
            color = color,
            maxLines = 1,
            softWrap = false,
        )
    }
}

private fun fittedFontSize(
    textMeasurer: TextMeasurer,
    baseStyle: TextStyle,
    maxWidthPx: Int,
    rowGapPx: Float,
    label: String,
    amountText: String,
    strikethroughText: String?,
    percentText: String?,
    badgeExtraPx: Float,
    numbersGapPx: Float,
): TextUnit {
    val minSize = 10.sp
    var size = baseStyle.fontSize
    while (size.value > minSize.value) {
        val style = baseStyle.copy(fontSize = size)
        val labelWidth = textMeasurer.measure(
            text = label,
            style = style,
            maxLines = 1,
            softWrap = false,
        ).size.width
        var numbersWidth = textMeasurer.measure(
            text = amountText,
            style = style.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            softWrap = false,
        ).size.width
        if (strikethroughText != null) {
            numbersWidth += numbersGapPx.toInt() + textMeasurer.measure(
                text = strikethroughText,
                style = style,
                maxLines = 1,
                softWrap = false,
            ).size.width
        }
        if (percentText != null) {
            numbersWidth += numbersGapPx.toInt() + textMeasurer.measure(
                text = percentText,
                style = style,
                maxLines = 1,
                softWrap = false,
            ).size.width + badgeExtraPx.toInt()
        }
        if (labelWidth + rowGapPx + numbersWidth <= maxWidthPx) {
            return size
        }
        size = (size.value - 0.5f).sp
    }
    return minSize
}

@Composable
private fun formatDuration(months: Int): String {
    val years = months / 12
    val remainingMonths = months % 12
    val yearsText = pluralStringResource(R.plurals.forecast_duration_years, years, years)
    val monthsText = pluralStringResource(R.plurals.forecast_duration_months, remainingMonths, remainingMonths)
    return when {
        years > 0 && remainingMonths > 0 -> {
            stringResource(R.string.forecast_duration_combined, yearsText, monthsText)
        }
        years > 0 -> yearsText
        else -> monthsText
    }
}

private fun formatSignedPercent(value: Double): String {
    val rounded = value.roundToInt()
    return if (rounded > 0) "+$rounded%" else "$rounded%"
}

private fun deltaColor(value: Double): Color {
    return when {
        value < -0.005 -> LoanGreen
        value > 0.005 -> LoanRed
        else -> LoanTextSecondary
    }
}
