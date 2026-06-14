package com.example.loancalcandroid.ui.schedule

import ru.kredit.calculator.data.calculation.PaymentSummary
import ru.kredit.calculator.data.model.Loan
import java.util.Calendar
import java.util.Date

enum class ScheduleRowType {
    PAYMENT,
    EXTRA,
    CHANGE_RATE,
}

data class ScheduleRow(
    val listIndex: Int,
    val type: ScheduleRowType,
    val displayNumber: String,
    val date: Date,
    val total: Double,
    val principal: Double,
    val interest: Double,
    val endBalance: Double,
    val extraAmount: Double,
    val rateExtra: Double,
    val isOdd: Boolean,
    val isCurrent: Boolean,
    val isNewYear: Boolean,
    val paymentIndex: Int,
)

data class ScheduleSummary(
    val paidPrincipal: Double,
    val loanAmount: Double,
    val totalExtras: Double,
    val forecastLabel: String?,
)

object ScheduleRowsGenerator {
    fun generate(
        loan: Loan,
        payments: List<PaymentSummary>,
        currentPaymentIndex: Int,
    ): List<ScheduleRow> {
        val rows = mutableListOf<ScheduleRow>()
        var numberCorrection = 0
        var odd = false
        var prevYear = 0
        var listIndex = 0
        var rowNumber = 0

        for (payment in payments) {
            rowNumber++
            odd = !odd
            val calendar = Calendar.getInstance().apply { time = payment.date }
            val year = calendar.get(Calendar.YEAR)
            val isNewYear = year != prevYear
            prevYear = year

            val hasRateChange = payment.rateExtra > 0.001
            val hasExtraAmount = payment.extras > 0.001
            val hasExtras = hasExtraAmount || hasRateChange

            val type: ScheduleRowType
            val displayNumber: String

            if (hasExtras) {
                if (hasRateChange) {
                    type = ScheduleRowType.CHANGE_RATE
                    numberCorrection++
                } else {
                    type = ScheduleRowType.EXTRA
                    if (!loan.applyExtrasImmediately) {
                        numberCorrection++
                    }
                }
                displayNumber = "+"
            } else {
                type = ScheduleRowType.PAYMENT
                displayNumber = (rowNumber - numberCorrection).toString()
            }

            rows += ScheduleRow(
                listIndex = listIndex,
                type = type,
                displayNumber = displayNumber,
                date = payment.date,
                total = payment.total,
                principal = payment.principal,
                interest = payment.interest,
                endBalance = payment.endBalance,
                extraAmount = payment.extras,
                rateExtra = payment.rateExtra,
                isOdd = odd,
                isCurrent = payment.index == currentPaymentIndex,
                isNewYear = isNewYear,
                paymentIndex = payment.index,
            )
            listIndex++
        }

        return rows
    }

    fun currentRowIndex(rows: List<ScheduleRow>): Int {
        return rows.indexOfFirst { it.isCurrent }.coerceAtLeast(0)
    }

    fun visibleRows(rows: List<ScheduleRow>, showPreviousPayments: Boolean): List<ScheduleRow> {
        if (showPreviousPayments || rows.isEmpty()) return rows
        val start = currentRowIndex(rows)
        return rows.drop(start)
    }
}

fun Date.isWeekend(): Boolean {
    val day = Calendar.getInstance().apply { time = this@isWeekend }.get(Calendar.DAY_OF_WEEK)
    return day == Calendar.SATURDAY || day == Calendar.SUNDAY
}
