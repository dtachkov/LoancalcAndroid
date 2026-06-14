package com.example.loancalcandroid.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.ui.extras.ExtraCategory

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
    loanId: Long,
    extraId: Long?,
    category: ExtraCategory,
    crossinline creator: (Application, Long, Long?, ExtraCategory) -> VM,
): VM {
    val application = LocalContext.current.applicationContext as Application
    return viewModel(
        key = "${loanId}-${extraId ?: "new"}-${category.name}",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator(application, loanId, extraId, category) as T
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
