package com.example.loancalcandroid.ui.tax

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
import androidx.compose.material3.MaterialTheme
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.common.LoanDecimalOutlinedTextField
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.review.RequestRuStoreReviewEffect
import com.example.loancalcandroid.ui.common.FeatureResultTable
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary

@Composable
fun TaxScreen(
    loanId: Long,
    onBack: () -> Unit,
) {
    val viewModel: TaxViewModel = loanViewModel(loanId, ::TaxViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RequestRuStoreReviewEffect(uiState.reviewRequestTrigger)

    LoanCalcScaffold(
        title = stringResource(R.string.menu_tax),
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoanOutlinedTextField(
                value = uiState.loanAmount,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.tax_loan_amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            LoanDecimalOutlinedTextField(
                value = uiState.objectPrice,
                onValueChange = viewModel::updateObjectPrice,
                label = { Text(stringResource(R.string.tax_object_price)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.objectPriceError != null,
                supportingText = uiState.objectPriceError?.let { { Text(it) } },
            )
            LoanDecimalOutlinedTextField(
                value = uiState.salary,
                onValueChange = viewModel::updateSalary,
                label = { Text(stringResource(R.string.tax_salary)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Button(
                onClick = viewModel::calculate,
                enabled = !uiState.isCalculating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isCalculating) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(stringResource(R.string.tax_calculate).uppercase())
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            uiState.totalTax?.let {
                TaxSummaryRow(
                    label = stringResource(R.string.tax_return_from_price),
                    value = uiState.principalTax.orEmpty(),
                )
                TaxSummaryRow(
                    label = stringResource(R.string.tax_return_from_percent),
                    value = uiState.interestTax.orEmpty(),
                )
                TaxSummaryRow(
                    label = stringResource(R.string.tax_return_total),
                    value = it,
                    emphasized = true,
                )

                Text(
                    text = stringResource(R.string.tax_title_by_years),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp),
                )
                FeatureResultTable(
                    headers = listOf(
                        stringResource(R.string.tax_detail_year),
                        stringResource(R.string.tax_detail_return),
                        stringResource(R.string.tax_detail_salary),
                        stringResource(R.string.tax_detail_rest),
                    ),
                    rows = uiState.tableRows.map { row ->
                        listOf(
                            row.year.toString(),
                            row.cumulativeReturn,
                            row.cumulativeSalaryTax,
                            row.restForReturn,
                        )
                    },
                    firstColumnWidth = 44.dp,
                )
            }
        }
    }
}

@Composable
private fun TaxSummaryRow(
    label: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = if (emphasized) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.bodyLarge
            },
            color = if (emphasized) MaterialTheme.colorScheme.primary else LoanTextSecondary,
        )
        Text(
            text = value,
            style = if (emphasized) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
