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
}
