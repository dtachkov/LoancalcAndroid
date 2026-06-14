package com.example.loancalcandroid.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.home.components.DebtProgressSection
import com.example.loancalcandroid.ui.home.components.HomeTopBar
import com.example.loancalcandroid.ui.home.components.LoanActionsSection
import com.example.loancalcandroid.ui.home.components.LoanCardsPager
import com.example.loancalcandroid.ui.home.components.NavigationMenuSection
import com.example.loancalcandroid.ui.home.components.QuickActionsRow
import com.example.loancalcandroid.ui.home.components.StatsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit,
    onAddLoanClick: () -> Unit,
    onEditLoanClick: (Long) -> Unit,
    onEarlyPaymentClick: (Long) -> Unit,
    onScheduleClick: (Long) -> Unit,
    onRequisitesClick: (Long) -> Unit,
    onExtrasClick: (Long) -> Unit,
    onForecastClick: (Long) -> Unit,
    onBestDateClick: (Long) -> Unit,
    onTaxClick: (Long) -> Unit,
    onCompareClick: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val selectedLoanId = uiState.selectedLoanId
    val details = uiState.loanDetails

    if (showDeleteDialog && details != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_loan_title)) },
            text = {
                Text(stringResource(R.string.delete_loan_message, details.title))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSelectedLoan()
                    },
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                onSettingsClick = onSettingsClick,
                onAddLoanClick = onAddLoanClick,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            item {
                LoanCardsPager(
                    summary = uiState.allLoansSummary,
                    loanCards = uiState.loanCards,
                    pagerIndex = uiState.pagerIndex,
                    onPageChanged = viewModel::onPagerPageChanged,
                    onLoanCardClick = onEditLoanClick,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (selectedLoanId != null && details != null) {
                item {
                    QuickActionsRow(
                        enabled = true,
                        onEarlyPaymentClick = { onEarlyPaymentClick(selectedLoanId) },
                        onScheduleClick = { onScheduleClick(selectedLoanId) },
                        onRequisitesClick = { onRequisitesClick(selectedLoanId) },
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    DebtProgressSection(details = details)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    StatsSection(details = details)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    NavigationMenuSection(
                        details = details,
                        onExtrasClick = { onExtrasClick(selectedLoanId) },
                        onForecastClick = { onForecastClick(selectedLoanId) },
                        onBestDateClick = { onBestDateClick(selectedLoanId) },
                        onTaxClick = { onTaxClick(selectedLoanId) },
                        onCompareClick = { onCompareClick(selectedLoanId) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    LoanActionsSection(
                        onEditClick = { onEditLoanClick(selectedLoanId) },
                        onDeleteClick = { showDeleteDialog = true },
                        onDuplicateClick = viewModel::duplicateSelectedLoan,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else if (uiState.loanCards.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.select_loan_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}
