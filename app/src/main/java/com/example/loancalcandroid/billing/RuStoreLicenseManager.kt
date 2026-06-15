package com.example.loancalcandroid.billing

import android.app.Activity
import android.content.Context
import com.example.loancalcandroid.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.kredit.calculator.data.LoanCalcData
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.pay.model.PreferredPurchaseType
import ru.rustore.sdk.pay.model.Product
import ru.rustore.sdk.pay.model.ProductId
import ru.rustore.sdk.pay.model.ProductPurchaseParams
import ru.rustore.sdk.pay.model.ProductPurchaseResult
import ru.rustore.sdk.pay.model.RuStorePaymentException
import ru.rustore.sdk.pay.model.SdkTheme

class RuStoreLicenseManager(
    private val appContext: Context,
) {
    private val chestPreferences = LoanCalcData.get().chestPreferences

    private val _isLicensed = MutableStateFlow(false)
    val isLicensed: StateFlow<Boolean> = _isLicensed.asStateFlow()

    private val _products = MutableStateFlow<Map<String, Product>>(emptyMap())
    val products: StateFlow<Map<String, Product>> = _products.asStateFlow()

    private var licenseFromNewBilling = false
    private var licenseFromOldBilling = false

    private val billingClient = RuStoreBillingClientFactory.create(
        appContext,
        BillingProducts.CONSOLE_APPLICATION_ID,
        BillingProducts.DEEPLINK_SCHEME,
        null,
    )

    init {
        BillingLogger.logEvent("Billing is Initialized")
        BillingLogger.logEvent(
            "RuStore installed: ${RuStorePayHelper.isRuStoreInstalled(appContext)}",
        )
        refreshLicenseState()
        RuStorePayHelper.runPayClientAction("init") {
            loadProducts()
            refreshPurchases()
            RuStorePayHelper.checkAuthorizationStatus()
        }
        refreshOldPurchases()
    }

    fun isAppPurchased(): Boolean {
        return BuildConfig.APP_PURCHASED ||
            chestPreferences.hasLicense() ||
            licenseFromNewBilling ||
            licenseFromOldBilling
    }

    fun refreshLicenseState() {
        _isLicensed.value = isAppPurchased()
    }

    fun loadProducts() {
        val ids = BillingProducts.productIds.map(::ProductId)
        RuStorePayHelper.productInteractor().getProducts(ids)
            .addOnSuccessListener { products ->
                BillingLogger.logEvent("OK. getSKUDetails Products list success found ${products.size}")
                _products.value = products.associateBy { it.productId.value }
            }
            .addOnFailureListener { throwable ->
                BillingLogger.logEvent(
                    "Error. getSKUDetails When getting products list with message: ${throwable.localizedMessage}",
                )
            }
    }

    fun refreshPurchases() {
        RuStorePayHelper.purchaseInteractor().getPurchases(productType = null, purchaseStatus = null)
            .addOnSuccessListener { purchases ->
                licenseFromNewBilling = RuStorePayHelper.hasActiveLicensePurchase(purchases)
                BillingLogger.logEvent(
                    "OK. getPurchasedItems When getting purchased items = ${purchases.size}",
                )
                if (licenseFromNewBilling) {
                    BillingLogger.logEvent("License Bought is true")
                } else {
                    BillingLogger.logEvent("Error. getPurchasedItems Active purchases not found")
                }
                refreshLicenseState()
            }
            .addOnFailureListener { error ->
                BillingLogger.logEvent(
                    "Error. getPurchasedItems When getting purchased items with message ${error.localizedMessage}",
                )
            }
    }

    private fun refreshOldPurchases() {
        billingClient.purchases.getPurchases()
            .addOnSuccessListener { purchases ->
                licenseFromOldBilling = purchases.isNotEmpty()
                BillingLogger.logEvent("Old license status is $licenseFromOldBilling (${purchases.size} items)")
                refreshLicenseState()
            }
            .addOnFailureListener { error ->
                BillingLogger.logEvent(
                    "Error. getOldPurchases When getting purchased items with message ${error.localizedMessage}",
                )
            }
    }

    fun checkPurchaseAvailability(
        onUnavailableMessage: (String) -> Unit = {},
    ) {
        RuStorePayHelper.checkPurchaseAvailability(
            onUnavailable = onUnavailableMessage,
        )
    }

    fun purchase(
        activity: Activity,
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit = {},
    ) {
        val product = _products.value[productId]
        if (product == null) {
            loadProducts()
            BillingLogger.logEvent("Error. launchBillingFlow Product not found: $productId")
            onError("products_unavailable")
            return
        }

        RuStorePayHelper.checkPurchaseAvailability(
            onAvailable = {
                startPurchase(product, onSuccess, onError, onCancelled)
            },
            onUnavailable = { message ->
                onError(message.ifBlank { "products_unavailable" })
            },
            onError = { message ->
                onError(message.ifBlank { "products_unavailable" })
            },
        )
    }

    private fun startPurchase(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit,
    ) {
        val params = ProductPurchaseParams(product.productId, null, null, null, null, null)
        RuStorePayHelper.runPayClientAction("purchase") {
            RuStorePayHelper.purchaseInteractor()
                .purchase(params, PreferredPurchaseType.ONE_STEP, SdkTheme.LIGHT, null)
                .addOnSuccessListener { result ->
                    BillingLogger.logEvent("OK. handlePaymentResult When handle payment result")
                    handlePurchaseResult(result, product, onSuccess, onError)
                }
                .addOnFailureListener { throwable ->
                    when (throwable) {
                        is RuStorePaymentException.ProductPurchaseCancelled -> {
                            BillingLogger.logEvent("OK. launchBillingFlow User canceled purchase")
                            onCancelled()
                        }
                        else -> {
                            val message = throwable.localizedMessage.orEmpty()
                            BillingLogger.logEvent("Error. launchBillingFlow When start purchase $message")
                            onError(message)
                        }
                    }
                }
        }
    }

    private fun handlePurchaseResult(
        result: ProductPurchaseResult,
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        RuStorePayHelper.purchaseInteractor().getPurchases(productType = null, purchaseStatus = null)
            .addOnSuccessListener { purchases ->
                val purchaseConfirmed = result.productId.value == product.productId.value &&
                    RuStorePayHelper.hasActiveLicensePurchase(purchases)
                if (purchaseConfirmed) {
                    licenseFromNewBilling = true
                    refreshLicenseState()
                    refreshOldPurchases()
                    onSuccess()
                } else {
                    BillingLogger.logEvent("Error. handlePaymentResult Purchase not confirmed")
                    onError("purchase_not_confirmed")
                }
            }
            .addOnFailureListener { throwable ->
                val message = throwable.localizedMessage.orEmpty()
                BillingLogger.logEvent("Error. handlePaymentResult $message")
                onError(message)
            }
    }

    fun updateProducts(products: Map<String, Product>) {
        _products.update { products }
    }
}
