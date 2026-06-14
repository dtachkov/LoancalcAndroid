package com.example.loancalcandroid.ui.loan

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
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.util.Calendar
import java.util.Date

data class LoanEditorUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val title: String = "",
    val amount: String = "",
    val rate: String = "",
    val term: String = "",
    val loanType: LoanType = LoanType.ANNUITY,
    val firstPaymentDate: Date? = null,
    val dateOfIssue: Date? = null,
    val considerDaysOff: Boolean = false,
    val payOnLastDayOfMonth: Boolean = false,
    val applyExtrasImmediately: Boolean = false,
    val calculateExtrasByBalanceLikeSberbank: Boolean = true,
    val ignorePassedPeriodsAfterRateChange: Boolean = false,
    val extraDayInMonth: Boolean = false,
    val showAdvanced: Boolean = false,
    val amountError: String? = null,
    val rateError: String? = null,
    val termError: String? = null,
    val dateError: String? = null,
    val saveError: String? = null,
    val savedLoanId: Long? = null,
)

class LoanEditorViewModel(
    application: Application,
    private val loanId: Long?,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private val _uiState = MutableStateFlow(LoanEditorUiState(isEditMode = loanId != null))
    val uiState: StateFlow<LoanEditorUiState> = _uiState.asStateFlow()

    init {
        if (loanId != null) {
            loadLoan(loanId)
        } else {
            val today = Date()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    firstPaymentDate = today.clearTime(),
                )
            }
        }
    }

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value) }
    fun updateAmount(value: String) = _uiState.update { it.copy(amount = value, amountError = null) }
    fun updateRate(value: String) = _uiState.update { it.copy(rate = value, rateError = null) }
    fun updateTerm(value: String) = _uiState.update { it.copy(term = value, termError = null) }
    fun updateLoanType(type: LoanType) = _uiState.update { it.copy(loanType = type) }
    fun updateFirstPaymentDate(date: Date) = _uiState.update { it.copy(firstPaymentDate = date, dateError = null) }
    fun updateDateOfIssue(date: Date?) = _uiState.update { it.copy(dateOfIssue = date) }
    fun toggleConsiderDaysOff() = _uiState.update { it.copy(considerDaysOff = !it.considerDaysOff) }
    fun togglePayOnLastDayOfMonth() = _uiState.update { it.copy(payOnLastDayOfMonth = !it.payOnLastDayOfMonth) }
    fun toggleApplyExtrasImmediately() = _uiState.update { it.copy(applyExtrasImmediately = !it.applyExtrasImmediately) }
    fun toggleCalculateExtrasByBalanceLikeSberbank() =
        _uiState.update { it.copy(calculateExtrasByBalanceLikeSberbank = !it.calculateExtrasByBalanceLikeSberbank) }
    fun toggleIgnorePassedPeriodsAfterRateChange() =
        _uiState.update { it.copy(ignorePassedPeriodsAfterRateChange = !it.ignorePassedPeriodsAfterRateChange) }
    fun toggleExtraDayInMonth() = _uiState.update { it.copy(extraDayInMonth = !it.extraDayInMonth) }
    fun toggleAdvanced() = _uiState.update { it.copy(showAdvanced = !it.showAdvanced) }

    fun save() {
        val state = _uiState.value
        if (!validate(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            try {
                val loan = buildLoan(state)
                val extras = if (loan.id != 0L) extraRepository.getExtras(loan.id) else emptyList()
                val monthlyPayment = withContext(Dispatchers.Default) {
                    runCatching { loanCalculator.calculate(loan, extras).currentPayment.toFloat() }
                        .getOrDefault(0f)
                }
                val saved = loanRepository.saveLoan(loan.copy(monthlyPayment = monthlyPayment))
                _uiState.update { it.copy(isSaving = false, savedLoanId = saved.id) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, saveError = e.message ?: "Ошибка сохранения")
                }
            }
        }
    }

    private fun loadLoan(id: Long) {
        viewModelScope.launch {
            val loan = loanRepository.getLoan(id)
            if (loan == null) {
                _uiState.update { it.copy(isLoading = false, saveError = "Кредит не найден") }
                return@launch
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    title = loan.title.orEmpty(),
                    amount = formatInputNumber(loan.amount),
                    rate = formatInputNumber(loan.rate),
                    term = loan.term.toString(),
                    loanType = loan.type,
                    firstPaymentDate = loan.firstPaymentDate,
                    dateOfIssue = loan.dateOfIssue,
                    considerDaysOff = loan.considerDaysOff,
                    payOnLastDayOfMonth = loan.payOnLastDayOfMonth,
                    applyExtrasImmediately = loan.applyExtrasImmediately,
                    calculateExtrasByBalanceLikeSberbank = loan.calculateExtrasByBalanceLikeSberbank,
                    ignorePassedPeriodsAfterRateChange = loan.ignorePassedPeriodsAfterRateChange,
                    extraDayInMonth = loan.extraDayInMonth,
                )
            }
        }
    }

    private fun validate(state: LoanEditorUiState): Boolean {
        var valid = true
        var amountError: String? = null
        var rateError: String? = null
        var termError: String? = null
        var dateError: String? = null

        val amount = com.example.loancalcandroid.util.Formatters.parseMoney(state.amount)
        if (amount <= 0f) {
            amountError = "Укажите сумму кредита"
            valid = false
        }
        val rate = com.example.loancalcandroid.util.Formatters.parsePercent(state.rate)
        if (rate <= 0f) {
            rateError = "Укажите ставку"
            valid = false
        }
        val term = com.example.loancalcandroid.util.Formatters.parseInt(state.term)
        if (term <= 0) {
            termError = "Укажите срок"
            valid = false
        }
        if (state.firstPaymentDate == null) {
            dateError = "Укажите дату первого платежа"
            valid = false
        }

        _uiState.update {
            it.copy(
                amountError = amountError,
                rateError = rateError,
                termError = termError,
                dateError = dateError,
            )
        }
        return valid
    }

    private fun buildLoan(state: LoanEditorUiState): Loan {
        return Loan(
            id = loanId ?: 0L,
            title = state.title.ifBlank { null },
            amount = com.example.loancalcandroid.util.Formatters.parseMoney(state.amount),
            rate = com.example.loancalcandroid.util.Formatters.parsePercent(state.rate),
            term = com.example.loancalcandroid.util.Formatters.parseInt(state.term),
            type = state.loanType,
            firstPaymentDate = state.firstPaymentDate,
            dateOfIssue = state.dateOfIssue,
            considerDaysOff = state.considerDaysOff,
            payOnLastDayOfMonth = state.payOnLastDayOfMonth,
            applyExtrasImmediately = state.applyExtrasImmediately,
            calculateExtrasByBalanceLikeSberbank = state.calculateExtrasByBalanceLikeSberbank,
            ignorePassedPeriodsAfterRateChange = state.ignorePassedPeriodsAfterRateChange,
            extraDayInMonth = state.extraDayInMonth,
        )
    }

    private fun formatInputNumber(value: Float): String {
        return if (value % 1f == 0f) value.toInt().toString() else value.toString()
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
}
