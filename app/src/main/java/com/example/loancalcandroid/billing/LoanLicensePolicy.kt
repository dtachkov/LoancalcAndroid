package com.example.loancalcandroid.billing

object LoanLicensePolicy {
    const val FREE_LOAN_LIMIT = 1

    fun canAddLoan(currentLoanCount: Int, isLicensed: Boolean): Boolean {
        return canAddLoans(currentLoanCount, loansToAdd = 1, isLicensed)
    }

    fun canAddLoans(currentLoanCount: Int, loansToAdd: Int, isLicensed: Boolean): Boolean {
        if (isLicensed) return true
        return currentLoanCount + loansToAdd <= FREE_LOAN_LIMIT
    }
}
