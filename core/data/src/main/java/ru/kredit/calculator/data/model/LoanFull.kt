package ru.kredit.calculator.data.model

import com.google.gson.annotations.SerializedName

data class LoanFull(
    @SerializedName("loan")
    val loan: Loan,
    @SerializedName("extras")
    val extras: List<Extra> = emptyList(),
)
