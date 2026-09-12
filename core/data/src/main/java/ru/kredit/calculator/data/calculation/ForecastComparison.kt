package ru.kredit.calculator.data.calculation

import java.util.Calendar
import java.util.Date

data class ForecastComparison(
    val remainingMonthsWithForecast: Int,
    val remainingMonthsWithoutForecast: Int,
    val overpayWithForecast: Double,
    val overpayWithoutForecast: Double,
    val totalWithForecast: Double,
    val totalWithoutForecast: Double,
    val lastPaymentDateWithForecast: Date?,
    val lastPaymentDateWithoutForecast: Date?,
) {
    val overpayDelta: Double get() = overpayWithForecast - overpayWithoutForecast
    val interestSaved: Double get() = overpayWithoutForecast - overpayWithForecast
    val totalDelta: Double get() = totalWithForecast - totalWithoutForecast
    val overpayPercentDelta: Double
        get() = if (overpayWithoutForecast > 0.0) {
            overpayDelta / overpayWithoutForecast * 100.0
        } else {
            0.0
        }

    companion object {
        fun from(
            withForecast: LoanCalculationResult,
            withoutForecast: LoanCalculationResult,
            loanAmount: Double,
            fromDate: Date = Date(),
        ): ForecastComparison {
            val overpayWith = overpay(withForecast)
            val overpayWithout = overpay(withoutForecast)
            return ForecastComparison(
                remainingMonthsWithForecast = monthsUntilLastPayment(withForecast, fromDate),
                remainingMonthsWithoutForecast = monthsUntilLastPayment(withoutForecast, fromDate),
                overpayWithForecast = overpayWith,
                overpayWithoutForecast = overpayWithout,
                totalWithForecast = loanAmount + overpayWith,
                totalWithoutForecast = loanAmount + overpayWithout,
                lastPaymentDateWithForecast = withForecast.payments.lastOrNull()?.date,
                lastPaymentDateWithoutForecast = withoutForecast.payments.lastOrNull()?.date,
            )
        }

        private fun overpay(result: LoanCalculationResult): Double {
            return result.totalInterest + result.fees + result.insurance
        }

        private fun monthsUntilLastPayment(result: LoanCalculationResult, fromDate: Date): Int {
            val lastPaymentDate = result.payments.lastOrNull()?.date ?: return 0
            return monthsBetween(fromDate, lastPaymentDate)
        }

        fun monthsBetween(from: Date, to: Date): Int {
            val start = calendarDay(from)
            val end = calendarDay(to)
            var months = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 +
                (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))
            if (end.get(Calendar.DAY_OF_MONTH) < start.get(Calendar.DAY_OF_MONTH)) {
                months--
            }
            return months.coerceAtLeast(0)
        }

        private fun calendarDay(date: Date): Calendar {
            return Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }
}
