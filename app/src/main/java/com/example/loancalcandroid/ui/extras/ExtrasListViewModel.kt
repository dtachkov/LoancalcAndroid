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
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan

data class ExtraListItem(
    val extra: Extra,
    val typeLabel: String,
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
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository
    private val extraRepository = LoanCalcData.get().extraRepository
    private val loanCalculator = LoanCalcData.get().loanCalculator

    private val savedMoney = MutableStateFlow(0.0)

    val uiState: StateFlow<ExtrasListUiState> = combine(
        loanRepository.observeLoans(),
        extraRepository.observeExtras(loanId),
        savedMoney,
    ) { loans, extras, savings ->
        val loan = loans.firstOrNull { it.id == loanId }
        ExtrasListUiState(
            loan = loan,
            items = extras.sortedByDescending { it.date }.map { extraToItem(it) },
            savedMoney = savings,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExtrasListUiState())

    init {
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

    fun deleteExtra(extraId: Long) {
        viewModelScope.launch {
            extraRepository.deleteExtra(loanId, extraId)
        }
    }

    private fun extraToItem(extra: Extra): ExtraListItem {
        return ExtraListItem(
            extra = extra,
            typeLabel = ExtraTypeLabels.label(extra.type),
            amountLabel = if (extra.type == ExtraType.CHANGE_RATE) {
                "${com.example.loancalcandroid.util.Formatters.money(extra.amount)} %"
            } else {
                com.example.loancalcandroid.util.Formatters.money(extra.amount)
            },
            dateLabel = com.example.loancalcandroid.util.Formatters.date(extra.date),
        )
    }
}

object ExtraTypeLabels {
    fun label(type: ExtraType): String = when (type) {
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT -> "Уменьшение суммы"
        ExtraType.CHANGE_RATE -> "Изменение ставки"
        ExtraType.PAYMENT_FOR_DECREASE_TERM -> "Уменьшение срока"
        ExtraType.INSURANCE -> "Страховка"
        ExtraType.FEE -> "Комиссия"
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY -> "Ежемес. уменьшение суммы"
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY -> "Ежемес. уменьшение срока"
        ExtraType.PAYMENT_FOR_CHANGE_DATE -> "Изменение даты платежа"
    }

    val earlyPaymentTypes = listOf(
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
        ExtraType.PAYMENT_FOR_DECREASE_TERM,
        ExtraType.CHANGE_RATE,
        ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT_MONTHLY,
        ExtraType.PAYMENT_FOR_DECREASE_TERM_MONTHLY,
        ExtraType.PAYMENT_FOR_CHANGE_DATE,
    )

    val commissionTypes = listOf(
        ExtraType.FEE,
        ExtraType.INSURANCE,
    )
}
