package com.example.loancalcandroid.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

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
