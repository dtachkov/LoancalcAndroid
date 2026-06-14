package com.example.loancalcandroid.ui.offers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.util.Formatters
import java.util.Calendar
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
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import ru.kredit.calculator.data.model.Offer

private const val MINIMUM_ALLOWED_TERM = 3
private const val AMOUNT_SLIDER_STEPS_COUNT = 20

data class OfferDetailUiState(
    val isLoading: Boolean = true,
    val offer: Offer? = null,
    val amountProgress: Int = 9,
    val termProgress: Int = 0,
    val amountStep: Int = 1,
    val amountMaxProgress: Int = AMOUNT_SLIDER_STEPS_COUNT - 1,
    val termMaxProgress: Int = 0,
    val selectedAmount: Int = 0,
    val selectedTerm: Int = MINIMUM_ALLOWED_TERM,
    val rateText: String = "",
    val monthlyPaymentText: String = "",
    val overpayText: String = "",
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
    val error: String? = null,
)

class OfferDetailViewModel(
    application: Application,
    private val offerId: Long,
) : AndroidViewModel(application) {
    private val offerRepository = LoanCalcData.get().offerRepository
    private val loanRepository = LoanCalcData.get().loanRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private val _uiState = MutableStateFlow(OfferDetailUiState())
    val uiState: StateFlow<OfferDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateAmountProgress(progress: Int) {
        _uiState.update { state ->
            val amount = (progress + 1) * state.amountStep
            state.copy(
                amountProgress = progress,
                selectedAmount = amount,
            )
        }
        recalculate()
    }

    fun updateTermProgress(progress: Int) {
        _uiState.update { state ->
            state.copy(
                termProgress = progress,
                selectedTerm = progress + MINIMUM_ALLOWED_TERM,
            )
        }
        recalculate()
    }

    fun saveToMyLoans() {
        val state = _uiState.value
        val offer = state.offer ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, savedMessage = null, error = null) }
            try {
                val loan = Loan(
                    title = offer.name,
                    amount = state.selectedAmount.toFloat(),
                    term = state.selectedTerm,
                    rate = state.rateForLoan(),
                    type = LoanType.ANNUITY,
                    firstPaymentDate = Date().clearTime(),
                )
                loanRepository.saveLoan(loan)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedMessage = "Кредит сохранён в списке",
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = e.message ?: "Ошибка сохранения")
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val offers = offerRepository.getOffers()
            val offer = offers.firstOrNull { it.id == offerId }
            if (offer == null) {
                _uiState.update { it.copy(isLoading = false, error = "Предложение не найдено") }
                return@launch
            }

            val amountStep = (offer.limit / AMOUNT_SLIDER_STEPS_COUNT).toInt().coerceAtLeast(1)
            val amountMaxProgress = AMOUNT_SLIDER_STEPS_COUNT - 1
            val termMaxProgress = (offer.term - MINIMUM_ALLOWED_TERM).coerceAtLeast(0)
            val amountProgress = amountMaxProgress / 2
            val termProgress = termMaxProgress * 2 / 3

            _uiState.update {
                it.copy(
                    isLoading = false,
                    offer = offer,
                    amountStep = amountStep,
                    amountMaxProgress = amountMaxProgress,
                    termMaxProgress = termMaxProgress,
                    amountProgress = amountProgress,
                    termProgress = termProgress,
                    selectedAmount = (amountProgress + 1) * amountStep,
                    selectedTerm = termProgress + MINIMUM_ALLOWED_TERM,
                )
            }
            recalculate()
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val offer = state.offer ?: return

        viewModelScope.launch {
            try {
                val loan = Loan(
                    title = offer.name,
                    amount = state.selectedAmount.toFloat(),
                    term = state.selectedTerm,
                    rate = state.rateForLoan(),
                    type = LoanType.ANNUITY,
                    firstPaymentDate = Date().clearTime(),
                )
                val result = withContext(Dispatchers.Default) {
                    loanCalculator.calculate(loan, emptyList())
                }
                val overpay = result.totalInterest + result.fees + result.insurance
                _uiState.update {
                    it.copy(
                        rateText = Formatters.schedulePercent(state.rateForLoan().toDouble()),
                        monthlyPaymentText = Formatters.moneyWithoutDecimal(result.currentPayment),
                        overpayText = Formatters.moneyWithoutDecimal(overpay),
                        error = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = CalculationErrors.format(e)) }
            }
        }
    }

    private fun OfferDetailUiState.rateForLoan(): Float {
        val offer = offer ?: return 0f
        val amount = selectedAmount.toDouble()
        val rate = when (val offerRate = offer.rate) {
            is Offer.FixedRateValue -> offerRate.value
            is Offer.FloatingRate -> offerRate.valueForAmount(amount)
            null -> 0.0
        }
        return rate.toFloat()
    }
}

private fun Date.clearTime(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
