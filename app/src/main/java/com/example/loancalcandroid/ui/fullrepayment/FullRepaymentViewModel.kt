package com.example.loancalcandroid.ui.fullrepayment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.analytics.AnalyticsHelper
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.calculation.FullRepaymentCalculator
import ru.kredit.calculator.data.calculation.FullRepaymentResult
import ru.kredit.calculator.data.calculation.PaymentSummary
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.Loan

data class FullRepaymentUiState(
    val isLoading: Boolean = true,
    val plannedDate: Date = Date(),
    val result: FullRepaymentResult? = null,
    val error: String? = null,
    val reviewRequestTrigger: Int = 0,
)

class FullRepaymentViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private var loan: Loan? = null
    private var extras: List<Extra> = emptyList()
    private var payments: List<PaymentSummary> = emptyList()

    private val _uiState = MutableStateFlow(FullRepaymentUiState())
    val uiState: StateFlow<FullRepaymentUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updatePlannedDate(date: Date) {
        _uiState.update { it.copy(plannedDate = date) }
        applyCalculation(logAnalytics = false)
    }

    fun extraPrefillAmount(): String {
        val amount = _uiState.value.result?.extraPrefillAmount ?: return ""
        return if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", amount)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val loadedLoan = loanRepository.getLoan(loanId)
                if (loadedLoan == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Кредит не найден") }
                    return@launch
                }
                extras = extraRepository.getExtras(loanId)
                val calculation = withContext(Dispatchers.Default) {
                    loanCalculator.calculate(loadedLoan, extras)
                }
                loan = loadedLoan
                payments = calculation.payments
                _uiState.update { it.copy(isLoading = false, error = null) }
                applyCalculation(logAnalytics = true)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = CalculationErrors.format(e))
                }
            }
        }
    }

    private fun applyCalculation(logAnalytics: Boolean) {
        val currentLoan = loan ?: return
        val result = FullRepaymentCalculator.calculate(
            loan = currentLoan,
            payments = payments,
            extras = extras,
            plannedDate = _uiState.value.plannedDate,
        )
        _uiState.update {
            it.copy(
                result = result,
                reviewRequestTrigger = if (result != null && logAnalytics) {
                    it.reviewRequestTrigger + 1
                } else {
                    it.reviewRequestTrigger
                },
            )
        }
        if (result != null && logAnalytics) {
            AnalyticsHelper.logEvent("CALC_FULL_REPAYMENT")
        }
    }
}
