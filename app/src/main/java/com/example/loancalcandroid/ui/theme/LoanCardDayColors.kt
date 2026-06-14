package com.example.loancalcandroid.ui.theme

import androidx.compose.ui.graphics.Color

object LoanCardDayColors {

    private val dayColors: List<Color> = listOf(
        Color(0xFFE53935), // 1
        Color(0xFFD84315), // 2
        Color(0xFFEF6C00), // 3
        Color(0xFFF57C00), // 4
        Color(0xFFFB8C00), // 5
        Color(0xFFF9A825), // 6
        Color(0xFFFBC02D), // 7
        Color(0xFFFDD835), // 8
        Color(0xFFC0CA33), // 9
        Color(0xFFAFB42B), // 10
        Color(0xFF9E9D24), // 11
        Color(0xFF827717), // 12
        Color(0xFF558B2F), // 13
        Color(0xFF689F38), // 14
        Color(0xFF7CB342), // 15
        Color(0xFF8BC34A), // 16
        Color(0xFF43A047), // 17
        Color(0xFF388E3C), // 18
        Color(0xFF2E7D32), // 19
        Color(0xFF00897B), // 20
        Color(0xFF009688), // 21
        Color(0xFF00ACC1), // 22
        Color(0xFF039BE5), // 23
        Color(0xFF1E88E5), // 24
        Color(0xFF1976D2), // 25
        Color(0xFF1565C0), // 26
        Color(0xFF3949AB), // 27
        Color(0xFF5E35B1), // 28
        Color(0xFF7B1FA2), // 29
        Color(0xFF8E24AA), // 30
        Color(0xFFC2185B), // 31
    )

    fun colorForDay(day: Int): Color = dayColors[(day.coerceIn(1, 31) - 1)]

    fun gradientForDay(day: Int): Pair<Color, Color> {
        val start = colorForDay(day)
        val end = darken(start, 0.22f)
        return start to end
    }

    private fun darken(color: Color, factor: Float): Color {
        return Color(
            red = color.red * (1f - factor),
            green = color.green * (1f - factor),
            blue = color.blue * (1f - factor),
            alpha = color.alpha,
        )
    }
}
