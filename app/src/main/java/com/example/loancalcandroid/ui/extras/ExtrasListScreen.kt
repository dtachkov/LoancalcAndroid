package com.example.loancalcandroid.ui.extras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.home.components.MenuNavigationRow
import com.example.loancalcandroid.ui.loanViewModel
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.util.Formatters

@Composable
fun ExtrasListScreen(
    loanId: Long,
    onBack: () -> Unit,
    onAddExtra: () -> Unit,
    onEditExtra: (Long) -> Unit,
) {
    val viewModel: ExtrasListViewModel = loanViewModel(loanId, ::ExtrasListViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var extraToDelete by remember { mutableStateOf<Long?>(null) }

    if (extraToDelete != null) {
        AlertDialog(
            onDismissRequest = { extraToDelete = null },
            title = { Text(stringResource(R.string.delete_extra_title)) },
            text = { Text(stringResource(R.string.delete_extra_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        extraToDelete?.let(viewModel::deleteExtra)
                        extraToDelete = null
                    },
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { extraToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    LoanCalcScaffold(
        title = stringResource(R.string.menu_extras_list),
        onBack = onBack,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExtra) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_extra))
            }
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@LoanCalcScaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = LoanCardSurface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.menu_extras_savings, Formatters.money(uiState.savedMoney)),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            if (uiState.items.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_extras_hint),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            items(uiState.items, key = { it.extra.id }) { item ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = LoanCardSurface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            MenuNavigationRow(
                                title = item.typeLabel,
                                subtitle = "${item.dateLabel} • ${item.amountLabel}",
                                onClick = { onEditExtra(item.extra.id) },
                                showDivider = false,
                            )
                        }
                        IconButton(onClick = { extraToDelete = item.extra.id }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                }
            }
        }
    }
}
