package ru.kredit.calculator.data.model

import ru.kredit.calculator.data.util.DateFormats
import java.util.Date

/**
 * Effective loan issue date for display: manually set [dateOfIssue]
 * or one month before [firstPaymentDate], matching iOS behaviour.
 */
fun Loan.effectiveIssueDate(): Date? {
    dateOfIssue?.let { return it }
    val firstPayment = firstPaymentDate ?: return null
    return DateFormats.addMonths(firstPayment, -1)
}
