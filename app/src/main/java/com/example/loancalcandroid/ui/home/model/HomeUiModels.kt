package com.example.loancalcandroid.ui.home.model

data class LoanCardUiModel(
    val id: Long,
    val title: String,
    val amount: String,
    val rate: String,
    val issueDate: String,
    val monthsPaid: Int,
    val termMonths: Int,
    val firstPaymentDay: Int,
)

data class AllLoanPaymentRowUiModel(
    val loanId: Long,
    val title: String,
    val nextPaymentDate: String,
    val nextPaymentAmount: String,
)

data class AllLoansSummaryUiModel(
    val loansCount: Int,
    val totalAmount: String,
    val totalDebt: String,
    val paymentsThisMonth: String,
    val loanPayments: List<AllLoanPaymentRowUiModel> = emptyList(),
)

data class LoanDetailsUiModel(
    val loanId: Long,
    val title: String,
    val paidAmount: String,
    val debtAmount: String,
    val paidFraction: Float,
    val currentPayment: String,
    val paymentDueDate: String,
    val interestPaid: String,
    val remainingToPay: String,
    val totalInterest: String,
    val totalCommission: String,
    val totalInsurance: String,
    val totalOverpay: String,
    val totalToPay: String,
    val extrasSavings: String,
    val extrasCount: Int,
    val forecastEnabled: Boolean,
)
