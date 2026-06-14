package com.example.loancalcandroid.ui.loan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.R

private val LOAN_TERM_PRESET_YEARS = listOf(1, 2, 3, 5, 10, 20)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoanTermPresetChips(
    selectedTermMonths: Int?,
    onTermMonthsSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LOAN_TERM_PRESET_YEARS.forEach { years ->
            val months = years * 12
            FilterChip(
                selected = selectedTermMonths == months,
                onClick = { onTermMonthsSelected(months) },
                label = {
                    Text(pluralStringResource(R.plurals.loan_term_preset_years, years, years))
                },
            )
        }
    }
}
