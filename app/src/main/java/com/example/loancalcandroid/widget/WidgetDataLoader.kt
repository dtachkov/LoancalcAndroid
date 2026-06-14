package com.example.loancalcandroid.widget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.calculation.LoanCalculationResult

object WidgetDataLoader {
    sealed interface LoadResult {
        data class Success(val calculation: LoanCalculationResult) : LoadResult
        data object LoanNotFound : LoadResult
        data object InfiniteLoan : LoadResult
        data object CalculationError : LoadResult
    }

    suspend fun load(loanId: Long): LoadResult = withContext(Dispatchers.Default) {
        if (loanId <= 0L) return@withContext LoadResult.LoanNotFound

        val data = LoanCalcData.get()
        val loan = data.loanRepository.getLoan(loanId) ?: return@withContext LoadResult.LoanNotFound
        if (!loan.validate()) return@withContext LoadResult.LoanNotFound

        val extras = data.extraRepository.getExtras(loanId)
        runCatching {
            data.loanCalculator.calculate(loan, extras)
        }.fold(
            onSuccess = { LoadResult.Success(it) },
            onFailure = { error ->
                when {
                    CalculationErrors.isInfiniteLoan(error) -> LoadResult.InfiniteLoan
                    else -> LoadResult.CalculationError
                }
            },
        )
    }
}
