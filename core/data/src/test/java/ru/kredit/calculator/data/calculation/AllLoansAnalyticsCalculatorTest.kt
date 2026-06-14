package ru.kredit.calculator.data.calculation

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanFull
import ru.kredit.calculator.data.model.LoanType
import java.util.Calendar
import java.util.GregorianCalendar

class AllLoansAnalyticsCalculatorTest {
    private val calculator = AllLoansAnalyticsCalculator(LoanCalculator())

    @Test
    fun timeline_debtDecreasesAndInterestGrows() = runBlocking {
        val firstPayment = GregorianCalendar(2025, Calendar.JULY, 15).time
        val loan = Loan(
            id = 1,
            title = "Test",
            amount = 1_000_000f,
            rate = 12f,
            term = 120,
            type = LoanType.ANNUITY,
            firstPaymentDate = firstPayment,
        )
        val data = calculator.calculate(listOf(LoanFull(loan = loan, extras = emptyList())))
            ?: error("Expected analytics data")

        val timeline = data.timeline
        assertTrue("Expected multiple timeline points, got ${timeline.size}", timeline.size > 12)

        val firstDebt = timeline.first().remainingDebt
        val lastDebt = timeline.last().remainingDebt
        val lastInterest = timeline.last().cumulativeInterest

        assertTrue("First debt should be close to loan amount: $firstDebt", firstDebt > 900_000)
        assertTrue("Last debt should approach zero: $lastDebt", lastDebt < 1_000)
        assertTrue("Interest should grow: $lastInterest", lastInterest > 100_000)
        assertTrue(
            "Debt should decrease over time",
            timeline.zipWithNext().count { (a, b) -> a.remainingDebt >= b.remainingDebt } > timeline.size / 2,
        )
    }

    @Test
    fun timeline_smallLoan_matchesUserScale() = runBlocking {
        val firstPayment = GregorianCalendar(2025, Calendar.JUNE, 15).time
        val loan1 = Loan(
            id = 1,
            title = "Big",
            amount = 11_732f,
            rate = 12f,
            term = 120,
            type = LoanType.ANNUITY,
            firstPaymentDate = firstPayment,
        )
        val loan2 = Loan(
            id = 2,
            title = "Small",
            amount = 122f,
            rate = 12f,
            term = 12,
            type = LoanType.ANNUITY,
            firstPaymentDate = firstPayment,
        )
        val data = calculator.calculate(
            listOf(
                LoanFull(loan = loan1, extras = emptyList()),
                LoanFull(loan = loan2, extras = emptyList()),
            ),
        ) ?: error("Expected analytics data")

        val timeline = data.timeline
        println("Timeline size: ${timeline.size}")
        println("First: debt=${timeline.first().remainingDebt}, interest=${timeline.first().cumulativeInterest}")
        println("Last: debt=${timeline.last().remainingDebt}, interest=${timeline.last().cumulativeInterest}")
        println("Mid: debt=${timeline[timeline.size / 2].remainingDebt}, interest=${timeline[timeline.size / 2].cumulativeInterest}")

        assertTrue(timeline.size > 12)
        assertTrue(timeline.last().remainingDebt < 100)
        assertTrue(timeline.last().cumulativeInterest > 5_000)
    }
}
