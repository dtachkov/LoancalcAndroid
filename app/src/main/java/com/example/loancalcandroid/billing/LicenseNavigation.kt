package com.example.loancalcandroid.billing

import androidx.annotation.StringRes
import androidx.navigation.NavHostController
import com.example.loancalcandroid.LoanCalcApplication
import com.example.loancalcandroid.navigation.Route

fun NavHostController.navigateWithLicenseCheck(
    @StringRes featureTitleRes: Int,
    destinationRoute: String,
) {
    val featureTitle = context.getString(featureTitleRes)
    val licenseManager = (context.applicationContext as LoanCalcApplication).licenseManager
    if (licenseManager.isAppPurchased()) {
        navigate(destinationRoute)
    } else {
        navigate(Route.purchase(featureTitle))
    }
}

fun NavHostController.navigateToPurchase(@StringRes featureTitleRes: Int) {
    navigate(Route.purchase(context.getString(featureTitleRes)))
}
