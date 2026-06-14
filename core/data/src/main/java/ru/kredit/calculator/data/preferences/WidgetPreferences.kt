package ru.kredit.calculator.data.preferences

import android.content.Context

class WidgetPreferences(context: Context) {
    private val preferences =
        context.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setLoanId(widgetId: Int, loanId: Long) {
        preferences.edit().putLong(widgetKey(widgetId), loanId).apply()
    }

    fun getLoanId(widgetId: Int): Long {
        return preferences.getLong(widgetKey(widgetId), 0L)
    }

    fun removeWidget(widgetId: Int) {
        preferences.edit().remove(widgetKey(widgetId)).apply()
    }

    fun removeAll() {
        preferences.edit().clear().apply()
    }

    fun getWidgetIds(): List<Int> {
        return preferences.all.keys
            .asSequence()
            .filter { it.startsWith(WIDGET_KEY_PREFIX) }
            .map { it.removePrefix(WIDGET_KEY_PREFIX).toInt() }
            .toList()
    }

    fun getWidgetIdForLoan(loanId: Long): Int? {
        return preferences.all.entries
            .firstOrNull { it.value == loanId }
            ?.key
            ?.removePrefix(WIDGET_KEY_PREFIX)
            ?.toIntOrNull()
    }

    private fun widgetKey(widgetId: Int): String = "$WIDGET_KEY_PREFIX$widgetId"

    companion object {
        private const val PREFERENCE_NAME = "widget_pref"
        private const val WIDGET_KEY_PREFIX = "widget_credit_"
    }
}
