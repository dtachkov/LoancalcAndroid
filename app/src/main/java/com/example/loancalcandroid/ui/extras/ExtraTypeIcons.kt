package com.example.loancalcandroid.ui.extras

import androidx.annotation.DrawableRes
import com.example.loancalcandroid.R
import ru.kredit.calculator.data.model.ExtraType

object ExtraTypeIcons {
    @DrawableRes
    fun icon(type: ExtraType): Int = when (type) {
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
        -> R.drawable.ic_amount

        ExtraType.CHANGE_RATE -> R.drawable.ic_percent

        ExtraType.PAYMENT_FOR_DECREASE_TERM,
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
        -> R.drawable.ic_amount_decrease

        ExtraType.INSURANCE -> R.drawable.ic_insurance
        ExtraType.FEE -> R.drawable.ic_fee
        ExtraType.PAYMENT_FOR_CHANGE_DATE -> R.drawable.ic_extra_date
    }
}
