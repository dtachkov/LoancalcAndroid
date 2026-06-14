package com.example.loancalcandroid.util

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val locale = Locale("ru", "RU")
    private val moneyFormat = DecimalFormat("#,##0.##", DecimalFormatSymbols(locale))
    private val moneyFixedFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(locale))
    private val displayDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
    private val percentFormat = NumberFormat.getPercentInstance(locale).apply {
        maximumFractionDigits = 3
        (this as DecimalFormat).decimalFormatSymbols = DecimalFormatSymbols(locale).apply {
            groupingSeparator = ' '
            decimalSeparator = '.'
        }
    }
    private val shortDateFormat = SimpleDateFormat("d MMMM yyyy г.", locale)
    private val inputDateFormat = SimpleDateFormat("dd.MM.yyyy", locale)
    private val monthDayFormat = SimpleDateFormat("dd.MM", locale)
    private val yearFormat = SimpleDateFormat("yyyy", locale)

    fun money(value: Double): String = moneyFormat.format(value)

    fun money(value: Float): String = money(value.toDouble())

    fun moneyFixed(value: Double): String = moneyFixedFormat.format(value)

    fun moneyFixed(value: Float): String = moneyFixed(value.toDouble())

    fun moneyWithoutDecimal(value: Double): String {
        val formatted = moneyFixedFormat.format(value).trim()
        val dotIndex = formatted.indexOf('.')
        return if (dotIndex >= 0) formatted.substring(0, dotIndex).trim() else formatted
    }

    fun moneyWithoutDecimal(value: Float): String = moneyWithoutDecimal(value.toDouble())

    fun percent(value: Float): String = schedulePercent(value.toDouble())

    fun schedulePercent(percent: Double): String =
        percentFormat.format(percent / 100.0).trim().replace('\u00A0', ' ')

    fun date(date: Date?): String {
        if (date == null) return "—"
        return displayDateFormat.format(date)
    }

    fun shortDate(date: Date?): String {
        if (date == null) return "—"
        return shortDateFormat.format(date)
    }

    fun inputDate(date: Date?): String {
        if (date == null) return ""
        return inputDateFormat.format(date)
    }

    fun parseMoney(text: String): Float {
        val normalized = text
            .replace(" ", "")
            .replace(",", ".")
            .trim()
        if (normalized.isBlank()) return 0f
        return normalized.toFloatOrNull() ?: 0f
    }

    fun parsePercent(text: String): Float = parseMoney(text)

    fun monthDay(date: Date?): String {
        if (date == null) return ""
        return monthDayFormat.format(date)
    }

    fun year(date: Date?): String {
        if (date == null) return ""
        return yearFormat.format(date)
    }

    fun currentMonthName(locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat("LLLL", locale).format(Date())
    }

    fun parseInt(text: String): Int {
        val normalized = text.trim()
        if (normalized.isBlank()) return 0
        return normalized.toIntOrNull() ?: 0
    }
}
