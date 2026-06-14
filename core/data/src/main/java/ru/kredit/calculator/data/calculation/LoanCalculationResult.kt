package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.Payment
import com.zoom.loancalc.Stats
import java.util.Date

data class LoanCalculationResult(
    val loanId: Long,
    val loanTitle: String,
    val currentPayment: Double,
    val currentPaymentDate: Date?,
    val currentPaymentIndex: Int,
    val savedMoney: Double,
    val owingAmount: Double,
    val alreadyPaidPrincipal: Double,
    val alreadyPaidInterest: Double,
    val totalInterest: Double,
    val fees: Double,
    val insurance: Double,
    val totalExtras: Double,
    val payments: List<PaymentSummary>,
) {
    companion object {
        fun from(
            loanId: Long,
            loanTitle: String,
            stats: Stats,
            payments: List<Payment>,
            currentPayment: Double,
            currentPaymentDate: Date?,
            currentPaymentIndex: Int,
            savedMoney: Double,
        ): LoanCalculationResult {
            return LoanCalculationResult(
                loanId = loanId,
                loanTitle = loanTitle,
                currentPayment = currentPayment,
                currentPaymentDate = currentPaymentDate,
                currentPaymentIndex = currentPaymentIndex,
                savedMoney = savedMoney,
                owingAmount = stats.owingAmount,
                alreadyPaidPrincipal = stats.alreadyPaidPrincipal,
                alreadyPaidInterest = stats.alreadyPaidInterest,
                totalInterest = stats.interest,
                fees = stats.fees,
                insurance = stats.insurance,
                totalExtras = stats.extras,
                payments = payments.map { PaymentSummary.from(it, currentPaymentIndex) },
            )
        }
    }
}
