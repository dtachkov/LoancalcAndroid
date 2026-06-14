package com.example.loancalcandroid.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.loancalcandroid.MainActivity
import com.example.loancalcandroid.R
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.calculation.PaymentSummary
import java.util.Date

object PaymentNotificationHelper {
    private const val CHANNEL_ID = "payment_reminders"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.settings_notification_title),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        manager.createNotificationChannel(channel)
    }

    fun showPaymentReminder(
        context: Context,
        loanId: Long,
        loanTitle: String,
        payment: PaymentSummary,
    ) {
        ensureChannel(context)
        val message = context.getString(
            R.string.notification_text,
            Formatters.moneyFixed(payment.total),
            Formatters.date(payment.date),
        )
        val contentIntent = PendingIntent.getActivity(
            context,
            loanId.toInt(),
            scheduleIntent(context, loanId, payment.date),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val deleteIntent = PendingIntent.getBroadcast(
            context,
            loanId.toInt(),
            dismissIntent(context, loanId, payment.date),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_title, loanTitle))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(contentIntent)
            .setDeleteIntent(deleteIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()

        NotificationManagerCompat.from(context).notify(loanId.toInt(), notification)
    }

    private fun scheduleIntent(context: Context, loanId: Long, paymentDate: Date): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = NotificationActions.SHOW_SCHEDULE
            putExtra(NotificationActions.EXTRA_LOAN_ID, loanId)
            putExtra(NotificationActions.EXTRA_PAYMENT_DATE_MILLIS, paymentDate.time)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    private fun dismissIntent(context: Context, loanId: Long, paymentDate: Date): Intent {
        return Intent(context, DismissNotificationReceiver::class.java).apply {
            action = NotificationActions.DISMISS_NOTIFICATION
            putExtra(NotificationActions.EXTRA_LOAN_ID, loanId)
            putExtra(NotificationActions.EXTRA_PAYMENT_DATE_MILLIS, paymentDate.time)
        }
    }
}
