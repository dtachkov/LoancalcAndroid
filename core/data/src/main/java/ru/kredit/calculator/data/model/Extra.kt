package ru.kredit.calculator.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Extra(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("amount")
    val amount: Float = 0f,
    @SerializedName("document_number")
    val documentNumber: String? = null,
    @SerializedName("type")
    val type: ExtraType = ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
    @SerializedName("date")
    val date: Date? = null,
    @SerializedName("loan_id")
    val loanId: Long = 0,
)

enum class ExtraType(val id: Int) {
    PAYMENT_FOR_DECREASE_LOAN_AMOUNT(0),
    CHANGE_RATE(1),
    PAYMENT_FOR_DECREASE_TERM(2),
    INSURANCE(3),
    FEE(4),
    PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY(5),
    PAYMENT_FOR_DECREASE_TERM_MONTHLY(6),
    PAYMENT_FOR_CHANGE_DATE(8),
    ;

    companion object {
        fun fromInt(id: Int): ExtraType {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Unknown extra type: $id")
        }

        fun siblingTypes(type: ExtraType): List<Int> {
            val extras = listOf(
                PAYMENT_FOR_DECREASE_LOAN_AMOUNT.id,
                CHANGE_RATE.id,
                PAYMENT_FOR_DECREASE_TERM.id,
                PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY.id,
                PAYMENT_FOR_DECREASE_TERM_MONTHLY.id,
                PAYMENT_FOR_CHANGE_DATE.id,
            )
            val commissions = listOf(FEE.id, INSURANCE.id)
            return when (type.id) {
                in commissions -> commissions
                in extras -> extras
                else -> throw IllegalArgumentException("There are no siblings for type: $type")
            }
        }
    }

    fun toInt(): Int = id
}
