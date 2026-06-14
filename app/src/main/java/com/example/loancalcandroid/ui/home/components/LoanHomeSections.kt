package com.example.loancalcandroid.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import com.example.loancalcandroid.ui.theme.LoanBlueDark
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.LoanGreen
import com.example.loancalcandroid.ui.theme.LoanRed
import com.example.loancalcandroid.ui.theme.LoanTextSecondary

@Composable
fun LoanCardsPager(
    summary: AllLoansSummaryUiModel?,
    loanCards: List<LoanCardUiModel>,
    pagerIndex: Int,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageCount = 1 + loanCards.size
    val pagerState = rememberPagerState(
        initialPage = pagerIndex.coerceIn(0, maxOf(pageCount - 1, 0)),
        pageCount = { maxOf(pageCount, 1) },
    )

    LaunchedEffect(pagerIndex) {
        if (pagerState.currentPage != pagerIndex && pagerIndex in 0 until pageCount) {
            pagerState.animateScrollToPage(pagerIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != pagerIndex) {
            onPageChanged(pagerState.currentPage)
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 12.dp,
        ) { page ->
            when {
                page == 0 -> SummaryLoanCard(summary = summary)
                else -> SingleLoanCard(card = loanCards[page - 1])
            }
        }
        if (pageCount > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            PagerIndicator(pageCount = pageCount, currentPage = pagerState.currentPage)
        }
    }
}

@Composable
private fun SummaryLoanCard(summary: AllLoansSummaryUiModel?) {
    LoanGradientCard {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            Text(
                text = stringResource(R.string.all_loans_card_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            if (summary == null || summary.loansCount == 0) {
                Text(
                    text = stringResource(R.string.no_loans_hint),
                    color = Color.White.copy(alpha = 0.9f),
                )
            } else {
                Text(
                    text = stringResource(R.string.all_loans_count, summary.loansCount),
                    color = Color.White.copy(alpha = 0.9f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    LoanCardStat(
                        label = stringResource(R.string.all_loans_total_amount),
                        value = summary.totalAmount,
                    )
                    LoanCardStat(
                        label = stringResource(R.string.all_loans_total_debt),
                        value = summary.totalDebt,
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleLoanCard(card: LoanCardUiModel) {
    LoanGradientCard {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Column {
                LinearProgressIndicator(
                    progress = {
                        if (card.termMonths > 0) card.monthsPaid.toFloat() / card.termMonths else 0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.35f),
                    strokeCap = StrokeCap.Round,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.loan_term_progress,
                        card.monthsPaid,
                        card.termMonths,
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                LoanCardStat(label = stringResource(R.string.loan_amount), value = card.amount)
                LoanCardStat(label = stringResource(R.string.loan_rate), value = card.rate)
            }
            Text(
                text = "${stringResource(R.string.loan_issue_date)}: ${card.issueDate}",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    enabled: Boolean,
    onEarlyPaymentClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onRequisitesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        QuickActionItem(
            icon = Icons.Default.Add,
            label = stringResource(R.string.quick_early_payment),
            enabled = enabled,
            onClick = onEarlyPaymentClick,
        )
        QuickActionItem(
            icon = Icons.Default.BarChart,
            label = stringResource(R.string.quick_schedule),
            enabled = enabled,
            onClick = onScheduleClick,
        )
        QuickActionItem(
            icon = Icons.Default.Assignment,
            label = stringResource(R.string.quick_requisites),
            enabled = enabled,
            onClick = onRequisitesClick,
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(8.dp),
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = if (enabled) LoanCardSurface else LoanCardSurface.copy(alpha = 0.6f),
            shadowElevation = 1.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (enabled) LoanBlueDark else LoanTextSecondary,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else LoanTextSecondary,
        )
    }
}

@Composable
fun DebtProgressSection(
    details: LoanDetailsUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF0F0F0)),
        ) {
            if (details.paidFraction > 0f) {
                Box(
                    modifier = Modifier
                        .weight(details.paidFraction.coerceIn(0.01f, 1f))
                        .fillMaxHeight()
                        .background(LoanGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    if (details.paidFraction > 0.15f) {
                        Text(
                            text = "${stringResource(R.string.paid_label)} ${details.paidAmount}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight((1f - details.paidFraction).coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(LoanRed),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${stringResource(R.string.debt_label)} ${details.debtAmount}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.current_payment),
            style = MaterialTheme.typography.bodyMedium,
            color = LoanTextSecondary,
        )
        Text(
            text = details.currentPayment,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.pay_until, details.paymentDueDate),
            style = MaterialTheme.typography.bodySmall,
            color = LoanTextSecondary,
        )
    }
}

@Composable
fun StatsSection(details: LoanDetailsUiModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LoanCardSurface,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            StatRow(stringResource(R.string.interest_paid), details.interestPaid)
            StatRow(stringResource(R.string.remaining_to_pay), details.remainingToPay)
            StatRow(stringResource(R.string.total_interest), details.totalInterest)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            StatRow(stringResource(R.string.total_commission), details.totalCommission)
            StatRow(stringResource(R.string.total_insurance), details.totalInsurance)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun NavigationMenuSection(
    details: LoanDetailsUiModel,
    onExtrasClick: () -> Unit,
    onForecastClick: () -> Unit,
    onBestDateClick: () -> Unit,
    onTaxClick: () -> Unit,
    onCompareClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LoanCardSurface,
    ) {
        Column {
            MenuNavigationRow(
                title = stringResource(R.string.menu_extras_list),
                subtitle = stringResource(R.string.menu_extras_savings, details.extrasSavings),
                onClick = onExtrasClick,
            )
            MenuNavigationRow(
                title = stringResource(R.string.menu_forecast),
                subtitle = stringResource(
                    if (details.forecastEnabled) R.string.menu_forecast_enabled
                    else R.string.menu_forecast_disabled,
                ),
                onClick = onForecastClick,
            )
            MenuNavigationRow(
                title = stringResource(R.string.menu_best_date),
                subtitle = stringResource(R.string.menu_best_date_hint),
                onClick = onBestDateClick,
            )
            MenuNavigationRow(
                title = stringResource(R.string.menu_tax),
                subtitle = stringResource(R.string.menu_tax_hint),
                onClick = onTaxClick,
            )
            MenuNavigationRow(
                title = stringResource(R.string.menu_compare),
                subtitle = stringResource(R.string.menu_compare_hint),
                onClick = onCompareClick,
                showDivider = false,
            )
        }
    }
}

@Composable
fun MenuNavigationRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    showDivider: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LoanTextSecondary,
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = LoanTextSecondary,
        )
    }
    if (showDivider) {
        HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
fun LoanActionsSection(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDuplicateClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LoanCardSurface,
    ) {
        Column {
            MenuNavigationRow(
                title = stringResource(R.string.action_edit_loan),
                onClick = onEditClick,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDeleteClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.action_delete_loan),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoanRed,
                    modifier = Modifier.weight(1f),
                )
            }
            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDuplicateClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = stringResource(R.string.action_duplicate_loan),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
fun AllLoansOverviewSection(
    summary: AllLoansSummaryUiModel?,
    onOpenAllLoans: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = LoanCardSurface,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.all_loans_overview_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (summary != null && summary.loansCount > 0) {
                StatRow(stringResource(R.string.all_loans_total_amount), summary.totalAmount)
                StatRow(stringResource(R.string.all_loans_total_debt), summary.totalDebt)
                summary.nearestPaymentAmount?.let { amount ->
                    StatRow(
                        label = stringResource(R.string.current_payment),
                        value = amount,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            MenuNavigationRow(
                title = stringResource(R.string.open_all_loans),
                onClick = onOpenAllLoans,
                showDivider = false,
            )
        }
    }
}
