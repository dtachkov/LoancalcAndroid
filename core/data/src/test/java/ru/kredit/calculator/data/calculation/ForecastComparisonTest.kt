package ru.kredit.calculator.data.calculation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class ForecastComparisonTest {
    private val calculator = LoanCalculator()

    @Test
    fun from_usesLastPaymentDatesNotCurrentIndex() {
        val now = GregorianCalendar(2026, Calendar.SEPTEMBER, 12).time
        val withForecast = result(
            currentIndex = 1,
            paymentCount = 46,
            lastDate = GregorianCalendar(2028, Calendar.JULY, 15).time,
            interest = 200_000.0,
            fees = 1_000.0,
        )
        val withoutForecast = result(
            currentIndex = 70,
            paymentCount = 80,
            lastDate = GregorianCalendar(2029, Calendar.OCTOBER, 15).time,
            interest = 400_000.0,
            fees = 1_000.0,
        )

        val comparison = ForecastComparison.from(
            withForecast = withForecast,
            withoutForecast = withoutForecast,
            loanAmount = 500_000.0,
            fromDate = now,
        )

        assertEquals(22, comparison.remainingMonthsWithForecast)
        assertEquals(37, comparison.remainingMonthsWithoutForecast)
        assertEquals(201_000.0, comparison.overpayWithForecast, 0.001)
        assertEquals(401_000.0, comparison.overpayWithoutForecast, 0.001)
        assertEquals(-200_000.0, comparison.overpayDelta, 0.001)
        assertEquals(-49.875, comparison.overpayPercentDelta, 0.001)
        assertEquals(GregorianCalendar(2028, Calendar.JULY, 15).time, comparison.lastPaymentDateWithForecast)
        assertEquals(GregorianCalendar(2029, Calendar.OCTOBER, 15).time, comparison.lastPaymentDateWithoutForecast)
    }

    @Test
    fun calculate_decreaseTerm_closesEarlierThanExistingExtrasOnly() {
        val comparison = compareForecast(
            extraType = ExtraType.PAYMENT_FOR_DECREASE_TERM,
            extras = listOf(existingExtra()),
        )

        assertTrue(
            "Forecast last date should be earlier than extras-only last date: " +
                "${comparison.remainingMonthsWithForecast} vs ${comparison.remainingMonthsWithoutForecast}",
            comparison.remainingMonthsWithForecast < comparison.remainingMonthsWithoutForecast,
        )
        assertTrue(
            "Forecast should cut overpay: ${comparison.overpayWithForecast} vs ${comparison.overpayWithoutForecast}",
            comparison.overpayWithForecast < comparison.overpayWithoutForecast,
        )
        assertTrue(comparison.overpayPercentDelta < 0.0)
    }

    @Test
    fun calculate_decreaseAmount_closesEarlierThanExistingExtrasOnly() {
        val comparison = compareForecast(
            extraType = ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
            extras = listOf(existingExtra()),
        )

        assertTrue(
            "Forecast last date should be earlier than extras-only last date: " +
                "${comparison.remainingMonthsWithForecast} vs ${comparison.remainingMonthsWithoutForecast}",
            comparison.remainingMonthsWithForecast < comparison.remainingMonthsWithoutForecast,
        )
        assertTrue(
            "Forecast should cut overpay: ${comparison.overpayWithForecast} vs ${comparison.overpayWithoutForecast}",
            comparison.overpayWithForecast < comparison.overpayWithoutForecast,
        )
    }

    @Test
    fun calculate_withoutForecast_keepsExistingExtras() {
        val extras = listOf(existingExtra())
        val loan = sampleLoan(ExtraType.PAYMENT_FOR_DECREASE_TERM)
        val withoutExtras = calculator.calculate(loan.copy(isForecastActive = false), emptyList())
        val withExistingExtras = calculator.calculate(loan.copy(isForecastActive = false), extras)

        val lastWithoutExtras = withoutExtras.payments.last().date
        val lastWithExtras = withExistingExtras.payments.last().date

        assertTrue(
            "Existing extras must shorten the no-forecast schedule: $lastWithExtras vs $lastWithoutExtras",
            lastWithExtras.before(lastWithoutExtras),
        )
    }

    private fun compareForecast(
        extraType: ExtraType,
        extras: List<Extra>,
    ): ForecastComparison {
        val loan = sampleLoan(extraType)
        val withForecast = calculator.calculate(loan, extras)
        val withoutForecast = calculator.calculate(loan.copy(isForecastActive = false), extras)
        return ForecastComparison.from(
            withForecast,
            withoutForecast,
            loan.amount.toDouble(),
            fromDate = GregorianCalendar(2026, Calendar.SEPTEMBER, 12).time,
        )
    }

    private fun sampleLoan(extraType: ExtraType): Loan {
        val firstPayment = GregorianCalendar(2024, Calendar.JANUARY, 15).time
        return Loan(
            id = 1,
            title = "Forecast",
            amount = 500_000f,
            rate = 20f,
            term = 102,
            type = LoanType.ANNUITY,
            firstPaymentDate = firstPayment,
            isForecastActive = true,
            forecastMonthlyPayment = 30_000f,
            forecastDaysBefore = 0,
            forecastStartDate = GregorianCalendar(2026, Calendar.SEPTEMBER, 12).time,
            forecastExtraType = extraType,
        )
    }

    private fun existingExtra(): Extra {
        return Extra(
            amount = 50_000f,
            type = ExtraType.PAYMENT_FOR_DECREASE_TERM,
            date = GregorianCalendar(2024, Calendar.JUNE, 10).time,
            loanId = 1,
        )
    }

    private fun result(
        currentIndex: Int,
        paymentCount: Int,
        lastDate: Date,
        interest: Double,
        fees: Double,
    ): LoanCalculationResult {
        return LoanCalculationResult(
            loanId = 1,
            loanTitle = "",
            currentPayment = 0.0,
            currentPaymentDate = null,
            currentPaymentIndex = currentIndex,
            savedMoney = 0.0,
            owingAmount = 0.0,
            alreadyPaidPrincipal = 0.0,
            alreadyPaidInterest = 0.0,
            totalInterest = interest,
            fees = fees,
            insurance = 0.0,
            totalExtras = 0.0,
            payments = List(paymentCount) { index ->
                PaymentSummary(
                    index = index + 1,
                    date = if (index == paymentCount - 1) lastDate else Date(0),
                    total = 0.0,
                    principal = 0.0,
                    interest = 0.0,
                    endBalance = 0.0,
                    extras = 0.0,
                    rateExtra = 0.0,
                    isCurrent = false,
                )
            },
        )
    }
}
