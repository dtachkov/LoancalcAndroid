package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.ExtraForecastException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculationErrorsTest {
    @Test
    fun format_extraForecast_usesHumanMessageWithAmounts() {
        val error = ExtraForecastException("forecast_monthly_Payment_error", 20000.0, 23789.93)
        val message = CalculationErrors.format(error)

        assertTrue(message.contains("20"))
        assertTrue(message.contains("23"))
        assertTrue(message.contains("Увеличьте сумму"))
        assertTrue(message.contains("Расчёт невозможен"))
        assertEquals(false, message.contains("forecast_monthly_Payment_error"))
    }
}