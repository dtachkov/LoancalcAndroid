package com.example.loancalcandroid.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SpokenLoanParserTest {
    @Test
    fun parsesSpokenExampleWithThousandDots() {
        val parsed = SpokenLoanParser.parse("кредит 20.000 3 года 12 процентов")
        assertEquals("20000", parsed.amount)
        assertEquals("12", parsed.rate)
        assertEquals("36", parsed.termMonths)
    }

    @Test
    fun parsesCommaRateAndMonths() {
        val parsed = SpokenLoanParser.parse("сумма 500000 ставка 11,5 процентов срок 36 месяцев")
        assertEquals("500000", parsed.amount)
        assertEquals("11.5", parsed.rate)
        assertEquals("36", parsed.termMonths)
    }

    @Test
    fun prefersAmountBeforeRubles() {
        val parsed = SpokenLoanParser.parse("12000 рублей на 2 года под 9%")
        assertEquals("12000", parsed.amount)
        assertEquals("9", parsed.rate)
        assertEquals("24", parsed.termMonths)
    }

    @Test
    fun stripsDotsFromRubleAmount() {
        val parsed = SpokenLoanParser.parse("20.000 руб 3 года 12 процентов")
        assertEquals("20000", parsed.amount)
        assertEquals("12", parsed.rate)
        assertEquals("36", parsed.termMonths)
    }

    @Test
    fun doesNotEatAmountWhenRateRepeatsDigits() {
        val parsed = SpokenLoanParser.parse("кредит 12000 3 года 12 процентов")
        assertEquals("12000", parsed.amount)
        assertEquals("12", parsed.rate)
        assertEquals("36", parsed.termMonths)
    }

    @Test
    fun parsesYearAndAHalf() {
        val parsed = SpokenLoanParser.parse("300000 1,5 года 10 процентов")
        assertEquals("300000", parsed.amount)
        assertEquals("10", parsed.rate)
        assertEquals("18", parsed.termMonths)
    }
}
