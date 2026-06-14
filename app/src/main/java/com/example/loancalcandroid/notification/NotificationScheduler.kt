package com.example.loancalcandroid.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.kredit.calculator.data.LoanCalcData
import java.util.Calendar

object NotificationScheduler {
    private const val ALARM_REQUEST_CODE = 0

    fun applySettings(context: Context) {
        val settings = LoanCalcData.get().settingsPreferences
        if (settings.areNotificationsEnabled()) {
            scheduleNotifications(context)
        } else {
            cancelNotifications(context)
        }
    }

    fun scheduleNotifications(context: Context) {
        val appContext = context.applicationContext
        val settings = LoanCalcData.get().settingsPreferences
        if (!settings.areNotificationsEnabled()) return

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = servicePendingIntent(appContext)

        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.getNotificationHour())
            set(Calendar.MINUTE, settings.getNotificationMinute())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (notificationTime.before(Calendar.getInstance())) {
            notificationTime.add(Calendar.DATE, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTime.timeInMillis,
                pendingIntent,
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTime.timeInMillis,
                pendingIntent,
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationTime.timeInMillis,
                pendingIntent,
            )
        }
    }

    fun cancelNotifications(context: Context) {
        val alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(servicePendingIntent(context.applicationContext))
    }

    private fun servicePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationsService::class.java).apply {
            action = NotificationActions.SHOW_NOTIFICATIONS
        }
        return PendingIntent.getService(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
