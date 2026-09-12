package com.example.loancalcandroid.review

import android.app.Activity
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.google.android.play.core.review.ReviewManagerFactory
import ru.kredit.calculator.data.LoanCalcData

object ReviewRequester {
    private var reviewFlowInProgress = false

    fun requestReviewAfterPositiveAction(activity: Activity) {
        val reviewPreferences = LoanCalcData.get().applicationReviewPreferences
        if (reviewPreferences.dontAskForReview()) return
        if (reviewFlowInProgress) return

        reviewFlowInProgress = true
        AnalyticsHelper.logEvent("REQUEST_REVIEW", "REQUEST")

        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow()
            .addOnCompleteListener { requestTask ->
                if (!requestTask.isSuccessful) {
                    reviewFlowInProgress = false
                    return@addOnCompleteListener
                }
                manager.launchReviewFlow(activity, requestTask.result)
                    .addOnCompleteListener { launchTask ->
                        if (launchTask.isSuccessful) {
                            reviewPreferences.setDontAskForReview()
                        }
                        reviewFlowInProgress = false
                    }
            }
    }
}
