package com.example.loancalcandroid.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.home.HomeViewModel
import com.example.loancalcandroid.ui.home.components.MenuNavigationRow
import com.example.loancalcandroid.ui.theme.LoanCardSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturePlaceholderScreen(
    title: String,
    loanId: Long? = null,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(R.string.feature_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (loanId != null) {
                Text(
                    text = "loanId: $loanId",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLoansScreen(
    onBack: () -> Unit,
    onLoanClick: (Long) -> Unit,
    onAddLoanClick: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.all_loans_screen)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddLoanClick),
                    shape = MaterialTheme.shapes.medium,
                    color = LoanCardSurface,
                ) {
                    Text(
                        text = stringResource(R.string.add_loan),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            items(uiState.loanCards, key = { it.id }) { card ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLoanClick(card.id) },
                    shape = MaterialTheme.shapes.medium,
                    color = LoanCardSurface,
                ) {
                    MenuNavigationRow(
                        title = card.title,
                        subtitle = "${card.amount} • ${card.rate}",
                        onClick = { onLoanClick(card.id) },
                        showDivider = false,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settings = ru.kredit.calculator.data.LoanCalcData.get().settingsPreferences
    var loadLastLoan by remember { mutableStateOf(settings.isLoadLastLoanAtStart()) }
    var notificationsEnabled by remember { mutableStateOf(settings.areNotificationsEnabled()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = LoanCardSurface,
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                MenuNavigationRow(
                    title = "Загружать последний кредит при старте",
                    subtitle = if (loadLastLoan) "Включено" else "Выключено",
                    onClick = {
                        loadLastLoan = !loadLastLoan
                        settings.setLoadLastLoanAtStart(loadLastLoan)
                    },
                )
                MenuNavigationRow(
                    title = "Уведомления",
                    subtitle = if (notificationsEnabled) "Включены" else "Выключены",
                    onClick = {
                        notificationsEnabled = !notificationsEnabled
                        settings.setNotificationsEnabled(notificationsEnabled)
                    },
                    showDivider = false,
                )
            }
        }
    }
}
