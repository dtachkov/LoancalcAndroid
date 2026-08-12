package com.example.loancalcandroid.ui.purchase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.billing.BillingSupportUtil
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.PaywallBackground
import com.example.loancalcandroid.ui.theme.PaywallBadgeGrey
import com.example.loancalcandroid.ui.theme.PaywallFeatureCard
import com.example.loancalcandroid.ui.theme.PaywallOrange
import com.example.loancalcandroid.ui.theme.PaywallOrangeDark
import com.example.loancalcandroid.ui.theme.PaywallPlanButton
import com.example.loancalcandroid.ui.theme.PaywallSocialProof

private val PaywallFeatureCardHeight = 88.dp
private val PaywallPlanBodyHeight = 196.dp
private val PaywallRecommendedExtraTop = 22.dp
private val PaywallPlanButtonHeight = 56.dp
private val PaywallCrownSlotHeight = 20.dp
private val PaywallCrownButtonGap = 12.dp
private val PaywallPlanTitleSlotHeight = 32.dp
private val PaywallPlanPriceSlotHeight = 34.dp
private val PaywallPlanContentPadding = 10.dp
private val PaywallBadgeOverlap = 14.dp
private val PaywallCrownWidth = 34.dp
private val PaywallCrownHeight = 18.dp

private data class PaywallFeatureItem(
    val iconRes: Int,
    val titleRes: Int,
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PaywallBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaywallBackground,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            if (uiState.isLicensed) {
                LicensedBanner()
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                PaywallHeader()
                Spacer(modifier = Modifier.height(12.dp))
            }

            PaywallFeaturesGrid()
            Spacer(modifier = Modifier.height(20.dp))

            if (!uiState.isLicensed) {
                if (uiState.isLoadingProducts) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = PaywallPlanButton)
                    }
                } else {
                    PaywallPlansRow(
                        options = uiState.options,
                        purchaseInProgress = uiState.purchaseInProgress,
                        onBuy = { productId ->
                            viewModel.purchase(
                                productId = productId,
                                onSuccess = onPurchased,
                                onError = {},
                                onCancelled = {},
                            )
                        },
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            PaywallSocialProof()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.send_support),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { BillingSupportUtil.shareBillingLog(context) }
                    .padding(vertical = 12.dp),
                style = MaterialTheme.typography.bodySmall.copy(
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
private fun LicensedBanner() {
    val premiumExistText = stringResource(R.string.premium_exist)
    val premiumLabel = "Premium"
    val premiumIndex = premiumExistText.indexOf(premiumLabel)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = PaywallFeatureCard,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.all_features_available),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PaywallHeader() {
    Text(
        text = stringResource(R.string.paywall_headline),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
        ),
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.paywall_subheadline),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = LoanTextSecondary,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
    )
}

@Composable
private fun PaywallFeaturesGrid() {
    val features = remember {
        listOf(
            PaywallFeatureItem(R.drawable.ic_paywall_forecast, R.string.paywall_feature_forecast),
            PaywallFeatureItem(R.drawable.ic_paywall_extra_payments, R.string.paywall_feature_extra_payments),
            PaywallFeatureItem(R.drawable.ic_paywall_best_date, R.string.paywall_feature_best_date),
            PaywallFeatureItem(R.drawable.ic_paywall_reminder, R.string.paywall_feature_reminder),
            PaywallFeatureItem(R.drawable.ic_paywall_profit, R.string.paywall_feature_profit),
            PaywallFeatureItem(R.drawable.ic_paywall_best_loan, R.string.paywall_feature_best_loan),
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        features.chunked(2).forEach { rowFeatures ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowFeatures.forEach { feature ->
                    PaywallFeatureCard(
                        iconRes = feature.iconRes,
                        title = stringResource(feature.titleRes),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowFeatures.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PaywallFeatureCard(
    iconRes: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = PaywallFeatureCardHeight)
            .height(PaywallFeatureCardHeight),
        shape = RoundedCornerShape(14.dp),
        color = PaywallFeatureCard,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}

@Composable
private fun PaywallPlansRow(
    options: List<PurchaseOptionUi>,
    purchaseInProgress: String?,
    onBuy: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { clip = false }
            .padding(top = PaywallBadgeOverlap),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        options.forEach { option ->
            PaywallPlanCard(
                option = option,
                isLoading = purchaseInProgress == option.productId,
                onBuy = { onBuy(option.productId) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PaywallPlanCard(
    option: PurchaseOptionUi,
    isLoading: Boolean,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(16.dp)
    val shellHeight = if (option.isRecommended) {
        PaywallPlanBodyHeight + PaywallRecommendedExtraTop
    } else {
        PaywallPlanBodyHeight
    }

    Box(
        modifier = modifier
            .height(shellHeight)
            .fillMaxWidth()
            .graphicsLayer { clip = false },
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(PaywallPlanBodyHeight)
                .fillMaxWidth()
                .then(
                    if (option.isRecommended) {
                        Modifier
                            .shadow(10.dp, cardShape, ambientColor = PaywallOrange, spotColor = PaywallOrange)
                            .border(2.5.dp, PaywallOrange, cardShape)
                    } else {
                        Modifier
                    },
                ),
            shape = cardShape,
            color = PaywallFeatureCard,
        ) {
            PaywallPlanContent(
                option = option,
                isLoading = isLoading,
                onBuy = onBuy,
            )
        }

        if (option.isRecommended && option.discountPercent != null) {
            PaywallBadge(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (PaywallRecommendedExtraTop - PaywallBadgeOverlap).coerceAtLeast(0.dp))
                    .zIndex(2f),
                text = stringResource(R.string.paywall_discount, option.discountPercent),
                backgroundColor = PaywallBadgeGrey,
            )
        }
    }
}

@Composable
private fun PaywallPlanContent(
    option: PurchaseOptionUi,
    isLoading: Boolean,
    onBuy: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaywallPlanContentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PaywallPlanTitleSlot(text = stringResource(option.planTitleRes))
        PaywallPlanPriceSlot(text = option.price)
        PaywallPlanCrownSlot(filled = option.crownFilled)
        Spacer(modifier = Modifier.height(PaywallCrownButtonGap))
        PaywallPlanButton(
            text = stringResource(option.buttonTextRes),
            isRecommended = option.isRecommended,
            isLoading = isLoading,
            onClick = onBuy,
            modifier = Modifier.height(PaywallPlanButtonHeight),
        )
    }
}

@Composable
private fun PaywallPlanTitleSlot(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PaywallPlanTitleSlotHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = LoanTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                fontSize = 10.sp,
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
private fun PaywallPlanPriceSlot(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PaywallPlanPriceSlotHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
private fun PaywallPlanCrownSlot(filled: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PaywallCrownSlotHeight),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                if (filled) R.drawable.ic_crown_filled else R.drawable.ic_crown_outline,
            ),
            contentDescription = null,
            modifier = Modifier.size(width = PaywallCrownWidth, height = PaywallCrownHeight),
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun PaywallBadge(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
            ),
        )
    }
}

@Composable
private fun PaywallPlanButton(
    text: String,
    isRecommended: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isRecommended) PaywallOrangeDark else PaywallPlanButton

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    fontSize = 10.sp,
                ),
                textAlign = TextAlign.Center,
                maxLines = 3,
            )
        }
    }
}

@Composable
private fun PaywallSocialProof() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_paywall_check),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.paywall_social_proof),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = PaywallSocialProof,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
