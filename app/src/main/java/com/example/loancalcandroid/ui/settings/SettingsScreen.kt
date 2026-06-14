package com.example.loancalcandroid.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.home.components.MenuNavigationRow
import com.example.loancalcandroid.ui.purchase.PurchaseDialog
import com.example.loancalcandroid.ui.theme.LoanCardSurface
import com.example.loancalcandroid.ui.theme.LoanTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOffersClick: () -> Unit,
    onHelpClick: () -> Unit,
    onVoteClick: () -> Unit,
    onExtraTypesHelpClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showImportUrlDialog by remember { mutableStateOf(false) }
    var showPurchaseDialog by remember { mutableStateOf(false) }
    val premiumFeatureTitle = stringResource(R.string.feature_premium)

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.setNotificationsEnabled(true)
            showNotificationDialog = true
        } else {
            viewModel.setNotificationsEnabled(false)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importFromUri(
                context = context,
                uri = it,
                incorrectFileMessage = context.getString(R.string.incorrect_filename_message),
            )
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri: Uri? ->
        uri?.let {
            viewModel.exportToUri(
                context = context,
                uri = it,
                emptyLoansMessage = context.getString(R.string.please_add_loan_first),
            )
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    if (showImportUrlDialog) {
        ImportFromUrlDialog(
            onDismiss = { showImportUrlDialog = false },
            onLoad = { url ->
                showImportUrlDialog = false
                viewModel.importFromUrl(
                    context = context,
                    url = url,
                    invalidUrlMessage = context.getString(R.string.error_parse_url),
                    notFoundMessage = context.getString(R.string.error_guid_not_found),
                    networkErrorMessage = context.getString(R.string.error_network_import_url),
                )
            },
        )
    }

    if (showNotificationDialog) {
        NotificationSettingsDialog(
            days = uiState.notificationDays,
            hour = uiState.notificationHour,
            minute = uiState.notificationMinute,
            onDismiss = { showNotificationDialog = false },
            onConfirm = { days, hour, minute ->
                viewModel.saveNotificationSettings(days, hour, minute)
                showNotificationDialog = false
            },
        )
    }

    if (showPurchaseDialog) {
        PurchaseDialog(
            featureTitle = premiumFeatureTitle,
            onDismiss = { showPurchaseDialog = false },
        )
    }

    val notificationSummary = stringResource(
        R.string.setting_remind_summary,
        uiState.notificationDays,
        "%02d".format(uiState.notificationHour),
        "%02d".format(uiState.notificationMinute),
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                SettingsCard {
                    SettingsSwitchRow(
                        title = stringResource(R.string.setting_load_last_calculated_loan_at_start),
                        subtitle = stringResource(
                            if (uiState.loadLastLoanAtStart) {
                                R.string.setting_summary_load_last_calculated_loan_on
                            } else {
                                R.string.setting_summary_load_last_calculated_loan_off
                            },
                        ),
                        checked = uiState.loadLastLoanAtStart,
                        onCheckedChange = viewModel::setLoadLastLoanAtStart,
                    )
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_notification_enable),
                        subtitle = if (uiState.notificationsEnabled) {
                            notificationSummary
                        } else {
                            stringResource(R.string.settings_notification_title)
                        },
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled) {
                                viewModel.setNotificationsEnabled(false)
                                return@SettingsSwitchRow
                            }
                            if (!uiState.isLicensed) {
                                showPurchaseDialog = true
                                return@SettingsSwitchRow
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS,
                                ) == PackageManager.PERMISSION_GRANTED
                                if (granted) {
                                    viewModel.setNotificationsEnabled(true)
                                    showNotificationDialog = true
                                } else {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                viewModel.setNotificationsEnabled(true)
                                showNotificationDialog = true
                            }
                        },
                    )
                    if (uiState.notificationsEnabled) {
                        MenuNavigationRow(
                            title = stringResource(R.string.settings_notification_title),
                            subtitle = notificationSummary,
                            onClick = {
                                if (uiState.isLicensed) {
                                    showNotificationDialog = true
                                } else {
                                    showPurchaseDialog = true
                                }
                            },
                            showDivider = false,
                        )
                    } else if (!uiState.isLicensed) {
                        Text(
                            text = stringResource(R.string.widget_label_license_required),
                            style = MaterialTheme.typography.bodySmall,
                            color = LoanTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                }

                SettingsCard(modifier = Modifier.padding(top = 16.dp)) {
                    MenuNavigationRow(
                        title = stringResource(R.string.menu_item_import),
                        subtitle = stringResource(R.string.menu_item_import_hint),
                        onClick = {
                            if (!uiState.isImporting && !uiState.isExporting) {
                                importLauncher.launch(arrayOf("*/*"))
                            }
                        },
                    )
                    MenuNavigationRow(
                        title = stringResource(R.string.menu_item_import_url),
                        subtitle = stringResource(R.string.menu_item_import_url_hint),
                        onClick = {
                            if (!uiState.isImporting && !uiState.isExporting) {
                                showImportUrlDialog = true
                            }
                        },
                    )
                    MenuNavigationRow(
                        title = stringResource(R.string.menu_item_export),
                        subtitle = stringResource(R.string.menu_item_export_hint),
                        onClick = {
                            if (!uiState.isImporting && !uiState.isExporting) {
                                exportLauncher.launch(context.getString(R.string.export_default_filename))
                            }
                        },
                        showDivider = false,
                    )
                }

                SettingsCard(modifier = Modifier.padding(top = 16.dp)) {
                    MenuNavigationRow(
                        title = stringResource(R.string.menu_item_premium),
                        subtitle = if (uiState.isLicensed) {
                            stringResource(R.string.premium_title)
                        } else {
                            stringResource(R.string.feature_request_title)
                        },
                        onClick = { showPurchaseDialog = true },
                    )
                    MenuNavigationRow(
                        title = stringResource(R.string.label_help_app),
                        subtitle = null,
                        onClick = onHelpClick,
                    )
                    MenuNavigationRow(
                        title = stringResource(R.string.label_new_features),
                        subtitle = null,
                        onClick = onVoteClick,
                    )
                    MenuNavigationRow(
                        title = stringResource(R.string.label_extra_types_help),
                        subtitle = stringResource(R.string.label_extra_types_help_hint),
                        onClick = onExtraTypesHelpClick,
                    )
                    MenuNavigationRow(
                        title = stringResource(R.string.offers_screen),
                        subtitle = stringResource(R.string.offers_settings_hint),
                        onClick = onOffersClick,
                        showDivider = false,
                    )
                }
            }

            if (uiState.isImporting || uiState.isExporting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = LoanCardSurface,
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LoanTextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}
