package com.example.loancalcandroid.ui.offers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.model.Offer

data class OffersUiState(
    val isRefreshing: Boolean = false,
    val refreshError: String? = null,
)

class OffersViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val offerRepository = LoanCalcData.get().offerRepository
    private val settingsPreferences = LoanCalcData.get().settingsPreferences

    val offers: StateFlow<List<Offer>> = offerRepository.observeOffers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(OffersUiState())
    val uiState: StateFlow<OffersUiState> = _uiState.asStateFlow()

    init {
        refreshOffers()
    }

    fun refreshOffers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, refreshError = null) }
            val result = offerRepository.refreshOffers(settingsPreferences.getLanguageCode())
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    refreshError = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
