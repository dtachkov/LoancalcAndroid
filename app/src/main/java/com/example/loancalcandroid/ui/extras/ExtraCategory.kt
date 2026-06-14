package com.example.loancalcandroid.ui.extras

enum class ExtraCategory {
    EARLY,
    COMMISSION,
    ;

    companion object {
        fun fromRoute(value: String?): ExtraCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: EARLY
        }
    }
}
