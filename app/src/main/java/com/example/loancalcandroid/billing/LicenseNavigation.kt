package com.example.loancalcandroid.billing

import androidx.annotation.StringRes
import androidx.navigation.NavHostController
import com.example.loancalcandroid.LoanCalcApplication
import com.example.loancalcandroid.R
import com.example.loancalcandroid.navigation.Route
import com.example.loancalcandroid.ui.extras.ExtraCategory

fun NavHostController.navigateWithLicenseCheck(
    @StringRes featureTitleRes: Int,
    destinationRoute: String,
): Boolean {
    val featureTitle = context.getString(featureTitleRes)
    val licenseManager = (context.applicationContext as LoanCalcApplication).licenseManager
    return if (licenseManager.isAppPurchased()) {
        navigate(destinationRoute)
        true
    } else {
        navigate(Route.purchase(featureTitle))
        false
    }
}

fun NavHostController.navigateToExtraFormIfLicensed(
    loanId: Long,
    category: ExtraCategory = ExtraCategory.EARLY,
    prefillAmount: String? = null,
    prefillDateMillis: Long? = null,
    prefillExtraType: String? = null,
) {
    val destinationRoute = Route.extraForm(loanId, category)
    val opened = navigateWithLicenseCheck(
        featureTitleRes = R.string.feature_extra_payments,
        destinationRoute = destinationRoute,
    )
    if (!opened) return
    currentBackStackEntry?.savedStateHandle?.apply {
        prefillAmount?.let { set(Route.ARG_PREFILL_AMOUNT, it) }
        prefillDateMillis?.let { set(Route.ARG_PREFILL_DATE_MILLIS, it) }
        prefillExtraType?.let { set(Route.ARG_PREFILL_EXTRA_TYPE, it) }
    }
}

fun NavHostController.navigateToPurchase(@StringRes featureTitleRes: Int) {
    navigate(Route.purchase(context.getString(featureTitleRes)))
}

fun NavHostController.navigateToAddLoanIfAllowed(currentLoanCount: Int) {
    val licenseManager = (context.applicationContext as LoanCalcApplication).licenseManager
    if (LoanLicensePolicy.canAddLoan(currentLoanCount, licenseManager.isAppPurchased())) {
        navigate(Route.ADD_LOAN)
    } else {
        navigateToPurchase(R.string.paywall_feature_extra_payments)
    }
}
