package ru.kredit.calculator.data.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

object DateFormats {
    private val dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    private val zoneId: ZoneId = ZoneId.systemDefault()

    fun formatDate(date: Date?): String? {
        return date?.let { toLocalDate(it).format(dbDateFormatter) }
    }

    fun parseDate(value: String?): Date? {
        if (value.isNullOrBlank()) return null
        return try {
            toDate(LocalDate.parse(value, dbDateFormatter))
        } catch (_: DateTimeParseException) {
            null
        }
    }

    fun clearTime(date: Date): Date = toDate(toLocalDate(date))

    fun addMonths(date: Date, months: Int): Date {
        return toDate(toLocalDate(date).plusMonths(months.toLong()))
    }

    private fun toLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(zoneId).toLocalDate()
    }

    private fun toDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay(zoneId).toInstant())
    }
}
