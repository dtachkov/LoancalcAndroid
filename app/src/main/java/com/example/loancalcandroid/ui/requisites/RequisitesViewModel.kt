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
import ru.kredit.calculator.data.model.Loan

data class RequisiteRow(val label: String, val value: String)

data class RequisitesUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val rows: List<RequisiteRow> = emptyList(),
    val error: String? = null,
)

class RequisitesViewModel(
    application: Application,
    private val loanId: Long,
) : AndroidViewModel(application) {
    private val loanRepository = LoanCalcData.get().loanRepository

    private val _uiState = MutableStateFlow(RequisitesUiState())
    val uiState: StateFlow<RequisitesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val loan = loanRepository.getLoan(loanId)
            if (loan == null) {
                _uiState.update { it.copy(isLoading = false, error = "Кредит не найден") }
                return@launch
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    title = loan.title.orEmpty().ifBlank { "Кредит #$loanId" },
                    rows = buildRows(loan),
                )
            }
        }
    }

    private fun buildRows(loan: Loan): List<RequisiteRow> {
        val f = com.example.loancalcandroid.util.Formatters
        return listOf(
            RequisiteRow("Название", loan.title.orEmpty().ifBlank { "—" }),
            RequisiteRow("Сумма", f.money(loan.amount)),
            RequisiteRow("Ставка", f.percent(loan.rate)),
            RequisiteRow("Срок", "${loan.term} мес."),
            RequisiteRow("Тип", if (loan.type.id == 0) "Аннуитет" else "Дифференцированный"),
            RequisiteRow("Дата выдачи", f.date(loan.dateOfIssue ?: loan.firstPaymentDate)),
            RequisiteRow("Первый платёж", f.date(loan.firstPaymentDate)),
            RequisiteRow("Ежемес. платёж", f.money(loan.monthlyPayment)),
            RequisiteRow("Учитывать выходные", yesNo(loan.considerDaysOff)),
            RequisiteRow("Платёж в последний день", yesNo(loan.payOnLastDayOfMonth)),
            RequisiteRow("Досрочки сразу", yesNo(loan.applyExtrasImmediately)),
            RequisiteRow("Как Сбербанк (по остатку)", yesNo(loan.calculateExtrasByBalanceLikeSberbank)),
        )
    }

    private fun yesNo(value: Boolean) = if (value) "Да" else "Нет"
}
