package ru.kredit.calculator.data.preferences

import android.content.Context

class SettingsPreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(
            "${context.packageName}_preferences",
            Context.MODE_PRIVATE,
        )

    fun isLoadLastLoanAtStart(): Boolean {
        return preferences.getBoolean(PreferenceKeys.LOAD_LAST_LOAN_AT_START, false)
    }

    fun setLoadLastLoanAtStart(enabled: Boolean) {
        preferences.edit().putBoolean(PreferenceKeys.LOAD_LAST_LOAN_AT_START, enabled).apply()
    }

    fun getLanguageCode(): String {
        return preferences.getString(PreferenceKeys.LANGUAGE, java.util.Locale.getDefault().language)
            ?: java.util.Locale.getDefault().language
    }

    fun setLanguageCode(code: String) {
        preferences.edit().putString(PreferenceKeys.LANGUAGE, code).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean(PreferenceKeys.NOTIFICATION_ENABLED, false)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(PreferenceKeys.NOTIFICATION_ENABLED, enabled).apply()
    }

    fun getNotificationDays(default: Int = DEFAULT_DAYS): Int {
        return readIntWithStringFallback(PreferenceKeys.NOTIFICATION_DAYS, default)
    }

    fun setNotificationDays(days: Int) {
        preferences.edit().putInt(PreferenceKeys.NOTIFICATION_DAYS, days).apply()
    }

    fun getNotificationHour(default: Int = DEFAULT_HOUR): Int {
        return readIntWithStringFallback(PreferenceKeys.NOTIFICATION_HOUR, default)
    }

    fun setNotificationHour(hour: Int) {
        preferences.edit().putInt(PreferenceKeys.NOTIFICATION_HOUR, hour).apply()
    }

    fun getNotificationMinute(default: Int = DEFAULT_MINUTE): Int {
        return readIntWithStringFallback(PreferenceKeys.NOTIFICATION_MINUTE, default)
    }

    fun setNotificationMinute(minute: Int) {
        preferences.edit().putInt(PreferenceKeys.NOTIFICATION_MINUTE, minute).apply()
    }

    private fun readIntWithStringFallback(key: String, default: Int): Int {
        return try {
            preferences.getInt(key, default)
        } catch (_: ClassCastException) {
            val stringValue = preferences.getString(key, default.toString())
            stringValue?.toIntOrNull() ?: default
        }
    }

    companion object {
        const val DEFAULT_DAYS = 5
        const val DEFAULT_HOUR = 12
        const val DEFAULT_MINUTE = 35
    }
}
