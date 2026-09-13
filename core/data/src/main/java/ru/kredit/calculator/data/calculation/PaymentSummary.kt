package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.Payment
import java.util.Date

data class PaymentSummary(
    val index: Int,
    val date: Date,
    val total: Double,
    val principal: Double,
    val interest: Double,
    val endBalance: Double,
    val extras: Double,
    val rateExtra: Double,
    val isCurrent: Boolean,
) {
    val hasExtras: Boolean
        get() = extras > EXTRA_EPS || rateExtra > EXTRA_EPS

    companion object {
        const val EXTRA_EPS = 0.001

        fun from(payment: Payment, currentIndex: Int): PaymentSummary {
            return PaymentSummary(
                index = payment.index,
                date = payment.date,
                total = payment.total,
                principal = payment.principal,
                interest = payment.interest,
                endBalance = payment.endBalance,
                extras = payment.extras,
                rateExtra = payment.rateExtra,
                isCurrent = payment.index == currentIndex,
            )
        }
    }
}
