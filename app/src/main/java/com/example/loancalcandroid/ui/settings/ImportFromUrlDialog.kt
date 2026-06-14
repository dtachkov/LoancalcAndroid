package com.example.loancalcandroid.ui.settings

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.example.loancalcandroid.ui.common.LoanOutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.theme.LoanTextSecondary

@Composable
fun ImportFromUrlDialog(
    onDismiss: () -> Unit,
    onLoad: (String) -> Unit,
) {
    val context = LocalContext.current
    var urlText by remember { mutableStateOf("") }
    val urlError = if (urlText.isBlank()) {
        stringResource(R.string.error_required_field)
    } else {
        null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.menu_item_import_url)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LoanOutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text(stringResource(R.string.custom_loan_url)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    isError = urlError != null,
                    supportingText = urlError?.let { { Text(it) } },
                )
                OutlinedButton(
                    onClick = {
                        pasteFromClipboard(context)?.let { pasted ->
                            urlText = pasted
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text(stringResource(R.string.paste_from_buffer))
                }
                Text(
                    text = stringResource(R.string.url_example),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = stringResource(R.string.default_loan_url_example),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoanTextSecondary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (urlText.isNotBlank()) {
                        onLoad(urlText.trim())
                    }
                },
                enabled = urlText.isNotBlank(),
            ) {
                Text(stringResource(R.string.menu_item_load))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

private fun pasteFromClipboard(context: Context): String? {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = clipboard?.primaryClip ?: return null
    if (clip.itemCount == 0) return null
    return clip.getItemAt(0).coerceToText(context)?.toString()?.takeIf { it.isNotBlank() }
}
