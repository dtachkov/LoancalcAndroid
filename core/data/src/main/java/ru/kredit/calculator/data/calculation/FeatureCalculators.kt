package ru.kredit.calculator.data.calculation

import com.zoom.loancalc.Extra
import com.zoom.loancalc.ExtraForecastException
import com.zoom.loancalc.InfiniteLoanException
import com.zoom.loancalc.LoanException
import ru.kredit.calculator.data.model.Extra as DomainExtra
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanFull
import java.util.Calendar
import java.util.Date
import java.util.TreeSet
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlinx.coroutines.ensureActive

data class BestDateRow(
    val date: Date,
    val overpayment: Double,
)

data class LoanCompareRow(
    val loanId: Long,
    val loanTitle: String,
    val savings: Double,
)

class FeatureCalculators(
    private val loanCalculator: LoanCalculator,
) {
    @Throws(InfiniteLoanException::class, ExtraForecastException::class, LoanException::class)
    suspend fun calculateBestDates(
        loan: Loan,
        existingExtras: List<DomainExtra>,
        amount: Double,
        decreaseAmount: Boolean,
        startDate: Date,
        endDate: Date,
        onProgress: suspend (currentDate: Date, progress: Int, max: Int) -> Unit = { _, _, _ -> },
    ): List<BestDateRow> {
        val calcType = if (decreaseAmount) Extra.BALANCE else Extra.TERM
        val calendar = Calendar.getInstance().apply {
            time = startDate.clearTime()
        }
        val endMillis = endDate.clearTime().time
        val maxDays = ((endMillis - calendar.timeInMillis) / DAY_MILLIS).toInt().coerceAtLeast(1)
        val results = ArrayList<BestDateRow>(maxDays)
        var dayIndex = 0

        while (calendar.timeInMillis < endMillis) {
            coroutineContext.ensureActive()
            val currentDate = calendar.time
            val additionalExtras = TreeSet<Extra>()
            additionalExtras.add(Extra(amount, calcType, currentDate))
            val calculation = loanCalculator.calculate(
                loan = loan,
                extras = existingExtras,
                additionalExtras = additionalExtras,
            )
            results.add(BestDateRow(currentDate, calculation.totalInterest))
            onProgress(currentDate, dayIndex + 1, maxDays)
            calendar.add(Calendar.DATE, 1)
            dayIndex++
        }

        return results.sortedBy { it.date }
    }

    fun pickBestDate(rows: List<BestDateRow>): BestDateRow? {
        if (rows.isEmpty()) return null
        val minOverpayment = rows.minOf { it.overpayment }
        return rows
            .filter { abs(it.overpayment - minOverpayment) < 0.01 }
            .maxByOrNull { it.date.time }
    }

    @Throws(InfiniteLoanException::class, ExtraForecastException::class, LoanException::class)
    suspend fun compareLoans(
        loans: List<LoanFull>,
        amount: Double,
        decreaseAmount: Boolean,
        extraDate: Date,
        onProgress: suspend (loanTitle: String, progress: Int, max: Int) -> Unit = { _, _, _ -> },
    ): List<LoanCompareRow> {
        val calcType = if (decreaseAmount) Extra.BALANCE else Extra.TERM
        val additionalExtras = TreeSet<Extra>()
        additionalExtras.add(Extra(amount, calcType, extraDate))
        val results = ArrayList<LoanCompareRow>(loans.size)

        loans.forEachIndexed { index, loanFull ->
            coroutineContext.ensureActive()
            val loan = loanFull.loan
            val savings = if (loan.isForecastActive) {
                0.0
            } else {
                val calculation = loanCalculator.calculate(
                    loan = loan,
                    extras = loanFull.extras,
                    additionalExtras = additionalExtras,
                    calculationMode = LoanCalculator.CALCULATION_MODE_BEST_LOAN,
                )
                calculation.savedMoney
            }
            results.add(
                LoanCompareRow(
                    loanId = loan.id,
                    loanTitle = loan.title.orEmpty(),
                    savings = savings,
                ),
            )
            onProgress(loan.title.orEmpty(), index + 1, loans.size)
        }

        return results.sortedBy { it.loanTitle }
    }

    fun pickBestLoan(rows: List<LoanCompareRow>): LoanCompareRow? {
        if (rows.isEmpty()) return null
        return rows.maxByOrNull { it.savings }
    }

    @Throws(InfiniteLoanException::class, ExtraForecastException::class, LoanException::class)
    fun calculateTax(
        loan: Loan,
        extras: List<DomainExtra>,
        objectPrice: Double,
    ): TaxCalculationResult {
        return loanCalculator.calculateTaxStats(loan, extras, objectPrice)
    }
}

private const val DAY_MILLIS = 24L * 60L * 60L * 1000L

private fun Date.clearTime(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
