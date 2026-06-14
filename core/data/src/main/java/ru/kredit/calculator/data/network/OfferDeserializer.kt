package ru.kredit.calculator.data.network

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import ru.kredit.calculator.data.model.Offer
import java.lang.reflect.Type

class OfferDeserializer : JsonDeserializer<Offer> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Offer {
        val objectJson = json.asJsonObject
        val rateType = context.deserialize<Offer.RateType>(objectJson.get("rate_type"), Offer.RateType::class.java)
        val rate = parseRate(objectJson, rateType, context)

        return Offer(
            name = objectJson.get("name")?.asString,
            organizationName = objectJson.get("org_name")?.asString,
            documents = objectJson.get("docs")?.asString,
            requirements = objectJson.get("req")?.asString,
            limit = objectJson.get("limit")?.asDouble ?: 0.0,
            term = objectJson.get("term")?.asInt ?: 0,
            link = objectJson.get("link")?.asString,
            logoColor = objectJson.get("logo_color")?.asString,
            logoImage = objectJson.get("img")?.asString,
            rate = rate,
        )
    }

    private fun parseRate(
        objectJson: JsonObject,
        rateType: Offer.RateType,
        context: JsonDeserializationContext,
    ): Offer.Rate? {
        val rateElement = objectJson.get("rate") ?: return null
        return when (rateType) {
            Offer.RateType.FIXED -> Offer.FixedRateValue(rateElement.asDouble)
            Offer.RateType.FLOATING -> Offer.FloatingRate(parseFloatingRates(rateElement.asJsonArray, context))
        }
    }

    private fun parseFloatingRates(
        rates: JsonArray,
        context: JsonDeserializationContext,
    ): List<Offer.FloatingRateValue> {
        return buildList {
            for (index in 0 until rates.size()) {
                add(
                    context.deserialize(
                        rates.get(index),
                        Offer.FloatingRateValue::class.java,
                    ),
                )
            }
        }
    }
}
