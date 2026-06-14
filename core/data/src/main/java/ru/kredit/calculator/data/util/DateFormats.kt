package ru.kredit.calculator.data.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormats {
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun formatDate(date: Date?): String? {
        return date?.let { dbDateFormat.format(it) }
    }

    fun parseDate(value: String?): Date? {
        if (value.isNullOrBlank()) return null
        return try {
            dbDateFormat.parse(value)
        } catch (_: ParseException) {
            null
        }
    }

    fun clearTime(date: Date): Date {
        val formatted = dbDateFormat.format(date)
        return dbDateFormat.parse(formatted) ?: date
    }
}
