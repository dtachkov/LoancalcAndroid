package com.example.loancalcandroid.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import ru.rustore.sdk.core.util.RuStoreUtils
import ru.rustore.sdk.pay.RuStorePayClient
import ru.rustore.sdk.pay.model.ProductPurchase
import ru.rustore.sdk.pay.model.ProductPurchaseStatus
import ru.rustore.sdk.pay.model.Purchase
import ru.rustore.sdk.pay.model.PurchaseAvailabilityResult
import ru.rustore.sdk.pay.model.RuStorePaymentException
import ru.rustore.sdk.pay.model.SdkTheme
import ru.rustore.sdk.pay.model.SubscriptionPurchase
import ru.rustore.sdk.pay.model.SubscriptionPurchaseStatus
import ru.rustore.sdk.pay.model.UserAuthorizationStatus

object RuStorePayHelper {
    fun isRuStoreInstalled(context: Context): Boolean = RuStoreUtils.isRuStoreInstalled(context)

    fun openRuStore(context: Context) {
        RuStoreUtils.openRuStore(context)
    }

    fun openRuStoreAuthorization(context: Context) {
        RuStoreUtils.openRuStoreAuthorization(context)
    }

    fun openRuStoreDownloadInstruction(context: Context) {
        RuStoreUtils.openRuStoreDownloadInstruction(context)
    }

    fun proceedPaymentIntent(activity: Activity, intent: Intent?) {
        runPayClientAction("proceedPaymentIntent") {
            payClient().getIntentInteractor().proceedIntent(intent, SdkTheme.LIGHT)
        }
    }

    fun checkPurchaseAvailability(
        onAvailable: () -> Unit = {},
        onUnavailable: (String) -> Unit = {},
        onError: (String) -> Unit = {},
    ) {
        runPayClientAction("checkPurchaseAvailability") {
            payClient().getPurchaseInteractor().getPurchaseAvailability()
                .addOnSuccessListener { result ->
                    when (result) {
                        is PurchaseAvailabilityResult.Available -> {
                            BillingLogger.logEvent("OK. checkCanPurchase Purchases are available")
                            onAvailable()
                        }
                        is PurchaseAvailabilityResult.Unavailable -> {
                            val message = result.cause.message.orEmpty()
                            BillingLogger.logEvent("Error. checkCanPurchase Purchases are NOT available: $message")
                            onUnavailable(message)
                        }
                    }
                }
                .addOnFailureListener { throwable ->
                    val message = throwable.localizedMessage.orEmpty()
                    BillingLogger.logEvent("Error. checkCanPurchase When check can purchase $message")
                    onError(message)
                }
        }
    }

    fun checkAuthorizationStatus(
        onAuthorized: () -> Unit = {},
        onUnauthorized: () -> Unit = {},
        onError: (String) -> Unit = {},
    ) {
        runPayClientAction("checkAuthorizationStatus") {
            payClient().getUserInteractor().getUserAuthorizationStatus()
                .addOnSuccessListener { status ->
                    when (status) {
                        UserAuthorizationStatus.AUTHORIZED -> {
                            BillingLogger.logEvent("OK. getUserAuthorizationStatus User is authorized in RuStore")
                            onAuthorized()
                        }
                        UserAuthorizationStatus.UNAUTHORIZED -> {
                            BillingLogger.logEvent("Error. getUserAuthorizationStatus User is NOT authorized in RuStore")
                            onUnauthorized()
                        }
                    }
                }
                .addOnFailureListener { throwable ->
                    val message = throwable.localizedMessage.orEmpty()
                    BillingLogger.logEvent("Error. getUserAuthorizationStatus $message")
                    onError(message)
                }
        }
    }

    fun hasActiveLicensePurchase(purchases: List<Purchase>): Boolean {
        return purchases.any(::isActiveLicensePurchase)
    }

    fun isActiveLicensePurchase(purchase: Purchase): Boolean {
        val productId = purchase.productIdOrNull() ?: return false
        if (productId !in BillingProducts.productIds) {
            return false
        }
        return when (purchase) {
            is ProductPurchase -> purchase.status == ProductPurchaseStatus.CONFIRMED
            is SubscriptionPurchase -> purchase.status == SubscriptionPurchaseStatus.ACTIVE ||
                purchase.status == SubscriptionPurchaseStatus.PAUSED
            else -> false
        }
    }

    fun payClient(): RuStorePayClient = RuStorePayClient.instance

    fun productInteractor() = payClient().getProductInteractor()

    fun purchaseInteractor() = payClient().getPurchaseInteractor()

    inline fun runPayClientAction(
        actionName: String,
        block: () -> Unit,
    ) {
        runCatching(block).onFailure { throwable ->
            if (throwable is RuStorePaymentException.RuStorePayClientNotCreated) {
                BillingLogger.logEvent("Error. $actionName RuStorePayClient is not initialized")
            } else {
                throw throwable
            }
        }
    }

    private fun Purchase.productIdOrNull(): String? {
        return when (this) {
            is ProductPurchase -> productId.value
            is SubscriptionPurchase -> productId.value
            else -> null
        }
    }
}
