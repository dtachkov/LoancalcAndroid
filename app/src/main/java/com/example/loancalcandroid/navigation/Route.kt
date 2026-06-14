package com.example.loancalcandroid.navigation

import com.example.loancalcandroid.ui.extras.ExtraCategory

object Route {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val HELP = "help/{topic}"
    const val ADD_LOAN = "loans/add"
    const val EDIT_LOAN = "loans/{loanId}/edit"
    const val SCHEDULE = "loans/{loanId}/schedule"
    const val SCHEDULE_PAYMENT = "loans/{loanId}/schedule/payment/{listIndex}?prevDateMillis={prevDateMillis}"
    const val REQUISITES = "loans/{loanId}/requisites"
    const val EXTRAS_LIST = "loans/{loanId}/extras"
    const val EXTRA_FORM = "loans/{loanId}/extras/add/{category}"
    const val EDIT_EXTRA = "loans/{loanId}/extras/{extraId}/edit"
    const val FORECAST = "loans/{loanId}/forecast"
    const val BEST_DATE = "loans/{loanId}/best-date"
    const val TAX = "loans/{loanId}/tax"
    const val COMPARE = "loans/{loanId}/compare"
    const val SUM_BY_PAYMENT = "sum-by-payment"
    const val OFFERS = "offers"
    const val OFFER_DETAIL = "offers/{offerId}"
    const val PURCHASE = "purchase/{featureTitle}"

    const val ARG_FEATURE_TITLE = "featureTitle"

    const val ARG_LOAN_ID = "loanId"
    const val ARG_EXTRA_ID = "extraId"
    const val ARG_EXTRA_CATEGORY = "category"
    const val ARG_PREFILL_AMOUNT = "prefillAmount"
    const val ARG_PREFILL_DATE_MILLIS = "prefillDateMillis"
    const val ARG_PREFILL_EXTRA_TYPE = "prefillExtraType"
    const val ARG_OFFER_ID = "offerId"
    const val ARG_LIST_INDEX = "listIndex"
    const val ARG_PREV_DATE_MILLIS = "prevDateMillis"
    const val ARG_HELP_TOPIC = "topic"

    const val HELP_TOPIC_SCHEDULE = "schedule"
    const val HELP_TOPIC_EXTRA_TYPES = "extra_types"
    const val HELP_TOPIC_APP = "app"
    const val HELP_TOPIC_VOTE = "vote"

    const val URL_HELP_APP = "https://mobile-testing.ru/help_kredit_calculator/"
    const val URL_VOTE = "https://mobile-testing.ru/new_features_loancalc/"

    fun editLoan(loanId: Long) = "loans/$loanId/edit"
    fun schedule(loanId: Long) = "loans/$loanId/schedule"
    fun schedulePayment(loanId: Long, listIndex: Int, prevDateMillis: Long = 0L) =
        "loans/$loanId/schedule/payment/$listIndex?prevDateMillis=$prevDateMillis"
    fun helpTopic(topic: String) = "help/$topic"
    fun requisites(loanId: Long) = "loans/$loanId/requisites"
    fun extrasList(loanId: Long) = "loans/$loanId/extras"
    fun extraForm(loanId: Long, category: ExtraCategory = ExtraCategory.EARLY) =
        "loans/$loanId/extras/add/${category.name}"
    fun editExtra(loanId: Long, extraId: Long) = "loans/$loanId/extras/$extraId/edit"
    fun forecast(loanId: Long) = "loans/$loanId/forecast"
    fun bestDate(loanId: Long) = "loans/$loanId/best-date"
    fun tax(loanId: Long) = "loans/$loanId/tax"
    fun compare(loanId: Long) = "loans/$loanId/compare"
    fun offerDetail(offerId: Long) = "offers/$offerId"
    fun purchase(featureTitle: String) = "purchase/${android.net.Uri.encode(featureTitle)}"
}
