package com.example.loancalcandroid.ui.offers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.loancalcandroid.R
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.model.Offer

@Composable
fun OffersScreen(
    onBack: () -> Unit,
    onOfferClick: (Long) -> Unit,
    viewModel: OffersViewModel = viewModel(),
) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanCalcScaffold(
        title = stringResource(R.string.offers_screen),
        onBack = onBack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            OutlinedButton(
                onClick = viewModel::refreshOffers,
                enabled = !uiState.isRefreshing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            ) {
                if (uiState.isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text(stringResource(R.string.offers_refresh))
            }

            uiState.refreshError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            if (offers.isEmpty() && !uiState.isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.offers_empty),
                        color = LoanTextSecondary,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(offers, key = { it.id }) { offer ->
                        OfferGridCard(
                            offer = offer,
                            onCardClick = { onOfferClick(offer.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfferGridCard(
    offer: Offer,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val frameColor = parseOfferColor(offer.logoColor)
    val contentColor = offerContentColor(frameColor)
    val rateText = offer.rate?.defaultValue()?.let { Formatters.schedulePercent(it) }.orEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offerFrame(frameColor)
                .padding(8.dp),
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = offer.name.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            )

            Text(
                text = stringResource(
                    R.string.offers_amount_limit,
                    Formatters.moneyWithoutDecimal(offer.limit),
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.offers_rate_term, rateText, offer.term),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )

            OfferOrderButton(
                text = stringResource(R.string.offers_order),
                onClick = {
                    offer.link?.let { link ->
                        AnalyticsHelper.openOfferLink(context, offer.name, link)
                    }
                },
                enabled = !offer.link.isNullOrBlank(),
                brandedColor = frameColor,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}
