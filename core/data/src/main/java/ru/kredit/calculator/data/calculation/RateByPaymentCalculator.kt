package ru.kredit.calculator.data.calculation

import kotlin.math.pow

object RateByPaymentCalculator {
    fun findMonthlyRate(
        principal: Double,
        payment: Double,
        months: Int,
    ): Double? {
        if (principal <= 0.0 || payment <= 0.0 || months <= 0) return null

        val minPayment = principal / months
        if (payment < minPayment) return null
        if (payment == minPayment) return 0.0

        var low = 0.0
        var high = 1.0

        while (annuityPayment(principal, high, months) < payment && high < 10.0) {
            high *= 2.0
        }

        repeat(100) {
            val rate = (low + high) / 2.0
            if (annuityPayment(principal, rate, months) < payment) {
                low = rate
            } else {
                high = rate
            }
        }

        return (low + high) / 2.0
    }

    fun annualPercent(
        principal: Double,
        payment: Double,
        months: Int,
    ): Double? {
        val monthlyRate = findMonthlyRate(principal, payment, months) ?: return null
        return monthlyRate * 12.0 * 100.0
    }

    private fun annuityPayment(
        principal: Double,
        monthlyRate: Double,
        months: Int,
    ): Double {
        if (monthlyRate == 0.0) return principal / months
        val factor = (1.0 + monthlyRate).pow(months)
        return principal * monthlyRate * factor / (factor - 1.0)
    }
}
