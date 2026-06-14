package com.example.loancalcandroid.ui.compare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.util.Formatters
import java.util.Date
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.calculation.LoanCompareRow

data class CompareUiState(
    val isLoading: Boolean = true,
    val isCalculating: Boolean = false,
    val amount: String = "10000",
    val extraDate: Date = Date(),
    val decreaseAmount: Boolean = true,
    val progress: Int = 0,
    val progressMax: Int = 0,
    val progressText: String = "",
    val rows: List<LoanCompareRow> = emptyList(),
    val bestLoanTitle: String? = null,
    val amountError: String? = null,
    val error: String? = null,
)

class CompareViewModel(
    application: Application,
    @Suppress("UNUSED_PARAMETER") loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val featureCalculators = LoanCalcData.get().featureCalculators

    private var calculationJob: Job? = null

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = false) }
    }

    fun updateAmount(value: String) = _uiState.update { it.copy(amount = value, amountError = null) }
    fun updateExtraDate(date: Date) = _uiState.update { it.copy(extraDate = date) }
    fun setDecreaseAmount(selected: Boolean) = _uiState.update { it.copy(decreaseAmount = selected) }

    fun calculate() {
        if (!validate()) return

        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCalculating = true,
                    error = null,
                    rows = emptyList(),
                    bestLoanTitle = null,
                    progress = 0,
                    progressMax = 0,
                    progressText = "",
                )
            }
            try {
                val loans = loanRepository.getLoans()
                if (loans.isEmpty()) {
                    _uiState.update {
                        it.copy(isCalculating = false, error = "Создайте кредиты для сравнения")
                    }
                    return@launch
                }

                val loanFulls = loanRepository.getLoanFulls(loans.map { it.id })
                val state = _uiState.value
                val rows = withContext(Dispatchers.Default) {
                    featureCalculators.compareLoans(
                        loans = loanFulls,
                        amount = Formatters.parseMoney(state.amount).toDouble(),
                        decreaseAmount = state.decreaseAmount,
                        extraDate = state.extraDate,
                    ) { loanTitle, progress, max ->
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    progress = progress,
                                    progressMax = max,
                                    progressText = loanTitle,
                                )
                            }
                        }
                    }
                }
                val best = featureCalculators.pickBestLoan(rows)
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        rows = rows,
                        bestLoanTitle = best?.loanTitle,
                    )
                }
                AnalyticsHelper.logEvent("CALC_BEST_LOAN")
            } catch (e: CancellationException) {
                _uiState.update { it.copy(isCalculating = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isCalculating = false, error = CalculationErrors.format(e))
                }
            }
        }
    }

    fun stopCalculation() {
        calculationJob?.cancel()
        _uiState.update {
            it.copy(
                isCalculating = false,
                progress = 0,
                progressMax = 0,
                progressText = "",
            )
        }
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var valid = true
        var amountError: String? = null

        if (Formatters.parseMoney(state.amount) <= 0f) {
            amountError = "Укажите сумму досрочного платежа"
            valid = false
        }

        _uiState.update { it.copy(amountError = amountError) }
        return valid
    }
}
