package com.example.loancalcandroid.review

import android.app.Activity
import com.example.loancalcandroid.analytics.AnalyticsHelper
import ru.kredit.calculator.data.LoanCalcData
import ru.rustore.sdk.review.RuStoreReviewManagerFactory

object ReviewRequester {
    private var reviewFlowInProgress = false

    fun requestReviewAfterPositiveAction(activity: Activity) {
        val reviewPreferences = LoanCalcData.get().applicationReviewPreferences
        if (reviewPreferences.dontAskForReview()) return
        if (reviewFlowInProgress) return

        reviewFlowInProgress = true
        AnalyticsHelper.logEvent("REQUEST_REVIEW", "REQUEST")

        val manager = RuStoreReviewManagerFactory.create(activity)
        manager.requestReviewFlow()
            .addOnSuccessListener { reviewInfo ->
                manager.launchReviewFlow(reviewInfo)
                    .addOnSuccessListener {
                        reviewPreferences.setDontAskForReview()
                        reviewFlowInProgress = false
                    }
                    .addOnFailureListener {
                        reviewFlowInProgress = false
                    }
            }
            .addOnFailureListener {
                // RuStore may silently refuse to show the dialog.
                reviewFlowInProgress = false
            }
    }
}
