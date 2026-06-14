package com.example.loancalcandroid.support

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.example.loancalcandroid.BuildConfig
import com.example.loancalcandroid.R
import com.example.loancalcandroid.billing.BillingLogger
import java.io.File

object DeveloperSupportUtil {
    fun sendDeveloperEmail(
        context: Context,
        loansExportFile: File?,
        billingLogFile: File?,
    ): Boolean {
        val subject = context.getString(
            R.string.developer_email_subject,
            BuildConfig.VERSION_NAME,
        )
        val body = context.getString(
            R.string.developer_email_android_version,
            Build.VERSION.RELEASE.orEmpty(),
        )

        val attachmentUris = buildList {
            billingLogFile?.takeIf { it.exists() && it.length() > 0L }?.let { add(fileUri(context, it)) }
            loansExportFile?.takeIf { it.exists() && it.length() > 0L }?.let { add(fileUri(context, it)) }
        }

        val sendIntent = if (attachmentUris.size <= 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                attachmentUris.singleOrNull()?.let { uri ->
                    putExtra(Intent.EXTRA_STREAM, uri)
                    clipData = ClipData.newRawUri("", uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(attachmentUris))
                clipData = ClipData.newRawUri("", attachmentUris.first()).apply {
                    attachmentUris.drop(1).forEach { uri ->
                        addItem(ClipData.Item(uri))
                    }
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        return launchEmailChooser(context, sendIntent)
    }

    private fun fileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }

    private fun launchEmailChooser(context: Context, sendIntent: Intent): Boolean {
        val chooser = Intent.createChooser(
            sendIntent,
            context.getString(R.string.developer_email_chooser_title),
        ).apply {
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        if (chooser.resolveActivity(context.packageManager) == null) {
            return false
        }
        context.startActivity(chooser)
        return true
    }
}
