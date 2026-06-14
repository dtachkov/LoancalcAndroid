package com.example.loancalcandroid.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.analytics.AnalyticsHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.AllLoansAnalyticsData
import ru.kredit.calculator.data.calculation.AllLoansAnalyticsCalculator
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.model.Loan

enum class AnalyticsBlock {
    DEBT_DONUT,
    LOAN_INTEREST_BARS,
    TIMELINE_LINE,
    REPAYMENT_PROGRESS,
    YEARLY_LOAD,
}

data class AllLoansAnalyticsUiState(
    val isCalculating: Boolean = true,
    val calculationProgress: Int = 0,
    val calculationProgressMax: Int = 0,
    val data: AllLoansAnalyticsData? = null,
    val readyBlocks: Set<AnalyticsBlock> = emptySet(),
    val error: String? = null,
)

class AllLoansAnalyticsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val analyticsCalculator = AllLoansAnalyticsCalculator(LoanCalcData.get().loanCalculator)

    private val _uiState = MutableStateFlow(AllLoansAnalyticsUiState())
    val uiState: StateFlow<AllLoansAnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loanRepository.observeLoans()
                .distinctUntilChanged()
                .collect { loans ->
                    reload(loans)
                }
        }
    }

    fun load() {
        viewModelScope.launch {
            reload(loanRepository.getLoans())
        }
    }

    private suspend fun reload(loans: List<Loan>) {
        _uiState.value = AllLoansAnalyticsUiState()
        try {
            if (loans.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        error = "Добавьте кредиты для аналитики",
                    )
                }
                return
            }

            val loanFulls = loanRepository.getLoanFulls(loans.map { it.id })
            val data = withContext(Dispatchers.Default) {
                analyticsCalculator.calculate(loanFulls) { progress, max ->
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                calculationProgress = progress,
                                calculationProgressMax = max,
                            )
                        }
                    }
                }
            }

            if (data == null) {
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        error = "Не удалось рассчитать аналитику",
                    )
                }
                return
            }

            _uiState.update {
                it.copy(
                    isCalculating = false,
                    data = data,
                )
            }

            revealBlocksSequentially()
            AnalyticsHelper.logEvent("CALC_ALL_LOANS_ANALYTICS")
        } catch (e: CancellationException) {
            _uiState.update { it.copy(isCalculating = false) }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isCalculating = false,
                    error = CalculationErrors.format(e),
                )
            }
        }
    }

    private suspend fun revealBlocksSequentially() {
        AnalyticsBlock.entries.forEach { block ->
            yield()
            delay(40)
            _uiState.update { it.copy(readyBlocks = it.readyBlocks + block) }
        }
    }
}
