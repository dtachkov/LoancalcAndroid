package ru.kredit.calculator.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Loan(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("creation_date")
    val creationDate: Date = Date(),
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("amount")
    val amount: Float = 0f,
    @SerializedName("rate")
    val rate: Float = 0f,
    @SerializedName("term")
    val term: Int = 0,
    @SerializedName("type")
    val type: LoanType = LoanType.ANNUITY,
    @SerializedName("first_payment_date")
    val firstPaymentDate: Date? = null,
    @SerializedName("monthly_payment")
    val monthlyPayment: Float = 0f,
    @SerializedName("date_of_issue")
    val dateOfIssue: Date? = null,
    @SerializedName("consider_days_off")
    val considerDaysOff: Boolean = false,
    @SerializedName("pay_on_last_day_of_month")
    val payOnLastDayOfMonth: Boolean = false,
    @SerializedName("apply_extras_immediately")
    val applyExtrasImmediately: Boolean = false,
    @SerializedName("interest_only_after_principal_paid_by_extra")
    val calculateExtrasByBalanceLikeSberbank: Boolean = true,
    @SerializedName("ignore_passed_periods_after_rate_change")
    val ignorePassedPeriodsAfterRateChange: Boolean = false,
    @SerializedName("extra_day_in_month")
    val extraDayInMonth: Boolean = false,
    @SerializedName("is_forecast_active")
    val isForecastActive: Boolean = false,
    @SerializedName("forecast_montly_pay")
    val forecastMonthlyPayment: Float = 0f,
    @SerializedName("forecast_days_before")
    val forecastDaysBefore: Int = 0,
    @SerializedName("forecast_start_date")
    val forecastStartDate: Date? = null,
    @SerializedName("forecast_extra_type")
    val forecastExtraType: ExtraType = ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
) {
    fun validate(): Boolean {
        return kotlin.math.abs(amount) > 0.001f &&
            kotlin.math.abs(rate) > 0.001f &&
            term > 0 &&
            firstPaymentDate != null
    }
}

enum class LoanType(val id: Int) {
    @SerializedName("0")
    ANNUITY(0),

    @SerializedName("1")
    GRADE(1),
    ;

    companion object {
        fun fromInt(id: Int): LoanType {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Unknown loan type: $id")
        }
    }

    fun toInt(): Int = id
}
