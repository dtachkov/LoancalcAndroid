package ru.kredit.calculator.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kredit.calculator.data.model.Offer

object OffersApiFactory {
    private const val BASE_URL = "https://hcpeople.ru/api/v2/"

    fun create(): OffersApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Offer::class.java, OfferDeserializer())
                        .create(),
                ),
            )
            .build()
            .create(OffersApi::class.java)
    }
}
