package com.example.loancalcandroid.ui.forecast

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.R
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import java.util.Date

data class ForecastUiState(
    val isLoading: Boolean = true,
    val isCalculating: Boolean = false,
    val forecastEnabled: Boolean = false,
    val monthlyPayment: String = "10000",
    val daysBeforePayment: String = "0",
    val startDate: Date? = null,
    val decreaseAmount: Boolean = true,
    val monthlyPaymentError: String? = null,
    val daysError: String? = null,
    val message: String? = null,
    val error: String? = null,
)

class ForecastViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private var loan: Loan? = null

    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun setForecastEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(forecastEnabled = enabled, message = null, error = null)
        }
        if (!enabled) {
            saveAndCalculate(disableOnly = true)
        }
    }

    fun updateMonthlyPayment(value: String) =
        _uiState.update { it.copy(monthlyPayment = value, monthlyPaymentError = null) }

    fun updateDaysBeforePayment(value: String) =
        _uiState.update { it.copy(daysBeforePayment = value, daysError = null) }

    fun updateStartDate(date: Date) =
        _uiState.update { it.copy(startDate = date) }

    fun setDecreaseAmount(selected: Boolean) =
        _uiState.update { it.copy(decreaseAmount = selected) }

    fun calculateForecast() {
        if (!validate()) return
        saveAndCalculate(disableOnly = false)
    }

    private fun load() {
        viewModelScope.launch {
            val loaded = loanRepository.getLoan(loanId)
            if (loaded == null) {
                _uiState.update { it.copy(isLoading = false, error = "Кредит не найден") }
                return@launch
            }
            loan = loaded
            _uiState.update {
                it.copy(
                    isLoading = false,
                    forecastEnabled = loaded.isForecastActive,
                    monthlyPayment = formatNumber(loaded.forecastMonthlyPayment),
                    daysBeforePayment = loaded.forecastDaysBefore.toString(),
                    startDate = loaded.forecastStartDate ?: Date(),
                    decreaseAmount = loaded.forecastExtraType != ExtraType.PAYMENT_FOR_DECREASE_TERM,
                )
            }
        }
    }

    private fun saveAndCalculate(disableOnly: Boolean) {
        val currentLoan = loan ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, message = null, error = null) }
            try {
                val state = _uiState.value
                val updatedLoan = buildLoan(currentLoan, state, disableOnly)
                loanRepository.saveLoan(updatedLoan)
                loan = updatedLoan

                if (!updatedLoan.isForecastActive) {
                    _uiState.update {
                        it.copy(isCalculating = false, message = "Прогноз отключён")
                    }
                    return@launch
                }

                val extras = extraRepository.getExtras(loanId)
                withContext(Dispatchers.Default) {
                    loanCalculator.calculate(updatedLoan, extras)
                }
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        message = getApplication<Application>().getString(R.string.forecast_calculated),
                    )
                }
                AnalyticsHelper.logEvent("CALC_FORECAST")
            } catch (e: Exception) {
                _uiState.update { it.copy(isCalculating = false, error = CalculationErrors.format(e)) }
            }
        }
    }

    private fun buildLoan(current: Loan, state: ForecastUiState, disableOnly: Boolean): Loan {
        if (disableOnly && !state.forecastEnabled) {
            return current.copy(isForecastActive = false)
        }
        return current.copy(
            isForecastActive = state.forecastEnabled,
            forecastMonthlyPayment = com.example.loancalcandroid.util.Formatters.parseMoney(state.monthlyPayment),
            forecastDaysBefore = com.example.loancalcandroid.util.Formatters.parseInt(state.daysBeforePayment),
            forecastStartDate = state.startDate,
            forecastExtraType = if (state.decreaseAmount) {
                ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT
            } else {
                ExtraType.PAYMENT_FOR_DECREASE_TERM
            },
        )
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var valid = true
        var monthlyPaymentError: String? = null
        var daysError: String? = null

        if (com.example.loancalcandroid.util.Formatters.parseMoney(state.monthlyPayment) <= 0f) {
            monthlyPaymentError = "Укажите сумму досрочного платежа"
            valid = false
        }
        if (state.daysBeforePayment.isBlank()) {
            daysError = "Укажите число дней"
            valid = false
        }

        _uiState.update { it.copy(monthlyPaymentError = monthlyPaymentError, daysError = daysError) }
        return valid
    }

    private fun formatNumber(value: Float): String {
        return if (value % 1f == 0f) value.toInt().toString() else value.toString()
    }
}
