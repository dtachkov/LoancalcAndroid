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
import ru.rustore.sdk.pay.RuStorePayClient
import ru.rustore.sdk.pay.model.PreferredPurchaseType
import ru.rustore.sdk.pay.model.Product
import ru.rustore.sdk.pay.model.ProductId
import ru.rustore.sdk.pay.model.ProductPurchaseParams
import ru.rustore.sdk.pay.model.ProductPurchaseResult
import ru.rustore.sdk.pay.model.PurchaseAvailabilityResult
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
        refreshLicenseState()
        loadProducts()
        refreshPurchases()
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
        val productInteractor = RuStorePayClient.instance.getProductInteractor()
        val ids = BillingProducts.productIds.map(::ProductId)
        productInteractor.getProducts(ids)
            .addOnSuccessListener { products ->
                _products.value = products.associateBy { it.productId.value }
            }
    }

    fun refreshPurchases() {
        val purchaseInteractor = RuStorePayClient.instance.getPurchaseInteractor()
        purchaseInteractor.getPurchases(null, null)
            .addOnSuccessListener { purchases ->
                licenseFromNewBilling = purchases.isNotEmpty()
                refreshLicenseState()
            }
    }

    private fun refreshOldPurchases() {
        billingClient.purchases.getPurchases()
            .addOnSuccessListener { purchases ->
                licenseFromOldBilling = purchases.isNotEmpty()
                refreshLicenseState()
            }
    }

    fun checkPurchaseAvailability(
        onUnavailableMessage: (String) -> Unit = {},
    ) {
        RuStorePayClient.instance.getPurchaseInteractor().getPurchaseAvailability()
            .addOnSuccessListener { result ->
                if (result is PurchaseAvailabilityResult.Unavailable) {
                    onUnavailableMessage(result.cause.message.orEmpty())
                }
            }
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
            onError("products_unavailable")
            return
        }

        val params = ProductPurchaseParams(product.productId, null, null, null, null, null)
        RuStorePayClient.instance.getPurchaseInteractor()
            .purchase(params, PreferredPurchaseType.ONE_STEP, SdkTheme.LIGHT, null)
            .addOnSuccessListener { result ->
                handlePurchaseResult(result, product, onSuccess, onError)
            }
            .addOnFailureListener { throwable ->
                when (throwable) {
                    is RuStorePaymentException.ProductPurchaseCancelled -> onCancelled()
                    else -> onError(throwable.localizedMessage.orEmpty())
                }
            }
    }

    private fun handlePurchaseResult(
        result: ProductPurchaseResult,
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        RuStorePayClient.instance.getPurchaseInteractor().getPurchases(null, null)
            .addOnSuccessListener { purchases ->
                if (result.productId.value == product.productId.value && purchases.isNotEmpty()) {
                    licenseFromNewBilling = true
                    refreshLicenseState()
                    onSuccess()
                } else {
                    onError("purchase_not_confirmed")
                }
            }
            .addOnFailureListener { throwable ->
                onError(throwable.localizedMessage.orEmpty())
            }
    }

    fun updateProducts(products: Map<String, Product>) {
        _products.update { products }
    }
}
