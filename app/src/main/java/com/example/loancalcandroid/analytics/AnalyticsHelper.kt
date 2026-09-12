package com.example.loancalcandroid.analytics

import android.content.Context
import android.util.Log
import com.example.loancalcandroid.BuildConfig
import io.appmetrica.analytics.AppMetrica

object AnalyticsHelper {
    private const val TAG = "Analytics"
    private const val EVENT_SUFFIX = "_GP"
    private val isAnalyticsEnabled: Boolean
        get() = !BuildConfig.DEBUG

    fun logEvent(eventName: String, eventText: String = "") {
        val reportedName = eventName.withStoreSuffix()
        Log.d(TAG, "$reportedName: $eventText")
        if (!isAnalyticsEnabled) return
        val params = mapOf("text" to eventText)
        runCatching {
            AppMetrica.reportEvent(reportedName, params)
        }.onFailure {
            Log.w(TAG, "Failed to report event $reportedName", it)
        }
    }

    fun logCalculation(amount: Float, source: String) {
        val reportedName = "CALC_LOAN".withStoreSuffix()
        Log.d(TAG, "$reportedName: amount=$amount source=$source")
        if (!isAnalyticsEnabled) return
        val params = mapOf(
            "amount" to amount.toString(),
            "source" to source,
        )
        runCatching {
            AppMetrica.reportEvent(reportedName, params)
        }.onFailure {
            Log.w(TAG, "Failed to report $reportedName", it)
        }
    }

    private fun String.withStoreSuffix(): String {
        return if (endsWith(EVENT_SUFFIX)) this else this + EVENT_SUFFIX
    }

    fun activate(context: Context) {
        if (!isAnalyticsEnabled) {
            Log.d(TAG, "AppMetrica disabled in debug build")
            return
        }
        if (BuildConfig.APPMETRICA_API_KEY.isBlank()) return
        runCatching {
            val builder = io.appmetrica.analytics.AppMetricaConfig
                .newConfigBuilder(BuildConfig.APPMETRICA_API_KEY)
            AppMetrica.activate(context, builder.build())
            AppMetrica.enableActivityAutoTracking(context.applicationContext as android.app.Application)
        }.onFailure {
            Log.e(TAG, "Failed to activate AppMetrica", it)
        }
    }
}
