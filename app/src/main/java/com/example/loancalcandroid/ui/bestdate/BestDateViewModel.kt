package com.example.loancalcandroid.ui.bestdate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.util.Formatters
import java.util.Calendar
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
import ru.kredit.calculator.data.calculation.BestDateRow
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan

data class BestDateUiState(
    val isLoading: Boolean = true,
    val isCalculating: Boolean = false,
    val amount: String = "10000",
    val startDate: Date = Date(),
    val endDate: Date = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time,
    val decreaseAmount: Boolean = true,
    val progress: Int = 0,
    val progressMax: Int = 0,
    val progressText: String = "",
    val rows: List<BestDateRow> = emptyList(),
    val bestDate: Date? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val error: String? = null,
    val reviewRequestTrigger: Int = 0,
)

class BestDateViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val featureCalculators = LoanCalcData.get().featureCalculators

    private var loan: Loan? = null
    private var calculationJob: Job? = null

    private val _uiState = MutableStateFlow(BestDateUiState())
    val uiState: StateFlow<BestDateUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateAmount(value: String) = _uiState.update { it.copy(amount = value, amountError = null) }
    fun updateStartDate(date: Date) = _uiState.update { it.copy(startDate = date, dateError = null) }
    fun updateEndDate(date: Date) = _uiState.update { it.copy(endDate = date, dateError = null) }
    fun setDecreaseAmount(selected: Boolean) = _uiState.update { it.copy(decreaseAmount = selected) }

    fun calculate() {
        if (!validate()) return
        val currentLoan = loan ?: return

        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCalculating = true,
                    error = null,
                    rows = emptyList(),
                    bestDate = null,
                    progress = 0,
                    progressMax = 0,
                    progressText = "",
                )
            }
            try {
                val state = _uiState.value
                val extras = extraRepository.getExtras(loanId)
                val rows = withContext(Dispatchers.Default) {
                    featureCalculators.calculateBestDates(
                        loan = currentLoan,
                        existingExtras = extras,
                        amount = Formatters.parseMoney(state.amount).toDouble(),
                        decreaseAmount = state.decreaseAmount,
                        startDate = state.startDate,
                        endDate = state.endDate,
                    ) { currentDate, progress, max ->
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    progress = progress,
                                    progressMax = max,
                                    progressText = Formatters.date(currentDate),
                                )
                            }
                        }
                    }
                }
                val best = featureCalculators.pickBestDate(rows)
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        rows = rows,
                        bestDate = best?.date,
                        reviewRequestTrigger = it.reviewRequestTrigger + 1,
                    )
                }
                AnalyticsHelper.logEvent("CALC_BEST_DATE")
            } catch (e: CancellationException) {
                _uiState.update { it.copy(isCalculating = false) }
            } catch (e: Exception) {
                AnalyticsHelper.logEvent("ERROR_BEST_DATE", CalculationErrors.format(e))
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

    fun selectedExtraType(): ExtraType {
        return if (_uiState.value.decreaseAmount) {
            ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT
        } else {
            ExtraType.PAYMENT_FOR_DECREASE_TERM
        }
    }

    private fun load() {
        viewModelScope.launch {
            val loaded = loanRepository.getLoan(loanId)
            if (loaded == null) {
                _uiState.update { it.copy(isLoading = false, error = "Кредит не найден") }
                return@launch
            }
            loan = loaded
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var valid = true
        var amountError: String? = null
        var dateError: String? = null

        if (Formatters.parseMoney(state.amount) <= 0f) {
            amountError = "Укажите сумму досрочного платежа"
            valid = false
        }
        if (!state.startDate.before(state.endDate)) {
            dateError = "Конечная дата должна быть позже начальной"
            valid = false
        }

        _uiState.update { it.copy(amountError = amountError, dateError = dateError) }
        return valid
    }
}
