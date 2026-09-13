package ru.kredit.calculator.data.calculation

import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.Loan
import java.util.Calendar
import java.util.Date

data class FullRepaymentResult(
    val totalAmount: Double,
    val includedAmount: Double,
    val remainingDebt: Double,
    val includesInterest: Boolean,
    val scheduledPaymentDate: Date? = null,
) {
    val extraPrefillAmount: Double
        get() {
            val raw = if (includesInterest) totalAmount else remainingDebt
            return ceilToCents(raw)
        }
}

object FullRepaymentCalculator {
    fun calculate(
        loan: Loan,
        payments: List<PaymentSummary>,
        extras: List<Extra>,
        plannedDate: Date,
    ): FullRepaymentResult? {
        return if (loan.applyExtrasImmediately) {
            calculateWithAccruedInterest(loan, payments, extras, plannedDate)
        } else {
            calculateFromNextPlannedPayment(payments, plannedDate)
        }
    }

    private fun calculateFromNextPlannedPayment(
        payments: List<PaymentSummary>,
        plannedDate: Date,
    ): FullRepaymentResult? {
        val plannedDay = startOfDay(plannedDate)
        val payment = payments
            .asSequence()
            .filter { !it.hasExtras }
            .sortedBy { it.date.time }
            .firstOrNull { !startOfDay(it.date).before(plannedDay) }
            ?: return null
        return FullRepaymentResult(
            totalAmount = payment.total + payment.endBalance,
            includedAmount = payment.total,
            remainingDebt = payment.endBalance,
            includesInterest = false,
            scheduledPaymentDate = payment.date,
        )
    }

    private fun calculateWithAccruedInterest(
        loan: Loan,
        payments: List<PaymentSummary>,
        extras: List<Extra>,
        plannedDate: Date,
    ): FullRepaymentResult {
        val plannedDay = startOfDay(plannedDate)
        val remainingDebt = payments
            .filter { startOfDay(it.date).before(plannedDay) }
            .maxByOrNull { it.date.time }
            ?.endBalance
            ?: loan.amount.toDouble()
        val interest = ExtraInterestCalculator.interestForExtraDate(
            loan = loan,
            payments = payments,
            extras = extras,
            extraDate = plannedDate,
        )
        return FullRepaymentResult(
            totalAmount = interest + remainingDebt,
            includedAmount = interest,
            remainingDebt = remainingDebt,
            includesInterest = true,
        )
    }

    private fun startOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}

internal fun ceilToCents(amount: Double): Double {
    return kotlin.math.ceil(amount * 100.0 - 1e-9) / 100.0
}
