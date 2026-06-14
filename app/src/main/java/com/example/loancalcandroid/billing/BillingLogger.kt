package com.example.loancalcandroid.billing

import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import com.example.loancalcandroid.analytics.AnalyticsHelper
import java.io.File
import java.util.ArrayList
import java.util.Date

data class BillingEvent(
    val eventDate: Date,
    val message: String,
)

object BillingLogger {
    private const val TAG = "BillingLogger"
    private val messages = ArrayList<BillingEvent>()

    fun logEvent(message: String) {
        Log.d(TAG, message)
        messages += BillingEvent(Date(), message)
        AnalyticsHelper.logEvent("BILLING_LOG", message)
    }

    fun getBillingEventsLog(): String {
        return messages.joinToString("\n") { event ->
            "[${DateFormat.format("yyyy-MM-dd HH:mm:ss", event.eventDate)}] ${event.message}"
        }.ifEmpty { "No billing events logged." }
    }

    fun getLogAsString(): String {
        return "${getBillingEventsLog()}\n${getDeviceInfo()}"
    }

    fun writeLogToFile(file: File) {
        file.writeText(getLogAsString())
    }

    fun getDeviceInfo(): String {
        val osVersion = Build.VERSION.RELEASE ?: "Unknown"
        val deviceName = Build.MANUFACTURER
        return "Устройство: $deviceName | ОС: $osVersion"
    }

    fun clear() {
        messages.clear()
    }
}
