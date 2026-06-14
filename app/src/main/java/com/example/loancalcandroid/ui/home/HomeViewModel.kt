package com.example.loancalcandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.ui.home.mapper.LoanPresentationMapper
import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.model.Loan

data class HomeUiState(
    val loanCards: List<LoanCardUiModel> = emptyList(),
    val allLoansSummary: AllLoansSummaryUiModel? = null,
    val selectedLoanId: Long? = null,
    val pagerIndex: Int = 0,
    val loanDetails: LoanDetailsUiModel? = null,
    val isLoading: Boolean = true,
    val loansRaw: List<Loan> = emptyList(),
)

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val chestPreferences = LoanCalcData.get().chestPreferences

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loanRepository.observeLoans().collect { loans ->
                val cards = loans.map(LoanPresentationMapper::toCard)
                val summary = LoanPresentationMapper.toAllLoansSummary(loans)
                val current = _uiState.value
                val pagerIndex = resolvePagerIndex(
                    loans = loans,
                    previousPagerIndex = current.pagerIndex,
                    previousSelectedId = current.selectedLoanId,
                )
                val selectedId = pagerIndexToLoanId(pagerIndex, loans)

                _uiState.update {
                    it.copy(
                        loanCards = cards,
                        allLoansSummary = summary,
                        loansRaw = loans,
                        pagerIndex = pagerIndex,
                        selectedLoanId = selectedId,
                        isLoading = false,
                    )
                }
                loadLoanDetails(selectedId)
            }
        }
    }

    fun onPagerPageChanged(page: Int) {
        val loans = _uiState.value.loansRaw
        val selectedId = pagerIndexToLoanId(page, loans)
        _uiState.update {
            it.copy(
                pagerIndex = page,
                selectedLoanId = selectedId,
            )
        }
        selectedId?.let { chestPreferences.setLastCalculatedLoanId(it) }
        loadLoanDetails(selectedId)
    }

    fun selectLoan(loanId: Long) {
        val loans = _uiState.value.loansRaw
        val index = loans.indexOfFirst { it.id == loanId }
        if (index >= 0) {
            onPagerPageChanged(index + 1)
        }
    }

    fun duplicateSelectedLoan() {
        val loanId = _uiState.value.selectedLoanId ?: return
        val loan = _uiState.value.loansRaw.firstOrNull { it.id == loanId } ?: return
        viewModelScope.launch {
            val duplicate = loan.copy(
                id = 0,
                title = "${loan.title.orEmpty()} (копия)",
            )
            val saved = loanRepository.saveLoan(duplicate)
            selectLoan(saved.id)
        }
    }

    fun deleteSelectedLoan() {
        val loanId = _uiState.value.selectedLoanId ?: return
        viewModelScope.launch {
            loanRepository.deleteLoan(loanId)
            _uiState.update {
                it.copy(
                    pagerIndex = 0,
                    selectedLoanId = null,
                    loanDetails = null,
                )
            }
        }
    }

    private fun loadLoanDetails(loanId: Long?) {
        if (loanId == null) {
            _uiState.update { it.copy(loanDetails = null) }
            return
        }
        viewModelScope.launch {
            val loan = loanRepository.getLoan(loanId) ?: return@launch
            val extras = extraRepository.getExtras(loanId)
            _uiState.update {
                it.copy(loanDetails = LoanPresentationMapper.toDetails(loan, extras))
            }
        }
    }

    private fun resolvePagerIndex(
        loans: List<Loan>,
        previousPagerIndex: Int,
        previousSelectedId: Long?,
    ): Int {
        if (loans.isEmpty()) return 0
        if (previousSelectedId != null) {
            val loanIndex = loans.indexOfFirst { it.id == previousSelectedId }
            if (loanIndex >= 0) return loanIndex + 1
        }
        val lastLoanId = chestPreferences.getLastCalculatedLoanId()
        if (lastLoanId > 0) {
            val loanIndex = loans.indexOfFirst { it.id == lastLoanId }
            if (loanIndex >= 0) return loanIndex + 1
        }
        return previousPagerIndex.coerceIn(0, loans.size)
    }

    private fun pagerIndexToLoanId(page: Int, loans: List<Loan>): Long? {
        if (page <= 0) return null
        return loans.getOrNull(page - 1)?.id
    }
}
