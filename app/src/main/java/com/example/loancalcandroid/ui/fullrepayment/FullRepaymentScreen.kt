package com.example.loancalcandroid.ui.fullrepayment

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.review.RequestRuStoreReviewEffect
import com.example.loancalcandroid.ui.common.AutoShrinkText
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters
import java.util.Date
import ru.kredit.calculator.data.model.ExtraType

@Composable
fun FullRepaymentScreen(
    loanId: Long,
    onBack: () -> Unit,
    onAddExtra: (amount: String, dateMillis: Long, extraType: String) -> Unit,
) {
    val viewModel: FullRepaymentViewModel = loanViewModel(loanId, ::FullRepaymentViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RequestRuStoreReviewEffect(uiState.reviewRequestTrigger)

    LoanCalcScaffold(
        title = stringResource(R.string.menu_full_repayment),
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.full_repayment_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )

            DatePickerField(
                label = stringResource(R.string.full_repayment_planned_date),
                value = uiState.plannedDate,
                onValueChange = viewModel::updatePlannedDate,
            )

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            val result = uiState.result
            if (result != null) {
                FullRepaymentResultTable(
                    totalAmount = Formatters.money(result.totalAmount),
                    includedAmount = Formatters.money(result.includedAmount),
                    remainingDebt = Formatters.money(result.remainingDebt),
                    includesInterest = result.includesInterest,
                    scheduledPaymentDate = result.scheduledPaymentDate,
                )
                Button(
                    onClick = {
                        onAddExtra(
                            viewModel.extraPrefillAmount(),
                            uiState.plannedDate.time,
                            ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT.name,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.best_date_add_extra).uppercase())
                }
            } else if (uiState.error == null) {
                Text(
                    text = stringResource(R.string.full_repayment_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                )
            }
        }
    }
}

@Composable
private fun FullRepaymentResultTable(
    totalAmount: String,
    includedAmount: String,
    remainingDebt: String,
    includesInterest: Boolean,
    scheduledPaymentDate: Date?,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FullRepaymentResultRow(
            label = stringResource(R.string.full_repayment_total),
            value = totalAmount,
            emphasized = true,
        )
        HorizontalDivider()
        FullRepaymentResultRow(
            label = stringResource(
                if (includesInterest) {
                    R.string.full_repayment_including_interest
                } else {
                    R.string.full_repayment_including_payment
                },
            ),
            value = includedAmount,
            indented = true,
        )
        HorizontalDivider()
        FullRepaymentResultRow(
            label = stringResource(R.string.full_repayment_debt),
            value = remainingDebt,
            indented = true,
        )
        if (!includesInterest && scheduledPaymentDate != null) {
            HorizontalDivider()
            FullRepaymentResultRow(
                label = stringResource(R.string.full_repayment_schedule_date),
                value = Formatters.date(scheduledPaymentDate),
                indented = true,
            )
        }
        HorizontalDivider()
    }
}

@Composable
private fun FullRepaymentResultRow(
    label: String,
    value: String,
    emphasized: Boolean = false,
    indented: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (indented) 20.dp else 0.dp)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(2f)
                .padding(end = 12.dp),
            style = if (emphasized) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = if (emphasized) {
                MaterialTheme.colorScheme.primary
            } else {
                LoanTextSecondary
            },
        )
        AutoShrinkText(
            text = value,
            modifier = Modifier.weight(1f),
            style = if (emphasized) {
                MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            } else {
                MaterialTheme.typography.bodyLarge
            },
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}
