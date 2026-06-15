package com.example.loancalcandroid.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.example.loancalcandroid.R

fun copyMetricValueToClipboard(context: Context, value: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText("metric", value))
    Toast.makeText(context, context.getString(R.string.metric_value_copied), Toast.LENGTH_SHORT).show()
}
