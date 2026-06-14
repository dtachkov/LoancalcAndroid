package com.example.loancalcandroid.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleWidgetsUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        WidgetUpdater.scheduleDailyUpdates(context)
        WidgetUpdater.updateAllWidgets(context)
    }
}
