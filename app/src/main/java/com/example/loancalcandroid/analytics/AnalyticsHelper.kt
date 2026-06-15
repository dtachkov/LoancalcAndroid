package com.example.loancalcandroid.analytics

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.loancalcandroid.BuildConfig
import io.appmetrica.analytics.AppMetrica

object AnalyticsHelper {
    private const val TAG = "Analytics"
    private val isAnalyticsEnabled: Boolean
        get() = !BuildConfig.DEBUG

    fun logEvent(eventName: String, eventText: String = "") {
        Log.d(TAG, "$eventName: $eventText")
        if (!isAnalyticsEnabled) return
        val params = mapOf("text" to eventText)
        runCatching {
            AppMetrica.reportEvent(eventName, params)
        }.onFailure {
            Log.w(TAG, "Failed to report event $eventName", it)
        }
    }

    fun logCalculation(amount: Float, source: String) {
        Log.d(TAG, "CALC_LOAN: amount=$amount source=$source")
        if (!isAnalyticsEnabled) return
        val params = mapOf(
            "amount" to amount.toString(),
            "source" to source,
        )
        runCatching {
            AppMetrica.reportEvent("CALC_LOAN", params)
        }.onFailure {
            Log.w(TAG, "Failed to report CALC_LOAN", it)
        }
    }

    fun logOfferOpening(offerName: String?) {
        logEvent("PRESS_LEAD", offerName.orEmpty())
    }

    fun openOfferLink(context: Context, offerName: String?, link: String) {
        logOfferOpening(offerName)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "No activity found for offer link: $link", e)
        }
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
