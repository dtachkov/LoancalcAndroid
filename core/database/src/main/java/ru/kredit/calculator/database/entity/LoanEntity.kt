package ru.kredit.calculator.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kredit.calculator.database.DatabaseContract

@Entity(tableName = DatabaseContract.LoanColumns.TABLE_NAME)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.LoanColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.LoanColumns.CREATION_DATE)
    val creationDate: String? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.TITLE)
    val title: String? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.AMOUNT)
    val amount: Double? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.RATE)
    val rate: Double? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.TERM)
    val term: Int? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.TYPE)
    val type: Int? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FIRST_PAYMENT_DATE)
    val firstPaymentDate: String? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.DATE_OF_ISSUE)
    val dateOfIssue: String? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.MONTHLY_PAYMENT)
    val monthlyPayment: Double? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FLAG_CONSIDER_DAYS_OFF)
    val considerDaysOff: Int? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FLAG_PAY_ON_LAST_DAY_OF_MONTH)
    val payOnLastDayOfMonth: Int? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.APPLY_EXTRAS_IMMEDIATELY)
    val applyExtrasImmediately: Int? = 0,
    @ColumnInfo(name = DatabaseContract.LoanColumns.CALCULATE_EXTRAS_BY_TERM_LIKE_SBERBANK)
    val calculateExtrasByTermLikeSberbank: Int? = 1,
    @ColumnInfo(name = DatabaseContract.LoanColumns.IGNORE_PASSED_PERIODS_AFTER_RATE_CHANGE)
    val ignorePassedPeriodsAfterRateChange: Int? = 0,
    @ColumnInfo(name = DatabaseContract.LoanColumns.EXTRA_DAY_IN_MONTH)
    val extraDayInMonth: Int? = 0,
    @ColumnInfo(name = DatabaseContract.LoanColumns.IS_FORECAST_ACTIVE)
    val isForecastActive: Int? = 0,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FORECAST_MONTHLY_PAY)
    val forecastMonthlyPay: Double? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FORECAST_DAYS_BEFORE)
    val forecastDaysBefore: Int? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FORECAST_START_DATE)
    val forecastStartDate: String? = null,
    @ColumnInfo(name = DatabaseContract.LoanColumns.FORECAST_EXTRA_TYPE)
    val forecastExtraType: Int? = null,
)
