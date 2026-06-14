package com.example.loancalcandroid.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.kredit.calculator.data.LoanCalcData
import java.util.Date

class DismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val loanId = intent?.getLongExtra(NotificationActions.EXTRA_LOAN_ID, 0L) ?: return
        if (loanId == 0L) return
        val paymentDateMillis = intent.getLongExtra(NotificationActions.EXTRA_PAYMENT_DATE_MILLIS, 0L)
        if (paymentDateMillis == 0L) return

        LoanCalcData.get().shownNotificationsPreferences.setNotified(
            loanId = loanId,
            paymentDate = Date(paymentDateMillis),
        )
    }
}
