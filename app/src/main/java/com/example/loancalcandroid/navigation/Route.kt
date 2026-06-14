package com.example.loancalcandroid.navigation

object Route {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val ALL_LOANS = "loans"
    const val ADD_LOAN = "loans/add"
    const val EDIT_LOAN = "loans/{loanId}/edit"
    const val SCHEDULE = "loans/{loanId}/schedule"
    const val REQUISITES = "loans/{loanId}/requisites"
    const val EXTRAS_LIST = "loans/{loanId}/extras"
    const val EXTRA_FORM = "loans/{loanId}/extras/add"
    const val EDIT_EXTRA = "loans/{loanId}/extras/{extraId}/edit"
    const val FORECAST = "loans/{loanId}/forecast"
    const val BEST_DATE = "loans/{loanId}/best-date"
    const val TAX = "loans/{loanId}/tax"
    const val COMPARE = "loans/{loanId}/compare"
    const val OFFERS = "offers"

    const val ARG_LOAN_ID = "loanId"
    const val ARG_EXTRA_ID = "extraId"

    fun editLoan(loanId: Long) = "loans/$loanId/edit"
    fun schedule(loanId: Long) = "loans/$loanId/schedule"
    fun requisites(loanId: Long) = "loans/$loanId/requisites"
    fun extrasList(loanId: Long) = "loans/$loanId/extras"
    fun extraForm(loanId: Long) = "loans/$loanId/extras/add"
    fun editExtra(loanId: Long, extraId: Long) = "loans/$loanId/extras/$extraId/edit"
    fun forecast(loanId: Long) = "loans/$loanId/forecast"
    fun bestDate(loanId: Long) = "loans/$loanId/best-date"
    fun tax(loanId: Long) = "loans/$loanId/tax"
    fun compare(loanId: Long) = "loans/$loanId/compare"
}
