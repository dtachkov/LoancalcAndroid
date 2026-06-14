package com.example.loancalcandroid.ui.purchase

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PurchaseDialog(
    featureTitle: String,
    onDismiss: () -> Unit,
    onPurchased: () -> Unit = onDismiss,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            PurchaseScreen(
                featureTitle = featureTitle,
                onBack = onDismiss,
                onPurchased = {
                    onPurchased()
                    onDismiss()
                },
            )
        }
    }
}
