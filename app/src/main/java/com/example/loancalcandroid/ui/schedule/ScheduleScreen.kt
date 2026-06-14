package com.example.loancalcandroid.ui.schedule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.loancalcandroid.ui.common.LoanTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.AutoShrinkText
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanBlueDark
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.SchedulePreviousBar
import com.example.loancalcandroid.ui.theme.ScheduleRowExtra
import com.example.loancalcandroid.ui.theme.ScheduleRowOdd
import com.example.loancalcandroid.ui.theme.ScheduleRowSelected
import com.example.loancalcandroid.util.Formatters

private val BriefNumberColumnWidth = 40.dp
private val BriefDummyColumnWidth = 50.dp
private val BriefMinColumnWidth = 70.dp
private val BriefDateMinColumnWidth = 96.dp
private val DetailedDateColumnWidth = 44.dp
private val DetailedMinValueColumnWidth = 52.dp
private const val DetailedValueColumnWeight = 1f
private const val BriefDateColumnWeight = 1.45f
private const val BriefAmountColumnWeight = 1f
private val BriefCellStyle
    @Composable get() = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    loanId: Long,
    onBack: () -> Unit,
    onAddExtra: () -> Unit,
    onHelp: () -> Unit,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
    refreshTrigger: Boolean = false,
    onRefreshHandled: () -> Unit = {},
) {
    val viewModel: ScheduleViewModel = loanViewModel(loanId, ::ScheduleViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger) {
            viewModel.load()
            onRefreshHandled()
        }
    }

    LoanCalcScaffold(
        title = stringResource(R.string.quick_schedule),
        onBack = onBack,
        actions = {
            IconButton(onClick = onAddExtra) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_extra),
                )
            }
            IconButton(
                onClick = {
                    viewModel.share(context) { message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.menu_share),
                )
            }
            IconButton(onClick = onHelp) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.help_title),
                )
            }
        },
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
                    LoanTabRow(
                        selectedTabIndex = uiState.selectedTab,
                        tabs = listOf(
                            stringResource(R.string.schedule_tab_brief),
                            stringResource(R.string.schedule_tab_detailed),
                        ),
                        onTabSelected = viewModel::selectTab,
                    )

                    when (uiState.selectedTab) {
                        0 -> BriefScheduleTab(
                            rows = uiState.visibleRows,
                            allRows = uiState.rows,
                            summary = uiState.summary,
                            showPreviousPayments = uiState.showPreviousPayments,
                            hasHiddenRows = uiState.hasHiddenRows,
                            onTogglePrevious = viewModel::toggleShowPreviousPayments,
                            onPaymentClick = onPaymentClick,
                        )
                        1 -> DetailedScheduleTab(
                            rows = uiState.rows,
                            onPaymentClick = onPaymentClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BriefScheduleTab(
    rows: List<ScheduleRow>,
    allRows: List<ScheduleRow>,
    summary: ScheduleSummary?,
    showPreviousPayments: Boolean,
    hasHiddenRows: Boolean,
    onTogglePrevious: () -> Unit,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
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
                val previousDateMillis = previousPaymentDateMillis(allRows, row)
                when (row.type) {
                    ScheduleRowType.PAYMENT -> BriefPaymentRow(row, onPaymentClick, previousDateMillis)
                    ScheduleRowType.EXTRA -> BriefExtraRow(row, isRateChange = false, onPaymentClick, previousDateMillis)
                    ScheduleRowType.CHANGE_RATE -> BriefExtraRow(row, isRateChange = true, onPaymentClick, previousDateMillis)
                }
            }
        }

        summary?.let { ScheduleSummaryBlock(it) }
    }

    LaunchedEffect(rows) {
        if (rows.isNotEmpty()) {
            val currentIndex = rows.indexOfFirst { it.isCurrent }.coerceAtLeast(0)
            listState.animateScrollToItem(currentIndex.coerceAtMost(rows.lastIndex))
        }
    }
}

@Composable
private fun DetailedScheduleTab(
    rows: List<ScheduleRow>,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
) {
    val listState = rememberLazyListState()
    val currentIndex = rows.indexOfFirst { it.isCurrent }.coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxSize()) {
        DetailedHeaderRow()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            items(rows, key = { "${it.listIndex}_${it.type}" }) { row ->
                val previousDateMillis = previousPaymentDateMillis(rows, row)
                when (row.type) {
                    ScheduleRowType.CHANGE_RATE -> DetailedRateChangeRow(row, onPaymentClick, previousDateMillis)
                    else -> DetailedPaymentRow(
                        row,
                        isExtra = row.type == ScheduleRowType.EXTRA,
                        onPaymentClick = onPaymentClick,
                        previousPaymentDateMillis = previousDateMillis,
                    )
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
        BriefHeaderCell(
            text = stringResource(R.string.schedule_col_number),
            width = BriefNumberColumnWidth,
            textAlign = TextAlign.Center,
        )
        BriefHeaderCell(
            text = stringResource(R.string.schedule_brief_col_date),
            weight = BriefDateColumnWeight,
            minWidth = BriefDateMinColumnWidth,
        )
        BriefHeaderCell(
            text = stringResource(R.string.schedule_brief_col_payment),
            weight = BriefAmountColumnWeight,
        )
        BriefHeaderCell("", BriefDummyColumnWidth)
        BriefHeaderCell(stringResource(R.string.schedule_brief_col_balance), weight = BriefAmountColumnWeight)
    }
    HorizontalDivider(color = LoanBlueDark, thickness = 2.dp)
}

@Composable
private fun DetailedHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LoanBlueDark)
            .padding(vertical = 8.dp),
    ) {
        ScheduleHeaderCell(
            text = stringResource(R.string.schedule_col_date),
            width = DetailedDateColumnWidth,
            textAlign = TextAlign.Center,
        )
        ScheduleHeaderCell(
            text = stringResource(R.string.schedule_col_payment),
            weight = DetailedValueColumnWeight,
        )
        ScheduleHeaderCell(
            text = stringResource(R.string.schedule_col_interest),
            weight = DetailedValueColumnWeight,
        )
        ScheduleHeaderCell(
            text = stringResource(R.string.schedule_col_principal),
            weight = DetailedValueColumnWeight,
        )
        ScheduleHeaderCell(
            text = stringResource(R.string.schedule_col_balance),
            weight = DetailedValueColumnWeight,
        )
    }
    HorizontalDivider(color = LoanBlueDark, thickness = 2.dp)
}

@Composable
private fun BriefPaymentRow(
    row: ScheduleRow,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
    previousPaymentDateMillis: Long,
) {
    BriefTableRow(
        background = rowBackground(row, isExtra = false),
        onClick = { onPaymentClick(row.listIndex, previousPaymentDateMillis) },
    ) {
        BriefNumberCell(row.displayNumber)
        BriefDateCell(
            text = Formatters.date(row.date),
            showWeekendIcon = row.date.isWeekend(),
        )
        BriefAmountCell(Formatters.moneyFixed(row.total))
        Box(modifier = Modifier.width(BriefDummyColumnWidth))
        BriefAmountCell(Formatters.moneyFixed(row.endBalance))
    }
}

@Composable
private fun BriefExtraRow(
    row: ScheduleRow,
    isRateChange: Boolean,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
    previousPaymentDateMillis: Long,
) {
    val amountText = if (isRateChange) {
        Formatters.schedulePercent(row.rateExtra)
    } else {
        Formatters.moneyFixed(row.extraAmount)
    }
    val typeLabel = if (isRateChange) {
        stringResource(R.string.schedule_extra_rate_change)
    } else {
        stringResource(R.string.schedule_extra_payment)
    }

    BriefTableRow(
        background = rowBackground(row, isExtra = true),
        onClick = { onPaymentClick(row.listIndex, previousPaymentDateMillis) },
    ) {
        BriefNumberCell(row.displayNumber)
        BriefLabelCell(typeLabel)
        BriefAmountCell(amountText)
        BriefExtraDateCell(Formatters.monthDay(row.date))
        BriefAmountCell(Formatters.moneyFixed(row.endBalance))
    }
}

@Composable
private fun DetailedPaymentRow(
    row: ScheduleRow,
    isExtra: Boolean,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
    previousPaymentDateMillis: Long,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground(row, isExtra = isExtra))
            .clickable { onPaymentClick(row.listIndex, previousPaymentDateMillis) },
    ) {
        if (row.isNewYear) {
            Text(
                text = stringResource(R.string.schedule_year_label, Formatters.year(row.date)),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LoanCardSurface)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScheduleBodyCell(
                text = Formatters.monthDay(row.date),
                width = DetailedDateColumnWidth,
                textAlign = TextAlign.Center,
            )
            ScheduleBodyCell(
                text = if (isExtra) {
                    Formatters.moneyWithoutDecimal(row.extraAmount.coerceAtLeast(row.total))
                } else {
                    Formatters.moneyFixed(row.total)
                },
                weight = DetailedValueColumnWeight,
                textAlign = TextAlign.Start,
                leadingIcon = if (isExtra) {
                    {
                        Image(
                            painter = painterResource(R.drawable.ic_schedule_extra),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .size(14.dp),
                        )
                    }
                } else {
                    null
                },
            )
            ScheduleBodyCell(
                text = Formatters.moneyFixed(row.interest),
                weight = DetailedValueColumnWeight,
            )
            ScheduleBodyCell(
                text = Formatters.moneyFixed(row.principal),
                weight = DetailedValueColumnWeight,
            )
            ScheduleBodyCell(
                text = Formatters.moneyFixed(row.endBalance),
                weight = DetailedValueColumnWeight,
            )
        }
    }
}

@Composable
private fun DetailedRateChangeRow(
    row: ScheduleRow,
    onPaymentClick: (listIndex: Int, previousPaymentDateMillis: Long) -> Unit,
    previousPaymentDateMillis: Long,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScheduleRowExtra)
            .clickable { onPaymentClick(row.listIndex, previousPaymentDateMillis) },
    ) {
        if (row.isNewYear) {
            Text(
                text = stringResource(R.string.schedule_year_label, Formatters.year(row.date)),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LoanCardSurface)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = Formatters.monthDay(row.date),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(DetailedDateColumnWidth),
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier
                    .weight(DetailedValueColumnWeight * 4)
                    .widthIn(min = DetailedMinValueColumnWidth * 4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_schedule_percent),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(14.dp),
                )
                AutoShrinkText(
                    text = stringResource(
                        R.string.schedule_new_rate,
                        Formatters.schedulePercent(row.rateExtra),
                    ),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall.copy(color = LoanTextSecondary),
                    textAlign = TextAlign.Start,
                    minFontSize = 9.sp,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun BriefTableRow(
    background: Color,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.BriefHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp = 0.dp,
    weight: Float = 0f,
    minWidth: androidx.compose.ui.unit.Dp = BriefMinColumnWidth,
    textAlign: TextAlign = TextAlign.Start,
) {
    val style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        color = Color.White,
    )
    val cellModifier = Modifier
        .then(
            if (weight > 0f) {
                Modifier
                    .weight(weight)
                    .widthIn(min = minWidth)
            } else {
                Modifier.width(width)
            },
        )
        .padding(horizontal = 4.dp)
        .fillMaxWidth()

    if (text.isEmpty()) {
        Box(modifier = cellModifier)
        return
    }

    if (textAlign == TextAlign.Center && weight == 0f) {
        Text(
            text = text,
            modifier = cellModifier,
            style = style,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    } else {
        AutoShrinkText(
            text = text,
            modifier = cellModifier,
            style = style,
            textAlign = textAlign,
            minFontSize = 8.sp,
            maxLines = 2,
        )
    }
}

@Composable
private fun RowScope.BriefNumberCell(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .width(BriefNumberColumnWidth)
            .padding(horizontal = 4.dp),
        style = BriefCellStyle.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

@Composable
private fun RowScope.BriefDateCell(
    text: String,
    showWeekendIcon: Boolean = false,
) {
    Row(
        modifier = Modifier
            .weight(BriefDateColumnWeight)
            .widthIn(min = BriefDateMinColumnWidth)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showWeekendIcon) {
            Image(
                painter = painterResource(R.drawable.ic_schedule_calendar),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(18.dp),
            )
        }
        AutoShrinkText(
            text = text,
            modifier = Modifier.weight(1f),
            style = BriefCellStyle,
            textAlign = TextAlign.End,
            minFontSize = 9.sp,
        )
    }
}

@Composable
private fun RowScope.BriefLabelCell(text: String) {
    AutoShrinkText(
        text = text,
        modifier = Modifier
            .weight(BriefDateColumnWeight)
            .widthIn(min = BriefDateMinColumnWidth)
            .padding(horizontal = 4.dp)
            .fillMaxWidth(),
        style = BriefCellStyle.copy(fontSize = 12.sp),
        textAlign = TextAlign.Start,
        minFontSize = 9.sp,
        maxLines = 2,
    )
}

@Composable
private fun RowScope.BriefAmountCell(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .weight(BriefAmountColumnWeight)
            .widthIn(min = BriefMinColumnWidth)
            .padding(horizontal = 4.dp),
        style = BriefCellStyle,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun RowScope.BriefExtraDateCell(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .width(BriefDummyColumnWidth)
            .padding(horizontal = 4.dp),
        style = BriefCellStyle.copy(fontSize = 9.sp),
        color = LoanTextSecondary,
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
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
                    Formatters.moneyFixed(summary.paidPrincipal),
                    Formatters.moneyFixed(summary.loanAmount),
                ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = Formatters.moneyFixed(summary.totalExtras),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = summary.forecastLabel ?: stringResource(R.string.schedule_forecast_off),
                style = MaterialTheme.typography.labelMedium,
                color = if (summary.forecastLabel == null) LoanBlueDark else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RowScope.ScheduleHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp = 0.dp,
    weight: Float = 0f,
    minWidth: androidx.compose.ui.unit.Dp = DetailedMinValueColumnWidth,
    textAlign: TextAlign = TextAlign.Start,
) {
    val style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        color = Color.White,
    )
    val cellModifier = Modifier
        .then(
            if (weight > 0f) {
                Modifier
                    .weight(weight)
                    .widthIn(min = minWidth)
            } else {
                Modifier.width(width)
            },
        )
        .padding(horizontal = 2.dp)
        .fillMaxWidth()

    if (textAlign == TextAlign.Center && weight == 0f) {
        Text(
            text = text,
            modifier = cellModifier,
            style = style,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    } else {
        AutoShrinkText(
            text = text,
            modifier = cellModifier,
            style = style,
            textAlign = textAlign,
            minFontSize = 8.sp,
            maxLines = 2,
        )
    }
}

@Composable
private fun RowScope.ScheduleBodyCell(
    text: String,
    width: androidx.compose.ui.unit.Dp = 0.dp,
    weight: Float = 0f,
    minWidth: androidx.compose.ui.unit.Dp = DetailedMinValueColumnWidth,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .then(
                if (weight > 0f) {
                    Modifier
                        .weight(weight)
                        .widthIn(min = minWidth)
                } else {
                    Modifier.width(width)
                },
            )
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.invoke()
        AutoShrinkText(
            text = text,
            modifier = if (leadingIcon != null) Modifier.weight(1f) else Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = fontWeight),
            textAlign = textAlign,
            minFontSize = 8.sp,
            maxLines = 1,
        )
    }
}

private fun rowBackground(row: ScheduleRow, isExtra: Boolean): Color {
    return when {
        isExtra && !row.isCurrent -> ScheduleRowExtra
        row.isCurrent -> ScheduleRowSelected
        row.isOdd -> ScheduleRowOdd
        else -> LoanCardSurface
    }
}

private fun previousPaymentDateMillis(rows: List<ScheduleRow>, row: ScheduleRow): Long {
    val index = rows.indexOfFirst { it.listIndex == row.listIndex }
    if (index <= 0) return 0L
    return rows[index - 1].date.time
}
