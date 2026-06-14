package com.example.loancalcandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.ui.home.mapper.LoanPresentationMapper
import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.LoanCalculationResult
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
    private val data = LoanCalcData.get()
    private val loanRepository = data.loanRepository
    private val extraRepository = data.extraRepository
    private val chestPreferences = data.chestPreferences
    private val loanCalculator = data.loanCalculator

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loanRepository.observeLoans().collect { loans ->
                val calculations = calculateLoans(loans)
                val cards = loans.map { loan ->
                    LoanPresentationMapper.toCard(loan, calculations[loan.id])
                }
                val summary = LoanPresentationMapper.toAllLoansSummary(loans, calculations)
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
                loadLoanDetails(selectedId, calculations)
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
        viewModelScope.launch {
            val calculations = calculateLoans(loans)
            loadLoanDetails(selectedId, calculations)
        }
    }

    fun selectLoan(loanId: Long) {
        chestPreferences.setLastCalculatedLoanId(loanId)
        val loans = _uiState.value.loansRaw
        val index = loans.indexOfFirst { it.id == loanId }
        if (index >= 0) {
            onPagerPageChanged(index + 1)
        } else {
            _uiState.update { it.copy(selectedLoanId = loanId) }
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

    private suspend fun calculateLoans(loans: List<Loan>): Map<Long, LoanCalculationResult> {
        val extrasByLoan = buildMap {
            for (loan in loans) {
                if (!loan.validate()) continue
                put(loan.id, extraRepository.getExtras(loan.id))
            }
        }
        return withContext(Dispatchers.Default) {
            buildMap {
                for (loan in loans) {
                    if (!loan.validate()) continue
                    runCatching {
                        loanCalculator.calculate(loan, extrasByLoan[loan.id].orEmpty())
                    }.onSuccess { put(loan.id, it) }
                }
            }
        }
    }

    private fun loadLoanDetails(
        loanId: Long?,
        calculations: Map<Long, LoanCalculationResult>,
    ) {
        if (loanId == null) {
            _uiState.update { it.copy(loanDetails = null) }
            return
        }
        val loan = _uiState.value.loansRaw.firstOrNull { it.id == loanId }
        val calculation = calculations[loanId]
        if (loan == null || calculation == null) {
            _uiState.update { it.copy(loanDetails = null) }
            return
        }
        viewModelScope.launch {
            val extras = extraRepository.getExtras(loanId)
            _uiState.update {
                it.copy(loanDetails = LoanPresentationMapper.toDetails(loan, extras, calculation))
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
        if (data.settingsPreferences.isLoadLastLoanAtStart()) {
            val lastLoanId = chestPreferences.getLastCalculatedLoanId()
            if (lastLoanId > 0) {
                val loanIndex = loans.indexOfFirst { it.id == lastLoanId }
                if (loanIndex >= 0) return loanIndex + 1
            }
        }
        return previousPagerIndex.coerceIn(0, loans.size)
    }

    private fun pagerIndexToLoanId(page: Int, loans: List<Loan>): Long? {
        if (page <= 0) return null
        return loans.getOrNull(page - 1)?.id
    }
}
