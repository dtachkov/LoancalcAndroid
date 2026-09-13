package com.example.loancalcandroid.ui.ratebypayment

import androidx.lifecycle.ViewModel
import com.example.loancalcandroid.util.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.kredit.calculator.data.calculation.RateByPaymentCalculator

data class RateByPaymentUiState(
    val paymentAmount: String = "",
    val loanAmount: String = "",
    val term: String = "",
    val rate: String = "",
    val showPaymentTotalError: Boolean = false,
)

class RateByPaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RateByPaymentUiState())
    val uiState: StateFlow<RateByPaymentUiState> = _uiState.asStateFlow()

    fun updatePaymentAmount(value: String) = update { it.copy(paymentAmount = value) }

    fun updateLoanAmount(value: String) = update { it.copy(loanAmount = value) }

    fun updateTerm(value: String) = update { it.copy(term = value) }

    private fun update(transform: (RateByPaymentUiState) -> RateByPaymentUiState) {
        _uiState.update { transform(it).withCalculatedRate() }
    }

    private fun RateByPaymentUiState.withCalculatedRate(): RateByPaymentUiState {
        val payment = Formatters.parseMoneyDouble(paymentAmount)
        val principal = Formatters.parseMoneyDouble(loanAmount)
        val months = Formatters.parseInt(term)
        val hasInputs = paymentAmount.isNotBlank() && loanAmount.isNotBlank() && term.isNotBlank()
        val showPaymentTotalError = hasInputs &&
            payment > 0.0 &&
            principal > 0.0 &&
            months > 0 &&
            payment * months < principal
        val annual = RateByPaymentCalculator.annualPercent(principal, payment, months)
        return copy(
            rate = annual?.let(Formatters::schedulePercent).orEmpty(),
            showPaymentTotalError = showPaymentTotalError,
        )
    }
}
