package ru.kredit.calculator.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.util.Calendar
import java.util.GregorianCalendar

class EntityMappersTest {
    @Test
    fun loanEntityRoundTripPreservesValues() {
        val creationDate = GregorianCalendar(2026, Calendar.JUNE, 14).time
        val firstPaymentDate = GregorianCalendar(2026, Calendar.JULY, 14).time
        val forecastStartDate = GregorianCalendar(2026, Calendar.AUGUST, 1).time

        val source = Loan(
            id = 10,
            creationDate = creationDate,
            title = "test",
            amount = 1000f,
            rate = 11.5f,
            term = 24,
            type = LoanType.ANNUITY,
            firstPaymentDate = firstPaymentDate,
            monthlyPayment = 0f,
            dateOfIssue = creationDate,
            considerDaysOff = true,
            payOnLastDayOfMonth = false,
            applyExtrasImmediately = true,
            calculateExtrasByBalanceLikeSberbank = true,
            ignorePassedPeriodsAfterRateChange = false,
            extraDayInMonth = true,
            isForecastActive = true,
            forecastMonthlyPayment = 12000f,
            forecastDaysBefore = 3,
            forecastStartDate = forecastStartDate,
            forecastExtraType = ExtraType.PAYMENT_FOR_DECREASE_TERM,
        )

        val restored = source.toEntity().toDomain()

        assertEquals(source.id, restored.id)
        assertEquals(source.title, restored.title)
        assertEquals(source.type, restored.type)
        assertTrue(restored.considerDaysOff)
        assertTrue(restored.isForecastActive)
        assertEquals(ExtraType.PAYMENT_FOR_DECREASE_TERM, restored.forecastExtraType)
        assertEquals("2026-06-14", source.toEntity().creationDate)
    }

    @Test
    fun newLoanEntityUsesNullIdForInsert() {
        val loan = Loan(
            id = 0,
            amount = 1000f,
            rate = 10f,
            term = 12,
        )

        assertEquals(null, loan.toEntity().id)
    }
}
