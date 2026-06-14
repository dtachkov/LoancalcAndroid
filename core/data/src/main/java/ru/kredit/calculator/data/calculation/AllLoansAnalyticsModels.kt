package ru.kredit.calculator.data.calculation

data class DebtByLoanSlice(
    val loanId: Long,
    val label: String,
    val debt: Double,
)

data class LoanInterestComparison(
    val totalLoanAmount: Double,
    val totalOverpay: Double,
)

data class AnalyticsTimelinePoint(
    val monthKey: Int,
    val axisLabel: String,
    val remainingDebt: Double,
    val cumulativeInterest: Double,
)

data class RepaymentProgressTotals(
    val paidPrincipal: Double,
    val remainingDebt: Double,
    val paidInterest: Double,
    val remainingInterest: Double,
)

data class YearlyDebtLoad(
    val year: Int,
    val principal: Double,
    val interest: Double,
) {
    val total: Double get() = principal + interest
}

data class AllLoansAnalyticsData(
    val debtByLoan: List<DebtByLoanSlice>,
    val totalRemainingDebt: Double,
    val allLoansAmount: Double,
    val allLoansOverpay: Double,
    val loanInterestComparison: LoanInterestComparison,
    val timeline: List<AnalyticsTimelinePoint>,
    val repaymentProgress: RepaymentProgressTotals,
    val yearlyLoad: List<YearlyDebtLoad>,
)
