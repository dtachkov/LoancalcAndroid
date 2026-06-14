package com.example.loancalcandroid.ui.home.mapper

import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

object LoanPresentationMapper {

    fun toCard(loan: Loan, now: Date = Date()): LoanCardUiModel {
        val monthsPaid = monthsBetween(loan.firstPaymentDate ?: loan.dateOfIssue, now)
        return LoanCardUiModel(
            id = loan.id,
            title = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
            amount = Formatters.money(loan.amount),
            rate = Formatters.percent(loan.rate),
            issueDate = Formatters.date(loan.dateOfIssue ?: loan.firstPaymentDate),
            monthsPaid = min(monthsPaid, loan.term),
            termMonths = loan.term,
        )
    }

    fun toAllLoansSummary(loans: List<Loan>): AllLoansSummaryUiModel {
        val totalAmount = loans.sumOf { it.amount.toDouble() }
        val totalDebt = loans.sumOf { estimateDebt(it).toDouble() }
        val nearest = loans
            .mapNotNull { loan ->
                val paymentDate = nextPaymentDate(loan)
                val payment = effectiveMonthlyPayment(loan)
                if (paymentDate != null) paymentDate to payment else null
            }
            .minByOrNull { it.first.time }

        return AllLoansSummaryUiModel(
            loansCount = loans.size,
            totalAmount = Formatters.money(totalAmount),
            totalDebt = Formatters.money(totalDebt),
            nearestPaymentDate = nearest?.first?.let(Formatters::shortDate),
            nearestPaymentAmount = nearest?.second?.let(Formatters::money),
        )
    }

    fun toDetails(loan: Loan, extras: List<Extra>, now: Date = Date()): LoanDetailsUiModel {
        val monthsPaid = min(monthsBetween(loan.firstPaymentDate ?: loan.dateOfIssue, now), loan.term)
        val monthlyPayment = effectiveMonthlyPayment(loan)
        val paidPrincipal = min(loan.amount, monthlyPayment * monthsPaid * 0.55f)
        val debt = max(0f, loan.amount - paidPrincipal)
        val totalInterest = estimateTotalInterest(loan)
        val interestPaid = totalInterest * (monthsPaid.toFloat() / max(loan.term, 1))
        val remaining = debt + (totalInterest - interestPaid)
        val commission = extras.filter { it.type == ExtraType.FEE }.sumOf { it.amount.toDouble() }
        val insurance = extras.filter { it.type == ExtraType.INSURANCE }.sumOf { it.amount.toDouble() }
        val earlyExtras = extras.count {
            it.type != ExtraType.FEE && it.type != ExtraType.INSURANCE
        }

        return LoanDetailsUiModel(
            loanId = loan.id,
            title = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
            paidAmount = Formatters.money(paidPrincipal),
            debtAmount = Formatters.money(debt),
            paidFraction = if (loan.amount > 0f) paidPrincipal / loan.amount else 0f,
            currentPayment = Formatters.money(monthlyPayment),
            paymentDueDate = Formatters.date(nextPaymentDate(loan)),
            interestPaid = Formatters.money(interestPaid),
            remainingToPay = Formatters.money(remaining),
            totalInterest = Formatters.money(totalInterest),
            totalCommission = Formatters.money(commission),
            totalInsurance = Formatters.money(insurance),
            extrasSavings = Formatters.money(estimateExtrasSavings(extras)),
            extrasCount = earlyExtras,
            forecastEnabled = loan.isForecastActive,
        )
    }

    private fun estimateDebt(loan: Loan): Float {
        val monthsPaid = monthsBetween(loan.firstPaymentDate ?: loan.dateOfIssue, Date())
        val monthlyPayment = effectiveMonthlyPayment(loan)
        val paidPrincipal = min(loan.amount, monthlyPayment * monthsPaid * 0.55f)
        return max(0f, loan.amount - paidPrincipal)
    }

    private fun effectiveMonthlyPayment(loan: Loan): Float {
        if (loan.monthlyPayment > 0f) return loan.monthlyPayment
        val monthlyRate = loan.rate / 100f / 12f
        if (monthlyRate <= 0f) return loan.amount / max(loan.term, 1)
        val factor = Math.pow((1 + monthlyRate).toDouble(), loan.term.toDouble()).toFloat()
        return loan.amount * monthlyRate * factor / (factor - 1f)
    }

    private fun estimateTotalInterest(loan: Loan): Float {
        val monthly = effectiveMonthlyPayment(loan)
        return max(0f, monthly * loan.term - loan.amount)
    }

    private fun estimateExtrasSavings(extras: List<Extra>): Float {
        return extras
            .filter { it.type != ExtraType.FEE && it.type != ExtraType.INSURANCE }
            .sumOf { (it.amount * 0.3f).toDouble() }
            .toFloat()
    }

    private fun monthsBetween(start: Date?, end: Date): Int {
        if (start == null) return 0
        val diff = end.time - start.time
        return max(0, TimeUnit.MILLISECONDS.toDays(diff).toInt() / 30)
    }

    private fun nextPaymentDate(loan: Loan): Date? {
        val base = loan.firstPaymentDate ?: loan.dateOfIssue ?: return null
        val calendar = Calendar.getInstance()
        calendar.time = base
        val monthsPaid = monthsBetween(base, Date())
        calendar.add(Calendar.MONTH, monthsPaid + 1)
        return calendar.time
    }
}
