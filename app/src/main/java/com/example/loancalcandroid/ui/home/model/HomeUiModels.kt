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

data class AllLoansSummaryUiModel(
    val loansCount: Int,
    val totalAmount: String,
    val totalDebt: String,
    val nearestPaymentDate: String?,
    val nearestPaymentAmount: String?,
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
    val extrasSavings: String,
    val extrasCount: Int,
    val forecastEnabled: Boolean,
)
