package com.example.loancalcandroid.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.ui.extras.ExtraCategory
import com.example.loancalcandroid.ui.extras.ExtraFormPrefill

@Composable
inline fun <reified VM : ViewModel> loanViewModel(
    loanId: Long,
    crossinline creator: (Application, Long) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, loanId) as T
            }
        },
    )
}

@Composable
inline fun <reified VM : ViewModel> extrasListViewModel(
    loanId: Long,
    category: ExtraCategory,
    crossinline creator: (Application, Long, ExtraCategory) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        key = "$loanId-${category.name}",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, loanId, category) as T
            }
        },
    )
}

@Composable
inline fun <reified VM : ViewModel> extraFormViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    loanId: Long,
    extraId: Long?,
    category: ExtraCategory,
    prefill: ExtraFormPrefill = ExtraFormPrefill(),
    crossinline creator: (Application, androidx.lifecycle.SavedStateHandle, Long?, ExtraCategory, ExtraFormPrefill) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = "${loanId}-${extraId ?: "new"}-${category.name}-${prefill.dateMillis}",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return creator(application, savedStateHandle, extraId, category, prefill) as T
            }
        },
    )
}

@Composable
inline fun <reified VM : ViewModel> offerDetailViewModel(
    offerId: Long,
    crossinline creator: (Application, Long) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        key = "offer-$offerId",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, offerId) as T
            }
        },
    )
}

@Composable
inline fun <reified VM : ViewModel> loanExtraViewModel(
    loanId: Long,
    extraId: Long,
    crossinline creator: (Application, Long, Long) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, loanId, extraId) as T
            }
        },
    )
}

@Composable
inline fun <reified VM : ViewModel> loanEditorViewModel(
    loanId: Long?,
    crossinline creator: (Application, Long?) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, loanId) as T
            }
        },
    )
}

@Composable
inline fun <reified VM : ViewModel> schedulePaymentDetailViewModel(
    loanId: Long,
    listIndex: Int,
    previousPaymentDateMillis: Long,
    crossinline creator: (Application, Long, Int, Long) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        key = "$loanId-$listIndex-$previousPaymentDateMillis",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, loanId, listIndex, previousPaymentDateMillis) as T
            }
        },
    )
}
