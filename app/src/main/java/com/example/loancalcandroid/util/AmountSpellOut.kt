package com.example.loancalcandroid.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

object AmountSpellOut {
    fun format(amount: Double, locale: Locale): String? {
        if (amount <= 0.0 || amount.isNaN() || amount.isInfinite()) return null
        val whole = amount.roundToLong()
        if (abs(amount - whole) >= 0.000001) return null
        val raw = spellOut(whole, locale) ?: return null
        if (raw.isBlank()) return null
        return raw.replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(locale) else ch.toString()
        }
    }

    private fun spellOut(amount: Long, locale: Locale): String? {
        return when (locale.language) {
            "ru" -> russian(amount)
            "uk" -> ukrainian(amount)
            "en" -> english(amount)
            else -> icuSpellOut(amount, locale) ?: english(amount)
        }
    }

    private fun icuSpellOut(amount: Long, locale: Locale): String? {
        return try {
            val clazz = Class.forName("android.icu.text.RuleBasedNumberFormat")
            val spellout = clazz.getField("SPELLOUT").getInt(null)
            val formatter = clazz
                .getConstructor(Locale::class.java, Integer.TYPE)
                .newInstance(locale, spellout)
            clazz.getMethod("format", java.lang.Long.TYPE)
                .invoke(formatter, amount) as? String
        } catch (_: Exception) {
            null
        }
    }

    private fun russian(amount: Long): String {
        if (amount == 0L) return "ноль"
        return buildScales(
            amount = amount,
            triad = { value, female -> ruTriad(value, female) },
            thousand = ruPlural(amountScale(amount, 1_000), "тысяча", "тысячи", "тысяч"),
            million = ruPlural(amountScale(amount, 1_000_000), "миллион", "миллиона", "миллионов"),
            billion = ruPlural(amountScale(amount, 1_000_000_000), "миллиард", "миллиарда", "миллиардов"),
            feminineThousands = true,
        )
    }

    private fun ukrainian(amount: Long): String {
        if (amount == 0L) return "нуль"
        return buildScales(
            amount = amount,
            triad = { value, female -> ukTriad(value, female) },
            thousand = ruPlural(amountScale(amount, 1_000), "тисяча", "тисячі", "тисяч"),
            million = ruPlural(amountScale(amount, 1_000_000), "мільйон", "мільйона", "мільйонів"),
            billion = ruPlural(amountScale(amount, 1_000_000_000), "мільярд", "мільярда", "мільярдів"),
            feminineThousands = true,
        )
    }

    private fun english(amount: Long): String {
        if (amount == 0L) return "zero"
        return buildScales(
            amount = amount,
            triad = { value, _ -> enTriad(value) },
            thousand = "thousand",
            million = "million",
            billion = "billion",
            feminineThousands = false,
        )
    }

    private fun buildScales(
        amount: Long,
        triad: (Int, Boolean) -> String,
        thousand: String,
        million: String,
        billion: String,
        feminineThousands: Boolean,
    ): String {
        val parts = ArrayList<String>(8)
        val billions = (amount / 1_000_000_000).toInt()
        val millions = ((amount / 1_000_000) % 1000).toInt()
        val thousands = ((amount / 1_000) % 1000).toInt()
        val rest = (amount % 1_000).toInt()
        if (billions > 0) {
            parts += triad(billions, false)
            parts += billion
        }
        if (millions > 0) {
            parts += triad(millions, false)
            parts += million
        }
        if (thousands > 0) {
            parts += triad(thousands, feminineThousands)
            parts += thousand
        }
        if (rest > 0) {
            parts += triad(rest, false)
        }
        return parts.filter { it.isNotBlank() }.joinToString(" ")
    }

    private fun amountScale(amount: Long, scale: Long): Int = ((amount / scale) % 1000).toInt()

    private fun ruPlural(value: Int, one: String, few: String, many: String): String {
        val n100 = value % 100
        val n10 = value % 10
        return when {
            n100 in 11..14 -> many
            n10 == 1 -> one
            n10 in 2..4 -> few
            else -> many
        }
    }

    private fun ruTriad(value: Int, feminine: Boolean): String {
        val units = if (feminine) RU_UNITS_FEMALE else RU_UNITS_MALE
        return slavicTriad(value, units, RU_TEENS, RU_TENS, RU_HUNDREDS)
    }

    private fun ukTriad(value: Int, feminine: Boolean): String {
        val units = if (feminine) UK_UNITS_FEMALE else UK_UNITS_MALE
        return slavicTriad(value, units, UK_TEENS, UK_TENS, UK_HUNDREDS)
    }

    private fun slavicTriad(
        value: Int,
        units: Array<String>,
        teens: Array<String>,
        tens: Array<String>,
        hundreds: Array<String>,
    ): String {
        val parts = ArrayList<String>(3)
        val hundred = value / 100
        val ten = (value % 100) / 10
        val unit = value % 10
        if (hundred > 0) parts += hundreds[hundred]
        when {
            ten == 1 -> parts += teens[unit]
            else -> {
                if (ten > 1) parts += tens[ten]
                if (unit > 0) parts += units[unit]
            }
        }
        return parts.joinToString(" ")
    }

    private fun enTriad(value: Int): String {
        val parts = ArrayList<String>(3)
        val hundred = value / 100
        val rest = value % 100
        if (hundred > 0) {
            parts += EN_ONES[hundred]
            parts += "hundred"
        }
        when {
            rest == 0 -> Unit
            rest < 20 -> parts += EN_ONES[rest]
            else -> {
                val ten = rest / 10
                val unit = rest % 10
                parts += if (unit == 0) EN_TENS[ten] else "${EN_TENS[ten]}-${EN_ONES[unit]}"
            }
        }
        return parts.joinToString(" ")
    }

    private val RU_UNITS_MALE = arrayOf("", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
    private val RU_UNITS_FEMALE = arrayOf("", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
    private val RU_TEENS = arrayOf(
        "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
        "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать",
    )
    private val RU_TENS = arrayOf(
        "", "", "двадцать", "тридцать", "сорок", "пятьдесят",
        "шестьдесят", "семьдесят", "восемьдесят", "девяносто",
    )
    private val RU_HUNDREDS = arrayOf(
        "", "сто", "двести", "триста", "четыреста", "пятьсот",
        "шестьсот", "семьсот", "восемьсот", "девятьсот",
    )

    private val UK_UNITS_MALE = arrayOf("", "один", "два", "три", "чотири", "п'ять", "шість", "сім", "вісім", "дев'ять")
    private val UK_UNITS_FEMALE = arrayOf("", "одна", "дві", "три", "чотири", "п'ять", "шість", "сім", "вісім", "дев'ять")
    private val UK_TEENS = arrayOf(
        "десять", "одинадцять", "дванадцять", "тринадцять", "чотирнадцять",
        "п'ятнадцять", "шістнадцять", "сімнадцять", "вісімнадцять", "дев'ятнадцять",
    )
    private val UK_TENS = arrayOf(
        "", "", "двадцять", "тридцять", "сорок", "п'ятдесят",
        "шістдесят", "сімдесят", "вісімдесят", "дев'яносто",
    )
    private val UK_HUNDREDS = arrayOf(
        "", "сто", "двісті", "триста", "чотириста", "п'ятсот",
        "шістсот", "сімсот", "вісімсот", "дев'ятсот",
    )

    private val EN_ONES = arrayOf(
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
        "seventeen", "eighteen", "nineteen",
    )
    private val EN_TENS = arrayOf(
        "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
    )
}
