package com.example.loancalcandroid.ui.extras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import java.util.Date

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
)

class ExtraFormViewModel(
    application: Application,
    private val loanId: Long,
    private val extraId: Long?,
    category: ExtraCategory,
) : AndroidViewModel(application) {
    private val extraRepository = LoanCalcData.get().extraRepository
    private val allowedTypes = when (category) {
        ExtraCategory.EARLY -> ExtraTypeUtils.earlyPaymentTypes
        ExtraCategory.COMMISSION -> ExtraTypeUtils.commissionTypes
    }

    private val _uiState = MutableStateFlow(
        ExtraFormUiState(
            isEditMode = extraId != null,
            category = category,
            allowedTypes = allowedTypes,
            selectedType = allowedTypes.first(),
            date = Date(),
        ),
    )
    val uiState: StateFlow<ExtraFormUiState> = _uiState.asStateFlow()

    init {
        if (extraId != null) {
            loadExtra(extraId)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectType(type: ExtraType) = _uiState.update { it.copy(selectedType = type, amountError = null) }
    fun updateDocumentNumber(value: String) = _uiState.update { it.copy(documentNumber = value) }
    fun updateAmount(value: String) = _uiState.update { it.copy(amount = value, amountError = null) }
    fun updateDate(date: Date) = _uiState.update { it.copy(date = date, dateError = null) }

    fun save() {
        val state = _uiState.value
        if (!validate(state)) return

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
