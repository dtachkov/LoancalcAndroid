package com.example.loancalcandroid.ui.extras

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.loancalcandroid.ui.common.LoanTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loancalcandroid.R
import com.example.loancalcandroid.navigation.Route
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.extrasListViewModel
import com.example.loancalcandroid.ui.theme.LoanBlueDark
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters

private val TableHeaderBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtrasTabsScreen(
    loanId: Long,
    onBack: () -> Unit,
    onAddExtra: (ExtraCategory) -> Unit,
    onEditExtra: (Long) -> Unit,
    onBestDateClick: () -> Unit,
    savedStateHandle: SavedStateHandle? = null,
    initialTab: Int = 0,
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 1)) }
    val tabToRestore = savedStateHandle?.get<Int>(Route.EXTRAS_TAB_KEY)
    LaunchedEffect(tabToRestore) {
        if (tabToRestore != null) {
            selectedTab = tabToRestore.coerceIn(0, 1)
            savedStateHandle.remove<Int>(Route.EXTRAS_TAB_KEY)
        }
    }
    val category = if (selectedTab == 0) ExtraCategory.EARLY else ExtraCategory.COMMISSION

    LoanCalcScaffold(
        title = stringResource(R.string.extras_screen_title),
        onBack = onBack,
        actions = {
            IconButton(onClick = { onAddExtra(category) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_extra))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LoanTabRow(
                selectedTabIndex = selectedTab,
                tabs = listOf(
                    stringResource(R.string.extras_tab_list),
                    stringResource(R.string.extras_tab_commission),
                ),
                onTabSelected = { selectedTab = it },
                uppercase = true,
            )
            ExtrasListContent(
                loanId = loanId,
                category = category,
                onEditExtra = onEditExtra,
                onBestDateClick = onBestDateClick,
            )
        }
    }
}

@Composable
private fun ExtrasListContent(
    loanId: Long,
    category: ExtraCategory,
    onEditExtra: (Long) -> Unit,
    onBestDateClick: () -> Unit,
) {
    val viewModel: ExtrasListViewModel = extrasListViewModel(loanId, category, ::ExtrasListViewModel)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var extraToDelete by remember { mutableStateOf<ExtraListItem?>(null) }

    extraToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { extraToDelete = null },
            title = { Text(stringResource(R.string.delete_extra_title)) },
            text = { Text(stringResource(R.string.delete_extra_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExtra(item.extra.id)
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

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ExtrasTableHeader()
        HorizontalDivider(color = Color.Black, thickness = 1.dp)

        if (uiState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_extras_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoanTextSecondary,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.items, key = { it.extra.id }) { item ->
                    ExtrasTableRow(
                        item = item,
                        onClick = { onEditExtra(item.extra.id) },
                        onLongClick = { extraToDelete = item },
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }

        if (category == ExtraCategory.EARLY) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.extras_saved_money),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                )
                Text(
                    text = Formatters.moneyFixed(uiState.savedMoney),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Button(
                onClick = onBestDateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE9E9E9),
                    contentColor = LoanBlueDark,
                ),
            ) {
                Text(
                    text = stringResource(R.string.extras_best_date_button),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ExtrasTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TableHeaderBackground)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExtrasTableCell(
            text = stringResource(R.string.extra_type),
            weight = 0.9f,
            fontWeight = FontWeight.Bold,
        )
        ExtrasTableCell(
            text = stringResource(R.string.extra_document_number),
            weight = 0.8f,
            fontWeight = FontWeight.Bold,
        )
        ExtrasTableCell(
            text = stringResource(R.string.extra_payment_date),
            weight = 1.1f,
            fontWeight = FontWeight.Bold,
        )
        ExtrasTableCell(
            text = stringResource(R.string.extra_payment_amount),
            weight = 1f,
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ExtrasTableRow(
    item: ExtraListItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(0.9f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(ExtraTypeIcons.icon(item.extra.type)),
                contentDescription = item.typeLabel,
                modifier = Modifier.size(32.dp),
            )
        }
        ExtrasTableCell(
            text = item.documentNumberLabel,
            weight = 0.8f,
        )
        ExtrasTableCell(
            text = item.dateLabel,
            weight = 1.1f,
        )
        ExtrasTableCell(
            text = item.amountLabel,
            weight = 1f,
        )
    }
}

@Composable
private fun RowScope.ExtrasTableCell(
    text: String,
    weight: Float,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 2.dp),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}
