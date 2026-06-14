package ru.kredit.calculator.data.preferences

import android.content.Context
import androidx.core.content.edit
import java.util.Date

class ShownNotificationsPreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setNotified(loanId: Long, paymentDate: Date) {
        preferences.edit {
            putLong(loanId.toString(), paymentDate.time)
        }
    }

    fun isNotified(loanId: Long, paymentDate: Date): Boolean {
        return preferences.getLong(loanId.toString(), 0L) == paymentDate.time
    }

    fun clear() {
        preferences.edit { clear() }
    }

    companion object {
        const val PREFERENCE_NAME = "shown_notifications"
    }
}
