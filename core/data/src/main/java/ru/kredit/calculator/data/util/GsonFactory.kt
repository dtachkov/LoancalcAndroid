package ru.kredit.calculator.data.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonFactory {
    private const val DATE_FORMAT = "yyyy-MM-dd"

    fun create(): Gson {
        return GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat(DATE_FORMAT)
            .create()
    }
}
