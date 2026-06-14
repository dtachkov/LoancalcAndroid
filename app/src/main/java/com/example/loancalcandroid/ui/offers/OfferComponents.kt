package com.example.loancalcandroid.ui.offers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loancalcandroid.ui.theme.OfferAccentText
import com.example.loancalcandroid.ui.theme.OfferButtonBackground
import com.example.loancalcandroid.ui.theme.OfferButtonText
import com.example.loancalcandroid.ui.theme.OfferFrameBorder

fun parseOfferColor(color: String?): Color? {
    if (color.isNullOrBlank()) return null
    return runCatching { Color(android.graphics.Color.parseColor(color)) }.getOrNull()
}

fun offerContentColor(frameColor: Color?): Color {
    if (frameColor == null) return Color.Black
    return if (frameColor.luminance() < 0.5f) Color.White else Color.Black
}

@Composable
fun Modifier.offerFrame(
    frameColor: Color? = null,
): Modifier {
    val shape = RoundedCornerShape(0.dp)
    return if (frameColor != null) {
        background(frameColor, shape)
    } else {
        background(Color.White, shape)
            .border(BorderStroke(1.dp, OfferFrameBorder), shape)
    }
}

@Composable
fun OfferSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(top = 8.dp),
        color = OfferAccentText,
        fontSize = 18.sp,
    )
}

@Composable
fun OfferOrderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brandedColor: Color? = null,
) {
    val textColor = brandedColor ?: OfferButtonText
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OfferButtonBackground,
            contentColor = textColor,
            disabledContainerColor = OfferButtonBackground.copy(alpha = 0.5f),
            disabledContentColor = textColor.copy(alpha = 0.5f),
        ),
        border = BorderStroke(1.dp, textColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text = text)
    }
}

@Composable
fun OfferCalculationCard(
    monthlyPayment: String,
    monthlyPaymentLabel: String,
    overpay: String,
    overpayLabel: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .offerFrame()
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            Text(
                text = monthlyPayment,
                color = OfferAccentText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = monthlyPaymentLabel,
                color = OfferAccentText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = overpay,
                color = OfferAccentText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = overpayLabel,
                color = OfferAccentText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun OfferInputLabelRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            textAlign = TextAlign.End,
        )
    }
}
