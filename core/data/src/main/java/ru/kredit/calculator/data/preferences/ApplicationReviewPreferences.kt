package ru.kredit.calculator.data.preferences

import android.content.Context
import androidx.core.content.edit

class ApplicationReviewPreferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getAppLaunchesCount(): Int {
        return preferences.getInt(LAUNCHES_COUNT, 0)
    }

    fun incrementAppLaunchesCount() {
        preferences.edit { putInt(LAUNCHES_COUNT, getAppLaunchesCount() + 1) }
    }

    fun resetAppLaunchesCount() {
        preferences.edit { putInt(LAUNCHES_COUNT, 0) }
    }

    fun setRedirectedForReview() {
        preferences.edit { putBoolean(REDIRECTED_FOR_REVIEW, true) }
    }

    fun isRedirectedForReview(): Boolean {
        return preferences.getBoolean(REDIRECTED_FOR_REVIEW, false)
    }

    fun setDontAskForReview() {
        preferences.edit { putBoolean(DONT_ASK_FOR_REVIEW, true) }
    }

    fun dontAskForReview(): Boolean {
        return preferences.getBoolean(DONT_ASK_FOR_REVIEW, false)
    }

    companion object {
        const val PREFERENCE_NAME = "Review"
        const val LAUNCHES_COUNT = "launche"
        const val REDIRECTED_FOR_REVIEW = "Review_ok"
        private const val DONT_ASK_FOR_REVIEW = "Review_no"
        const val LAUNCHES_BEFORE_ASK_REVIEW = 1
    }
}
