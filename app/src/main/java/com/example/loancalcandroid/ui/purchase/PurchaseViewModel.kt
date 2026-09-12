package com.example.loancalcandroid.ui.purchase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.LoanCalcApplication
import com.example.loancalcandroid.billing.LicenseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PurchaseUiState(
    val featureTitle: String = "",
    val isLicensed: Boolean = false,
)

class PurchaseViewModel(
    application: Application,
    featureTitle: String,
) : AndroidViewModel(application) {
    private val licenseManager: LicenseManager =
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
        licenseManager.refreshPurchases()
    }
}
