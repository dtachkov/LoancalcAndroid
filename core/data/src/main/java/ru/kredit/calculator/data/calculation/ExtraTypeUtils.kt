package ru.kredit.calculator.data.calculation

import ru.kredit.calculator.data.model.ExtraType

object ExtraTypeUtils {
    val earlyPaymentTypes: List<ExtraType> = listOf(
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
        ExtraType.CHANGE_RATE,
        ExtraType.PAYMENT_FOR_DECREASE_TERM,
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
        ExtraType.PAYMENT_FOR_CHANGE_DATE,
    )

    val commissionTypes: List<ExtraType> = listOf(
        ExtraType.INSURANCE,
        ExtraType.FEE,
    )

    fun toCalculatorType(type: ExtraType): Int = ExtraTypeMapper.toCalculatorType(type)

    fun label(type: ExtraType): String = when (type) {
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT -> "Платеж в уменьшение суммы ежемесячного платежа"
        ExtraType.CHANGE_RATE -> "Изменение процентной ставки по кредиту"
        ExtraType.PAYMENT_FOR_DECREASE_TERM -> "Платеж в уменьшение срока кредита"
        ExtraType.INSURANCE -> "Страховка"
        ExtraType.FEE -> "Комиссия"
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY -> "Ежемесячное уменьшение суммы"
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY -> "Ежемесячное уменьшение срока"
        ExtraType.PAYMENT_FOR_CHANGE_DATE -> "Изменение даты ежемесячного платежа"
    }

    fun description(type: ExtraType): String = when (type) {
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT -> """
            Учёт досрочного погашения в дату очередного платежа происходит в следующем платежном периоде. Сумма досрочного погашения вводится без учёта текущего платежа.
            Если вы делали досрочное погашение вместе с очередным платежом, отнимите от суммы вашего платежа текущий аннуитетный платёж — это и будет сумма досрочного погашения.
            Если хотите учёт в дату досрочного погашения, установите соответствующий флаг в параметрах расчёта.
        """.trimIndent()
        ExtraType.CHANGE_RATE -> """
            Изменение ставки не является в полном смысле досрочным погашением. Обычно прописано в кредитном договоре.
            Ставка меняется при получении прав собственности на ипотеку. При этом пересчитывается ежемесячный платёж, срок кредита не меняется.
        """.trimIndent()
        ExtraType.PAYMENT_FOR_DECREASE_TERM -> """
            При уменьшении срока ежемесячный платёж не меняется. Обычно уменьшается срок кредита — количество ежемесячных платежей.
        """.trimIndent()
        ExtraType.INSURANCE -> """
            Данный тип не является досрочным погашением в полной мере. Обычно вы каждый год платите страховку по ипотечному кредиту.
            Введите сумму страхового платежа. Страховки участвуют при расчёте общей переплаты по кредиту.
        """.trimIndent()
        ExtraType.FEE -> """
            Укажите сумму платежей на уплату комиссий и других платежей по кредиту (кроме страховки): комиссия за ведение счёта, выпуск карты и т.п.
        """.trimIndent()
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY -> """
            Введите сумму досрочного погашения, которое будет проводиться ежемесячно с указанной даты до даты последнего платежа.
            Каждый месяц будет добавлено досрочное погашение с типом «Уменьшение суммы».
        """.trimIndent()
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY -> """
            Введите сумму досрочного погашения, которое будет проводиться ежемесячно с указанной даты до даты последнего платежа.
            Каждый месяц будет добавлено досрочное погашение с типом «Уменьшение срока».
        """.trimIndent()
        ExtraType.PAYMENT_FOR_CHANGE_DATE -> """
            Укажите новую дату ежемесячного платежа. Этот тип используется для переноса даты платежа по кредиту.
        """.trimIndent()
    }

    fun siblingTypeIds(type: ExtraType): List<Int> = ExtraType.siblingTypes(type)

    fun isEarlyPayment(type: ExtraType): Boolean = type in earlyPaymentTypes

    fun isCommission(type: ExtraType): Boolean = type in commissionTypes
}
