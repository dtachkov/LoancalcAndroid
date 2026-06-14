package com.example.loancalcandroid.ui.offers

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
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

    LoanCalcScaffold(
        title = stringResource(R.string.offer_detail_title),
        onBack = onBack,
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
                Text(text = uiState.error ?: "Предложение не найдено")
            }
            return@LoanCalcScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = offer.logoImage,
                    contentDescription = offer.name,
                    modifier = Modifier.size(56.dp),
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = offer.organizationName.orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = LoanTextSecondary,
                    )
                    Text(
                        text = offer.name.orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            OfferSliderSection(
                label = stringResource(R.string.offer_amount),
                valueText = Formatters.moneyWithoutDecimal(uiState.selectedAmount.toDouble()),
                progress = uiState.amountProgress,
                max = uiState.amountMaxProgress,
                onProgressChange = viewModel::updateAmountProgress,
            )
            OfferSliderSection(
                label = stringResource(R.string.loan_term),
                valueText = uiState.selectedTerm.toString(),
                progress = uiState.termProgress,
                max = uiState.termMaxProgress,
                onProgressChange = viewModel::updateTermProgress,
            )

            OfferValueRow(stringResource(R.string.loan_rate), uiState.rateText)
            OfferValueRow(stringResource(R.string.offer_monthly_payment), uiState.monthlyPaymentText)
            OfferValueRow(stringResource(R.string.offer_overpay), uiState.overpayText)

            offer.documents?.let {
                OfferHtmlSection(stringResource(R.string.offer_documents), it)
            }
            offer.requirements?.let {
                OfferHtmlSection(stringResource(R.string.offer_requirements), it)
            }
            offer.extraPaymentRules?.takeIf { it.isNotBlank() }?.let {
                OfferHtmlSection(stringResource(R.string.offer_early_payments), it)
            }

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            uiState.savedMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    offer.link?.let { link ->
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !offer.link.isNullOrBlank(),
            ) {
                Text(stringResource(R.string.offers_order))
            }
            Button(
                onClick = viewModel::saveToMyLoans,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(stringResource(R.string.offer_save_loan))
            }
        }
    }
}

@Composable
private fun OfferSliderSection(
    label: String,
    valueText: String,
    progress: Int,
    max: Int,
    onProgressChange: (Int) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(text = valueText, style = MaterialTheme.typography.bodyLarge)
        }
        Slider(
            value = progress.toFloat(),
            onValueChange = { onProgressChange(it.toInt()) },
            valueRange = 0f..max.toFloat().coerceAtLeast(0f),
            steps = max.coerceAtLeast(1) - 1,
        )
    }
}

@Composable
private fun OfferValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OfferHtmlSection(title: String, html: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = LoanTextSecondary,
        )
    }
}
