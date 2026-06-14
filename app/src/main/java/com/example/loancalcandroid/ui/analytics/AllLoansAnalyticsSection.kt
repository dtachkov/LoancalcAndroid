package com.example.loancalcandroid.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.analytics.components.AnalyticsBlockCard
import com.example.loancalcandroid.ui.analytics.components.AnalyticsSectionHeader
import com.example.loancalcandroid.ui.analytics.components.DebtDonutChart
import com.example.loancalcandroid.ui.analytics.components.DebtInterestTimelineChart
import com.example.loancalcandroid.ui.analytics.components.LoanInterestComparisonChart
import com.example.loancalcandroid.ui.analytics.components.RepaymentProgressBlock
import com.example.loancalcandroid.ui.analytics.components.YearlyDebtLoadChart

@Composable
fun AllLoansAnalyticsSection(
    modifier: Modifier = Modifier,
    viewModel: AllLoansAnalyticsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val data = uiState.data

    if (uiState.error != null && data == null) {
        Text(
            text = uiState.error.orEmpty(),
            color = MaterialTheme.colorScheme.error,
            modifier = modifier.padding(vertical = 8.dp),
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AnalyticsBlockCard(
            title = stringResource(R.string.analytics_debt_donut_title),
            footer = stringResource(R.string.analytics_remaining_debt),
            isLoading = uiState.isCalculating || AnalyticsBlock.DEBT_DONUT !in uiState.readyBlocks,
        ) {
            data?.let { analytics ->
                if (analytics.debtByLoan.isNotEmpty()) {
                    DebtDonutChart(
                        slices = analytics.debtByLoan,
                        totalDebt = analytics.totalRemainingDebt,
                    )
                }
            }
        }

        AnalyticsBlockCard(
            title = stringResource(R.string.analytics_loans_interest_title),
            footer = stringResource(R.string.analytics_loans_interest_footer),
            isLoading = uiState.isCalculating || AnalyticsBlock.LOAN_INTEREST_BARS !in uiState.readyBlocks,
        ) {
            data?.let { analytics ->
                LoanInterestComparisonChart(comparison = analytics.loanInterestComparison)
            }
        }

        AnalyticsBlockCard(
            title = stringResource(R.string.analytics_timeline_title),
            footer = stringResource(R.string.analytics_timeline_footer),
            isLoading = uiState.isCalculating || AnalyticsBlock.TIMELINE_LINE !in uiState.readyBlocks,
        ) {
            data?.let { analytics ->
                DebtInterestTimelineChart(
                    points = analytics.timeline,
                    yAxisMax = maxOf(analytics.allLoansAmount, analytics.allLoansOverpay) * 1.1,
                )
            }
        }

        AnalyticsSectionHeader(title = stringResource(R.string.analytics_repayment_progress_title))
        AnalyticsBlockCard(
            title = "",
            isLoading = uiState.isCalculating || AnalyticsBlock.REPAYMENT_PROGRESS !in uiState.readyBlocks,
        ) {
            data?.let { analytics ->
                RepaymentProgressBlock(progress = analytics.repaymentProgress)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        AnalyticsSectionHeader(title = stringResource(R.string.analytics_yearly_load_title))
        AnalyticsBlockCard(
            title = "",
            footer = stringResource(R.string.analytics_yearly_load_footer),
            isLoading = uiState.isCalculating || AnalyticsBlock.YEARLY_LOAD !in uiState.readyBlocks,
        ) {
            data?.let { analytics ->
                YearlyDebtLoadChart(yearlyLoad = analytics.yearlyLoad)
            }
        }
    }
}
