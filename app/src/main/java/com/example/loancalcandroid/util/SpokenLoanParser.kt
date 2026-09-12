package com.example.loancalcandroid.util

import kotlin.math.abs
import kotlin.math.roundToLong

data class SpokenLoanFields(
    val amount: String? = null,
    val rate: String? = null,
    val termMonths: String? = null,
)

object SpokenLoanParser {
    private val termKeywordRegex = Regex(
        """(?<![А-Яа-яЁёA-Za-z])(?:месяц(?:а|ев|е)?|мес\.?|лет|год(?:а|у|ов)?)(?![А-Яа-яЁёA-Za-z])""",
        RegexOption.IGNORE_CASE,
    )
    private val rateKeywordRegex = Regex(
        """(?<![А-Яа-яЁёA-Za-z])процент(?:а|ов|е|ная|ный|ную)?(?![А-Яа-яЁёA-Za-z])|%""",
        RegexOption.IGNORE_CASE,
    )
    private val amountKeywordRegex = Regex(
        """(?<![А-Яа-яЁёA-Za-z])(?:руб(?:л(?:ей|я|ь))?|₽)(?![А-Яа-яЁёA-Za-z])""",
        RegexOption.IGNORE_CASE,
    )
    private val yearKeywordRegex = Regex("""год|лет""", RegexOption.IGNORE_CASE)

    fun parse(phrase: String): SpokenLoanFields {
        val text = phrase.trim()
        if (text.isEmpty()) return SpokenLoanFields()

        val termMatch = findNumberBeforeKeyword(text, termKeywordRegex)
        val termMonths = termMatch?.let { match ->
            val keyword = text.substring(match.keywordStart, match.keywordEnd)
            val months = if (yearKeywordRegex.containsMatchIn(keyword)) {
                match.value * 12.0
            } else {
                match.value
            }
            formatValue(months)
        }

        val rateMatch = findNumberBeforeKeyword(text, rateKeywordRegex)
        val rate = rateMatch?.let { formatValue(it.value) }

        val amount = parseAmount(text, termMatch, rateMatch)

        return SpokenLoanFields(
            amount = amount,
            rate = rate,
            termMonths = termMonths,
        )
    }

    private fun parseAmount(
        text: String,
        termMatch: NumberMatch?,
        rateMatch: NumberMatch?,
    ): String? {
        val rubMatch = findNumberBeforeKeyword(text, amountKeywordRegex)
        if (rubMatch != null) {
            return digitsOnly(rubMatch.raw)
        }

        val leftover = StringBuilder(text)
        listOfNotNull(termMatch, rateMatch)
            .sortedByDescending { it.start }
            .forEach { match -> leftover.replace(match.start, match.end, "") }
        return digitsOnly(leftover.toString())
    }

    private fun findNumberBeforeKeyword(text: String, keywordRegex: Regex): NumberMatch? {
        val keyword = keywordRegex.find(text) ?: return null
        var index = keyword.range.first - 1
        while (index >= 0 && (text[index].isWhitespace() || text[index] == '-' || text[index] == '—')) {
            index--
        }
        val numberEnd = index + 1
        while (index >= 0 && (text[index].isDigit() || text[index] == ',' || text[index] == '.')) {
            index--
        }
        val numberStart = index + 1
        if (numberStart >= numberEnd) return null

        val raw = text.substring(numberStart, numberEnd)
        val normalized = raw.replace(',', '.')
        val value = normalized.toDoubleOrNull() ?: return null
        return NumberMatch(
            start = numberStart,
            end = numberEnd,
            raw = raw,
            value = value,
            keywordStart = keyword.range.first,
            keywordEnd = keyword.range.last + 1,
        )
    }

    private fun digitsOnly(value: String): String? {
        val digits = value.filter { it.isDigit() }
        return digits.takeIf { it.isNotEmpty() }
    }

    private fun formatValue(value: Double): String {
        val rounded = value.roundToLong()
        return if (abs(value - rounded) < 0.000001) {
            rounded.toString()
        } else {
            value.toString()
        }
    }

    private data class NumberMatch(
        val start: Int,
        val end: Int,
        val raw: String,
        val value: Double,
        val keywordStart: Int,
        val keywordEnd: Int,
    )
}
