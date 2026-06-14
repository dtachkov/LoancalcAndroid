package com.example.loancalcandroid.ui.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.loancalcandroid.LoanCalcApplication
import com.example.loancalcandroid.notification.NotificationScheduler
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.repository.WebLoanImportError
import ru.kredit.calculator.data.repository.WebLoanImportException
import ru.kredit.calculator.data.preferences.SettingsPreferences
import java.io.File

data class SettingsUiState(
    val loadLastLoanAtStart: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val isLicensed: Boolean = false,
    val notificationDays: Int = SettingsPreferences.DEFAULT_DAYS,
    val notificationHour: Int = SettingsPreferences.DEFAULT_HOUR,
    val notificationMinute: Int = SettingsPreferences.DEFAULT_MINUTE,
    val isImporting: Boolean = false,
    val isExporting: Boolean = false,
    val snackbarMessage: String? = null,
)

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val data = LoanCalcData.get()
    private val licenseManager = (application as LoanCalcApplication).licenseManager
    private val settings = data.settingsPreferences
    private val shownNotifications = data.shownNotificationsPreferences
    private val importExportRepository = data.importExportRepository
    private val webLoanImportRepository = data.webLoanImportRepository
    private val loanRepository = data.loanRepository

    private val _uiState = MutableStateFlow(loadState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            licenseManager.isLicensed.collect { licensed ->
                _uiState.update { it.copy(isLicensed = licensed) }
            }
        }
    }

    private fun loadState(): SettingsUiState {
        return SettingsUiState(
            loadLastLoanAtStart = settings.isLoadLastLoanAtStart(),
            notificationsEnabled = settings.areNotificationsEnabled(),
            isLicensed = licenseManager.isAppPurchased(),
            notificationDays = settings.getNotificationDays(),
            notificationHour = settings.getNotificationHour(),
            notificationMinute = settings.getNotificationMinute(),
        )
    }

    fun setLoadLastLoanAtStart(enabled: Boolean) {
        settings.setLoadLastLoanAtStart(enabled)
        _uiState.update { it.copy(loadLastLoanAtStart = enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        settings.setNotificationsEnabled(enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        applyNotificationSchedule(enabled)
    }

    fun saveNotificationSettings(days: Int, hour: Int, minute: Int) {
        settings.setNotificationDays(days)
        settings.setNotificationHour(hour)
        settings.setNotificationMinute(minute)
        _uiState.update {
            it.copy(
                notificationDays = days,
                notificationHour = hour,
                notificationMinute = minute,
            )
        }
        shownNotifications.clear()
        NotificationScheduler.applySettings(getApplication())
    }

    private fun applyNotificationSchedule(enabled: Boolean) {
        shownNotifications.clear()
        if (enabled) {
            NotificationScheduler.applySettings(getApplication())
        } else {
            NotificationScheduler.cancelNotifications(getApplication())
        }
    }

    fun importFromUri(context: Context, uri: Uri, incorrectFileMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, snackbarMessage = null) }
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val fileName = resolveDisplayName(context, uri)
                    if (!fileName.contains(".lcj", ignoreCase = true)) {
                        error(incorrectFileMessage)
                    }
                    val tempFile = File(context.cacheDir, "import_${System.currentTimeMillis()}.lcj")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { output -> input.copyTo(output) }
                    } ?: error("Unable to read file")
                    try {
                        importExportRepository.importFromFile(tempFile)
                    } finally {
                        tempFile.delete()
                    }
                }
            }
            _uiState.update {
                it.copy(
                    isImporting = false,
                    snackbarMessage = result.fold(
                        onSuccess = { count -> formatImportMessage(context, count) },
                        onFailure = { error -> error.message.orEmpty() },
                    ),
                )
            }
        }
    }

    fun exportToUri(context: Context, uri: Uri, emptyLoansMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, snackbarMessage = null) }
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val loanIds = loanRepository.getLoans().map { it.id }
                    if (loanIds.isEmpty()) {
                        error(emptyLoansMessage)
                    }
                    val tempFile = File(context.cacheDir, "export_${System.currentTimeMillis()}.lcj")
                    try {
                        importExportRepository.exportToFile(tempFile, loanIds)
                        context.contentResolver.openOutputStream(uri)?.use { output ->
                            tempFile.inputStream().use { input -> input.copyTo(output) }
                        } ?: error("Unable to write file")
                        loanIds.size
                    } finally {
                        tempFile.delete()
                    }
                }
            }
            _uiState.update {
                it.copy(
                    isExporting = false,
                    snackbarMessage = result.fold(
                        onSuccess = { count -> formatExportMessage(context, count) },
                        onFailure = { error -> error.message.orEmpty() },
                    ),
                )
            }
        }
    }

    fun importFromUrl(
        context: Context,
        url: String,
        invalidUrlMessage: String,
        notFoundMessage: String,
        networkErrorMessage: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, snackbarMessage = null) }
            val result = runCatching {
                webLoanImportRepository.importFromUrl(url)
            }
            _uiState.update {
                it.copy(
                    isImporting = false,
                    snackbarMessage = result.fold(
                        onSuccess = { count -> formatImportMessage(context, count) },
                        onFailure = { error ->
                            when ((error as? WebLoanImportException)?.error) {
                                WebLoanImportError.INVALID_URL -> invalidUrlMessage
                                WebLoanImportError.NOT_FOUND -> notFoundMessage
                                WebLoanImportError.NETWORK -> networkErrorMessage
                                else -> error.message.orEmpty()
                            }
                        },
                    ),
                )
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun resolveDisplayName(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex).orEmpty()
            }
        }
        return uri.lastPathSegment.orEmpty()
    }

    private fun formatImportMessage(context: Context, count: Int): String {
        return context.resources.getQuantityString(
            com.example.loancalcandroid.R.plurals.toast_import_completed,
            count,
            count,
        )
    }

    private fun formatExportMessage(context: Context, count: Int): String {
        return context.resources.getQuantityString(
            com.example.loancalcandroid.R.plurals.toast_export_completed,
            count,
            count,
        )
    }
}
