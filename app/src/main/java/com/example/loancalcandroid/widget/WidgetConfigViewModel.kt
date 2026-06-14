package com.example.loancalcandroid.widget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.model.Loan

class WidgetConfigViewModel(
    application: Application,
) : AndroidViewModel(application) {
    val loans: StateFlow<List<Loan>> = LoanCalcData.get()
        .loanRepository
        .observeLoans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
