package com.example.loancalcandroid.ui.loan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.R
import com.example.loancalcandroid.util.Formatters

private val LOAN_TERM_PRESET_YEARS = listOf(1, 2, 3, 5, 10, 20)
private val LOAN_AMOUNT_PRESETS = listOf(
    100_000L,
    200_000L,
    300_000L,
    400_000L,
    500_000L,
    1_000_000L,
    2_000_000L,
    5_000_000L,
)

@Composable
fun LoanTermPresetChips(
    selectedTermMonths: Int?,
    onTermMonthsSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(LOAN_TERM_PRESET_YEARS, key = { it }) { years ->
            val months = years * 12
            FilterChip(
                selected = selectedTermMonths == months,
                onClick = { onTermMonthsSelected(months) },
                modifier = Modifier.focusProperties { canFocus = false },
                label = {
                    Text(pluralStringResource(R.plurals.loan_term_preset_years, years, years))
                },
            )
        }
    }
}

@Composable
fun LoanAmountPresetChips(
    selectedAmountText: String,
    onAmountSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedAmount = Formatters.parseMoney(selectedAmountText).toLong()
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(LOAN_AMOUNT_PRESETS, key = { it }) { amount ->
            FilterChip(
                selected = selectedAmount == amount,
                onClick = { onAmountSelected(amount.toString()) },
                modifier = Modifier.focusProperties { canFocus = false },
                label = {
                    Text(stringResource(R.string.loan_amount_preset_thousands, amount / 1_000))
                },
            )
        }
    }
}
