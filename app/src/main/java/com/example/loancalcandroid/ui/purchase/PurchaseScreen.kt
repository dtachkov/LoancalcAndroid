package com.example.loancalcandroid.ui.purchase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.billing.BillingSupportUtil
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.PremiumBuyButton
import com.example.loancalcandroid.ui.theme.PremiumDivider
import com.example.loancalcandroid.ui.theme.PremiumFeaturesBackground

private data class PremiumFeatureItem(
    val titleRes: Int,
    val descriptionRes: Int,
)

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

    val screenTitle = if (uiState.isLicensed) {
        stringResource(R.string.premium_title)
    } else {
        stringResource(R.string.feature_request_title)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            PremiumStatusSection(
                isLicensed = uiState.isLicensed,
                featureTitle = uiState.featureTitle,
            )

            if (uiState.isLoadingProducts) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.options.forEach { option ->
                    PurchaseOptionRow(
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

            PremiumFeaturesSection()

            Text(
                text = stringResource(R.string.send_support),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { BillingSupportUtil.shareBillingLog(context) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LoanTextSecondary,
                    textDecoration = TextDecoration.Underline,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PremiumStatusSection(
    isLicensed: Boolean,
    featureTitle: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LoanCardSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLicensed) {
                val premiumExistText = stringResource(R.string.premium_exist)
                val premiumLabel = "Premium"
                val premiumIndex = premiumExistText.indexOf(premiumLabel)
                Text(
                    text = if (premiumIndex >= 0) {
                        buildAnnotatedString {
                            append(premiumExistText.substring(0, premiumIndex))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(premiumLabel)
                            }
                            append(premiumExistText.substring(premiumIndex + premiumLabel.length))
                        }
                    } else {
                        buildAnnotatedString { append(premiumExistText) }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.all_features_available),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = stringResource(R.string.you_are_try_function, featureTitle),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.trying_to_use_only_paid),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}

@Composable
private fun PurchaseOptionRow(
    title: String,
    price: String,
    isLoading: Boolean,
    onBuy: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = PremiumDivider,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_premium),
                contentDescription = null,
                modifier = Modifier.size(width = 22.dp, height = 11.dp),
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = price,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            } else {
                Surface(
                    modifier = Modifier.clickable(onClick = onBuy),
                    shape = RoundedCornerShape(4.dp),
                    color = PremiumBuyButton,
                ) {
                    Text(
                        text = stringResource(R.string.buy_button_title).uppercase(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = PremiumDivider,
        )
    }
}

@Composable
private fun PremiumFeaturesSection() {
    val features = remember {
        listOf(
            PremiumFeatureItem(R.string.feature_extra_forecast, R.string.feature_forecast_description),
            PremiumFeatureItem(R.string.feature_extra_payments, R.string.feature_extra_payments_description),
            PremiumFeatureItem(R.string.feature_best_date, R.string.feature_best_date_description),
            PremiumFeatureItem(R.string.feature_reminder, R.string.feature_reminder_description),
            PremiumFeatureItem(R.string.feature_extra_profit, R.string.feature_extra_profit_description),
            PremiumFeatureItem(R.string.feature_best_loan, R.string.feature_best_loan_description),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(PremiumFeaturesBackground),
    ) {
        Text(
            text = stringResource(R.string.all_features_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        features.forEach { feature ->
            PremiumFeatureRow(
                title = stringResource(feature.titleRes),
                description = stringResource(feature.descriptionRes),
            )
        }
    }
}

@Composable
private fun PremiumFeatureRow(
    title: String,
    description: String,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "featureChevron",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PremiumFeaturesBackground)
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.rotate(rotation),
                tint = LoanTextSecondary,
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
            )
        }
    }
}
