package com.example.loancalcandroid.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.util.Formatters
import com.example.loancalcandroid.util.ShareScheduleUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.calculation.LoanCalculationResult
import ru.kredit.calculator.data.model.Loan

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val loan: Loan? = null,
    val calculation: LoanCalculationResult? = null,
    val rows: List<ScheduleRow> = emptyList(),
    val summary: ScheduleSummary? = null,
    val showPreviousPayments: Boolean = false,
    val selectedTab: Int = 0,
    val error: String? = null,
) {
    val visibleRows: List<ScheduleRow>
        get() = ScheduleRowsGenerator.visibleRows(rows, showPreviousPayments)

    val hasHiddenRows: Boolean
        get() = ScheduleRowsGenerator.currentRowIndex(rows) > 0
}

class ScheduleViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun toggleShowPreviousPayments() {
        _uiState.update { it.copy(showPreviousPayments = !it.showPreviousPayments) }
    }

    fun share(onError: (String) -> Unit) {
        if (loanId == 0L) {
            onError("Сначала сохраните кредит")
            return
        }
        viewModelScope.launch {
            try {
                ShareScheduleUtil.share(getApplication(), loanId)
            } catch (e: Exception) {
                onError(CalculationErrors.format(e))
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val loan = loanRepository.getLoan(loanId)
            if (loan == null) {
                _uiState.update { it.copy(isLoading = false, error = "Кредит не найден") }
                return@launch
            }
            val extras = extraRepository.getExtras(loanId)
            val calculation = withContext(Dispatchers.Default) {
                runCatching { loanCalculator.calculate(loan, extras) }.getOrNull()
            }
            if (calculation == null) {
                _uiState.update {
                    it.copy(isLoading = false, loan = loan, error = "Не удалось рассчитать график")
                }
                return@launch
            }

            val rows = ScheduleRowsGenerator.generate(
                loan = loan,
                payments = calculation.payments,
                currentPaymentIndex = calculation.currentPaymentIndex,
            )
            val summary = buildSummary(loan, calculation)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loan = loan,
                    calculation = calculation,
                    rows = rows,
                    summary = summary,
                    error = null,
                )
            }
        }
    }

    private fun buildSummary(loan: Loan, calculation: LoanCalculationResult): ScheduleSummary {
        val forecastLabel = if (loan.isForecastActive && loan.forecastStartDate != null) {
            "${Formatters.moneyFixed(loan.forecastMonthlyPayment)} с ${Formatters.inputDate(loan.forecastStartDate)}"
        } else {
            null
        }
        return ScheduleSummary(
            paidPrincipal = calculation.alreadyPaidPrincipal,
            loanAmount = loan.amount.toDouble(),
            totalExtras = calculation.totalExtras,
            forecastLabel = forecastLabel,
        )
    }
}
