package com.example.loancalcandroid.ui.requisites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.model.LoanDetails

data class RequisitesUiState(
    val isLoading: Boolean = true,
    val bankName: String = "",
    val accountNumber: String = "",
    val uic: String = "",
    val correspondentAccount: String = "",
    val paymentComment: String = "",
    val isSaving: Boolean = false,
)

class RequisitesViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val _uiState = MutableStateFlow(RequisitesUiState())
    val uiState: StateFlow<RequisitesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val details = loanRepository.getLoanDetails(loanId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    bankName = details.bankName,
                    accountNumber = details.accountNumber,
                    uic = details.uic,
                    correspondentAccount = details.correspondentAccount,
                    paymentComment = details.paymentComment.orEmpty(),
                )
            }
        }
    }

    fun updateBankName(value: String) = _uiState.update { it.copy(bankName = value) }

    fun updateAccountNumber(value: String) = _uiState.update { it.copy(accountNumber = value) }

    fun updateUic(value: String) = _uiState.update { it.copy(uic = value) }

    fun updateCorrespondentAccount(value: String) = _uiState.update { it.copy(correspondentAccount = value) }

    fun updatePaymentComment(value: String) = _uiState.update { it.copy(paymentComment = value) }

    fun save() {
        val state = _uiState.value
        if (state.isLoading || state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            loanRepository.saveLoanDetails(
                loanId = loanId,
                details = LoanDetails(
                    bankName = state.bankName.trim(),
                    accountNumber = state.accountNumber.trim(),
                    uic = state.uic.trim(),
                    correspondentAccount = state.correspondentAccount.trim(),
                    paymentComment = state.paymentComment.trim().takeIf { it.isNotEmpty() },
                ),
            )
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}
