package com.example.loancalcandroid.ui.extras

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan

data class ExtraListItem(
    val extra: Extra,
    val typeLabel: String,
    val documentNumberLabel: String,
    val amountLabel: String,
    val dateLabel: String,
)

data class ExtrasListUiState(
    val loan: Loan? = null,
    val items: List<ExtraListItem> = emptyList(),
    val savedMoney: Double = 0.0,
    val isLoading: Boolean = true,
)

class ExtrasListViewModel(
    application: Application,
    private val loanId: Long,
    private val category: ExtraCategory,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private val allowedTypes: Set<ExtraType> = when (category) {
        ExtraCategory.EARLY -> ExtraTypeUtils.earlyPaymentTypes.toSet()
        ExtraCategory.COMMISSION -> ExtraTypeUtils.commissionTypes.toSet()
    }

    private val savedMoney = MutableStateFlow(0.0)

    val uiState: StateFlow<ExtrasListUiState> = combine(
        loanRepository.observeLoans(),
        extraRepository.observeExtras(loanId),
        savedMoney,
    ) { loans, extras, savings ->
        val loan = loans.firstOrNull { it.id == loanId }
        val filtered = extras.filter { it.type in allowedTypes }
        ExtrasListUiState(
            loan = loan,
            items = filtered.sortedByDescending { it.date }.map { extraToItem(it) },
            savedMoney = if (category == ExtraCategory.EARLY) savings else 0.0,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExtrasListUiState())

    init {
        if (category == ExtraCategory.EARLY) {
            viewModelScope.launch {
                combine(
                    loanRepository.observeLoans(),
                    extraRepository.observeExtras(loanId),
                ) { loans, extras ->
                    val loan = loans.firstOrNull { it.id == loanId }
                    if (loan != null && loan.validate()) {
                        withContext(Dispatchers.Default) {
                            runCatching { loanCalculator.calculate(loan, extras).savedMoney }
                                .getOrDefault(0.0)
                        }
                    } else {
                        0.0
                    }
                }.collect { savings ->
                    savedMoney.value = savings
                }
            }
        }
    }

    fun deleteExtra(extraId: Long) {
        viewModelScope.launch {
            extraRepository.deleteExtra(loanId, extraId)
        }
    }

    private fun extraToItem(extra: Extra): ExtraListItem {
        return ExtraListItem(
            extra = extra,
            typeLabel = ExtraTypeUtils.label(extra.type),
            documentNumberLabel = extra.documentNumber.orEmpty().ifBlank { "—" },
            amountLabel = if (extra.type == ExtraType.CHANGE_RATE) {
                "${com.example.loancalcandroid.util.Formatters.moneyFixed(extra.amount)} %"
            } else if (extra.type == ExtraType.PAYMENT_FOR_CHANGE_DATE) {
                ""
            } else {
                com.example.loancalcandroid.util.Formatters.moneyFixed(extra.amount)
            },
            dateLabel = com.example.loancalcandroid.util.Formatters.date(extra.date),
        )
    }
}
