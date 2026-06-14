package ru.kredit.calculator.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ChestPreferences(
    context: Context,
    buildType: String,
) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("loancalc_$buildType", Context.MODE_PRIVATE)

    fun getLastCalculatedLoanId(): Long {
        return preferences.getLong(PreferenceKeys.LAST_CALCULATED_LOAN_ID, 0)
    }

    fun setLastCalculatedLoanId(loanId: Long) {
        preferences.edit { putLong(PreferenceKeys.LAST_CALCULATED_LOAN_ID, loanId) }
    }

    fun hasLicense(): Boolean {
        return preferences.contains(PreferenceKeys.LICENSE_KEY)
    }

    fun getLicense(): String? {
        return preferences.getString(PreferenceKeys.LICENSE_KEY, null)
    }

    fun setLicense(license: String?) {
        preferences.edit {
            if (license.isNullOrBlank()) {
                remove(PreferenceKeys.LICENSE_KEY)
            } else {
                putString(PreferenceKeys.LICENSE_KEY, license)
            }
        }
    }

    fun getStoredAppVersionCode(): Int {
        return preferences.getInt(PreferenceKeys.APP_VERSION_CODE, 0)
    }

    fun setStoredAppVersionCode(versionCode: Int) {
        preferences.edit { putInt(PreferenceKeys.APP_VERSION_CODE, versionCode) }
    }
}
