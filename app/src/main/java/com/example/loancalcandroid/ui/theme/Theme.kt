package com.example.loancalcandroid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LoanBlueDark,
    onPrimary = Color.White,
    primaryContainer = LoanBlueStart,
    secondary = LoanBlueEnd,
    background = LoanBackground,
    surface = LoanCardSurface,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E),
    onSurfaceVariant = LoanTextSecondary,
    outline = LoanInputBorder,
)

@Composable
fun LoanCalcAndroidTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
