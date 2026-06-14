package com.example.loancalcandroid.review

import android.app.Activity
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.preferences.ApplicationReviewPreferences
import ru.rustore.sdk.review.RuStoreReviewManagerFactory

object ReviewRequester {
    fun onAppStart() {
        val reviewPreferences = LoanCalcData.get().applicationReviewPreferences
        if (!reviewPreferences.isRedirectedForReview()) {
            reviewPreferences.incrementAppLaunchesCount()
        }
    }

    fun maybeRequestReview(activity: Activity) {
        val reviewPreferences = LoanCalcData.get().applicationReviewPreferences
        if (reviewPreferences.dontAskForReview()) return
        if (reviewPreferences.getAppLaunchesCount() < ApplicationReviewPreferences.LAUNCHES_BEFORE_ASK_REVIEW) {
            return
        }

        val manager = RuStoreReviewManagerFactory.create(activity)
        manager.requestReviewFlow()
            .addOnSuccessListener { reviewInfo ->
                manager.launchReviewFlow(reviewInfo)
                    .addOnSuccessListener {
                        reviewPreferences.resetAppLaunchesCount()
                    }
                    .addOnFailureListener {
                        reviewPreferences.resetAppLaunchesCount()
                    }
            }
            .addOnFailureListener {
                // RuStore may silently refuse to show the dialog.
            }
    }
}
