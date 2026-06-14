package com.example.loancalcandroid.ui.home.mapper

import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.calculation.LoanCalculationResult
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
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
            issueDate = Formatters.date(loan.dateOfIssue ?: loan.firstPaymentDate),
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
            calculations[loan.id]?.owingAmount ?: estimateDebt(loan).toDouble()
        }
        val nearest = loans.mapNotNull { loan ->
            val calculation = calculations[loan.id] ?: return@mapNotNull null
            val paymentDate = calculation.currentPaymentDate ?: return@mapNotNull null
            paymentDate to calculation.currentPayment
        }.minByOrNull { it.first.time }

        return AllLoansSummaryUiModel(
            loansCount = loans.size,
            totalAmount = Formatters.money(totalAmount),
            totalDebt = Formatters.money(totalDebt),
            nearestPaymentDate = nearest?.first?.let(Formatters::shortDate),
            nearestPaymentAmount = nearest?.second?.let(Formatters::money),
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
        val remaining = debt + max(0.0, totalInterest - interestPaid)
        val earlyExtras = extras.count {
            it.type != ExtraType.FEE && it.type != ExtraType.INSURANCE
        }

        return LoanDetailsUiModel(
            loanId = loan.id,
            title = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
            paidAmount = Formatters.money(paidPrincipal),
            debtAmount = Formatters.money(debt),
            paidFraction = if (totalPrincipal > 0) (paidPrincipal / totalPrincipal).toFloat() else 0f,
            currentPayment = Formatters.money(calculation.currentPayment),
            paymentDueDate = Formatters.date(calculation.currentPaymentDate),
            interestPaid = Formatters.money(interestPaid),
            remainingToPay = Formatters.money(remaining),
            totalInterest = Formatters.money(totalInterest),
            totalCommission = Formatters.money(calculation.fees),
            totalInsurance = Formatters.money(calculation.insurance),
            extrasSavings = Formatters.money(calculation.savedMoney),
            extrasCount = earlyExtras,
            forecastEnabled = loan.isForecastActive,
        )
    }

    private fun estimateDebt(loan: Loan): Float {
        val monthlyPayment = effectiveMonthlyPayment(loan)
        val monthsPaid = estimateMonthsPaid(loan)
        val paidPrincipal = minOf(loan.amount, monthlyPayment * monthsPaid * 0.55f)
        return max(0f, loan.amount - paidPrincipal)
    }

    private fun effectiveMonthlyPayment(loan: Loan): Float {
        if (loan.monthlyPayment > 0f) return loan.monthlyPayment
        val monthlyRate = loan.rate / 100f / 12f
        if (monthlyRate <= 0f) return loan.amount / max(loan.term, 1)
        val factor = Math.pow((1 + monthlyRate).toDouble(), loan.term.toDouble()).toFloat()
        return loan.amount * monthlyRate * factor / (factor - 1f)
    }

    private fun dayOfMonth(date: Date?): Int {
        if (date == null) return 1
        return Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH)
    }

    private fun estimateMonthsPaid(loan: Loan): Int {
        val start = loan.firstPaymentDate ?: loan.dateOfIssue ?: return 0
        val diffDays = ((Date().time - start.time) / (24 * 60 * 60 * 1000)).toInt()
        return max(0, diffDays / 30)
    }
}
