package com.example.loancalcandroid.ui.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.loancalcandroid.LoanCalcApplication
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.DatePickerField
import com.example.loancalcandroid.ui.common.FeatureCalculationProgress
import com.example.loancalcandroid.ui.common.FeatureResultTable
import com.example.loancalcandroid.ui.common.FeatureTypeSegmentedControl
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters

@Composable
fun CompareTabContent(
    onPurchaseRequired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: CompareViewModel = viewModel(
        key = "loans_list_compare",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CompareViewModel(application, 0L) as T
            }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val licenseManager = (LocalContext.current.applicationContext as LoanCalcApplication).licenseManager
    val isLicensed by licenseManager.isLicensed.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CompareFormContent(
            uiState = uiState,
            isLicensed = isLicensed,
            onExtraDateChange = viewModel::updateExtraDate,
            onAmountChange = viewModel::updateAmount,
            onSelectAmount = { viewModel.setDecreaseAmount(true) },
            onSelectTerm = { viewModel.setDecreaseAmount(false) },
            onCalculate = viewModel::calculate,
            onStop = viewModel::stopCalculation,
            onPurchaseRequired = onPurchaseRequired,
        )
    }
}

@Composable
fun CompareScreen(
    loanId: Long,
    onBack: () -> Unit,
    onPurchaseRequired: () -> Unit = {},
) {
    val viewModel: CompareViewModel = loanViewModel(loanId, ::CompareViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val licenseManager = (LocalContext.current.applicationContext as LoanCalcApplication).licenseManager
    val isLicensed by licenseManager.isLicensed.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.menu_compare),
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
            CompareFormContent(
                uiState = uiState,
                isLicensed = isLicensed,
                onExtraDateChange = viewModel::updateExtraDate,
                onAmountChange = viewModel::updateAmount,
                onSelectAmount = { viewModel.setDecreaseAmount(true) },
                onSelectTerm = { viewModel.setDecreaseAmount(false) },
                onCalculate = viewModel::calculate,
                onStop = viewModel::stopCalculation,
                onPurchaseRequired = onPurchaseRequired,
            )
        }
    }
}

@Composable
private fun CompareFormContent(
    uiState: CompareUiState,
    isLicensed: Boolean,
    onExtraDateChange: (java.util.Date) -> Unit,
    onAmountChange: (String) -> Unit,
    onSelectAmount: () -> Unit,
    onSelectTerm: () -> Unit,
    onCalculate: () -> Unit,
    onStop: () -> Unit,
    onPurchaseRequired: () -> Unit,
) {
            Text(
                text = stringResource(R.string.compare_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = LoanTextSecondary,
            )

            DatePickerField(
                label = stringResource(R.string.compare_extra_date),
                value = uiState.extraDate,
                onValueChange = onExtraDateChange,
            )

            LoanOutlinedTextField(
                value = uiState.amount,
                onValueChange = onAmountChange,
                label = { Text(stringResource(R.string.extra_payment_amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
            )

            Text(
                text = stringResource(R.string.best_date_extra_type),
                style = MaterialTheme.typography.bodyMedium,
            )
            FeatureTypeSegmentedControl(
                decreaseAmountLabel = stringResource(R.string.forecast_type_amount),
                decreaseTermLabel = stringResource(R.string.forecast_type_term),
                decreaseAmount = uiState.decreaseAmount,
                onSelectAmount = onSelectAmount,
                onSelectTerm = onSelectTerm,
            )

            if (uiState.isCalculating) {
                FeatureCalculationProgress(
                    progress = uiState.progress,
                    max = uiState.progressMax,
                    statusText = stringResource(R.string.compare_progress, uiState.progressText),
                )
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.best_date_stop))
                }
            } else {
                Button(
                    onClick = {
                        if (isLicensed) {
                            onCalculate()
                        } else {
                            onPurchaseRequired()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.forecast_calculate).uppercase())
                }
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            uiState.bestLoanTitle?.let { bestTitle ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.compare_result),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = bestTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                val selectedIndex = uiState.rows.indexOfFirst { it.loanTitle == bestTitle }
                FeatureResultTable(
                    headers = listOf(
                        stringResource(R.string.compare_table_loan),
                        stringResource(R.string.compare_table_savings),
                    ),
                    rows = uiState.rows.map { row ->
                        listOf(
                            row.loanTitle,
                            Formatters.money(row.savings),
                        )
                    },
                    selectedRowIndex = selectedIndex.takeIf { it >= 0 },
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
}
