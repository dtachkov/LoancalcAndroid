package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.ExtraForecastException
import com.zoom.loancalc.InfiniteLoanException

object CalculationErrors {
    fun isInfiniteLoan(error: Throwable): Boolean = error is InfiniteLoanException

    fun isExtraForecastError(error: Throwable): Boolean = error is ExtraForecastException

    fun format(e: Throwable): String = when (e) {
        is ExtraForecastException -> {
            val details = e.numbers?.joinToString(", ").orEmpty()
            val message = e.message.orEmpty()
            if (details.isNotBlank()) "$message: $details" else message.ifBlank { "Ошибка прогноза" }
        }
        is InfiniteLoanException -> "Кредит не может быть рассчитан (бесконечный)"
        else -> e.message ?: "Ошибка расчёта"
    }
}
