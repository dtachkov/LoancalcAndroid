package ru.kredit.calculator.data.model

import com.google.gson.annotations.SerializedName

data class LoanDetails(
    @SerializedName("bank_name")
    val bankName: String = "",
    @SerializedName("account_number")
    val accountNumber: String = "",
    @SerializedName("uic")
    val uic: String = "",
    @SerializedName("correspondent_account")
    val correspondentAccount: String = "",
    @SerializedName("payment_comment")
    val paymentComment: String? = null,
) {
    fun isEmpty(): Boolean {
        return bankName.isBlank() &&
            accountNumber.isBlank() &&
            uic.isBlank() &&
            correspondentAccount.isBlank() &&
            paymentComment.isNullOrBlank()
    }
}
