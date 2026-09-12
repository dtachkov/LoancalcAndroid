package com.example.loancalcandroid.billing

import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.example.loancalcandroid.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LicenseManager(
    appContext: Context,
) {
    private val _isLicensed = MutableStateFlow(false)
    val isLicensed: StateFlow<Boolean> = _isLicensed.asStateFlow()

    private var hasPk1Purchase = false

    private val billingClient = BillingClient.newBuilder(appContext)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                applyPurchases(purchases.orEmpty())
            } else {
                BillingLogger.logEvent(
                    "Error. purchasesUpdated ${billingResult.responseCode} ${billingResult.debugMessage}",
                )
            }
        }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    init {
        BillingLogger.logEvent("Google Play Billing is Initialized")
        refreshLicenseState()
        startConnection()
    }

    fun isAppPurchased(): Boolean {
        return BuildConfig.APP_PURCHASED || hasPk1Purchase
    }

    fun refreshLicenseState() {
        _isLicensed.value = isAppPurchased()
    }

    fun refreshPurchases() {
        if (!billingClient.isReady) {
            startConnection()
            return
        }
        queryPurchases()
    }

    private fun startConnection() {
        if (billingClient.isReady) {
            queryPurchases()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    BillingLogger.logEvent("OK. Billing setup finished")
                    queryPurchases()
                } else {
                    BillingLogger.logEvent(
                        "Error. Billing setup ${billingResult.responseCode} ${billingResult.debugMessage}",
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                BillingLogger.logEvent("Error. Billing service disconnected")
            }
        })
    }

    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                BillingLogger.logEvent("OK. queryPurchases found ${purchases.size} items")
                applyPurchases(purchases)
            } else {
                BillingLogger.logEvent(
                    "Error. queryPurchases ${billingResult.responseCode} ${billingResult.debugMessage}",
                )
            }
        }
    }

    private fun applyPurchases(purchases: List<Purchase>) {
        val licensePurchases = purchases.filter(::isActivePk1Purchase)
        hasPk1Purchase = licensePurchases.isNotEmpty()
        if (hasPk1Purchase) {
            BillingLogger.logEvent("License Bought is true (pk_1)")
            licensePurchases.forEach(::acknowledgeIfNeeded)
        } else {
            BillingLogger.logEvent("Active pk_1 purchase not found")
        }
        refreshLicenseState()
    }

    private fun isActivePk1Purchase(purchase: Purchase): Boolean {
        return purchase.products.contains(BillingProducts.SKU_LICENSE) &&
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                BillingLogger.logEvent("OK. acknowledgePurchase pk_1")
            } else {
                BillingLogger.logEvent(
                    "Error. acknowledgePurchase ${billingResult.responseCode} ${billingResult.debugMessage}",
                )
            }
        }
    }
}
