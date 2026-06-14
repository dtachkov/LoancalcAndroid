package com.example.loancalcandroid.ui.purchase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    featureTitle: String,
    onBack: () -> Unit,
    onPurchased: () -> Unit = onBack,
) {
    val context = LocalContext.current
    val viewModel: PurchaseViewModel = viewModel(
        factory = PurchaseViewModelFactory(
            application = context.applicationContext as android.app.Application,
            featureTitle = featureTitle,
        ),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        val message = uiState.message ?: return@LaunchedEffect
        val text = when (message) {
            "products_unavailable" -> context.getString(R.string.play_store_unavailable)
            "purchase_not_confirmed" -> context.getString(R.string.buy_complete)
            else -> message
        }
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isLicensed) {
                            stringResource(R.string.premium_title)
                        } else {
                            stringResource(R.string.feature_request_title)
                        },
                    )
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (uiState.isLicensed) {
                Text(
                    text = stringResource(R.string.premium_exist),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.all_features_available),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                )
            } else {
                Text(
                    text = stringResource(R.string.you_are_try_function, uiState.featureTitle),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.trying_to_use_only_paid),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                )

                if (uiState.isLoadingProducts) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    uiState.options.forEach { option ->
                        PurchaseOptionCard(
                            title = option.title,
                            price = option.price,
                            isLoading = uiState.purchaseInProgress == option.productId,
                            onBuy = {
                                viewModel.purchase(
                                    productId = option.productId,
                                    onSuccess = onPurchased,
                                    onError = {},
                                    onCancelled = {},
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseOptionCard(
    title: String,
    price: String,
    isLoading: Boolean,
    onBuy: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = LoanCardSurface,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = onBuy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.buy_button_title))
                }
            }
        }
    }
}
