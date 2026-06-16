package com.example.loancalcandroid.util

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

private val utcTimeZone: TimeZone = TimeZone.getTimeZone("UTC")

/** Converts a local calendar date to UTC midnight millis expected by Material DatePicker. */
fun Date.toDatePickerMillis(): Long {
    val local = Calendar.getInstance().apply { time = this@toDatePickerMillis }
    return Calendar.getInstance(utcTimeZone).apply {
        set(Calendar.YEAR, local.get(Calendar.YEAR))
        set(Calendar.MONTH, local.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, local.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

/** Converts UTC midnight millis from Material DatePicker to a local calendar date. */
fun Long.toLocalDateFromDatePicker(): Date {
    val utcCalendar = Calendar.getInstance(utcTimeZone).apply { timeInMillis = this@toLocalDateFromDatePicker }
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR))
        set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}
