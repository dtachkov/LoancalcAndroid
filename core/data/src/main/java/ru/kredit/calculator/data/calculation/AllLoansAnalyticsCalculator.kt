package ru.kredit.calculator.data.calculation

import java.util.Calendar
import java.util.Date
import kotlin.coroutines.coroutineContext
import kotlin.math.round
import kotlinx.coroutines.ensureActive
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanFull

/**
 * Port of iOS [AnalyticsViewController] aggregation logic.
 */
class AllLoansAnalyticsCalculator(
    private val loanCalculator: LoanCalculator,
) {
    suspend fun calculate(
        loanFulls: List<LoanFull>,
        onLoanProgress: suspend (index: Int, total: Int) -> Unit = { _, _ -> },
    ): AllLoansAnalyticsData? {
        if (loanFulls.isEmpty()) return null

        val calculations = LinkedHashMap<Long, LoanCalculationResult>()
        loanFulls.forEachIndexed { index, loanFull ->
            coroutineContext.ensureActive()
            val loan = loanFull.loan
            if (!loan.validate()) return@forEachIndexed
            runCatching {
                loanCalculator.calculate(loan, loanFull.extras)
            }.onSuccess { calculations[loan.id] = it }
            onLoanProgress(index + 1, loanFulls.size)
        }

        if (calculations.isEmpty()) return null

        val loans = loanFulls
            .map { it.loan }
            .filter { calculations.containsKey(it.id) }
            .sortedBy { it.firstPaymentDate?.time ?: Long.MAX_VALUE }

        val totals = computeTotals(loans, calculations)
        val debtByLoan = buildDebtByLoan(loans, calculations)
        val comparison = LoanInterestComparison(
            totalLoanAmount = totals.allLoansAmount,
            totalOverpay = totals.allLoansOverpay,
        )

        return AllLoansAnalyticsData(
            debtByLoan = debtByLoan,
            totalRemainingDebt = totals.leftTotal,
            allLoansAmount = totals.allLoansAmount,
            allLoansOverpay = totals.allLoansOverpay,
            loanInterestComparison = comparison,
            timeline = buildTimeline(loans, calculations),
            repaymentProgress = RepaymentProgressTotals(
                paidPrincipal = totals.allLoansAmount - totals.leftTotal,
                remainingDebt = totals.leftTotal,
                paidInterest = totals.paidInterest,
                remainingInterest = totals.leftInterest,
            ),
            yearlyLoad = buildYearlyLoad(calculations),
        )
    }

    private data class AnalyticsTotals(
        val allLoansAmount: Double,
        val allLoansOverpay: Double,
        val leftTotal: Double,
        val leftInterest: Double,
        val paidInterest: Double,
    )

    private fun computeTotals(
        loans: List<Loan>,
        calculations: Map<Long, LoanCalculationResult>,
    ): AnalyticsTotals {
        var allLoansAmount = 0.0
        var allLoansOverpay = 0.0
        var leftTotal = 0.0
        var leftInterest = 0.0
        var paidInterest = 0.0

        loans.forEach { loan ->
            val calculation = calculations[loan.id] ?: return@forEach
            val amount = loan.amount.toDouble()
            allLoansAmount += amount
            allLoansOverpay += totalOverpay(calculation)
            leftTotal += amount - calculation.alreadyPaidPrincipal
            leftInterest += interestLeft(calculation)
            paidInterest += calculation.alreadyPaidInterest
        }

        return AnalyticsTotals(
            allLoansAmount = allLoansAmount,
            allLoansOverpay = round2(allLoansOverpay),
            leftTotal = leftTotal,
            leftInterest = leftInterest,
            paidInterest = paidInterest,
        )
    }

    private fun buildDebtByLoan(
        loans: List<Loan>,
        calculations: Map<Long, LoanCalculationResult>,
    ): List<DebtByLoanSlice> {
        return loans.mapNotNull { loan ->
            val calculation = calculations[loan.id] ?: return@mapNotNull null
            val leftAmount = loan.amount.toDouble() - calculation.alreadyPaidPrincipal
            if (leftAmount <= 0.001) return@mapNotNull null
            DebtByLoanSlice(
                loanId = loan.id,
                label = loan.title.orEmpty().ifBlank { "Кредит #${loan.id}" },
                debt = round(leftAmount),
            )
        }
    }

    private fun buildTimeline(
        loans: List<Loan>,
        calculations: Map<Long, LoanCalculationResult>,
    ): List<AnalyticsTimelinePoint> {
        val loanMonthsByLoan = mutableMapOf<Long, List<MonthLoanData>>()

        loans.forEach { loan ->
            val calculation = calculations[loan.id] ?: return@forEach
            val monthKeys = calculation.payments
                .map { monthKey(it.date) }
                .toSortedSet()

            val loanMonths = monthKeys.map { key ->
                val paymentsInMonth = calculation.payments.filter { monthKey(it.date) == key }
                val balances = paymentsInMonth.map { it.endBalance }
                val interests = paymentsInMonth.map { it.interest }
                MonthLoanData(
                    monthKey = key,
                    minEndBalance = balances.minOrNull() ?: 0.0,
                    interestSum = interests.sum(),
                )
            }
            loanMonthsByLoan[loan.id] = loanMonths
        }

        val paymentMonthKeys = loanMonthsByLoan.values
            .flatMap { months -> months.map { it.monthKey } }
            .toSortedSet()
        if (paymentMonthKeys.isEmpty()) return emptyList()

        val minKey = paymentMonthKeys.first()
        val maxKey = paymentMonthKeys.last()
        var cumulativeInterest = 0.0

        return (minKey..maxKey).map { key ->
            var totalDebt = 0.0
            var monthInterest = 0.0

            loans.forEach { loan ->
                val loanMonths = loanMonthsByLoan[loan.id] ?: return@forEach
                val balanceAtMonth = loanMonths
                    .filter { it.monthKey <= key }
                    .maxByOrNull { it.monthKey }
                    ?.minEndBalance
                if (balanceAtMonth != null) {
                    totalDebt += balanceAtMonth
                }
                monthInterest += loanMonths
                    .filter { it.monthKey == key }
                    .sumOf { it.interestSum }
            }

            cumulativeInterest += monthInterest

            AnalyticsTimelinePoint(
                monthKey = key,
                axisLabel = monthKeyLabel(key),
                remainingDebt = totalDebt,
                cumulativeInterest = cumulativeInterest,
            )
        }
    }

    private fun buildYearlyLoad(
        calculations: Map<Long, LoanCalculationResult>,
    ): List<YearlyDebtLoad> {
        val calendar = Calendar.getInstance()
        val totals = mutableMapOf<Int, Pair<Double, Double>>()

        calculations.values.forEach { calculation ->
            calculation.payments.forEach { payment ->
                calendar.time = payment.date
                val year = calendar.get(Calendar.YEAR)
                val current = totals[year] ?: (0.0 to 0.0)
                totals[year] = (current.first + payment.principal) to (current.second + payment.interest)
            }
        }

        return totals.keys.sorted().map { year ->
            val (principal, interest) = totals.getValue(year)
            YearlyDebtLoad(
                year = year,
                principal = principal,
                interest = interest,
            )
        }
    }

    private fun totalOverpay(calculation: LoanCalculationResult): Double {
        return calculation.totalInterest + calculation.fees + calculation.insurance
    }

    private fun interestLeft(calculation: LoanCalculationResult): Double {
        return calculation.totalInterest - calculation.alreadyPaidInterest
    }

    private fun monthKey(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return month + year * 12
    }

    private fun monthKeyLabel(key: Int): String {
        return "${1 + key % 12}.${key / 12}"
    }

    private fun round2(value: Double): Double {
        return round(value * 100.0) / 100.0
    }

    private data class MonthLoanData(
        val monthKey: Int,
        val minEndBalance: Double,
        val interestSum: Double,
    )
}
