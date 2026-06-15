package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.ExtraForecastException
import com.zoom.loancalc.InfiniteLoanException
import com.zoom.loancalc.LoanCalendar
import com.zoom.loancalc.LoanException
import com.zoom.loancalc.LoanManager
import com.zoom.loancalc.calculation.Calculator
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import java.util.Calendar
import java.util.Date
import java.util.SortedSet
import java.util.TreeSet
import com.zoom.loancalc.Extra as CalcExtra
import com.zoom.loancalc.Loan as CalcLoan

class LoanCalculator {
    companion object {
        const val CALCULATION_MODE_DEFAULT = 0
        const val CALCULATION_MODE_BEST_LOAN = 1
    }

    @Throws(InfiniteLoanException::class, ExtraForecastException::class, LoanException::class)
    fun calculate(
        loan: Loan,
        extras: List<Extra>,
        objectPrice: Double = 0.0,
        additionalExtras: SortedSet<CalcExtra>? = null,
        calculationMode: Int = CALCULATION_MODE_DEFAULT,
    ): LoanCalculationResult {
        val calendar = createCalendar(loan)
        val loanManager = LoanManager(calendar, Calculator(calendar)).apply {
            setObjectPrice(objectPrice)
        }

        val firstPaymentDate = loan.firstPaymentDate ?: Date(0)
        val issueDate = loan.dateOfIssue ?: Date(0).apply { time = 0 }
        val firstPaymentIsPercentOnly = loan.dateOfIssue != null

        val (firstDay, firstMonth, firstYear) = firstPaymentDate.toYmd()
        val (issueDay, issueMonth, issueYear) = issueDate.toYmd()

        return calculateInternal(
            loanCalendar = calendar,
            loanManager = loanManager,
            firstPaymentDay = firstDay,
            firstPaymentMonth = firstMonth,
            firstPaymentYear = firstYear,
            dateOfIssueDay = issueDay,
            dateOfIssueMonth = issueMonth,
            dateOfIssueYear = issueYear,
            term = loan.term,
            rate = loan.rate,
            amount = loan.amount,
            type = loan.type.toInt(),
            raiffeisen = loan.extraDayInMonth,
            firstPaymentIsPercentOnly = firstPaymentIsPercentOnly,
            applyExtrasImmediately = loan.applyExtrasImmediately,
            interestOnlyAfterPrincipalPaidByExtra = loan.calculateExtrasByBalanceLikeSberbank,
            ignorePassedPeriodsAfterRateChange = loan.ignorePassedPeriodsAfterRateChange,
            domainExtras = extras,
            additionalExtras = additionalExtras,
            isForecastActive = loan.isForecastActive,
            forecastPayment = loan.forecastMonthlyPayment,
            forecastDays = loan.forecastDaysBefore,
            forecastDate = loan.forecastStartDate,
            forecastExtraType = loan.forecastExtraType.toInt(),
            calculationMode = calculationMode,
        ).let { result ->
            val (fees, insurance) = commissionTotalsFromExtras(extras)
            result.copy(
                loanId = loan.id,
                loanTitle = loan.title.orEmpty(),
                fees = fees,
                insurance = insurance,
            )
        }
    }

    private fun commissionTotalsFromExtras(extras: List<Extra>): Pair<Double, Double> {
        var fees = 0.0
        var insurance = 0.0
        for (extra in extras) {
            when (extra.type) {
                ExtraType.FEE -> fees += extra.amount
                ExtraType.INSURANCE -> insurance += extra.amount
                else -> Unit
            }
        }
        return fees to insurance
    }

    @Throws(InfiniteLoanException::class, ExtraForecastException::class, LoanException::class)
    private fun calculateInternal(
        loanCalendar: LoanCalendar,
        loanManager: LoanManager,
        firstPaymentDay: Int,
        firstPaymentMonth: Int,
        firstPaymentYear: Int,
        dateOfIssueDay: Int,
        dateOfIssueMonth: Int,
        dateOfIssueYear: Int,
        term: Int,
        rate: Float,
        amount: Float,
        type: Int,
        raiffeisen: Boolean,
        firstPaymentIsPercentOnly: Boolean,
        applyExtrasImmediately: Boolean,
        interestOnlyAfterPrincipalPaidByExtra: Boolean,
        ignorePassedPeriodsAfterRateChange: Boolean,
        domainExtras: List<Extra>,
        additionalExtras: SortedSet<CalcExtra>?,
        isForecastActive: Boolean,
        forecastPayment: Float,
        forecastDays: Int,
        forecastDate: Date?,
        forecastExtraType: Int,
        calculationMode: Int,
    ): LoanCalculationResult {
        val extras = TreeSet<CalcExtra>()
        val percentExtras = TreeSet<CalcExtra>()

        for (extra in domainExtras) {
            val extraDate = extra.date ?: continue
            val calcType = ExtraTypeMapper.toCalculatorType(extra.type)
            val extraAmount = extra.amount.toDouble()

            when (extra.type) {
                ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
                ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
                -> {
                    val startCredit = loanCalendar.date(firstPaymentYear, firstPaymentMonth, firstPaymentDay)
                    val endCredit = addMonths(startCredit, term)
                    var currentDate = extraDate
                    while (currentDate.time in startCredit.time..endCredit.time) {
                        val (day, month, year) = currentDate.toYmd()
                        val normalizedDate = loanCalendar.date(year, month, day)
                        addOrAccumulateExtra(extras, calcType, extraAmount, normalizedDate)
                        currentDate = addMonths(currentDate, 1)
                    }
                }
                else -> {
                    val (day, month, year) = extraDate.toYmd()
                    val normalizedDate = loanCalendar.date(year, month, day)
                    if (extra.type == ExtraType.CHANGE_RATE || extra.type == ExtraType.PAYMENT_FOR_CHANGE_DATE) {
                        percentExtras.add(CalcExtra(extraAmount, calcType, normalizedDate))
                    }
                    addOrAccumulateExtra(extras, calcType, extraAmount, normalizedDate)
                }
            }
        }

        val loanWithoutExtras: CalcLoan
        val calcLoan: CalcLoan

        if (calculationMode == CALCULATION_MODE_DEFAULT) {
            additionalExtras?.let { extras.addAll(it) }
            loanWithoutExtras = buildCalcLoan(
                loanCalendar,
                firstPaymentIsPercentOnly,
                dateOfIssueYear,
                dateOfIssueMonth,
                dateOfIssueDay,
                firstPaymentYear,
                firstPaymentMonth,
                firstPaymentDay,
                raiffeisen,
                applyExtrasImmediately,
                interestOnlyAfterPrincipalPaidByExtra,
                ignorePassedPeriodsAfterRateChange,
                term,
                rate,
                amount,
                type,
                percentExtras,
            )
            calcLoan = buildCalcLoan(
                loanCalendar,
                firstPaymentIsPercentOnly,
                dateOfIssueYear,
                dateOfIssueMonth,
                dateOfIssueDay,
                firstPaymentYear,
                firstPaymentMonth,
                firstPaymentDay,
                raiffeisen,
                applyExtrasImmediately,
                interestOnlyAfterPrincipalPaidByExtra,
                ignorePassedPeriodsAfterRateChange,
                term,
                rate,
                amount,
                type,
                extras,
            )
        } else {
            loanWithoutExtras = buildCalcLoan(
                loanCalendar,
                firstPaymentIsPercentOnly,
                dateOfIssueYear,
                dateOfIssueMonth,
                dateOfIssueDay,
                firstPaymentYear,
                firstPaymentMonth,
                firstPaymentDay,
                raiffeisen,
                applyExtrasImmediately,
                interestOnlyAfterPrincipalPaidByExtra,
                ignorePassedPeriodsAfterRateChange,
                term,
                rate,
                amount,
                type,
                extras,
            )
            additionalExtras?.let { extras.addAll(it) }
            calcLoan = buildCalcLoan(
                loanCalendar,
                firstPaymentIsPercentOnly,
                dateOfIssueYear,
                dateOfIssueMonth,
                dateOfIssueDay,
                firstPaymentYear,
                firstPaymentMonth,
                firstPaymentDay,
                raiffeisen,
                applyExtrasImmediately,
                interestOnlyAfterPrincipalPaidByExtra,
                ignorePassedPeriodsAfterRateChange,
                term,
                rate,
                amount,
                type,
                extras,
            )
        }

        if (isForecastActive) {
            calcLoan.daysBeforePayment = forecastDays
            calcLoan.dateOfStartCalcExtras = forecastDate
            calcLoan.forecastExtraType = ExtraTypeMapper.toCalculatorType(ExtraType.fromInt(forecastExtraType))
            calcLoan.forecastMonthlyPayment = forecastPayment.toDouble()
        }

        loanManager.setLoan(calcLoan)
        loanManager.calculate(isForecastActive)

        val stats = loanManager.stats
        val currentPayment = stats.currentPayment

        val loanManagerWithoutExtras = loanManager.clone() as LoanManager
        loanManagerWithoutExtras.setLoan(loanWithoutExtras)
        loanManagerWithoutExtras.calculate(false)
        val savedMoney = loanManagerWithoutExtras.stats.interest - stats.interest

        return LoanCalculationResult.from(
            loanId = 0,
            loanTitle = "",
            stats = stats,
            payments = loanManager.payments,
            currentPayment = currentPayment.total,
            currentPaymentDate = currentPayment.date,
            currentPaymentIndex = currentPayment.index,
            savedMoney = savedMoney,
        )
    }

    @Throws(InfiniteLoanException::class, ExtraForecastException::class, LoanException::class)
    fun calculateTaxStats(
        loan: Loan,
        extras: List<Extra>,
        objectPrice: Double,
    ): TaxCalculationResult {
        val calendar = createCalendar(loan)
        val loanManager = LoanManager(calendar, Calculator(calendar)).apply {
            setObjectPrice(objectPrice)
        }

        val firstPaymentDate = loan.firstPaymentDate ?: Date(0)
        val issueDate = loan.dateOfIssue ?: Date(0).apply { time = 0 }
        val firstPaymentIsPercentOnly = loan.dateOfIssue != null

        val (firstDay, firstMonth, firstYear) = firstPaymentDate.toYmd()
        val (issueDay, issueMonth, issueYear) = issueDate.toYmd()

        val calcLoan = buildCalcLoan(
            loanCalendar = calendar,
            firstPaymentIsPercentOnly = firstPaymentIsPercentOnly,
            dateOfIssueYear = issueYear,
            dateOfIssueMonth = issueMonth,
            dateOfIssueDay = issueDay,
            firstPaymentYear = firstYear,
            firstPaymentMonth = firstMonth,
            firstPaymentDay = firstDay,
            raiffeisen = loan.extraDayInMonth,
            applyExtrasImmediately = loan.applyExtrasImmediately,
            interestOnlyAfterPrincipalPaidByExtra = loan.calculateExtrasByBalanceLikeSberbank,
            ignorePassedPeriodsAfterRateChange = loan.ignorePassedPeriodsAfterRateChange,
            term = loan.term,
            rate = loan.rate,
            amount = loan.amount,
            type = loan.type.toInt(),
            extras = buildExtrasSet(calendar, loan, extras),
        )

        loanManager.setLoan(calcLoan)
        loanManager.calculate(false)
        val stats = loanManager.stats

        return TaxCalculationResult(
            principalTax = stats.principialTax,
            interestTax = stats.interestTax,
            totalReturnTax = stats.totalReturnTax,
            taxPayments = stats.taxPayments.orEmpty().map { payment ->
                TaxPaymentRow(
                    year = payment.year,
                    interestPayment = payment.payment,
                    returnTax = payment.returnTax,
                )
            },
        )
    }

    private fun buildExtrasSet(
        loanCalendar: LoanCalendar,
        loan: Loan,
        domainExtras: List<Extra>,
    ): TreeSet<CalcExtra> {
        val extras = TreeSet<CalcExtra>()
        val firstPaymentDate = loan.firstPaymentDate ?: Date(0)
        val (firstDay, firstMonth, firstYear) = firstPaymentDate.toYmd()

        for (extra in domainExtras) {
            val extraDate = extra.date ?: continue
            val calcType = ExtraTypeMapper.toCalculatorType(extra.type)
            val extraAmount = extra.amount.toDouble()

            when (extra.type) {
                ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
                ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
                -> {
                    val startCredit = loanCalendar.date(firstYear, firstMonth, firstDay)
                    val endCredit = addMonths(startCredit, loan.term)
                    var currentDate = extraDate
                    while (currentDate.time in startCredit.time..endCredit.time) {
                        val (day, month, year) = currentDate.toYmd()
                        val normalizedDate = loanCalendar.date(year, month, day)
                        addOrAccumulateExtra(extras, calcType, extraAmount, normalizedDate)
                        currentDate = addMonths(currentDate, 1)
                    }
                }
                else -> {
                    val (day, month, year) = extraDate.toYmd()
                    val normalizedDate = loanCalendar.date(year, month, day)
                    addOrAccumulateExtra(extras, calcType, extraAmount, normalizedDate)
                }
            }
        }
        return extras
    }

    private fun createCalendar(loan: Loan): LoanCalendar {
        return LoanCalendar().apply {
            lastDayFlag = loan.payOnLastDayOfMonth
            moveDayOff = loan.considerDaysOff
            extraDayInMonth = if (loan.extraDayInMonth) 1 else 0
        }
    }

    private fun buildCalcLoan(
        loanCalendar: LoanCalendar,
        firstPaymentIsPercentOnly: Boolean,
        dateOfIssueYear: Int,
        dateOfIssueMonth: Int,
        dateOfIssueDay: Int,
        firstPaymentYear: Int,
        firstPaymentMonth: Int,
        firstPaymentDay: Int,
        raiffeisen: Boolean,
        applyExtrasImmediately: Boolean,
        interestOnlyAfterPrincipalPaidByExtra: Boolean,
        ignorePassedPeriodsAfterRateChange: Boolean,
        term: Int,
        rate: Float,
        amount: Float,
        type: Int,
        extras: SortedSet<CalcExtra>,
    ): CalcLoan {
        val issueDate = if (firstPaymentIsPercentOnly) {
            loanCalendar.date(dateOfIssueYear, dateOfIssueMonth, dateOfIssueDay)
        } else {
            null
        }
        val startDate = loanCalendar.date(firstPaymentYear, firstPaymentMonth, firstPaymentDay)
        return CalcLoan(
            issueDate,
            startDate,
            raiffeisen,
            firstPaymentIsPercentOnly,
            applyExtrasImmediately,
            interestOnlyAfterPrincipalPaidByExtra,
            ignorePassedPeriodsAfterRateChange,
            term,
            rate.toDouble(),
            amount.toDouble(),
            type,
            extras,
        )
    }

    private fun addOrAccumulateExtra(
        extras: SortedSet<CalcExtra>,
        calcType: Int,
        extraAmount: Double,
        date: Date,
    ) {
        for (extra in extras) {
            if (extra.type == calcType && extra.date.time == date.time) {
                extra.value = extra.value + extraAmount
                return
            }
        }
        extras.add(CalcExtra(extraAmount, calcType, date))
    }

    private fun addMonths(date: Date, months: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, months)
        return calendar.time
    }

    private fun Date.toYmd(): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return Triple(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR),
        )
    }
}
