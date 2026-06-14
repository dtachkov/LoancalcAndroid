package com.example.loancalcandroid.widget

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.kredit.calculator.data.LoanCalcData

object WidgetUpdateCoordinator {
    private var started = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start(context: Context) {
        if (started) return
        started = true

        val appContext = context.applicationContext
        val data = LoanCalcData.get()

        data.loanRepository.observeLoans()
            .map { loans -> loans.map { it.id }.toSet() }
            .distinctUntilChanged()
            .onEach { WidgetUpdater.updateAllWidgets(appContext) }
            .launchIn(scope)

        data.extraRepository.observeExtrasChanges()
            .distinctUntilChanged()
            .onEach { WidgetUpdater.updateAllWidgets(appContext) }
            .launchIn(scope)
    }
}
