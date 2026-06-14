package com.example.loancalcandroid.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import ru.kredit.calculator.data.preferences.WidgetPreferences
import java.util.Calendar

object WidgetUpdater {
    private const val DAILY_UPDATE_REQUEST_CODE = 1

    fun updateWidget(context: Context, widgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = intArrayOf(widgetId)
        val intent = Intent(context, LoanWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }

    fun updateAllWidgets(context: Context) {
        val appContext = context.applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(appContext)
        val componentName = ComponentName(appContext, LoanWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (widgetIds.isEmpty()) return

        val intent = Intent(appContext, LoanWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }
        appContext.sendBroadcast(intent)
    }

    fun updateWidgetsForLoan(context: Context, loanId: Long) {
        val widgetId = WidgetPreferences(context).getWidgetIdForLoan(loanId) ?: return
        updateWidget(context, widgetId)
    }

    fun scheduleDailyUpdates(context: Context) {
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = dailyUpdatePendingIntent(appContext)

        val nextUpdate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            nextUpdate.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }

    fun cancelDailyUpdates(context: Context) {
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(dailyUpdatePendingIntent(appContext))
    }

    private fun dailyUpdatePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, WidgetDailyUpdateService::class.java)
        return PendingIntent.getService(
            context,
            DAILY_UPDATE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
