package com.example.loancalcandroid.ui.schedule

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.kredit.calculator.data.calculation.PaymentSummary
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.util.Date

class ScheduleRowsGeneratorTest {
    @Test
    fun generate_usesRowPositionNotPaymentIndex_forDisplayNumber() {
        val loan = Loan(
            id = 1L,
            title = "Test",
            type = LoanType.ANNUITY,
            applyExtrasImmediately = false,
        )
        val payments = listOf(
            payment(index = 0, extras = 100.0),
            payment(index = 1),
            payment(index = 2, extras = 50.0),
            payment(index = 3),
        )

        val rows = ScheduleRowsGenerator.generate(loan, payments, currentPaymentIndex = 1)

        assertEquals("+", rows[0].displayNumber)
        assertEquals("1", rows[1].displayNumber)
        assertEquals("+", rows[2].displayNumber)
        assertEquals("2", rows[3].displayNumber)
    }

    private fun payment(
        index: Int,
        extras: Double = 0.0,
        rateExtra: Double = 0.0,
    ): PaymentSummary {
        return PaymentSummary(
            index = index,
            date = Date(126_230_400_000L + index.toLong() * 86_400_000L),
            total = 1_000.0,
            principal = 800.0,
            interest = 200.0,
            endBalance = 10_000.0 - index * 800.0,
            extras = extras,
            rateExtra = rateExtra,
            isCurrent = false,
        )
    }
}
