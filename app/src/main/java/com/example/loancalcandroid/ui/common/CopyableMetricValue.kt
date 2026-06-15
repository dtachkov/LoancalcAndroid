package com.example.loancalcandroid.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.loancalcandroid.util.copyMetricValueToClipboard

@Composable
fun CopyableMetricValue(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    val context = LocalContext.current
    val resolvedStyle = if (fontWeight != null) style.copy(fontWeight = fontWeight) else style
    Text(
        text = text,
        modifier = modifier.clickable { copyMetricValueToClipboard(context, text) },
        style = resolvedStyle,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
    )
}
