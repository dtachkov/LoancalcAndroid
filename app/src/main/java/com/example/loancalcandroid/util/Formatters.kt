package com.example.loancalcandroid.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val moneyFormat = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale("ru", "RU")))
    private val displayDateFormat = SimpleDateFormat("d MMMM yyyy г.", Locale("ru", "RU"))
    private val shortDateFormat = SimpleDateFormat("d MMMM yyyy г.", Locale("ru", "RU"))
    private val inputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))
    private val monthDayFormat = SimpleDateFormat("dd.MM", Locale("ru", "RU"))
    private val yearFormat = SimpleDateFormat("yyyy", Locale("ru", "RU"))

    fun money(value: Double): String = moneyFormat.format(value)

    fun money(value: Float): String = money(value.toDouble())

    fun percent(value: Float): String = "${moneyFormat.format(value.toDouble())} %"

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

    fun parseInt(text: String): Int {
        val normalized = text.trim()
        if (normalized.isBlank()) return 0
        return normalized.toIntOrNull() ?: 0
    }
}
