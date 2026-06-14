package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.LoanCalendar
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import java.util.Calendar
import java.util.Date

object ExtraInterestCalculator {
    fun interestForExtraDate(
        loan: Loan,
        payments: List<PaymentSummary>,
        extras: List<Extra>,
        extraDate: Date,
    ): Double {
        if (payments.isEmpty()) return 0.0

        val calendar = createCalendar(loan)
        for (index in payments.indices) {
            val nextPayment = payments[index]
            if (sameDay(nextPayment.date, extraDate) && nextPayment.total != 0.0) {
                return 0.0
            }

            val nextDate = nextPayment.date
            val (oldDate, oldBalance) = previousBalanceDate(loan, payments, index, calendar)
            if (nextDate.after(extraDate) && oldDate.before(extraDate)) {
                val proportion = calendar.daysProportionInYearBetweenDates(oldDate, extraDate, false)
                val rate = rateForDate(loan, extras, extraDate)
                return proportion * oldBalance * rate / 100.0
            }
        }
        return 0.0
    }

    private fun previousBalanceDate(
        loan: Loan,
        payments: List<PaymentSummary>,
        index: Int,
        calendar: LoanCalendar,
    ): Pair<Date, Double> {
        if (index - 1 >= 0) {
            val previous = payments[index - 1]
            if (previous.total != 0.0) {
                return previous.date to previous.endBalance
            }
            if (index - 2 >= 0) {
                val beforePrevious = payments[index - 2]
                return beforePrevious.date to beforePrevious.endBalance
            }
        }

        val firstPaymentDate = loan.firstPaymentDate ?: return Date(0) to loan.amount.toDouble()
        val issueDate = calendar.addMonthsToDate(firstPaymentDate, -1)
        return issueDate to loan.amount.toDouble()
    }

    private fun rateForDate(loan: Loan, extras: List<Extra>, forDate: Date): Double {
        var currentRate = loan.rate.toDouble()
        extras
            .asSequence()
            .filter { it.type == ExtraType.CHANGE_RATE && it.date != null && it.date.before(forDate) }
            .forEach { currentRate = it.amount.toDouble() }
        return currentRate
    }

    private fun createCalendar(loan: Loan): LoanCalendar {
        return LoanCalendar().apply {
            lastDayFlag = loan.payOnLastDayOfMonth
            moveDayOff = loan.considerDaysOff
            extraDayInMonth = if (loan.extraDayInMonth) 1 else 0
        }
    }

    private fun sameDay(first: Date, second: Date): Boolean {
        val left = Calendar.getInstance().apply { time = first }
        val right = Calendar.getInstance().apply { time = second }
        return left.get(Calendar.YEAR) == right.get(Calendar.YEAR) &&
            left.get(Calendar.DAY_OF_YEAR) == right.get(Calendar.DAY_OF_YEAR)
    }
}
