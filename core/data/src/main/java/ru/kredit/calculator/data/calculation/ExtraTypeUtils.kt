package ru.kredit.calculator.data.calculation

import ru.kredit.calculator.data.model.ExtraType

object ExtraTypeUtils {
    val earlyPaymentTypes: List<ExtraType> = listOf(
        ExtraType.CHANGE_RATE,
        ExtraType.PAYMENT_FOR_DECREASE_TERM,
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
        ExtraType.PAYMENT_FOR_CHANGE_DATE,
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
    )

    val commissionTypes: List<ExtraType> = listOf(
        ExtraType.INSURANCE,
        ExtraType.FEE,
    )

    fun toCalculatorType(type: ExtraType): Int = ExtraTypeMapper.toCalculatorType(type)

    fun label(type: ExtraType): String = when (type) {
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT -> "Уменьшение суммы"
        ExtraType.CHANGE_RATE -> "Изменение ставки"
        ExtraType.PAYMENT_FOR_DECREASE_TERM -> "Уменьшение срока"
        ExtraType.INSURANCE -> "Страховка"
        ExtraType.FEE -> "Комиссия"
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY -> "Ежемес. уменьшение суммы"
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY -> "Ежемес. уменьшение срока"
        ExtraType.PAYMENT_FOR_CHANGE_DATE -> "Изменение даты платежа"
    }

    fun siblingTypeIds(type: ExtraType): List<Int> = ExtraType.siblingTypes(type)

    fun isEarlyPayment(type: ExtraType): Boolean = type in earlyPaymentTypes

    fun isCommission(type: ExtraType): Boolean = type in commissionTypes
}
