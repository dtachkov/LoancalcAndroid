package com.example.loancalcandroid.ui.extras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.calculation.ExtraInterestCalculator
import ru.kredit.calculator.data.calculation.LoanCalculator
import ru.kredit.calculator.data.calculation.PaymentSummary
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import java.util.Date

data class ExtraFormPrefill(
    val amount: String = "",
    val dateMillis: Long = 0L,
    val extraType: ExtraType? = null,
)

data class ExtraFormUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val category: ExtraCategory = ExtraCategory.EARLY,
    val allowedTypes: List<ExtraType> = ExtraTypeUtils.earlyPaymentTypes,
    val selectedType: ExtraType = ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
    val documentNumber: String = "",
    val amount: String = "",
    val date: Date? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val saveError: String? = null,
    val saved: Boolean = false,
    val showInterestBreakdown: Boolean = false,
    val interestToPay: Double = 0.0,
    val netExtraAmount: Double = 0.0,
    val netExtraIsError: Boolean = false,
)

class ExtraFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val extraId: Long?,
    category: ExtraCategory,
    prefill: ExtraFormPrefill = ExtraFormPrefill(),
) : AndroidViewModel(application) {
    private val loanId: Long = savedStateHandle.get<Long>(Route.ARG_LOAN_ID) ?: 0L
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanRepository = LoanCalcData.get().loanRepository
    private val loanCalculator = LoanCalculator()
    private var loan: Loan? = null
    private var schedulePayments: List<PaymentSummary> = emptyList()
    private var extrasForCalculation: List<Extra> = emptyList()
    private val allowedTypes = when (category) {
        ExtraCategory.EARLY -> ExtraTypeUtils.earlyPaymentTypes
        ExtraCategory.COMMISSION -> ExtraTypeUtils.commissionTypes
    }

    private val _uiState = MutableStateFlow(
        ExtraFormUiState(
            isEditMode = extraId != null,
            category = category,
            allowedTypes = allowedTypes,
            selectedType = prefill.extraType ?: allowedTypes.first(),
            amount = prefill.amount,
            date = if (prefill.dateMillis > 0L) Date(prefill.dateMillis) else Date(),
        ),
    )
    val uiState: StateFlow<ExtraFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCalculationContext()
            if (extraId != null) {
                loadExtra(extraId)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                recalculateBreakdown()
            }
        }
    }

    fun selectType(type: ExtraType) {
        _uiState.update { it.copy(selectedType = type, amountError = null) }
        recalculateBreakdown()
    }

    fun updateDocumentNumber(value: String) = _uiState.update { it.copy(documentNumber = value) }

    fun updateAmount(value: String) {
        _uiState.update { it.copy(amount = value, amountError = null) }
        recalculateBreakdown()
    }

    fun updateDate(date: Date) {
        _uiState.update { it.copy(date = date, dateError = null) }
        recalculateBreakdown()
    }

    fun save() {
        val state = _uiState.value
        if (!validate(state)) return
        if (loanId == 0L) {
            _uiState.update { it.copy(saveError = "Не выбран кредит для сохранения") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            try {
                val extra = Extra(
                    id = extraId ?: 0L,
                    loanId = loanId,
                    type = state.selectedType,
                    amount = com.example.loancalcandroid.util.Formatters.parseMoney(state.amount),
                    date = state.date,
                    documentNumber = state.documentNumber.ifBlank { null },
                )
                extraRepository.saveExtra(extra)
                _uiState.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, saveError = e.message ?: "Ошибка сохранения")
                }
            }
        }
    }

    private suspend fun loadCalculationContext() {
        val loadedLoan = loanRepository.getLoan(loanId) ?: return
        val allExtras = extraRepository.getExtras(loanId)
        extrasForCalculation = if (extraId != null) {
            allExtras.filter { it.id != extraId }
        } else {
            allExtras
        }
        loan = loadedLoan
        schedulePayments = try {
            loanCalculator.calculate(loadedLoan, extrasForCalculation).payments
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun recalculateBreakdown() {
        val state = _uiState.value
        val currentLoan = loan
        val showBreakdown = currentLoan != null &&
            state.category == ExtraCategory.EARLY &&
            currentLoan.applyExtrasImmediately &&
            state.selectedType != ExtraType.CHANGE_RATE &&
            state.selectedType != ExtraType.PAYMENT_FOR_CHANGE_DATE

        if (!showBreakdown || state.date == null) {
            _uiState.update {
                it.copy(
                    showInterestBreakdown = false,
                    interestToPay = 0.0,
                    netExtraAmount = 0.0,
                    netExtraIsError = false,
                )
            }
            return
        }

        val interest = ExtraInterestCalculator.interestForExtraDate(
            loan = currentLoan,
            payments = schedulePayments,
            extras = extrasForCalculation,
            extraDate = state.date,
        )
        val extraAmount = com.example.loancalcandroid.util.Formatters.parseMoney(state.amount).toDouble()
        val netExtra = extraAmount - interest
        _uiState.update {
            it.copy(
                showInterestBreakdown = true,
                interestToPay = interest,
                netExtraAmount = if (netExtra > 0) netExtra else 0.0,
                netExtraIsError = netExtra <= 0,
            )
        }
    }

    private fun loadExtra(id: Long) {
        viewModelScope.launch {
            val extras = extraRepository.getExtras(loanId)
            val extra = extras.firstOrNull { it.id == id }
            if (extra == null) {
                _uiState.update { it.copy(isLoading = false, saveError = "Запись не найдена") }
                return@launch
            }
            val category = if (ExtraTypeUtils.isCommission(extra.type)) {
                ExtraCategory.COMMISSION
            } else {
                ExtraCategory.EARLY
            }
            val types = when (category) {
                ExtraCategory.EARLY -> ExtraTypeUtils.earlyPaymentTypes
                ExtraCategory.COMMISSION -> ExtraTypeUtils.commissionTypes
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    category = category,
                    allowedTypes = types,
                    selectedType = extra.type,
                    documentNumber = extra.documentNumber.orEmpty(),
                    amount = formatAmount(extra),
                    date = extra.date,
                )
            }
            recalculateBreakdown()
        }
    }

    private fun formatAmount(extra: Extra): String {
        return if (extra.amount % 1f == 0f) {
            extra.amount.toInt().toString()
        } else {
            extra.amount.toString()
        }
    }

    private fun validate(state: ExtraFormUiState): Boolean {
        var valid = true
        var amountError: String? = null
        var dateError: String? = null

        val amount = com.example.loancalcandroid.util.Formatters.parseMoney(state.amount)
        if (amount <= 0f) {
            amountError = if (state.selectedType == ExtraType.CHANGE_RATE) {
                "Укажите новую ставку"
            } else {
                "Укажите сумму"
            }
            valid = false
        }
        if (state.date == null) {
            dateError = "Укажите дату"
            valid = false
        }

        _uiState.update { it.copy(amountError = amountError, dateError = dateError) }
        return valid
    }
}
