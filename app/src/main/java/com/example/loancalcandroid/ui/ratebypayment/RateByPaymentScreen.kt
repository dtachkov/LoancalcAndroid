package com.example.loancalcandroid.ui.ratebypayment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.common.LoanDecimalOutlinedTextField
import com.example.loancalcandroid.ui.common.LoanNumberOutlinedTextField
import com.example.loancalcandroid.ui.loan.LoanAmountPresetChips
import com.example.loancalcandroid.ui.loan.LoanTermPresetChips
import com.example.loancalcandroid.util.AmountSpellOut
import com.example.loancalcandroid.util.Formatters

@Composable
fun RateByPaymentScreen(
    onBack: () -> Unit,
    viewModel: RateByPaymentViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val paymentInWords = remember(uiState.paymentAmount, locale) {
        AmountSpellOut.format(Formatters.parseMoney(uiState.paymentAmount).toDouble(), locale)
    }
    val amountInWords = remember(uiState.loanAmount, locale) {
        AmountSpellOut.format(Formatters.parseMoney(uiState.loanAmount).toDouble(), locale)
    }
    val inputTextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)

    LoanCalcScaffold(
        title = stringResource(R.string.rate_by_payment),
        onBack = onBack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoanDecimalOutlinedTextField(
                value = uiState.paymentAmount,
                onValueChange = viewModel::updatePaymentAmount,
                label = { Text(stringResource(R.string.rate_by_payment_amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = inputTextStyle,
                supportingText = paymentInWords?.let {
                    {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LoanDecimalOutlinedTextField(
                    value = uiState.loanAmount,
                    onValueChange = viewModel::updateLoanAmount,
                    label = { Text(stringResource(R.string.loan_editor_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = inputTextStyle,
                    supportingText = amountInWords?.let {
                        {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
                LoanAmountPresetChips(
                    selectedAmountText = uiState.loanAmount,
                    onAmountSelected = viewModel::updateLoanAmount,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LoanNumberOutlinedTextField(
                    value = uiState.term,
                    onValueChange = viewModel::updateTerm,
                    label = { Text(stringResource(R.string.loan_editor_term)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = inputTextStyle,
                    suffix = { Text(stringResource(R.string.loan_editor_term_suffix)) },
                )
                LoanTermPresetChips(
                    selectedTermMonths = uiState.term.trim().toIntOrNull(),
                    onTermMonthsSelected = { months ->
                        viewModel.updateTerm(months.toString())
                    },
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.rate_by_payment_result),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                    Text(
                        text = uiState.rate.ifBlank { "—" },
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    if (uiState.showPaymentTotalError) {
                        Text(
                            text = stringResource(R.string.rate_by_payment_error_total),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
        }
    }
}
