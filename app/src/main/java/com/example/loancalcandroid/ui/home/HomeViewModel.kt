package com.example.loancalcandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.R
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.ui.home.mapper.LoanPresentationMapper
import com.example.loancalcandroid.ui.home.model.AllLoansSummaryUiModel
import com.example.loancalcandroid.ui.home.model.LoanCardUiModel
import com.example.loancalcandroid.ui.home.model.LoanDetailsUiModel
import com.example.loancalcandroid.util.Formatters
import com.example.loancalcandroid.util.SpokenLoanParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.calculation.LoanCalculationResult
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.util.Calendar
import java.util.Date

data class HomeUiState(
    val loanCards: List<LoanCardUiModel> = emptyList(),
    val allLoansSummary: AllLoansSummaryUiModel? = null,
    val selectedLoanId: Long? = null,
    val pagerIndex: Int = 0,
    val loanDetails: LoanDetailsUiModel? = null,
    val isLoading: Boolean = true,
    val loansRaw: List<Loan> = emptyList(),
    val spokenLoanError: String? = null,
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

    private var hasResolvedInitialPager = false

    init {
        viewModelScope.launch {
            combine(
                loanRepository.observeLoans(),
                extraRepository.observeExtrasChanges(),
            ) { loans, _ -> loans }
                .collect { loans ->
                val calculations = calculateLoans(loans)
                val cards = loans.map { loan ->
                    LoanPresentationMapper.toCard(loan, calculations.results[loan.id])
                }
                val summary = LoanPresentationMapper.toAllLoansSummary(loans, calculations.results)
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

    fun globalFeatureLoanId(): Long? {
        val loans = _uiState.value.loansRaw
        if (loans.isEmpty()) return null
        val lastId = chestPreferences.getLastCalculatedLoanId()
        if (lastId > 0 && loans.any { it.id == lastId }) return lastId
        return loans.first().id
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

    fun createLoanFromSpokenPhrase(phrase: String) {
        val parsed = SpokenLoanParser.parse(phrase)
        val amount = parsed.amount?.let(Formatters::parseMoney) ?: 0f
        val rate = parsed.rate?.let(Formatters::parsePercent) ?: 0f
        val term = parsed.termMonths?.let(Formatters::parseInt) ?: 0
        if (amount <= 0f || rate <= 0f || term <= 0) {
            AnalyticsHelper.logEvent("ERROR_SPEECH_LOAN", phrase)
            _uiState.update {
                it.copy(
                    spokenLoanError = getApplication<Application>().getString(
                        R.string.speech_loan_parse_error,
                        phrase,
                    ),
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val loan = Loan(
                    title = phrase,
                    amount = amount,
                    rate = rate,
                    term = term,
                    type = LoanType.ANNUITY,
                    firstPaymentDate = Date().clearTime(),
                )
                val monthlyPayment = withContext(Dispatchers.Default) {
                    loanCalculator.calculate(loan, emptyList()).currentPayment.toFloat()
                }
                val saved = loanRepository.saveLoan(loan.copy(monthlyPayment = monthlyPayment))
                AnalyticsHelper.logCalculation(loan.amount, "HomeViewModel")
                selectLoan(saved.id)
            } catch (e: Exception) {
                AnalyticsHelper.logEvent("ERROR_SPEECH_LOAN", phrase)
                _uiState.update {
                    it.copy(spokenLoanError = CalculationErrors.format(e))
                }
            }
        }
    }

    fun consumeSpokenLoanError() {
        _uiState.update { it.copy(spokenLoanError = null) }
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

    private data class LoanCalculations(
        val results: Map<Long, LoanCalculationResult>,
        val forecastErrorIds: Set<Long>,
    )

    private suspend fun calculateLoans(loans: List<Loan>): LoanCalculations {
        val extrasByLoan = buildMap {
            for (loan in loans) {
                if (!loan.validate()) continue
                put(loan.id, extraRepository.getExtras(loan.id))
            }
        }
        return withContext(Dispatchers.Default) {
            val results = linkedMapOf<Long, LoanCalculationResult>()
            val forecastErrorIds = linkedSetOf<Long>()
            for (loan in loans) {
                if (!loan.validate()) continue
                runCatching {
                    loanCalculator.calculate(loan, extrasByLoan[loan.id].orEmpty())
                }.onSuccess { results[loan.id] = it }
                    .onFailure { error ->
                        if (CalculationErrors.isExtraForecastError(error)) {
                            forecastErrorIds += loan.id
                        }
                    }
            }
            LoanCalculations(results = results, forecastErrorIds = forecastErrorIds)
        }
    }

    private fun loadLoanDetails(
        loanId: Long?,
        calculations: LoanCalculations,
    ) {
        if (loanId == null) {
            _uiState.update { it.copy(loanDetails = null) }
            return
        }
        val loan = _uiState.value.loansRaw.firstOrNull { it.id == loanId }
        if (loan == null) {
            _uiState.update { it.copy(loanDetails = null) }
            return
        }
        val calculation = calculations.results[loanId]
        viewModelScope.launch {
            val extras = extraRepository.getExtras(loanId)
            val details = if (calculation != null) {
                LoanPresentationMapper.toDetails(loan, extras, calculation)
            } else {
                LoanPresentationMapper.toUnavailableDetails(
                    loan = loan,
                    extras = extras,
                    forecastError = loanId in calculations.forecastErrorIds || loan.isForecastActive,
                )
            }
            _uiState.update { it.copy(loanDetails = details) }
        }
    }

    private fun resolvePagerIndex(
        loans: List<Loan>,
        previousPagerIndex: Int,
        previousSelectedId: Long?,
    ): Int {
        if (loans.isEmpty()) {
            hasResolvedInitialPager = false
            return 0
        }

        if (previousSelectedId != null) {
            val loanIndex = loans.indexOfFirst { it.id == previousSelectedId }
            if (loanIndex >= 0) return loanIndex + 1
        }

        if (!hasResolvedInitialPager) {
            hasResolvedInitialPager = true
            if (data.settingsPreferences.isLoadLastLoanAtStart()) {
                val lastLoanId = chestPreferences.getLastCalculatedLoanId()
                if (lastLoanId > 0) {
                    val loanIndex = loans.indexOfFirst { it.id == lastLoanId }
                    if (loanIndex >= 0) return loanIndex + 1
                }
            }
            return 1
        }

        return previousPagerIndex.coerceIn(0, loans.size)
    }

    private fun pagerIndexToLoanId(page: Int, loans: List<Loan>): Long? {
        if (page <= 0) return null
        return loans.getOrNull(page - 1)?.id
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
