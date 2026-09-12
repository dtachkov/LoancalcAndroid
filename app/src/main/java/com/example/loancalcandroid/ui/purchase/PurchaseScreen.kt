package com.example.loancalcandroid.ui.purchase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.loancalcandroid.support.DeveloperSupportUtil
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.PaywallBackground
import com.example.loancalcandroid.ui.theme.PaywallFeatureCard
import com.example.loancalcandroid.ui.theme.PaywallOrangeDark
import com.example.loancalcandroid.ui.theme.PaywallSocialProof
import kotlinx.coroutines.launch

private val PaywallFeatureCardHeight = 88.dp

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
    val coroutineScope = rememberCoroutineScope()
    val noEmailAppMessage = stringResource(R.string.developer_email_no_app)

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
                PaywallUnavailableSection(
                    onContactDeveloper = {
                        val sent = DeveloperSupportUtil.sendFullVersionEmail(context)
                        if (!sent) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(noEmailAppMessage)
                            }
                        }
                    },
                )
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
private fun PaywallUnavailableSection(
    onContactDeveloper: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.paywall_purchase_unavailable),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = LoanTextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onContactDeveloper,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PaywallOrangeDark,
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = stringResource(R.string.menu_item_contact_developer),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
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
