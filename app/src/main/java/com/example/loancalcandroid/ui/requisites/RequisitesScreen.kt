package com.example.loancalcandroid.ui.requisites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.loanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequisitesScreen(
    loanId: Long,
    onBack: () -> Unit,
) {
    val viewModel: RequisitesViewModel = loanViewModel(loanId, ::RequisitesViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { viewModel.save() }
    }

    LoanCalcScaffold(
        title = stringResource(R.string.quick_requisites),
        onBack = {
            viewModel.save()
            onBack()
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LoanOutlinedTextField(
                        value = uiState.bankName,
                        onValueChange = viewModel::updateBankName,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.requisites_bank_name)) },
                        placeholder = { Text(stringResource(R.string.requisites_bank_name_placeholder)) },
                        singleLine = true,
                    )
                    LoanOutlinedTextField(
                        value = uiState.accountNumber,
                        onValueChange = viewModel::updateAccountNumber,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.requisites_account_number)) },
                        placeholder = { Text(stringResource(R.string.requisites_account_number_placeholder)) },
                        singleLine = true,
                    )
                    LoanOutlinedTextField(
                        value = uiState.uic,
                        onValueChange = viewModel::updateUic,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.requisites_bic)) },
                        placeholder = { Text(stringResource(R.string.requisites_bic_placeholder)) },
                        singleLine = true,
                    )
                    LoanOutlinedTextField(
                        value = uiState.correspondentAccount,
                        onValueChange = viewModel::updateCorrespondentAccount,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.requisites_correspondent_account)) },
                        placeholder = { Text(stringResource(R.string.requisites_correspondent_account_placeholder)) },
                        singleLine = true,
                    )
                    LoanOutlinedTextField(
                        value = uiState.paymentComment,
                        onValueChange = viewModel::updatePaymentComment,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.requisites_payment_comment)) },
                        placeholder = { Text(stringResource(R.string.requisites_payment_comment_placeholder)) },
                        minLines = 3,
                    )
                }
            }
        }
    }
}
