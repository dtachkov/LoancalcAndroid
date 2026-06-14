package ru.kredit.calculator.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface WebLoanApi {
    @GET("loanapi/v1/loans/{loanId}")
    suspend fun getLoan(@Path("loanId") loanId: String): WebLoanDto
}
