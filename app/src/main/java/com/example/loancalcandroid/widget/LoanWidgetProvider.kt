package com.example.loancalcandroid.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.example.loancalcandroid.R
import com.example.loancalcandroid.util.Formatters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.preferences.WidgetPreferences

class LoanWidgetProvider : AppWidgetProvider() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdater.scheduleDailyUpdates(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetUpdater.cancelDailyUpdates(context)
        WidgetPreferences(context).removeAll()
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val preferences = WidgetPreferences(context)
        appWidgetIds.forEach(preferences::removeWidget)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pendingResult = goAsync()
        scope.launch {
            try {
                appWidgetIds.forEach { widgetId ->
                    updateWidget(context, appWidgetManager, widgetId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int,
    ) {
        val views = RemoteViews(context.packageName, R.layout.loan_widget)
        val preferences = WidgetPreferences(context)
        val loanId = preferences.getLoanId(widgetId)

        views.setOnClickPendingIntent(
            R.id.widget_root,
            WidgetPendingIntents.configActivity(context, widgetId),
        )

        when (val result = WidgetDataLoader.load(loanId)) {
            is WidgetDataLoader.LoadResult.Success -> {
                val calculation = result.calculation
                views.setViewVisibility(R.id.widget_loading, View.GONE)
                views.setTextViewText(R.id.tv_loan_title, calculation.loanTitle)
                views.setTextViewText(
                    R.id.tv_payment_date,
                    Formatters.date(calculation.currentPaymentDate),
                )
                views.setTextViewText(
                    R.id.tv_monthly_payment,
                    Formatters.money(calculation.currentPayment),
                )
                views.setTextViewText(R.id.tv_info, context.getString(R.string.widget_payment_label))
            }

            WidgetDataLoader.LoadResult.InfiniteLoan -> {
                showPlaceholder(
                    views = views,
                    message = context.getString(R.string.widget_infinite_loan),
                )
            }

            WidgetDataLoader.LoadResult.LoanNotFound,
            WidgetDataLoader.LoadResult.CalculationError,
            -> {
                showPlaceholder(
                    views = views,
                    message = context.getString(R.string.widget_select_loan),
                )
            }
        }

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun showPlaceholder(views: RemoteViews, message: String) {
        views.setTextViewText(R.id.tv_loan_title, "")
        views.setTextViewText(R.id.tv_payment_date, "")
        views.setTextViewText(R.id.tv_monthly_payment, "")
        views.setViewVisibility(R.id.widget_loading, View.VISIBLE)
        views.setTextViewText(R.id.tv_widget_link, message)
    }
}
