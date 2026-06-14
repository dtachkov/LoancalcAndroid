package ru.kredit.calculator.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Offer(
    val id: Long = 0,
    val name: String? = null,
    val organizationName: String? = null,
    val rate: Rate? = null,
    val documents: String? = null,
    val requirements: String? = null,
    val extraPaymentRules: String? = null,
    val limit: Double = 0.0,
    val term: Int = 0,
    val link: String? = null,
    val logoImage: String? = null,
    val logoColor: String? = null,
) {
    enum class RateType {
        @SerializedName("fix")
        FIXED,

        @SerializedName("float")
        FLOATING,
    }

    sealed class Rate {
        abstract fun defaultValue(): Double?
    }

    data class FixedRateValue(val value: Double) : Rate() {
        override fun defaultValue(): Double = value
    }

    data class FloatingRate(val values: List<FloatingRateValue>) : Rate() {
        override fun defaultValue(): Double? = values.lastOrNull()?.value

        fun valueForAmount(amount: Double): Double {
            values.forEach { rateValue ->
                if (amount in rateValue.minimumAmount..rateValue.maximumAmount) {
                    return rateValue.value
                }
            }
            return values.firstOrNull()?.value ?: 0.0
        }
    }

    data class FloatingRateValue(
        @SerializedName("val")
        val value: Double,
        @SerializedName("min")
        val minimumAmount: Double,
        @SerializedName("max")
        val maximumAmount: Double,
    )

    companion object {
        private val gson = Gson()

        fun rateTypeOf(rate: Rate?): RateType {
            return if (rate is FloatingRate) RateType.FLOATING else RateType.FIXED
        }

        fun encodeRateType(rateType: RateType): String = gson.toJson(rateType)

        fun decodeRateType(json: String?): RateType {
            return gson.fromJson(json, RateType::class.java)
        }

        fun encodeRate(rate: Rate?): String = gson.toJson(rate)

        fun decodeRate(rateType: RateType, json: String?): Rate? {
            if (json.isNullOrBlank()) return null
            return when (rateType) {
                RateType.FIXED -> gson.fromJson(json, FixedRateValue::class.java)
                RateType.FLOATING -> gson.fromJson(json, FloatingRate::class.java)
            }
        }
    }
}
