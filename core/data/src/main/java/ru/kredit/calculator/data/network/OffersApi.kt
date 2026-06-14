package ru.kredit.calculator.data.network

import retrofit2.http.Body
import retrofit2.http.POST
import ru.kredit.calculator.data.model.Offer

interface OffersApi {
    @POST("loans")
    suspend fun getAdvertisedLoans(@Body request: OffersRequest): List<Offer>
}
