package com.example.loancalcandroid.ui.purchase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.LoanCalcApplication
import com.example.loancalcandroid.billing.BillingProducts
import com.example.loancalcandroid.billing.RuStoreLicenseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rustore.sdk.pay.model.Product

data class PurchaseOptionUi(
    val productId: String,
    val title: String,
    val price: String,
)

data class PurchaseUiState(
    val featureTitle: String = "",
    val isLicensed: Boolean = false,
    val options: List<PurchaseOptionUi> = emptyList(),
    val isLoadingProducts: Boolean = true,
    val purchaseInProgress: String? = null,
    val message: String? = null,
)

class PurchaseViewModel(
    application: Application,
    featureTitle: String,
) : AndroidViewModel(application) {
    private val licenseManager: RuStoreLicenseManager =
        (application as LoanCalcApplication).licenseManager

    private val _uiState = MutableStateFlow(
        PurchaseUiState(
            featureTitle = featureTitle,
            isLicensed = licenseManager.isAppPurchased(),
        ),
    )
    val uiState: StateFlow<PurchaseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            licenseManager.isLicensed.collect { licensed ->
                _uiState.update { it.copy(isLicensed = licensed) }
            }
        }
        viewModelScope.launch {
            licenseManager.products.collect { products ->
                _uiState.update {
                    it.copy(
                        isLoadingProducts = products.isEmpty(),
                        options = products.toOptions(),
                    )
                }
            }
        }
        licenseManager.loadProducts()
    }

    fun purchase(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit,
    ) {
        val activity = getApplication<LoanCalcApplication>().currentActivity ?: return
        _uiState.update { it.copy(purchaseInProgress = productId, message = null) }
        licenseManager.purchase(
            activity = activity,
            productId = productId,
            onSuccess = {
                _uiState.update { it.copy(purchaseInProgress = null, isLicensed = true) }
                onSuccess()
            },
            onError = { message ->
                _uiState.update { it.copy(purchaseInProgress = null, message = message) }
                onError(message)
            },
            onCancelled = {
                _uiState.update { it.copy(purchaseInProgress = null) }
                onCancelled()
            },
        )
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun Map<String, Product>.toOptions(): List<PurchaseOptionUi> {
        return listOf(
            BillingProducts.SKU_LICENSE_MONTH,
            BillingProducts.SKU_LICENSE_YEAR,
            BillingProducts.SKU_LICENSE_FOREVER,
        ).mapNotNull { productId ->
            val product = this[productId] ?: return@mapNotNull null
            PurchaseOptionUi(
                productId = productId,
                title = product.title.value,
                price = product.amountLabel.value,
            )
        }
    }
}
