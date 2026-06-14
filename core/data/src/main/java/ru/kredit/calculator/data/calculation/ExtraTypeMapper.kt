package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.Extra
import ru.kredit.calculator.data.model.ExtraType

internal object ExtraTypeMapper {
    fun toCalculatorType(type: ExtraType): Int {
        return when (type) {
            ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
            ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
            -> Extra.BALANCE
            ExtraType.CHANGE_RATE -> Extra.RATE
            ExtraType.PAYMENT_FOR_DECREASE_TERM,
            ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
            -> Extra.TERM
            ExtraType.INSURANCE -> Extra.INSURANCE
            ExtraType.FEE -> Extra.FEE
            ExtraType.PAYMENT_FOR_CHANGE_DATE -> Extra.DATE
        }
    }
}
