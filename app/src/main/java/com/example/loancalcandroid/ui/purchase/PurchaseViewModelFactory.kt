package com.example.loancalcandroid.ui.purchase

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PurchaseViewModelFactory(
    private val application: Application,
    private val featureTitle: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseViewModel::class.java)) {
            return PurchaseViewModel(application, featureTitle) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}
