package com.example.loancalcandroid.ui.schedule

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
import ru.kredit.calculator.data.LoanCalcData
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.util.DateFormats
import java.util.Date

data class SchedulePaymentDetailUiState(
    val isLoading: Boolean = true,
    val row: ScheduleRow? = null,
    val periodExtras: List<Extra> = emptyList(),
    val error: String? = null,
)

class SchedulePaymentDetailViewModel(
    application: Application,
    private val loanId: Long,
    private val listIndex: Int,
    private val previousPaymentDateMillis: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private val _uiState = MutableStateFlow(SchedulePaymentDetailUiState())
    val uiState: StateFlow<SchedulePaymentDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
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
                _uiState.update { it.copy(isLoading = false, error = "Не удалось рассчитать график") }
                return@launch
            }

            val rows = ScheduleRowsGenerator.generate(
                loan = loan,
                payments = calculation.payments,
                currentPaymentIndex = calculation.currentPaymentIndex,
            )
            val row = rows.firstOrNull { it.listIndex == listIndex }
            if (row == null) {
                _uiState.update { it.copy(isLoading = false, error = "Платёж не найден") }
                return@launch
            }

            val previousDate = if (previousPaymentDateMillis > 0L) {
                Date(previousPaymentDateMillis)
            } else {
                DateFormats.parseDate("2010-01-01")
            }
            val periodExtras = filterExtrasForPeriod(
                extras = extras,
                previousPaymentDate = previousDate,
                paymentDate = row.date,
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    row = row,
                    periodExtras = periodExtras,
                    error = null,
                )
            }
        }
    }

    private fun filterExtrasForPeriod(
        extras: List<Extra>,
        previousPaymentDate: Date?,
        paymentDate: Date,
    ): List<Extra> {
        val prevKey = DateFormats.formatDate(previousPaymentDate) ?: "2010-01-01"
        val paymentKey = DateFormats.formatDate(paymentDate) ?: return emptyList()

        return extras
            .filter { extra ->
                val extraKey = DateFormats.formatDate(extra.date) ?: return@filter false
                extraKey >= prevKey && extraKey < paymentKey
            }
            .sortedWith(
                compareByDescending<Extra> { DateFormats.formatDate(it.date) }
                    .thenByDescending { it.id },
            )
    }
}

fun formatExtraAmount(extra: Extra): String {
    return if (extra.type == ExtraType.CHANGE_RATE) {
        Formatters.schedulePercent(extra.amount.toDouble())
    } else {
        Formatters.moneyFixed(extra.amount)
    }
}
