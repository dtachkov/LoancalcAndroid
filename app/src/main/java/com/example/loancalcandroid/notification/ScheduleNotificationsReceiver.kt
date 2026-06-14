package com.example.loancalcandroid.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleNotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationScheduler.applySettings(context)
    }
}
