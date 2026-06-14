package ru.kredit.calculator.data.network

import android.net.Uri
import com.google.gson.annotations.SerializedName
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WebLoanDto(
    val error: String? = null,
    val rate: Float = 0f,
    val sum: Float = 0f,
    val term: Int = 0,
    @SerializedName("paymentType")
    val paymentType: Int = 0,
    @SerializedName("firstpaymentdate")
    val firstPaymentDate: String? = null,
    @SerializedName("issuedate")
    val issueDate: String? = null,
    @SerializedName("flagpercent")
    val flagPercent: Int = 0,
    @SerializedName("lastdayFlag")
    val lastDayFlag: Int = 0,
    @SerializedName("extradayinmonth")
    val extraDayInMonth: Int = 0,
    @SerializedName("interest_only_after_principal_paid_by_extra")
    val interestOnlyAfterPrincipalPaidByExtra: Boolean = true,
    @SerializedName("applyExtrasImmediately")
    val applyExtrasImmediately: Boolean = false,
    @SerializedName("ignorePassedPeriodsAfterRateChange")
    val ignorePassedPeriodsAfterRateChange: Boolean = false,
    @SerializedName("flagdayoff")
    val flagDayOff: Int = 0,
    val extra: List<WebExtraDto>? = null,
)

data class WebExtraDto(
    @SerializedName("extradate")
    val extraDate: String? = null,
    @SerializedName("extratype")
    val extraType: Int = 0,
    @SerializedName("extraamount")
    val extraAmount: Double = 0.0,
)

fun parseLoanIdFromUrl(rawUrl: String): String? {
    return runCatching {
        Uri.parse(rawUrl.trim()).getQueryParameter("loan")?.takeIf { it.isNotBlank() }
    }.getOrNull()
}

fun WebLoanDto.toLoan(title: String): Loan {
    return Loan(
        id = 0,
        title = title,
        amount = sum,
        rate = rate,
        term = term,
        type = LoanType.fromInt(paymentType),
        firstPaymentDate = parseWebDate(firstPaymentDate),
        dateOfIssue = if (flagPercent != 0) parseWebDate(issueDate) else null,
        considerDaysOff = flagDayOff != 0,
        payOnLastDayOfMonth = lastDayFlag != 0,
        applyExtrasImmediately = applyExtrasImmediately,
        calculateExtrasByBalanceLikeSberbank = interestOnlyAfterPrincipalPaidByExtra,
        ignorePassedPeriodsAfterRateChange = ignorePassedPeriodsAfterRateChange,
        extraDayInMonth = extraDayInMonth != 0,
    )
}

fun WebLoanDto.toExtras(): List<Extra> {
    return extra.orEmpty().mapNotNull { item ->
        val type = mapWebExtraType(item.extraType) ?: return@mapNotNull null
        val date = parseWebDate(item.extraDate) ?: return@mapNotNull null
        val amount = when (type) {
            ExtraType.PAYMENT_FOR_CHANGE_DATE -> 0f
            else -> item.extraAmount.toFloat()
        }
        Extra(
            id = 0,
            amount = amount,
            type = type,
            date = date,
        )
    }
}

private fun mapWebExtraType(webType: Int): ExtraType? {
    return when (webType) {
        1 -> ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT
        2 -> ExtraType.PAYMENT_FOR_DECREASE_TERM
        3 -> ExtraType.CHANGE_RATE
        4 -> ExtraType.FEE
        5 -> ExtraType.INSURANCE
        6 -> ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY
        7 -> ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY
        12 -> ExtraType.PAYMENT_FOR_CHANGE_DATE
        else -> null
    }
}

private fun parseWebDate(value: String?): Date? {
    if (value.isNullOrBlank()) return null
    return runCatching {
        WEB_DATE_FORMAT.parse(value)
    }.getOrNull()
}

private val WEB_DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
