package com.example.loancalcandroid.ui.home.mapper

import com.example.loancalcandroid.ui.home.model.AllLoanPaymentRowUiModel
import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.calculation.LoanCalculationResult
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.effectiveIssueDate
import java.util.Calendar
import java.util.Date
import kotlin.math.max

object LoanPresentationMapper {

    fun toCard(loan: Loan, calculation: LoanCalculationResult? = null): LoanCardUiModel {
        val monthsPaid = calculation?.currentPaymentIndex?.minus(1)?.coerceAtLeast(0)
            ?: estimateMonthsPaid(loan)
        return LoanCardUiModel(
            id = loan.id,
            title = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
            amount = Formatters.money(loan.amount),
            rate = Formatters.percent(loan.rate),
            issueDate = Formatters.cardDate(loan.effectiveIssueDate()),
            monthsPaid = monthsPaid.coerceAtMost(loan.term),
            termMonths = loan.term,
            firstPaymentDay = dayOfMonth(loan.firstPaymentDate),
        )
    }

    fun toAllLoansSummary(
        loans: List<Loan>,
        calculations: Map<Long, LoanCalculationResult> = emptyMap(),
    ): AllLoansSummaryUiModel {
        val totalAmount = loans.sumOf { it.amount.toDouble() }
        val totalDebt = loans.sumOf { loan ->
            calculations[loan.id]?.owingAmount ?: 0.0
        }
        val paymentsThisMonth = loans.sumOf { loan ->
            calculations[loan.id]?.currentPayment ?: 0.0
        }
        val loanPayments = loans.map { loan ->
            val calculation = calculations[loan.id]
            AllLoanPaymentRowUiModel(
                loanId = loan.id,
                title = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
                nextPaymentDate = Formatters.date(calculation?.currentPaymentDate),
                nextPaymentAmount = calculation?.let { Formatters.money(it.currentPayment) } ?: "—",
            )
        }

        return AllLoansSummaryUiModel(
            loansCount = loans.size,
            totalAmount = Formatters.money(totalAmount),
            totalDebt = Formatters.money(totalDebt),
            paymentsThisMonth = Formatters.money(paymentsThisMonth),
            loanPayments = loanPayments,
        )
    }

    fun toDetails(
        loan: Loan,
        extras: List<Extra>,
        calculation: LoanCalculationResult,
    ): LoanDetailsUiModel {
        val paidPrincipal = calculation.alreadyPaidPrincipal
        val debt = calculation.owingAmount
        val totalPrincipal = paidPrincipal + debt
        val interestPaid = calculation.alreadyPaidInterest
        val totalInterest = calculation.totalInterest
        val remainingInterest = max(0.0, totalInterest - interestPaid)
        val earlyExtras = extras.count {
            it.type != ExtraType.FEE && it.type != ExtraType.INSURANCE
        }

        val fees = calculation.fees
        val insurance = calculation.insurance
        val overpay = calculation.totalInterest + fees + insurance
        val totalToPay = loan.amount + overpay

        return LoanDetailsUiModel(
            loanId = loan.id,
            title = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
            paidAmount = Formatters.money(paidPrincipal),
            debtAmount = Formatters.money(debt),
            paidFraction = if (totalPrincipal > 0) (paidPrincipal / totalPrincipal).toFloat() else 0f,
            currentPayment = Formatters.money(calculation.currentPayment),
            paymentDueDate = Formatters.date(calculation.currentPaymentDate),
            interestPaid = Formatters.money(interestPaid),
            remainingToPay = Formatters.money(remainingInterest),
            totalInterest = Formatters.money(totalInterest),
            totalCommission = Formatters.money(fees),
            totalInsurance = Formatters.money(insurance),
            totalOverpay = Formatters.money(overpay),
            totalToPay = Formatters.money(totalToPay),
            extrasSavings = Formatters.money(calculation.savedMoney),
            extrasCount = earlyExtras,
            forecastEnabled = loan.isForecastActive,
        )
    }

    private fun dayOfMonth(date: Date?): Int {
        if (date == null) return 1
        return Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH)
    }

    private fun estimateMonthsPaid(loan: Loan): Int {
        val start = loan.effectiveIssueDate() ?: return 0
        val diffDays = ((Date().time - start.time) / (24 * 60 * 60 * 1000)).toInt()
        return max(0, diffDays / 30)
    }
}
