package com.example.loancalcandroid

import android.app.Activity
import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import com.example.loancalcandroid.billing.RuStoreLicenseManager
import com.example.loancalcandroid.notification.NotificationScheduler
import com.example.loancalcandroid.notification.PaymentNotificationHelper
import com.example.loancalcandroid.review.ReviewRequester
import com.example.loancalcandroid.widget.LoanWidgetProvider
import com.example.loancalcandroid.widget.WidgetUpdateCoordinator
import com.example.loancalcandroid.widget.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.DatabasePathResolver
import java.io.File

class LoanCalcApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var licenseManager: RuStoreLicenseManager
        private set

    var currentActivity: Activity? = null
        private set

    override fun onCreate() {
        super.onCreate()
        val data = LoanCalcData.initialize(
            context = this,
            buildType = BuildConfig.BUILD_TYPE,
        )
        ReviewRequester.onAppStart()
        licenseManager = RuStoreLicenseManager(this)
        registerActivityLifecycleCallbacks(ActivityTracker())
        applicationScope.launch {
            data.offerRepository.refreshOffers(data.settingsPreferences.getLanguageCode())
        }
        PaymentNotificationHelper.ensureChannel(this)
        NotificationScheduler.applySettings(this)
        WidgetUpdateCoordinator.start(this)
        if (hasActiveWidgets()) {
            WidgetUpdater.scheduleDailyUpdates(this)
        }
        WidgetUpdater.updateAllWidgets(this)
    }

    private fun hasActiveWidgets(): Boolean {
        val manager = AppWidgetManager.getInstance(this)
        val component = ComponentName(this, LoanWidgetProvider::class.java)
        return manager.getAppWidgetIds(component).isNotEmpty()
    }

    override fun getDatabasePath(name: String): File {
        if (name == DatabaseContract.DATABASE_NAME) {
            val externalDatabaseDir = DatabasePathResolver.getExternalDatabaseDir(this)
            if (externalDatabaseDir != null) {
                return File(externalDatabaseDir, name)
            }
        }
        return super.getDatabasePath(name)
    }

    private inner class ActivityTracker : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }
        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
        }
        override fun onActivityPaused(activity: Activity) {
            if (currentActivity === activity) {
                currentActivity = null
            }
        }
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity === activity) {
                currentActivity = null
            }
        }
    }
}
