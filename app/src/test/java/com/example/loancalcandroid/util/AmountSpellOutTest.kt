package com.example.loancalcandroid.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class AmountSpellOutTest {
    @Test
    fun formatsRussianThousands() {
        assertEquals("Триста тысяч", AmountSpellOut.format(300_000.0, Locale("ru")))
        assertEquals("Сто тысяч", AmountSpellOut.format(100_000.0, Locale("ru")))
        assertEquals("Одна тысяча", AmountSpellOut.format(1_000.0, Locale("ru")))
        assertEquals("Два миллиона", AmountSpellOut.format(2_000_000.0, Locale("ru")))
    }

    @Test
    fun formatsEnglishThousands() {
        assertEquals("Three hundred thousand", AmountSpellOut.format(300_000.0, Locale.ENGLISH))
        assertEquals("One thousand", AmountSpellOut.format(1_000.0, Locale.ENGLISH))
    }
}
