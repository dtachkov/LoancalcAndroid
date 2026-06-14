package com.example.loancalcandroid.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import kotlinx.coroutines.runBlocking
import com.example.loancalcandroid.LoanCalcApplication
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.LoanCalculationResult
import ru.kredit.calculator.data.calculation.PaymentSummary
import ru.kredit.calculator.data.preferences.ShownNotificationsPreferences
import java.util.Calendar

class NotificationsService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = LoanCalcData.get()
        if (!data.settingsPreferences.areNotificationsEnabled()) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        if (!(application as LoanCalcApplication).licenseManager.isAppPurchased()) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val wakeLock = acquireWakeLock()
        Thread {
            try {
                runBlocking {
                    checkAndNotify(data)
                }
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
                NotificationScheduler.scheduleNotifications(this)
                stopSelf(startId)
            }
        }.start()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private suspend fun checkAndNotify(data: LoanCalcData) {
        PaymentNotificationHelper.ensureChannel(this)
        val shownNotifications = data.shownNotificationsPreferences
        val today = NotificationDateUtils.todayAtMidnight()
        val daysBefore = data.settingsPreferences.getNotificationDays()
        val loans = data.loanRepository.getLoans()

        for (loan in loans) {
            if (!loan.validate()) continue
            val extras = data.extraRepository.getExtras(loan.id)
            val calculation = runCatching {
                data.loanCalculator.calculate(loan, extras)
            }.getOrNull() ?: continue

            val currentPayment = getCurrentPayment(today, calculation) ?: continue
            val showNotificationDate = NotificationDateUtils.getNotificationDate(
                paymentDate = currentPayment.date,
                daysBefore = daysBefore,
            )

            if (today.timeInMillis == showNotificationDate.timeInMillis &&
                currentPayment.total > 0.001 &&
                !shownNotifications.isNotified(loan.id, currentPayment.date)
            ) {
                PaymentNotificationHelper.showPaymentReminder(
                    context = this,
                    loanId = loan.id,
                    loanTitle = loan.title.orEmpty(),
                    payment = currentPayment,
                )
            }
        }
    }

    private fun getCurrentPayment(
        today: Calendar,
        calculation: LoanCalculationResult,
    ): PaymentSummary? {
        for (payment in calculation.payments) {
            val paymentDate = Calendar.getInstance().apply {
                time = payment.date
                NotificationDateUtils.clearTime(this)
            }
            if (paymentDate.timeInMillis == today.timeInMillis) {
                return payment
            }
        }
        return calculation.payments.firstOrNull { it.isCurrent }
    }

    private fun acquireWakeLock(): PowerManager.WakeLock {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG).apply {
            acquire(WAKE_LOCK_TIMEOUT_MS)
        }
    }

    companion object {
        private const val TAG = "NotificationsService"
        private const val WAKE_LOCK_TIMEOUT_MS = 60_000L
    }
}
