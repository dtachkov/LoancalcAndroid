package com.example.loancalcandroid.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.loancalcandroid.R

object BillingSupportUtil {
    fun shareBillingLog(context: Context) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_log_subject))
            putExtra(Intent.EXTRA_TEXT, BillingLogger.getLogAsString())
        }
        val chooser = Intent.createChooser(
            sendIntent,
            context.getString(R.string.support_log_chooser_title),
        ).apply {
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        if (chooser.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
        }
    }
}
