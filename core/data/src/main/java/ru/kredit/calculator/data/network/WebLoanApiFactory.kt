package ru.kredit.calculator.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WebLoanApiFactory {
    private const val BASE_URL = "https://mobile-testing.ru/"

    fun create(): WebLoanApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create(),
                ),
            )
            .build()
            .create(WebLoanApi::class.java)
    }
}
