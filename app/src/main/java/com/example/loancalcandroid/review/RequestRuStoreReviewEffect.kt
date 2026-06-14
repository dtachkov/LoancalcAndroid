package com.example.loancalcandroid.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.loancalcandroid.util.findActivity

@Composable
fun RequestRuStoreReviewEffect(trigger: Int) {
    val activity = LocalContext.current.findActivity() ?: return
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            ReviewRequester.requestReviewAfterPositiveAction(activity)
        }
    }
}
