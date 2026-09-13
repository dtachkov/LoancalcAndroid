package ru.kredit.calculator.data.calculation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class FullRepaymentCalculatorTest {
    @Test
    fun calculate_withoutImmediateFlag_skipsExtraRows_andTakesNextPlannedPayment() {
        val extrasRow = payment(
            date = date(2026, Calendar.SEPTEMBER, 10),
            total = 5_000.0,
            endBalance = 90_000.0,
            extras = 5_000.0,
        )
        val planned = payment(
            date = date(2026, Calendar.SEPTEMBER, 15),
            total = 12_000.0,
            endBalance = 80_000.0,
        )

        val result = FullRepaymentCalculator.calculate(
            loan = loan(applyExtrasImmediately = false),
            payments = listOf(extrasRow, planned),
            extras = emptyList(),
            plannedDate = date(2026, Calendar.SEPTEMBER, 12),
        )

        assertEquals(92_000.0, result!!.totalAmount, 0.001)
        assertEquals(12_000.0, result.includedAmount, 0.001)
        assertEquals(80_000.0, result.remainingDebt, 0.001)
        assertEquals(80_000.0, result.extraPrefillAmount, 0.001)
        assertFalse(result.includesInterest)
        assertEquals(planned.date, result.scheduledPaymentDate)
    }

    @Test
    fun calculate_withoutImmediateFlag_usesPaymentOnTheSameDay() {
        val planned = payment(
            date = date(2026, Calendar.SEPTEMBER, 13, hour = 18),
            total = 10_000.0,
            endBalance = 50_000.0,
        )

        val result = FullRepaymentCalculator.calculate(
            loan = loan(applyExtrasImmediately = false),
            payments = listOf(planned),
            extras = emptyList(),
            plannedDate = date(2026, Calendar.SEPTEMBER, 13, hour = 9),
        )

        assertEquals(60_000.0, result!!.totalAmount, 0.001)
    }

    @Test
    fun calculate_withoutImmediateFlag_returnsNull_whenDateIsAfterLastPlannedPayment() {
        val planned = payment(
            date = date(2026, Calendar.AUGUST, 15),
            total = 10_000.0,
            endBalance = 0.0,
        )

        val result = FullRepaymentCalculator.calculate(
            loan = loan(applyExtrasImmediately = false),
            payments = listOf(planned),
            extras = emptyList(),
            plannedDate = date(2026, Calendar.SEPTEMBER, 13),
        )

        assertNull(result)
    }

    @Test
    fun calculate_withImmediateFlag_usesDebtBeforeDate_andAccruedInterest() {
        val firstPaymentDate = date(2026, Calendar.SEPTEMBER, 15)
        val loan = loan(
            applyExtrasImmediately = true,
            amount = 100_000f,
            rate = 12f,
            firstPaymentDate = firstPaymentDate,
        )
        val previous = payment(
            date = firstPaymentDate,
            total = 5_000.0,
            endBalance = 95_000.0,
        )
        val next = payment(
            date = date(2026, Calendar.OCTOBER, 15),
            total = 5_000.0,
            endBalance = 90_000.0,
        )
        val extraOnTheWay = payment(
            date = date(2026, Calendar.SEPTEMBER, 20),
            total = 0.0,
            endBalance = 90_000.0,
            extras = 5_000.0,
        )
        val payments = listOf(previous, extraOnTheWay, next)
        val plannedDate = date(2026, Calendar.SEPTEMBER, 25)
        val interest = ExtraInterestCalculator.interestForExtraDate(
            loan = loan,
            payments = payments,
            extras = emptyList(),
            extraDate = plannedDate,
        )

        val result = FullRepaymentCalculator.calculate(
            loan = loan,
            payments = payments,
            extras = emptyList(),
            plannedDate = plannedDate,
        )

        assertTrue(result!!.includesInterest)
        assertEquals(90_000.0, result.remainingDebt, 0.001)
        assertEquals(interest, result.includedAmount, 0.001)
        assertEquals(interest + 90_000.0, result.totalAmount, 0.001)
        assertEquals(ceilToCents(result.totalAmount), result.extraPrefillAmount, 0.001)
    }

    @Test
    fun calculate_withImmediateFlag_usesLoanAmount_whenNoPaymentsBeforeDate() {
        val loan = loan(
            applyExtrasImmediately = true,
            amount = 120_000f,
            rate = 10f,
            firstPaymentDate = date(2026, Calendar.OCTOBER, 15),
        )
        val firstPayment = payment(
            date = date(2026, Calendar.OCTOBER, 15),
            total = 4_000.0,
            endBalance = 116_000.0,
        )
        val plannedDate = date(2026, Calendar.SEPTEMBER, 20)
        val interest = ExtraInterestCalculator.interestForExtraDate(
            loan = loan,
            payments = listOf(firstPayment),
            extras = emptyList(),
            extraDate = plannedDate,
        )

        val result = FullRepaymentCalculator.calculate(
            loan = loan,
            payments = listOf(firstPayment),
            extras = emptyList(),
            plannedDate = plannedDate,
        )

        assertEquals(120_000.0, result!!.remainingDebt, 0.001)
        assertEquals(interest, result.includedAmount, 0.001)
        assertEquals(interest + 120_000.0, result.totalAmount, 0.001)
    }

    @Test
    fun extraPrefillAmount_ceilsToTwoDecimalPlaces() {
        val result = FullRepaymentResult(
            totalAmount = 975_881.711,
            includedAmount = 8_271.088,
            remainingDebt = 967_610.623,
            includesInterest = true,
        )

        assertEquals(975_881.72, result.extraPrefillAmount, 0.0001)
    }

    private fun loan(
        applyExtrasImmediately: Boolean,
        amount: Float = 100_000f,
        rate: Float = 12f,
        firstPaymentDate: Date = date(2026, Calendar.SEPTEMBER, 15),
    ): Loan {
        return Loan(
            id = 1,
            title = "Test",
            amount = amount,
            rate = rate,
            term = 12,
            type = LoanType.ANNUITY,
            firstPaymentDate = firstPaymentDate,
            applyExtrasImmediately = applyExtrasImmediately,
        )
    }

    private fun payment(
        date: Date,
        total: Double,
        endBalance: Double,
        extras: Double = 0.0,
        rateExtra: Double = 0.0,
    ): PaymentSummary {
        return PaymentSummary(
            index = 1,
            date = date,
            total = total,
            principal = total,
            interest = 0.0,
            endBalance = endBalance,
            extras = extras,
            rateExtra = rateExtra,
            isCurrent = false,
        )
    }

    private fun date(year: Int, month: Int, day: Int, hour: Int = 0): Date {
        return GregorianCalendar(year, month, day, hour, 0, 0).time
    }
}
