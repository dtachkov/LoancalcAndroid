package com.example.loancalcandroid.ui.tax

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.util.Formatters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.CalculationErrors
import ru.kredit.calculator.data.calculation.TaxCalculationResult
import ru.kredit.calculator.data.model.Loan

data class TaxYearTableRow(
    val year: Int,
    val cumulativeReturn: String,
    val cumulativeSalaryTax: String,
    val restForReturn: String,
)

data class TaxUiState(
    val isLoading: Boolean = true,
    val isCalculating: Boolean = false,
    val loanAmount: String = "",
    val objectPrice: String = "1000000",
    val salary: String = "60000",
    val principalTax: String? = null,
    val interestTax: String? = null,
    val totalTax: String? = null,
    val tableRows: List<TaxYearTableRow> = emptyList(),
    val objectPriceError: String? = null,
    val error: String? = null,
    val reviewRequestTrigger: Int = 0,
)

class TaxViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val featureCalculators = LoanCalcData.get().featureCalculators

    private var loan: Loan? = null

    private val _uiState = MutableStateFlow(TaxUiState())
    val uiState: StateFlow<TaxUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateObjectPrice(value: String) =
        _uiState.update { it.copy(objectPrice = value, objectPriceError = null) }

    fun updateSalary(value: String) = _uiState.update { it.copy(salary = value) }

    fun calculate() {
        val currentLoan = loan ?: return
        val state = _uiState.value
        val objectPrice = Formatters.parseMoney(state.objectPrice).toDouble()
        val loanAmount = Formatters.parseMoney(state.loanAmount).toDouble()

        if (objectPrice < loanAmount) {
            _uiState.update {
                it.copy(objectPriceError = "Сумма кредита не должна быть больше цены недвижимости")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, error = null, objectPriceError = null) }
            try {
                val extras = extraRepository.getExtras(loanId)
                val taxResult = withContext(Dispatchers.Default) {
                    featureCalculators.calculateTax(currentLoan, extras, objectPrice)
                }
                val salaryPerMonth = Formatters.parseMoney(state.salary).toDouble()
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        principalTax = Formatters.money(taxResult.principalTax),
                        interestTax = Formatters.money(taxResult.interestTax),
                        totalTax = Formatters.money(taxResult.totalReturnTax),
                        tableRows = buildTableRows(taxResult, salaryPerMonth),
                        reviewRequestTrigger = it.reviewRequestTrigger + 1,
                    )
                }
                AnalyticsHelper.logEvent("CALC_RETURN_TAX")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isCalculating = false, error = CalculationErrors.format(e))
                }
            }
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
            _uiState.update {
                it.copy(
                    isLoading = false,
                    loanAmount = Formatters.money(loaded.amount),
                )
            }
        }
    }

    private fun buildTableRows(
        taxResult: TaxCalculationResult,
        salaryPerMonth: Double,
    ): List<TaxYearTableRow> {
        var cumulativeReturn = taxResult.principalTax
        var cumulativeSalaryTax = 12 * salaryPerMonth * 0.13

        return taxResult.taxPayments.map { payment ->
            cumulativeReturn += payment.returnTax
            val row = TaxYearTableRow(
                year = payment.year,
                cumulativeReturn = Formatters.money(cumulativeReturn),
                cumulativeSalaryTax = Formatters.money(cumulativeSalaryTax),
                restForReturn = Formatters.money(cumulativeReturn - cumulativeSalaryTax),
            )
            cumulativeSalaryTax += 0.13 * 12 * salaryPerMonth
            row
        }
    }
}
