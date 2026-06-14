package ru.kredit.calculator.data.calculation

data class TaxPaymentRow(
    val year: Int,
    val interestPayment: Double,
    val returnTax: Double,
)

data class TaxCalculationResult(
    val principalTax: Double,
    val interestTax: Double,
    val totalReturnTax: Double,
    val taxPayments: List<TaxPaymentRow>,
)
