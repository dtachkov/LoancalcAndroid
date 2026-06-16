package ru.kredit.calculator.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kredit.calculator.data.calculation.LoanCalculator
import ru.kredit.calculator.data.util.DateFormats
import java.util.Calendar
import java.util.GregorianCalendar

class LoanExtensionsTest {
    @Test
    fun effectiveIssueDate_usesManualDateWhenSet() {
        val issueDate = GregorianCalendar(2026, Calendar.JUNE, 10).time
        val firstPaymentDate = GregorianCalendar(2026, Calendar.JULY, 14).time

        val loan = Loan(
            firstPaymentDate = firstPaymentDate,
            dateOfIssue = issueDate,
        )

        assertEquals(issueDate, loan.effectiveIssueDate())
    }

    @Test
    fun effectiveIssueDate_usesMonthBeforeFirstPaymentWhenIssueDateMissing() {
        val firstPaymentDate = GregorianCalendar(2026, Calendar.JULY, 14).time

        val loan = Loan(firstPaymentDate = firstPaymentDate)

        val expected = GregorianCalendar(2026, Calendar.JUNE, 14).time
        assertEquals(expected, loan.effectiveIssueDate())
    }

    @Test
    fun calculate_firstPaymentIsPositiveWhenIssueDateSet() {
        val loan = Loan(
            amount = 1_000_000f,
            rate = 12f,
            term = 120,
            firstPaymentDate = DateFormats.clearTime(GregorianCalendar(2026, Calendar.JUNE, 30).time),
            dateOfIssue = DateFormats.clearTime(GregorianCalendar(2026, Calendar.JUNE, 3).time),
        )

        val result = LoanCalculator().calculate(loan, emptyList())
        val firstPayment = result.payments.first()

        assertTrue(firstPayment.total > 0)
        assertEquals(0.0, firstPayment.principal, 0.01)
    }
}
