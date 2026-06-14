package com.example.loancalcandroid.ui.schedule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanBlueDark
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.SchedulePreviousBar
import com.example.loancalcandroid.ui.theme.ScheduleRowExtra
import com.example.loancalcandroid.ui.theme.ScheduleRowOdd
import com.example.loancalcandroid.ui.theme.ScheduleRowSelected
import com.example.loancalcandroid.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    loanId: Long,
    onBack: () -> Unit,
) {
    val viewModel: ScheduleViewModel = loanViewModel(loanId, ::ScheduleViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.quick_schedule),
        onBack = onBack,
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = uiState.error.orEmpty())
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                        Tab(
                            selected = uiState.selectedTab == 0,
                            onClick = { viewModel.selectTab(0) },
                            text = { Text(stringResource(R.string.schedule_tab_brief)) },
                        )
                        Tab(
                            selected = uiState.selectedTab == 1,
                            onClick = { viewModel.selectTab(1) },
                            text = { Text(stringResource(R.string.schedule_tab_detailed)) },
                        )
                    }

                    when (uiState.selectedTab) {
                        0 -> BriefScheduleTab(
                            rows = uiState.visibleRows,
                            summary = uiState.summary,
                            showPreviousPayments = uiState.showPreviousPayments,
                            hasHiddenRows = uiState.hasHiddenRows,
                            onTogglePrevious = viewModel::toggleShowPreviousPayments,
                        )
                        1 -> DetailedScheduleTab(rows = uiState.rows)
                    }
                }
            }
        }
    }
}

@Composable
private fun BriefScheduleTab(
    rows: List<ScheduleRow>,
    summary: ScheduleSummary?,
    showPreviousPayments: Boolean,
    hasHiddenRows: Boolean,
    onTogglePrevious: () -> Unit,
) {
    val listState = rememberLazyListState()
    val arrowRotation by animateFloatAsState(
        targetValue = if (showPreviousPayments) 0f else 180f,
        label = "previousArrow",
    )

    Column(modifier = Modifier.fillMaxSize()) {
        BriefHeaderRow()

        if (hasHiddenRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SchedulePreviousBar)
                    .clickable(onClick = onTogglePrevious)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.schedule_previous_payments),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.rotate(arrowRotation),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
        ) {
            items(rows, key = { "${it.listIndex}_${it.type}" }) { row ->
                when (row.type) {
                    ScheduleRowType.PAYMENT -> BriefPaymentRow(row)
                    ScheduleRowType.EXTRA -> BriefExtraRow(row, isRateChange = false)
                    ScheduleRowType.CHANGE_RATE -> BriefExtraRow(row, isRateChange = true)
                }
            }
        }

        summary?.let { ScheduleSummaryBlock(it) }
    }

    LaunchedEffect(showPreviousPayments) {
        if (showPreviousPayments) {
            listState.animateScrollToItem(0)
        }
    }
}

@Composable
private fun DetailedScheduleTab(rows: List<ScheduleRow>) {
    val listState = rememberLazyListState()
    val currentIndex = rows.indexOfFirst { it.isCurrent }.coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize()) {
        DetailedHeaderRow()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            items(rows, key = { "${it.listIndex}_${it.type}" }) { row ->
                when (row.type) {
                    ScheduleRowType.CHANGE_RATE -> DetailedRateChangeRow(row)
                    else -> DetailedPaymentRow(row, isExtra = row.type == ScheduleRowType.EXTRA)
                }
            }
        }
    }

    LaunchedEffect(rows) {
        if (rows.isNotEmpty()) {
            listState.animateScrollToItem(currentIndex.coerceAtMost(rows.lastIndex))
        }
    }
}

@Composable
private fun BriefHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LoanBlueDark)
            .padding(vertical = 8.dp),
    ) {
        ScheduleHeaderCell(stringResource(R.string.schedule_col_number), 36.dp)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_date), 0.dp, weight = 1f)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_payment), 0.dp, weight = 1f)
        ScheduleHeaderCell("", 40.dp)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_balance), 0.dp, weight = 1f)
    }
    HorizontalDivider(color = LoanBlueDark, thickness = 2.dp)
}

@Composable
private fun DetailedHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LoanBlueDark)
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        ScheduleHeaderCell(stringResource(R.string.schedule_col_date), 52.dp)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_payment), 88.dp)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_interest), 88.dp)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_principal), 88.dp)
        ScheduleHeaderCell(stringResource(R.string.schedule_col_balance), 88.dp)
    }
    HorizontalDivider(color = LoanBlueDark, thickness = 2.dp)
}

@Composable
private fun BriefPaymentRow(row: ScheduleRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground(row, isExtra = false))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScheduleBodyCell(row.displayNumber, 36.dp, fontWeight = FontWeight.Normal)
        ScheduleBodyCell(
            text = Formatters.date(row.date),
            width = 0.dp,
            weight = 1f,
            leadingIcon = if (row.date.isWeekend()) {
                {
                    Icon(
                        Icons.Outlined.Event,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp),
                        tint = LoanBlueDark,
                    )
                }
            } else {
                null
            },
        )
        ScheduleBodyCell(Formatters.money(row.total), 0.dp, weight = 1f)
        Box(modifier = Modifier.width(40.dp))
        ScheduleBodyCell(Formatters.money(row.endBalance), 0.dp, weight = 1f)
    }
}

@Composable
private fun BriefExtraRow(row: ScheduleRow, isRateChange: Boolean) {
    val amountText = if (isRateChange) {
        Formatters.percent(row.rateExtra.toFloat())
    } else {
        Formatters.money(row.extraAmount)
    }
    val typeLabel = if (isRateChange) {
        stringResource(R.string.schedule_extra_rate_change)
    } else {
        stringResource(R.string.schedule_extra_payment)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground(row, isExtra = true))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScheduleBodyCell(row.displayNumber, 36.dp)
        ScheduleBodyCell(typeLabel, 0.dp, weight = 1f, textAlign = TextAlign.Start)
        ScheduleBodyCell(amountText, 0.dp, weight = 1f)
        ScheduleBodyCell(Formatters.monthDay(row.date), 40.dp)
        ScheduleBodyCell(Formatters.money(row.endBalance), 0.dp, weight = 1f)
    }
}

@Composable
private fun DetailedPaymentRow(row: ScheduleRow, isExtra: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground(row, isExtra = isExtra)),
    ) {
        if (row.isNewYear) {
            Text(
                text = stringResource(R.string.schedule_year_label, Formatters.year(row.date)),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LoanBlueDark.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = LoanBlueDark,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScheduleBodyCell(Formatters.monthDay(row.date), 52.dp)
            ScheduleBodyCell(
                text = if (isExtra) Formatters.money(row.extraAmount.coerceAtLeast(row.total)) else Formatters.money(row.total),
                width = 88.dp,
                leadingIcon = if (isExtra) {
                    {
                        Icon(
                            Icons.Outlined.Savings,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp),
                            tint = LoanBlueDark,
                        )
                    }
                } else {
                    null
                },
            )
            ScheduleBodyCell(Formatters.money(row.interest), 88.dp)
            ScheduleBodyCell(Formatters.money(row.principal), 88.dp)
            ScheduleBodyCell(Formatters.money(row.endBalance), 88.dp)
        }
    }
}

@Composable
private fun DetailedRateChangeRow(row: ScheduleRow) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScheduleRowExtra),
    ) {
        if (row.isNewYear) {
            Text(
                text = stringResource(R.string.schedule_year_label, Formatters.year(row.date)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = Formatters.monthDay(row.date),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(52.dp),
            )
            Text(
                text = stringResource(R.string.schedule_new_rate, Formatters.percent(row.rateExtra.toFloat())),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ScheduleSummaryBlock(summary: ScheduleSummary) {
    HorizontalDivider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LoanCardSurface)
            .padding(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.schedule_total_paid),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.schedule_total_extras),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.schedule_forecast_label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = LoanBlueDark,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    R.string.schedule_paid_of,
                    Formatters.money(summary.paidPrincipal),
                    Formatters.money(summary.loanAmount),
                ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = Formatters.money(summary.totalExtras),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = summary.forecastLabel ?: stringResource(R.string.schedule_forecast_off),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RowScope.ScheduleHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    weight: Float = 0f,
) {
    Text(
        text = text,
        modifier = Modifier
            .then(if (weight > 0f) Modifier.weight(weight) else Modifier.width(width))
            .padding(horizontal = 4.dp),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
        ),
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

@Composable
private fun RowScope.ScheduleBodyCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    weight: Float = 0f,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.End,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .then(if (weight > 0f) Modifier.weight(weight) else Modifier.width(width))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (textAlign == TextAlign.Start) Arrangement.Start else Arrangement.End,
    ) {
        leadingIcon?.invoke()
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = fontWeight),
            textAlign = textAlign,
            maxLines = 2,
        )
    }
}

private fun rowBackground(row: ScheduleRow, isExtra: Boolean): Color {
    return when {
        row.isCurrent -> ScheduleRowSelected
        isExtra -> ScheduleRowExtra
        row.isOdd -> ScheduleRowOdd
        else -> LoanCardSurface
    }
}
