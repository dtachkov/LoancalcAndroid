package com.example.loancalcandroid.notification

import java.util.Calendar
import java.util.Date

internal object NotificationDateUtils {
    fun clearTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    fun todayAtMidnight(): Calendar {
        return Calendar.getInstance().apply { clearTime(this) }
    }

    fun getNotificationDate(paymentDate: Date, daysBefore: Int): Calendar {
        return Calendar.getInstance().apply {
            time = paymentDate
            add(Calendar.DATE, -daysBefore)
            clearTime(this)
        }
    }
}
