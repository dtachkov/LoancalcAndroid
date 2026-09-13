package ru.kredit.calculator.data.calculation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.pow

class RateByPaymentCalculatorTest {
    @Test
    fun findMonthlyRate_invertsAnnuityPayment() {
        val principal = 1_000_000.0
        val months = 60
        val monthlyRate = 0.01
        val payment = annuityPayment(principal, monthlyRate, months)

        val found = RateByPaymentCalculator.findMonthlyRate(principal, payment, months)

        assertEquals(monthlyRate, found!!, 1e-10)
        assertEquals(12.0, RateByPaymentCalculator.annualPercent(principal, payment, months)!!, 1e-8)
    }

    @Test
    fun annualPercent_forMillionAt25000For60Months() {
        val annual = RateByPaymentCalculator.annualPercent(
            principal = 1_000_000.0,
            payment = 25_000.0,
            months = 60,
        )

        assertEquals(17.273737, annual!!, 1e-6)
    }

    @Test
    fun findMonthlyRate_zeroWhenPaymentEqualsPrincipalOverTerm() {
        val found = RateByPaymentCalculator.findMonthlyRate(
            principal = 120_000.0,
            payment = 10_000.0,
            months = 12,
        )

        assertEquals(0.0, found!!, 1e-12)
    }

    @Test
    fun findMonthlyRate_nullWhenPaymentBelowMinimum() {
        assertNull(
            RateByPaymentCalculator.findMonthlyRate(
                principal = 120_000.0,
                payment = 9_999.0,
                months = 12,
            ),
        )
    }

    private fun annuityPayment(
        principal: Double,
        monthlyRate: Double,
        months: Int,
    ): Double {
        val factor = (1.0 + monthlyRate).pow(months)
        return principal * monthlyRate * factor / (factor - 1.0)
    }
}
