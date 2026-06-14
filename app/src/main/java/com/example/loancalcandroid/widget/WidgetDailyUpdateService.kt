package com.example.loancalcandroid.widget

import android.app.Service
import android.content.Intent
import android.os.IBinder

class WidgetDailyUpdateService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        WidgetUpdater.updateAllWidgets(this)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
