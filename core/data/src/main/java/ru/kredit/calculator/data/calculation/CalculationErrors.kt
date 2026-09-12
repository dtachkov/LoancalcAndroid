package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.ExtraForecastException
import com.zoom.loancalc.InfiniteLoanException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CalculationErrors {
    private val moneyFormat = DecimalFormat(
        "#,##0.##",
        DecimalFormatSymbols(Locale("ru", "RU")),
    )

    fun isInfiniteLoan(error: Throwable): Boolean = error is InfiniteLoanException

    fun isExtraForecastError(error: Throwable): Boolean = error is ExtraForecastException

    fun format(e: Throwable): String = when (e) {
        is ExtraForecastException -> formatForecastError(e)
        is InfiniteLoanException -> "Кредит не может быть рассчитан (бесконечный)"
        else -> e.message ?: "Ошибка расчёта"
    }

    private fun formatForecastError(e: ExtraForecastException): String {
        val numbers = e.numbers.orEmpty().mapNotNull { it.replace(',', '.').toDoubleOrNull() }
        return if (numbers.size >= 2) {
            val forecast = moneyFormat.format(numbers[0])
            val monthly = moneyFormat.format(numbers[1])
            "Сумма платежа прогноза $forecast превышает ежемесячный платеж $monthly. Увеличьте сумму. Расчёт невозможен."
        } else {
            "Сумма платежа прогноза меньше ежемесячного платежа. Увеличьте сумму. Расчёт невозможен."
        }
    }
}
