package com.example.loancalcandroid.ui.offers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.loancalcandroid.R
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.theme.LoanDivider
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.ui.theme.OfferAccentText
import com.example.loancalcandroid.util.Formatters

@Composable
fun OfferDetailScreen(
    offerId: Long,
    onBack: () -> Unit,
) {
    val viewModel: OfferDetailViewModel = com.example.loancalcandroid.ui.offerDetailViewModel(offerId) { app, id ->
        OfferDetailViewModel(app, id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    LoanCalcScaffold(
        title = stringResource(R.string.offer_detail_title),
        onBack = onBack,
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.offer_save_loan)) },
                    onClick = {
                        menuExpanded = false
                        viewModel.saveToMyLoans()
                    },
                    enabled = !uiState.isSaving,
                )
            }
        },
        bottomBar = {
            val offer = uiState.offer
            if (offer != null && !uiState.isLoading) {
                Column {
                    HorizontalDivider(color = LoanDivider)
                    OfferOrderButton(
                        text = stringResource(R.string.offer_order_loan),
                        onClick = {
                            offer.link?.let { link ->
                                AnalyticsHelper.openOfferLink(context, offer.name, link)
                            }
                        },
                        enabled = !offer.link.isNullOrBlank(),
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
            return@LoanCalcScaffold
        }

        val offer = uiState.offer
        if (offer == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                Text(text = uiState.error ?: stringResource(R.string.offer_not_found))
            }
            return@LoanCalcScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offerFrame()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = offer.logoImage,
                    contentDescription = offer.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(27.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = offer.organizationName.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
                Text(
                    text = offer.name.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
            }

            OfferSectionHeader(text = stringResource(R.string.offer_terms))

            OfferInputLabelRow(
                label = stringResource(R.string.offer_amount_label),
                value = Formatters.moneyWithoutDecimal(uiState.selectedAmount.toDouble()),
            )
            Slider(
                value = uiState.amountProgress.toFloat(),
                onValueChange = { viewModel.updateAmountProgress(it.toInt()) },
                valueRange = 0f..uiState.amountMaxProgress.toFloat().coerceAtLeast(0f),
                steps = uiState.amountMaxProgress.coerceAtLeast(1) - 1,
                colors = SliderDefaults.colors(
                    thumbColor = OfferAccentText,
                    activeTrackColor = OfferAccentText,
                ),
            )

            OfferInputLabelRow(
                label = stringResource(R.string.offer_term_label),
                value = uiState.selectedTerm.toString(),
            )
            Slider(
                value = uiState.termProgress.toFloat(),
                onValueChange = { viewModel.updateTermProgress(it.toInt()) },
                valueRange = 0f..uiState.termMaxProgress.toFloat().coerceAtLeast(0f),
                steps = uiState.termMaxProgress.coerceAtLeast(1) - 1,
                colors = SliderDefaults.colors(
                    thumbColor = OfferAccentText,
                    activeTrackColor = OfferAccentText,
                ),
            )

            OfferInputLabelRow(
                label = stringResource(R.string.offer_rate_label),
                value = uiState.rateText,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OfferCalculationCard(
                monthlyPayment = uiState.monthlyPaymentText,
                monthlyPaymentLabel = stringResource(R.string.offer_monthly_payment),
                overpay = uiState.overpayText,
                overpayLabel = stringResource(R.string.offer_overpay),
            )

            offer.documents?.let {
                OfferSectionHeader(text = stringResource(R.string.offer_documents))
                OfferHtmlBody(html = it)
            }
            offer.requirements?.let {
                OfferSectionHeader(text = stringResource(R.string.offer_requirements))
                OfferHtmlBody(html = it)
            }
            offer.extraPaymentRules?.takeIf { it.isNotBlank() }?.let {
                OfferSectionHeader(text = stringResource(R.string.offer_early_payments))
                OfferHtmlBody(html = it)
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            uiState.savedMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun OfferHtmlBody(html: String) {
    Text(
        text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString(),
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Black,
        modifier = Modifier.padding(top = 8.dp),
    )
}
