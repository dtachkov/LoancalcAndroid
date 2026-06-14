package ru.kredit.calculator.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
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
}
