package com.example.loancalcandroid.ui.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.schedulePaymentDetailViewModel
import com.example.loancalcandroid.ui.extras.ExtraTypeIcons
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.ScheduleRowOdd
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.model.Extra

private val TableHeaderBackground = Color(0xFFF5F5F5)

@Composable
fun SchedulePaymentDetailScreen(
    loanId: Long,
    listIndex: Int,
    previousPaymentDateMillis: Long,
    onBack: () -> Unit,
    viewModel: SchedulePaymentDetailViewModel = schedulePaymentDetailViewModel(
        loanId,
        listIndex,
        previousPaymentDateMillis,
    ) { app, lId, idx, prev -> SchedulePaymentDetailViewModel(app, lId, idx, prev) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.schedule_payment_detail_title),
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
            uiState.row != null -> {
                PaymentDetailContent(
                    row = uiState.row!!,
                    periodExtras = uiState.periodExtras,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun PaymentDetailContent(
    row: ScheduleRow,
    periodExtras: List<Extra>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LoanCardSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DetailValueRow(
                label = stringResource(R.string.extra_payment_date),
                value = Formatters.date(row.date),
            )
            DetailValueRow(
                label = stringResource(R.string.schedule_col_payment),
                value = Formatters.moneyFixed(row.total),
            )
            DetailValueRow(
                label = stringResource(R.string.schedule_col_interest),
                value = Formatters.moneyFixed(row.interest),
            )
            DetailValueRow(
                label = stringResource(R.string.schedule_col_principal),
                value = Formatters.moneyFixed(row.principal),
            )
            DetailValueRow(
                label = stringResource(R.string.schedule_col_balance),
                value = Formatters.moneyFixed(row.endBalance),
            )
        }

        Text(
            text = stringResource(R.string.schedule_detail_extras_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
            color = LoanTextSecondary,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableHeaderBackground)
                .padding(vertical = 8.dp, horizontal = 4.dp),
        ) {
            DetailHeaderCell(stringResource(R.string.extra_type), 0.9f)
            DetailHeaderCell(stringResource(R.string.extra_payment_date), 1.1f)
            DetailHeaderCell(stringResource(R.string.extra_payment_amount), 1f)
        }
        HorizontalDivider()

        if (periodExtras.isEmpty()) {
            Text(
                text = stringResource(R.string.schedule_detail_no_extras),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )
        } else {
            periodExtras.forEachIndexed { index, extra ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (index % 2 == 1) ScheduleRowOdd else LoanCardSurface)
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(0.9f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(ExtraTypeIcons.icon(extra.type)),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Text(
                        text = Formatters.date(extra.date),
                        modifier = Modifier.weight(1.1f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = formatExtraAmount(extra),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun RowScope.DetailHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
    )
}
